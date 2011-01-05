package org.integratedmodelling.modelling.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.datastructures.IntelligentMap;

/**
 * Mock-SPAN to spank one ass and save another's. Basically a generic agent model
 * driver. Hopefully rendered useless asap.
 * 
 * @author ferdinando.villa
 *
 */
public abstract class SPANK {

	/*
	 * catalog-related stuff
	 */
	static IntelligentMap<Class<?>> catalog = new IntelligentMap<Class<?>>();
	
	protected IState source;
	protected IState use;
	protected IState sink;
	protected IContext context;
	
	/*
	 * space-related 
	 */
	protected int width, height;
	protected double xmeters, ymeters;
	protected GridExtent grid;

	protected HashMap<String, Object> parameters;
	
	public abstract class Agent {
		
		protected int xpos, ypos;
		
		protected Agent(int x, int y) {
			this.xpos = x;
			this.ypos = y;
		}
		
		protected void run() throws ThinklabException {
			
			while (!done()) {
				takeStock();
				move();
			}
		}

		protected abstract void takeStock() throws ThinklabException;
		protected abstract void move() throws ThinklabException;
		protected abstract boolean done();
		
		@Override
		public String toString() {
			return "@(" + xpos + "," + ypos + ")";
		}
	}
	
	public void initialize(IContext context, HashMap<String, Object> parameters) throws ThinklabException {
		setupSpace(this.context = context);
		this.parameters = parameters;
	}
	
    /**
     * This will return anything passed as a keyword in the 
     * model form. Use the keyword string with the ":" prefix. All args
     * are eval'ed at compile time before being stored.
     * 
     * @param s
     * @return
     */
    protected Object getParameter(String s) {
    	return parameters.get(s);
    }
	
	
    /**
     * This will return anything passed as a keyword in the 
     * model form. Use the keyword string with the ":" prefix. All args
     * are eval'ed at compile time before being stored. If parameter is 
     * not there, passed default is used.
     * 
     * @param s
     * @return
     */
    protected Object getParameter(String s, Object def) {
    	Object ret = parameters.get(s);
    	return ret == null ? def : ret;
    }
	
	/*
	 * if redefined, make sure you call the parent first
	 */
	protected void setupSpace(IContext context) throws ThinklabException {
		
		IExtent space = context.getSpace();
		if (! (space instanceof GridExtent))
			throw new ThinklabValidationException("can't spank a non-grid context");
	
		this.grid = (GridExtent)space; 
		this.width = grid.getXCells();
		this.height = grid.getYCells();
		this.xmeters = grid.getCellWidthMeters();
		this.ymeters = grid.getCellHeightMeters();
		
		extractStates(context);
	}

	public Collection<IState> run() throws ThinklabException {
		
		ArrayList<IState> ret = createStates();
		
		for (Agent agent : createAgents()) {
			agent.run();
		}
		
		return ret;
	}

	protected abstract void extractStates(IContext context) throws ThinklabException;

	protected abstract ArrayList<IState> createStates() throws ThinklabException;

	protected abstract Collection<Agent> createAgents() throws ThinklabException;
	
	/*
	 * also
	 * serves as a catalog of SPANK model classes keyed by observation type.
	 */
	public static SPANK getSpankModel(IConcept concept, IContext context, HashMap<String, Object> parameters) throws ThinklabException {
		
		SPANK ret = null;
		Class<?> cls = catalog.get(concept);
		if (cls != null) {
			try {
				ret = (SPANK) cls.newInstance();
			} catch (Exception e) {
				throw new ThinklabValidationException("error making a spank model for " + concept);
			}
			ret.initialize(context, parameters);
		}
		
		if (ret == null)
			throw new ThinklabResourceNotFoundException("can't make a spank model for " + concept);
		
		return ret;
	}
	
	public static void registerSpankClass(IConcept c, Class<?> cls) {
		catalog.put(c, cls);
	}
	
}
