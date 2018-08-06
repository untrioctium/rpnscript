/*
 * StackObject.java
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
 * Interface for all objects that may be on the execution or value stack.
 * @author Alex
 */
public interface StackObject {

    /**
     * Perform the appropriate action on the program's state.
     * @param p The program invoking the operation.
     * @throws RPNScriptException
     */
    void operate( RPNProgram p ) throws RPNScriptException;

}
