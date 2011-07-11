/**
 * IInference.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Feb 25, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of RiskWiz.
 * 
 * RiskWiz is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * RiskWiz is distributed in the hope that it will be useful,
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
 * @author    Sergey Krivov
 * @date      Feb 25, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/

package org.integratedmodelling.thinklab.riskwiz.interfaces;

import java.util.Map;

import org.integratedmodelling.exceptions.ThinklabException;

public interface IBayesianInference {
	
	public void run();

	public void setEvidence(String node, String outcome) throws ThinklabException;
	
	public double getMarginal(String node, String outcome);
	
	public Map<String,Double> getMarginals(String node);
	
	public double[] getMarginalValues(String node);
	
	public void clearEvidence();
}
