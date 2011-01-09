package org.integratedmodelling.modelling.corescience;

import java.util.Collection;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.model.DefaultAbstractModel;
import org.integratedmodelling.modelling.model.DefaultDynamicAbstractModel;
import org.integratedmodelling.modelling.model.DefaultStatefulAbstractModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;

public class MeasurementModel extends DefaultDynamicAbstractModel {

	String unitSpecs = null;
	boolean isProbabilistic = false;
	boolean isCount = false;
	
	@Override
	public void applyClause(String keyword, Object argument)
			throws ThinklabException {

		if (keyword.equals(":count"))
			isCount = (Boolean)argument;
		else if (keyword.equals(":probabilistic"))
			isProbabilistic = (Boolean)argument;
		else
			super.applyClause(keyword, argument);
	}
	
	@Override
	protected void copy(DefaultStatefulAbstractModel model) {
		super.copy(model);
		unitSpecs = ((MeasurementModel)model).unitSpecs;
		isProbabilistic = ((MeasurementModel)model).isProbabilistic;
		isCount = ((MeasurementModel)model).isCount;
	}

	@Override
	public String toString() {
		return ("measurement(" + getObservableClass() + "," + unitSpecs + ")");
	}

	public void setUnits(Object unitSpecs) {
		this.unitSpecs = unitSpecs.toString();
		this.metadata.put(Metadata.UNITS, this.unitSpecs);
		this.metadata.put(Metadata.CONTINUOUS, Boolean.TRUE);
	}

	@Override
	public Polylist buildDefinition(IKBox kbox, ISession session, IContext context, int flags)
			throws ThinklabException {
		
		if (id.equals("sky-emperor-harvest")) {
			System.out.println("STOPHERE");
		}
		
		/*
		 * choose observation class according to derivative, probability etc.
		 */
		Polylist def = Polylist.listNotNull(
				((dynSpecs == null && changeSpecs == null && derivativeSpecs == null) ? 
						CoreScience.MEASUREMENT : 
						"modeltypes:DynamicMeasurement"),
						Polylist.list(CoreScience.HAS_FORMAL_NAME, getLocalFormalName()), 
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
				(unitSpecs.contains(" ") ? 
						Polylist.list(CoreScience.HAS_VALUE, unitSpecs) : 
						Polylist.list(CoreScience.HAS_UNIT, unitSpecs)), 
				(this.state != null ? 
						Polylist.list(CoreScience.HAS_VALUE, this.state) : 
						null), 
				(this.distribution != null ? 
						Polylist.list(":distribution", this.distribution) : 
						null),
				(isMediating() ?
						null :
						Polylist.list(CoreScience.HAS_OBSERVABLE,
								Polylist.list(getObservableClass()))));

		return addImplicitExtents(def, context);
	}

	@Override
	public IConcept getCompatibleObservationType(ISession session) {
		return CoreScience.Measurement();
	}

	@Override
	public void validateMediatedModel(IModel model)
			throws ThinklabValidationException {
		super.validateMediatedModel(model);
		if (!(model instanceof MeasurementModel)) {
			throw new ThinklabValidationException(
					"measurement models can only mediate other measurements");
		}
	}

	@Override
	protected Object validateState(Object state)
			throws ThinklabValidationException {
		return state instanceof Double ? state : Double.parseDouble(state
				.toString());
	}

	@Override
	public IModel getConfigurableClone() {

		MeasurementModel ret = new MeasurementModel();
		ret.copy(this);
		ret.unitSpecs = unitSpecs;
		ret.isCount = isCount;
		ret.isProbabilistic = isProbabilistic;
		return ret;
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

	@Override
	protected IModel validateSubstitutionModel(IModel m) {
		
		IModel ret = null;
		
		if (m instanceof MeasurementModel) {
			// FIXME ensure substitution of units. This is a very dumb way to do it.
			if (!((MeasurementModel)m).unitSpecs.equals(this.unitSpecs)) {
				
				try {
					ret = (IModel) this.clone();
					((DefaultAbstractModel)ret).setMediatedModel(m);
				} catch (CloneNotSupportedException e) {
				}
				
			} else {
				try {
					ret = (IModel) ((DefaultAbstractModel)m).clone();
				} catch (CloneNotSupportedException e) {
				}
			}
		}
		
		return ret;
	}
	
}
