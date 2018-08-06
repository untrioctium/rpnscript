/*
 * AboutDialog.java
 *
 * Copyright (c) 2009 Alex Riley <a.s.riley at gmail.com>. All rights reserved.
 *
 * This file is part of RPNScript.
 *
 * RPNScript is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RPNScript is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RPNScript.  If not, see <http://www.gnu.org/licenses/>.
 */

package rpnscript;

/**
 * Represents a constant value on the stack.
 * @author Alex
 */
public class StackValue implements StackObject
{

    private Object value;

    /**
     * Create an uninitalized value.
     */
    public StackValue()
    {
        value = null;
    }

    /**
     * Create with a specific value.
     * @param o The value to assign.
     */
    public StackValue( Object o )
    {
        value = o;
    }

    /**
     * Push this value onto the value stack.
     * @param p The program to push to.
     * @throws RPNScriptException An error occurred while pushing.
     */
    @Override
    public void operate( RPNProgram p ) throws RPNScriptException
    {
        p.pushStackValue( this );
        p.executionStackPosition++;
    }

    /**
     * Set the value of this constant.
     * @param p The program invoking the change.
     * @param o The new value
     */
    public void setValue( RPNProgram p, Object o )
    {
        value = o;
    }

    /**
     * Get the value of this StackValue.
     * @param p The program querying the value.
     * @return The value of the StackValue.
     */
    public Object getValue( RPNProgram p )
    {
        return value;
    }
}
