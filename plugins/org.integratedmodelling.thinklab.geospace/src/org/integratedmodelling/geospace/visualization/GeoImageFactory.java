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
import org.geotools.ows.ServiceException;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
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


	public static final String WMS_IMAGERY_SERVER_PROPERTY = "imagery.wms";
	public static final String WMS_LAYER_PROPERTY = "imagery.wms.layers";
	
	/*
	 * yes, it's a singleton. It's also a simpleton.
	 */
	private static GeoImageFactory _instance;

	private static WebMapServer _wms = null;
	private int _wms_index = -1;
	
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
				Geospace.get().getProperties().getProperty(WMS_IMAGERY_SERVER_PROPERTY + "." + i);
			
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
		
		return Geospace.get().getResourceURL(worldImage);
	}
	
	public URL getSatelliteImage(Envelope envelope, int width, int height) throws ThinklabException {
		return getSatelliteImageURL(
				envelope, width, height, null, null, 
				HAlignment.MIDDLE, VAlignment.MIDDLE);
	}
	
	public BufferedImage getImagery(Envelope envelope, int width, int height) throws ThinklabException {

		BufferedImage ret = getWFSImage(envelope, width, height);

		if (ret == null)
			ret = getSatelliteImage(envelope, width, height, null, null, 
					HAlignment.MIDDLE, VAlignment.MIDDLE);
		
		return ret;
		
	}
	
	private BufferedImage getWFSImage(Envelope envelope, int width, int height) throws ThinklabResourceNotFoundException {
		
		BufferedImage ret = null;
		initializeWms();
		
		if (_wms != null) {
		
			GetMapRequest request = _wms.createGetMapRequest();
			request.setFormat("image/png");
			request.setDimensions(""+width, ""+height); 
			request.setTransparent(true);
			
			// FIXME this assumes the envelope is in lat/lon
			request.setSRS("EPSG:4326");
			
			String bbox =
				envelope.getMinX() + "," + envelope.getMinY() + "," +
				envelope.getMaxX() + "," + envelope.getMaxY();
			
			request.setBBox(bbox);

			for ( Layer layer : getWMSLayers()) {
				 request.addLayer(layer);
			}
			
			GetMapResponse response = null;
			try {
				response = (GetMapResponse) _wms.issueRequest(request);
				ret = ImageIO.read(response.getInputStream());
			} catch (Exception e) {
				throw new ThinklabResourceNotFoundException(e);
			}
		}
		
		return ret;
	}
	
	private Collection<Layer> getWMSLayers() {
		
		String zp = Geospace.get().getProperties().getProperty(WMS_LAYER_PROPERTY + "." + _wms_index);
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
