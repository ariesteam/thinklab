package org.integratedmodelling.modelling.storyline;

import java.awt.image.BufferedImage;

import javax.swing.tree.DefaultMutableTreeNode;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.geospace.extents.ArealExtent;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.geospace.visualization.GeoImageFactory;
import org.integratedmodelling.modelling.visualization.presentation.PresentationFactory;
import org.integratedmodelling.modelling.visualization.presentation.PresentationTemplate;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.Pair;

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
public abstract class Storyline extends DefaultMutableTreeNode {

	private static final long serialVersionUID = -1201242975327831908L;
	
	protected IContext      context;
	protected ISession      session;
	protected PresentationTemplate template;
	
	private double contextArea = -1.0;
	private double percentCovered = -1.0;

	ShapeValue coverage = null;
	boolean coverageComputed = false;
	BufferedImage coverageMap = null;
	
	protected void setTemplate(PresentationTemplate presentation) {
		this.template = presentation;
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
			ret = start.template().getAttribute(property);
			if (ret != null)
				break;
			start = (Storyline) start.getParent();
		}
		return ret;
	}

	public PresentationTemplate template() {
		
		if (template == null) {
			template = PresentationFactory.getPresentation(getObservable());
			processTemplate(template);
		}
		
		if (template == null)
			throw new ThinklabRuntimeException("internal error: no presentation template for " + getObservable());
				
		return template;
	}
	
	protected abstract void processTemplate(PresentationTemplate template);

	public IContext getContext() {
		return context;
	}
	
	public IConcept getObservable() {
		return template().getConcept();
	}
}
