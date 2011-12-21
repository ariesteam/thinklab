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
package org.integratedmodelling.utils.beans;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.utils.Escape;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.xml.XMLDocument;
import org.w3c.dom.Node;

class XMLBeanReader implements Bean.BeanReader {

	void readObject(XMLDocument doc, Node root, Bean object, Map<String, Class<? extends Bean>> cmap) 
		throws ThinklabException {
		
		for (XMLDocument.NodeIterator it = doc.iterator(root); it.hasNext(); ) {
			
			Node node = it.next();
			if (node.getNodeType() != Node.ELEMENT_NODE) 
				continue;

			HashMap<String, String> attrs = null;
			
			for (Pair<String, String>  ap : XMLDocument.getNodeAttributes(node)) {
				if (attrs == null)
					attrs = new HashMap<String, String>();
				attrs.put(ap.getFirst(), ap.getSecond());
			}
			
			if (cmap.containsKey(node.getNodeName())) {
				try {
					Bean obj = cmap.get(node.getNodeName()).newInstance();
					readObject(doc, node, obj, cmap);
					object.addChild(node.getNodeName(), obj, attrs);
				} catch (Exception e) {
					throw new ThinklabValidationException(e);
				}
			} else {
				object.addField(node.getNodeName(), node.getTextContent(), attrs);
			}
		}
		
	}
	
	@Override
	public void read(InputStream input, Bean object,
			Map<String, Class<? extends Bean>> cmap)
			throws ThinklabException {
		
		XMLDocument doc = new XMLDocument(input);
		readObject(doc, doc.root(), object, cmap);
		
	}

	@Override
	public void write(OutputStream output, Bean object, Map<Class<? extends Bean>, String> cmap)
			throws ThinklabException {

		OutputStreamWriter ow = new OutputStreamWriter(output);
		PrintWriter writer = new PrintWriter(ow);
		writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		writer.println("<" + cmap.get(object.getClass()) + ">");
		writeObject(writer, object, 2, cmap);
		writer.println("</" + cmap.get(object.getClass()) + ">");
		writer.close();
		try {
			ow.close();
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	}

	private void printRoot(PrintWriter writer, String id, 
			Map<String, String> attributes, String wspace) {

		writer.print(wspace + "<" + id);
		if (attributes != null) {
			for (String a : attributes.keySet()) {
				writer.print(" " + a + "=\"" + Escape.forXML(attributes.get(a)) + "\"");
			}
		}
		writer.print(">");
	}
	
	private void writeObject(PrintWriter writer, Bean object, int level,
			Map<Class<? extends Bean>, String> cmap) {

		String space = MiscUtilities.spaces(level);		
		
		for (Bean.OD o : object.getFields()) {
			printRoot(writer, o.id, o.attributes, space);
			writer.print(Escape.forXML(o.value.toString()));
			writer.println("</" + o.id + ">");
		}
		for (Bean.OD o : object.getChildren()) {
			printRoot(writer, cmap.get(o.value.getClass()), o.attributes, space);
			writer.println();
			writeObject(writer, (Bean)o.value, level+2, cmap);
			writer.println(space + "</" + cmap.get(o.value.getClass()) + ">");
		}		
	}

}
