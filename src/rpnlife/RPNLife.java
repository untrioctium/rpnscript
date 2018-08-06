/*
 * RPNLife.java
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
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;

import java.util.Stack;
import java.util.HashMap;

import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.EOFException;

import rpnscript.RPNCompiler;
import rpnscript.RPNProgram;
import rpnscript.RPNScriptException;

import rpnscript.valuetypes.RPNArray;

/**
 *
 * @author Alex
 */
public class RPNLife extends javax.swing.JFrame
{

    private final int GRID_CELL_SIZE = 12;
    private String currentScript;
    private JFileChooser scriptChooser;
    private JFileChooser boardChooser;
    private Timer stepTimer;
    private RPNProgram lifeProgram;
    private GridComponent gridDisplay;
    private HashMap<String, Integer> cellTypes;

    /** Creates new form RPNLife */
    public RPNLife()
    {
        initComponents();

        // initialize the timer responsible for updating the game
        this.stepTimer = new Timer( 500,
                new ActionListener()
                {

                    public void actionPerformed( ActionEvent e )
                    {
                        stepGame();
                    }
                } );

        this.stepTimer.setInitialDelay( 0 );

        initGrid();
        initProgram( "rpnlife.rpn" );
        initCellList();

        scriptChooser = new JFileChooser();
        scriptChooser.addChoosableFileFilter(
                new FileNameExtensionFilter( "RPN Scripts", "rpn" ) );

        boardChooser = new JFileChooser();
        boardChooser.addChoosableFileFilter(
                new FileNameExtensionFilter( "RPNLife Boards", "rlb" ) );

        this.pack();
    }

    /**
     * Initializes the grid.
     */
    public void initGrid()
    {
        this.gridDisplay = new GridComponent( 30, 30, GRID_CELL_SIZE );
        this.gridDisplay.addGridEventListener(
                new GridEventListener()
                {

                    public void gridEvent( GridEvent e )
                    {
                        handleGrid( e );
                    }

                    public void actionPerformed( ActionEvent e )
                    {
                    }
                } );


        this.paneLife.setViewportView( this.gridDisplay );
        this.paneLife.setPreferredSize( this.gridDisplay.getPreferredSize() );
    }

    /**
     * Initalizes the simulation program and starts its execution.
     * @param programName The name of the program to start.
     */
    public void initProgram( String programName )
    {
        this.lifeProgram = new RPNProgram();

        currentScript = programName;

        try
        {
            this.lifeProgram.loadStandardFunctions();
            this.lifeProgram.loadClass( ColorGrid.class );
            RPNCompiler.compileFile( programName, lifeProgram );

            this.lifeProgram.pushObject( this.gridDisplay.getGrid() );
            this.lifeProgram.executeFunction( "gameInit" );

            // check to see if the randomize button should be enabled
            Object canRandom = this.lifeProgram.getGlobal( "can_randomize" );

            if ( canRandom instanceof Boolean )
            {
                this.btnRandomize.setEnabled( (Boolean) canRandom );
                this.btnRandomize.setToolTipText(
                        "This script does not support randomization." );
            }

            this.gridDisplay.repaint();

            this.stepTimer.start();
        }
        catch ( Exception e )
        {
            String dialogMessage;

            if ( e instanceof RPNScriptException )
            {
                dialogMessage = "Error on line " +
                        ((RPNScriptException) e).getLineNumber() +
                        ": " + e.getMessage();
            }
            else
            {
                dialogMessage = "System error: " + e.toString();
            }

            this.showError( dialogMessage, true );
        }
    }

    /**
     * Initalizes the list that allows users to select the cell to place.
     */
    private void initCellList()
    {
        this.cellTypes = new HashMap();
        this.lifeProgram.executeFunction( "gameCellTypes" );

        Stack infoStack = new Stack();

        for ( Object o = this.lifeProgram.popType(); o != null;
                o = this.lifeProgram.popType() )
        {
            infoStack.push( o );
        }

        DefaultListModel model = new DefaultListModel();

        while ( !infoStack.empty() )
        {
            if ( !(infoStack.peek() instanceof String) )
            {
                this.showError( "Invalid cell type definition. Expecting name.",
                        true );
            }

            String cellName = (String) infoStack.pop();

            if ( !(infoStack.peek() instanceof Number) )
            {
                this.showError( "Invalid cell type definition. Expecting ID.",
                        true );
            }

            int cellID = ((Number) infoStack.pop()).intValue();

            model.addElement( cellName );
            cellTypes.put( cellName, cellID );

        }

        this.lstCells.setModel( model );
        this.lstCells.setSelectedIndex( 0 );
    }

