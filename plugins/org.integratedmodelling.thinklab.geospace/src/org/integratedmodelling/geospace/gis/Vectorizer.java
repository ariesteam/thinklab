package org.integratedmodelling.geospace.gis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import org.geotools.data.memory.MemoryFeatureCollection;
import org.geotools.feature.AttributeType;
import org.geotools.feature.AttributeTypeFactory;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;
import org.geotools.feature.FeatureTypeBuilder;
import org.geotools.feature.GeometryAttributeType;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.integratedmodelling.geospace.coverage.VectorCoverage;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Extract features from a raster. Vectorization algorithm based on GRASS' r.to.vect.
 * 
 * Modified to classify the input and produce features for all recognized classes of input (except
 * nodata).
 * 
 * @author Bill Brown, Mike Baba, Jean Ezell, Andrew Heekin, David Satnik, Andrea Aime, Radim Blazek (original GRASS algorithm)
 * @author Andrea Antonello (initial port of C code to Java for JGrass 2.1)
 * @author Ferdinando Villa (initial port to Geotools, extension for input classification)
 *
 */
public class Vectorizer {

	private int datarows = 0;
	private int datacols = 0;
	Envelope extent = null;
	
	ArrayList<Double> dataClasses;

	double[][] dataMatrix = null;
	private Coords[][] dataNodeMatrix = null;

	private double novalue = 0.0;
	private String valueAttributeID = "the_value";
	private FeatureType ftContour;

	public class VectorizationException extends Exception {

		private static final long serialVersionUID = 137382710893797945L;

		public VectorizationException() {
		}

		public VectorizationException(String arg0, Throwable arg1) {
			super(arg0, arg1);
		}

		public VectorizationException(String arg0) {
			super(arg0);
		}

		public VectorizationException(Throwable arg0) {
			super(arg0);
		}

	}
	
	/**
	 * Copied and adapted from JGrass
	 * 
	 * Coords represents a point of the boundary of a 
	 * map cell, i.e. if we have a map cell given by its
	 * row and column inside the active region, this cell 
	 * will have 4 corners (nodes), one of which this object 
	 * will represent.
	 * 
	 * @author Andrea Antonello (JGrass code)
	 * @author Ferdinando Villa (adapt for GeoTools)
	 *
	 */
	public class Coords
	{

	  public Coords backCoorPointer = null;
	  public Coords forwardCoorPointer = null; /* pointers to neighboring points */
	  public Coordinate coord = null;
	  public boolean hasBeenUsed = false;

	  
	  private double[] rowColToNodeboundCoordinates(Envelope extent, int xCells, int yCells, int row, int col) {

			double anorth = extent.getMaxY();
			double awest = extent.getMinX();
			double nsres = extent.getHeight()/yCells;
			double ewres = extent.getWidth()/xCells;

			double[] nsew = new double[4];
			nsew[0] = anorth - row * nsres;
			nsew[1] = anorth - row * nsres - nsres;
			nsew[2] = awest + col * ewres + ewres;
			nsew[3] = awest + col * ewres;

			return nsew;
		}

	  /**
		 * set the coordinates of the node by its easting and northing
		 * 
		 * @param x -
		 *            easting
		 * @param y -
		 *            northing
		 */
	  public void setCoordinate(double x, double y) {
	    coord = new Coordinate(x, y);
	  }

	  /**
	   * set the coordinates from the top-left corner of the cell
	   * 
	   * @param row
	   * @param col
	   */
	  public void setCoordinateFromTl(int row, int col, Envelope extent, int xc, int yc)
	  {
	    double[] nsew = rowColToNodeboundCoordinates(extent, xc, yc, row, col);
	    coord = new Coordinate(nsew[3], nsew[0]);
	  }


	/**
	   * set the coordinates from the top-right corner of the cell
	   * 
	   * @param row
	   * @param col
	   */
	  public void setCoordinateFromTr(int row, int col, Envelope extent, int xc, int yc)
	  {
	    double[] nsew = rowColToNodeboundCoordinates(extent, xc, yc, row, col);
	    coord = new Coordinate(nsew[2], nsew[0]);
	  }

	  /**
	   * set the coordinates from the bottom-left corner of the cell
	   * 
	   * @param row
	   * @param col
	   */
	  public void setCoordinateFromBl(int row, int col, Envelope extent, int xc, int yc)
	  {
	    double[] nsew = rowColToNodeboundCoordinates(extent, xc, yc, row, col);
	    coord = new Coordinate(nsew[3], nsew[1]);
	  }

