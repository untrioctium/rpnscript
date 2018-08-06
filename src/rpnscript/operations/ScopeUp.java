/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpnscript.operations;

import rpnscript.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that handles going up in variable scope. When going up in scope, any
 * variables not shared between the top scope and the next scope are discarded.
 * @author Alex
 */
public class ScopeUp extends StackOperation
{

    public void operate( RPNProgram p ) throws RPNScriptException
    {
        Map<String, Object> localStack =
                new HashMap( p.variableStacks.get( 0 ) );

        p.variableStacks.remove( 0 );

        // calculate elements not shared by the stacks
        Map<String, Object> notShared =
                new HashMap( localStack );

        notShared.values().removeAll( p.variableStacks.get( 0 ).values() );

        for ( Map.Entry<String, Object> value : notShared.entrySet() )
        {
            localStack.remove( value.getKey() );
        }

        Object curDepth = localStack.get( "_SCOPE_DEPTH_" );

        p.variableStacks.get( 0 ).putAll( localStack );

        if ( curDepth instanceof Number )
        {
            p.variableStacks.get( 0 ).put( "_SCOPE_DEPTH_", ((Number) curDepth).intValue() - 1 );
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
        String[] names =
        {
            "scopepush"
        };
        return names;
    }
}
