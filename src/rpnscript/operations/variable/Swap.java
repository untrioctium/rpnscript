/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpnscript.operations.variable;

import rpnscript.*;

/**
 *
 * @author Alex
 */
public class Swap extends StackOperation
{

    public void operate( RPNProgram p ) throws RPNScriptException
    {
        StackValue a = p.popStackValue();
        StackValue b = p.popStackValue();

        Object aVal = a.getValue( p );

        a.setValue( p, b.getValue( p ) );
        b.setValue( p, aVal );
        
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
            "swap"
        };
        return names;
    }
}
