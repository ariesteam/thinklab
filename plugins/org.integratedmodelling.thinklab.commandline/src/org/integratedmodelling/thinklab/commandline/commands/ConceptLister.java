package org.integratedmodelling.thinklab.commandline.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ListingProvider;
import org.integratedmodelling.thinklab.interfaces.commands.IListingProvider;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.WildcardMatcher;

@ListingProvider(label="concepts",itemlabel="concept")
public class ConceptLister implements IListingProvider {

	private boolean _canonical;
	private String  _match;
	private boolean _tree;

	@Override
	public void notifyParameter(String parameter, String value) {
		
		if (parameter.equals("canonical") && value.equals("true")) 
			this._canonical = true;
		if (parameter.equals("tree") && value.equals("true")) 
			this._tree = true;
		else if (parameter.equals("match")) 
			this._match = value;
		
	}

	@Override
	public Collection<?> getListing() throws ThinklabException {
		// TODO Auto-generated method stub
		
		List<?> con = 
			_tree ?
				KnowledgeManager.get().getKnowledgeRepository().getAllRootConcepts() :
				KnowledgeManager.get().getKnowledgeRepository().getAllConcepts();
				
		List<?> ret = con;
		
		if (_match != null) {
			ArrayList<Object> zoz = new ArrayList<Object>();
			for (Object c : con) {
				if (new WildcardMatcher().match(c.toString(), _match))
					zoz.add(c);
			}
			ret = zoz;
		}
		
		if (_tree) {
			
			/*
			 * change to tree, strings or not according to format
			 */
			List<?> source = ret;
			ArrayList<Object> zoz = new ArrayList<Object>();
			for (Object o : source) {
				for (Object u : getHierarchy((IConcept)o, 0, new HashSet<IConcept>()))
					zoz.add(u);
			}
			ret = zoz;
		}
		
		return ret;
	}

	private List<?> getHierarchy(IConcept o, int level, HashSet<IConcept> hashSet) {

		ArrayList<Object> rr = new ArrayList<Object>();

		if (hashSet.contains(o))
			return rr;
			
		if (_canonical) {
			rr.add(getHierarchy(o, new HashSet<IConcept>()));
		} else {
			// add strings for each subconcept with the appropriate indent level
		}
					
		return rr;
	}

	
	

	private Object getHierarchy(IConcept o, HashSet<IConcept> hashSet) {

		ArrayList<Object> cc = new ArrayList<Object>();
		cc.add(o);
		if (!hashSet.contains(o))
			hashSet.add(o);
			for (IConcept c : o.getChildren()) {
				cc.add(getHierarchy(c, hashSet));
			}
		
		return cc.toArray(new Object[cc.size()]);
	}

	@Override
	public Collection<?> getSpecificListing(String item)
			throws ThinklabException {

		IConcept c = KnowledgeManager.get().requireConcept(item);

		ArrayList<Object> ret = new ArrayList<Object>();

		if (_tree) {
			for (Object u : getHierarchy(c, 0, new HashSet<IConcept>()))
				ret.add(u);
		} else {
			ret.add(c);
		}
		
		return ret;

	}

//	private ClassNode getClassStructure(IConcept root, HashSet<String> catalog) {
//		
//		String cid = root.toString();
//		
//		ClassNode ret = new ClassNode(root);
//
//		if (!catalog.contains(cid)) {
//
//			catalog.add(cid);
//
//			for (IConcept c : root.getChildren()) {
//				ClassNode cn = getClassStructure(c, catalog);
//				if (cn != null)
//					ret.add(cn);
//			}
//		}
//		/* put class node into hash */
//		ArrayList<ClassNode> r = map.get(cid);
//		if (r == null) {
//			r = new ArrayList<ClassNode>();
//		}
//		
//		r.add(ret);
//		map.put(cid, r);
//		
//		return ret;
//	}
	
}
