package org.integratedmodelling.thinklab.modelling.internal;

import java.util.Collection;

import org.integratedmodelling.thinklab.api.knowledge.IConcept;

/**
 * During contextualization, a concept mapper is created for each observation whose
 * state needs to be accessed. Because the multiplicities along different extents may
 * differ in different observations being contextualized together, a context mapper is
 * created to map the overall context to the harmonized extents of each particular
 * observation. The mapper has the role of translating the current context index in the
 * overall context to the correspondent context index in the observation it's assigned
 * to. It also knows whether moving from an overall context state to the next has caused a 
 * change in state in any of the extents, and can return the list of extents that
 * have changed.
 *
 * @author Ferd
 *
 */
public interface IContextMapper {
	
	/**
	 * Return the linear index that the n-th state of the overall context maps to in 
	 * the observation assigned to us.
	 * 
	 * @param index
	 * @return
	 */
	int map(int index);

	/**
	 * This one will be called as many times as there are states in the overall context. Each
	 * time, the current index should be set to the correspondent one in the mapped observation,
	 * and if the change has determined a shift in the state of one or more extents, the correspondent
	 * concepts should be returned so the accessor can take any necessary action (e.g. calling
	 * update() if time has shifted and/or move() if space has shifted).
	 * 
	 * @return
	 */
	public Collection<IConcept> increment();
	
	/**
	 * Return the current index of the context in the observation we map to after the last
	 * increment().
	 * 
	 * @return
	 */
	public int current();
}
