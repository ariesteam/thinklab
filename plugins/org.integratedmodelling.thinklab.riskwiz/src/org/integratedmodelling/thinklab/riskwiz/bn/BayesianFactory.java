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
package org.integratedmodelling.thinklab.riskwiz.bn;

import java.io.File;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.riskwiz.RiskWizPlugin;
import org.integratedmodelling.thinklab.riskwiz.genie.GenieBayesianNetwork;
import org.integratedmodelling.thinklab.riskwiz.interfaces.IBayesianNetwork;
import org.integratedmodelling.utils.MiscUtilities;

/**
 * Serves bayesian network objects of the type set through the global parameters in
 * plugin configuration. 
 * 
 * @author Ferdinando
 *
 */
public class BayesianFactory {

	private static BayesianFactory _this = null;
	private enum BType {
		RISKWIZ,
		GENIE
	};
	
	BType btype = BType.GENIE;
	
	private BayesianFactory() {
		
		// check if the bayesian engine was set in startup options first - these override plugin options
		String engine = System.getProperty(RiskWizPlugin.BAYESIAN_ENGINE_PROPERTY);
	
		// then in plugin config, which takes over
		if (engine == null) {
			engine = RiskWizPlugin.get().getProperties().getProperty(RiskWizPlugin.BAYESIAN_ENGINE_PROPERTY);
		}
		
		if (engine != null) {
		
			if (engine.equals("riskwiz")) {
				btype = BType.RISKWIZ;
			} else if (engine.equals("genie")) {
				btype = BType.GENIE;
			}
			
			RiskWizPlugin.get().logger().info("bayesian engine set to " + engine);
			
		}
	}
	
	public static BayesianFactory get() {
		if (_this == null)
			_this = new BayesianFactory();
		return _this;
	}
	
	public IBayesianNetwork createBayesianNetwork(String resourceId) 
		throws ThinklabException {
		
		File input = MiscUtilities.resolveUrlToFile(resourceId);	
		IBayesianNetwork ret = null;
		
		if (btype.equals(BType.RISKWIZ)) {
			ret = new RiskwizBayesianNetwork(input);
		} else if (btype.equals(BType.GENIE)) {
			ret = new GenieBayesianNetwork(input);
		}
		
		return ret;
	}
	
}
