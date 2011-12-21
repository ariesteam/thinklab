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
package org.integratedmodelling.utils.xml;

import org.integratedmodelling.thinklab.exception.ThinklabException;

/**
 * @author Ferdinando Villa
 * 
 */
public class GeoRSS extends XML {

	public static class GeoRSSNode extends XmlNode {

		private static final long serialVersionUID = -749115666134776879L;

		public GeoRSSNode href(String href) {
			return (GeoRSSNode) attr("href", href);
		}
	}

	public static GeoRSSNode feed(Object ... objects) throws ThinklabException {
		return (GeoRSSNode) 
			node(new GeoRSSNode(), "feed", objects).
				attr("xmlns", "http://www.w3.org/2005/Atom").
				attr("xmlns:georss", "http://www.georss.org/georss");
	}
	
	public static GeoRSSNode RDF(Object ... objects) throws ThinklabException {
		return (GeoRSSNode) 
			node(new GeoRSSNode(), "rdf:RDF", objects).
			    attr("xmlns:rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#").
				attr("xmlns", "http://purl.org/rss/1.0/").
				attr("xmlns:dc", "http://purl.org/dc/elements/1.1/").
				attr("xmlns:georss", "http://www.georss.org/georss");
	}
	
	public static GeoRSSNode items(Object ... objects) throws ThinklabException {
		return (GeoRSSNode) node(new GeoRSSNode(), "items", objects);
	}
	
	public static GeoRSSNode description(Object ... objects) throws ThinklabException {
		return (GeoRSSNode) node(new GeoRSSNode(), "description", objects);
	}
	
	public static GeoRSSNode id(Object ... objects) throws ThinklabException {
		return (GeoRSSNode) node(new GeoRSSNode(), "id", objects);
	}

	public static GeoRSSNode item(Object ... objects) throws ThinklabException {
		return (GeoRSSNode) node(new GeoRSSNode(), "item", objects);
	}
	
	public static GeoRSSNode email(Object ... objects) throws ThinklabException {
		return (GeoRSSNode) node(new GeoRSSNode(), "email", objects);
	}
	
	public static GeoRSSNode channel(Object ... objects) throws ThinklabException {
		return (GeoRSSNode) node(new GeoRSSNode(), "channel", objects);
	}
	
	public static GeoRSSNode name(Object ... objects) throws ThinklabException {
		return (GeoRSSNode) node(new GeoRSSNode(), "name", objects);
	}
	
	public static GeoRSSNode author(Object ... objects) throws ThinklabException {
		return (GeoRSSNode) node(new GeoRSSNode(), "author", objects);
	}
	
	public static GeoRSSNode creator(Object ... objects) throws ThinklabException {
		return (GeoRSSNode) node(new GeoRSSNode(), "dc:creator", objects);
	}
	
	public static GeoRSSNode date(Object ... objects) throws ThinklabException {
		return (GeoRSSNode) node(new GeoRSSNode(), "dc:date", objects);
	}
	
	public static GeoRSSNode updated(Object ... objects) throws ThinklabException {
		return (GeoRSSNode) node(new GeoRSSNode(), "updated", objects);
	}
	
	public static GeoRSSNode link(Object ... objects) throws ThinklabException {
		return (GeoRSSNode) node(new GeoRSSNode(), "link", objects);
	}
	
	public static GeoRSSNode title(Object ... objects) throws ThinklabException {
		return (GeoRSSNode) node(new GeoRSSNode(), "title", objects);
	}
	
	public static GeoRSSNode entry(Object ... objects) throws ThinklabException {
		return (GeoRSSNode) node(new GeoRSSNode(), "entry", objects);
	}
	
	public static GeoRSSNode summary(Object ... objects) throws ThinklabException {
		return (GeoRSSNode) node(new GeoRSSNode(), "summary", objects);
	}
	
	public static GeoRSSNode point(Object ... objects) throws ThinklabException {
		return (GeoRSSNode) node(new GeoRSSNode(), "georss:point", objects);
	}
	

}