    /**
     * Show an error.  Will not return if isFatal is true.
     * @param message The message to display.
     * @param isFatal Indicates if the error if fatal. The program will exit on
     * a fatal error.
     */
    private void showError( String message, boolean isFatal )
    {
        JOptionPane.showMessageDialog( this, message, "RPNScript Error",
                JOptionPane.ERROR_MESSAGE );

        if ( isFatal )
        {
            System.exit( 1 );
        }
    }

    /**
     * Called when the grid is clicked or when the mouse changes position.
     * @param e Information about the event.
     */
    private void handleGrid( GridEvent e )
    {
        if ( e.wasGridClicked() )
        {
            this.lifeProgram.pushObject( e.getGridPosition().x );
            this.lifeProgram.pushObject( e.getGridPosition().y );
            this.lifeProgram.pushObject(
                    this.cellTypes.get( this.lstCells.getSelectedValue() ) );

            this.lifeProgram.executeFunction( "gameClicked" );
            this.gridDisplay.repaint();
        }
    }

    /**
     * Called by stepTimer. Updates the current game.
     */
    private void stepGame()
    {
        long stepStart = System.currentTimeMillis();
        this.gridDisplay.getGrid().clearGrid( null );
        this.lifeProgram.executeFunction( "gameStep" );
        this.gridDisplay.repaint();
        long stepTime = System.currentTimeMillis() - stepStart;

        this.lblStatus.setText( "Generation completed in " + stepTime / 1000.0 +
                " s." );
    }

