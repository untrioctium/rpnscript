/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpnscript.operations.io;

import rpnscript.*;

import java.util.Scanner;

/**
 *
 * @author Alex
 */
public class Prompt extends StackOperation
{

    public void operate( RPNProgram p ) throws RPNScriptException
    {
        System.out.print( p.popStackValue().getValue( p ) );
        p.pushObject( in.next() );
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
            "prompt"
        };
        return names;
    }
    private static Scanner in;
}
