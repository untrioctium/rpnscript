/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rpnscript.operations;
import rpnscript.*;

/**
 * Class that handles an unconditional jump. Unlike conditional jumps,
 * am unconditional jump requires no predicate and will always jump to a
 * specified position.
 * @author Alex
 */
public class UnconditionalJump extends StackOperation {

    public void operate( RPNProgram p ) throws RPNScriptException
    {
        StackValue position = p.popStackValue();

        Object val = position.getValue( p );

        if( val instanceof Number )
        {
            p.executionStackPosition = ((Number)val).intValue();
        }
        else
        {
            throw new RPNScriptException("attempted to jump to a non-numeric position", -1);
        }
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
        String[] names = {"jump"};
        return names;
    }

}
