/**
 * RegularTimeGridExtent.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabTimePlugin.
 * 
 * ThinklabTimePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabTimePlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.time.extents;

import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.ITopologicallyComparable;
import org.integratedmodelling.corescience.interfaces.internal.IDatasourceTransformation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.time.TimePlugin;
import org.integratedmodelling.time.literals.PeriodValue;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.Polylist;
import org.joda.time.DateTime;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * A regular grid extent represents a fixed number of milliseconds from time x to y. We represent it internally
 * as a line segment, so we can use topological operations on it through JTS.
 * 
 * This class only handles continuous grid segments - it's easy, although cumbersome, to represent discontinuous
 * time extents, but the questions becomes one of semantics. It should be bound to its own time concept, as in the
 * standard one there can be no discontinuities.
 * 
 * @author Ferdinando Villa
 *
 */
public class RegularTimeGridExtent implements IExtent {
	
	/* we represent time as a nice linestring with all Y coordinates = 0, so we can use intersections and unions
	 * appropriately.
	 */
	LineString extent = null;
	long granuleSize = 1;
	
	// just to avoid creating one every time we need it, although arguably Java optimizers know better than that.
	Coordinate[] c = new Coordinate[2];
	private DateTime start;
	private DateTime end;
	private long step;
	
	public DateTime getStart() {
		return start;
	}

	public DateTime getEnd() {
		return end;
	}

	public long getStep() {
		return step;
	}

	// geometry factory used for all calculations; we use fixed 0-decimal precision, so that we deal
	// with whole milliseconds.
	static private GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(0.0));

	/*
	 * Called at all construction points to ensure the grid is internally consistent - i.e. commensurate with
	 * the grid step and as contiguous as required.
	 */
	private void validateGrid() throws ThinklabValidationException {
		
		 if (extent.getNumPoints() != 2)
			 throw new ThinklabValidationException("time extent is discontinuous: " + extent);

		 /* TODO make sure we can use the assigned step to define a grid over the extent */
			double me = extent.getEndPoint().getX();
			double ms = extent.getStartPoint().getX();
			long msecs = (long)(me - ms);
			
			if ((msecs % granuleSize) != 0)
				throw new ThinklabValidationException(
						"time extent " + 
						extent + 
						" is not commensurate with grid step of " + 
						granuleSize + 
						" milliseconds");
	}
	
	
	public RegularTimeGridExtent(DateTime start, DateTime end, long step) throws ThinklabValidationException {
		this.start = start;
		this.end = end;
		this.step = step;
		c[0] = new Coordinate(start.getMillis(), 0);
		c[1] = new Coordinate(end.getMillis(), 0);
		granuleSize = step;
		extent = geometryFactory.createLineString(c);
		validateGrid();
	}

	public RegularTimeGridExtent(LineString gg, long step) throws ThinklabValidationException {

		this.start = new DateTime((long)gg.getStartPoint().getX());
		this.end = new DateTime((long)gg.getEndPoint().getX());
		this.step = step;
		extent = gg;
		granuleSize = step;
		validateGrid();
	}

	@Override
	public IValue getFullExtentValue() {

		IValue ret = null;
		try {
			double me = extent.getEndPoint().getX();
			double ms = extent.getStartPoint().getX();
			ret = new PeriodValue((long)ms, (long)me);
		} catch (ThinklabException e) {
		}
		return ret;
	}

	@Override
	public IValue getState(int granule) throws ThinklabException {
		long ls = (long)(extent.getStartPoint().getX()) + granuleSize*granule;
		return new PeriodValue(ls, ls + granuleSize);
	}

	public int getTotalGranularity() {
		return (int)((long)(extent.getEndPoint().getX() - extent.getStartPoint().getX())/granuleSize);
	}

	public RegularTimeGridExtent intersection(RegularTimeGridExtent oth) throws ThinklabException {
		
		Geometry gg = extent.intersection(oth.extent);
		
		if (!(gg instanceof LineString))				
			throw new ThinklabValidationException(
					"intersection of temporal grid extents generates unsupported extent: " +
					gg);
		
		return new RegularTimeGridExtent((LineString)gg, granuleSize);
	}
	
	public RegularTimeGridExtent union(RegularTimeGridExtent oth) throws ThinklabException {
		
		Geometry gg = extent.union(oth.extent);
		
		if (!(gg instanceof LineString))				
			throw new ThinklabValidationException(
					"union of temporal grid extents generates unsupported extent: " +
					gg);
		
		return new RegularTimeGridExtent((LineString)gg, granuleSize);
	}
	
	public String toString() {
		return "[" + extent + "]/[" + granuleSize + "]";
	}

	public long getGranuleSize() {
		return granuleSize;
	}
	
	
	@Override
	public boolean equals(Object o) {
		return extent.equals(((RegularTimeGridExtent)o).extent) &&  
			   granuleSize == ((RegularTimeGridExtent)o).granuleSize;
	}


	public LineString getTimeExtent() {
		// TODO Auto-generated method stub
		return extent;
	}


	@Override
	public Polylist conceptualize() throws ThinklabException {
	
		return Polylist.list(
				TimePlugin.TEMPORALGRID_TYPE_ID,
				Polylist.list(TimePlugin.STARTS_AT_PROPERTY_ID, start.toString()),
				Polylist.list(TimePlugin.ENDS_AT_PROPERTY_ID, end.toString()),
				Polylist.list(TimePlugin.STEP_SIZE_PROPERTY_ID, step + " ms"));
	}


	@Override
	public IExtent getExtent(int granule) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDatasourceTransformation getDatasourceTransformation(
			IConcept mainObservable, IExtent extent) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IExtent and(IExtent extent) throws ThinklabException {
		
		/*
		 * TODO implement clipping with time intervals
		 */
		if (! (extent instanceof RegularTimeGridExtent)) {
			throw new ThinklabValidationException("time grids can only be merged with other time grids");
		}
		return this.intersection((RegularTimeGridExtent) extent);
		
	}


	@Override
	public IExtent constrain(IExtent extent) throws ThinklabException {
		// TODO IMPLEMENT CORRECTLY
		return and(extent);
	}


	@Override
	public String getSignature() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean contains(ITopologicallyComparable o)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean intersects(ITopologicallyComparable o)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean overlaps(ITopologicallyComparable o)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public IExtent or(IExtent myExtent) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IExtent getAggregatedExtent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Pair<String, Integer>> getStateLocators(int index) {

		ArrayList<Pair<String,Integer>> ret = null;
		
		if (index > 0) {
			ret = new ArrayList<Pair<String,Integer>>();
			// previous
			ret.add(new Pair<String, Integer>("previous", index-1));
		}
		return ret;
	}
}
