package org.integratedmodelling.geospace.gazetteers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

import org.integratedmodelling.geospace.interfaces.IGazetteer;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
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
	
	public SimpleGazetteer(Properties properties) throws ThinklabValidationException {
		
		for (Object p : properties.keySet()) {	
			String prop = p.toString();
			if (prop.startsWith(ENTRY_PROPERTY_PREFIX)) {
				String loc = MiscUtilities.getFileExtension(prop);
				ShapeValue shape = new ShapeValue(properties.getProperty(prop));
				locations.put(loc, shape);
			}
		}
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

}
