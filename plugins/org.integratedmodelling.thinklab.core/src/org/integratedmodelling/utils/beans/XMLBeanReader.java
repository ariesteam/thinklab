package org.integratedmodelling.utils.beans;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.xml.XMLDocument;
import org.w3c.dom.Node;

class XMLBeanReader implements BeanObject.BeanReader {

	void readObject(XMLDocument doc, Node root, BeanObject object, Map<String, Class<? extends BeanObject>> cmap) 
		throws ThinklabException {
		
		for (XMLDocument.NodeIterator it = doc.iterator(root); it.hasNext(); ) {
			
			Node node = it.next();
			HashMap<String, String> attrs = null;
			
			for (Pair<String, String>  ap : XMLDocument.getNodeAttributes(node)) {
				if (attrs == null)
					attrs = new HashMap<String, String>();
				attrs.put(ap.getFirst(), ap.getSecond());
			}
			
			if (cmap.containsKey(node.getNodeName())) {
				try {
					BeanObject obj = cmap.get(node.getNodeName()).newInstance();
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
	public void read(InputStream input, BeanObject object,
			Map<String, Class<? extends BeanObject>> cmap)
			throws ThinklabException {
		
		XMLDocument doc = new XMLDocument(input);
		readObject(doc, doc.root(), object, cmap);
		
	}

}
