package org.integratedmodelling.modelling.bayesian;

import java.io.InputStream;
import java.util.ArrayList;

import org.integratedmodelling.modelling.ModelFactory;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.riskwiz.bn.BeliefNetwork;
import org.integratedmodelling.riskwiz.io.genie.GenieReader;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.utils.CamelCase;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Triple;
import org.nfunk.jep.ParseException;

/**
 * 
 * @author ferdinando.villa
 *
 */
public class BayesianModelFactory {

	/**
	 * Analyze bayesian models coming from one of the formats recognized by
	 * RiskWiz, and turn them into model statements reusing the information, if
	 * any, already present in existing models using a given namespace to
	 * find them.
	 * 
	 * @param bayesianURL
	 * @param modelUrl
	 * @throws ThinklabException 
	 */
	public void syncModels(String bayesianUrl, String modelNamespace) throws ThinklabException {

		GenieReader r = new GenieReader();
		InputStream is = MiscUtilities.getInputStreamForResource(bayesianUrl);
		BeliefNetwork bn = null;
		
		try {
			bn = r.load(is);
		} catch (ParseException e) {
			throw new ThinklabValidationException(e);
		}
		
		ArrayList<Triple<String, String, IModel>> models = 
			new ArrayList<Triple<String,String,IModel>>();
		
		/*
		 * find concept space from models if any exists in namespace.
		 */
		for (String nn : bn.getNodeNames()) {	
			String mname = 
				modelNamespace+ "/" + CamelCase.toLowerCase(nn, '-');
			IModel model = ModelFactory.get().retrieveModel(mname);
			models.add(new Triple<String, String, IModel>(nn, mname, model));
		}
		
		/*
		 * sync each model appropriately
		 */
		for (Triple<String, String, IModel> tm : models) {

			if (tm.getThird() == null) {
				tm.setThird(createModel(bn, tm.getFirst()));
			} else {
				tm.setThird(syncModel(bn, tm.getFirst(), tm.getThird()));
			}
		}
		
		/*
		 * output? we need the string rep of each model, including those that 
		 * were in the same file but not part of the bn.
		 */
		for (Triple<String, String, IModel> tm : models) {
		}
		
	}

	private IModel syncModel(BeliefNetwork bn, String nodename, IModel third) {
		// TODO Auto-generated method stub
		return null;
	}

	private IModel createModel(BeliefNetwork bn, String nodename) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
