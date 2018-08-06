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
public class Size extends StackOperation
{

    public void operate( RPNProgram p ) throws RPNScriptException
    {
        RPNArray array = p.popType();

        if ( array != null )
        {
            p.pushObject( (long) array.size());
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
            "size"
        };
        return names;
    }
}
