/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpnscript.operations.array;

import rpnscript.*;

import rpnscript.valuetypes.RPNArray;

/**
 * Takes a pointer variable from the top of the stack and pushes back an internal
 * representation.
 * @author Alex
 */
public class Push extends StackOperation
{

    public void operate( RPNProgram p ) throws RPNScriptException
    {
        Object object = p.popObject();
        RPNArray array = p.popType();

        if ( array != null )
        {
            if( callingName.equals("push"))
            {
                array.push( object );
            }
            else if( callingName.equals("pushback"))
            {
                array.pushBack( object );
            }
        }
        else
        {
            throw new RPNScriptException("size called on non-array type",
                    p.getCurrentLine());
        }
        p.executionStackPosition++;
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
            "push", "pushback"
        };
        return names;
    }
}
