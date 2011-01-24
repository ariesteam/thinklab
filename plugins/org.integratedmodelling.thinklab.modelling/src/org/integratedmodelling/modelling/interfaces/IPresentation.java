package org.integratedmodelling.modelling.interfaces;

import java.util.Properties;

import org.integratedmodelling.modelling.storyline.Storyline;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

public interface IPresentation {

	/**
	 * Render the full storyline we've been initialized with, appropriately
	 * handling its hierarchical structure for the medium.
	 * 
	 * @throws ThinklabException
	 */
	public abstract void render() throws ThinklabException;

	/**
	 * Render only the given concept within the storyline - e.g. a single
	 * page in the associated visualization.
	 * 
	 * @param concept
	 * @throws ThinklabException
	 */
	public abstract void render(IConcept concept) throws ThinklabException;
	
	/**
	 * Communicate the storyline we'll have to work with. It should not be
	 * modified by us - if there's no visualization in it, just do not
	 * visualize data.
	 * 
	 * @param storyline
	 * @param properties TODO
	 */
	public abstract void initialize(Storyline storyline, Properties properties);
	
}
