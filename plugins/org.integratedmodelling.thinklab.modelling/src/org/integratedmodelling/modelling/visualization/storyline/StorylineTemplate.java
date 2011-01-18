package org.integratedmodelling.modelling.visualization.storyline;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.model.ModelFactory;
import org.integratedmodelling.modelling.storyline.StorylineFactory;
import org.integratedmodelling.modelling.visualization.wiki.WikiFactory;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.Cast;
import org.integratedmodelling.utils.NameGenerator;
import org.integratedmodelling.utils.beans.Bean;

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
public class StorylineTemplate extends Bean {

	private String id;
	private boolean _synchronized;
	
	ArrayList<Page> pages = null;
	private File sourceFile;
	
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
	
	public static class Model extends Bean {
		
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
		
	public static class Page extends Bean {

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
		
		public String getHtml(String field) {

			String content = get(field);
			String langatt = getAttribute("field", "language");
			
			if (langatt == null)
				langatt = "textile";
		
			if (langatt.equals("textile")) {
				content = WikiFactory.textileToHTML(content);
			} else if (langatt.equals("confluence")) {
				content = WikiFactory.confluenceToHTML(content);
			} else if (langatt.equals("wikimedia")) {
				content = WikiFactory.mediawikiToHTML(content);
			} else if (!langatt.equals("html")) {
				throw new ThinklabRuntimeException(
						"cannot translate language " + langatt + " into html");
			}
			
			return content;
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection<Page> getPages() {
		
		if (this.pages == null) {
			syncPages();
			Collection<Bean> ret1 = getAllObjectsWithoutField("page", "id");
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
				
				StorylineTemplate t;
				try {
					t = StorylineFactory.getStoryline(inherited).getTemplate();
				} catch (ThinklabException e) {
					throw new ThinklabRuntimeException(e);
				}
				
				for (Page p : t.getSinglePages()) {
					if (getObjectWith("page", "id", p.get("id")) == null) {
						addChild("page", (Bean)(p.clone()), null);
					}
				}
				
				for (Page p : t.getPages()) {
					if (getObjectWithField("page", "concept", p.get("concept")) == null) {
						addChild("page", (Bean)(p.clone()), null);						
					}
				}
			}
			
			_synchronized = true;
		}
	}

	public Collection<Page> getSinglePages() {
		syncPages();
		return new Cast<Bean, Page>().cast(getAllObjectsWithField("page", "id"));
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
		return new Cast<Bean, Model>().cast(getAllObjects("model"));
	}
	
	public void read(String s) throws ThinklabException {
		HashMap<String, Class<? extends Bean>> cls = 
			new HashMap<String, Class<? extends Bean>>();
		cls.put("page",  Page.class);
		cls.put("model", Model.class);
		read(s, cls);
	}

	public void setSourceFile(File f) {
		this.sourceFile = f;
	}
	
	public File getSourceFile() {
		return this.sourceFile;
	}

	public void write(String string) throws ThinklabException {
		// TODO Auto-generated method stub
		HashMap<Class<? extends Bean>, String> cls = 
			new HashMap<Class<? extends Bean>, String>();
		cls.put(Page.class, "page");
		cls.put(Model.class, "model");
		cls.put(StorylineTemplate.class, "storyline");
		write(string, cls);
	}
	
	public void save() throws ThinklabException {
		write(this.sourceFile.toString());
	}
	

	public String getSignature() {
		return
			sourceFile == null ?
				getId() :
				sourceFile + getId();

	}
}
