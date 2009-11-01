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
import org.integratedmodelling.corescience.implementations.datasources.ClassData;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IExtentMediator;
import org.integratedmodelling.corescience.interfaces.cmodel.IStateValidator;
import org.integratedmodelling.corescience.interfaces.cmodel.IValueAggregator;
import org.integratedmodelling.corescience.interfaces.cmodel.MediatingConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.ScalingConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.ValidatingConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.context.IObservationContextState;
import org.integratedmodelling.corescience.interfaces.data.IContextualizedState;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.literals.BooleanValue;
import org.integratedmodelling.utils.Polylist;
import org.jscience.mathematics.number.Rational;

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
@InstanceImplementation(concept="measurement:Ranking")
public class Classification extends Observation implements IConceptualizable {
	
	protected IConcept cspace;
	
	/**
	 * Conceptual model for a simple numeric ranking. 
	 * @author Ferdinando Villa
	 *
	 */
	public class ClassificationModel implements IConceptualModel {
		
		IDataSource<?> datasource = null;
		
		public IConcept getStateType() {
			return cspace;
		}


		@Override
		public IStateAccessor getStateAccessor(IConcept stateType, IObservationContext context) {
			return new ClassificationStateAccessor(datasource);
		}

		@Override
		public void handshake(IDataSource<?> dataSource,
				IObservationContext observationContext,
				IObservationContext overallContext) throws ThinklabException {
			
			this.datasource = dataSource;
		}

		@Override
		public IContextualizedState createContextualizedStorage(int size)
				throws ThinklabException {
			return new ClassData(cspace, size);
		}

		@Override
		public void validate(IObservation observation)
				throws ThinklabValidationException {
			// TODO Auto-generated method stub
		}
	}
	
	public class ClassificationStateAccessor implements IStateAccessor {

		private int index = 0;
		private IDataSource<?> ds = null;
		
		public ClassificationStateAccessor(IDataSource<?> src) {
			this.ds = src;
		}
		
		@Override
		public boolean notifyDependencyObservable(IObservation o, IConcept observable, String formalName)
				throws ThinklabException {
			return false;
		}

		@Override
		public void notifyDependencyRegister(IObservation observation, IConcept observable,
				int register, IConcept stateType) throws ThinklabException {
			// won't be called
		}

		@Override
		public Object getValue(Object[] registers) {
			return getNextValue(registers);
		}

		private Object getNextValue(Object[] registers) {
			return ds.getValue(index++, registers);
		}

		@Override
		public boolean isConstant() {
			return false;
		}
		
		@Override
		public String toString() {
			return "[ClassificationAccessor]";
		}
	}

	@Override
	public void initialize(IInstance i) throws ThinklabException {

		super.initialize(i);
		IValue v = i.get(CoreScience.HAS_CONCEPTUAL_SPACE);
		if (v != null) {
			cspace = KnowledgeManager.get().requireConcept(v.toString());
		}
	}

	@Override
	public IConceptualModel createMissingConceptualModel() throws ThinklabException {
		return new ClassificationModel();
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

}
