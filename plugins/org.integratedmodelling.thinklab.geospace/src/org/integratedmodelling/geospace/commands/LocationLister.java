package org.integratedmodelling.geospace.commands;

import java.io.PrintStream;
import java.util.Collection;

import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ListingProvider;
import org.integratedmodelling.thinklab.interfaces.commands.IListingProvider;

@ListingProvider(label="locations",itemlabel="location")
public class LocationLister implements IListingProvider {

	@Override
	public Collection<String> getListing() throws ThinklabException {
		return Geospace.get().listKnownFeatures();
	}

	@Override
	public void listItem(String item, PrintStream out) throws ThinklabException {
		
		int i = 0;

	}
}
