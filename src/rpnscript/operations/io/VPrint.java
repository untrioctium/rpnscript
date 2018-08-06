/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rpnscript.operations.io;
import rpnscript.*;
import java.util.Stack;

/**
 *
 * @author Alex
 */
public class VPrint extends StackOperation {

    public void operate( RPNProgram p ) throws RPNScriptException
    {
        Object count = p.popObject();

        Stack out = new Stack();

        if( count instanceof Number )
        {
            int popCount = ((Number) count).intValue();

            for( int i = 0; i < popCount; i++ )
            {
                out.push( p.popStackValue().getValue( p ) );
            }

            while( !out.empty() )
            {
                System.out.print( out.pop() );
            }
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
        String[] names = {"vprint"};
        return names;
    }

}
