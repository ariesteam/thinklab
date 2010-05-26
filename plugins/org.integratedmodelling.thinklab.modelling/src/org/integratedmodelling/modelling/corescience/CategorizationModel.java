package org.integratedmodelling.modelling.corescience;

import java.util.Collection;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.modelling.DefaultDynamicAbstractModel;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;

public class CategorizationModel extends DefaultDynamicAbstractModel {

	@Override
	public String toString() {
		return ("categorization(" + getObservable() + ")");
	}

	
	public void setCategories(Object categories) {
		System.out.println("categories: " + categories);
		// TODO do something
	}
	
	@Override
	public void validateMediatedModel(IModel model)
			throws ThinklabValidationException {
		super.validateMediatedModel(model);
		// a ranking can mediate another ranking or a measurement
		if (! (model instanceof CategorizationModel)) {
			throw new ThinklabValidationException("categorization models can only mediate other categorizations");
		}
	}

	@Override
	protected Object validateState(Object state)
			throws ThinklabValidationException {
		return state instanceof String ? state : state.toString();
	}

	@Override
	public IConcept getCompatibleObservationType(ISession session) {
		return CoreScience.Categorization();
	}

	@Override
	public IModel getConfigurableClone() {
		CategorizationModel ret = new CategorizationModel();
		ret.copy(this);
		// TODO copy categories when we store them
		return ret;
	}

	@Override
	public Polylist buildDefinition(IKBox kbox, ISession session, Collection<Topology> extents) throws ThinklabException {

		Polylist def = Polylist.listNotNull(
				CoreScience.CATEGORIZATION,
				(id != null ? 
					Polylist.list(CoreScience.HAS_FORMAL_NAME, id) :
					null),				/*
				 * TODO add scale attributes, possibly units
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
		// nothing to do	
	}

}
