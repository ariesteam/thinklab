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
package org.integratedmodelling.modelling.annotation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.opal.OPALValidator;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.Escape;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Polylist;
import org.integratedmodelling.utils.instancelist.InstanceList;

public class OPALAnnotationImporter {

	public static void importAnnotations(String from, String to) throws ThinklabException {
		
		URL fr = null;
		try {
			fr = MiscUtilities.resolveUrlToFile(from).toURI().toURL();
		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}
		Collection<Polylist> obss =
			new OPALValidator().validateToLists(fr);

		String namespace = "default.namespace";
		
		PrintStream out = System.out;
		if (to != null) {
			FileOutputStream fout;
			try {
				fout = new FileOutputStream(new File(to));
			} catch (FileNotFoundException e) {
				throw new ThinklabIOException(e);
			}
			out = new PrintStream(fout);
			namespace = MiscUtilities.getFileBaseName(to);
		}
		
		out.println(
				"(ns " + namespace + "  \r\n" + 
				"  (:refer-clojure :rename {count length}) \r\n" + 
				"  (:refer modelling :only (defobject namespace-ontology count)))\n");
		
		if (obss != null && obss.size() > 0) {
			for (Polylist pl : obss) {
				out.println(getAnnotationSourceCode(pl) + "\n\n"); 
			}
		}
		
		out.close();
	}
	
	/**
	 * Return the source code of a defobject representing the passed instance list.
	 * 
	 * @param pl
	 * @return
	 * @throws ThinklabException
	 */
	public static String getAnnotationSourceCode(Polylist pl) throws ThinklabException {
		
		InstanceList il = new InstanceList(pl);
		
		IConcept c = il.getDirectType();
		String   d = il.getDescription();
		String  id = il.getId();
		Polylist obs = null;
		
		ArrayList<Object> o = new ArrayList<Object>();
		o.add(c);
		
		for (Object ll : pl.array()) {
			
			if (ll instanceof Polylist) {
				
				Polylist pll = (Polylist)ll;
				
				if (!pll.isEmpty()) {
					
					String fst = pll.first().toString();
					if (fst.equals("observation:hasObservable")) {
						obs = (Polylist) pll.second();
					} else if (!fst.equals("rdfs:comment")) {
						o.add(pll);
					}
				}
			}
			
		}
		
		
		String defobject = "(defobject " + id + " ";
		if (obs.length() == 1)
			defobject += obs.first().toString();
		else 
			defobject += obs;
		
		if (d != null)
			defobject += "\n\n  \"" + Escape.forDoubleQuotedString(d, false) + "\"\n";
		
		return 
			defobject + "\n" + Polylist.prettyPrint(Polylist.PolylistFromArrayList(o), 1) + ")";
	}
}
