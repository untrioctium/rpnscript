/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpnscript.operations;

import rpnscript.*;

/**
 * Class that handles boolean operations in RPNScript.
 * @author Alex
 */
public class BooleanOps extends StackOperation
{

    @Override
	public void operate( RPNProgram p ) throws RPNScriptException
    {
        Object b = p.popObject();
        Object a = null;

        if( !callingName.equals( "not" ) && !callingName.equals( "!"))
        {
            a = p.popObject();
        }
        
        boolean arg1 = p.toBoolean( a );
        boolean arg2 = p.toBoolean( b );
        boolean result = false;

        if( callingName.equals( "not") || callingName.equals("!"))
        {
            result = !arg2;
        }
        else if( callingName.equals("and") || callingName.equals( "&&"))
        {
            result = arg1 && arg2;
        }
        else if( callingName.equals("or") || callingName.equals( "||"))
        {
            result = arg1 || arg2;
        }
        else if( callingName.equals("nor"))
        {
            result = !arg1 && !arg2;
        }

        p.pushStackValue(  new StackValue( result ));
        p.executionStackPosition++;
    }
	
    public int getArgumentCount()
    {
        return 2;
    }

    @Override
    public String[] getNames()
    {
        String[] names =
        {
            "and", "&&", "or", "||", "not", "!", "nor"
        };
        return names;
    }
    
    
}
