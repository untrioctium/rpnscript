/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpnscript.operations;

import rpnscript.operations.math.*;
import rpnscript.*;
/**
 * Class that handles various timing functions.
 * @author Alex
 */
public class Timing extends StackOperation
{

	public void operate( RPNProgram p ) throws RPNScriptException
    {
        StackValue result = null;
        if( callingName.equals( "time"))
        {
            result = new StackValue((long)(System.currentTimeMillis() * .001));
        }
        else if( callingName.equals( "nano"))
        {
            result = new StackValue(System.nanoTime());
        }

        p.pushStackValue( result );
        // increment the stack position
        p.executionStackPosition++;

    }
	

    /**
     *
     * @return
     */
    public int getArgumentCount()
    {
        return 0;
    }

    /**
     *
     * @return
     */
    public String[] getNames()
    {
        String[] names =
        {
            "time", "nano"
        };
        return names;
    }
    
    
}
