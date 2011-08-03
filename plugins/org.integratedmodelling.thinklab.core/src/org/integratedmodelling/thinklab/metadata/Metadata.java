package org.integratedmodelling.thinklab.metadata;

import java.util.ArrayList;
import java.util.HashMap;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.list.PolyList;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.modelling.metadata.IMetadata;

public class Metadata implements IMetadata {

	HashMap<String, Object> _md = new HashMap<String, Object>();
	
	@Override
	public Object get(String string) {
		return _md.get(string);
	}

	@Override
	public IList conceptualize() throws ThinklabException {

		// recognize strings that are known properties and conceptualize to them; if
		// object is a conceptualizable use an object property, otherwise annotation.
		ArrayList<Object> ret = new ArrayList<Object>();
		
		for (String s : _md.keySet()) {
			IProperty p = Thinklab.get().getProperty(s);
			if (p != null) {
				
				Object v = _md.get(s);
				
				if (v instanceof IConceptualizable) {
					v = ((IConceptualizable)v).conceptualize();
				}
				
				ret.add(PolyList.list(p, v));
				
			}
		}
		return PolyList.fromCollection(ret);
	}
	
}
