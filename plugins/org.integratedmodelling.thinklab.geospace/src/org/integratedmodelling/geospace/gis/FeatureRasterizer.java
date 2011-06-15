package org.integratedmodelling.geospace.gis;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.util.HashMap;

import javax.media.jai.RasterFactory;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.type.NumericAttributeType;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.geospace.feature.AttributeTable;
import org.integratedmodelling.geospace.interfaces.IGridMask;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.NameGenerator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.referencing.cs.AxisDirection;

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
import com.vividsolutions.jts.geom.Polygon;

/**
 *  Rasterize features onto a WritableRaster object using Java 2D Graphics/BufferedImage.
 *
 * @author  steve.ansari, NOAA (original code)
 * @author  Ferdinando Villa  (extensive modifications)
 * @created March 20, 2008
 */
public class FeatureRasterizer {

    private int height;
    private int width;
    private float noDataValue = Float.NaN;
    private WritableRaster raster = null;   
    private BufferedImage bimage = null;
    private Graphics2D graphics = null;

    private java.awt.geom.Rectangle2D.Double bounds;
    private double cellsize;
    private double minAttValue = 999999999;
    private double maxAttValue = -999999999;

    // Declare these as global
    private int[] coordGridX = new int[3500];
    private int[] coordGridY = new int[3500];
    private float value = 0.0f;
    private boolean swapAxis = false;
    private boolean emptyGrid = false;

    private Geometry extentGeometry;
    private GeometryFactory geoFactory = new GeometryFactory();
    private String attributeName = "value";

	public static GridCoverageFactory rasterFactory = new GridCoverageFactory();
    
    private double xInterval;
    private double yInterval;  

    // Any change in height, width or no_data values will cause 
    // the raster to 'reset' at the next call to .rasterize(...)
    private boolean resetRaster = true;
    
	private AttributeTable attributeTable;
	private int attributeHandle = -1;
	private AttributeDescriptor attributeDescriptor;
	
	/*
	 * if this is not null, we have rasterized a string attribute, and the map
	 * defines the values encountered and the corresponding short integer in
	 * the resulting coverage.
	 */
	private HashMap<String, Integer> classification = null;
	private String valueDefault;
	
	private Geometry hullShape = null;
	private GridExtent extent;
	
    /**
     *Constructor for the FeatureRasterizer object
     *
     * @exception  FeatureRasterizerException  Description of the Exception
     */
    public FeatureRasterizer() {
        this(800, 800, Float.NaN);
    }
    
    /**
     * Notify that instead of taking the value of the raster directly from an attribute, we use
     * an attribute value to link into an external attribute table (e.g. from a CSV file). 
     * 
     * @param table
     * @param linkField
     * @param valueField
     * @throws ThinklabException 
     */
    public void setAttributeTable(AttributeTable table, String linkField, String valueField) throws ThinklabException {
    	
    	attributeTable = table;
    	attributeName = linkField;
    	attributeHandle = table.index(linkField, valueField);
    	
    }

    /**
     * Constructor for the FeatureRasterizer object - will use default 800x800 raster
     *
     * @param  noData                         No Data value for raster
     * @exception  FeatureRasterizerException  Description of the Exception
     */
    public FeatureRasterizer(float noData) {
        this(800, 800, noData);
    }

    public boolean isClassification() {
    	return classification != null;
    }
    
    /**
     * Returns values of the rasterized attribute that correspond to consecutive numeric
     * IDs, ranging from 1 to the number of classes (inclusive), in the raster.
     *  
     * @return
     */
    public String[] getClassification() {

    	String[] ret = new String[classification.size()];
    	
    	for (String s : classification.keySet()) {
    		ret[classification.get(s)-1] = s;
    	}
    	
    	return ret;
    }
    
    /**
     * Constructor for the FeatureRasterizer object.  No Data value defaults to -999.0
     *
     * @param  height                         Height of raster (number of grid cells)
     * @param  width                          Width of raster (number of grid cells)
     */
    public FeatureRasterizer(int height, int width) {
        this(height, width, Float.NaN);
    }

    /**
     * Constructor for the FeatureRasterizer object
     *
     * @param  height                         Height of raster (number of grid cells)
     * @param  width                          Width of raster (number of grid cells)
     * @param  noData                         No Data value for raster
     */
    public FeatureRasterizer(int height, int width, float noData) {
        this.height = height;
        this.width = width;
        this.noDataValue = noData;

        raster = /* RasterFactory.createBandedRaster(DataBuffer.TYPE_FLOAT,
                width, height, 1, null)*/ null;
        bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        bimage.setAccelerationPriority(1.0f);

 //       GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        graphics = bimage.createGraphics();
        graphics.setPaintMode();
        graphics.setComposite(AlphaComposite.Src);
    }
    
