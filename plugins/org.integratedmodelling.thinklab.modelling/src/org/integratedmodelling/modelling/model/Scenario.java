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
package org.integratedmodelling.modelling.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.interfaces.IModelForm;
import org.integratedmodelling.thinklab.exception.ThinklabException;
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
	private IContext context;
	private boolean isPublic;
	
	public void dump(PrintStream out) {
		out.println("scenario " + getId());
		for (IModel m : models) {
			((DefaultAbstractModel)m).dump(out, 3);
		}
	}
	
	public void addModel(Object model, Map<?,?> metadata, Object editableDesc) {
		
		if (model instanceof IModel) {
			models.add((IModel)model);
		} else if (model instanceof IContext) {
			this.context = (IContext)model;
		}
		editableData.add(editableDesc);
	}
	
	public IContext getContext() {
		return context;
	}
	
	public boolean isPublic() {
		return isPublic;
	}
	
	public void applyClause(String keyword, Object argument)
			throws ThinklabException {

		// System.out.println(this + "processing clause " + keyword + " -> " +
		// argument);

		if (keyword.equals(":as")) {
		} else if (keyword.equals(":when")) {
		} else if (keyword.equals(":public")) {
			this.isPublic = (Boolean)argument;
		} else if (keyword.equals(":optional")) {
		} else if (keyword.equals(":required")) {
		} else if (keyword.equals(":agent")) {
		} else {
		}
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
