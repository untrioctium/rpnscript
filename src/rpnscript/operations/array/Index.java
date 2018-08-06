/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpnscript.operations.array;

import rpnscript.*;

import rpnscript.referencetypes.ArrayReference;
import rpnscript.valuetypes.RPNArray;

/**
 * Takes a pointer variable from the top of the stack and pushes back an internal
 * representation.
 * @author Alex
 */
public class Index extends StackOperation
{
    public void operate( RPNProgram p ) throws RPNScriptException
    {
        Number index = p.popType();
        RPNArray array = p.popType();

        if ( index != null && array != null )
        {
            if( index.intValue() < 0 || index.intValue() >= array.size() )
            {
                throw new RPNScriptException("invalid index: " + index.intValue(),
                        p.getCurrentLine());
            }

            p.pushStackValue( new ArrayReference( array, index.intValue() ));
        }
        else
        {
            throw new RPNScriptException("array indexing error", p.getCurrentLine());
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
            "index"
        };
        return names;
    }
}
