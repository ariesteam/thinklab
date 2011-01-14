package org.integratedmodelling.modelling.visualization.storyline;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.integratedmodelling.modelling.storyline.StorylineFactory;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.NameGenerator;
import org.integratedmodelling.utils.Pair;
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
public class StorylineTemplate {

	ArrayList<Page> pages = new ArrayList<StorylineTemplate.Page>();
	ArrayList<Page> singlePages = new ArrayList<StorylineTemplate.Page>();
	HashMap<String, Page> singlePagesById = new HashMap<String, StorylineTemplate.Page>();

	private String concept;
	
	/*
	 * a pretty primitive way of storing model specs: alternating strings are
	 * model name and context name for coverage. If a context name is null model works
	 * globally.
	 */
	private ArrayList<String> models = null;

	private String id;
	
	private Properties properties = new Properties();
	
	public HashMap<String, String> attributes    = new HashMap<String, String>();
	public HashMap<String, String> attrAttributes = new HashMap<String, String>();

	private boolean _synchronized;

	@Override
	public String toString() {	
		return "[presentation: " + getTitle() + ": " + concept + "]"; 
	}
	
	/**
	 * Properties collects the content of all fields that have a simple
	 * text content.
	 * 
	 * @return
	 */
	public Properties getProperties() {
		return this.properties;
	}
	
	/**
	 * all templates have an ID. If not there, it's assigned an ugly unique
	 * name.
	 */
	public String getId() {
		if (id == null)
			id = NameGenerator.newName("stempl");
		return id;
	}
		
	public class Page {

		public String concept;
		public String id;
		
		
		public ArrayList<String> otherTypes = new ArrayList<String>();
		public HashMap<String, String> attributes = new HashMap<String, String>();
		public HashMap<String, String> attrAttributes = new HashMap<String, String>();
		
		public IConcept getConcept() {
			try {
				return KnowledgeManager.get().requireConcept(concept);
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);
			}
		}
		public String getTitle() {
			return getAttribute("title");
		}
		public String getDescription() {
			return getAttribute("description");
		}
		public String getRunningHead() {
			return getAttribute("runninghead");
		}
		public String getBackground() {
			return getAttribute("background");
		}
		public String getName() {
			return getAttribute("name");
		}
		public String getId() {
			return id;
		}
		public String getPlotType() {
			return getAttribute("plot-type");
		}
		public String getCredits() {
			return getAttribute("credits");
		}
		public String getSeeAlso() {
			return getAttribute("see-also");
		}
		public String getDescriptionTitle() {
			return getAttributeAttribute("description", "title");
		}
		public String getCreditsTitle() {
			return getAttributeAttribute("credits", "title");
		}
		public String getSeeAlsoTitle() {
			return getAttributeAttribute("see-also", "title");
		}
		public String getAttribute(String s) {
			return attributes.get(s);
		}
		public String getAttributeAttribute(String attr, String spec) {
			return attrAttributes.get(attr + "|" + spec);
		}
	}
	
	public void read(URL input) throws ThinklabException {
		
		XMLDocument doc = new XMLDocument(input);
		
		for (XMLDocument.NodeIterator it = doc.iterator(); it.hasNext(); ) {
			Node node = it.next();
			if (node.getNodeName().equals("page")) {
				addPage(doc, node);
			} else if (node.getNodeName().equals("id")) {
				this.id = XMLDocument.getNodeValue(node);
				properties.put("id", this.id);
			} else if (node.getNodeName().equals("concept")) {
				this.concept = XMLDocument.getNodeValue(node).trim();
				properties.put("concept", this.concept);
			} else 	if (node.getNodeName().equals("model")) {
				String id = XMLDocument.getAttributeValue(node, "id");
				String ct = XMLDocument.getAttributeValue(node, "context");
				if (this.models == null) {
					this.models = new ArrayList<String>();
				}
				models.add(id);
				models.add(ct);
				
			} else {
				
				/*
				 * any content of custom nodes goes in attributes. The node is 
				 * preserved if structural analysis is required.
				 */
				String ss = XMLDocument.getNodeValue(node);
				if (ss != null && !ss.isEmpty()) {
					attributes.put(node.getNodeName(), ss);
					properties.put(node.getNodeName(), ss);
					for (Pair<String, String> ap : XMLDocument.getNodeAttributes(node)) {
						attrAttributes.put(
							node.getNodeName() + "|" + ap.getFirst(),
							ap.getSecond());
					}
				}
			}
		}
	}

	private void addPage(XMLDocument doc, Node root) {

		Page page = new Page();
		for (XMLDocument.NodeIterator ct = doc.iterator(root); ct.hasNext(); ) {
			Node node = ct.next();
			if (node.getNodeName().equals("type")) {
			} else if (node.getNodeName().equals("concept")) {
				page.concept = XMLDocument.getNodeValue(node).trim();
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
					for (Pair<String, String> ap : XMLDocument.getNodeAttributes(node)) {
						attrAttributes.put(
							node.getNodeName() + "|" + ap.getFirst(),
							ap.getSecond());
					}
				}
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
	
	public String getAttributeAttribute(String attr, String spec) {
		return attrAttributes.get(attr + "|" + spec);
	}
	
	public ArrayList<Page> getPages() {
		syncPages();
		return pages;
	}

	private void syncPages() {

		/*
		 * if we're inheriting pages from another template, ensure
		 * we have them synchronized.
		 */
		if (!_synchronized) {
			
			String inherited = getAttribute("inherit");
			
			if (inherited != null) {
				StorylineTemplate t = StorylineFactory.getPresentation(inherited);
				
				for (Page p : t.getSinglePages()) {
					if (getPage(p.getId()) == null) {
						singlePages.add(p);
						singlePagesById.put(p.getId(), p);
					}
				}
				for (Page p : t.getPages()) {
					boolean hasIt = false;
					for (Page op : getPages()) {
						if (op.getConcept().equals(p.getConcept())) {
							hasIt = true;
							break;
						}
					}
					if (!hasIt)
						pages.add(p);
				}
			}
			
			_synchronized = true;
		}
	}

	public ArrayList<Page> getSinglePages() {
		syncPages();
		return singlePages;
	}
	
	public int getPagesCount() {
		syncPages();
		return pages.size();
	}
	
	public Page getPage(String id) {
		syncPages();
		return singlePagesById.get(id);
	}
	
	public Page getPage(int seq) {
		syncPages();
		return pages.get(seq);
	}

	public String getTitle() {
		return getAttribute("title");
	}

	public String getDescription() {
		return getAttribute("description");
	}

	public String getRunningHead() {
		return getAttribute("runninghead");
	}

	public IConcept getConcept() {
		try {
			return KnowledgeManager.get().requireConcept(concept);
		} catch (ThinklabException e) {
			throw new ThinklabRuntimeException(e);
		}
	}

	public String getStyle() {
		return getAttribute("style");
	}
	

	public String getShortDescription() {
		return getAttribute("short-description");
	}
	
	public List<String> getModelSpecifications() {
		return this.models;
	}
}
