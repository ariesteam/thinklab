package org.integratedmodelling.modelling.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.ascape.model.CellOccupant;
import org.integratedmodelling.modelling.DefaultAbstractModel;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.Polylist;

/**
 * A generic Thinklab agent occupies a cell of a Thinkspace, non-exclusively. The cell doesn't need to
 * correspond to a fixed topology, but is just a proxy for the actual observation context of the 
 * agent. Each agent has its own model and its own context, which must map on the "root" context of
 * the thinkscape and acts as an observation filter on the world's observation. Different subclasses
 * of ThinkAgent implement different localities of observation on the existing topologies - space, time
 * or any conceptual dimension (e.g. different opinions...)
 * 
 * @author Ferdinando
 *
 */
public class ThinkAgent extends CellOccupant {

	ArrayList<IModel> models = null;
	protected IConcept observable = null;
	protected String observableId = null;
	protected String name = null; 
	
	protected Polylist observableSpecs = null;
	private static final long serialVersionUID = 6817729294716016787L;
	
	/*
	 * Any clause not intercepted by applyClause becomes metadata, which is communicated
	 * to the observation created. 
	 */
	protected HashMap<String, Object> metadata = new HashMap<String, Object>();
	private String description;

	/* (non-Javadoc)
	 * @see org.ascape.model.Cell#initialize()
	 */
	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		super.initialize();
	}

	public void applyClause(String keyword, Object argument) throws ThinklabException {
		
		// System.out.println(this + "processing clause " + keyword + " -> " + argument);
		
		if (keyword.equals(":context")) {
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
