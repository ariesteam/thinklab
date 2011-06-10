package org.integratedmodelling.modelling.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.integratedmodelling.modelling.ModelMap;
import org.integratedmodelling.modelling.ModelMap.DepEdge;
import org.integratedmodelling.modelling.interfaces.IModelForm;
import org.integratedmodelling.modelling.model.ModelFactory;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ListingProvider;
import org.integratedmodelling.thinklab.interfaces.commands.IListingProvider;
import org.integratedmodelling.utils.StringUtils;
import org.integratedmodelling.utils.WildcardMatcher;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

@ListingProvider(label="models",itemlabel="model")
public class ModelLister implements IListingProvider {

	private boolean _source;
	private boolean _dependencies;
	private boolean _canonical;

	private String _match;
	
	@Override
	public Collection<?> getListing() throws ThinklabException {
		
		ArrayList<String> ret = new ArrayList<String>();
		for (Object o : ModelFactory.get().modelsById.keySet()) {
			if (_match != null && !(new WildcardMatcher().match(o.toString(), _match)))
				continue;
			ret.add(o.toString());
		}
		Collections.sort(ret);
		return ret;
	}

	@Override
	public Collection<?> getSpecificListing(String item) throws ThinklabException {

		ArrayList<String> ret = new ArrayList<String>();
		
		for (Object o : ModelFactory.get().modelsById.keySet()) {
			if (new WildcardMatcher().match(o.toString(),item))
				ret.add(getModelEntry(item, ret, new HashSet<String>()));
		}
		
		return ret;
	}
	
	private String getModelDescription(String model) throws ThinklabException {

		if (_source) {
			return ModelMap.getSource(model);
		}		
		return model;
	}
	
	private String getModelEntry(String model, ArrayList<String> res, HashSet<String> refs) throws ThinklabException {
		
		IModelForm mod = ModelMap.getModelForm(model);
		
		if (_dependencies) {

			String s = "";
			DefaultDirectedGraph<IModelForm, DepEdge> deps = 
				ModelMap.getDependencies(model);
			
			if (_source) {
				TopologicalOrderIterator<IModelForm, DepEdge> ord =
					new TopologicalOrderIterator<IModelForm, DepEdge>(deps);
				while (ord.hasNext()) {
					s = getModelDescription(((IModelForm)ord.next()).getName()) + "\n" + s;
				}
				return s;
			} else {
				return listStructure(mod, deps, 0);
			}
		} 
		
		return getModelDescription(model);	
	}

	private String listStructure(IModelForm mod,
			DefaultDirectedGraph<IModelForm, DepEdge> deps,
			int level) {

		String ret = StringUtils.repeat(" ", level*2) + mod.getName() + "\n";
		
		for (DepEdge e : deps.outgoingEdgesOf(mod)) {
			ret += listStructure(deps.getEdgeTarget(e), deps, level + 1);
		}
		
		return ret;
	}

	@Override
	public void notifyParameter(String parameter, String value) {

		if (parameter.equals("source") && value.equals("true")) 
			this._source = true;
		else if (parameter.equals("dependencies") && value.equals("true")) 
			this._dependencies = true;
		else if (parameter.equals("canonical") && value.equals("true")) 
			this._canonical = true;
		else if (parameter.equals("match"))
			this._match = value;
	}

}
