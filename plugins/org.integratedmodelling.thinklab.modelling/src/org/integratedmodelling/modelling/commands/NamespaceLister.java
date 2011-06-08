package org.integratedmodelling.modelling.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.integratedmodelling.modelling.ModelMap;
import org.integratedmodelling.modelling.agents.ThinkAgent;
import org.integratedmodelling.modelling.annotation.Annotation;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.interfaces.IModelForm;
import org.integratedmodelling.modelling.model.Scenario;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ListingProvider;
import org.integratedmodelling.thinklab.interfaces.commands.IListingProvider;

@ListingProvider(label="namespaces",itemlabel="namespace")
public class NamespaceLister implements IListingProvider {

	@Override
	public Collection<?> getListing() throws ThinklabException {
		
		ArrayList<String> ret = new ArrayList<String>();

		for (ModelMap.Entry e : ModelMap.getNamespaces()) {
			ret.add(e.toString());
		}
		Collections.sort(ret);
		return ret;
	}

	@Override
	public Collection<?> getSpecificListing(String item) throws ThinklabException {
		
		ArrayList<Object> ret = new ArrayList<Object>();

		for (IModelForm f : ModelMap.listNamespace(item)) {
			
			String prefix = "";
			if (f instanceof Scenario)
				prefix = "S";
			else if (f instanceof IModel)
				prefix = "M";
			else if (f instanceof ThinkAgent)
				prefix = "A";
			else if (f instanceof Annotation)
				prefix = "O";
			
			ret.add("   " + prefix + " " + f.getId());
		}
		
		return ret;
	}

	@Override
	public void notifyParameter(String parameter, String value) {
		// TODO Auto-generated method stub
		
	}

}