	  /**
	   * set the coordinates from the bottom-right corner of the cell
	   * 
	   * @param row
	   * @param col
	   */
	  public void setCoordinateFromBr(int row, int col, Envelope extent, int xc, int yc)
	  {
	    double[] nsew = rowColToNodeboundCoordinates(extent, xc, yc, row, col);
	    coord = new Coordinate(nsew[2], nsew[1]);
	  }

	}


	/**
	 * Create a vectorizer. Pass an array of data to vectorize, the no data value, and
	 * the spatial envelope of the array.
	 * 
	 * @param data
	 * @param extent
	 * @param noDataValue
	 */
	public Vectorizer(double[][] data, Envelope extent, double noDataValue) {
		
		this.novalue = noDataValue;
		this.extent = extent;
		initialize(data);
	}

	/**
	 * Set the name we want for the attribute value. It's going to be a Float for now, no choice
	 * given. If this is not called, the attribute name is "the_value".
	 * 
	 * @param aName
	 */
	public void setValueAttributeName(String aName) {
		this.valueAttributeID = aName;
	}
	
	public String getValueAttributeName() {
		return this.valueAttributeID;
	}
	
	/**
	 * The main driver of the vectorization algorithm. Will run extract_areas on each 
	 * data class.
	 * 
	 * @return
	 * @throws VectorizationException
	 */
	public FeatureCollection extractFeatures() throws VectorizationException {

		MemoryFeatureCollection ret = null;

		/*
		 * run extract_areas for each data class
		 *
		 * TODO we can use ints or even bits, reclassing as we go
		 */
		for (Double d : dataClasses) {
			
			System.out.println("Extracting " + d);
			
			Feature f = extract_areas(extent, datacols, datarows, d);

			if (f != null) {				
				
				if (ret == null)
					ret = new MemoryFeatureCollection(ftContour);
				
				ret.add(f);
			}
		}
		
		return ret;
		
	}
	
	public void initialize(double[][] data) {

		dataMatrix = data;
		
		datarows = dataMatrix.length;
		datacols = dataMatrix[0].length;

		/*
		 * Classify data, skipping the no data value
		 */
		dataClasses = new ArrayList<Double>();
		
		for (int x = 0; x < datarows; x++) 
			for (int y = 0; y < datacols; y++) {
				if (dataMatrix[x][y] != novalue && !dataClasses.contains(dataMatrix[x][y]))
					dataClasses.add(dataMatrix[x][y]);
			}
		
		dataNodeMatrix = new Coords[datarows + 1][datacols + 1];
		for (int i = 0; i < dataNodeMatrix.length; i++) {
			for (int j = 0; j < dataNodeMatrix[0].length; j++) {
				dataNodeMatrix[i][j] = null;
			}
		}
	}
	
