package org.integratedmodelling.utils.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.tree.DefaultMutableTreeNode;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.Polylist;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Make XML encoding as fun as possible using a functional style. Also automatically
 * converts (suitable) polylists to XML nodes.
 * 
 * Example:
 * 
 * XML.document("myns=http://my.namespace.org/ns"
 * 		XML.node("root",
 * 			XML.node("myns:content1", "Hi!")
 * 			XML.node("myns:content2", "Hi!")
 * 			XML.node("myns:content3", "Hi!")
 *      )).write("file.xml");
 * 
 * @author Ferdinando Villa
 */
public class XML {
	
	public static class XmlNode extends DefaultMutableTreeNode {

		private static final long serialVersionUID = -3750169814447901831L;
		String tag = null;
		String text = null;
		ArrayList<Pair<String, String>> attrs = null;
		ArrayList<Collection<?>> collections = null;
		ArrayList<Polylist> lists = null;
		
		public void attr(String s, String v) {
			if (attrs == null)
				attrs = new ArrayList<Pair<String,String>>();
			attrs.add(new Pair<String,String>(s,v));
		}
		
		public void text(String text) {
			this.text = text;
		}
		
		public XmlNode(String tag) {
			this.tag = tag;
		}
		
		Node create(Node parent, Document doc) throws ThinklabException {
			
			Node ret = doc.createElement(tag);
			
			if (attrs != null)
				for (Pair<String,String> a : attrs) {
					Attr attr = doc.createAttribute(a.getFirst());		
					attr.setValue(a.getSecond());
					((Element)ret).setAttributeNode(attr);					
				}
		
			if (text != null) 
				ret.setTextContent(text);
			
			if (collections != null)
				for (Collection<?> c : collections) {
					for (Iterator<?> it = ((Collection<?>)c).iterator(); it.hasNext(); ) {
						Object o = it.next();
						if (!(o instanceof XmlNode)) {
							throw new ThinklabValidationException(
								"XML.node: collections must be of XmlNode");	
						}
						ret.appendChild(((XmlNode)o).create(ret, doc));
					}
			}
			
			for (Object o : this.children) {
				ret.appendChild(((XmlNode)o).create(ret, doc));
			}
			
			return ret;
		}

		void define(Node self, Document doc) throws ThinklabException {
					
			if (attrs != null)
				for (Pair<String,String> a : attrs) {
					Attr attr = doc.createAttribute(a.getFirst());		
					attr.setValue(a.getSecond());
					((Element)self).setAttributeNode(attr);					
				}
			
			if (collections != null)
				for (Collection<?> c : collections) {
					for (Iterator<?> it = ((Collection<?>)c).iterator(); it.hasNext(); ) {
						Object o = it.next();
						if (o instanceof XmlNode) {
							self.appendChild(((XmlNode)o).create(self, doc));
						} else if (o instanceof Polylist) {
							self.appendChild(((Polylist)o).createXmlNode().create(self, doc));
						} else {
							throw new ThinklabValidationException(
								"XML.node: collections must be of XmlNode or Polylist");	
						}
					}
				}

			for (Object o : this.children) {
				if (o instanceof XmlNode)	
					self.appendChild(((XmlNode)o).create(self, doc));
			}
			
			for (Polylist p : this.lists) {
					self.appendChild(p.createXmlNode().create(self, doc));					
			}
		}
	}
	
	public static XMLDocument document(Object ...objects) throws ThinklabException {
	
		XmlNode root = null;
		ArrayList<String> namespaces = null;
		
		for (Object o : objects) {
			if (o instanceof String) {
				
				/*
				 * namespace
				 */
				if (namespaces == null)
					namespaces = new ArrayList<String>();
				
				namespaces.add((String)o);
				
			} else if (o instanceof XmlNode) {
			 
				/*
				 * must be only root node
				 */
				if (root != null)
					throw new ThinklabValidationException(
							"XML document: non-unique root node");
				root = (XmlNode)o;
				
			} 
		}
		
		if (root == null)
			throw new ThinklabValidationException(
					"XML.document: no root node specified");	
		
		XMLDocument doc = new XMLDocument(root.tag);
		
		if (namespaces != null) {
			for (String ns : namespaces) {
				String[] nss = ns.split("=");
				if (nss.length != 2)
					throw new ThinklabValidationException(
						"XML.document: bad namespace specification: must be name=uri");	
				doc.addNamespace(nss[0], nss[1]);
			}
		}
		
		root.define(doc.root(), doc.dom);
		
		return doc;
	}
	
	public static XmlNode node(String tag, Object ... objects) throws ThinklabException {
		
		XmlNode ret = new XmlNode(tag);
		
		for (Object o : objects) {
			if (o instanceof XmlNode) {
				ret.add((XmlNode)o);
			} if (o instanceof Polylist) {
				ret.lists.add((Polylist)o);
			} else if (o instanceof String) {
				if (ret.text != null)
					throw new ThinklabValidationException(
					"XML.node: only one content string admitted");
				ret.text = (String)o;
			} else if (o instanceof Collection<?>) {
				if (ret.collections == null)
					ret.collections = new ArrayList<Collection<?>>();
				ret.collections.add((Collection<?>)o);
			} else {
				throw new ThinklabValidationException(
						"XML.node: only admitted content is one text string or other XmlNodes");
			}
		}
		
		return ret;
	}
	

}
