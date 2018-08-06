/*
 * RPNCompiler.java
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

import rpnscript.referencetypes.*;

import rpnscript.operations.ScopeUp;
import rpnscript.operations.ScopeDown;
import rpnscript.operations.ConditionalJump;
import rpnscript.operations.UnconditionalJump;

import java.util.List;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collections;

/**
 * Compiles raw RPNScript code into a RPNProgram.  All methods and members of
 * this class are static, so instantiation of a Compiler class is not needed
 * to compile a program.
 * @author Alex
 */
public class RPNCompiler
{

    /**
     * Compiles a RPNProgram from a file.
     * @param filename The name of the file containing the source.
     * @param p The program object to compile in to.
     * @throws RPNScriptException
     */
    public static void compileFile( String filename, RPNProgram p )
            throws RPNScriptException
    {
        try
        {
            BufferedReader in = new BufferedReader( new FileReader( filename ) );
            String source = new String();
            String line;
            while ( (line = in.readLine()) != null )
            {
                source += line.trim() + "\n";
            }
            in.close();
            compileString( source, p );
            return;
        }
        catch ( RPNScriptException e )
        {
            throw e;
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            throw new RPNScriptException( "cannot read file '" + filename + "'",
                    -1 );
        }
    }

    private static class ConditionalDescriptor
    {

        public int comparisonStart;
        public int jumpNotEqual;
        public List<Integer> jumpEqual;

        public ConditionalDescriptor( int start )
        {
            comparisonStart = start;
            jumpEqual = new ArrayList();
        }
    }

