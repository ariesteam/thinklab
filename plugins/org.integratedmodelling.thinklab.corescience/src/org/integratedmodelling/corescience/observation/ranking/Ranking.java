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
package org.integratedmodelling.corescience.observation.ranking;

import org.integratedmodelling.corescience.interfaces.IConceptualModel;
import org.integratedmodelling.corescience.observation.Observation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.value.BooleanValue;

/**
 * A ranking is the simplest of quantifications, defining the observable through
 * a numeric state that may or may not be bounded. Bounded ranks of different
 * scales can be mediated if they have been defined to represent scales. 
 * 
 * Ranks are double by default but can be constrained to
 * integers. Rankings are useful in providing an immediate translation for
 * nonsemantic "variables", e.g. in legacy models seen as observation
 * structures.
 * 
 * For ease of specification, rankings contain all their conceptual model
 * parameters in their own properties, and create and configure the conceptual
 * model automatically during validation.
 * 
 * @author Ferdinando Villa
 * 
 */
public class Ranking extends Observation {

	private static final String MINVALUE_PROPERTY = "measurement:minValue";
	private static final String MAXVALUE_PROPERTY = "measurement:maxValue";
	private static final String ISINTEGER_PROPERTY = "measurement:isInteger";
	private static final String ISSCALE_PROPERTY = "measurement:isScale";

	@Override
	public IConceptualModel createMissingConceptualModel() throws ThinklabException {

		double minV = 0.0;
		double maxV = -1.0;
		boolean integer = false;
		boolean isScale = false;
		
		// read in scale attributes and pass to CM
		IValue min = getObservationInstance().get(MINVALUE_PROPERTY);
		IValue max = getObservationInstance().get(MAXVALUE_PROPERTY);
		IValue isi = getObservationInstance().get(ISINTEGER_PROPERTY);
		IValue iss = getObservationInstance().get(ISSCALE_PROPERTY);

		if (min != null)
			minV = min.asNumber().asDouble();
		if (max != null) 
			maxV = max.asNumber().asDouble();
		if (isi != null)
			integer = BooleanValue.parseBoolean(isi.toString());
		if (iss != null)
			isScale = BooleanValue.parseBoolean(iss.toString());

		return new RankingModel(minV, maxV, integer, min != null, max != null, isScale);

	}

}