	/**
	 * extract_areas - trace boundaries of polygons of given value in file; produce a feature with the 
	 * value as attribute.
	 *  
	 * @param d the value of the features we're looking for
	 * @throws VectorizationException 
	 */
	protected Feature extract_areas(Envelope extent, int xc, int yc, double d) throws VectorizationException {
		
		double tl, tr, bl, br;
		
		/*
		 *  process rest of file, 2 rows at  a time
		 */
		for (int row = 0; row < datarows - 1; row++) {

			for (int col = 0; col < datacols - 1; col++) {
				
				tl = dataMatrix[row][col]; /* top left in window */
				tr = dataMatrix[row][col + 1]; /* top right */
				bl = dataMatrix[row + 1][col]; /* bottom left */
				br = dataMatrix[row + 1][col + 1]; /* bottom right */
				int kase = nabors(tl, tr, bl, br, d);
				update_list(tl, tr, bl, br, kase, row, col, extent, xc, yc);
			}
		}

		Vector<CoordinateList> coordVector = new Vector<CoordinateList>();

		boolean error = false;
		for (int i = 0; i < dataNodeMatrix.length; i++) {
			for (int j = 0; j < dataNodeMatrix[0].length; j++) {
				if (dataNodeMatrix[i][j] != null
						&& !dataNodeMatrix[i][j].hasBeenUsed) {
					CoordinateList carr = new CoordinateList();
					/*
					 *  there is some coord info in it. The Coords 
					 *  are linked, so that the whole polygon can be 
					 *  recreated. In order to not double the polygons, 
					 *  the used Coords are marked as used.
					 */
					Coords begin = dataNodeMatrix[i][j];
					begin.hasBeenUsed = true;
					Coords next = null;
					if (begin.forwardCoorPointer != null)
						next = begin.forwardCoorPointer;
					else 
						throw new VectorizationException(
								"Found a loose end vectorizing data");

					carr.add(begin.coord, true);
					int maxcount = dataNodeMatrix[0].length
							* dataNodeMatrix.length;
					int runningcount = 0;
					while (!begin.equals(next)) {
						carr.add(next.coord, true);
						next.hasBeenUsed = true;
						if (next.forwardCoorPointer != null)
							next = next.forwardCoorPointer;
						else
							throw new VectorizationException(
									"Found a loose end vectorizing data");

						if (runningcount > maxcount) {
							
							/*
							 * This should not be an error, it's just a linestring
							 * feature. We may need to fatten it into a polygon later,
							 * but it should not be the vectorizer's job.
							 */
//							throw new VectorizationException(
//									"No matching end (polygon error) vectorizing " + 
//									(coverage == null ? "data" : coverage.getLayerName()));
				            error = true;
				            break;	
						}
						runningcount++;
					}
					if (!error) {
						// adding end coordinate
						carr.add(begin.coord, true);
					}
					coordVector.add(carr);
				}
			}
		}

		/*
		 *  at that point the coordinate list should be complete,
		 *  let's try to build the polygon
		 */
		GeometryAttributeType geometryAttribute = null;
		if (!error) {
			geometryAttribute = (GeometryAttributeType) AttributeTypeFactory
					.newAttributeType("the_geom", MultiPolygon.class);
		} else {
			geometryAttribute = (GeometryAttributeType) AttributeTypeFactory
					.newAttributeType("the_geom", MultiLineString.class);
		}
		AttributeType areaAttribute = (AttributeType) AttributeTypeFactory
				.newAttributeType("area", Float.class);
		AttributeType boundAttribute = (AttributeType) AttributeTypeFactory
				.newAttributeType("perimeter", Float.class);
		
		/*
		 * add the value
		 */
		AttributeType valueAttribute = (AttributeType) AttributeTypeFactory
			.newAttributeType(valueAttributeID , Float.class);

		try {
			this.ftContour = FeatureTypeBuilder.newFeatureType(new AttributeType[] {
					geometryAttribute, areaAttribute, boundAttribute, valueAttribute },
					"default");
		} catch (Exception e) {
	    	throw new VectorizationException(e);
	    }

		// create the feature attributes
		GeometryFactory gf = new GeometryFactory();
		Geometry ls = null;

		if (!error) {
			Polygon[] pg = new Polygon[coordVector.size()];
			for (int i = 0; i < coordVector.size(); i++) {
				LinearRing ring = gf.createLinearRing(coordVector.elementAt(i)
						.toCoordinateArray());
				pg[i] = gf.createPolygon(ring, null);
			}
			ls = gf.createMultiPolygon(pg);
		} else {
			LineString[] pg = new LineString[coordVector.size()];
			for (int i = 0; i < coordVector.size(); i++) {
				pg[i] = gf.createLineString(coordVector.elementAt(i)
						.toCoordinateArray());
			}
			ls = gf.createMultiLineString(pg);
		}

		Object[] featureAttribs = { ls, ls.getArea(), ls.getLength(), new Float(d) };

		// create the feature
		Feature feature = null;
		try {
			feature = ftContour.create(featureAttribs);
	    } catch (Exception e) {
	    	throw new VectorizationException(e);
	    }
		return feature;

	}

