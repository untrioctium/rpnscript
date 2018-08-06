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
 * Abstract class that implements shared functionality for all derived external
 * functions.
 * @author Alex
 */
public abstract class StackOperation implements StackObject
{

    @Override
    public abstract void operate( RPNProgram p ) throws RPNScriptException;

    /**
     * Add this operation to a program.
     * @param p The program to add to.
     */
    public void addToProgram( RPNProgram p )
    {
        for ( String name : getNames() )
        {
            p.externalFunctions.put( name, this );
        }
    }

    /**
     * Create a copy of this operation.
     * @param callName The name this operation is being invoked as.
     * @return The copy.
     */
    public StackOperation newOperation( String callName )
    {
        try
        {
            StackOperation copy = this.getClass().newInstance();
            copy.callingName = callName;
            return copy;
        }
        catch( Exception e )
        {
            return null;
        }

    }

    /**
     * Get all aliases that a function goes by.
     * @return
     */
    public abstract String[] getNames();

    /**
     * Get the amount of arguments this function expects to be on the stack.
     * @return The function's argument count.
     */
    public abstract int getArgumentCount();

    /**
     * The name the function is being invoked as.
     */
    public String callingName;
}
