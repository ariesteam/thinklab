package org.integratedmodelling.modelling.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.integratedmodelling.modelling.corescience.ObservationModel;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.interfaces.IModelForm;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * A scenario is an identification model containing observables that can be
 * swapped for others in a model. The form allows a simpler specification
 * of dependencies, which are used differently.
 * 
 * @author Ferdinando Villa
 *
 */
public class Scenario implements IModelForm {

	private String namespace;

	public Scenario(String namespace) {
		this.namespace = namespace;
	}

	ArrayList<IModel> models = new ArrayList<IModel>();
	ArrayList<Object> editableData = new ArrayList<Object>();
	private String name;
	private String id;
	private String description;
	private HashSet<IConcept> observables;

	public void addModel(IModel model, Map<?,?> metadata, Object editableDesc) {
		
		models.add(model);
		editableData.add(editableDesc);
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
	
	@Override
	public boolean equals(Object obj) {
		return 
			obj instanceof Scenario ? 
				getName().equals(((IModelForm)obj).getName()) : false;
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	public void setName(String name) {
		String[] x = name.split("/");
		this.name = name;
		this.namespace = x[0];
		this.id = x[1];
	}

	@Override
	public String getNamespace() {
		return this.namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String desc) {
		this.description = desc;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Set<IConcept> getObservables() {
		if (this.observables == null) {
			this.observables = new HashSet<IConcept>();
			for (IModel m : models)
				this.observables.add(m.getObservableClass());
		}
		return this.observables;
	}

}
