package org.integratedmodelling.modelling.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import org.ascape.model.Agent;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;

import clojure.lang.IFn;

/**
 * A generic Thinklab agent occupies a cell of a Thinkspace, non-exclusively. The cell doesn't need to
 * correspond to a fixed topology, but is just a proxy for the actual observation context of the 
 * agent. Each agent has its own model and its own context, which must map on the "root" context of
 * the thinkscape and acts as an observation filter on the world's observation. Different subclasses
 * of ThinkAgent implement different localities of observation on the existing topologies - space, time
 * or any conceptual dimension (e.g. different opinions...)
 * 
 * ThinkAgent proxies an Ascape agent.
 * 
 * @author Ferdinando
 *
 */
public class ThinkAgent  extends DefaultMutableTreeNode {

	Agent _agent;
	
	ArrayList<IModel> models = null;
	protected IConcept observable = null;
	protected String observableId = null;
	protected String name = null; 
	
	IFn _update = null;
	
	protected Polylist observableSpecs = null;
	
	/*
	 * Any clause not intercepted by applyClause becomes metadata, which is communicated
	 * to the observation created. 
	 */
	protected HashMap<String, Object> metadata = new HashMap<String, Object>();
	private String description;

	private static final long serialVersionUID = -8666426017903754905L;

	/**
	 * This serves as the kbox for all agents in this world. It is updated whenever an agent modifies the
	 * world or the context is changed.
	 */
	private IObservation world; 
	
	/*
	 * the model that generated our world, which we may re-run any time we need to observe it again.
	 */
	IModel worldModel;
	Topology[] topologies;
	ISession session;
	IKBox kbox;
	
	protected void copy(ThinkAgent agent) {
		worldModel = agent.worldModel;
		kbox = agent.kbox;
		session = agent.session;
		description = agent.description;
		_update = agent._update;
		models = agent.models;
		observable = agent.observable;
		observableId = agent.observableId;
		name = agent.name;
	}
	
	@Override
	public Object clone() {
		ThinkAgent ret = new ThinkAgent();
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
		
		// System.out.println(this + "processing clause " + keyword + " -> " + argument);
		
		if (keyword.equals(":update")) {
			_update = (IFn) argument;
		} else if (keyword.equals(":random-walk")) {
			// TODO
		} else if (keyword.equals(":movement")) {
			// TODO
		} else if (keyword.equals(":death")) {
			// TODO
		} else if (keyword.equals(":metabolism")) {
			// TODO
		} else if (keyword.equals(":random-move")) {
			// TODO
		} else if (keyword.equals(":play")) {
			// TODO
		} else if (keyword.equals(":initialize")) {
			// TODO
		} else {
			metadata.put(keyword.substring(1), argument);
		}
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
		this.name = id;
	}

	public String getId() {
		return name;
	}
	
}
