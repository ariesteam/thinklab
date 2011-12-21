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
package org.integratedmodelling.modelling.annotation;

import java.util.Set;

import org.integratedmodelling.modelling.interfaces.IModelForm;
import org.integratedmodelling.modelling.model.ModelFactory;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.NameGenerator;
import org.integratedmodelling.utils.Polylist;

public class Annotation implements IModelForm {

	String _namespace;
	String _id;
	String _description;
	
	protected Polylist observation;
	protected IConcept observable = null;
	protected Polylist observableSpecs = null;

	public Annotation(String namespace) {
		this._namespace = namespace;
		this._id = NameGenerator.newName("ann");
	}
	
	public void add(Polylist obs, Object mlist) {
		this.observation = obs;
	}
	
	public void setObservable(Object observableOrModel)
			throws ThinklabException {

		if (observableOrModel instanceof IConcept) {
			this.observable = (IConcept) observableOrModel;
			this.observableSpecs = Polylist.list(this.observable);
		} else {
			this.observable = ModelFactory.annotateConcept(_namespace, observableOrModel.toString());
			this.observableSpecs = Polylist.list(this.observable);
		}
	}
	
	@Override
	public String getId() {
		return _id;
	}

	@Override
	public String getNamespace() {
		return _namespace;
	}

	@Override
	public String getName() {
		return _namespace + "/" + _id;
	}

	@Override
	public Set<IConcept> getObservables() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public void setDescription(String s) {
		this._description = s;
	}

	public void setName(String name) {
		String[] x = name.split("/");
		this._namespace = x[0];
		this._id = x[1];
	}
}
