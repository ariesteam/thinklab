package org.integratedmodelling.geospace.commands;

import java.util.Collection;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.thinklab.interfaces.annotations.ListingProvider;
import org.integratedmodelling.thinklab.interfaces.commands.IListingProvider;

@ListingProvider(label="locations",itemlabel="location")
public class LocationLister implements IListingProvider {

	@Override
	public Collection<?> getListing() throws ThinklabException {
		return Geospace.get().listKnownFeatures();
	}

	@Override
	public Collection<?> getSpecificListing(String item) throws ThinklabException {
		
		int i = 0;
		
		return null;

	}

	@Override
	public void notifyParameter(String parameter, String value) {
		// TODO Auto-generated method stub
		
	}
}
