/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpnscript.referencetypes;

import rpnscript.StackValue;
import rpnscript.RPNProgram;
import rpnscript.RPNScriptException;

/**
 * Class derived from StackValue that refers to a global.
 * @author Alex
 */
public class GlobalReference extends StackValue
{
    /**
     * Holds the name of the global being referenced.
     */
    private String globalName;

    /**
     * Create a reference to a global.
     * @param gName
     */
    public GlobalReference( String gName )
    {
        globalName = gName;
    }

    @Override
    public void setValue( RPNProgram p, Object o )
    {
        p.globals.put( globalName, o );
    }

    /**
     *
     * @param p
     * @return
     */
    @Override
    public Object getValue( RPNProgram p )
    {
        return p.globals.get( globalName );
    }

    /**
     *
     * @return
     */
    public String getGlobalName()
    {
        return globalName;
    }
}