	/**
	 * update_list - maintains linked list of COOR structures which resprsent 
	 * bends in and endpoints of lines separating areas in input file; 
	 * compiles a list of area to category number correspondences; 
	 * for pictures of what each case in the switch represents, see comments 
	 * before nabors() 
	 * 
	 * @param i
	 * @return
	 */
	private void update_list(double tl, double tr, double bl, double br, int i,
			int row, int col, Envelope extent, int xc, int yc) {
		
		Coords c1 = null;
		Coords c2 = null;
		Coords c3 = null;

		switch (i) {
		case 0:
			/*
			 *--*--*              c1
			 | x |    |
			 *-- *   *     c3   c2
			 |         |
			 *--*--*      
			 */

			c1 = new Coords();
			c2 = new Coords();
			c3 = new Coords();
			c1.setCoordinateFromTr(row, col, extent, xc, yc);
			c2.setCoordinateFromBr(row, col, extent, xc, yc);
			c3.setCoordinateFromBl(row, col, extent, xc, yc);

			// top-center node
			if (dataNodeMatrix[row][col + 1] == null) {
				dataNodeMatrix[row][col + 1] = c1;
			}
			// middle-center node
			if (dataNodeMatrix[row + 1][col + 1] == null) {
				dataNodeMatrix[row + 1][col + 1] = c2;
			}
			// middle-left node
			if (dataNodeMatrix[row + 1][col] == null) {
				dataNodeMatrix[row + 1][col] = c3;
			}
			joinCoordsToSequence(dataNodeMatrix[row][col + 1],
					dataNodeMatrix[row + 1][col + 1],
					dataNodeMatrix[row + 1][col]);

			break;
		case 1:
			/*
			 *--*--*             c3
			 |    | x |
			 *-- *  *     c1   c2
			 | x   x |
			 *--*--*      
			 */

			c1 = new Coords();
			c2 = new Coords();
			c3 = new Coords();
			c1.setCoordinateFromBl(row, col, extent, xc, yc);
			c2.setCoordinateFromBr(row, col, extent, xc, yc);
			c3.setCoordinateFromTr(row, col, extent, xc, yc);

			// top-center node
			if (dataNodeMatrix[row][col + 1] == null) {
				dataNodeMatrix[row][col + 1] = c3;
			}
			// middle-center node
			if (dataNodeMatrix[row + 1][col + 1] == null) {
				dataNodeMatrix[row + 1][col + 1] = c2;
			}
			// middle-left node
			if (dataNodeMatrix[row + 1][col] == null) {
				dataNodeMatrix[row + 1][col] = c1;
			}
			joinCoordsToSequence(dataNodeMatrix[row + 1][col],
					dataNodeMatrix[row + 1][col + 1],
					dataNodeMatrix[row][col + 1]);

			break;
		case 2:
			/*
			 *--*--*      c3
			 |    | x |
			 *   *--*     c2   c1
			 |         |
			 *--*--*      
			 */

			c1 = new Coords();
			c2 = new Coords();
			c3 = new Coords();
			c1.setCoordinateFromBr(row, col + 1, extent, xc, yc);
			c2.setCoordinateFromBr(row, col, extent, xc, yc);
			c3.setCoordinateFromTr(row, col, extent, xc, yc);

			// top-center node
			if (dataNodeMatrix[row][col + 1] == null) {
				dataNodeMatrix[row][col + 1] = c3;
			}
			// middle-center node
			if (dataNodeMatrix[row + 1][col + 1] == null) {
				dataNodeMatrix[row + 1][col + 1] = c2;
			}
			// middle-right node
			if (dataNodeMatrix[row + 1][col + 2] == null) {
				dataNodeMatrix[row + 1][col + 2] = c1;
			}
			joinCoordsToSequence(dataNodeMatrix[row + 1][col + 2],
					dataNodeMatrix[row + 1][col + 1],
					dataNodeMatrix[row][col + 1]);

			break;
		case 3:
			/*
			 *--*--*      c1
			 | x |    |
			 *   *--*     c2   c3
			 | x  x  |
			 *--*--*      
			 */

			c1 = new Coords();
			c2 = new Coords();
			c3 = new Coords();
			c1.setCoordinateFromTr(row, col, extent, xc, yc);
			c2.setCoordinateFromBr(row, col, extent, xc, yc);
			c3.setCoordinateFromBr(row, col + 1, extent, xc, yc);

			// top-center node
			if (dataNodeMatrix[row][col + 1] == null) {
				dataNodeMatrix[row][col + 1] = c1;
			}
			// middle-center node
			if (dataNodeMatrix[row + 1][col + 1] == null) {
				dataNodeMatrix[row + 1][col + 1] = c2;
			}
			// middle-right node
			if (dataNodeMatrix[row + 1][col + 2] == null) {
				dataNodeMatrix[row + 1][col + 2] = c3;
			}
			joinCoordsToSequence(dataNodeMatrix[row][col + 1],
					dataNodeMatrix[row + 1][col + 1],
					dataNodeMatrix[row + 1][col + 2]);

			break;
		case 4:
			/*
			 *--*--*      
			 |        |
			 *--*   *     c1   c2
			 | x |   |
			 *--*--*            c3
			 */

			c1 = new Coords();
			c2 = new Coords();
			c3 = new Coords();
			c1.setCoordinateFromTl(row + 1, col, extent, xc, yc);
			c2.setCoordinateFromTr(row + 1, col, extent, xc, yc);
			c3.setCoordinateFromBr(row + 1, col, extent, xc, yc);

			// middle-left node
			if (dataNodeMatrix[row + 1][col] == null) {
				dataNodeMatrix[row + 1][col] = c1;
			}
			// middle-center node
			if (dataNodeMatrix[row + 1][col + 1] == null) {
				dataNodeMatrix[row + 1][col + 1] = c2;
			}
			// bottom-center node
			if (dataNodeMatrix[row + 2][col + 1] == null) {
				dataNodeMatrix[row + 2][col + 1] = c3;
			}
			joinCoordsToSequence(dataNodeMatrix[row + 1][col],
					dataNodeMatrix[row + 1][col + 1],
					dataNodeMatrix[row + 2][col + 1]);

			break;
		case 5:
			/*
			 *--*--*      
			 | x  x |
			 *--*   *     c3   c2
			 |    |x |
			 *--*--*            c1
			 */

			c1 = new Coords();
			c2 = new Coords();
			c3 = new Coords();
			c1.setCoordinateFromBr(row + 1, col, extent, xc, yc);
			c2.setCoordinateFromTr(row + 1, col, extent, xc, yc);
			c3.setCoordinateFromTl(row + 1, col, extent, xc, yc);

			// middle-left node
			if (dataNodeMatrix[row + 1][col] == null) {
				dataNodeMatrix[row + 1][col] = c3;
			}
			// middle-center node
			if (dataNodeMatrix[row + 1][col + 1] == null) {
				dataNodeMatrix[row + 1][col + 1] = c2;
			}
			// bottom-center node
			if (dataNodeMatrix[row + 2][col + 1] == null) {
				dataNodeMatrix[row + 2][col + 1] = c1;
			}
			joinCoordsToSequence(dataNodeMatrix[row + 2][col + 1],
					dataNodeMatrix[row + 1][col + 1],
					dataNodeMatrix[row + 1][col]);
			break;
		case 6:
			/*
			 *--*--*      
			 |         |
			 *   *--*     c2   c3
			 |    | x |
			 *--*--*      c1
			 */

			c1 = new Coords();
			c2 = new Coords();
			c3 = new Coords();
			c1.setCoordinateFromBl(row + 1, col + 1, extent, xc, yc);
			c2.setCoordinateFromTl(row + 1, col + 1, extent, xc, yc);
			c3.setCoordinateFromTr(row + 1, col + 1, extent, xc, yc);

			// bottom-center node
			if (dataNodeMatrix[row + 2][col + 1] == null) {
				dataNodeMatrix[row + 2][col + 1] = c1;
			}
			// middle-center node
			if (dataNodeMatrix[row + 1][col + 1] == null) {
				dataNodeMatrix[row + 1][col + 1] = c2;
			}
			// middle-right node
			if (dataNodeMatrix[row + 1][col + 2] == null) {
				dataNodeMatrix[row + 1][col + 2] = c3;
			}
			joinCoordsToSequence(dataNodeMatrix[row + 2][col + 1],
					dataNodeMatrix[row + 1][col + 1],
					dataNodeMatrix[row + 1][col + 2]);

			break;
		case 7:
			/*
			 *--*--*      
			 | x   x |
			 *   *--*     c2   c1
			 | x |   |
			 *--*--*      c3
			 */

			c1 = new Coords();
			c2 = new Coords();
			c3 = new Coords();
			c1.setCoordinateFromTr(row + 1, col + 1, extent, xc, yc);
			c2.setCoordinateFromTl(row + 1, col + 1, extent, xc, yc);
			c3.setCoordinateFromBl(row + 1, col + 1, extent, xc, yc);

			// bottom-center node
			if (dataNodeMatrix[row + 2][col + 1] == null) {
				dataNodeMatrix[row + 2][col + 1] = c3;
			}
			// middle-center node
			if (dataNodeMatrix[row + 1][col + 1] == null) {
				dataNodeMatrix[row + 1][col + 1] = c2;
			}
			// middle-right node
			if (dataNodeMatrix[row + 1][col + 2] == null) {
				dataNodeMatrix[row + 1][col + 2] = c1;
			}
			joinCoordsToSequence(dataNodeMatrix[row + 1][col + 2],
					dataNodeMatrix[row + 1][col + 1],
					dataNodeMatrix[row + 2][col + 1]);
			break;
		case 8:
			/*
			 *--*--*      c3
			 |    | x |
			 *   *   *     c2   
			 |    | x |
			 *--*--*      c1
			 */

			c1 = new Coords();
			c2 = new Coords();
			c3 = new Coords();
			c1.setCoordinateFromBl(row + 1, col + 1, extent, xc, yc);
			c2.setCoordinateFromTl(row + 1, col + 1, extent, xc, yc);
			c3.setCoordinateFromTl(row, col + 1, extent, xc, yc);

			// bottom-center node
			if (dataNodeMatrix[row + 2][col + 1] == null) {
				dataNodeMatrix[row + 2][col + 1] = c1;
			}
			// middle-center node
			if (dataNodeMatrix[row + 1][col + 1] == null) {
				dataNodeMatrix[row + 1][col + 1] = c2;
			}
			// top-center node
			if (dataNodeMatrix[row][col + 1] == null) {
				dataNodeMatrix[row][col + 1] = c3;
			}
			joinCoordsToSequence(dataNodeMatrix[row + 2][col + 1],
					dataNodeMatrix[row + 1][col + 1],
					dataNodeMatrix[row][col + 1]);
			break;
		case 9:
			/*
			 *--*--*      c1
			 | x |    |
			 *   *   *     c2   
			 | x |    |
			 *--*--*      c3
			 */

			c1 = new Coords();
			c2 = new Coords();
			c3 = new Coords();
			c1.setCoordinateFromTl(row, col + 1, extent, xc, yc);
			c2.setCoordinateFromTl(row + 1, col + 1, extent, xc, yc);
			c3.setCoordinateFromBl(row + 1, col + 1, extent, xc, yc);

			// bottom-center node
			if (dataNodeMatrix[row + 2][col + 1] == null) {
				dataNodeMatrix[row + 2][col + 1] = c3;
			}
			// middle-center node
			if (dataNodeMatrix[row + 1][col + 1] == null) {
				dataNodeMatrix[row + 1][col + 1] = c2;
			}
			// top-center node
			if (dataNodeMatrix[row][col + 1] == null) {
				dataNodeMatrix[row][col + 1] = c1;
			}
			joinCoordsToSequence(dataNodeMatrix[row][col + 1],
					dataNodeMatrix[row + 1][col + 1],
					dataNodeMatrix[row + 2][col + 1]);
			break;
		case 10:
			/*
			 *--*--*      
			 | x   x |
			 *--*-- *     c3    c2    c1   
			 |         |
			 *--*--*      
			 */

			c1 = new Coords();
			c2 = new Coords();
			c3 = new Coords();
			c1.setCoordinateFromBr(row, col + 1, extent, xc, yc);
			c2.setCoordinateFromBr(row, col, extent, xc, yc);
			c3.setCoordinateFromBl(row, col, extent, xc, yc);

			// middle-right node
			if (dataNodeMatrix[row + 1][col + 2] == null) {
				dataNodeMatrix[row + 1][col + 2] = c1;
			}
			// middle-center node
			if (dataNodeMatrix[row + 1][col + 1] == null) {
				dataNodeMatrix[row + 1][col + 1] = c2;
			}
			// top-center node
			if (dataNodeMatrix[row + 1][col] == null) {
				dataNodeMatrix[row + 1][col] = c3;
			}
			joinCoordsToSequence(dataNodeMatrix[row + 1][col + 2],
					dataNodeMatrix[row + 1][col + 1],
					dataNodeMatrix[row + 1][col]);
			break;
		case 11:
			/*
			 *--*--*      
			 |         |
			 *--*-- *     c1    c2    c3   
			 | x   x |
			 *--*--*      
			 */

			c1 = new Coords();
			c2 = new Coords();
			c3 = new Coords();
			c1.setCoordinateFromBl(row, col, extent, xc, yc);
			c2.setCoordinateFromBr(row, col, extent, xc, yc);
			c3.setCoordinateFromBr(row, col + 1, extent, xc, yc);

			// middle-right node
			if (dataNodeMatrix[row + 1][col + 2] == null) {
				dataNodeMatrix[row + 1][col + 2] = c3;
			}
			// middle-center node
			if (dataNodeMatrix[row + 1][col + 1] == null) {
				dataNodeMatrix[row + 1][col + 1] = c2;
			}
			// top-center node
			if (dataNodeMatrix[row + 1][col] == null) {
				dataNodeMatrix[row + 1][col] = c1;
			}
			joinCoordsToSequence(dataNodeMatrix[row + 1][col],
					dataNodeMatrix[row + 1][col + 1],
					dataNodeMatrix[row + 1][col + 2]);
			break;
		case 12:
			/*
			 * this would create a non valid polygon because
			 * of selfintersection. Because of tecnical issues
			 * the choosen solution is that to bypass the center 
			 * point in the second case (i.e. when coming back d1-d2).
			 * 
			 *--*--*              c3
			 |    | x |
			 *-- *--*     c1    c2       d1
			 | x |    |
			 *--*-- *              d2
			 */

			c1 = new Coords();
			c2 = new Coords();
			c3 = new Coords();
			c1.setCoordinateFromBl(row, col, extent, xc, yc);
			c2.setCoordinateFromBr(row, col, extent, xc, yc);
			c3.setCoordinateFromTr(row, col, extent, xc, yc);

			// middle-left node
			if (dataNodeMatrix[row + 1][col] == null) {
				dataNodeMatrix[row + 1][col] = c1;
			}
			// middle-center node
			if (dataNodeMatrix[row + 1][col + 1] == null) {
				dataNodeMatrix[row + 1][col + 1] = c2;
			}
			// top-center node
			if (dataNodeMatrix[row][col + 1] == null) {
				dataNodeMatrix[row][col + 1] = c3;
			}

			dataNodeMatrix[row + 1][col].forwardCoorPointer = dataNodeMatrix[row + 1][col + 1];

			dataNodeMatrix[row + 1][col + 1].forwardCoorPointer = dataNodeMatrix[row][col + 1];
			dataNodeMatrix[row + 1][col + 1].backCoorPointer = dataNodeMatrix[row + 1][col];

			dataNodeMatrix[row][col + 1].backCoorPointer = dataNodeMatrix[row + 1][col + 1];

			Coords d1 = new Coords();
			Coords d2 = new Coords();
			d1.setCoordinateFromBr(row, col + 1, extent, xc, yc);
			d2.setCoordinateFromBr(row + 1, col, extent, xc, yc);

			// middle-right node
			if (dataNodeMatrix[row + 1][col + 2] == null) {
				dataNodeMatrix[row + 1][col + 2] = d1;
			}
			// lower-center node
			if (dataNodeMatrix[row + 2][col + 1] == null) {
				dataNodeMatrix[row + 2][col + 1] = d2;
			}

			// join them
			dataNodeMatrix[row + 1][col + 2].forwardCoorPointer = dataNodeMatrix[row + 2][col + 1];
			dataNodeMatrix[row + 2][col + 1].backCoorPointer = dataNodeMatrix[row + 1][col + 2];
			break;
		case 13:
			/*
			 * this would create a non valid polygon because
			 * of selfintersection. Because of tecnical issues
			 * the choosen solution is that to bypass the center 
			 * point in the second case (i.e. when coming back d1-d2).
			 * 
			 *--*--*              c1
			 | x |    |
			 *-- *--*     e2    c2       c3
			 |    | x |
			 *--*-- *              e1
			 */

			c1 = new Coords();
			c2 = new Coords();
			c3 = new Coords();
			c1.setCoordinateFromTr(row, col, extent, xc, yc);
			c2.setCoordinateFromBr(row, col, extent, xc, yc);
			c3.setCoordinateFromBr(row, col + 1, extent, xc, yc);

			// top-center node
			if (dataNodeMatrix[row][col + 1] == null) {
				dataNodeMatrix[row][col + 1] = c1;
			}
			// middle-center node
			if (dataNodeMatrix[row + 1][col + 1] == null) {
				dataNodeMatrix[row + 1][col + 1] = c2;
			}
			// middle-right node
			if (dataNodeMatrix[row + 1][col + 2] == null) {
				dataNodeMatrix[row + 1][col + 2] = c3;
			}

			dataNodeMatrix[row][col + 1].forwardCoorPointer = dataNodeMatrix[row + 1][col + 1];

			dataNodeMatrix[row + 1][col + 1].forwardCoorPointer = dataNodeMatrix[row + 1][col + 2];
			dataNodeMatrix[row + 1][col + 1].backCoorPointer = dataNodeMatrix[row][col + 1];

			dataNodeMatrix[row + 1][col + 2].backCoorPointer = dataNodeMatrix[row + 1][col + 1];

			Coords e1 = new Coords();
			Coords e2 = new Coords();
			e1.setCoordinateFromBr(row + 1, col, extent, xc, yc);
			e2.setCoordinateFromTl(row + 1, col, extent, xc, yc);

			// lower-center node
			if (dataNodeMatrix[row + 2][col + 1] == null) {
				dataNodeMatrix[row + 2][col + 1] = e1;
			}
			// middle-left node
			if (dataNodeMatrix[row + 1][col] == null) {
				dataNodeMatrix[row + 1][col] = e2;
			}

			// join them
			dataNodeMatrix[row + 2][col + 1].forwardCoorPointer = dataNodeMatrix[row + 1][col];
			dataNodeMatrix[row + 1][col].backCoorPointer = dataNodeMatrix[row + 2][col + 1];
			break;
		} /* switch */

	}

