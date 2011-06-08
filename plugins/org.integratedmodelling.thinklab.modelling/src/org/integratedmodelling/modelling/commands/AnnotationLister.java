package org.integratedmodelling.modelling.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.integratedmodelling.modelling.model.ModelFactory;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ListingProvider;
import org.integratedmodelling.thinklab.interfaces.commands.IListingProvider;

@ListingProvider(label="annotations")
public class AnnotationLister implements IListingProvider {

	private boolean _source;

	@Override
	public Collection<?> getListing() throws ThinklabException {
		
		ArrayList<String> ret = new ArrayList<String>();
		for (Object o : ModelFactory.get().annotationsById.keySet()) {
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
		
		if (parameter.equals("source") && value.equals("true")) 
			this._source = true;
		
	}

}
