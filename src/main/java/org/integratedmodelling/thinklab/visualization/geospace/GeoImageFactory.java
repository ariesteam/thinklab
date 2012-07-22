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
package org.integratedmodelling.thinklab.visualization.geospace;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;

import org.geotools.data.ows.Layer;
import org.geotools.data.wms.WMSUtils;
import org.geotools.data.wms.WebMapServer;
import org.geotools.data.wms.request.GetMapRequest;
import org.geotools.data.wms.response.GetMapResponse;
import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.visualization.ColorMap;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.image.ImageUtil;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;

public class GeoImageFactory {

	public enum HAlignment {
		LEFT,
		MIDDLE,
		RIGHT
	}
	
	public enum VAlignment {
		TOP,
		MIDDLE,
		BOTTOM
	}

	public static final int HOLLOW_SHAPES = 0x0000;
	public static final int FILLED_SHAPES = 0x0001;
	public static final int BORDER = 0x0002;
	public static final int GREEN_SHAPES = 0x004;

	public static final String WMS_IMAGERY_SERVER_PROPERTY = "imagery.wms";
	public static final String WMS_LAYER_PROPERTY = "imagery.wms.layers";
	
	private HashMap<String, BufferedImage> _cache = new HashMap<String, BufferedImage>();
	
	/*
	 * yes, it's a singleton. It's also a simpleton.
	 */
	private static GeoImageFactory _instance;

	private static WebMapServer _wms = null;
	private int _wms_index = -1;

	private String[] wmsServers = new String[] {
			"http://geoposer.com:443/server/services/ve.xml",
			"http://terraservice.net/ogccapabilities.ashx",
			"http://onearth.jpl.nasa.gov/wms.cgi?request=GetCapabilities"
	};
	
	private HashMap<String, URL> worldImages = new HashMap<String, URL>();
	
	/**
	 * Try out all the configured WMS servers (in imagery.properties) stopping at the first
	 * one that responds. The server will be in _wms after that; if none has responded, _wms
	 * will be null. It will only run the search once, so it can safely be called multiple 
	 * times with no performance penalty, and should be called by each function that wants
	 * to use WMS imagery.
	 */
	private void initializeWms() {
		
		if (_wms_index >= 0)
			return;
		
		for (int i = 0; ; i++) {
			
			String url = 
					Thinklab.get().getProperties().getProperty(WMS_IMAGERY_SERVER_PROPERTY + "." + i);
			
			if (url == null)
				break;
			
			try {
				WebMapServer wms = new WebMapServer(new URL(url));
				if (wms != null)
					_wms = wms;
			} catch (Exception e) {
				/* just try the next */
			}
			
			_wms_index = i;

			if (_wms != null) {
				break;
			}
		}
		
		if (_wms == null) {
			/*
			 * try defaults
			 */
			for (String s : wmsServers) {
				try {
					WebMapServer wms = new WebMapServer(new URL(s));
					if (wms != null) {
						_wms = wms;
						break;
					}
				} catch (Exception e) {
					/* just try the next */
				}				
			}
		}
		
	}
	
