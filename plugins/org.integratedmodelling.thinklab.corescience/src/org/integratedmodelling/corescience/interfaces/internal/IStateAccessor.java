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
package org.integratedmodelling.corescience.interfaces.internal;

import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * Conceptual models are asked to create objects of this type to compile access to states into
 * contextualizers. Conceptual models that don't have access to datasources are expected to use 
 * an accessor to implement mediation of their dependencies.
 * 
 * In case the observation depends on others, the accessor will be notified of the observables that it 
 * depends upon in the sequence known to the contextualizer, so that they can be matched to what is 
 * known to the CM (e.g. variable names in expressions) and they will be retrievable at contextualization 
 * using their notification index.
 * 
 * @author Ferdinando
 *
 */
public interface IStateAccessor {

	/**
	 * Notifies that the observation whose state we must provide access to depends on another
	 * observation of this observable, and that the observable will be available as the given
	 * type. Called in the same order that contextualization will use (e.g. to know where on 
	 * the stack parameters are found). 
	 * 
	 * Communicates whether the state of the passed observable is necessary to access the
	 * given state by returning a boolean.
	 * 
	 * If the obs doesn't know what to do with the observable, it should throw an exception here.
	 * @param o 
	 * @param formalName the id of this dependency, so that we can link in code to something other
	 * 	      than the observable concept
	 *  
	 * @return true if the observable's state really needs to be passed at contextualization. Used
	 * to optimize contextualization.
	 * @throws ThinklabException TODO
	 */
	public boolean notifyDependencyObservable(IObservation o, IConcept observable, String formalName) throws ThinklabException;
	
	/**
	 * If notifyDependencyObservable has returned true for this observable, a register will be allocated
	 * and communicated through a call to this one, so that its value can be accessed using the register
	 * number (as a literal of given state type) when the state we're providing access to is requested.
	 * 
	 * If there's anything wrong with the type, throw an exception, although this should not happen
	 * and the accessor should be prepared to deal with the type returned by the CM.
	 * @param observation TODO
	 * @param stateType
	 * @param newRegister
	 * 
	 * @throws ThinklabException TODO
	 */
	public void notifyDependencyRegister(IObservation observation, IConcept observable, int register, IConcept stateType) throws ThinklabException;
	
	/**
	 * Compute or retrieve the value. The passed array contains values
	 * for the notified dependency observables, in the type notified
	 * previously.
	 * 
	 * @param index the global index of the overall context. The latter is known to the getAccessor() and getMediator()
	 * 	      methods of the observations that create the accessor, and is also passed to the notifyState method, 
	 *        so it can be passed to it if necessary. Note that this is the OVERALL index, not the local (own) index, so
	 *        it is wrong to use it as the index in a local data array. If used, this should be mediated
	 *        through a context mapper created in notifyState. Most of the time you don't need to, as 
	 *        a simple sequential fill of the storage array is enough; this parameter is used by computing
	 *        states so they can mediate to dependency states that are not in the same context.
	 *        
	 * @param registers
	 * @return
	 */
	public Object getValue(int overallContextIndex, Object[] registers);

	/**
	 * returning true means that the value returned by getValue() does not change 
	 * and is known before any computation starts. Of course it also means
	 * that registers can safely be null in any call to getValue(). Compilers will
	 * inline the value and discard the accessor, exposing the value register so that
	 * compilation can be run again with different values.
	 * 
	 * @return
	 */
	public boolean isConstant();

	/**
	 * The compiler will pass the state, the overall context and the state's own context when
	 * the state is created. NOTE: this will be called with a null for the state if the compiler
	 * doesn't need the state to be stored. If the accessor needs to remember state, it should create
	 * suitable private storage.
	 * @param dds
	 * @throws ThinklabException 
	 */
	public void notifyState(IState dds, IObservationContext overallContext, IObservationContext ownContext) throws ThinklabException;

}