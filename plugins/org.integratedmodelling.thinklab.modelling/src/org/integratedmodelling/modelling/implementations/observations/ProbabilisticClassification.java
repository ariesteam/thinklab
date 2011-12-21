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
package org.integratedmodelling.modelling.implementations.observations;

import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.modelling.data.CategoricalDistributionDatasource;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

@InstanceImplementation(concept="modeltypes:ProbabilisticClassification")
public class ProbabilisticClassification extends ModeledClassification {

	String unitSpecs = null;

	@Override
	public IState createState(int size, IObservationContext context)
			throws ThinklabException {

		IConcept[] vmaps = new IConcept[classifiers.size()];
		for (int i = 0; i < classifiers.size(); i++)
			vmaps[i] = classifiers.get(i).getSecond();
		
		IState ret =
			new CategoricalDistributionDatasource(cSpace, size, vmaps, classifiers, (ObservationContext) context);

		ret.getMetadata().merge(this.metadata);
		return ret;
	}

	@Override
	public void initialize(IInstance i) throws ThinklabException {
		super.initialize(i);
	}

}
