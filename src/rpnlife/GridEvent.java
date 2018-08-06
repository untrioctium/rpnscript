/*
 * GridEvent.java
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

import java.awt.event.ActionEvent;
import java.awt.Point;
import java.awt.Component;

/**
 * A grid event.
 * @author Alex
 */
public class GridEvent extends ActionEvent {

    private boolean wasClicked;
    private Point gridPos;

    /**
     * Create a new grid event.
     * @param source The GridComponent that originated the event.
     * @param id Internal ID
     * @param pos The position of the mouse.
     * @param clicked Indicates if the mouse was clicked on the position.
     */
    public GridEvent( Component source, int id, Point pos, boolean clicked )
    {
        super( source, id, null );
        
        gridPos = pos;
        wasClicked = clicked;
    }
    
    /**
     * Get the grid position hovered by the mouse.
     * @return The position of the mouse in grid coordinates.
     */
    public Point getGridPosition()
    {
        return gridPos;
    }
    
    /**
     * Indicates if the coordinate was clicked when this event was fired.
     * @return If the mouse was clicked on the grid.
     */
    public boolean wasGridClicked()
    {
        return wasClicked;
    }

}
