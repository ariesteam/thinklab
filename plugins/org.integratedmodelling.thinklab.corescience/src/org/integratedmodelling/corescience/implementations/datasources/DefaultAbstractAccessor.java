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
package org.integratedmodelling.corescience.implementations.datasources;

import org.integratedmodelling.corescience.context.ContextMapper;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.multidimensional.MultidimensionalCursor;
import org.integratedmodelling.thinklab.exception.ThinklabException;

/**
 * Adds a few useful methods to navigate the complexities of multidimensional contexts.
 * 
 * @author Ferdinando
 *
 */
public abstract class DefaultAbstractAccessor implements IStateAccessor {

	protected MultidimensionalCursor cursor = null;
	protected IObservationContext overallContext = null;
	protected IState state = null;
	protected IObservationContext ownContext = null;
	protected ContextMapper cmapper = null;

	@Override
	public void notifyState(IState dds, IObservationContext overallContext,
			IObservationContext ownContext) throws ThinklabException {

		this.state = dds;
		this.ownContext = ownContext;
		this.overallContext = overallContext;
		
		if (dds == null) 
			this.state = createMissingState(ownContext.getObservation(), ownContext);
		
		cursor       = ContextMapper.getCursor(ownContext);
		this.cmapper = new ContextMapper(overallContext, ownContext);
	}

	/**
	 * Called only when the compiler decides that it doesn't need to store the states we 
	 * produce. If we need them to keep history, we can create private storage here.
	 * 
	 * @param observation
	 * @param context
	 * @return
	 * @throws ThinklabException
	 */
	protected IState createMissingState(IObservation observation, IObservationContext context) throws ThinklabException {
		return null;
	}

	protected int getOffsetFromOverallIndex(int index) {
		return cmapper.getIndex(index);
	}
	
	protected Object getHistoryState(int[] offsets) {
		return null;
	}
}