    /**
     * Sets the program's paused state.
     * @param pause Whether the program should be paused or not.
     */
    private void setPaused( boolean pause )
    {
        if ( pause )
        {
            this.stepTimer.stop();
            this.btnPause.setText( "Resume" );
            this.lblStatus.setText( "Paused." );
        }
        else
        {
            this.stepTimer.start();
            this.btnPause.setText( "Pause" );
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        paneLife = new javax.swing.JScrollPane();
        pnlControls = new javax.swing.JPanel();
        btnPause = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnRandomize = new javax.swing.JButton();
        sldSpeed = new javax.swing.JSlider();
        panelCells = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstCells = new javax.swing.JList();
        pnlStatusBar = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        mnuMenu = new javax.swing.JMenuBar();
        mnuFile = new javax.swing.JMenu();
        mnuNew = new javax.swing.JMenuItem();
        mnuSaveBoard = new javax.swing.JMenuItem();
        mnuOpenBoard = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        mnuLoadScript = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        mnuExit = new javax.swing.JMenuItem();
        mnuHelp = new javax.swing.JMenu();
        mnuShowHelp = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        mnuAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        paneLife.setBackground(new java.awt.Color(0, 0, 0));

        pnlControls.setBorder(javax.swing.BorderFactory.createTitledBorder("Control Panel"));

        btnPause.setText("Pause");
        btnPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPauseActionPerformed(evt);
            }
        });

        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        btnRandomize.setText("Randomize");
        btnRandomize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRandomizeActionPerformed(evt);
            }
        });

        sldSpeed.setMaximum(5000);
        sldSpeed.setMinimum(100);
        sldSpeed.setBorder(javax.swing.BorderFactory.createTitledBorder("Game Speed"));
        sldSpeed.setInverted(true);
        sldSpeed.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sldSpeedStateChanged(evt);
            }
        });

        javax.swing.GroupLayout pnlControlsLayout = new javax.swing.GroupLayout(pnlControls);
        pnlControls.setLayout(pnlControlsLayout);
        pnlControlsLayout.setHorizontalGroup(
            pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlControlsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(sldSpeed, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlControlsLayout.createSequentialGroup()
                        .addComponent(btnRandomize, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(6, 6, 6)
                        .addComponent(btnPause, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pnlControlsLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnPause, btnRandomize});

        pnlControlsLayout.setVerticalGroup(
            pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlControlsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnClear)
                    .addComponent(btnPause)
                    .addComponent(btnRandomize))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(sldSpeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelCells.setBorder(javax.swing.BorderFactory.createTitledBorder("Cell Selection"));

        lstCells.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(lstCells);

        javax.swing.GroupLayout panelCellsLayout = new javax.swing.GroupLayout(panelCells);
        panelCells.setLayout(panelCellsLayout);
        panelCellsLayout.setHorizontalGroup(
            panelCellsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
        );
        panelCellsLayout.setVerticalGroup(
            panelCellsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
        );

        pnlStatusBar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pnlStatusBar.setPreferredSize(new java.awt.Dimension(441, 18));

        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);

        javax.swing.GroupLayout pnlStatusBarLayout = new javax.swing.GroupLayout(pnlStatusBar);
        pnlStatusBar.setLayout(pnlStatusBarLayout);
        pnlStatusBarLayout.setHorizontalGroup(
            pnlStatusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
        );
        pnlStatusBarLayout.setVerticalGroup(
            pnlStatusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlStatusBarLayout.createSequentialGroup()
                .addComponent(lblStatus)
                .addContainerGap())
        );

        mnuFile.setText("Game");

        mnuNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.ALT_MASK));
        mnuNew.setText("New Board");
        mnuNew.setToolTipText("Create a new board");
        mnuNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuNewActionPerformed(evt);
            }
        });
        mnuFile.add(mnuNew);

        mnuSaveBoard.setText("Save Board");
        mnuSaveBoard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSaveBoardActionPerformed(evt);
            }
        });
        mnuFile.add(mnuSaveBoard);

        mnuOpenBoard.setText("Load Board");
        mnuOpenBoard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuOpenBoardActionPerformed(evt);
            }
        });
        mnuFile.add(mnuOpenBoard);
        mnuFile.add(jSeparator1);

        mnuLoadScript.setText("Load Script");
        mnuLoadScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLoadScriptActionPerformed(evt);
            }
        });
        mnuFile.add(mnuLoadScript);
        mnuFile.add(jSeparator3);

        mnuExit.setText("Exit");
        mnuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExitActionPerformed(evt);
            }
        });
        mnuFile.add(mnuExit);

        mnuMenu.add(mnuFile);

        mnuHelp.setText("Help");

        mnuShowHelp.setText("Help");
        mnuHelp.add(mnuShowHelp);
        mnuHelp.add(jSeparator2);

        mnuAbout.setText("About");
        mnuAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAboutActionPerformed(evt);
            }
        });
        mnuHelp.add(mnuAbout);

        mnuMenu.add(mnuHelp);

        setJMenuBar(mnuMenu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelCells, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(paneLife, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(pnlStatusBar, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(paneLife, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelCells, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlControls, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlStatusBar, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Exits when the exit menu item is selected.
     * @param evt Event information.
     */
    private void mnuExitActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_mnuExitActionPerformed
    {//GEN-HEADEREND:event_mnuExitActionPerformed
        System.exit( 0 );
    }//GEN-LAST:event_mnuExitActionPerformed

    /**
     * Randomizes the current board.
     * @param evt Event information.
     */
    private void btnRandomizeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnRandomizeActionPerformed
    {//GEN-HEADEREND:event_btnRandomizeActionPerformed
        this.lifeProgram.pushObject( .25 );
        this.lifeProgram.executeFunction( "gameRandom" );
        this.gridDisplay.repaint();
    }//GEN-LAST:event_btnRandomizeActionPerformed

    /**
     * Pauses or unpauses the game, based on the current state.
     * @param evt Event information.
     */
    private void btnPauseActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnPauseActionPerformed
    {//GEN-HEADEREND:event_btnPauseActionPerformed
        this.setPaused( this.stepTimer.isRunning() );
    }//GEN-LAST:event_btnPauseActionPerformed

    /**
     * Clears the current board.
     * @param evt Event information.
     */
    private void btnClearActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnClearActionPerformed
    {//GEN-HEADEREND:event_btnClearActionPerformed
        this.lifeProgram.pushObject( 0 );
        this.lifeProgram.executeFunction( "gameRandom" );
        this.gridDisplay.repaint();
    }//GEN-LAST:event_btnClearActionPerformed

    /**
     * Creates a new board by showing a dimensions selection dialog.
     * @param evt Event information.
     */
    private void mnuNewActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_mnuNewActionPerformed
    {//GEN-HEADEREND:event_mnuNewActionPerformed

        boolean wasPaused;

        if ( this.stepTimer.isRunning() )
        {
            wasPaused = true;
            this.setPaused( true );
        }
        else
        {
            wasPaused = false;
        }

        DimensionsDialog dlgDimensions = new DimensionsDialog( this, true );

        this.repaint();
        dlgDimensions.setVisible( true );

        if ( dlgDimensions.getDimensions() == null && wasPaused )
        {
            this.setPaused( false );
        }
        else
        {
            this.stepTimer.stop();
            this.gridDisplay.resizeGrid( dlgDimensions.getDimensions(),
                    GRID_CELL_SIZE );
            this.repaint();

            this.lifeProgram.pushObject( this.gridDisplay.getGrid() );
            this.lifeProgram.executeFunction( "gameInit" );
        }
    }//GEN-LAST:event_mnuNewActionPerformed

    /**
     * Loads a new game script.
     * @param evt Event information.
     */
    private void mnuLoadScriptActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_mnuLoadScriptActionPerformed
    {//GEN-HEADEREND:event_mnuLoadScriptActionPerformed
        int returnVal = scriptChooser.showOpenDialog( this );

        if ( returnVal != JFileChooser.APPROVE_OPTION )
        {
            return;
        }

        this.initProgram( scriptChooser.getSelectedFile().getPath() );
        this.initCellList();

    }//GEN-LAST:event_mnuLoadScriptActionPerformed

    /**
     * Opens a board from a file.
     * @param evt Event information.
     */
    private void mnuOpenBoardActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_mnuOpenBoardActionPerformed
    {//GEN-HEADEREND:event_mnuOpenBoardActionPerformed
        int returnVal = boardChooser.showOpenDialog( this );

        if ( returnVal != JFileChooser.APPROVE_OPTION )
        {
            return;
        }

        RPNArray values = new RPNArray();

        int newWidth = 0;
        int newHeight = 0;
        int readItems = 0;

        try
        {
            DataInputStream board = new DataInputStream(
                    new BufferedInputStream(
                    new FileInputStream( boardChooser.getSelectedFile() ) ) );

            String scriptName = board.readUTF();

            if ( !scriptName.equals( currentScript ) )
            {
                this.showError( "Selected board requires script '" + scriptName +
                        "'; current script is '" + currentScript + "'.", false );
                return;
            }

            newWidth = board.readInt();
            newHeight = board.readInt();

            for ( readItems = 0;; readItems++ )
            {
                values.pushBack( board.readInt() );
            }

        }
        catch ( EOFException e )
        {
            if ( readItems == 0 )
            {
                this.showError( "Unexpected end of file.", false );
                return;
            }
        }
        catch ( Exception e )
        {
            this.showError( e.getMessage(), false );
        }

        this.gridDisplay.resizeGrid( newWidth, newHeight, GRID_CELL_SIZE );
        this.lifeProgram.pushObject( this.gridDisplay.getGrid() );
        this.lifeProgram.pushObject( values );
        this.lifeProgram.executeFunction( "gameLoad" );
        this.gridDisplay.repaint();

    }//GEN-LAST:event_mnuOpenBoardActionPerformed

    /**
     * Saves the current board to a file.
     * @param evt Event information
     */
    private void mnuSaveBoardActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_mnuSaveBoardActionPerformed
    {//GEN-HEADEREND:event_mnuSaveBoardActionPerformed
        int returnVal = boardChooser.showSaveDialog( this );

        if ( returnVal != JFileChooser.APPROVE_OPTION )
        {
            return;
        }

        try
        {
            DataOutputStream board = new DataOutputStream(
                    new BufferedOutputStream(
                    new FileOutputStream( boardChooser.getSelectedFile() ) ) );

            this.lifeProgram.executeFunction( "gameSave" );

            RPNArray values = this.lifeProgram.popType();

            if ( values == null )
            {
                this.showError( "Error reading the board.", false );
                return;
            }

            board.writeUTF( currentScript );
            board.writeInt( this.gridDisplay.getGrid().getWidth() );
            board.writeInt( this.gridDisplay.getGrid().getHeight() );

            while ( values.size() > 0 )
            {
                board.writeInt( ((Number) values.pop()).intValue() );
            }

            board.close();

        }
        catch ( Exception e )
        {
            this.showError( e.getMessage(), false );
        }


    }//GEN-LAST:event_mnuSaveBoardActionPerformed

    private void mnuAboutActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_mnuAboutActionPerformed
    {//GEN-HEADEREND:event_mnuAboutActionPerformed
        (new AboutDialog( this, true )).setVisible( true );
    }//GEN-LAST:event_mnuAboutActionPerformed

    private void sldSpeedStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_sldSpeedStateChanged
    {//GEN-HEADEREND:event_sldSpeedStateChanged
        this.stepTimer.stop();
        this.stepTimer.setDelay( this.sldSpeed.getValue() );
        this.stepTimer.start();
    }//GEN-LAST:event_sldSpeedStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main( String args[] )
    {
        java.awt.EventQueue.invokeLater( new Runnable()
        {

            public void run()
            {
                new RPNLife().setVisible( true );
            }
        } );
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnPause;
    private javax.swing.JButton btnRandomize;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JList lstCells;
    private javax.swing.JMenuItem mnuAbout;
    private javax.swing.JMenuItem mnuExit;
    private javax.swing.JMenu mnuFile;
    private javax.swing.JMenu mnuHelp;
    private javax.swing.JMenuItem mnuLoadScript;
    private javax.swing.JMenuBar mnuMenu;
    private javax.swing.JMenuItem mnuNew;
    private javax.swing.JMenuItem mnuOpenBoard;
    private javax.swing.JMenuItem mnuSaveBoard;
    private javax.swing.JMenuItem mnuShowHelp;
    private javax.swing.JScrollPane paneLife;
    private javax.swing.JPanel panelCells;
    private javax.swing.JPanel pnlControls;
    private javax.swing.JPanel pnlStatusBar;
    private javax.swing.JSlider sldSpeed;
    // End of variables declaration//GEN-END:variables
}
