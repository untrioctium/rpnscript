/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpnscript.operations;

import rpnscript.*;

import java.util.ArrayList;

/**
 * Class that handles returning from a function in RPNScript.
 * @author Alex
 */
public class Return extends StackOperation
{

    public void operate( RPNProgram p ) throws RPNScriptException
    {
        if ( !callingName.equals( "endfunction" ) && p.valueStacks.get( 0 ).size() > 0 )
        {
            int returnCount;
            ArrayList returnValues = new ArrayList();

            if ( callingName.equals( "vreturn" ) )
            {
                Number retCount = p.popType();
                if ( retCount == null )
                {
                    throw new RPNScriptException( "attempted to return non-numeric value count",
                            p.getCurrentLine() );
                }
                returnCount = retCount.intValue();
            }
            else
            {
                returnCount = 1;
            }

            for ( int i = 0; i < returnCount; i++ )
            {
                returnValues.add( 0, p.popObject() );
            }

            p.valueStacks.remove( 0 );

            for ( int i = 0; i < returnCount; i++ )
            {
                p.pushObject( returnValues.get( i ) );
            }
        }
        else
        {
            p.valueStacks.remove( 0 );
        }

        Object ret = p.variableStacks.get( 0 ).get( "_RETURN_POS_" );
        Object popCount = p.variableStacks.get( 0 ).get( "_SCOPE_DEPTH_" );

        if ( ret instanceof Number )
        {
            p.executionStackPosition = ((Number) ret).intValue();

            for ( int i = 0; i <= ((Number) popCount).intValue(); i++ )
            {
                p.variableStacks.remove( 0 );
            }


        }
        else
        {
            throw new RPNScriptException( "attempted to jump to a non-numeric position", -1 );
        }
    }

    /**
     *
     * @return
     */
    public int getArgumentCount()
    {
        return 1;
    }

    /**
     *
     * @return
     */
    public String[] getNames()
    {
        String[] names =
        {
            "return", "vreturn", "endfunction"
        };
        return names;
    }
}