	public URL getWorldImageURL(String worldImage, ShapeValue ... shapes) throws ThinklabIOException {
		
		URL f = getWorldImageFile(worldImage);
		
		if (shapes == null) 
			return f;
		
		/* open image, get graphics object to draw unto */
		BufferedImage img;
		try {
			img = ImageIO.read(f);
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		
		Graphics g = img.createGraphics();
		g.setColor(Color.RED);
		g.setPaintMode();
		
		for (ShapeValue s : shapes) {
			Polygon p = getPolygon(s, img.getWidth(), img.getHeight());
			g.fillPolygon(p.xpoints, p.ypoints, p.npoints);
		}
		
		File o = null;
		
		try {
			o = File.createTempFile("wim", ".gif");
			ImageIO.write(img, "gif", o);
			f = o.toURI().toURL();
		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}
		
		return f;
	}

	private Polygon getPolygon(ShapeValue s, int w, int h) {
		
		Geometry g = s.getGeometry();
		Polygon ret = new Polygon();
		HashSet<String> points = new HashSet<String>();
		int sx = 0, sy = 0;
		for (Coordinate c : g.getBoundary().getCoordinates()) {
			
			int x = (int)((double)w * (c.x + 180.0)/360.0);
			int y = h - (int)((double)h * (c.y + 90.0)/180.0);

			if (!points.contains(x+"|"+y)) {

				if (ret.npoints == 0) {
					sx = x;	
					sy = y;
				}
				
				ret.addPoint(x, y);
				points.add(x+"|"+y);
			}
		}
		
		/*
		 * close polygon
		 */
		if (ret.npoints > 0) {
			ret.addPoint(sx, sy);
		}
		
		return ret;
	}

	public URL getWorldImageFile(String worldImage) throws ThinklabIOException {
		
		URL ret = worldImages.get(worldImage);
		
		if (ret != null)
			return ret;
		
		return Thinklab.get().getResourceURL(worldImage);
	}
	
	public URL getSatelliteImage(Envelope envelope, int width, int height) throws ThinklabException {
		return getSatelliteImageURL(
				envelope, width, height, null, null, 
				HAlignment.MIDDLE, VAlignment.MIDDLE);
	}
	
	public BufferedImage getImagery(Envelope envelope, int width, int height) throws ThinklabException {

		BufferedImage ret = getWMSImage(envelope, width, height);

		if (ret == null)
			ret = getSatelliteImage(envelope, width, height, null, null, 
					HAlignment.MIDDLE, VAlignment.MIDDLE);
		
		return ret;
		
	}
	
	public BufferedImage getRasterImagery(Envelope envelope, int width, int height, 
			int[] imageData, int rowWidth, ColorMap cmap) 
		throws ThinklabException {

		BufferedImage ret = getWMSImage(envelope, width, height);

		if (ret == null)
			ret = getSatelliteImage(envelope, width, height, null, null, 
					HAlignment.MIDDLE, VAlignment.MIDDLE);
		
		/*
		 * get unscaled image from pixels
		 */
		Image image = ImageUtil.drawUnscaledRaster(
				ImageUtil.upsideDown(imageData, rowWidth),
				rowWidth, cmap);
		
		/*
		 * paint scaled over the scenery
		 */
		Graphics graphics = ret.getGraphics();
		graphics.drawImage(image, 0, 0, width, height, null); // /??? scaling
		graphics.dispose();
		
		return ret;
	}
	
	public BufferedImage paintOverImagery(Envelope envelope, int width, int height, 
			Image image, int rowWidth, ColorMap cmap) 
		throws ThinklabException {

		BufferedImage ret = getWMSImage(envelope, width, height);

		if (ret == null)
			ret = getSatelliteImage(envelope, width, height, null, null, 
					HAlignment.MIDDLE, VAlignment.MIDDLE);
		
		/*
		 * paint scaled over the scenery
		 */
		Graphics graphics = ret.getGraphics();
		graphics.drawImage(image, 0, 0, width, height, null); // /??? scaling
		graphics.dispose();
		
		return ret;
	}
	
	/**
	 * Return an image of the world with a shape drawn on it. Flags control the
	 * rendering mode. Default is a hollow shape in red outline, touching the borders
	 * of the image. Uses WMS servers configured in, trying hard-coded defaults if not
	 * configured or configured badly, and resorts to static image if one was provided
	 * before declaring defeat.
	 * 
	 * @param shape
	 * @param width
	 * @param height
	 * @param flags
	 * @return
	 * @throws ThinklabException
	 */
	public BufferedImage getImagery(Envelope envelope, ShapeValue shape, int width, int height, int flags) throws ThinklabException {

		BufferedImage ret = getImagery(envelope, width, height);
		GeometryFactory geoFactory = new GeometryFactory();

		double edgeBuffer = 0.0;
	    
		if (ret == null) {
			ret = getSatelliteImage(envelope, width, height, null, null, 
					HAlignment.MIDDLE, VAlignment.MIDDLE);
		}	
		
		/*
		 * draw shape boundaries.
		 */
		Geometry geometry = shape.getGeometry();
		double x = envelope.getMinX() - edgeBuffer;
		double y = envelope.getMinY() - edgeBuffer;
		double w = envelope.getWidth() + edgeBuffer * 2;
		double h = envelope.getHeight() + edgeBuffer * 2;
		
		java.awt.geom.Rectangle2D.Double bounds = 
			new java.awt.geom.Rectangle2D.Double(x, y, w, h);

		Graphics graphics = ret.getGraphics();
		
		if ((flags & GREEN_SHAPES) != 0) {
			graphics.setColor(Color.green);
		} else {
			graphics.setColor(Color.red);
		}
		
		graphics.setPaintMode();
		
		if (geometry.getClass().equals(MultiPolygon.class) || geometry.getClass().equals(Polygon.class)) {	

			for (int i = 0; i < geometry.getNumGeometries(); i++) {
				com.vividsolutions.jts.geom.Polygon poly = 
					(com.vividsolutions.jts.geom.Polygon) geometry.getGeometryN(i);
				LinearRing lr = geoFactory.createLinearRing(poly.getExteriorRing().getCoordinates());
				com.vividsolutions.jts.geom.Polygon part = 
					geoFactory.createPolygon(lr, null);
                drawGeometry(part, bounds, graphics, width, height, flags);
                for (int j = 0; j < poly.getNumInteriorRing(); j++) {
                	lr = geoFactory.createLinearRing(poly.getInteriorRingN(j).getCoordinates());
                	part = geoFactory.createPolygon(lr, null);
                	drawGeometry(part, bounds, graphics, width, height, flags);
                }
			}
		} else if (geometry.getClass().equals(MultiLineString.class)) {
			MultiLineString mp = (MultiLineString)geometry;
			for (int n=0; n<mp.getNumGeometries(); n++) {
				drawGeometry(mp.getGeometryN(n), bounds, graphics, width, height, flags);
			}
		} else if (geometry.getClass().equals(MultiPoint.class)) {
			MultiPoint mp = (MultiPoint)geometry;
			for (int n=0; n<mp.getNumGeometries(); n++) {
				drawGeometry(mp.getGeometryN(n), bounds, graphics, width, height, flags);
			}
		} else {
			drawGeometry(geometry, bounds, graphics, width, height, flags);
		}
		
		return ret;	
	}

	public BufferedImage getImagery(ShapeValue shape, int width, int height, int flags) throws ThinklabException {
		return getImagery(shape.getEnvelope(), shape, width, height, flags);
	}

    private void drawGeometry(Geometry geometry, 
    		java.awt.geom.Rectangle2D.Double bounds, 
    		Graphics graphics, int width, int height, int flags) {

        Coordinate[] coords = geometry.getCoordinates();
        
        double xInterval = bounds.width / (double) width;
        double yInterval = bounds.height / (double) height;

        // System.out.println("xInterval: " + xInterval + "  yInterval: " + yInterval);

        if (xInterval > yInterval) {
            yInterval = xInterval;
        }
        if (yInterval > xInterval) {
            xInterval = yInterval;
        }
        
        // for later
        double cellsize = yInterval;

        // TODO fix this stupid legacy preallocation when it works
        int[] coordGridX = new int[coords.length];
        int[] coordGridY = new int[coords.length];

        // Go through coordinate array in order received (clockwise)
        for (int n = 0; n < coords.length; n++) {
        	
        	coordGridX[n] = (int) (((coords[n].x - bounds.x) / xInterval));
        	coordGridY[n] = (int) (((coords[n].y - bounds.y) / yInterval));
        	coordGridY[n] = height - coordGridY[n] - 1; 
        	
        	// this may happen at the extremes, unless we use the pixel center as the coordinate
        	if (coordGridX[n] < 0) coordGridX[n] = 0;
        	if (coordGridY[n] < 0) coordGridY[n] = 0;
        	if (coordGridX[n] >= width) coordGridX[n] = width - 1;
        	if (coordGridY[n] >= height) coordGridY[n] = height -1;
        }
         
        /*
         * ok, this if isn't really necessary, but it may become so in the
         * future.
         */
        if (geometry.getClass().equals(com.vividsolutions.jts.geom.Polygon.class)) {
        	if ((flags & FILLED_SHAPES) != 0) {
        		graphics.fillPolygon(coordGridX, coordGridY, coords.length);
        	} else {
        		graphics.drawPolyline(coordGridX, coordGridY, coords.length);
        	}
        }
        else if (geometry.getClass().equals(LinearRing.class)) {
            graphics.drawPolyline(coordGridX, coordGridY, coords.length);
        }
        else if (geometry.getClass().equals(LineString.class)) {
            graphics.drawPolyline(coordGridX, coordGridY, coords.length);
        }
        else if (geometry.getClass().equals(Point.class)) {
            graphics.drawPolyline(coordGridX, coordGridY, coords.length);
        }
    }
	
    /**
     * @param envelope
     * @param width
     * @param height
     * @return
     * @throws ThinklabResourceNotFoundException
     */
	private BufferedImage getWMSImage(Envelope envelope, int width, int height) throws ThinklabResourceNotFoundException {
		
		BufferedImage ret = null;
		initializeWms();
		
		String sig = envelope.toString() + "," + width + "," + height;
		
		if (_cache.containsKey(sig))
			return ImageUtil.clone(_cache.get(sig));
		
		if (_wms != null) {
		
			GetMapRequest request = _wms.createGetMapRequest();
			request.setFormat("image/png");
			request.setDimensions(""+width, ""+height); 
			request.setTransparent(true);
			
			// FIXME this assumes the envelope is in lat/lon
			request.setSRS("EPSG:4326");
			
			String bbox =
				(float)envelope.getMinX() + "," + (float)envelope.getMinY() + "," +
				(float)envelope.getMaxX() + "," + (float)envelope.getMaxY();
			
			request.setBBox(bbox);
			
			for ( Layer layer : getWMSLayers()) {
				 request.addLayer(layer);
			}
			
			GetMapResponse response = null;
			try {
				
				System.out.println(request.getFinalURL());
				
				response = (GetMapResponse) _wms.issueRequest(request);
				ret = ImageIO.read(response.getInputStream());
			} catch (Exception e) {
				Thinklab.get().logger().warn("cannot get WFS imagery: " + e.getLocalizedMessage());
				return null;
			}
			
			/*
			 * FIXME this obviously must have a limit
			 */
			if (ret != null)
				_cache.put(sig, ImageUtil.clone(ret));
		}
		
		return ret;
	}
	
	private Collection<Layer> getWMSLayers() {
		
		String zp = Thinklab.get().getProperties().getProperty(WMS_LAYER_PROPERTY + "." + _wms_index);
		ArrayList<Layer> layers = new ArrayList<Layer>();
		for (Layer l : WMSUtils.getNamedLayers(_wms.getCapabilities())) {
			if (zp == null || (zp != null && zp.contains(l.getName()))) {
				layers.add(l);
			}
		}
		return layers;
	}

	public URL getSatelliteImage(Envelope envelope, URL other, int width, int height) throws ThinklabException {
		return getSatelliteImageURL(
				envelope, width, height, null, other, 
				HAlignment.MIDDLE, VAlignment.MIDDLE);
	}
	
	/**
	 * The all-configurable draw image engine
	 * 
	 * @param envelope
	 * @param width
	 * @param height
	 * @param worldImage
	 * @param otherImage
	 * @param horAligment
	 * @param verAlignment
	 * @return
	 * @throws ThinklabException
	 */
	public BufferedImage getSatelliteImage(Envelope envelope, int width, int height, String worldImage, URL otherImage, HAlignment horAligment, VAlignment verAlignment) throws ThinklabException {
		
		URL f = null;
		
		if (worldImage == null)  {
				throw new ThinklabIOException("geospace: no world image file specified in properties");
		}
		
		f = getWorldImageFile(worldImage);		
		
		/* open image, get graphics object to draw unto */
		BufferedImage img;
		try {
			img = ImageIO.read(f);
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		
		int w = img.getWidth();
		int h = img.getHeight();
		
		int x1 = (int)((double)w * (envelope.getMinX() + 180.0)/360.0);
		int y2 = h - (int)((double)h * (envelope.getMinY() + 90.0)/180.0);

		int x2= (int)((double)w * (envelope.getMaxX() + 180.0)/360.0);
		int y1 = h - (int)((double)h * (envelope.getMaxY() + 90.0)/180.0);

		int gw = x2 - x1;
		int gh = y2 - y1;
		
		BufferedImage part = img.getSubimage(x1, y1, gw, gh);		 
		
		// Create rescaled picture as new buffered image	    
        AffineTransform tx = new AffineTransform();
        tx.scale((double) width / gw, (double) height/ gh);
       
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        BufferedImage newImage = op.filter(part, null);
	    
		/* rescale to desired width */
		Graphics2D graphics2D = newImage.createGraphics();

		/* if we passed one, burn in the other image in specified alignment */
		if (otherImage != null) {
			
			BufferedImage other;
			try {
				other = ImageIO.read(otherImage);
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}
			
			int ow = other.getWidth();
			int oh = other.getHeight();
			
			// TODO check if this is ok: a larger image does not get drawn, without
			// any warning.
			if (width >= ow && height >= oh) {
				
				int nx = 0, ny = 0;
				
				if (horAligment == HAlignment.MIDDLE)
					nx = (width - ow)/2;
				else if (horAligment == HAlignment.RIGHT)
					nx = width - ow;
				
				if (verAlignment == VAlignment.MIDDLE)
					ny = (height - oh)/2;
				else if (verAlignment == VAlignment.BOTTOM)
					ny = (height - oh);
				
				
				graphics2D.drawImage(other, nx, ny, ow, oh, null);
				
			}
		}
		
		return newImage;
				
	}
	
	public URL getSatelliteImageURL(Envelope envelope, int width, int height, String worldImage, URL otherImage, HAlignment horAligment, VAlignment verAlignment) throws ThinklabException {
		
		BufferedImage newImage = 
			getSatelliteImage(envelope, width, height, worldImage, otherImage, horAligment, verAlignment);

		URL f = null;
		File o = null;
		
		try {
			o = File.createTempFile("sim", ".png");
			ImageIO.write(newImage, "png", o);
			f = o.toURI().toURL();
		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}
		
		return f;
		
	}

	/**
	 * Define the plot size for the given map dimensions that ensures
	 * that a map drawing of the area fits maximally within a viewport.
	 * 
	 * @param viewportWidth
	 * @param viewportHeight
	 * @param mapWidth
	 * @param mapHeight
	 * @return <width, height> of the largest map that fits in the viewport without distorsion.
	 */
	public static Pair<Integer, Integer> getPlotSize(int viewportWidth, int viewportHeight, int mapWidth, int mapHeight) {

		int x = viewportWidth, y = viewportHeight;
		double image_aspect_ratio = (double)mapWidth / (double)mapHeight;

			// largest side of image must fit within corresponding side of viewport
			if (mapWidth > mapHeight) {
				x = viewportWidth;
				y = (int)(((double)x) / image_aspect_ratio);
				if (y > viewportHeight) {
					// reduce further
					double fc = (double)viewportHeight/(double)y;
					x = (int)((double)x*fc);
					y = (int)((double)y*fc);
				}
			} else {
				y = viewportHeight;
				x = (int)(((double)y) * image_aspect_ratio);
				if (x > viewportWidth) {
					// reduce further
					double fc = (double)viewportWidth/(double)x;
					x = (int)((double)x*fc);
					y = (int)((double)y*fc);
				}
			}

		return new Pair<Integer, Integer>(x, y);
	}
	
	public static GeoImageFactory get() {

		if (_instance == null) {
			_instance = new GeoImageFactory();
		}
		
		return _instance;
	}

	public void addWorldImage(URL url) {
		
		String wname = MiscUtilities.getURLBaseName(url.toString());
		worldImages.put(wname, url);
		
	}
}
