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
	
	public static GeoRSSNode div(Object ... objects) throws ThinklabException {
		return (GeoRSSNode) node(new GeoRSSNode(), "div", objects);
	}
	
	public static GeoRSSNode id(Object ... objects) throws ThinklabException {
		return (GeoRSSNode) node(new GeoRSSNode(), "id", objects);
	}
	
	public static GeoRSSNode email(Object ... objects) throws ThinklabException {
		return (GeoRSSNode) node(new GeoRSSNode(), "email", objects);
	}
	
	public static GeoRSSNode name(Object ... objects) throws ThinklabException {
		return (GeoRSSNode) node(new GeoRSSNode(), "name", objects);
	}
	
	public static GeoRSSNode author(Object ... objects) throws ThinklabException {
		return (GeoRSSNode) node(new GeoRSSNode(), "author", objects);
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
