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
package org.integratedmodelling.modelling.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.interfaces.IModelForm;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;

import clojure.lang.IFn;

/**
 * 
 * @author Ferdinando
 *
 */
public class Agent extends DefaultMutableTreeNode implements IModelForm {

	protected IContext context = null;
	ArrayList<IModel> models = null;
	protected IConcept observable = null;
	protected String observableId = null;
	protected String id = null; 
	
	protected Polylist observableSpecs = null;
	
	/*
	 * our behavior is a map of named closures which will get this as their single parameter
	 */
	protected HashMap<String, IFn> rules = new HashMap<String, IFn>();
	
	/*
	 * Any clause not intercepted by applyClause becomes metadata, which is communicated
	 * to the observation created. 
	 */
	protected Metadata metadata = new Metadata();
	private String description;

	private static final long serialVersionUID = -8666426017903754905L;
	
	/*
	 * the model that generated our world, which we may re-run any time we need to observe it again.
	 */
	IModel worldModel;
	Topology[] topologies;
	ISession session;
	IKBox kbox;
	private String namespace;
	private HashSet<IConcept> observables;
	
	protected void copy(Agent agent) {
		worldModel = agent.worldModel;
		kbox = agent.kbox;
		session = agent.session;
		description = agent.description;
		rules = agent.rules;
		models = agent.models;
		observable = agent.observable;
		observableId = agent.observableId;
		id = agent.id;
		namespace = agent.namespace;
	}
	
	@Override
	public Object clone() {
		Agent ret = new Agent();
		ret.copy(this);
		return ret;
	}
	
	public void initialize(IModel world, IKBox kbox, ISession session, Topology ... context) {
		
		this.kbox = kbox;
		this.session = session;
		this.topologies = context;
		this.worldModel = world;
	}

	public void applyClause(String keyword, Object argument) throws ThinklabException {
		
		if (argument instanceof IFn) {
			String ruleId = keyword.substring(1);
			addRule(ruleId, (IFn)argument);
		} else {
			metadata.put(keyword.substring(1), argument);
		}
	}
	
	private void addRule(String ruleId, IFn closure) {
		rules.put(ruleId, closure);
	}

	public void setDescription(String s) {
		this.description = s;
	}
	
	public String getDescription() {
		return this.description ;
	}
	public void setObservable(Object observableOrModel) throws ThinklabException {
		
		if (observableOrModel instanceof IConcept) {
			this.observable = (IConcept) observableOrModel;
			this.observableSpecs = Polylist.list(this.observable);
			this.observableId = this.observable.toString();
		} else if (observableOrModel instanceof Polylist) {
			this.observableSpecs = (Polylist)observableOrModel;
			this.observableId = this.observableSpecs.first().toString();
		} else {
			this.observableId = observableOrModel.toString();
		}
	}

	/**
	 * Can be called once or more; models are passed after being configured with their
	 * clauses. They may have :when clauses to condition them to a particular context
	 * state, or have the implicit :when :observable clause which makes them apply
	 * as default in order of declaration, until the context is covered.
	 */
	public void addModel(IModel model, Map<?,?> metadata) {
		
		// System.out.println("setting unconditional " + model);
		if (models == null) {
			models = new ArrayList<IModel>();
		}
		
		if (metadata != null) {
			// TODO use it
		}
		
		models.add(model);
	}
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getNamespace() {
		return this.namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	public void setName(String name) {
		String[] x = name.split("/");
		this.namespace = x[0];
		this.id = x[1];
	}
	
	@Override
	public String getName() {
		return namespace + "/" + id;
	}
	
	@Override
	public boolean equals(Object obj) {
		return 
			obj instanceof Agent ? 
				getName().equals(((IModelForm)obj).getName()) : false;
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public Set<IConcept> getObservables() {
		if (this.observables == null) {
			this.observables = new HashSet<IConcept>();
		}
		return this.observables;
	}


}