    /**
     * Call this one when you need to analyze the attribute for rasterization and
     * behave accordingly. 
     * 
     * @param yCells
     * @param xCells
     * @param noData
     * @param attributeDescriptor
     */
    public FeatureRasterizer(int yCells, int xCells, float noData,
			AttributeDescriptor attributeDescriptor, GridExtent extent) {
		this(yCells, xCells, noData);
		this.attributeDescriptor = attributeDescriptor;
		this.extent = extent;
	}

	public GridCoverage2D rasterize(String name, FeatureCollection<SimpleFeatureType, SimpleFeature> fc, String attributeName, IConcept valueType, ReferencedEnvelope env) 
		throws ThinklabException {
    	
    	if (raster == null) {
    	
    		WritableRaster raster = 
    			RasterFactory.createBandedRaster(
    				getRasterType(valueType), 
					this.width, 
					this.height, 
					1, 
					null);
		
			setWritableRaster(raster);
    	} 

    	clearRaster(null, null);
    	
    	if (env == null) {
    	    		
    		rasterize(fc, attributeName);

    		/*
    		 * Use full envelope from feature collection
    		 * TODO check if we need to use a buffer like in Steve's code above
    		 */
    		env = fc.getBounds();

    	} else {
    		
    		/*
    		 * TODO check if we need to use a buffer like in Steve's code above
    		 */
			 java.awt.geom.Rectangle2D.Double box =
	               new java.awt.geom.Rectangle2D.Double(
	            		   env.getMinX(),
	            		   env.getMinY(), 
	            		   env.getWidth(), 
	            		   env.getHeight());
			 
			 rasterize(fc, box, attributeName);
    	}
    	
		GridCoverage2D coverage = 
			rasterFactory.create(name, raster, env);
		
    	return coverage;
    }
    
    private int getRasterType(IConcept valueType) {

    	int ret = DataBuffer.TYPE_FLOAT;
    	
    	if (valueType != null) {
    		
    		if ( !(valueType.is(KnowledgeManager.Number()))) {
    			if (classification == null) {
    				classification = new HashMap<String, Integer>();
    			}
    		}
    		
    	} else if (attributeDescriptor != null) {
    		
    		if ( !(attributeDescriptor.getType() instanceof NumericAttributeType)) {
    			if (classification == null) {
    				classification = new HashMap<String, Integer>();
    			}
    		}
    	}
    	
    	return ret;
	}

    /**
     * Rasterize a single shape. It is assumed that the grid and the shape agree with each
     * other, and that the axes don't need swapping. FIXME: at the moment uses the normalized
     * envelope, so there may be a problem if stuff comes in non-normalized projections.
     * 
     * @param shape
     * @param grid
     * @param value
     * @return
     * @throws FeatureRasterizerException
     */
	public GridCoverage2D rasterize(ShapeValue shape, GridExtent grid, int value) throws ThinklabException {
    	
    	if (raster == null) {
    	
    		WritableRaster raster = 
    			RasterFactory.createBandedRaster(
    				DataBuffer.TYPE_SHORT, 
					this.width, 
					this.height, 
					1, 
					null);
		
			setWritableRaster(raster);
    	} 

        clearRaster(null, null);
        
        setBounds(grid.getNormalizedBox());
        this.value = (float)value;
        checkReset(DataBuffer.TYPE_SHORT);
        addShape(shape);
        close();
        
		GridCoverage2D coverage = 
			rasterFactory.create(NameGenerator.newName("mask"), raster, grid.getNormalizedEnvelope());
		
    	return coverage;
    }
    
	
	private void checkReset(int type) {

		if (resetRaster) {
            raster = RasterFactory.createBandedRaster(type, width, height, 1, null);

            bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            bimage.setAccelerationPriority(1.0f);
            graphics = bimage.createGraphics();
            graphics.setPaintMode();
            graphics.setComposite(AlphaComposite.Src);
            resetRaster = false;
        }
	}
	
	
	public GridCoverage2D rasterize(String name, FeatureIterator<SimpleFeature> fc, String attributeName, IConcept valueType, 
			String valueDefault, String valueExpression, ReferencedEnvelope env, ReferencedEnvelope normEnv,
			ReferencedEnvelope dataEnvelope) throws ThinklabException {
    	
    	if (raster == null) {
    	
    		WritableRaster raster = 
    			RasterFactory.createBandedRaster(
    				getRasterType(valueType), 
					this.width, 
					this.height, 
					1, 
					null);
		
			setWritableRaster(raster);
    	} 
    	if (env == null) {
    		throw new ThinklabValidationException("rasterizer: envelope must be passed");
    	} 
    	
    	/*
    	 * if we have north-south on the X axis, swap the coordinates.
    	 */
    	if (env.getCoordinateReferenceSystem().getCoordinateSystem().getAxis(0).getDirection().equals(AxisDirection.NORTH)) {
    		// ZORK swapAxis = true;
    	}
    	    	
    	/*
    	 * TODO check if we need to use a buffer like in Steve's code above
    	 */
    	java.awt.geom.Rectangle2D.Double box =
    		new java.awt.geom.Rectangle2D.Double(
    				normEnv.getMinX(),
    				normEnv.getMinY(), 
    				normEnv.getWidth(), 
    				normEnv.getHeight());
			     	
		rasterize(fc, box, attributeName, valueType, valueDefault, dataEnvelope);

		GridCoverage2D coverage = 
			rasterFactory.create(name, raster, env);
		
    	return coverage;
    }

