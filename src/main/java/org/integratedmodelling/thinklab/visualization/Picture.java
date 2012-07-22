/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.thinklab.visualization;

/*************************************************************************
*  Compilation:  javac Picture.java
*  Execution:    java Picture filename
*
*  Data type for manipulating individual pixels of an image. The original
*  image can be read from a file in JPEG, GIF, or PNG format, or the
*  user can create a blank image of a given size. Includes methods for
*  displaying the image in a window on the screen or saving to a file.
*
*  % java Picture image.jpg
*
*  Remarks
*  -------
*   - pixel (0, 0) is upper left hand corner
*
*   - see also GrayPicture.java for a grayscale version
*
*************************************************************************/

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public final class Picture implements ActionListener {
   private BufferedImage image;    // the rasterized image
   private JFrame frame;           // on-screen view

   // create a blank w-by-h image
   public Picture(int w, int h) {
       image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
       // set to TYPE_INT_ARGB to support transparency
   }

   // create from a preexisting image
   public Picture(BufferedImage image) {
       this.image = image;
   }

   
   // create an image by reading in the PNG, GIF, or JPEG from a filename
   public Picture(String filename) {

       try {
           // try to read from file in working directory
           File file = new File(filename);
           if (file.isFile()) {
               image = ImageIO.read(file);
           }

           // now try to read from file in same directory as this .class file
           else {
               URL url = getClass().getResource(filename);
               if (url == null) { url = new URL(filename); }
               image = ImageIO.read(url);
           }
       }
       catch (IOException e) {
           // e.printStackTrace();
           throw new RuntimeException("Could not open file: " + filename);
       }

       // check that image was read in
       if (image == null) {
           throw new RuntimeException("Invalid image file: " + filename);
       }
   }

   // create an image by reading in the PNG, GIF, or JPEG from a file
   public Picture(File file) {
       try { image = ImageIO.read(file); }
       catch (IOException e) {
           e.printStackTrace();
           throw new RuntimeException("Could not open file: " + file);
       }
       if (image == null) {
           throw new RuntimeException("Invalid image file: " + file);
       }
   }

   // to embed in a JPanel, JFrame or other GUI widget
   public JLabel getJLabel() {
       if (image == null) { return null; }         // no image available
       ImageIcon icon = new ImageIcon(image);
       return new JLabel(icon);
   }

   // view on-screen, creating new frame if necessary
   /**
    * @wbp.parser.entryPoint
    */
   public void show() {

       // create the GUI for viewing the image if needed
       if (frame == null) {
           frame = new JFrame();

           JMenuBar menuBar = new JMenuBar();
           JMenu menu = new JMenu("File");
           menuBar.add(menu);
           JMenuItem menuItem1 = new JMenuItem(" Save...   ");
           menuItem1.addActionListener(this);
           menuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
           menu.add(menuItem1);
           frame.setJMenuBar(menuBar);



           frame.setContentPane(getJLabel());
           // f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
           frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
           frame.setTitle("Picture Frame");
           frame.setResizable(false);
           frame.pack();
           frame.setVisible(true);
       }

       // draw
       frame.repaint();
   }


   // accessor methods
   public int height() { return image.getHeight(null); }
   public int width()  { return image.getWidth(null);  }

   // return Color of pixel (i, j)
   public Color get(int i, int j) {
       return new Color(image.getRGB(i, j));
   }

   // change color of pixel (i, j) to c
   public void set(int i, int j, Color c) {
       if (c == null) { throw new RuntimeException("can't set Color to null"); }
       image.setRGB(i, j, c.getRGB());
   }

   // save to given filename - suffix must be png, jpg, or gif
   public void save(String filename) { save(new File(filename)); }

   // save to given filename - suffix must be png, jpg, or gif
   public void save(File file) {
       String filename = file.getName();
       String suffix = filename.substring(filename.lastIndexOf('.') + 1);
       suffix = suffix.toLowerCase();
       if (suffix.equals("jpg") || suffix.equals("png")) {
           try { ImageIO.write(image, suffix, file); }
           catch (IOException e) { e.printStackTrace(); }
       }
       else {
           System.out.println("Error: filename must end in .jpg or .png");
       }
   }

   // open a save dialog when the user selects "Save As" from the menu
   public void actionPerformed(ActionEvent e) {
       FileDialog chooser = new FileDialog(frame,
                            "Use a .png or .jpg extension", FileDialog.SAVE);
       chooser.setVisible(true);
       String filename = chooser.getFile();
       if (filename != null) {
           save(chooser.getDirectory() + File.separator + chooser.getFile());
       }
   }



   // test client: read in input file and display
   public static void main(String[] args) {
       Picture pic = new Picture(args[0]);
       pic.show();
   }

}
