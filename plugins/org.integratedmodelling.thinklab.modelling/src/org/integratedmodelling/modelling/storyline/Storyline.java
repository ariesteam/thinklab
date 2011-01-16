package org.integratedmodelling.modelling.storyline;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.tree.DefaultMutableTreeNode;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.geospace.extents.ArealExtent;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.geospace.visualization.GeoImageFactory;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.interfaces.IVisualization;
import org.integratedmodelling.modelling.visualization.storyline.StorylineTemplate;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.exec.ITaskScheduler;

/**
 * Storylines are the workflow metaphor for an agent, composed of a model and
 * a context. They are arranged in a tree to reflect provenance (e.g. user-generated 
 * scenarios are children of the modified storyline that provides the baseline to 
 * compare against). They have an associated StorylineView that provides a MVC 
 * paradigm for them.
 * 
 * @author ferdinando.villa
 *
 */
public class Storyline extends DefaultMutableTreeNode {

	private static final long serialVersionUID = -1201242975327831908L;
	
	// processing status codes. Status changes are communicated to listener.
	public static final int 
		IDLE = 0, 
		COMPUTING = 1, 
		COMPUTED = 2, 
		ERROR = 3, 
		DISABLED = 4, 
		PENDING = 5;
	
	public static final String[] statusLabels = {
		"IDLE",
		"COMPUTING",
		"COMPUTED",
		"ERROR",
		"DISABLED",
		"PENDING"
	};
	
	protected IContext      context;
	protected ISession      session;
	protected StorylineTemplate template;
	protected int status = IDLE;
	
	private double contextArea = -1.0;
	private double percentCovered = -1.0;

	ShapeValue coverage = null;
	boolean coverageComputed = false;
	BufferedImage coverageMap = null;
	IVisualization visualization = null;
	
	/*
	 * the set of all possible models that can compute this storyline in specified
	 * contexts. Read from the storyline file. We can only run one at a time and they
	 * only apply to model storylines, but they need to be here because of constructor
	 * logics.
	 */
	protected ArrayList<Pair<IModel,IContext>> models = 
		new ArrayList<Pair<IModel,IContext>>();
	

	
	public static interface Listener {
		
		/*
		 * 
		 */
		public ITaskScheduler getScheduler();
		
		/*
		 * Create a visualization if you want the storyline to be visualized. One
		 * will be created for each model storyline run unless you return null here.
		 * When created, it will be stored in the storyline and will be accessible 
		 * using getVisualization().
		 * 
		 * Do not initialize the visualization - it is initialized with the same
		 * context after creation. Context is only passed to help choosing what
		 * visualization to create.
		 */
		public IVisualization createVisualization(IModel model, IContext iContext);
		
		/*
		 * called any time the computation status changes.
		 */
		public void onStatusChange(Storyline storyline, int original, int newstatus);

		/*
		 * 
		 */
		public ISession getSession();
	}
	
	public Storyline(StorylineTemplate template) {
		setTemplate(template);
	}

	public StorylineTemplate getTemplate() {
		return template;
	}
	
	protected void setTemplate(StorylineTemplate presentation) {
		processTemplate(presentation);
		this.template = presentation;
	}
	
	public void setContext(IContext context) throws ThinklabException {

		this.context = context;
		this.coverage = null;
		coverageComputed = false;
		
		for (int i = 0; i < getChildCount(); i++) {
			((Storyline)getChildAt(i)).setContext(context);
		}
	}
	
	/**
	 * Return the latest visualization created by compute(), or 
	 * null if none was created.
	 * 
	 * @return
	 */
	public IVisualization getVisualization() {
		return visualization;
	}
	
