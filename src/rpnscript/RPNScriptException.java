/*
 * RPNScriptException.java
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
 * Exception thrown on any errors related to RPNScript
 * @author Alex
 */
public class RPNScriptException extends Exception {

    private int lineNumber;
    
    /**
     * Throw an unknown error.
     */
    public RPNScriptException()
    {
        super("Unknown exception.");
        lineNumber = -1;
    }
    
    /**
     * Throw a specific error regarding RPNScript.
     * @param message Error message
     * @param line Line the error ocurred on.
     */
    public RPNScriptException( String message, int line )
    {
        super(message);
        lineNumber = line;
    }
    
    /**
     * Get the error's line number.
     * @return The line number.
     */
    public int getLineNumber()
    {
        return lineNumber;
    }

}
