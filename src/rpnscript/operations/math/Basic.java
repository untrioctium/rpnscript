/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpnscript.operations.math;

import rpnscript.*;

/**
 * Class that handles basic arithmetic in RPNScript. The following functions
 * are implemented:<ul>
 * <li><b>add</b> - Addition of two numbers.
 * <li><b>sub</b> - Subtraction of two numbers.
 * <li><b>mul</b> - Multiplication of two numbers.
 * <li><b>div</b> - Division of two numbers.
 * <li><b>pow</b> - Exponentation of two numbers.
 * <li><b>root</b> - Roots of a number.
 * </ul>
 * @author Alex
 */
public class Basic extends StackOperation
{

	public void operate( RPNProgram p ) throws RPNScriptException
    {
        // pop two arguments off the active stack
        Object b = p.popObject();
        Object a = p.popObject();
        
        double arg1, arg2;
        double result = 0;
        StackValue pushResult;

        // make sure they are numeric
        if ( a instanceof Number && b instanceof Number )
        {
            arg1 = ((Number) a).doubleValue();
            arg2 = ((Number) b).doubleValue();

            if( callingName.equals("add"))
            {
                result = arg1 + arg2;
            }
            else if( callingName.equals("sub"))
            {
                result = arg1 - arg2;
            }
            else if( callingName.equals( "mul"))
            {
                result = arg1 * arg2;
            }
            else if( callingName.equals( "div"))
            {
                result = arg1 / arg2;
            }
            else if( callingName.equals("pow"))
            {
                result = Math.pow( arg1, arg2);
            }
            else if( callingName.equals( "root"))
            {
                result = Math.pow( arg1, 1.0/arg2);
            }

            pushResult = new StackValue( RPNProgram.implicitConvert( result, (Number)a , (Number)b));
            p.pushStackValue( pushResult );

            // increment the stack position
            p.executionStackPosition++;
        }
        else
        {
            throw new RPNScriptException("cannot perform operation '" + callingName +
                "on values of type '" + a.getClass().getName() + "' and '" +
                b.getClass().getName() + "'", p.getCurrentLine());
        }

    }
	

    /**
     *
     * @return
     */
    public int getArgumentCount()
    {
        return 2;
    }

    /**
     *
     * @return
     */
    public String[] getNames()
    {
        String[] names =
        {
            "add", "sub", "mul", "div", "pow", "root"
        };
        return names;
    }
    
    
}