	/**
	 * Return the unioned coverage of all the storylines. 
	 * 
	 * @return
	 * @throws ThinklabException
	 */
	public ShapeValue getCoverage() throws ThinklabException {
		
		if (!coverageComputed) {
			for (int i = 0; i < getChildCount(); i++) {
				Storyline s = (Storyline) getChildAt(i);
				ShapeValue sh = s.getCoverage();
				if (sh != null) {
					if (this.coverage == null)
						this.coverage = sh;
					else this.coverage = this.coverage.union(sh);
				}
			}
			coverageComputed = true;
		} 
		
		return this.coverage;
	}

	/**
	 * Compute if necessary and return the area covered by the context in sqm and 
	 * the area in it that is covered by storylines.
	 * 
	 * @return
	 * @throws ThinklabException 
	 */
	public Pair<Double,Double> getCoverageStatistics() throws ThinklabException {
		
		if (contextArea < 0.0) {
			ShapeValue total = ((ArealExtent)context.getSpace()).getShape();
			ShapeValue cover = getCoverage();
			contextArea = total.getArea();
			percentCovered = cover.getArea()/contextArea * 100.0;
		}
		return new Pair<Double,Double>(contextArea, percentCovered);
	}
	
	public BufferedImage getCoverageMap(int x, int y) throws ThinklabException {
		
		if (coverageMap == null) {
			
			ShapeValue shape = getCoverage();
			ShapeValue total = ((ArealExtent)context.getSpace()).getShape();
			
			if (shape != null)  {
				coverageMap = 
					GeoImageFactory.get().getImagery(total.getEnvelope(), shape, x, y, GeoImageFactory.GREEN_SHAPES);
			}
		}
		return coverageMap;
	}
	
	/**
	 * Find the given property in our template, look up in the ones of each
	 * parent if not found.
	 * 
	 * @param property
	 * @return
	 */
	public String findProperty(String property)	{
		String ret = null;
		Storyline start = this;
		while (start != null) {
			ret = start.template.get(property);
			if (ret != null)
				break;
			start = (Storyline) start.getParent();
		}
		return ret;
	}

	protected void processTemplate(StorylineTemplate template) {
		
	}

	public IContext getContext() {
		return context;
	}
	
	public IConcept getObservable() {
		return template.getConcept();
	}

	/**
	 * Compute the storyline and all its children using the given listener. All it
	 * does is enqueue the tasks, if the scheduler in the listener isn't started it
	 * needs to be started from outside.
	 * 
	 * @param listener
	 * @throws ThinklabException
	 */
	public void compute(Listener listener) throws ThinklabException {
		for (int i = 0; i < getChildCount(); i++) {
			((Storyline)getChildAt(i)).compute(listener);
		}
	}
	
	@Override
	public String toString() {
		return template.getTitle() + " [" + template.getConcept() + "]";
	}

	/**
	 * Return all children as an iterable collection.
	 * 
	 * Convenience method to escape the ugliness of DefaultMutableTreeNode.
	 * 
	 * @return
	 */
	public Collection<Storyline> getChildren() {

		ArrayList<Storyline> ret = new ArrayList<Storyline>();
		for (int i = 0; i < getChildCount(); i++)
			ret.add((Storyline)getChildAt(i));
		return ret;
	}

	/**
	 * Covered by default if it has at least one covered child.
	 * 
	 * @return
	 */
	public boolean isCovered() {

		boolean ret = false;
		for (Storyline s : getChildren())
			if (ret = s.isCovered())
				break;
		
		return ret;
	}

	public boolean isAuthorized(ISession session) {
		// TODO cross-check template privileges with user properties
		return true;
	}
	
	public int getStatus() {
		return status;
	}
	
	public Storyline findStoryline(IConcept c) {
		return findStoryline(this, c);
	}
	
	private Storyline findStoryline(Storyline sl,
			IConcept concept) {
		
		if (sl.getObservable().equals(concept))
			return sl;
		for (int i = 0; i < sl.getChildCount(); i++) {
			Storyline ret = findStoryline((Storyline) sl.getChildAt(i), concept);
			if (ret != null)
				return ret;
		}
		return null;
	}

}