    /**
     * Compile a string of code into a RPNProgram
     * @param code String holding the code to compile.
     * @param p The program to compile in to.
     * @throws rpnscript.RPNScriptException There was an error during compilation.
     */
    public static void compileString( String code, RPNProgram p )
            throws RPNScriptException
    {
        CodeTokenizer ct = new CodeTokenizer( code );

        // indicates if we are presently processing in a function
        boolean inFunction = false;

        // contains information for all currently open conditionals
        ArrayList<ConditionalDescriptor> ifStack = new ArrayList();
        ArrayList<ConditionalDescriptor> whileStack = new ArrayList();

        String token = new String();

        while ( ct.hasToken() )
        {
            token = ct.getToken();
            if ( !inFunction )
            {
                if ( token.equals( "function" ) )
                {
                    inFunction = true;
                    parseFunctionDef( ct, p );
                }
                else if ( token.equals( "globals" ) )
                {
                    parseGlobals( ct, p );
                }
                else
                {
                    // cannot have anything but global blocks or functions in
                    // global scope
                    throw new RPNScriptException( "statement '" + token +
                            "' not allowed outside function or globals block",
                            ct.getCurrentLine() );
                }
            }
            else
            {
                if ( token.equals( "function" ) )
                {
                    // cannot define functions inside eachother... yet ;)
                    throw new RPNScriptException(
                            "function definition inside another",
                            ct.getCurrentLine() );
                }
                else if ( token.equals( "while" ) )
                {
                    // start parsing a new while loop
                    whileStack.add( 0, new ConditionalDescriptor(
                            p.executionStack.size() ) );
                }
                else if ( token.equals( "do" ) && whileStack.size() > 0 )
                {
                    // set up a spot on the stack that will eventually contain
                    // a jumpneq instruction to the end of the loop
                    whileStack.get( 0 ).jumpNotEqual = p.executionStack.size();
                    p.executionStack.add( null );
                    StackOperation jump = new ConditionalJump();
                    jump.callingName = "jumpneq";
                    p.executionStack.add( jump );

                    // indicate that we are entering a new scope
                    p.executionStack.add( new ScopeDown() );

                }
                else if ( token.equals( "loop" ) )
                {
                    // move back up in scope
                    p.executionStack.add( new ScopeUp() );

                    // jump to the conditional of the loop
                    p.executionStack.add( new StackValue(
                            whileStack.get( 0 ).comparisonStart ) );
                    p.executionStack.add( new UnconditionalJump() );

                    // the spot after the unconditional jump is where the loop
                    // will exit, so set the position for the jump
                    p.executionStack.set( whileStack.get( 0 ).jumpNotEqual,
                            new StackValue( p.executionStack.size() ) );

                    // we're done processing this loop
                    whileStack.remove( 0 );

                }
                else if ( token.equals( "if" ) )
                {
                    // start parsing a new if statement
                    ifStack.add( 0, new ConditionalDescriptor(
                            p.executionStack.size() ) );
                }
                else if ( token.equals( "then" ) )
                {

                    ifStack.get( 0 ).jumpNotEqual = p.executionStack.size();
                    p.executionStack.add( null );
                    StackOperation jump = new ConditionalJump();
                    jump.callingName = "jumpneq";
                    p.executionStack.add( jump );
                    p.executionStack.add( new ScopeDown() );
                }
                else if ( token.equals( "elseif" ) )
                {
                    ifStack.get( 0 ).jumpEqual.add( p.executionStack.size() );
                    p.executionStack.add( null );
                    p.executionStack.add( new UnconditionalJump() );

                    p.executionStack.set( ifStack.get( 0 ).jumpNotEqual,
                            new StackValue( p.executionStack.size() ) );

                }
                else if ( token.equals( "else" ) )
                {
                    ifStack.get( 0 ).jumpEqual.add( p.executionStack.size() );

                    p.executionStack.add( null );
                    p.executionStack.add( new UnconditionalJump() );
                    p.executionStack.set( ifStack.get( 0 ).jumpNotEqual,
                            new StackValue( p.executionStack.size() ) );

                    p.executionStack.add( new ScopeDown() );

                    ifStack.get( 0 ).jumpNotEqual = -1;
                }
                else if ( token.equals( "endif" ) )
                {
                    for ( int i : ifStack.get( 0 ).jumpEqual )
                    {
                        p.executionStack.set( i,
                                new StackValue( p.executionStack.size() ) );
                    }

                    p.executionStack.add( new ScopeUp() );

                    if ( ifStack.get( 0 ).jumpNotEqual >= 0 )
                    {
                        p.executionStack.set( ifStack.get( 0 ).jumpNotEqual,
                                new StackValue( p.executionStack.size() ) );
                    }


                    ifStack.remove( 0 );
                }
                else if ( token.equals( "true" ) )
                {
                    p.executionStack.add( new StackValue( true ) );
                }
                else if ( token.equals( "false" ) )
                {
                    p.executionStack.add( new StackValue( false ) );
                }
                else if ( p.externalFunctions.containsKey( token ) )
                {
                    if ( token.equals( "endfunction" ) )
                    {
                        if ( ifStack.size() > 0 || whileStack.size() > 0 )
                        {
                            throw new RPNScriptException(
                                    "unclosed conditional statements (" +
                                    ifStack.size() + " if and " +
                                    whileStack.size() + " while)",
                                    ct.getCurrentLine() );
                        }
                        inFunction = false;
                    }
                    StackOperation op = p.externalFunctions.get( token ).
                            newOperation( token );
                    p.executionStack.add( op );
                    continue;
                }
                else if ( p.loadedClasses.containsKey( token ) )
                {
                    try
                    {
                        p.executionStack.add( new StackValue( p.loadedClasses.get(
                                token ).newInstance() ) );
                    }
                    catch ( Exception e )
                    {
                    }
                }
                else if ( parseString( token, p ) || parseNumber( token, p ) )
                {
                    continue;
                }
                else
                {
                    if ( token.charAt( 0 ) == '.' )
                    {
                        if ( token.length() == 1 )
                        {
                            throw new RPNScriptException(
                                    "empty member reference",
                                    ct.getCurrentLine() );
                        }
                        p.executionStack.add( new MemberReference( token.substring(
                                1 ) ) );
                    }
                    else
                    {
                        p.executionStack.add( new VariableReference( token ) );
                    }
                }

            }
        }
    }

