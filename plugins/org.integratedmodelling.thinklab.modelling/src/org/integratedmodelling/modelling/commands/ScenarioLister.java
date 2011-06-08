package org.integratedmodelling.modelling.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.integratedmodelling.modelling.model.ModelFactory;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ListingProvider;
import org.integratedmodelling.thinklab.interfaces.commands.IListingProvider;

@ListingProvider(label="scenarios")
public class ScenarioLister implements IListingProvider {

	@Override
	public Collection<?> getListing() throws ThinklabException {
		
		ArrayList<String> ret = new ArrayList<String>();
		for (Object o : ModelFactory.get().scenariosById.keySet()) {
			ret.add(o.toString());
		}
		Collections.sort(ret);
		return ret;
	}

	@Override
	public Collection<?> getSpecificListing(String item) throws ThinklabException {
		// TODO implement listing of single model
		return null;
	}

	@Override
	public void notifyParameter(String parameter, String value) {
		// TODO Auto-generated method stub
		
	}

}
