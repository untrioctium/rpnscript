/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rpnscript.operations.stack;
import rpnscript.operations.variable.*;
import rpnscript.*;

/**
 * Takes a pointer variable from the top of the stack and pushes back an internal
 * representation.
 * @author Alex
 */
public class StackSize extends StackOperation {

    public void operate( RPNProgram p ) throws RPNScriptException
    {
        p.pushObject( (long) p.valueStacks.get(0).size());
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
        String[] names = {"stacksize"};
        return names;
    }

}
