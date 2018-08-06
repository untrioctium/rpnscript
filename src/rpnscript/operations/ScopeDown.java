/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rpnscript.operations;
import rpnscript.*;


/**
 * Class that handles moving down in scope in RPNScript. This happens when
 * entering an if or while block.
 * @author Alex
 */
public class ScopeDown extends StackOperation {

    public void operate( RPNProgram p ) throws RPNScriptException
    {
        p.variableStacks.add( 0, p.variableStacks.get(0) );
        Object curDepth = p.variableStacks.get( 0 ).get( "_SCOPE_DEPTH_");

        if( curDepth instanceof Number )
        {
            p.variableStacks.get(0).put( "_SCOPE_DEPTH_", ((Number)curDepth).intValue() + 1 );
        }


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
