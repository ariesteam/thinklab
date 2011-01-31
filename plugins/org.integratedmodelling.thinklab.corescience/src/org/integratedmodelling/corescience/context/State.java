package org.integratedmodelling.corescience.context;

import java.util.Properties;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IDataSource;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.multidimensional.Average;
import org.integratedmodelling.multidimensional.IAggregator;
import org.integratedmodelling.multidimensional.Sum;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.datastructures.ConfigurableIntelligentMap;

/**
 * Methods to access states across contexts. Also serves as a factory for aggregators.
 * 
 * @author Ferdinando
 *
 */
public class State {

	public static final String AGGREGATOR_PROPERTY_PREFIX = "thinklab.aggregator.";
	
	public static class AggregatorMap extends  ConfigurableIntelligentMap<IAggregator> {
	
		public AggregatorMap() {
			
			super(AGGREGATOR_PROPERTY_PREFIX);
			try {
				this.registerProperty(
						KnowledgeManager.get().requireConcept(CoreScience.EXTENSIVE_PHYSICAL_PROPERTY), 
						Sum.class.toString()
					);
				this.registerProperty(
						KnowledgeManager.get().requireConcept(CoreScience.INTENSIVE_PHYSICAL_PROPERTY), 
						Average.class.toString()
					);
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);
			}
		}

		
		@Override
		protected IAggregator getObjectFromPropertyValue(String pvalue,
				Object[] parameters) throws ThinklabException {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	static AggregatorMap aggregators = new AggregatorMap();

	public static void loadAggregators(Properties properties) throws ThinklabException {
		aggregators.load(properties);
	}
	
	public static IAggregator getAggregator(IConcept c) throws ThinklabException {
		IAggregator ret = aggregators.get(c);
		if (ret == null)
			// ehm
			ret = new Sum();
		return ret;
	}
	
	/**
	 * A weird iterator of iterators, whose next() points to the iterator of state values
	 * in the desired context dimension, and each iterator correspond to all combinations
	 * of the remaining dimensions. So you can use it to iterate over all space states in
	 * an unknown observation by asking for an iterator over Geospace.SPACE, which will
	 * scan as many states as other dimensions (e.g. time) imply, giving back iterators
	 * for each object in different space states at each time slice.
	 * 
	 * @author Ferdinando
	 *
	 */
	 public class Iterator  implements java.util.Iterator<java.util.Iterator<Object>> {

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public java.util.Iterator<Object> next() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static IState aggregate(IState state, IConcept dimension) throws ThinklabException {
		
		IContext actx = state.getObservationContext().collapse(dimension);
		IAggregator agg = getAggregator(state.getObservableClass());
		
		return null;
	}

	public static IState aggregate(IState state) throws ThinklabException {

		IContext actx = state.getObservationContext().collapse(null);
		IAggregator agg = getAggregator(state.getObservableClass());

		return null;
	}

	public static IState distribute(Object value, IState state, IConcept dimension) throws ThinklabException {
		
		float ratio = 1.0f/(float)(state.getObservationContext().getMultiplicity(dimension));
		IAggregator agg = getAggregator(state.getObservableClass());
		value = agg.getDistributedValue(value, ratio);
		
		
		return null;
	}

	public static IState distribute(Object value, IState state) throws ThinklabException {
		
		float ratio = 1.0f/(float)(state.getObservationContext().getMultiplicity());
		IAggregator agg = getAggregator(state.getObservableClass());
		value = agg.getDistributedValue(value, ratio);
		
		
		return null;
	}
	
	public static Iterator iterator(IState state, IConcept dimensionLeft) {
		return null;
	}

	public int multiplicityExcept(IState state, IConcept dimensionLeft) {
		return 0;
	}
	
	public IDataSource<?> asDatasource(IState state) {
		return new DatasourceStateAdapter(state);
	}
}
