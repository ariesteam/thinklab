package org.integratedmodelling.modelling.corescience;

import java.util.Collection;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.modelling.DefaultAbstractModel;
import org.integratedmodelling.modelling.DefaultDynamicAbstractModel;
import org.integratedmodelling.modelling.DefaultStatefulAbstractModel;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;

public class RankingModel extends DefaultDynamicAbstractModel {

	boolean isBinary = false;
	boolean isCategorical = false;
	boolean isProbabilistic = false;
	boolean isInteger = false;
	boolean isScale = false;
	double  scaleMin = -1.0;
	double  scaleMax = -1.0;
	
	@Override
	protected void copy(DefaultStatefulAbstractModel model) {

		super.copy(model);

		isBinary = ((RankingModel)model).isBinary;
		isCategorical = ((RankingModel)model).isCategorical;
		isProbabilistic = ((RankingModel)model).isProbabilistic;
		isInteger = ((RankingModel)model).isInteger;
		isScale = ((RankingModel)model).isScale;
		scaleMin = ((RankingModel)model).scaleMin;
		scaleMax = ((RankingModel)model).scaleMax;
	}
	
	@Override
	public void applyClause(String keyword, Object argument)
			throws ThinklabException {

		if (keyword.equals(":binary"))
			isBinary = (Boolean)argument;
		else if (keyword.equals(":numeric-classification"))
			isCategorical = (Boolean)argument;
		else if (keyword.equals(":probabilistic"))
			isProbabilistic = (Boolean)argument;
		else if (keyword.equals(":min"))
			scaleMin = ((Number)argument).doubleValue();
		else if (keyword.equals(":max"))
			scaleMax = ((Number)argument).doubleValue();
		else if (keyword.equals(":integer"))
			isInteger = (Boolean)argument;
		else if (keyword.equals(":scale"))
			isScale = (Boolean)argument;
		else
			super.applyClause(keyword, argument);
	}

	public RankingModel() {
		this.metadata.put(Metadata.CONTINUOUS, Boolean.TRUE);
	}
	
	@Override
	public String toString() {
		return ("ranking(" + getObservable() + ")");
	}
	
	@Override
	public void validateMediatedModel(IModel model)
			throws ThinklabValidationException {
		// a ranking can mediate another ranking or a measurement
		super.validateMediatedModel(model);
		if (! ((model instanceof MeasurementModel) || (model instanceof RankingModel))) {
			throw new ThinklabValidationException("ranking models can only mediate ranking or measurements");
		}
	}

	@Override
	protected Object validateState(Object state)
			throws ThinklabValidationException {
		return state instanceof Number ? state : Double.parseDouble(state.toString());
	}

	@Override
	public IConcept getCompatibleObservationType(ISession session) {
		return CoreScience.Ranking();
	}

	@Override
	public IModel getConfigurableClone() {
		RankingModel ret = new RankingModel();
		ret.copy(this);
		return ret;
	}

	@Override
	public Polylist buildDefinition(IKBox kbox, ISession session, Collection<Topology> extents) throws ThinklabException {

		/*
		 * TODO choose observation class according to derivative, probability etc.
		 */
		Polylist def = Polylist.listNotNull(
				((dynSpecs == null && changeSpecs == null && derivativeSpecs == null) ?
							CoreScience.RANKING : 
							"modeltypes:DynamicRanking"),
				(id != null ? 
					Polylist.list(CoreScience.HAS_FORMAL_NAME, id) :
					null),
				(dynSpecs != null ? 
					Polylist.list(":code", dynSpecs) : null),
				(changeSpecs != null ? 
					Polylist.list(":change", changeSpecs) : null),
				(derivativeSpecs != null ? 
					Polylist.list(":derivative", derivativeSpecs) : null),
				((dynSpecs != null || changeSpecs != null || derivativeSpecs != null)? 
							Polylist.list(
								"modeltypes:hasExpressionLanguage", 
								this.lang.equals(language.CLOJURE) ? "clojure" : "mvel")
								: null), 
				(this.distribution != null ? 
					Polylist.list(":distribution", this.distribution) : 
					null),
				(this.state != null ? 
					Polylist.list(CoreScience.HAS_VALUE, this.state) : 
					null), 
				getImplicitExtents(extents),

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
		// TODO check that scale has min/max, that we don't have scale if 
		// we are binary or categories, and that probabilistic specs and
		// states are OK and match
	}
	
	@Override
	protected IModel validateSubstitutionModel(IModel m) {
		
		IModel ret = null;
		
		if (m instanceof RankingModel) {
			// FIXME mediate to substitute ranks and scale if necessary.
			try {
				ret = (IModel) ((DefaultAbstractModel)m).clone();
				} catch (CloneNotSupportedException e) {
			}
		}
		
		return ret;
	}

}
