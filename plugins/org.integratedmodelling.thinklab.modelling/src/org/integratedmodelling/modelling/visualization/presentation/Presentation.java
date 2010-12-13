package org.integratedmodelling.modelling.visualization.presentation;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import org.integratedmodelling.modelling.interfaces.IVisualization;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.utils.xml.XMLDocument;
import org.w3c.dom.Node;

/**
 * A descriptor for a "storyline". Serializable to/from XML. It's for now limited to one
 * concept per page and associated (optional) visuals. Provides sequence and basic 
 * content, no layout for now.
 * 
 * Sequence and off-sequence pages can be defined (to be interpreted by the view).
 * 
 * @author ferdinando.villa
 *
 */
public class Presentation {

	ArrayList<Page> pages = new ArrayList<Presentation.Page>();
	ArrayList<Page> singlePages = new ArrayList<Presentation.Page>();
	private String title;
	private String description;
	private String runningHead;
	private String concept;
	private String style;
	
	public class Page {

		public String concept;
		public String title;
		public String description;
		public String runningHead;
		public String background;
		public String name;
		public String id;
		public int sequence = -1;
		
		public void setup(File directory, IVisualization visual) {
			
		}
		
		public String getConcept() {
			return concept;
		}
		public String getTitle() {
			return title;
		}
		public String getDescription() {
			return description;
		}
		public String getRunningHead() {
			return runningHead;
		}
		public String getBackground() {
			return background;
		}
		public String getName() {
			return name;
		}
		public String getId() {
			return id;
		}
		public int getSequence() {
			return sequence;
		}
	}
	
	public void read(URL input) throws ThinklabException {
		
		XMLDocument doc = new XMLDocument(input);
		
		for (XMLDocument.NodeIterator it = doc.iterator(); it.hasNext(); ) {
			Node node = it.next();
			if (node.getNodeName().equals("page")) {
				addPage(doc, node);
			} else if (node.getNodeName().equals("title")) {
				this.title = XMLDocument.getNodeValue(node);
			} else if (node.getNodeName().equals("description")) {
				this.description = XMLDocument.getNodeValue(node);
			} else if (node.getNodeName().equals("runninghead")) {
				this.runningHead = XMLDocument.getNodeValue(node);
			} else if (node.getNodeName().equals("concept")) {
				this.concept = XMLDocument.getNodeValue(node);
			} else if (node.getNodeName().equals("style")) {
				this.style = XMLDocument.getNodeValue(node);
			}
		}
		
	}

	private void addPage(XMLDocument doc, Node root) {

		Page page = new Page();
		for (XMLDocument.NodeIterator ct = doc.iterator(root); ct.hasNext(); ) {
			Node node = ct.next();
			if (node.getNodeName().equals("type")) {
			} else if (node.getNodeName().equals("title")) {
				page.title = XMLDocument.getNodeValue(node);
			} else if (node.getNodeName().equals("description")) {
				page.description = XMLDocument.getNodeValue(node);
			} else if (node.getNodeName().equals("runninghead")) {
				page.runningHead = XMLDocument.getNodeValue(node);
			} else if (node.getNodeName().equals("concept")) {
				page.concept = XMLDocument.getNodeValue(node);
			} else if (node.getNodeName().equals("background")) {
				page.background = XMLDocument.getNodeValue(node);
			} else if (node.getNodeName().equals("name")) {
				page.name = XMLDocument.getNodeValue(node);
			} else if (node.getNodeName().equals("id")) {
				page.id = XMLDocument.getNodeValue(node);
			} else if (node.getNodeName().equals("sequence")) {
				page.sequence = Integer.parseInt(XMLDocument.getNodeValue(node));
			}
		}
		if (page.sequence < 0) {
			singlePages.add(page);
		} else {
			pages.ensureCapacity(page.sequence+1);
			pages.add(page.sequence, page);
		}
	}

	public ArrayList<Page> getPages() {
		return pages;
	}

	public ArrayList<Page> getSinglePages() {
		return singlePages;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getRunningHead() {
		return runningHead;
	}

	public String getConcept() {
		return concept;
	}

	public String getStyle() {
		return style;
	}
	
	/**
	 * Sets up each individual page in a persistent location, so that the actual visualization
	 * can be quickly built.
	 * 
	 * @param vis
	 */
	public void initialize(IVisualization vis) throws ThinklabException {
		
	}
}
