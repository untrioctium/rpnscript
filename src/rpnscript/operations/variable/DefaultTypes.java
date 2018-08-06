/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rpnscript.operations.variable;
import rpnscript.*;

import rpnscript.valuetypes.RPNArray;

/**
 *
 * @author Alex
 */
public class DefaultTypes extends StackOperation {

    public void operate( RPNProgram p ) throws RPNScriptException
    {
        if( callingName.equals( "bool"))
        {
            p.pushObject( false );
        }
        else if( callingName.equals("int"))
        {
            p.pushObject((long) 0);
        }
        else if( callingName.equals( "float"))
        {
            p.pushObject((double) 0);
        }
        else if( callingName.equals("array"))
        {
            p.pushObject( new RPNArray() );
        }
        else if( callingName.equals("string"))
        {
            p.pushObject(new String());
        }
        else if( callingName.equals( "null"))
        {
            p.pushObject(null);
        }
        p.executionStackPosition++;

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
        String[] names = {"bool","int", "float", "array", "string", "null"};
        return names;
    }

}