    /**
     *  Gets the raster attribute of the FeatureRasterizer object
     *  Processes data from the FeatureCollection and approximates onto a Raster Grid.
     *
     * @param  fc                             Feature Collection with features to rasterize.
     * @param  attributeName                  Name of attribute from feature collection to provide as the cell value.
     * @exception  FeatureRasterizerException  An error when rasterizing the data
     */
    public void rasterize(FeatureCollection<SimpleFeatureType, SimpleFeature> fc, String attributeName)
    throws ThinklabException {

    	double edgeBuffer = 0.001;
        double x = fc.getBounds().getMinX() - edgeBuffer;
        double y = fc.getBounds().getMinY() - edgeBuffer;
        double width = fc.getBounds().getWidth() + edgeBuffer * 2;
        double height = fc.getBounds().getHeight() + edgeBuffer * 2;
        java.awt.geom.Rectangle2D.Double bounds = new java.awt.geom.Rectangle2D.Double(x, y, width, height);
        rasterize(fc, bounds, attributeName);
    }

    /**
     *  Gets the raster attribute of the FeatureRasterizer object
     *  Processes data from the FeatureCollection and approximates onto a Raster Grid.
     *
     * @param  fc                             Description of the Parameter
     * @param  bounds                         Description of the Parameter
     * @param  attributeName                  Name of attribute from feature collection to provide as the cell value.
     * @exception  FeatureRasterizerException  An error when rasterizing the data
     */
    public void rasterize(FeatureCollection<SimpleFeatureType, SimpleFeature> fc, java.awt.geom.Rectangle2D.Double bounds, String attributeName)
    	throws ThinklabException {

        this.attributeName = attributeName;
        checkReset(DataBuffer.TYPE_FLOAT);
        clearRaster(null, null);
        setBounds(bounds);
        FeatureIterator<SimpleFeature> fci = fc.features();
        SimpleFeature feature;

        while (fci.hasNext()) {
        	
            feature = fci.next();
            addFeature(feature);
        }
        
        close();
    }

    
    /**
     *  Gets the raster attribute of the FeatureRasterizer object
     *  Processes data from the FeatureCollection and approximates onto a Raster Grid.
     *
     * @param  fc                             Description of the Parameter
     * @param  bounds                         Description of the Parameter
     * @param  attributeName                  Name of attribute from feature collection to provide as the cell value.
     * @exception  FeatureRasterizerException  An error when rasterizing the data
     */
    public void rasterize(FeatureIterator<SimpleFeature> fc, java.awt.geom.Rectangle2D.Double bounds, 
    		String attributeName, IConcept valueType, String valueDefault, ReferencedEnvelope denv)
    	throws ThinklabException {
    	
        this.attributeName = attributeName;
        this.valueDefault = valueDefault;

        checkReset(getRasterType(valueType));
        
        if (attributeName == null) {
			// no attribute means use 1.0 for presence of feature
			value = 1.0f;
//			noDataValue = 0.0;
        }
        
        // initialize raster to NoData value
        clearRaster(bounds, denv);
        setBounds(bounds);
        
        SimpleFeature feature; int n = 0;
        while (fc.hasNext()) {       
        	try {
        		feature = fc.next();
        		addFeature(feature);
        		n++;
        	} catch (Exception e) {
        		// timeouts are easy to get into here, we don't want them to kill the rasterization
        		Geospace.get().logger().warn("problem reading feature #" + n + ": " + e.getMessage());
        	}
        	// log when we have to rasterize lots of features, so we know we should take action otherwise 
        	if (n > 0 && (n % 5000) == 0)
        		Geospace.get().logger().info("rasterized " + n + "-th feature... that's a lot to rasterize");
        }
        
        close();

        Geospace.get().logger().info("rasterized " + n + " features");
       
    }

