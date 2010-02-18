package org.integratedmodelling.modelling.commands;

import java.io.PrintStream;
import java.util.Collection;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ListingProvider;
import org.integratedmodelling.thinklab.interfaces.commands.IListingProvider;
import org.integratedmodelling.utils.image.ColorMap;

@ListingProvider(label="colormaps")
public class ColormapLister implements IListingProvider {

	@Override
	public Collection<String> getListing() throws ThinklabException {
		return ColorMap.paletteNames;
	}

	@Override
	public void listItem(String item, PrintStream out) throws ThinklabException {
		// TODO implement listing of single colormap
	}

}
