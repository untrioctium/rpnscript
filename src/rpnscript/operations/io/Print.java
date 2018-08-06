/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rpnscript.operations.io;
import rpnscript.*;

/**
 *
 * @author Alex
 */
public class Print extends StackOperation {

    public void operate( RPNProgram p ) throws RPNScriptException
    {
        System.out.print( p.popObject() );
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
        String[] names = {"print"};
        return names;
    }

}