    public void addShape(ShapeValue shape) {
    	
        int rgbVal = floatBitsToInt(value);
        graphics.setColor(new Color(rgbVal, true));
        Geometry geometry = shape.getGeometry();
                
        if (geometry.intersects(extentGeometry)) {
        	
            if (geometry.getClass().equals(MultiPolygon.class) || geometry.getClass().equals(Polygon.class)) {

                for (int i = 0; i < geometry.getNumGeometries(); i++) {
                	Polygon poly = (Polygon) geometry.getGeometryN(i);
                	LinearRing lr = geoFactory.createLinearRing(poly.getExteriorRing().getCoordinates());
            		Polygon part = geoFactory.createPolygon(lr, null);
                	drawGeometry(part, false);
                	for (int j = 0; j < poly.getNumInteriorRing(); j++) {
                		lr = geoFactory.createLinearRing(poly.getInteriorRingN(j).getCoordinates());
                		part = geoFactory.createPolygon(lr, null);
        				drawGeometry(part, true);
        			}
                }
            }
            else if (geometry.getClass().equals(MultiLineString.class)) {
                MultiLineString mp = (MultiLineString)geometry;
                for (int n=0; n<mp.getNumGeometries(); n++) {
                    drawGeometry(mp.getGeometryN(n), false);
                }
            }
            else if (geometry.getClass().equals(MultiPoint.class)) {
                MultiPoint mp = (MultiPoint)geometry;
                for (int n=0; n<mp.getNumGeometries(); n++) {
                    drawGeometry(mp.getGeometryN(n), false);
                }
            }
            else {
                drawGeometry(geometry, false);
            }
        }

    }
    
    /**
     * Implementation the StreamingProcess interface.  Rasterize a single feature and 
     * update current WriteableRaster using the current settings.
     * 
     * @param  feature     The feature to rasterize and add to current WritableRaster
     */   
    public void addFeature(SimpleFeature feature) {

        try {
        	
        	if (this.attributeTable == null) {
        		
        		if (attributeName != null) {
        			
        			Object attr = feature.getAttribute(attributeName);
        			if (attr == null) {
        				if (this.valueDefault != null) {
        					attr = this.valueDefault;
        				} else {
        					return;
        				}
        			}
        			
        			if (classification != null) {
        				value = getClassifiedValue(attr.toString());
        			} else {
        				value = Float.parseFloat(attr.toString());                       				
        			}
        		} 
        		
        		if (value > maxAttValue) { maxAttValue = value; }
        		if (value < minAttValue) { minAttValue = value; }
        		
        	} else {

        		// TODO account for value being used as key into a hash. If so, the value may be null or empty, 
            	// and nodata should be left undisturbed with no error (warning maybe).
        		// value = Float.parseFloat(attributeTable.getIndexedValue(feature.getAttribute(attributeName), attributeHandle));
        		// attributeTable.getIndexedValue(feature.getAttribute(attributeName), attributeHandle)
        	}
        } catch (Exception e) {	        
            e.printStackTrace();	        
            Geospace.get().logger().error(
            		"THE FEATURE COULD NOT BE RASTERIZED BASED ON THE '"+attributeName+
                    "' ATTRIBUTE VALUE OF '"+feature.getAttribute(attributeName).toString()+"'");	        
            return;	        
        }
        
        // Extract polygon and rasterize!
        Geometry geometry = (Geometry) feature.getDefaultGeometry();
                
        if (geometry.intersects(extentGeometry)) {
        	
            if (geometry.getClass().equals(MultiPolygon.class) || geometry.getClass().equals(Polygon.class)) {

                for (int i = 0; i < geometry.getNumGeometries(); i++) {
                	Polygon poly = (Polygon) geometry.getGeometryN(i);
                	LinearRing lr = geoFactory.createLinearRing(poly.getExteriorRing().getCoordinates());
            		Polygon part = geoFactory.createPolygon(lr, null);
                	drawGeometry(part, false);
                	for (int j = 0; j < poly.getNumInteriorRing(); j++) {
                		lr = geoFactory.createLinearRing(poly.getInteriorRingN(j).getCoordinates());
                		part = geoFactory.createPolygon(lr, null);
        				drawGeometry(part, true);
        			}
                }
            }
            else if (geometry.getClass().equals(MultiLineString.class)) {
                MultiLineString mp = (MultiLineString)geometry;
                for (int n=0; n<mp.getNumGeometries(); n++) {
                    drawGeometry(mp.getGeometryN(n), false);
                }
            }
            else if (geometry.getClass().equals(MultiPoint.class)) {
                MultiPoint mp = (MultiPoint)geometry;
                for (int n=0; n<mp.getNumGeometries(); n++) {
                    drawGeometry(mp.getGeometryN(n), false);
                }
            }
            else {
                drawGeometry(geometry, false);
            }
        }
    }

