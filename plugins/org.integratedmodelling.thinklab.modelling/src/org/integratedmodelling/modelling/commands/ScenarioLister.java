package org.integratedmodelling.modelling.commands;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.integratedmodelling.modelling.ModelFactory;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ListingProvider;
import org.integratedmodelling.thinklab.interfaces.commands.IListingProvider;

@ListingProvider(label="scenarios")
public class ScenarioLister implements IListingProvider {

	@Override
	public Collection<String> getListing() throws ThinklabException {
		
		ArrayList<String> ret = new ArrayList<String>();
		for (Object o : ModelFactory.get().scenariosById.keySet()) {
			ret.add(o.toString());
		}
		Collections.sort(ret);
		return ret;
	}

	@Override
	public void listItem(String item, PrintStream out) throws ThinklabException {
		// TODO implement listing of single model
	}

}
