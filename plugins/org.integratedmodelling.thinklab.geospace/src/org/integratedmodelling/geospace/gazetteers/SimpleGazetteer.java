package org.integratedmodelling.geospace.gazetteers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.interfaces.IGazetteer;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.utils.MiscUtilities;

/**
 * The most trivial of gazetteers - configured through properties.
 * 
 * @author Ferdinando
 *
 */
public class SimpleGazetteer implements IGazetteer {

	public static final String ENTRY_PROPERTY_PREFIX = "geospace.gazetteer.entry.";
	
	HashMap<String, ShapeValue> locations = new HashMap<String, ShapeValue>();
	
	public SimpleGazetteer() {
		// expect initialize()
	}
	
	public SimpleGazetteer(Properties properties) throws ThinklabException {
		initialize(properties);
	}
	
	@Override
	public Collection<String> getKnownNames(Collection<String> container) {
		if (container == null)
			container = new ArrayList<String>();
		container.addAll(locations.keySet());
		return container;
	}

	@Override
	public Collection<ShapeValue> resolve(String name,
			Collection<ShapeValue> container, Properties options)
			throws ThinklabException {
		Collection<ShapeValue> ret = container;
		if (ret == null)
			ret = new ArrayList<ShapeValue>();
		ShapeValue sh = locations.get(name);
		if (sh != null)
			ret.add(sh);
		return ret;
	}

	@Override
	public void addLocation(String id, ShapeValue shape,
			Map<String, Object> metadata) throws ThinklabException {
		
		// just ignore any metadata, but do add it, even if we're read only, who cares
		locations.put(id, shape);
	}

	@Override
	public void importLocations(String url) throws ThinklabException {
		// do nothing, we're read only
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public void initialize(Properties properties) throws ThinklabException {
		
		for (Object p : properties.keySet()) {	
			String prop = p.toString();
			if (prop.startsWith(ENTRY_PROPERTY_PREFIX)) {
				String loc = MiscUtilities.getFileExtension(prop);
				ShapeValue shape = 
					new ShapeValue(properties.getProperty(prop), Geospace.get().getStraightGeoCRS());
				locations.put(loc, shape);
			}
		}
	}

}
