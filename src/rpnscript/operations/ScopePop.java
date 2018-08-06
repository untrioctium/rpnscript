/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpnscript.operations;

import rpnscript.*;

/**
 * Class that handles popping a scope from the scope stack. This happens when
 * returning from a function.
 * @author Alex
 */
public class ScopePop extends StackOperation
{
    public void operate( RPNProgram p ) throws RPNScriptException
    {
        p.variableStacks.remove( 0 );
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
            "scopepop"
        };
        return names;
    }
}
