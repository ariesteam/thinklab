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
package org.integratedmodelling.thinklab.modelling.bayes.rw;

import java.io.File;
import java.io.FileInputStream;
import java.util.Vector;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.riskwiz.bn.BeliefNetwork;
import org.integratedmodelling.riskwiz.domain.Domain;
import org.integratedmodelling.riskwiz.domain.LabelDomain;
import org.integratedmodelling.riskwiz.io.genie.GenieReader;
import org.integratedmodelling.thinklab.interfaces.bayes.IBayesianInference;
import org.integratedmodelling.thinklab.interfaces.bayes.IBayesianNetwork;

public class RiskwizBayesianNetwork implements IBayesianNetwork {

	BeliefNetwork bn = null;
	
	public RiskwizBayesianNetwork(File in) throws ThinklabIOException {

		/*
		 * TODO support other formats based on file extension, or create a new
		 * GenericReader.
		 */
		GenieReader r = new GenieReader();
		try {
			this.bn = r.load(new FileInputStream(in));
		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}

	}
	
	@Override
	public IBayesianInference getInference() {
		return new RiskwizBayesianInference(bn);
	}

	@Override
	public int getNodeCount() {
		return bn.getNodeNumber();
	}

	@Override
	public String[] getAllNodeIds() {
		return bn.getNodeNames();
	}

	@Override
	public int getOutcomeCount(String nodeId) {
		return bn.getBeliefNode(nodeId).getCount();
	}

	@Override
	public String getOutcomeId(String nodeId, int outcomeIndex) {
		return getOutcomeIds(nodeId)[outcomeIndex];
	}

	@Override
	public String[] getParentIds(String nodeId) {
		return bn.getParents(nodeId);
	}

	@Override
	public String[] getChildIds(String nodeId) {
		return bn.getChildren(nodeId);
	}

	@Override
	public String[] getOutcomeIds(String nodeId) {

		String[] ret = null;
		Domain domain = bn.getBeliefNode(nodeId).getDomain();
		if (domain instanceof LabelDomain) {
			Vector<String> cc = ((LabelDomain)domain).getStates();
			ret = new String[cc.size()]; int i = 0;
			for (String c : cc)
				ret[i++] = c;
		}
		return ret;
	}

	@Override
	public String getName() {
		return bn.getName();
	}
	
	@Override
	public IBayesianNetwork train(File observations, String method) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write(File modelFile) throws ThinklabIOException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean isLeaf(String nodeId) {
		// TODO Auto-generated method stub
		String[] ids = getChildIds(nodeId);
		return ids == null || ids.length == 0;
	}

}
