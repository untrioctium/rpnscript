/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpnscript.valuetypes;

import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Alex
 */
public class RPNSet
{

    private List array;

    /**
     *
     */
    public RPNSet()
    {
        array = new ArrayList();
    }

    /**
     *
     * @return
     */
    public Object pop()
    {
        if ( array.isEmpty() )
        {
            return null;
        }

        return array.remove( 0 );
    }

    /**
     *
     * @return
     */
    public Object popBack()
    {
        if ( array.isEmpty() )
        {
            return null;
        }

        return array.remove( array.size() - 1 );
    }

    /**
     *
     * @param o
     */
    public void push( Object o )
    {
        array.add( 0, o );
    }

    /**
     *
     * @param o
     */
    public void pushBack( Object o )
    {
        array.add( o );
    }

    /**
     *
     * @param index
     * @return
     */
    public Object remove( int index )
    {
        if ( index < 0 || index >= array.size() )
        {
            return null;
        }

        return array.remove( index );
    }

    /**
     *
     * @param index
     * @return
     */
    public Object get( int index )
    {
        if ( index < 0 || index >= array.size() )
        {
            return null;
        }

        return array.get( index );
    }

    /**
     *
     * @param index
     * @param o
     */
    public void set( int index, Object o )
    {
        if ( index < 0 || index >= array.size() )
        {
            return;
        }

        array.set( index, o );
    }

    /**
     *
     */
    public void clear()
    {
        array.clear();
    }

    /**
     *
     * @return
     */
    public int size()
    {
        return array.size();
    }
}
