/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rpnscript.operations;
import rpnscript.*;

import java.util.HashMap;

/**
 * Class that handles pushing an entirely new variable scope in an RPNScript
 * program. This occurs when entering a new function.
 * @author Alex
 */
public class ScopePush extends StackOperation {

    public void operate( RPNProgram p ) throws RPNScriptException
    {
        p.variableStacks.add( 0, new HashMap() );
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
        String[] names = {"scopepush"};
        return names;
    }

}