    private float getClassifiedValue(String string) {
    	
    	Integer ret = classification.get(string);
		if (ret == null) {
			ret = classification.size() + 1;
			classification.put(string,ret);
		}
		
		return (float)ret;
	}

	/**
     * this copies values from BufferedImage RGB to WritableRaster of floats or integers.
	 * @throws ThinklabException 
     */
    public void close() throws ThinklabException {
    	
    	IGridMask mask = null;
    	if (extent != null && hullShape != null) {

    		mask = ThinklabRasterizer.createMask(
    				new ShapeValue(hullShape.buffer(
    						Math.max(extent.getEWExtent(), extent.getNSExtent())), 
    						extent.getCRS()), extent);
    	}

    	for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
            	
        		float fval = Float.NaN;
        		if (mask == null || mask.isActive(i, j)) {
        			fval = Float.intBitsToFloat(bimage.getRGB(i, j));
        		}
        		raster.setSample(i, j, 0, fval);
            }
        }
    }

    private void drawGeometry(Geometry geometry, boolean hole) {

    	
        Coordinate[] coords = geometry.getCoordinates();

    	if (!hole) {
    		 collectConvexHull(geometry);
      	}
        
        int rgbVal = floatBitsToInt(hole ? Float.NaN : value);
        graphics.setColor(new Color(rgbVal, true));        
        
        // enlarge if needed
        if (coords.length > coordGridX.length) {
            coordGridX = new int[coords.length];
            coordGridY = new int[coords.length];
        }

        // Clear Array
        for (int i = 0; i < coords.length; i++) {
            coordGridX[i] = -1;
        }
        for (int i = 0; i < coords.length; i++) {
            coordGridY[i] = -1;
        }

        // Go through coordinate array in order received (clockwise)
        if (swapAxis) {
        	for (int n = 0; n < coords.length; n++) {
        		coordGridX[n] = (int) (((coords[n].y - bounds.x) / xInterval));
        		coordGridY[n] = (int) (((coords[n].x - bounds.y) / yInterval));
        		coordGridY[n] = bimage.getHeight() - coordGridY[n]; 
        	}        	
        } else {
        	for (int n = 0; n < coords.length; n++) {
        		coordGridX[n] = (int) (((coords[n].x - bounds.x) / xInterval));
        		coordGridY[n] = (int) (((coords[n].y - bounds.y) / yInterval));
        		coordGridY[n] = bimage.getHeight() - coordGridY[n]; 
        	}
        }

        if (geometry.getClass().equals(Polygon.class)) {
            graphics.fillPolygon(coordGridX, coordGridY, coords.length);
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

    private void collectConvexHull(Geometry geometry) {
    	
//    	ConvexHull chull = null;
//    	
//    	/**
//    	 * OK, this doesn't work either unless we clip the geometry to the
//    	 * bbox every f'ing time.
//    	 */
//    	ArrayList<Coordinate> cac = new ArrayList<Coordinate>();
//    	cac.ensureCapacity(
//    			(hullShape == null ? 0 : hullShape.getNumPoints()) + 
//    			geometry.getNumPoints());
//    	if (hullShape != null) {
//    		for (Coordinate c : hullShape.getCoordinates())
//    			if (extent.contains(c))
//    				cac.add(c);
//    	}
//    	for (Coordinate c : geometry.getCoordinates())
//    		if (extent.contains(c))
//    			cac.add(c);
//    	
//        chull = new ConvexHull(cac.toArray(new Coordinate[cac.size()]), geoFactory);
//    	hullShape = chull.getConvexHull();
	}

	/**
     *  Gets the emptyGrid attribute of the FeatureRasterizer object
     *
     * @return    The emptyGrid value
     */
    public boolean isEmptyGrid() {
        return emptyGrid;
    }

    /**
     *  Gets the writableRaster attribute of the FeatureRasterizer object
     *
     * @return    The writableRaster value
     */
    public WritableRaster getWritableRaster() {
        return raster;
    }

    /**
     *  Sets the writableRaster attribute of the FeatureRasterizer object
     *
     * @param  raster  The new writableRaster value
     */
    public void setWritableRaster(WritableRaster raster) {
        this.raster = raster;
    }

    /**
     *  Gets the bounds attribute of the FeatureRasterizer object
     *
     * @return    The bounds value
     */
    public java.awt.geom.Rectangle2D.Double getBounds() {
        return bounds;
    }

    /**
     *  Sets the bounds for the Rasterizer
     *
     * @return    The bounds value
     */
    public void setBounds(java.awt.geom.Rectangle2D.Double bounds) {
    	
        this.bounds = bounds;

        xInterval = bounds.width / (double) width;
        yInterval = bounds.height / (double) height;

        if (xInterval > yInterval) {
            yInterval = xInterval;
        }
        if (yInterval > xInterval) {
            xInterval = yInterval;
        }

        cellsize = yInterval;

        // Clip geometries to the provided bounds      
        // Create extent geometry  
        Envelope env = 
        	swapAxis ? 
        			new Envelope(
                        	bounds.getY(), 
                       		bounds.getY() + bounds.getHeight(),
                       		bounds.getX(),
                     		bounds.getX() + bounds.getWidth()
                       ) : 
                    new Envelope(
                    		bounds.getX(), 
                       		bounds.getX() + bounds.getWidth(),
                       		bounds.getY(),
                       		bounds.getY() + bounds.getHeight()
                    );
        extentGeometry = geoFactory.toGeometry(env);
    }

    /**
     *  Sets the entire raster to NoData
     * @param bounds2 
     */
    public void clearRaster(java.awt.geom.Rectangle2D.Double bounds, ReferencedEnvelope denv) {

    	minAttValue = 999999999;
        maxAttValue = -999999999;

        double xc = 0.0;
        double yc = 0.0;
        if (bounds != null) {
        	xc = bounds.width / width;
        	yc = bounds.height / height;
        }
        
        // initialize raster to NoData value inside covered area, NaN outside
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
            	
            	boolean inRegion = true;
            	if (bounds != null && denv != null) {
            		inRegion = denv.contains(bounds.getMinX() + xc*i + xc/2.0, bounds.getMinY() + yc*j + yc/2);
            	}
            		
            	float clear = inRegion ? noDataValue : Float.NaN;            	
            	raster.setSample(i, j, 0, clear);
            	bimage.setRGB(i, j, floatBitsToInt(clear));
            }
        }
        
        /*
         * reset collection of coordinates or geometry to compute convex hull for
         * activation
         */
        this.hullShape  = null;
    }

    /**
     *  Get the current attribute to use as the grid cell values.
     */
    public String getAttName() {
        return attributeName;
    }

    /**
     *  Sets the current attribute to use as the grid cell values.
     */
    public void setAttName(String attName) {
        this.attributeName = attName;
    }

    /**
     *  Gets the cellsize attribute of the FeatureRasterizer object
     *
     * @return    The cellsize value
     */
    public double getCellsize() {
        return cellsize;
    }

    public float getNoDataValue() {
        return noDataValue;
    }

    public void setNoDataValue(float noData) {
        if (noData != noDataValue) {
            resetRaster = true;
        }
        this.noDataValue = noData;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        if (this.height != height) {
            resetRaster = true;
        }
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        if (this.width != width) {
            resetRaster = true;
        }
        this.width = width;
    }

    public double getMinAttValue() {
        return minAttValue;
    }

    public double getMaxAttValue() {
        return maxAttValue;
    }

    private int floatBitsToInt(float f) {
    	    	
        ByteBuffer conv = ByteBuffer.allocate(4);
        conv.putFloat(0, f);
        return conv.getInt(0);
    }

    public String toString() {
        return "FEATURE RASTERIZER: WIDTH="+width+" , HEIGHT="+height+" , NODATA=" + noDataValue;
    }
    
}


