package org.integratedmodelling.modelling.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.modelling.interfaces.IModel;
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
public class Agent extends DefaultMutableTreeNode  {

	protected IContext context = null;
	ArrayList<IModel> models = null;
	protected IConcept observable = null;
	protected String observableId = null;
	protected String name = null; 
	
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
	
	protected void copy(Agent agent) {
		worldModel = agent.worldModel;
		kbox = agent.kbox;
		session = agent.session;
		description = agent.description;
		rules = agent.rules;
		models = agent.models;
		observable = agent.observable;
		observableId = agent.observableId;
		name = agent.name;
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
		this.name = id;
	}

	public String getId() {
		return name;
	}

//	@Override
//	public void setModel(IModel... model) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public IContext onContextChanged() {
//		// TODO check if session allows storage. If so, store previous context before going to the
//		// next.
//		return null;
//	}
//
//	@Override
//	public Collection<IAgent> getChildrenAgents() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Collection<IAgent> getParentAgents() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	
}
