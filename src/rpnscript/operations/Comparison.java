/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpnscript.operations;

import rpnscript.*;

/**
 * Class that handles comparison operations in RPNScript.
 * @author Alex
 */
public class Comparison extends StackOperation
{

	public void operate( RPNProgram p ) throws RPNScriptException
    {
        // pop two arguments off the active stack
        Object b = p.popObject();
        Object a = p.popObject();

        boolean result = false;

        if( a instanceof Number && !(b instanceof Number))
        {
            throw new RPNScriptException("cannot compare numerical types to non-numerical types", p.getCurrentLine());
        }

        if ( a instanceof Number && b instanceof Number )
        {
            double arg1 = ((Number) a).doubleValue();
            double arg2 = ((Number) b).doubleValue();

            if( callingName.equals("eq") || callingName.equals("==") )
            {
                result = arg1 == arg2;
            }
            else if( callingName.equals("is") || callingName.equals("==="))
            {
                result = arg1 == arg2 && a.getClass() == b.getClass();
            }
            else if( callingName.equals("neq") || callingName.equals("!=") )
            {
                result = arg1 != arg2;
            }
            else if( callingName.equals( "great") || callingName.equals(">") )
            {
                result = arg1 > arg2;
            }
            else if( callingName.equals( "less") || callingName.equals("<") )
            {
                result = arg1 < arg2;
            }
            else if( callingName.equals( "greateq") || callingName.equals(">=") )
            {
                result = arg1 >= arg2;
            }
            else if( callingName.equals( "lesseq") || callingName.equals("<=") )
            {
                result = arg1 <= arg2;
            }
        }
        else if( a instanceof String && b instanceof String )
        {
            String arg1 = ((String )a);
            String arg2 = ((String)b);

            if( callingName.equals("eq") || callingName.equals("==") || 
                callingName.equals("is") || callingName.equals("===") )
            {
                result = arg1.equals( arg2 );
            }
            else if( callingName.equals("neq") || callingName.equals("!=") )
            {
                result = !arg1.equals( arg2 );
            }
            else if( callingName.equals( "great") || callingName.equals(">") )
            {
                result = arg1.compareTo( arg2 ) > 0;
            }
            else if( callingName.equals( "less") || callingName.equals("<") )
            {
                result = arg1.compareTo( arg2 ) < 0;
            }
            else if( callingName.equals( "great") || callingName.equals(">") )
            {
                result = arg1.compareTo( arg2 ) >= 0;
            }
            else if( callingName.equals( "less") || callingName.equals("<") )
            {
                result = arg1.compareTo( arg2 ) <= 0;
            }
        }

        p.pushObject( result );

            // increment the stack position
            p.executionStackPosition++;

    }
	
    @Override
    public int getArgumentCount()
    {
        return 2;
    }

    @Override
    public String[] getNames()
    {
        String[] names =
        {
            "eq", "==", "is", "===", "neq", "!=", "greater", ">", "less", "<",
            "greateq", ">=", "lesseq", "<="
        };
        return names;
    }
    
    
}
