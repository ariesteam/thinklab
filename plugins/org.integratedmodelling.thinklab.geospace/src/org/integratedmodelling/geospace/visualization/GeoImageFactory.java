package org.integratedmodelling.geospace.visualization;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;

import org.integratedmodelling.geospace.values.ShapeValue;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.utils.MiscUtilities;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class GeoImageFactory {

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
		
		File f = new File(worldImage);
		
		if (f.exists()) {
			try {
				ret = f.toURI().toURL();
			} catch (MalformedURLException e) {
				throw new ThinklabIOException(e);
			}
		} else {
			/* lookup into plugin area */
			
		}
			
		return ret;
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
