/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpnscript.referencetypes;

import rpnscript.StackValue;
import rpnscript.RPNProgram;
import rpnscript.RPNScriptException;

import rpnscript.valuetypes.RPNArray;

/**
 * Class derived from StackValue that refers to a variable.
 * @author Alex
 */
public class ArrayReference extends StackValue
{
    /**
     * Holds the name of the variable being referenced.
     */
    private RPNArray refArray;
    private int refIndex;

    /**
     * Create a reference to a variable.
     * @param array
     * @param index
     */
    public ArrayReference( RPNArray array, int index )
    {
        refArray = array;
        refIndex = index;
    }

    /**
     * Checks to see if the variable refers to a user function. If it does, execution
     * is branched to that function, but if it doesn't, a reference to the variable
     * is pushed on the stack.
     * @param p The program invoking the operation.
     * @throws rpnscript.RPNScriptException There was an error accessing the variable.
     */
    @Override
    public void operate( RPNProgram p ) throws RPNScriptException
    {
        super.operate( p );
    }

    @Override
    public void setValue( RPNProgram p, Object o )
    {
        refArray.set( refIndex, o );
    }

    /**
     *
     * @param p
     * @return
     */
    @Override
    public Object getValue( RPNProgram p )
    {
        return refArray.get( refIndex );
    }
}
