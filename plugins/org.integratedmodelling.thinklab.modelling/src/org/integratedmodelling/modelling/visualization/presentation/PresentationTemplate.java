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
	private String shortDescription;
	private String runningHead;
	private String concept;
	private String style;
	public HashMap<String, String> attributes = new HashMap<String, String>();

	public ArrayList<Node> customNodes = new ArrayList<Node>();

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
		public String plotType;
		public ArrayList<Node> customNodes = new ArrayList<Node>();
		public ArrayList<String> otherTypes = new ArrayList<String>();
		public HashMap<String, String> attributes = new HashMap<String, String>();
		public String credits;
		public String seeAlso;
		
		public String descriptionTitle = "Description";
		public String creditsTitle = "Credits";
		public String seeAlsoTitle = "See Also";
		
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
		public String getPlotType() {
			return plotType;
		}
		public String getCredits() {
			return credits;
		}
		public String getSeeAlso() {
			return seeAlso;
		}
		public String getDescriptionTitle() {
			return descriptionTitle;
		}
		public String getCreditsTitle() {
			return creditsTitle;
		}
		public String getSeeAlsoTitle() {
			return seeAlsoTitle;
		}
		public String getAttribute(String s) {
			return attributes.get(s);
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
			} else if (node.getNodeName().equals("short-description")) {
				this.shortDescription = XMLDocument.getNodeValue(node);
			}  else if (node.getNodeName().equals("runninghead")) {
				this.runningHead = XMLDocument.getNodeValue(node);
			} else if (node.getNodeName().equals("concept")) {
				this.concept = XMLDocument.getNodeValue(node).trim();
			} else if (node.getNodeName().equals("style")) {
				this.style = XMLDocument.getNodeValue(node);
			} else {
				
				/*
				 * any content of custom nodes goes in attributes. The node is 
				 * preserved if structural analysis is required.
				 */
				String ss = XMLDocument.getNodeValue(node);
				if (ss != null && !ss.isEmpty()) {
					attributes.put(node.getNodeName(), ss);
				}
				
				/*
				 * custom nodes: keep with the page for now. This is quite inelegant as the XML doc
				 * doesn't get garbage collected, but polymorphism at this stage is worse. FIXME 
				 */
				customNodes.add(node);
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
				String attr = XMLDocument.getAttributeValue(node, "title");
				if (attr != null)
					page.descriptionTitle = attr;
			} else if (node.getNodeName().equals("runninghead")) {
				page.runningHead = XMLDocument.getNodeValue(node);
			} else if (node.getNodeName().equals("concept")) {
				page.concept = XMLDocument.getNodeValue(node).trim();
			} else if (node.getNodeName().equals("background")) {
				page.background = XMLDocument.getNodeValue(node);
			} else if (node.getNodeName().equals("name")) {
				page.name = XMLDocument.getNodeValue(node);
			} else if (node.getNodeName().equals("plot-type")) {
				
				String attr = XMLDocument.getAttributeValue(node, "default");
				if (attr != null && attr.equals("true")) {
					page.plotType = XMLDocument.getNodeValue(node);
				} else {
					page.otherTypes.add(XMLDocument.getNodeValue(node));
				}
			} else if (node.getNodeName().equals("credits")) {
				page.credits = XMLDocument.getNodeValue(node);
				String attr = XMLDocument.getAttributeValue(node, "title");
				if (attr != null)
					page.creditsTitle = attr;
			} else if (node.getNodeName().equals("see-also")) {
				page.seeAlso = XMLDocument.getNodeValue(node);
				String attr = XMLDocument.getAttributeValue(node, "title");
				if (attr != null)
					page.seeAlsoTitle = attr;
			} else if (node.getNodeName().equals("id")) {
				page.id = XMLDocument.getNodeValue(node);
			} else {
				
				/*
				 * any content of custom nodes goes in attributes. The node is 
				 * preserved if structural analysis is required.
				 */
				String ss = XMLDocument.getNodeValue(node);
				if (ss != null && !ss.isEmpty()) {
					page.attributes.put(node.getNodeName(), ss);
				}
				
				/*
				 * custom nodes: keep with the page for now. This is quite inelegant as the XML doc
				 * doesn't get garbage collected, but polymorphism at this stage is worse. FIXME 
				 */
				page.customNodes.add(node);
			}
		}
		if (page.id != null) {
			singlePages.add(page);
			singlePagesById.put(page.id, page);
		} else {
			pages.add(page);
		}
	}

	public String getAttribute(String s) {
		return attributes.get(s);
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
	
	public ArrayList<Node> getCustomNodes() {
		return customNodes;
	}

	public String getShortDescription() {
		return shortDescription;
	}
}
