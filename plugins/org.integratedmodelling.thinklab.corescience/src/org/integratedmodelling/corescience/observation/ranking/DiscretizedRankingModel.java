/**
 * RankingModel.java
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
package org.integratedmodelling.corescience.observation.ranking;

import java.util.ArrayList;

import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IValueAggregator;
import org.integratedmodelling.corescience.interfaces.cmodel.IValueMediator;
import org.integratedmodelling.corescience.interfaces.cmodel.ScalingConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.TransformingConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.ValidatingConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.context.IObservationContextState;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.corescience.interfaces.observation.IObservationState;
import org.integratedmodelling.corescience.observation.ConceptualModel;
import org.integratedmodelling.corescience.values.MappedInterval;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.literals.IUncertainty;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.value.NumberValue;
import org.integratedmodelling.utils.Pair;
import org.jscience.mathematics.number.Rational;

/**
 * Conceptual model for a discrete ranking - a classification bound to actual numeric ranges. 
 * @author Ferdinando Villa
 *
 *
 * TODO identical to Ranking (continuous) for now - to be written.
 */
public class DiscretizedRankingModel extends ConceptualModel implements TransformingConceptualModel, IInstanceImplementation {

	boolean leftBounded = false;
	boolean rightBounded = false;
	boolean integer = false;
	boolean isScale = false;
	double min = 0.0;
	double max = 0.0;
	private ArrayList<MappedInterval> range;
	IConcept valueType = null;
	
	/**
	 * NOTE: this expects a SORTED interval array. See ClojureBridge for how to sort it.
	 * 
	 * @param intervals
	 */
	private void define(MappedInterval[] intervals) {

		ArrayList<IConcept> concepts = new ArrayList<IConcept>();
		this.range = new ArrayList<MappedInterval>();
		
		for (MappedInterval i : intervals){
			range.add(i);
			concepts.add(i.getConcept());
		}

		this.leftBounded = range.get(0).getInterval().isLeftBounded();
		this.rightBounded = range.get(range.size() - 1).getInterval().isRightBounded();
		
		if (this.leftBounded)
			this.min = range.get(0).getInterval().getMinimumValue();

		if (this.rightBounded)
			this.max = range.get(range.size() - 1).getInterval().getMaximumValue();

		/*
		 * TODO build a discretizer for transformation
		 */
		
		valueType = KnowledgeManager.get().getLeastGeneralCommonConcept(concepts);	
	}
	
	public DiscretizedRankingModel(MappedInterval[] intervals) {
		define(intervals);
	}

	@Override
	public boolean canTransformFrom(IConcept otherConceptualModel) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IDataSource transformState(IConceptualModel otherConceptualModel,
			IObservationState state) throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConcept getStateType() {
		// TODO Auto-generated method stub
		return valueType;
	}

	@Override
	public IConcept getUncertaintyType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void validate(IObservation observation)
			throws ThinklabValidationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IValue validateLiteral(String value,
			IObservationContextState contextState)
			throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize(IInstance i) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validate(IInstance i) throws ThinklabException {
		// TODO set from IValues
		
	}


}
