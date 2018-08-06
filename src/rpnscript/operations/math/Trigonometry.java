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
public class Trigonometry extends StackOperation
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

            if ( callingName.equals( "sin" ) )
            {
                result = Math.sin( arg );
            }
            else if ( callingName.equals( "cos" ) )
            {
                result = Math.cos( arg );
            }
            else if ( callingName.equals( "tan" ) )
            {
                result = Math.tan( arg );
            }
            else if ( callingName.equals( "asin" ) )
            {
                result = Math.asin( arg );
            }
            else if ( callingName.equals( "acos" ) )
            {
                result = Math.acos( arg );
            }
            else if ( callingName.equals( "atan" ) )
            {
                result = Math.atan( arg );
            }
            else if ( callingName.equals( "deg2rad" ) )
            {
                result = Math.toRadians( arg );
            }
            else if ( callingName.equals( "rad2deg" ) )
            {
                result = Math.toDegrees( arg );
            }

            p.pushObject( result );

            // increment the stack position
            p.executionStackPosition++;
        }
        else
        {
            throw new RPNScriptException( "cannot perform operation '" +
                    callingName +
                    "on value of type '" + a.getClass().getName() + "'",
                    p.getCurrentLine() );
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
            "sin", "cos", "tan", "asin", "acos", "atan", "rad2deg", "deg2rad"
        };
        return names;
    }
}
