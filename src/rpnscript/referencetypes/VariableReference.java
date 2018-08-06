/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpnscript.referencetypes;

import rpnscript.StackValue;
import rpnscript.RPNProgram;
import rpnscript.RPNScriptException;

/**
 * Class derived from StackValue that refers to a variable.
 * @author Alex
 */
public class VariableReference extends StackValue
{
    /**
     * Holds the name of the variable being referenced.
     */
    private String variableName;

    /**
     * Create a reference to a variable.
     * @param varName The variable name.
     */
    public VariableReference( String varName )
    {
        variableName = varName;
    }

    /**
     * Checks to see if the variable refers to a user function. If it does, execution
     * is branched to that function, but if it doesn't, a reference to the variable
     * is pushed on the stack.
     * @param p The program invoking the operation.
     * @throws rpnscript.RPNScriptException There was an error accessing the variable.
     */
    @Override
    public void operate( RPNProgram p ) throws RPNScriptException
    {
        if( p.loadedClasses.containsKey( variableName ) )
        {
            try
            {
                p.pushObject( p.loadedClasses.get(variableName).newInstance() );
            }
            catch( Exception e )
            {

            }
            p.executionStackPosition++;
        }
        else if ( p.globals.containsKey( variableName ))
        {
            p.pushStackValue( new GlobalReference(variableName));
            p.executionStackPosition++;
        }
        else if ( p.userFunctions.containsKey( variableName ) )
        {
            p.moveToFunction( variableName );
        }
        else
        {
            super.operate( p );
        }
    }

    @Override
    public void setValue( RPNProgram p, Object o )
    {
        p.variableStacks.get( 0 ).put( variableName, o );
    }

    /**
     *
     * @param p
     * @return
     */
    @Override
    public Object getValue( RPNProgram p )
    {
        if ( p.variableStacks.get( 0 ).containsKey( variableName ) )
        {
            return p.variableStacks.get( 0 ).get( variableName );
        }
        else
        {
            return null;
        }
    }

    /**
     *
     * @return
     */
    public String getVariableName()
    {
        return variableName;
    }
}
