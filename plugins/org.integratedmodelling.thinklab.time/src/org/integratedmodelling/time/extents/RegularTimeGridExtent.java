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
package org.integratedmodelling.time.extents;

import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.CoreScience.PhysicalNature;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.ITopologicallyComparable;
import org.integratedmodelling.corescience.interfaces.internal.IDatasourceTransformation;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.corescience.units.Unit;
import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IOperator;
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
	public Object getValue(int granule) {
		long ls = (long)(extent.getStartPoint().getX()) + granuleSize*granule;
		try {
			return new PeriodValue(ls, ls + granuleSize);
		} catch (ThinklabException e) {
			throw new ThinklabRuntimeException(e);
		}
	}

	public int getValueCount() {
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

	@Override
	public boolean checkDomainDiscontinuity() throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IExtent intersection(IExtent extent) throws ThinklabException {
		// TODO Auto-generated method stub
		return extent;
	}

	@Override
	public IExtent force(IExtent extent) throws ThinklabException {
		// TODO Auto-generated method stub
		return extent;
	}

	@Override
	public IExtent union(IExtent extent) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConcept getValueType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValue(int index, Object o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getRawData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] getDataAsDoubles() throws ThinklabValueConversionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getDoubleValue(int index)
			throws ThinklabValueConversionException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Metadata getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConcept getObservableClass() {
		return TimePlugin.TimeGrid();
	}

	@Override
	public IContext getObservationContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Restriction getConstraint(String operator) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IState aggregate(IConcept concept) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AggregationParameters getAggregationParameters(IConcept observable,
			Unit unit) throws ThinklabException  {
		
		AggregationParameters ret = new AggregationParameters(observable, unit);
		ret.aggregationOperator = IOperator.AVG;
		ret.aggregatedUnit = unit;
		ret.aggregatedNature = PhysicalNature.INTENSIVE;
		
		if (CoreScience.isExtensive(observable)) {
			
			/*
			 * determine cell area and conversion factor to 
			 * turn density into quantity
			 */
			Unit sd = unit.getTimeExtentUnit();
			Unit rf = new Unit("sec");
			double um2 = rf.convert(1.0, sd);
			final double cnv = (double)getGranuleSize()/um2;
			
			ret.aggregatedNature = PhysicalNature.EXTENSIVE;
			ret.aggregationOperator = IOperator.SUM;
			ret.aggregator = new Aggregator() {
				@Override
				public double getAggregationFactor(int granule) {
					return cnv;
				}
			};
			
			/*
			 * eliminate the areal term from the aggregated unit
			 */
			ret.aggregatedUnit = 
				new Unit(unit.getUnit().times(sd.getUnit().inverse()));
			
		}
		
		return ret;
	}

	@Override
	public boolean isSpatiallyDistributed() {
		return false;
	}

	@Override
	public boolean isTemporallyDistributed() {
		return getValueCount() > 1;
	}

	@Override
	public boolean isCovered(int granule) {
		// we have no gaps
		return true;
	}

	@Override
	public boolean isProbabilistic() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isContinuous() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNumeric() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCategorical() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBoolean() {
		// TODO Auto-generated method stub
		return false;
	}
}