	/**
	 * Joins three Coords assuming that the second is the next of the first and the 
	 * first the previous of the second and so on.
	 * That way they are chained to each other in the 
	 * right order through their internal pointers.
	 * 
	 * @param coords1
	 * @param coords2
	 * @param coords3
	 */
	private void joinCoordsToSequence(Coords coords1, Coords coords2,
			Coords coords3) {
		if (coords1.forwardCoorPointer == null)
			coords1.forwardCoorPointer = coords2;

		if (coords2.forwardCoorPointer == null)
			coords2.forwardCoorPointer = coords3;
		if (coords2.backCoorPointer == null)
			coords2.backCoorPointer = coords1;

		if (coords3.backCoorPointer == null)
			coords3.backCoorPointer = coords2;
	}

	/**
	 *  nabors - check 2 x 2 matrix and return case from table below 
	 *
	 *    *--*--*      *--*--*      *--*--*      *--*--*    
	 *    | x |   |      |    | x|       |   | x |      | x |   |    
	 *    *--*  *      *--*   *      *   *--*      *   *--*    
	 *    |        |      | x   x |      |         |      | x  x |    
	 *    *--*--*      *--*--*      *--*--*      *--*--*    
	 *       0                 1               2               3      
	 *
	 *    *--*--*      *--*--*      *--*--*      *--*--*   
	 *    |        |      | x  x  |      |         |      | x  x |  
	 *    *--*   *     *--*   *      *   *--*      *   *--*   
	 *    | x |   |      |   | x |       |   | x |      | x|    |    
	 *    *--*--*      *--*--*      *--*--*      *--*--*   
	 *       4                5                6               7       
	 *
	 *    *--*--*      *--*--*      *--*--*      *--*--*    
	 *    |    |x |      | x |   |      | x   x |      |         |    
	 *    *   *   *      *  *  *      *--*--*      *--*--*    
	 *    |    |x |      | x |   |      |         |      | x   x |    
	 *    *--*--*      *--*--*      *--*--*      *--*--*    
	 *       8               9               10              11      
	 *
	 *    *--*--*      *--*--*      *--*--*    
	 *    |    |x |      | x |   |      |         |    
	 *    *--*--*      *  *  *      *         *    
	 *    | x |   |      |    | x|      |         |    
	 *    *--*--*      *--*--*      *--*--*    
	 *       12              13             14       
	 */
	private int nabors(double tl, double tr, double bl, double br, double checked) {
		
		if (tl == checked && tr != checked && bl != checked && br != checked)
			return 0;
		if (tl != checked && tr == checked && bl == checked && br == checked)
			return 1;
		if (tl != checked && tr == checked && bl != checked && br != checked)
			return 2;
		if (tl == checked && tr != checked && bl == checked && br == checked)
			return 3;
		if (tl != checked && tr != checked && bl == checked && br != checked)
			return 4;
		if (tl == checked && tr == checked && bl != checked && br == checked)
			return 5;
		if (tl != checked && tr != checked && bl != checked && br == checked)
			return 6;
		if (tl == checked && tr == checked && bl == checked && br != checked)
			return 7;
		if (tl != checked && tr == checked && bl != checked && br == checked)
			return 8;
		if (tl == checked && tr != checked && bl == checked && br != checked)
			return 9;
		if (tl == checked && tr == checked && bl != checked && br != checked)
			return 10;
		if (tl != checked && tr != checked && bl == checked && br == checked)
			return 11;
		if (tl != checked && tr == checked && bl == checked && br != checked)
			return 12;
		if (tl == checked && tr != checked && bl != checked && br == checked)
			return 13;

		return 14;
	}
	
	public static void main(String[] args) {
		
		double[][] data = 
		{
			{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 7.0, 7.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 7.0, 7.0, 7.0, 7.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 7.0, 7.0, 7.0, 7.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 1.0, 1.0, 7.0, 7.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 1.0, 1.0, 1.0, 7.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 1.0, 1.0, 1.0, 4.0, 4.0, 4.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 1.0, 4.0, 4.0, 5.0, 5.0, 5.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 1.0, 1.0, 5.0, 5.0, 5.0, 5.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 1.0, 1.0, 5.0, 5.0, 5.0, 5.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 1.0, 1.0, 5.0, 5.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
			{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
		};
		
		
		Envelope cext = new Envelope(0.0, 31.0, 0.0, 25.0);

		Vectorizer vect = new Vectorizer(data, cext, 1.0);
		
		try {
			FeatureCollection azzo = vect.extractFeatures();
			System.out.println(azzo);
			VectorCoverage vc = new VectorCoverage(azzo, DefaultGeographicCRS.WGS84, "the_value", false);
			vc.show();
		} catch (VectorizationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
