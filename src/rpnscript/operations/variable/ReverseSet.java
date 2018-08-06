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
public class ReverseSet extends StackOperation
{

    public void operate( RPNProgram p ) throws RPNScriptException
    {
        StackValue reference = p.popStackValue();
        StackValue value = p.popStackValue();

        reference.setValue( p, value.getValue( p ) );
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
            "rset"
        };
        return names;
    }
}
