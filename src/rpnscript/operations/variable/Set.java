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
public class Set extends StackOperation
{

    public void operate( RPNProgram p ) throws RPNScriptException
    {
        Object value = p.popObject();
        StackValue reference = p.popStackValue();

        reference.setValue( p, value );
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
            "set"
        };
        return names;
    }
}
