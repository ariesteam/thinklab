/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.thinklab.commandline.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ListingProvider;
import org.integratedmodelling.thinklab.interfaces.commands.IListingProvider;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.Polylist;
import org.integratedmodelling.utils.WildcardMatcher;

@ListingProvider(label="concepts",itemlabel="concept")
public class ConceptLister implements IListingProvider {

	private boolean _canonical = false;
	private String  _match = null;
	private boolean _tree = false;
	private boolean _upwards = false;
	
	@Override
	public void notifyParameter(String parameter, String value) {
		
		if (parameter.equals("canonical") && value.equals("true")) 
			this._canonical = true;
		if (parameter.equals("tree") && value.equals("true")) 
			this._tree = true;
		else if (parameter.equals("match")) 
			this._match = value;
		else if (parameter.equals("upwards") && value.equals("true")) 
			this._upwards = true;
		
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
		
			if (_canonical) {
				
				ArrayList<Object> list = new ArrayList<Object>();
				
				for (Object conc : ret) {
					Polylist pl = 
						getHierarchyList((IConcept) conc, new HashSet<IConcept>());

					if (pl != null)
						list.add(pl);
				}
				
				Polylist ls = Polylist.PolylistFromArrayList(list);
				ret = Collections.singletonList(ls);
				
			} else {
				
				List<?> source = ret;
				ArrayList<Object> zoz = new ArrayList<Object>();
				for (Object o : source) {
					for (Object u : getHierarchyStrings((IConcept)o))
						zoz.add(u);
				}
				ret = zoz;
			}
		}
		
		return ret;
	}

	private Collection<String> getHierarchyStrings(IConcept o) {
		ArrayList<String> list = new ArrayList<String>();
		
		
		
		return list;
	}

	private Polylist getHierarchyList(IConcept conc, HashSet<IConcept> hs) {

		ArrayList<Object> list = new ArrayList<Object>();
		
		if (hs.contains(conc))
			return null;

		hs.add(conc);
		list.add(conc);
		
		for (IConcept c : (_upwards? conc.getParents() : conc.getChildren())) {

			// can happen if using sloppy ontologies - better say something.
			if (hs.contains(c)) {
				Thinklab.get().logger().warn("concept " + c + " has inconsistent hierarchy");
			}
			Collection<IConcept> zio = _upwards ? c.getParents() : c.getChildren();
			Object oo = 
				zio.size() == 0 ? c : getHierarchyList(c, hs);
			if (oo != null)
				list.add(oo);
		}
		
		
		return Polylist.PolylistFromArrayList(list);
	}


	@Override
	public Collection<?> getSpecificListing(String item)
			throws ThinklabException {

		IConcept c = KnowledgeManager.get().requireConcept(item);

		ArrayList<Object> ret = new ArrayList<Object>();

		if (_tree) {
			if (_canonical) {
				ret.add(getHierarchyList(c, new HashSet<IConcept>()));
			} else {
				for (Object u : getHierarchyStrings(c))
					ret.add(u);
			}
		} else {
			ret.add(c);
		}
		
		return ret;

	}	
}