    /**
     * Internal utility function that parses out a RPNScript function
     * definition.
     * @param ct The code tokenizer to retrieve tokens from.
     * @param p The program being compiled.
     * @throws rpnscript.RPNScriptException The function definition was invalid.
     */
    private static void parseFunctionDef( CodeTokenizer ct, RPNProgram p )
            throws RPNScriptException
    {
        String token;
        ArrayList<String> functionDef = new ArrayList();
        // parse out the function definition
        while ( ct.hasToken() )
        {
            token = ct.getToken();
            if ( token.equals( "begin" ) )
            {
                break;
            }

            functionDef.add( token );
        }

        if ( functionDef.size() == 0 )
        {
            throw new RPNScriptException( "unexpected end of file",
                    ct.getCurrentLine() );
        }

        if ( p.nameTaken( functionDef.get( 0 ) ) )
        {
            throw new RPNScriptException( "redefinition of '" + functionDef.get(
                    0 ) + "'",
                    ct.getCurrentLine() );
        }

        if ( parseNumber( functionDef.get( 0 ), p ) || functionDef.get( 0 ).
                split( "\\W", -1 ).length > 1 )
        {
            throw new RPNScriptException( "invalid function name '" + functionDef.get(
                    0 ) + "'",
                    ct.getCurrentLine() );
        }

        RPNProgram.FunctionDefinition def = new RPNProgram.FunctionDefinition();
        def.entryPoint = p.executionStack.size();
        def.argNames.addAll( functionDef );
        Collections.reverse( def.argNames );
        def.argNames.remove( def.argNames.size() - 1 );

        p.userFunctions.put( functionDef.get( 0 ), def );
    }

    /**
     * Internal utility function that parses a RPNScript globals block.
     * @param ct The code tokenizer to retrieve tokens from.
     * @param p The program being compiled.
     * @throws rpnscript.RPNScriptException The globals block was invalid.
     */
    private static void parseGlobals( CodeTokenizer ct, RPNProgram p ) throws
            RPNScriptException
    {
        String token;

        while ( ct.hasToken() )
        {
            token = ct.getToken();

            if ( token.equals( "endglobals" ) )
            {
                break;
            }

            // make sure this global isn't already defined
            if ( p.nameTaken( token ) )
            {
                throw new RPNScriptException( "redefinition of '" + token + "'",
                        ct.getCurrentLine() );
            }

            // make sure it's a valid variable name
            if ( parseNumber( token, p ) || token.split( "\\W", -1 ).length > 1 )
            {
                throw new RPNScriptException(
                        "invalid global name '" + token + "'",
                        ct.getCurrentLine() );
            }

            p.globals.put( token, new Object() );
        }
    }

    /**
     * Internal function that tries to parse out a number.
     * @param token The token to parse.
     * @param p The program being compiled.
     * @return True if a number was parsed and added, false if not.
     */
    private static boolean parseNumber( String token, RPNProgram p )
    {
        // try to parse by a format specifier
        try
        {
            char format = token.charAt( token.length() - 1 );
            String raw = token.substring( 0, token.length() - 1 );

            Object value = null;

            switch ( format )
            {
                case 'b':
                    value = Byte.parseByte( raw );
                    break;

                case 'f':
                    value = Double.parseDouble( raw );
                    break;

            }

            if ( value != null )
            {
                p.executionStack.add( new StackValue( value ) );
                return true;
            }
        }
        catch ( Exception e )
        {
        }

        // try to parse a long
        try
        {
            long value = Long.parseLong( token );
            p.executionStack.add( new StackValue( value ) );
            return true;
        }
        catch ( Exception e )
        {
        }

        // try to parse a double
        try
        {
            double value = Double.parseDouble( token );
            p.executionStack.add( new StackValue( value ) );
            return true;
        }
        catch ( Exception e )
        {
        }

        return false;
    }

    /**
     * Internal function that tries to parse out a string.
     * @param token The token to parse.
     * @param p The program being compiled.
     * @return True if a string was parsed and added, false if not.
     */
    private static boolean parseString( String token, RPNProgram p )
    {
        if ( token.charAt( 0 ) == '"' && token.charAt( token.length() - 1 ) ==
                '"' )
        {
            p.executionStack.add( new StackValue( token.substring( 1,
                    token.length() - 1 ) ) );
            return true;
        }
        return false;
    }
}
