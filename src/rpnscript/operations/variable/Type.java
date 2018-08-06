/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpnscript.operations.variable;

import rpnscript.*;
import rpnscript.referencetypes.VariableReference;

/**
 *
 * @author Alex
 */
public class Type extends StackOperation
{

    public void operate( RPNProgram p ) throws RPNScriptException
    {
        Object val = p.popObject();

        if ( val == null )
        {
            p.pushObject( null );
        }
        else if ( val instanceof Boolean)
        {
            p.pushObject( false);
        }
        else if ( val instanceof Long )
        {
            p.pushObject( (long) 0 );
        }
        else if ( val instanceof Double )
        {
            p.pushObject( (double) 0 );
        }
        else if ( val instanceof Integer )
        {
            p.pushObject( (int) 0 );
        }
        else if ( val instanceof String )
        {
            p.pushObject( new String() );
        }
        else if( p.loadedClasses.containsValue( val.getClass() ))
        {
            try
            {
                p.pushObject( val.getClass().newInstance());
            }
            catch( Exception e )
            {
                throw new RPNScriptException("cannot create new variable of type '" +
                        val.getClass().getName() + "'", p.getCurrentLine());
            }
        }
        else
        {
            p.pushObject( null );
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
            "type"
        };
        return names;
    }
}
