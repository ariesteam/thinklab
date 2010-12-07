package org.integratedmodelling.modelling;

import java.util.ArrayList;
import java.util.Map;

import org.integratedmodelling.modelling.corescience.ObservationModel;
import org.integratedmodelling.modelling.interfaces.IModel;

/**
 * A scenario is an identification model containing observables that can be
 * swapped for others in a model. The form allows a simpler specification
 * of dependencies, which are used differently.
 * 
 * @author Ferdinando Villa
 *
 */
public class Scenario extends ObservationModel {

	ArrayList<IModel> models = new ArrayList<IModel>();
	ArrayList<Object> editableData = new ArrayList<Object>();
	private String description;
	
	public void setDescription(String s) {
		description = s;
	}
	
	public void setId(String s) {
		id = s;
	}
	
	public void addModel(IModel model, Map<?,?> metadata, Object editableDesc) {
		
		models.add(model);
		editableData.add(editableDesc);
	}
	
	public String getDescription() {
		return description;
	}
	
	/**
	 * Add observables that were not defined, substitute those
	 * that were with the incoming ones.
	 * 
	 * @param scenario
	 */
	public void merge(Scenario scenario) {

		for (IModel m : scenario.models) {
			int i = 0;
			for (IModel om : models) {
				if (m.getObservableClass().is(om.getObservableClass())) {
					models.set(i, m);
					break;
				}
				i++;
			}
			if (i== models.size()) {
				models.add(m);
				editableData.add(((Model)m).editable);
			}
		}
	}

}
