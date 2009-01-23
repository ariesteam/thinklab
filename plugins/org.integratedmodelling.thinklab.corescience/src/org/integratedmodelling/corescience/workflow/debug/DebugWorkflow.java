/**
 * DebugWorkflow.java
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
package org.integratedmodelling.corescience.workflow.debug;

import java.util.Date;

import org.integratedmodelling.corescience.contextualization.ObservationContext;
import org.integratedmodelling.corescience.contextualization.ObservationContextState;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.context.IObservationContextState;
import org.integratedmodelling.corescience.observation.IObservation;
import org.integratedmodelling.corescience.observation.IObservationState;
import org.integratedmodelling.corescience.workflow.AsynchronousContextualizationWorkflow;
import org.integratedmodelling.corescience.workflow.AsynchronousContextualizationWorkflow.ActivationRecord;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

/**
 * Debug workflow: simply record what should be done, and output the sequence of
 * operations when run.
 * 
 * @author Ferdinando Villa
 *
 */
public class DebugWorkflow extends AsynchronousContextualizationWorkflow {

	public IValue run(IObservation observation, IObservationContext ctx) throws ThinklabException {
		
		createActivationRecords(observation, ctx);
		
		System.out.println("*** Debug contextualization workflow run started on " + new Date() + " ***");
		
		ObservationContext context = (ObservationContext)ctx;
		
		/* make sure we call this one properly */
		if (context == null || observation == null || observationOrder == null) {
			System.out.println("Debug workflow is not properly initialized. Exiting.");
			return null;
		}
			
		/* dump the main observation we're contextualizing */
		System.out.println("Contextualizing observation " + observation);
		
		/* dump all observations in dependency order and their own declared context */
		System.out.println("\nObservations in order of contextualization:");
		int cnt = 0;
		
		for (IObservation obs : observationOrder) {
			System.out.println("\t[" + cnt++ + "] " + obs);
		}
		
		/* dump overall observation context */
		System.out.println();
		context.dump("");
			
		System.out.println("Dumping activation records");
		for (ActivationRecord ar : activationOrder) {
			ar.dump();
		}

		
		System.out.println("\n*** Starting contextualization loop");
		int idx = 0;
		
		/* iterate over all fine-grained context states */
		for (IObservationContextState contextState : ctx.getContextStates(this)) {
			
			System.out.println("\t[" + idx++ + "]: context state is " + contextState);
			
			// extract state, mediating and deactivating as necessary
			int iidx = 0;
			for (ActivationRecord ar : activationOrder) {
				ar.extractStateFromDatasource((ObservationContextState) contextState);
				ar.dumpState("\t\t" + idx + "." + iidx++ + ": ");
			}
			
		}
		
		System.out.println("\n*** Debug contextualization workflow run ended on " + new Date() + " ***");

		// do whatever we need to do with the states left in activation records
		return generateResult(observation, ctx);
	}

	@Override
	public IValue generateResult(IObservation observation, IObservationContext context) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public IObservationState createObservationState(IObservation observation, IConcept stateType, long nStates) throws ThinklabException {

		return new DebugState();
	}


}
