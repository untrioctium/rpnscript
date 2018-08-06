/*
 * RPNProgram.java
 *
 * Copyright (c) 2009 Alex Riley <a.s.riley at gmail.com>. All rights reserved.
 *
 * This file is part of RPNScript.
 *
 * RPNScript is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RPNScript is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RPNScript.  If not, see <http://www.gnu.org/licenses/>.
 */

package rpnscript;

import java.util.List;
import java.util.Map;

import java.util.ArrayList;
import java.util.HashMap;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Class that encapsulates all functionality related to executing RPNScript
 * programs.
 * @author Alex
 */
public class RPNProgram
{

    private int errorLine;
    /**
     *
     */
    public String errorMessage;
    /**
     * A stack that contains the instructions for a program. Once created, it is
     * not normally modified by the RPNProgram object, though it is still mutable
     * to allow flexibility in certain cases.
     */
    public List<StackObject> executionStack;
    /**
     * The current position on the execution stack. All operations change this
     * appropriately.  Most functions will just increment it, but jumps will radically
     * change the position.
     */
    public int executionStackPosition;
    /**
     * Holds all of the loaded external functions.
     */
    public Map<String, StackOperation> externalFunctions;
    /**
     * Stores information about user defined functions.
     */
    public Map<String, FunctionDefinition> userFunctions;
    /**
     * The value stack is where the actual results of operations on the execution
     * stack are stored.  Operations are always performed on the topmost stack.
     * There are script functions that allow the manipulation of the stacks in
     * this array directly.
     */
    public List<List<StackValue>> valueStacks;
    /**
     * A stack of variable assignments. When the program enters a new scope, a clone
     * of the current variable stack is pushed. When the scope is exited, only those
     * variables that are defined in the previous stack have their values changed.
     * There are also built in script functions that allow the manual manipulation
     * of this stack.
     */
    public List<Map<String, Object>> variableStacks;
    /**
     * A map of classes that are loaded and allowed to be used within RPNScript
     * programs.
     */
    public Map<String, Class> loadedClasses;
    /**
     * A map of globals for this RPNScript program.  Globals can be accessed in
     * any context, and are not cleared between execution.
     */
    public Map<String, Object> globals;

    /**
     * Initalizes the RPNProgram.
     */
    public RPNProgram()
    {
        executionStack = new ArrayList();
        executionStackPosition = 0;
        userFunctions = new HashMap();
        externalFunctions = new HashMap();
        variableStacks = new ArrayList();
        valueStacks = new ArrayList();
        valueStacks.add( new ArrayList() );
        loadedClasses = new HashMap();
        globals = new HashMap();
    }

    /**
     * Executes the main function in the compiled program.  Cleans the program's
     * enviroment before execution.
     * @return True if the program executed successfully.
     */
    public boolean execute()
    {
        // clear memory and all stacks
        cleanEnvironment();

        // execute the main function
        return executeFunction( "main" );
    }

