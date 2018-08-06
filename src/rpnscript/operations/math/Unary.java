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
public class Unary extends StackOperation
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

            if( callingName.equals("inv"))
            {
                result = 1.0 / arg;
            }
            else if( callingName.equals("neg"))
            {
                result = -1.0 * arg;
            }
            else if( callingName.equals( "abs"))
            {
                result = Math.abs( arg );
            }
            else if( callingName.equals( "sign"))
            {
                result = Math.signum( result );
            }
            else if( callingName.equals("floor"))
            {
                result = Math.floor(  arg );
            }
            else if( callingName.equals( "ceil"))
            {
                result = Math.ceil( arg );
            }
            else if( callingName.equals( "round"))
            {
                result = Math.round( arg );
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
            "inv", "neg", "abs", "sign", "floor", "ceil", "round"
        };
        return names;
    }
    
    
}
