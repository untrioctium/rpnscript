/*
 * GridListener.java
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

package rpnlife;

import java.awt.event.ActionListener;

/**
 * Interface for classes capable of handling grid events.
 * @author Alex
 */
public interface GridEventListener extends ActionListener {

    /**
     * Handles a grid event. The two types of events are grid clicks and mouse
     * movements.
     * @param e Information about the event.
     */
    void gridEvent( GridEvent e );

}
