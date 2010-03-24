package org.integratedmodelling.geospace.agents;

import org.ascape.model.Scape;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.modelling.interfaces.IModel;

/**
 * A world for Thinklab agents to be in. We use a lattice but map it to the spatial topology of the 
 * underlying world model, so it doesn't need to mean that space literally, although visualization
 * will be proper if the underlying world maps to a grid.
 * 
 * @author Ferdinando
 *
 */
public class ThinkScape extends Scape {

	private static final long serialVersionUID = -8666426017903754905L;
	private IModel worldModel;

	/**
	 * This serves as the kbox for all agents in this world. It is updated whenever an agent modifies the
	 * world or the context is changed.
	 */
	private IObservation world; 
	
	Scape topology = null;
	
	public ThinkScape(IModel world) {
		this.worldModel = world;
	}
	
	/* (non-Javadoc)
	 * @see org.ascape.model.Scape#createScape()
	 */
	@Override
	public void createScape() {
		super.createScape();
	}

}
