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
public class Pop extends StackOperation
{

    public void operate( RPNProgram p ) throws RPNScriptException
    {
        RPNArray array = p.popType();

        if ( array != null )
        {
            if( callingName.equals("pop"))
            {
                p.pushObject( array.pop() );
            }
            else if( callingName.equals("popback"))
            {
                p.pushObject( array.popBack() );
            }
        }
        else
        {
            throw new RPNScriptException("pop called on non-array type",
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
            "pop", "popback"
        };
        return names;
    }
}