    /**
     * Calls a function in the compiled program.
     * @param functionName The name of the function to call.
     * @return True if the function executed successfully.
     */
    public boolean executeFunction( String functionName )
    {

        // does this function exist?
        if ( !userFunctions.containsKey( functionName ) )
        {
            errorMessage = "Function '" + functionName + "' not found.";
            errorLine = -1;

            return false;
        }


        executionStackPosition = -2;

        try
        {
            moveToFunction( functionName );

            while ( executionStackPosition >= 0 )
            {
                executionStack.get( (int) executionStackPosition ).operate( this );
            }

        } catch ( RPNScriptException e )
        {
            errorLine = e.getLineNumber();
            errorMessage = e.getMessage();
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Creates a new "thread" of execution for this RPNScript program. The
     * returned program will share globals, user functions, loaded classes,
     * execution instructions, and external functions with its "parent" program,
     * but it will have an independent variable stack, allowing for concurrent
     * execution.
     * @return A new "thread" of this program.
     */
    public RPNProgram createThread()
    {
        RPNProgram thread = new RPNProgram();

        thread.globals = this.globals;
        thread.userFunctions = this.userFunctions;
        thread.loadedClasses = this.loadedClasses;
        thread.executionStack = this.executionStack;
        thread.externalFunctions = this.externalFunctions;

        return thread;
    }

    /**
     * Sets up a function call and moves the execution position appropriately.
     * This function is only used internally and should only be called by
     * advanced users.
     *
     * @param functionName The name of the function to call.
     * @throws rpnscript.RPNScriptException Returned if the function does not exist.
     */
    public void moveToFunction( String functionName ) throws RPNScriptException
    {
        RPNProgram.FunctionDefinition def = userFunctions.get( functionName );

        Map<String, Object> newStack = new HashMap();

        newStack.put( "_RETURN_POS_", executionStackPosition + 1 );
        newStack.put( "_SCOPE_DEPTH_", 0 );

        try
        {
            for ( String arg : def.argNames )
            {
                newStack.put( arg, popObject() );
            }
        } catch ( RPNScriptException e )
        {
            throw new RPNScriptException( "not enough arguments on the stack for function " +
                    functionName, getCurrentLine() );
        }
        variableStacks.add( 0, newStack );
        valueStacks.add( 0, new ArrayList() );
        executionStackPosition = userFunctions.get( functionName ).entryPoint;

    }

    /**
     * Pushes a StackValue onto the current value stack.
     * @param o The StackValue to push.
     */
    public void pushStackValue( StackValue o )
    {
        valueStacks.get( 0 ).add( 0, o );
    }

    /**
     * Pops a StackValue off the current value stack.
     * @return The StackValue popped off.
     * @throws rpnscript.RPNScriptException The stack was empty.
     */
    public StackValue popStackValue() throws RPNScriptException
    {
        if ( valueStacks.get( 0 ).size() == 0 )
        {
            throw new RPNScriptException( "Attempted to pop values off an empty stack.", -1 );
        }

        return valueStacks.get( 0 ).remove( 0 );
    }

    /**
     * Pops an object from the stack.
     * @return The object contained within the top StackValue.
     * @throws rpnscript.RPNScriptException The stack was empty.
     */
    public Object popObject() throws RPNScriptException
    {
        if ( valueStacks.get( 0 ).size() == 0 )
        {
            throw new RPNScriptException( "Attempted to pop values off an empty stack.", -1 );
        }

        return valueStacks.get( 0 ).remove( 0 ).getValue( this );
    }

    /**
     * Push an object onto the current stack. Handles creation of a StackValue wrapper.
     * @param o The object to push.
     */
    public void pushObject( Object o )
    {
        valueStacks.get( 0 ).add( 0, new StackValue( o ) );
    }

    /**
     * Pops a value of a specified type off the stack.
     * @param <T> The type that should be returned.
     * @return The object contained within the top StackValue if a conversion to
     * the type is possible. If not, null is returned.
     */
    public <T> T popType()
    {
        if( this.valueStacks.get(0).size() == 0 )
            return null;

        Object val;

        try
        {
            val = this.popObject();
        }
        catch( Exception e )
        {
            return null;
        }
        
        try
        {

            return (T) val;
        } catch( Exception e )
        {
            this.pushObject( val );
            return null;
        }
    }

    /**
     * Gets the value of a global and casts it to the specified type.
     * @param <T> The type that should be returned.
     * @param globalName The name of the global to retrieve.
     * @return The retrieved global value if the global exists. If not, null is
     * returned.
     */
    public <T> T getGlobal( String globalName )
    {
        if( globals.containsKey( globalName ))
            return (T) globals.get( globalName );

        return null;
    }

    /**
     * Cleans the memory and all stacks.
     */
    public void cleanEnvironment()
    {
        variableStacks.clear();
        valueStacks.clear();
        valueStacks.add( new ArrayList() );
    }

    /**
     * Loads an internal function into the function library.
     * @param functionName The name of the function.
     * @return True if the function was loaded.
     */
    private boolean loadFunction( String functionName )
    {
        try
        {
            StackOperation op = (StackOperation) Class.forName( "rpnscript.operations." + functionName ).newInstance();

            op.addToProgram( this );
        } catch ( Exception e )
        {
            System.out.println( "Error loading '" + functionName + "':" + e.toString() );

            return false;
        }

        return true;
    }

    /**
     * Loads all standard functions from the library.
     */
    public void loadStandardFunctions()
    {
        try
        {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader( RPNProgram.class.getResourceAsStream( "functions.def" ) ) );

            String opName;
            while ( (opName = in.readLine()) != null )
            {
                opName = opName.trim();
                if ( opName.length() > 0 )
                {
                    loadFunction( opName );
                }
            }
        } catch ( Exception e )
        {
            System.out.println( e.toString() );
        }

    }

    /**
     * Load an external function into the library.
     * @param op The function to load.
     */
    public void loadFunction( StackOperation op )
    {
        op.addToProgram( this );
    }

    /**
     * Load a class so that it may be accessed by RPNScript. Loading a class
     * allows instances of that class to be created in RPNScript by calling the
     * name of the class, which calls the default constructor of the class and
     * pushes the resulting object on the stack.
     * @param clazz The class that should be loaded.
     */
    public void loadClass( Class clazz )
    {
        loadedClasses.put( clazz.getName(), clazz );
    }

    /**
     * Maintains the correct implicit conversion rules.
     * @param value The final value to convert.
     * @param a The first operand.
     * @param b The second operand.
     * @return value converted to the correct type.
     */
    public static Object implicitConvert( double value, Number a, Number b )
    {
        if ( (a instanceof Long) && (b instanceof Long) )
        {
            return (long) value;
        }

        return value;
    }

    /**
     * Converts a non-boolean type to a boolean.
     * @param value The value to convert.
     * @return The resulting boolean value.
     */
    public boolean toBoolean( Object value )
    {
        if ( value instanceof Boolean )
        {
            return ((Boolean) value).booleanValue();
        }
        else if ( value instanceof Number )
        {
            return ((Number) value).doubleValue() != 0;
        }
        else if ( value instanceof String )
        {
            return ((String) value).length() > 0;
        }

        return false;
    }

    /**
     * Indicates if a name has been reserved by a program.
     * @param name The name to check.
     * @return True if the name is taken, false if not.
     */
    public boolean nameTaken( String name )
    {
        return externalFunctions.containsKey( name ) ||
                userFunctions.containsKey( name ) ||
                globals.containsKey( name ) ||
                loadedClasses.containsKey( name );
    }

    /**
     * Get the current line of execution.
     * @return The line currently being executed.
     */
    public int getCurrentLine()
    {
        return -1;
    }

    /**
     * Definition of a user defined function.
     */
    public static class FunctionDefinition
    {

        /**
         * The names of the function's arguments.
         */
        public List<String> argNames;
        /**
         * The start of this function on the execution stack.
         */
        public int entryPoint;

        /**
         * Default constructor.
         */
        public FunctionDefinition()
        {
            argNames = new ArrayList();
        }
    }
}