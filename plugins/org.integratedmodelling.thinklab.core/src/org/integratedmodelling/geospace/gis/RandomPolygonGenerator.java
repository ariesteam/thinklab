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
package org.integratedmodelling.geospace.gis;

import java.util.Random;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.integratedmodelling.geospace.literals.ShapeValue;

import com.vividsolutions.jts.algorithm.ConvexHull;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;


public class RandomPolygonGenerator {

	    private static final double CLOUD_MIN = 150;
	    private static final double CLOUD_W = 200;
//	    private static final int IMGW = (int)(CLOUD_W + 2 * CLOUD_MIN);
//
//	    private Polygon hullPoly;
//	    private Polygon merPoly;
//
//	    private void doDemo() {
//	        generatePolys();
//
//	        final JPanel panel = new JPanel() {
//
//	            @Override
//	            protected void paintComponent(Graphics g) {
//	                super.paintComponent(g);
//
//	                BufferedImage bufImg = new BufferedImage(IMGW, IMGW,
//	BufferedImage.TYPE_INT_ARGB);
//	                Graphics2D gr = (Graphics2D) bufImg.getGraphics();
//
//	                gr.setColor(Color.BLUE);
//
//	                Coordinate[] hullCoords = hullPoly.getCoordinates();
//	                Coordinate c0 = hullCoords[0];
//	                for (int i = 1; i < hullCoords.length; i++) {
//	                    gr.fillOval((int)c0.x-4, (int)c0.y-4, 9, 9);
//	                    Coordinate c1 = hullCoords[i];
//	                    gr.drawLine((int)c0.x, (int)c0.y, (int)c1.x, (int)c1.y);
//	                    c0 = c1;
//	                }
//
//	                gr.setColor(Color.MAGENTA);
//	                Coordinate[] merCoords = merPoly.getCoordinates();
//	                c0 = merCoords[0];
//	                for (int i = 1; i < merCoords.length; i++) {
//	                    Coordinate c1 = merCoords[i];
//	                    gr.drawLine((int)c0.x, (int)c0.y, (int)c1.x, (int)c1.y);
//	                    c0 = c1;
//	                }
//
//	                ((Graphics2D)g).drawImage(bufImg, null, 0, 0);
//	            }
//	        };
//
//	        panel.addMouseListener(new MouseAdapter() {
//
//	            @Override
//	            public void mouseClicked(MouseEvent e) {
//	                generatePolys();
//	                panel.repaint();
//	            }
//	        });
//
//	        panel.setToolTipText("click for new polygon");
//
//	        final JFrame frame = new JFrame("MER demo");
//	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//	        frame.add(panel);
//	        frame.setSize(IMGW, IMGW);
//
//	        SwingUtilities.invokeLater(new Runnable() {
//
//	            public void run() {
//	                frame.setVisible(true);
//	            }
//	        });
//	    }

	    public static ShapeValue randomPolygon(ReferencedEnvelope envelope, 
	    		int resolutionMeters, int maxLinearSize) {

	    	Random rand = new Random();
	        final Coordinate[] cloud = new Coordinate[10];

	        /*
	         * size and current resolution of box. Ensure that passed parameters are
	         * consistent with enclosing box.
	         */
	        
	        /*
	         * determine bounds where to create points based on given resolution and
	         * linear size.
	         */
	        
	        /*
	         * create 10 random points in box and make polygon shape from their convex hull.
	         */
	        for (int i = 0; i < cloud.length; i++) {
	            cloud[i] = new Coordinate(
	                    CLOUD_MIN + CLOUD_W * rand.nextDouble(),
	                    CLOUD_MIN + CLOUD_W * rand.nextDouble());
	        }

	        ConvexHull hull = new ConvexHull(cloud, new GeometryFactory());
	        return new ShapeValue((Polygon)(hull.getConvexHull()), envelope.getCoordinateReferenceSystem());
	    }


	
}
