/**
 * ContextStateGenerator.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabCoreSciencePlugin.
 * 
 * ThinklabCoreSciencePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabCoreSciencePlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.corescience.contextualization;

import java.util.Iterator;

import org.integratedmodelling.corescience.interfaces.context.IContextStateGenerator;
import org.integratedmodelling.corescience.interfaces.context.IObservationContextState;
import org.integratedmodelling.corescience.utils.Ticker;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

public class ContextStateGenerator implements IContextStateGenerator {
	
	/**
	 * 
	 * @author Ferdinando Villa
	 * @date June 26, 2007
	 */
	public class ContextStateIterator implements Iterator<IObservationContextState> {

		ObservationContext context = null;
		Ticker ticker = null;
		ObservationContextState state = new ObservationContextState();
		boolean newit = true;
		
		protected ContextStateIterator(ObservationContext context, boolean useIndex) {
			this.context = context;
			this.ticker = new Ticker();
			
			for (IConcept c : context.getContextDimensions()) {
				try {
					ticker.addDimension(context.getMultiplicity(c));
					state.defineDimension(c);
					
				} catch (ThinklabException e) {
					// should not happen, if it does, too bad, we can't have exceptions 
					// thrown here.
					throw new ThinklabRuntimeException(e);
				}
			}
		}
		
		public boolean hasNext() {
			// FIXME
			return ticker.current() < ticker.last();
		}

		public IObservationContextState next() {

			// this ugly gimmick is intended to enable use of the ticker's current
			// states in the context state without having to copy them.
			if (!newit) {
				ticker.increment();
			}
			newit = false;
			
			// extract specific granules from extents			
			if (!useIndex) {
				int i = 0;
				for (IConcept c : context.getContextDimensions()) {
					try {
						state.set(c.toString(), context.getExtent(c).getState(ticker.current(i++)));
					} catch (ThinklabException e) {
						throw new ThinklabRuntimeException(e);
					}
				}
			}
			
			state.setIndexes(ticker.retrieveState(), ticker.changedStates(), ticker.current());

			return state;
		}

		public void remove() {

			// just don't use it for now. It may become relevant when 
			// agent models are used, but who knows.
			throw new ThinklabRuntimeException("internal: ContextStateIterator.remove() called");

		}
		
	}

	private ObservationContext context;
	private boolean useIndex = false;
	
	public ContextStateGenerator(ObservationContext context, boolean useIndex) {
		this.context = context;
		this.useIndex  = useIndex;
	}
	
	public Iterator<IObservationContextState> iterator() {
		// TODO Auto-generated method stub
		return new ContextStateIterator(context, useIndex);
	}

}
