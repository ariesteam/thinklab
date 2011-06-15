package org.integratedmodelling.modelling.commands;

import java.util.Collection;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ListingProvider;
import org.integratedmodelling.thinklab.interfaces.commands.IListingProvider;
import org.integratedmodelling.utils.image.ColorMap;

@ListingProvider(label="colormaps")
public class ColormapLister implements IListingProvider {

	@Override
	public Collection<?> getListing() throws ThinklabException {
		return ColorMap.paletteNames;
	}

	@Override
	public Collection<?> getSpecificListing(String item) throws ThinklabException {
		// TODO implement listing of single colormap
		return null;
	}

	@Override
	public void notifyParameter(String parameter, String value) {
		// TODO Auto-generated method stub
		
	}

}
