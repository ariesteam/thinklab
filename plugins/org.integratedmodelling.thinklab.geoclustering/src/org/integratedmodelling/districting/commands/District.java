/**
 * District.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Feb 05, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabDistrictingPlugin.
 * 
 * ThinklabDistrictingPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabDistrictingPlugin is distributed in the hope that it will be useful,
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
 * @author    Gary Johnson (gwjohnso@uvm.edu)
 * @date      Feb 05, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.districting.commands;

import java.util.ArrayList;

import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IObservationState;
import org.integratedmodelling.districting.DistrictingPlugin;
import org.integratedmodelling.districting.interfaces.IDistrictingAlgorithm;
import org.integratedmodelling.districting.utils.DistrictingResults;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.coverage.RasterCoverage;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.extensions.CommandHandler;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandInputProvider;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandOutputReceptor;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

/**
 * Command to invoke your districting algorithm of choice on a spatial dataset
 * extracted from observation states.
 * 
 * @author Gary Johnson
 * 
 */
public class District implements CommandHandler {

	/*
	 * TODO Just a stub for now - produce an observation structure that
	 * complements the districting results with appropriate semantics.
	 */

	private IValue reprojectDistricts(IInstance observation,
			DistrictingResults districtingResults) throws ThinklabException {

		// can't fail
		IObservation obs = (IObservation) observation.getImplementation();

		// get the spatial extent and check it's a CellExtent, which it should
		// be
		IObservationContext ctx = obs.getCurrentObservationContext();
		GridExtent extent = (GridExtent) ctx.getExtent(Geospace.get().
				RasterGridObservable());

		// produce a RasterCoverage from data and extent and show it
		RasterCoverage coverage = new RasterCoverage("districting results",
				extent, districtingResults.getTypeset());

		coverage.show();

		return null;
	}

	/*
	 * Extract states from a contextualized observation and make sure they're
	 * good for districting
	 */
	private double[][] extractStates(IInstance obs) throws ThinklabException {

		/*
		 * the assumption here is that we district the spatial state of the
		 * immediate dependencies of a top-level observation.
		 */
		IInstanceImplementation impl = obs.getImplementation();

		if (impl == null || !(impl instanceof IObservation)) {
			throw new ThinklabValidationException(
					"districting: trying to district a non-observation: "
							+ obs.getDirectType());
		}

		IObservation observation = (IObservation) impl;
		ArrayList<IObservationState> states = new ArrayList<IObservationState>();

		for (IObservation dep : observation.getDependencies()) {

			IObservationState state = dep.getObservationState();
			if (state != null) {
				states.add(state);
			}
		}

		for (IObservation dep : observation.getContingencies()) {

			IObservationState state = dep.getObservationState();
			if (state != null) {
				states.add(state);
			}
		}

		if (states.size() < 1) {
			throw new ThinklabPluginException(
					"districting: can't find any state to work with in " + obs);
		}

		/* build array and double-check that array sizes match */
		double[][] ret = new double[states.size()][];

		int i = 0;
		int size = 0;
		for (IObservationState state : states) {

			double[] data = state.getDataAsDouble();

			if (i == 0)
				size = data.length;
			else if (size != data.length)
				throw new ThinklabPluginException(
						"districting: observation states are of different numerosity");

			ret[i++] = data;
		}

		return ret;
	}

	public IValue execute(Command command, ICommandInputProvider inputSource,
			ICommandOutputReceptor outputDest, ISession session, KnowledgeManager km) throws ThinklabException {

		int k = command.getArgument("k").asNumber().asInteger();
		String oid = command.getArgumentAsString("dataset");
		IInstance obs = session.requireObject(oid);

		double[][] dataset = extractStates(obs);

		String metric = command.getOptionAsString("metric", "euclidean");
		String algorithm = command.getOptionAsString("algorithm", "k-means");

		IDistrictingAlgorithm alg = DistrictingPlugin.get()
				.retrieveDistrictingAlgorithm(algorithm);

		if (alg == null) {
			throw new ThinklabPluginException(
					"No algorithm registered under name " + algorithm
							+ " to partition dataset.");
		}

		DistrictingResults districtingResults;

		if (algorithm.equals("k-means")) {
			districtingResults = alg.createDistricts(dataset, k);
		} else if (algorithm.equals("isodata")) {
			/*
			 * TODO meaningful defaults (these should be very reasonable)
			 */
			double stoppingThreshold = command.getOptionAsDouble(
					"stopping-threshold", 0.2);
			double varianceRatio = command.getOptionAsDouble("variance-ratio",
					9.0);
			double membershipRatio = command.getOptionAsDouble(
					"membership-ratio", 0.1);
			double separationRatio = command.getOptionAsDouble(
					"separation-ratio", 0.25);

			districtingResults = alg.createDistricts(dataset, k,
					stoppingThreshold, varianceRatio, membershipRatio,
					separationRatio);
		} else {
			throw new ThinklabPluginException(
					"Valid algorithm options are k-means or isodata.");
		}

		return reprojectDistricts(obs, districtingResults);
	}

}
