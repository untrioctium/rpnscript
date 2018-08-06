/*
 * ColorGrid.java
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

import java.awt.Color;

/**
 * An arbitrarily large grid that holds colors.
 * @author Alex
 */
public class ColorGrid
{

    private Color[][] grid;
    private int width;
    private int height;

    /**
     * Creates a grid with the specified width and height.
     * @param w The desired width
     * @param h The desired height
     */
    public ColorGrid( int w, int h )
    {
        width = w;
        height = h;

        grid = new Color[height][width];
        clearGrid( Color.BLACK );
    }

    /**
     * Set the color of a position on the grid.
     * @param x The x coordindate
     * @param y The y coordinate
     * @param r The red value
     * @param g The green value
     * @param b The blue value
     */
    public void setColor( int x, int y, int r, int g, int b )
    {
        try
        {
            grid[y][x] = new Color( r, g, b );
        }
        catch ( Exception e )
        {
        }
    }

    /**
     * Get the color of a grid position.
     * @param x The x coordinate
     * @param y The y coordinate
     * @return Color containing the color value at the point.
     */
    public Color getColor( int x, int y )
    {
        return grid[y][x];
    }

    /**
     * Get this grid's width.
     * @return The grid width.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Get this grid's height.
     * @return The grid height.
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Clear the entire grid to a color/
     * @param clearCol Requested color to clear to.
     */
    public void clearGrid( Color clearCol )
    {
        for ( int y = 0; y < height; y++ )
        {
            for ( int x = 0; x < width; x++ )
            {
                grid[y][x] = clearCol;
            }
        }
    }
}
