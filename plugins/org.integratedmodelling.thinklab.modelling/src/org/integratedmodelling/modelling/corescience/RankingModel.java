package org.integratedmodelling.modelling.corescience;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.modelling.DefaultDynamicAbstractModel;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;

public class RankingModel extends DefaultDynamicAbstractModel {

	Object unitSpecs = null;
	
	public RankingModel() {
		this.metadata.put(Metadata.CONTINUOUS, Boolean.TRUE);
	}
	
	@Override
	public String toString() {
		return ("ranking(" + getObservable() + ")");
	}

	public void setUnits(Object unitSpecs) {
		this.unitSpecs = unitSpecs;
	}
	
	@Override
	public void validateMediatedModel(IModel model)
			throws ThinklabValidationException {
		// a ranking can mediate another ranking or a measurement
		if (! ((model instanceof MeasurementModel) || (model instanceof RankingModel))) {
			throw new ThinklabValidationException("ranking models can only mediate ranking or measurements");
		}
	}

	@Override
	protected Object validateState(Object state)
			throws ThinklabValidationException {
		return state instanceof Double ? state : Double.parseDouble(state.toString());
	}

	@Override
	public IConcept getCompatibleObservationType(ISession session) {
		return CoreScience.Ranking();
	}

	@Override
	public IModel getConfigurableClone() {
		RankingModel ret = new RankingModel();
		ret.copy(this);
		ret.unitSpecs = unitSpecs;
		return ret;
	}

	@Override
	public Polylist buildDefinition(IKBox kbox, ISession session) throws ThinklabException {

		/*
		 * TODO choose observation class according to derivative, probability etc.
		 */
		Polylist def = Polylist.listNotNull(
				(dynSpecs == null ? CoreScience.RANKING : "modeltypes:DynamicRanking"),
				(id != null ? 
					Polylist.list(CoreScience.HAS_FORMAL_NAME, id) :
					null),
				(dynSpecs != null?
					Polylist.list("modeltypes:hasStateFunction", dynSpecs) :
					null),
				(dynSpecs != null?
					Polylist.list("modeltypes:hasExpressionLanguage", 	
						this.lang.equals(language.CLOJURE) ? "clojure" : "mvel") :
					null),
				(metadata != null ? 
					Polylist.list(":metadata", metadata) :
						null),
				/*
				 * TODO add scale attributes
				 */
				(isMediating() ? 
						null :
						Polylist.list(
								CoreScience.HAS_OBSERVABLE,
								Polylist.list(getObservable()))));
		
		return def;
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void validateSemantics(ISession session) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

}
