package org.integratedmodelling.geospace.visualization;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;

import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.utils.MiscUtilities;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

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
	
	
	/*
	 * yes, it's a singleton.
	 */
	private static GeoImageFactory _instance;

	private HashMap<String, URL> worldImages = new HashMap<String, URL>();
	
	public URL getWorldImage(String worldImage, ShapeValue ... shapes) throws ThinklabIOException {
		
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
		
		return Geospace.get().getResourceURL(worldImage);
	}
	
	public URL getSatelliteImage(Envelope envelope, int width, int height) throws ThinklabException {
		return getSatelliteImage(
				envelope, width, height, null, null, 
				HAlignment.MIDDLE, VAlignment.MIDDLE);
	}
	
	
	public URL getSatelliteImage(Envelope envelope, URL other, int width, int height) throws ThinklabException {
		return getSatelliteImage(
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
	public URL getSatelliteImage(Envelope envelope, int width, int height, String worldImage, URL otherImage, HAlignment horAligment, VAlignment verAlignment) throws ThinklabException {
		
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
