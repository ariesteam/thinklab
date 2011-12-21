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
package org.integratedmodelling.thinklab.riskwiz.clojure;

import java.util.Collection;

import org.integratedmodelling.riskwiz.bn.BeliefNetwork;

/**
 * Interface functions for Clojure
 * 
 * @author Ferdinando
 *
 */
public class RiskwizClojureBridge {

	class BeliefNodeDescriptor {
		
	}
	
	public static BeliefNetwork buildBeliefNetwork(Collection<BeliefNodeDescriptor> nodes) {
		
		BeliefNetwork ret = null;
		
		/*
		 * link nodes based on parent IDs
		 */
		
		/*
		 * build BN from them
		 */
		
		
		return ret;
	}
	
	public static BeliefNodeDescriptor buildBeliefNode(String ID, Collection<String> parentIDs, float[] cpt) {
		return null;
	}
	
}
