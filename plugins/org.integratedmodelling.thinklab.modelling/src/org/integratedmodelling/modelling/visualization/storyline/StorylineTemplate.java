package org.integratedmodelling.modelling.visualization.storyline;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.model.ModelFactory;
import org.integratedmodelling.modelling.storyline.StorylineFactory;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.NameGenerator;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.beans.BeanObject;
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
 * TODO this shoud be simply implemented using javabeans 
 * 
 * @author ferdinando.villa
 *
 */
public class StorylineTemplate extends BeanObject {

	private String id;
	private boolean _synchronized;
	
	ArrayList<Page> pages = null;
	
	@Override
	public String toString() {	
		return "[presentation: " + getTitle() + ": " + get("concept") + "]"; 
	}
	
	/**
	 * all templates must have an ID. If not given in configuration, it's assigned an ugly unique
	 * name.
	 */
	public String getId() {
		
		if (id == null)
			id = get("id");
		if (id == null) {
			id = NameGenerator.newName("stempl");
		}
		return id;
	}
	
	public static class Model extends BeanObject {
		
		public IModel getModel() throws ThinklabException {
			return ModelFactory.get().requireModel(get("id"));
		}
		
		public IContext getContext() throws ThinklabException {
			String ctx = get("context");
			return 
				ctx == null ?
					null :
					ModelFactory.get().requireContext(ctx);
		}
	}
		
	public static class Page extends BeanObject {

		public IConcept getConcept() {
			try {
				return KnowledgeManager.get().requireConcept(get("concept"));
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);
			}
		}
		public String getTitle() {
			return get("title");
		}
		public String getDescription() {
			return get("description");
		}
		public String getRunningHead() {
			return get("runninghead");
		}
		public String getName() {
			return get("name");
		}

		public String getPlotType() {
			return get("plot-type");
		}
		public String getCredits() {
			return get("credits");
		}
		public String getSeeAlso() {
			return get("see-also");
		}
		public String getDescriptionTitle() {
			return getAttribute("description", "title");
		}
		public String getCreditsTitle() {
			return getAttribute("credits", "title");
		}
		public String getSeeAlsoTitle() {
			return getAttribute("see-also", "title");
		}
	}

	public static class Cast<B, T> {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Collection<T> cast(Collection<B> b) {
			return (Collection<T>)(Collection)b;
		}
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public ArrayList<T> cast(ArrayList<B> b) {
			return (ArrayList<T>)(ArrayList)b;
		}
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public HashSet<T> cast(HashSet<B> b) {
			return (HashSet<T>)(HashSet)b;
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection<Page> getPages() {
		
		if (this.pages == null) {
			syncPages();
			Collection<BeanObject> ret1 = getAllObjectsWithoutField("page", "id");
			this.pages = (ArrayList)ret1;
		}
		return this.pages;
	}

	private void syncPages() {

		/*
		 * if we're inheriting pages from another template, ensure
		 * we have them synchronized.
		 */
		if (!_synchronized) {
			
			String inherited = get("inherit");
			
			if (inherited != null) {
				StorylineTemplate t = StorylineFactory.getPresentation(inherited);
				
				for (Page p : t.getSinglePages()) {
					
					if (getObjectWith("page", "id", p.get("id")) == null) {
						addChild("page", p, t.getAttributes("page","id",p.get("id")));
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Collection<Page> getSinglePages() {
		syncPages();
		Collection<BeanObject> ret1 = getAllObjectsWithField("page", "id");
		Collection<Page> ret = (Collection)ret1;
		return ret;
	}
	
	public int getPagesCount() {
		return getPages().size();
	}
	
	public Page getPage(String id) {
		syncPages();
		return (Page)getObjectWithField("page", "id", id);
	}
	
	public Page getPage(int seq) {
		return ((ArrayList<Page>)getPages()).get(seq);
	}

	public String getTitle() {
		return get("title");
	}

	public String getDescription() {
		return get("description");
	}

	public String getRunningHead() {
		return get("runninghead");
	}

	public IConcept getConcept() {
		try {
			return KnowledgeManager.get().requireConcept(get("concept"));
		} catch (ThinklabException e) {
			throw new ThinklabRuntimeException(e);
		}
	}
	
	public String getShortDescription() {
		return get("short-description");
	}
	
	public Collection<Model> getModelSpecifications() {
		return new Cast<BeanObject, Model>().cast(getAllObjects("model"));
	}
	
	public void read(String s) throws ThinklabException {
		HashMap<String, Class<? extends BeanObject>> cls = 
			new HashMap<String, Class<? extends BeanObject>>();
		cls.put("page",  Page.class);
		cls.put("model", Model.class);
		read(s, cls);
	}
}
