/**
 * Ranking.java
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
package org.integratedmodelling.corescience.implementations.observations;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.implementations.datasources.ClassData;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.internal.IndirectObservation;
import org.integratedmodelling.corescience.interfaces.internal.MediatingObservation;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.list.Polylist;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;

/**
 * A ranking is the simplest of quantifications, defining the observable through
 * a numeric state that may or may not be bounded. Bounded ranks of different
 * scales can be mediated if they have been defined to represent scales. 
 * 
 * Ranks are double by default but can be constrained to
 * integers. Rankings are useful in providing an immediate translation for
 * non-semantic "variables", e.g. in legacy models seen as observation
 * structures.
 * 
 * For ease of specification, rankings contain all their conceptual model
 * parameters in their own properties, and create and configure the conceptual
 * model automatically during validation.
 * 
 * @author Ferdinando Villa
 *
 */
@InstanceImplementation(concept="observation:Classification")
public class Classification extends Observation implements MediatingObservation {
	
	protected IConcept cspace;
	
	@Override
	public String toString() {
		return ("classification(" + getObservableClass() + "): " + cspace);
	}

	public class ClassificationAccessor implements IStateAccessor {

		protected int index = 0;
		
		@Override
		public Object getValue(int idx, Object[] registers) {
			return getNextValue(registers);
		}

		private Object getNextValue(Object[] registers) {
			return getDataSource().getValue(index++, registers);
		}

		@Override
		public boolean isConstant() {
			return false;
		}
		
		@Override
		public String toString() {
			return "[ClassificationAccessor]";
		}

		@Override
		public boolean notifyDependencyObservable(IObservation o,
				IConcept observable, String formalName)
				throws ThinklabException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void notifyDependencyRegister(IObservation observation,
				IConcept observable, int register, IConcept stateType)
				throws ThinklabException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void notifyState(IState dds, IObservationContext overallContext,
				IObservationContext ownContext) throws ThinklabException {
		}
	}
	
	public class ClassificationMediator extends ClassificationAccessor {

		@Override
		public Object getValue(int idx, Object[] registers) {
			return registers[index];
		}
		
	}

	@Override
	public void initialize(IInstance i) throws ThinklabException {

		metadata.put(Metadata.CONTINUOUS, Boolean.FALSE);

		super.initialize(i);
		IValue v = i.get(CoreScience.HAS_CONCEPTUAL_SPACE);
		if (v != null) {
			cspace = KnowledgeManager.get().requireConcept(v.toString());
		}
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {

		return Polylist.list(
				CoreScience.CLASSIFICATION,
				Polylist.list(CoreScience.HAS_OBSERVABLE,
						(getObservable() instanceof IConceptualizable) ? 
								((IConceptualizable)getObservable()).conceptualize() :
								getObservable().toList(null)),
						Polylist.list(CoreScience.HAS_CONCEPTUAL_SPACE, cspace.toString()));
	}

	@Override
	public IStateAccessor getAccessor(IObservationContext context) {
		return new ClassificationAccessor();
	}

	@Override
	public IConcept getStateType() {
		return cspace;
	}

	@Override
	public IState createState(int size, IObservationContext context) throws ThinklabException {
		IState ret = new ClassData(cspace, size, null, (ObservationContext)context);
		ret.getMetadata().merge(this.metadata);
		return ret;
	}

	@Override
	public IStateAccessor getMediator(IndirectObservation observation, IObservationContext context)
			throws ThinklabException {
		return new ClassificationMediator();
	}

}
