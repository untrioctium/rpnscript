/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpnscript.operations.math;

import rpnscript.*;

/**
 *
 * @author Alex
 */
public class Exponential extends StackOperation
{

    public void operate( RPNProgram p ) throws RPNScriptException
    {
        Object a = p.popObject();

        double arg;
        double result = 0;

        // make sure the argument is numeric
        if ( a instanceof Number )
        {
            arg = ((Number) a).doubleValue();

            if ( callingName.equals( "exp" ) )
            {
                result = Math.exp( arg );
            }
            else if ( callingName.equals( "log" ) )
            {
                result = Math.log( arg );
            }
            else if ( callingName.equals( "log10" ) )
            {
                result = Math.log10( arg );
            }

            p.pushObject( result );

            // increment the stack position
            p.executionStackPosition++;
        }
        else
        {
            throw new RPNScriptException( "cannot perform operation '" + callingName +
                    "on value of type '" + a.getClass().getName() + "'", p.getCurrentLine() );
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
            "exp", "log", "log10",
        };
        return names;
    }
}
