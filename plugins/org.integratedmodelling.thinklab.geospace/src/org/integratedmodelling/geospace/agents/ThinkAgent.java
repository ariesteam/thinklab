package org.integratedmodelling.geospace.agents;

import org.ascape.model.CellOccupant;

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

	private static final long serialVersionUID = 6817729294716016787L;

	/* (non-Javadoc)
	 * @see org.ascape.model.Cell#initialize()
	 */
	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		super.initialize();
	}

	
	
}
