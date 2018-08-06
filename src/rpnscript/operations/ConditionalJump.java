/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rpnscript.operations;
import rpnscript.*;

/**
 * Class that handles conditional jumps in RPNScript. Conditional jumps are
 * either "jumpeq" (which jumps to a position if its predicate is true) or
 * "jumpneq" (whichs jumps to a position if its predicate is false).
 * @author Alex
 */
public class ConditionalJump extends StackOperation {

    public void operate( RPNProgram p ) throws RPNScriptException
    {
        Object position = p.popObject();

        boolean condition = p.toBoolean( p.popStackValue().getValue( p ) );

        if( position instanceof Number )
        {
            if( callingName.equals( "jumpeq"))
            {
                if( condition )
                    p.executionStackPosition = ((Number)position).intValue();
                else p.executionStackPosition++;
            }
            else if( callingName.equals("jumpneq"))
            {
                if( !condition )
                    p.executionStackPosition = ((Number)position).intValue();
                else p.executionStackPosition++;
            }
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
        return 2;
    }

    /**
     *
     * @return
     */
    public String[] getNames()
    {
        String[] names = {"jumpeq", "jumpneq"};
        return names;
    }

}
