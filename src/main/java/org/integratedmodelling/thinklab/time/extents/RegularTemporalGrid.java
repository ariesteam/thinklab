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
package org.integratedmodelling.thinklab.time.extents;

import java.util.Collection;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.modelling.IExtent;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.api.modelling.ITopologicallyComparable;
import org.integratedmodelling.thinklab.modelling.lang.Observation;
import org.integratedmodelling.thinklab.time.Time;
import org.joda.time.DateTime;

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
public class RegularTemporalGrid extends Observation implements IExtent {

	public RegularTemporalGrid(DateTime timeData, DateTime timeData2,
			long milliseconds) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object getValue(int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getRawData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] getDataAsDoubles() throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getDoubleValue(int index) throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getValueCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IState aggregate(IConcept concept) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSpatiallyDistributed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTemporallyDistributed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getMultiplicity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IExtent intersection(IExtent other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IExtent union(IExtent other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean contains(IExtent o) throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean overlaps(IExtent o) throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean intersects(IExtent o) throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IExtent collapse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IExtent getExtent(int stateIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCovered(int stateIndex) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Pair<String, Integer>> getStateLocators(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDiscontinuous() throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IExtent force(IExtent extent) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

//	TimeValue start = null;
//	TimeValue end = null;
//	DurationValue step = null;
//	
//	RegularTimeGridExtent extent = null;
//
//	@Override
//	public void initialize(IInstance i) throws ThinklabException {
//
//		/* complete definition with observable. */
//		i.addObjectRelationship(CoreScience.HAS_OBSERVABLE, TimePlugin.continuousTimeInstance());
//
//		/* 
//		 * recover values for three properties defining the grid. If one is not there, we assume none is
//		 * there.
//		 */
//		if (i.get(TimePlugin.STARTS_AT_PROPERTY_ID) != null) {
//			start = (TimeValue) i.get(TimePlugin.STARTS_AT_PROPERTY_ID);
//			end   = (TimeValue) i.get(TimePlugin.ENDS_AT_PROPERTY_ID);
//			step  = (DurationValue) i.get(TimePlugin.STEP_SIZE_PROPERTY_ID);
//		}
//		
//		super.initialize(i);
//		
//		this.extent = new RegularTimeGridExtent(
//				start.getTimeData(), end.getTimeData(), step.getMilliseconds());
//	}
//
//	@Override
//	public Polylist conceptualize() throws ThinklabException {
//		
//		return Polylist.list(
//				TimePlugin.TEMPORALGRID_TYPE_ID,
//				Polylist.list(TimePlugin.STARTS_AT_PROPERTY_ID, start.toString()),
//				Polylist.list(TimePlugin.ENDS_AT_PROPERTY_ID, end.toString()),
//				Polylist.list(TimePlugin.STEP_SIZE_PROPERTY_ID, step));
//	}
//
//	@Override
//	public IExtent getExtent() throws ThinklabException {
//		return extent;
//	}
//
//	@Override
//	public void checkUnitConformance(IConcept concept, Unit unit)
//			throws ThinklabValidationException {
//		
//		if (!unit.isRate())
//			throw new ThinklabValidationException(
//					"concept " + 
//					concept + 
//					" is observed in time but unit " + 
//					unit + 
//					" does not specify a rate");
//	}

	
//	/* we represent time as a nice linestring with all Y coordinates = 0, so we can use intersections and unions
//	 * appropriately.
//	 */
//	LineString extent = null;
//	long granuleSize = 1;
//	
//	// just to avoid creating one every time we need it, although arguably Java optimizers know better than that.
//	Coordinate[] c = new Coordinate[2];
//	private DateTime start;
//	private DateTime end;
//	private long step;
//	
//	public DateTime getStart() {
//		return start;
//	}
//
//	public DateTime getEnd() {
//		return end;
//	}
//
//	public long getStep() {
//		return step;
//	}
//
//	// geometry factory used for all calculations; we use fixed 0-decimal precision, so that we deal
//	// with whole milliseconds.
//	static private GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(0.0));
//
//	/*
//	 * Called at all construction points to ensure the grid is internally consistent - i.e. commensurate with
//	 * the grid step and as contiguous as required.
//	 */
//	private void validateGrid() throws ThinklabValidationException {
//		
//		 if (extent.getNumPoints() != 2)
//			 throw new ThinklabValidationException("time extent is discontinuous: " + extent);
//
//		 /* TODO make sure we can use the assigned step to define a grid over the extent */
//			double me = extent.getEndPoint().getX();
//			double ms = extent.getStartPoint().getX();
//			long msecs = (long)(me - ms);
//			
//			if ((msecs % granuleSize) != 0)
//				throw new ThinklabValidationException(
//						"time extent " + 
//						extent + 
//						" is not commensurate with grid step of " + 
//						granuleSize + 
//						" milliseconds");
//	}
//	
//	
//	public RegularTimeGridExtent(DateTime start, DateTime end, long step) throws ThinklabValidationException {
//		this.start = start;
//		this.end = end;
//		this.step = step;
//		c[0] = new Coordinate(start.getMillis(), 0);
//		c[1] = new Coordinate(end.getMillis(), 0);
//		granuleSize = step;
//		extent = geometryFactory.createLineString(c);
//		validateGrid();
//	}
//
//	public RegularTimeGridExtent(LineString gg, long step) throws ThinklabValidationException {
//
//		this.start = new DateTime((long)gg.getStartPoint().getX());
//		this.end = new DateTime((long)gg.getEndPoint().getX());
//		this.step = step;
//		extent = gg;
//		granuleSize = step;
//		validateGrid();
//	}
//
//	@Override
//	public IValue getFullExtentValue() {
//
//		IValue ret = null;
//		try {
//			double me = extent.getEndPoint().getX();
//			double ms = extent.getStartPoint().getX();
//			ret = new PeriodValue((long)ms, (long)me);
//		} catch (ThinklabException e) {
//		}
//		return ret;
//	}
//
//	@Override
//	public Object getValue(int granule) {
//		long ls = (long)(extent.getStartPoint().getX()) + granuleSize*granule;
//		try {
//			return new PeriodValue(ls, ls + granuleSize);
//		} catch (ThinklabException e) {
//			throw new ThinklabRuntimeException(e);
//		}
//	}
//
//	public int getValueCount() {
//		return (int)((long)(extent.getEndPoint().getX() - extent.getStartPoint().getX())/granuleSize);
//	}
//
//	public RegularTimeGridExtent intersection(RegularTimeGridExtent oth) throws ThinklabException {
//		
//		Geometry gg = extent.intersection(oth.extent);
//		
//		if (!(gg instanceof LineString))				
//			throw new ThinklabValidationException(
//					"intersection of temporal grid extents generates unsupported extent: " +
//					gg);
//		
//		return new RegularTimeGridExtent((LineString)gg, granuleSize);
//	}
//	
//	public RegularTimeGridExtent union(RegularTimeGridExtent oth) throws ThinklabException {
//		
//		Geometry gg = extent.union(oth.extent);
//		
//		if (!(gg instanceof LineString))				
//			throw new ThinklabValidationException(
//					"union of temporal grid extents generates unsupported extent: " +
//					gg);
//		
//		return new RegularTimeGridExtent((LineString)gg, granuleSize);
//	}
//	
//	public String toString() {
//		return "[" + extent + "]/[" + granuleSize + "]";
//	}
//
//	public long getGranuleSize() {
//		return granuleSize;
//	}
//	
//	
//	@Override
//	public boolean equals(Object o) {
//		return extent.equals(((RegularTimeGridExtent)o).extent) &&  
//			   granuleSize == ((RegularTimeGridExtent)o).granuleSize;
//	}
//
//
//	public LineString getTimeExtent() {
//		// TODO Auto-generated method stub
//		return extent;
//	}
//
//
//	@Override
//	public Polylist conceptualize() throws ThinklabException {
//	
//		return Polylist.list(
//				TimePlugin.TEMPORALGRID_TYPE_ID,
//				Polylist.list(TimePlugin.STARTS_AT_PROPERTY_ID, start.toString()),
//				Polylist.list(TimePlugin.ENDS_AT_PROPERTY_ID, end.toString()),
//				Polylist.list(TimePlugin.STEP_SIZE_PROPERTY_ID, step + " ms"));
//	}
//
//
//	@Override
//	public IExtent getExtent(int granule) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//	@Override
//	public IDatasourceTransformation getDatasourceTransformation(
//			IConcept mainObservable, IExtent extent) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//	@Override
//	public IExtent and(IExtent extent) throws ThinklabException {
//		
//		/*
//		 * TODO implement clipping with time intervals
//		 */
//		if (! (extent instanceof RegularTimeGridExtent)) {
//			throw new ThinklabValidationException("time grids can only be merged with other time grids");
//		}
//		return this.intersection((RegularTimeGridExtent) extent);
//		
//	}
//
//
//	@Override
//	public IExtent constrain(IExtent extent) throws ThinklabException {
//		// TODO IMPLEMENT CORRECTLY
//		return and(extent);
//	}
//
//
//	@Override
//	public String getSignature() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//	@Override
//	public boolean contains(ITopologicallyComparable o)
//			throws ThinklabException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//
//	@Override
//	public boolean intersects(ITopologicallyComparable o)
//			throws ThinklabException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//
//	@Override
//	public boolean overlaps(ITopologicallyComparable o)
//			throws ThinklabException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//
//	@Override
//	public IExtent or(IExtent myExtent) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//	@Override
//	public IExtent getAggregatedExtent() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public  Collection<org.integratedmodelling.corescience.interfaces.Pair<String, Integer>> getStateLocators(int index) {
//
//		ArrayList<Pair<String,Integer>> ret = null;
//		
//		if (index > 0) {
//			ret = new ArrayList<Pair<String,Integer>>();
//			// previous
//			ret.add(new Pair<String, Integer>("previous", index-1));
//		}
//		return ret;
//	}
//
//	@Override
//	public boolean checkDomainDiscontinuity() throws ThinklabException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public IExtent intersection(IExtent extent) throws ThinklabException {
//		// TODO Auto-generated method stub
//		return extent;
//	}
//
//	@Override
//	public IExtent force(IExtent extent) throws ThinklabException {
//		// TODO Auto-generated method stub
//		return extent;
//	}
//
//	@Override
//	public IExtent union(IExtent extent) throws ThinklabException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IConcept getValueType() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void setValue(int index, Object o) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public Object getRawData() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public double[] getDataAsDoubles()  {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public double getDoubleValue(int index) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public Metadata getMetadata() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IConcept getObservableClass() {
//		return TimePlugin.TimeGrid();
//	}
//
//	@Override
//	public IContext getObservationContext() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Restriction getConstraint(String operator) throws ThinklabException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IState aggregate(IConcept concept) throws ThinklabException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public AggregationParameters getAggregationParameters(IConcept observable,
//			Unit unit) throws ThinklabException  {
//		
//		AggregationParameters ret = new AggregationParameters(observable, unit);
//		ret.aggregationOperator = IOperator.AVG;
//		ret.aggregatedUnit = unit;
//		ret.aggregatedNature = PhysicalNature.INTENSIVE;
//		
//		if (CoreScience.isExtensive(observable)) {
//			
//			/*
//			 * determine cell area and conversion factor to 
//			 * turn density into quantity
//			 */
//			Unit sd = unit.getTimeExtentUnit();
//			Unit rf = new Unit("sec");
//			double um2 = rf.convert(1.0, sd);
//			final double cnv = (double)getGranuleSize()/um2;
//			
//			ret.aggregatedNature = PhysicalNature.EXTENSIVE;
//			ret.aggregationOperator = IOperator.SUM;
//			ret.aggregator = new Aggregator() {
//				@Override
//				public double getAggregationFactor(int granule) {
//					return cnv;
//				}
//			};
//			
//			/*
//			 * eliminate the areal term from the aggregated unit
//			 */
//			ret.aggregatedUnit = 
//				new Unit(unit.getUnit().times(sd.getUnit().inverse()));
//			
//		}
//		
//		return ret;
//	}
//
//	@Override
//	public boolean isSpatiallyDistributed() {
//		return false;
//	}
//
//	@Override
//	public boolean isTemporallyDistributed() {
//		return getValueCount() > 1;
//	}
//
//	@Override
//	public boolean isCovered(int granule) {
//		// we have no gaps
//		return true;
//	}
//
//	@Override
//	public IDataSource getDatasource() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IContext getContext() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IInstance getObservable() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IObserver getObserver() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Collection<IObservation> getDependencies() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IObservation contextualize(IContext context)
//			throws ThinklabException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Set<IInstance> getObservables() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public LanguageElement getLanguageElement() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getId() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getNamespace() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getName() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IProperty getContextProperty() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<IObservation> getContextObjects() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public int getMultiplicity() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public boolean contains(IExtent o) throws ThinklabException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean overlaps(IExtent o) throws ThinklabException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean intersects(IExtent o) throws ThinklabException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public IExtent collapse() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IRestriction getConstraint(IOperator operator)
//			throws ThinklabException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Collection<Pair<String, Integer>> getStateLocators(int index) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public boolean isDiscontinuous() throws ThinklabException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public AggregationParameters getAggregationParameters(IConcept concept,
//			IUnit unit) throws ThinklabException {
//		// TODO Auto-generated method stub
//		return null;
//	}
	

	@Override
	public IConcept getDomainConcept() {
		return Time.get().TimeDomain();
	}

	@Override
	public IConcept getStateType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getCoverage(ITopologicallyComparable<?> obj) {
		// TODO Auto-generated method stub
		return 0;
	}

}
