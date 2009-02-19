/**
 * IDataSource.java
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
package org.integratedmodelling.corescience.interfaces.data;

import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * Datasources negotiate all details of providing data with the conceptual model,
 * and are not used by any other component. The only required behavior is to 
 * associate a value with an index, and to know the Thinklab type they 
 * provide. A handshake function is called on both the datasource and
 * the conceptual model so they know each other. 
 * 
 * @author Ferdinando Villa
 *
 */
public interface IDataSource<T> {	

	/**
	 * Called once before contextualization and after the overall context has been
	 * computed. It's meant to negotiate communication parameters between the datasource
	 * and the observation state. It should analyze the contexts of the observation that
	 * the DS belongs to, and check the overall context as necessary, which may differ. Any
	 * discrepancy that cannot be negotiated (or the inability to understand the discrepancies)
	 * should cause an exception to be thrown.
	 * 
	 * The expected return value is true if the datasource can return a value using only
	 * the ordinal indexes of the extent granules in the observation context state, and false if
	 * the actual extent values (returned by IExtent.getValue()) are needed. This saves time
	 * during contextualization when getValue() is called; if true is returned, the IObservationContextState
	 * passed will only contain values for the dependencies, and not for the extents.
	 * 
	 * @param cm the conceptual model of the connected observation
	 * @param observationContext the finished context of the connected observation
	 * @param overallContext the overall context, including at least the same extents of observationContext,
	 * 		but potentially other extents compounded from other observations, and with possibly
	 * 		a finer grain and a narrower extent than what represented in the datasource.
	 * @return true if ordinal indexes can be used to return a value for a specific extent
	 * 		state; false if actual IValues are needed for each current extent.
	 * @throws ThinklabException 
	 */
	public abstract boolean handshake(
			IConceptualModel cm, 
			IObservationContext observationContext,
			IObservationContext overallContext)
		 throws ThinklabException;
	
	
	/**
	 * All datasources must report to the conceptual model what kind of
	 * value they are going to return.
	 * 
	 * @return
	 */
	public abstract IConcept getValueType();


	/**
	 * Datasources may have an initial value before anything is computed or extracted. This value, if
	 * not null, is used to initialize state storage before contextualization. If the initial value
	 * makes sense for the datasource, return it here. Otherwise just return null.
	 * 
	 * @return
	 */
	public abstract T getInitialValue();
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public abstract T getValue(int index);

}
