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
public class Hyperbolic extends StackOperation
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

            if( callingName.equals("sinh"))
            {
                result = Math.sinh( arg );
            }
            else if( callingName.equals("cosh"))
            {
                result = Math.cosh( arg );
            }
            else if( callingName.equals( "tanh"))
            {
                result = Math.tanh( arg );
            }
            else if( callingName.equals( "asinh"))
            {
                result = Math.log( arg + Math.sqrt( arg * arg + 1));
            }
            else if( callingName.equals("acosh"))
            {
                result = Math.log( arg + Math.sqrt( arg * arg - 1));
            }
            else if( callingName.equals( "atanh"))
            {
                result = .5 * Math.log( (1 + arg)/(1 - arg));
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
            "sinh", "cosh", "tanh", "asinh", "acosh", "atanh"
        };
        return names;
    }
    
    
}
