package org.integratedmodelling.modelling.visualization.presentation;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.xml.XMLDocument;
import org.w3c.dom.Node;

/**
 * A descriptor for a "storyline". Serializable to/from XML. It's for now limited to one
 * concept per page and associated (optional) visuals. Provides sequence and basic 
 * content, no layout, which must be supplied externally through Presentations.
 * 
 * Sequence and off-sequence pages can be defined (to be interpreted by the view). The 
 * off-sequence pages can be found by ID, the sequenced ones by sequence number.
 * 
 * @author ferdinando.villa
 *
 */
public class PresentationTemplate {

	ArrayList<Page> pages = new ArrayList<PresentationTemplate.Page>();
	ArrayList<Page> singlePages = new ArrayList<PresentationTemplate.Page>();
	HashMap<String, Page> singlePagesById = new HashMap<String, PresentationTemplate.Page>();
	private String title;
	private String description;
	private String runningHead;
	private String concept;
	private String style;
	
	@Override
	public String toString() {	
		return "[presentation: " + title + ": " + concept + "]"; 
	}
	
	public class Page {

		public String concept;
		public String title;
		public String description;
		public String runningHead;
		public String background;
		public String name;
		public String id;
		public ArrayList<Node> customNodes = new ArrayList<Node>();
		
		public int sequence = -1;
		
		public IConcept getConcept() {
			try {
				return KnowledgeManager.get().requireConcept(concept);
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);
			}
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
			} else {
				
				/*
				 * custom nodes: keep with the page for now. This is quite inelegant as the XML doc
				 * doesn't get garbage collected, but polymorphism at this stage is worse. FIXME 
				 */
				page.customNodes.add(node);
			}
		}
		if (page.sequence < 0) {
			singlePages.add(page);
			singlePagesById.put(page.id, page);
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
	
	public int getPagesCount() {
		return pages.size();
	}
	
	public Page getPage(String id) {
		return singlePagesById.get(id);
	}
	
	public Page getPage(int seq) {
		return pages.get(seq);
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
	
}
