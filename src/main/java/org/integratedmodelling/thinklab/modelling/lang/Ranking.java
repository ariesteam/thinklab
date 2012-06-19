package org.integratedmodelling.thinklab.modelling.lang;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.lang.RankingScale;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IComputingAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IMediatingAccessor;
import org.integratedmodelling.thinklab.api.modelling.ISerialAccessor;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.api.modelling.parsing.IClassificationDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IRankingObserverDefinition;
import org.integratedmodelling.thinklab.modelling.lang.Measurement.MeasurementAccessor;
import org.integratedmodelling.thinklab.modelling.states.NumberState;

@Concept(NS.RANKING_OBSERVER)
public class Ranking extends Observer<Ranking> implements IRankingObserverDefinition {
	
	Type   _type = Type.RANKING;

	// default scale is unbounded
	RankingScale _scale = new RankingScale();
	
	@Override
	public Type getType() {
		return _type;
	}

	@Override
	public RankingScale getScale() {
		return _scale;
	}

	@Override
	public void setType(Type type) {
		_type = type;
	}

	@Override
	public void setScale(Number from, Number to) {
		_scale = new RankingScale(from, to);
	}

	@Override
	public Ranking demote() {
		return this;
	}

	@Override
	public IState createState(ISemanticObject<?> observable, IContext context) throws ThinklabException {
		return new NumberState(observable, context, this);
	}

	@Override
	public IAccessor getNaturalAccessor(IContext context) {
		
		if (getDependencies().size() > 0 || /* TODO check if expressions have been defined */ false)
			return new ComputingRankingAccessor();
		
		return new RankingAccessor();
	}

	/*
	 * -----------------------------------------------------------------------------------
	 * accessor - it's always a mediator, either to another measurement or to a datasource
	 * whose content was defined explicitly to conform to our semantics
	 * -----------------------------------------------------------------------------------
	 */
	public class RankingAccessor 
		implements ISerialAccessor, IMediatingAccessor {

		RankingAccessor _mediated;
		
		@Override
		public IConcept getStateType() {
			return Thinklab.DOUBLE;
		}

		@Override
		public void notifyMediatedAccessor(IAccessor accessor)
				throws ThinklabException {
			
			/*
			 * must be another measurement accessor, or a direct datasource.
			 */
			if (accessor instanceof MeasurementAccessor) {

				/*
				 * TODO
				 * check scale compatibility
				 */				
				_mediated = (RankingAccessor) accessor;
			}			
		}
		
		@Override
		public String toString() {
			return "[ranking: " + _scale + "]";
		}
		
		public RankingScale getRankingScale() {
			return _scale;
		}

		@Override
		public Object mediate(Object object) throws ThinklabException {
			
			if (object == null || (object instanceof Number && Double.isNaN(((Number)object).doubleValue())))
				return Double.NaN;
			
			return _mediated == null ?
					object :
					_scale.convert(((Number)object).doubleValue(), _mediated.getRankingScale());
		}

		@Override
		public Object getValue(int idx) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	
	class ComputingRankingAccessor extends RankingAccessor implements IComputingAccessor {

		@Override
		public void notifyDependency(ISemanticObject<?> observable, String key) {
		}

		@Override
		public void notifyExpectedOutput(ISemanticObject<?> observable,
				String key) {
		}

		@Override
		public void process(int stateIndex) throws ThinklabException {
		}

		@Override
		public void setValue(String inputKey, Object value) {
		}

		@Override
		public Object getValue(String outputKey) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}


	@Override
	public void setDiscretization(IClassificationDefinition classification) {
		// TODO Auto-generated method stub
		
	}

}
