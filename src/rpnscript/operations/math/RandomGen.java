/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpnscript.operations.math;

import rpnscript.*;
import java.util.Random;

/**
 *
 * @author Alex
 */
public class RandomGen extends StackOperation
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

            if( callingName.equals("srand"))
            {
                rand.setSeed( (long) arg );
                p.executionStackPosition++;
                return;
            }
            else if( callingName.equals("rand"))
            {
                p.pushObject( rand.nextInt( (int) arg ));
                p.executionStackPosition++;
                return;
            }
            else if( callingName.equals( "randf"))
            {
                result = rand.nextDouble() * arg;
            }
            else if( callingName.equals( "randnorm"))
            {
                result = rand.nextGaussian() * arg;
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
            "srand", "rand", "randf", "randnorm"
        };
        return names;
    }

    private static Random rand = new Random();
    
}
