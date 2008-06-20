package org.integratedmodelling.utils;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
 
public class DisplayImage extends JFrame implements MouseListener {
	
	private static final long serialVersionUID = 534353042627845857L;

	public DisplayImage(URL image) {
    	
        super(image.toString());
 
        try {
            Image largeImage = ImageIO.read(image);
 
            // ImagePanel is a JComponent subclass (see below)
            ImagePanel displayPanel = new ImagePanel(largeImage);
 
            // by adding a mouse listener to it, we can listen for clicks on the image
            displayPanel.addMouseListener(this);
 
            // content pane is the main container for the frame
            Container cp = getContentPane();
 
            // wraps the image panel in a scroll pane
            JScrollPane scroller = new JScrollPane(displayPanel);
 
            // tell the scrollbar what size you would like it to be, otherwise
            // it will try to be as large as the viewport component
            scroller.setPreferredSize(new Dimension(500, 500));
 
            // add the scrollpane to our frame here
            cp.add(scroller);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
 
        // swing components use layout managers to help with resizing frames.
        // pack() tries to resize all components to their preferred size using
        // the layout managers. By setting the layout of a component to null,
        // you can use setBounds or setSize to explicitly tell components how
        // big they should be, but they won't automatically resize.
        pack();
 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
 
    // since we said we implement the MouseListener interface, we must include
    // all the methods. Of course, you can leave the body blank if you are not
    // interested in an event
    public void mousePressed(MouseEvent evt) {
        //System.out.println("Pressed at " + evt.getX() + "," + evt.getY());
    }
 
    public void mouseReleased(MouseEvent evt) {
        //System.out.println("Released at " + evt.getX() + "," + evt.getY());
    }
 
    public void mouseClicked(MouseEvent evt) {
        //System.out.println("Clicked at " + evt.getX() + "," + evt.getY());
    }
 
    public void mouseEntered(MouseEvent evt) {
        //System.out.println("Entered at " + evt.getX() + "," + evt.getY());
    }
 
    public void mouseExited(MouseEvent evt) {
        //System.out.println("Exited at " + evt.getX() + "," + evt.getY());
    }
 
    // simple overwritten component to display an image
   class ImagePanel extends JComponent {

	private static final long serialVersionUID = -2981813461531528090L;
	private Image drawImage;
 
       public ImagePanel(Image image) {
          setImage(image);
       }
 
       public void setImage(Image image) {
          if (image == null) {
             this.drawImage = null;
             return;
          }
 
          int width = image.getWidth(this);
 
          // if the image is not fully loaded, it's width will
          // return 0
          if (width <= 0) {
             MediaTracker tracker = new MediaTracker(this);
             tracker.addImage(image, 0);
 
             try {
                tracker.waitForID(0);
             }
             catch (InterruptedException ie) {}
 
             width = image.getWidth(this);
          }
 
          int height = image.getHeight(this);
 
          this.setPreferredSize(new Dimension(width, height));
          this.drawImage = image;
       }
 
       // overwrite paintComponent here instead of paint 
       public void paintComponent(Graphics g) {
          g.drawImage(drawImage, 0, 0, this);
       }
    }
 
}
