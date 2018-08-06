/*
 * GridComponent.java
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

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import java.awt.AWTEvent;
import java.awt.AWTEventMulticaster;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.Scrollable;
import javax.swing.JComponent;

/**
 * Component that displays a ColorGrid.
 * @author Alex
 */
public class GridComponent extends JComponent implements Scrollable
{

    private ColorGrid grid;
    private int squareSize;
    private Dimension dimensions;
    private Point translation;
    private Point mousePos;
    private GridEventListener gridEventListener;

    /**
     * Add a listener for grid events.
     * @param l
     */
    public synchronized void addGridEventListener( GridEventListener l )
    {
        gridEventListener = (GridEventListener) AWTEventMulticaster.add(
                gridEventListener, l );
    }

    /**
     * Remove a listener for grid events.
     * @param l
     */
    public synchronized void removeGridEventListener( GridEventListener l )
    {
        gridEventListener = (GridEventListener) AWTEventMulticaster.remove(
                gridEventListener, l );
    }

    /**
     * Process events and detect if the grid has been interacted with.
     * @param e The event to process.
     */
    @Override
    public void processEvent( AWTEvent e )
    {
        if ( e instanceof MouseEvent && gridEventListener != null )
        {
            MouseEvent m = (MouseEvent) e;

            if ( m.getID() == MouseEvent.MOUSE_MOVED )
            {
                Point newPos = this.translateToGrid( m.getPoint() );

                if ( newPos != null && !newPos.equals( this.mousePos ) )
                {
                    gridEventListener.gridEvent(
                            new GridEvent( this, m.getID(), newPos, false ) );
                    mousePos = newPos;

                }
            }
            else if ( m.getID() == MouseEvent.MOUSE_CLICKED )
            {
                Point clickedPos = this.translateToGrid( m.getPoint() );

                gridEventListener.gridEvent(
                        new GridEvent( this, m.getID(), clickedPos, true ) );
            }
        }
        else
        {
            super.processEvent( e );
        }
    }

    /**
     * Create a new grid component.
     *
     * The actual width and height of the resulting component can be calculated
     * as:
     *
     * width = (grid width) * (size + 1) + 1
     * height = (grid height) * (size + 1) + 1
     * @param width The x dimensions of the grid
     * @param height The y dimensions of the grid
     * @param size The requested grid cell size.
     */
    public GridComponent( int width, int height, int size )
    {
        this.setBackground( Color.BLACK );
        this.setForeground( Color.WHITE );
        this.resizeGrid( width, height, size );
        this.mousePos = null;
        this.gridEventListener = null;

        this.enableEvents( AWTEvent.MOUSE_EVENT_MASK |
                AWTEvent.MOUSE_MOTION_EVENT_MASK );

    }

    /**
     *
     * @param width
     * @param height
     * @param size
     */
    public void resizeGrid( int width, int height, int size )
    {
        grid = new ColorGrid( width, height );
        grid.clearGrid( null );

        this.squareSize = size;

        dimensions = new Dimension( width * (size + 1) + 1,
                height * (size + 1) + 1 );

        this.setMinimumSize( dimensions );
        this.setPreferredSize( dimensions );
        this.validate();
    }

    /**
     *
     * @param dim
     * @param size
     */
    public void resizeGrid( Dimension dim, int size )
    {
        this.resizeGrid( dim.width, dim.height, size );
    }

    /**
     * Draw this grid.
     * @param g Graphics handle.
     */
    public void paintComponent( Graphics g )
    {
        translation = new Point(
                (this.getParent().getWidth() - this.dimensions.width) / 2,
                (this.getParent().getHeight() - this.dimensions.height) / 2 );

        if ( translation.x < 0 )
        {
            translation.x = 0;
        }
        if ( translation.y < 0 )
        {
            translation.y = 0;
        }

        g.translate( translation.x, translation.y );

        this.setPreferredSize(
                new Dimension( this.dimensions.width + translation.x,
                this.dimensions.height + translation.y ) );
        this.validate();


        g.setColor( this.getForeground() );
        g.fillRect( 0, 0, this.dimensions.width, this.dimensions.height );

        for ( int y = 0; y < grid.getHeight(); y++ )
        {
            for ( int x = 0; x < grid.getWidth(); x++ )
            {
                Color drawColor = grid.getColor( x, y );
                if ( drawColor != null )
                {
                    g.setColor( drawColor );
                }
                else
                {
                    g.setColor( this.getBackground() );
                }
                g.fillRect( 1 + (squareSize + 1) * x,
                        1 + (squareSize + 1) * y, squareSize, squareSize );
            }
        }
    }

    /**
     * Get the ColorGrid associated with this GridComponent
     * @return
     */
    public ColorGrid getGrid()
    {
        return grid;
    }

    private Point translateToGrid( Point pos )
    {
        if ( !this.getBounds().contains( pos ) )
        {
            return null;
        }

        pos = new Point( pos.x - translation.x, pos.y - translation.y );

        int gx = pos.x / (squareSize + 1) - 1;
        int gy = pos.y / (squareSize + 1) - 1;

        if ( pos.x % (squareSize + 1) > 0 )
        {
            gx++;
        }
        if ( pos.y % (squareSize + 1) > 0 )
        {
            gy++;
        }

        if ( gx >= dimensions.width || gy >= dimensions.height )
        {
            return null;
        }
        return new Point( gx, gy );
    }

    /**
     * @return the mousePos
     */
    public Point getMouseGridPosition()
    {
        return mousePos;
    }

    public Dimension getPreferredScrollableViewportSize()
    {
        return dimensions;
    }

    public int getScrollableUnitIncrement( Rectangle visibleRect,
            int orientation, int direction )
    {
        return squareSize;
    }

    public int getScrollableBlockIncrement( Rectangle visibleRect,
            int orientation, int direction )
    {
        return squareSize;
    }

    public boolean getScrollableTracksViewportWidth()
    {
        return false;
    }

    public boolean getScrollableTracksViewportHeight()
    {
        return false;
    }
}
