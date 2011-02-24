package org.integratedmodelling.modelling.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

import org.ascape.model.Agent;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.interfaces.IModelForm;
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
public class ThinkAgent  extends DefaultMutableTreeNode implements IModelForm {

	Agent _agent;
	
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

	private String namespace;
	private HashSet<IConcept> observables;
	
	protected void copy(ThinkAgent agent) {
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

	public String getId() {
		return this.id;
	}
	
	@Override
	public String getNamespace() {
		return this.namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	@Override
	public boolean equals(Object obj) {
		return 
			obj instanceof ThinkAgent ? 
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
