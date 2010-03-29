package org.integratedmodelling.utils.xml;

import org.integratedmodelling.thinklab.exception.ThinklabException;

/**
 * Who knew you could have fun writing html.
 * 
 * 
 * @author Ferdinando Villa
 * 
 */
public class HTML extends XML {

	public static class HtmlNode extends XmlNode {

		private static final long serialVersionUID = -7718272313696083552L;
		
		public HtmlNode href(String href) {
			return (HtmlNode) attr("href", href);
		}
		
		public HtmlNode id(String href) {
			return (HtmlNode) attr("id", href);
		}
		
		public HtmlNode clazz(String href) {
			return (HtmlNode) attr("class", href);
		}
		
		public HtmlNode src(String href) {
			return (HtmlNode) attr("src", href);
		}
		
		public HtmlNode target(String href) {
			return (HtmlNode) attr("target", href);
		}
		
		public HtmlNode size(String href) {
			return (HtmlNode) attr("size", href);
		}
		
		public HtmlNode width(String href) {
			return (HtmlNode) attr("width", href);
		}
		
		public HtmlNode height(String href) {
			return (HtmlNode) attr("height", href);
		}
		
		public HtmlNode color(String href) {
			return (HtmlNode) attr("color", href);
		}
		
		public HtmlNode align(String href) {
			return (HtmlNode) attr("align", href);
		}
		
		public HtmlNode border(String href) {
			return (HtmlNode) attr("border", href);
		}
		
		public HtmlNode cellspacing(String href) {
			return (HtmlNode) attr("cellspacing", href);
		}
		
		public HtmlNode cellpadding(String href) {
			return (HtmlNode) attr("cellpadding", href);
		}
		
		public HtmlNode rel(String href) {
			return (HtmlNode) attr("rel", href);
		}
		
		public HtmlNode type(String href) {
			return (HtmlNode) attr("type", href);
		}
	}
	
	public static HtmlNode body(Object ... objects) throws ThinklabException {
		return (HtmlNode) node(new HtmlNode(), "body", objects);
	}
	
	public static HtmlNode table(Object ... objects) throws ThinklabException {
		return (HtmlNode) node(new HtmlNode(), "table", objects);
	}
	
	public static HtmlNode div(Object ... objects) throws ThinklabException {
		return (HtmlNode) node(new HtmlNode(), "div", objects);
	}
	
	public static HtmlNode b(Object ... objects) throws ThinklabException {
		return (HtmlNode) node(new HtmlNode(), "b", objects);
	}
	
	public static HtmlNode em(Object ... objects) throws ThinklabException {
		return (HtmlNode) node(new HtmlNode(), "em", objects);
	}
	
	public static HtmlNode i(Object ... objects) throws ThinklabException {
		return (HtmlNode) node(new HtmlNode(), "i", objects);
	}
	
	public static HtmlNode img(Object ... objects) throws ThinklabException {
		return (HtmlNode) node(new HtmlNode(), "img", objects);
	}
	
	public static HtmlNode tr(Object ... objects) throws ThinklabException {
		return (HtmlNode) node(new HtmlNode(), "tr", objects);
	}
	
	public static HtmlNode th(Object ... objects) throws ThinklabException {
		return (HtmlNode) node(new HtmlNode(), "tr", objects);
	}
	
	public static HtmlNode td(Object ... objects) throws ThinklabException {
		return (HtmlNode) node(new HtmlNode(), "td", objects);
	}
	
	public static HtmlNode meta(Object ... objects) throws ThinklabException {
		return (HtmlNode) node(new HtmlNode(), "meta", objects);
	}
	
	public static HtmlNode script(Object ... objects) throws ThinklabException {
		return (HtmlNode) node(new HtmlNode(), "script", objects);
	}
	
	public static HtmlNode title(Object ... objects) throws ThinklabException {
		return (HtmlNode) node(new HtmlNode(), "title", objects);
	}
	
	public static HtmlNode span(Object ... objects) throws ThinklabException {
		return (HtmlNode) node(new HtmlNode(), "span", objects);
	}
	
	public static HtmlNode html(Object ... objects) throws ThinklabException {
		return (HtmlNode) node(new HtmlNode(), "html", objects);
	}
	
	public static HtmlNode link(Object ... objects) throws ThinklabException {
		return (HtmlNode) node(new HtmlNode(), "link", objects);
	}
	
	public static HtmlNode ol(Object ... objects) throws ThinklabException {
		return (HtmlNode) node(new HtmlNode(), "ol", objects);
	}
	
	public static HtmlNode ul(Object ... objects) throws ThinklabException {
		return (HtmlNode) node(new HtmlNode(), "ul", objects);
	}
	
	public static HtmlNode li(Object ... objects) throws ThinklabException {
		return (HtmlNode) node(new HtmlNode(), "li", objects);
	}
	
	public static HtmlNode head(Object ... objects) throws ThinklabException {
		return (HtmlNode) node(new HtmlNode(), "head", objects);
	}
	
	public static HtmlNode blockquote(Object ... objects) throws ThinklabException {
		return (HtmlNode) node(new HtmlNode(), "blockquote", objects);
	}
	
	public static HtmlNode p(Object ... objects) throws ThinklabException {
		return (HtmlNode) node(new HtmlNode(), "p", objects);
	}
	
	public static HtmlNode u(Object ... objects) throws ThinklabException {
		return (HtmlNode) node(new HtmlNode(), "u", objects);
	}
	
	public static HtmlNode h1(Object ... objects) throws ThinklabException {
		return (HtmlNode) node("h1", objects);
	}
	
	public static HtmlNode h2(Object ... objects) throws ThinklabException {
		return (HtmlNode) node("h2", objects);
	}
	
	public static HtmlNode h3(Object ... objects) throws ThinklabException {
		return (HtmlNode) node("h3", objects);
	}
	
	public static HtmlNode h4(Object ... objects) throws ThinklabException {
		return (HtmlNode) node("h4", objects);
	}
	
	public static HtmlNode h5(Object ... objects) throws ThinklabException {
		return (HtmlNode) node("h5", objects);
	}
	
	public static HtmlNode a(Object ... objects) throws ThinklabException {
		return (HtmlNode) node("a", objects);
	}

	public static HtmlNode hr() throws ThinklabException {
		return (HtmlNode) node("hr", (Object[]) null);
	}

	public static HtmlNode br() throws ThinklabException {
		return (HtmlNode) node("br", (Object[]) null);
	}
	
}
