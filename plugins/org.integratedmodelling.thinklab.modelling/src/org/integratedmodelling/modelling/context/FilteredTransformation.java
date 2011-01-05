package org.integratedmodelling.modelling.context;

import java.util.ArrayList;
import java.util.Map;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.internal.IContextTransformation;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.geospace.gis.ThinklabRasterizer;
import org.integratedmodelling.geospace.interfaces.IGridMask;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

import clojure.lang.IFn;
import clojure.lang.Symbol;

/**
 * Created by the "transform" form. Can apply a variety of transformations
 * to a state, optionally using a generalized context filter.
 * 
 * @author ferdinando.villa
 *
 */
public class FilteredTransformation implements IContextTransformation {

	private IConcept concept;
	private Object value;
	private IFn closure;
	private ArrayList<Object> filters = new ArrayList<Object>();
	private boolean _initialized = false;
	private IGridMask activationLayer;
	
	
	
	public FilteredTransformation(IConcept concept, Object value) {
		this.concept = concept;
		this.value = interpretValue(value);
	}
	
	private Object interpretValue(Object value) {

		if (value instanceof IConcept || value instanceof String || value instanceof Number) {
			return value;
		} else if (value instanceof Symbol) {
			return KnowledgeManager.getConcept(value.toString());
		} else if (value instanceof IFn) {
			this.closure = ((IFn)value);
		}
		
		return null;
	}

	@Override
	public Object transform(Object original, IContext context, int stateIndex,
			Map<?, ?> parameters) {
		
		if (!_initialized)
			initialize(context);
		
		if (match(context, stateIndex)) {
			try {
				return closure == null ? value : closure.invoke(original);
			} catch (Exception e) {
				throw new ThinklabRuntimeException(e);
			}
		}
		
		return original;
	}

	private boolean match(IContext context, int stateIndex) {
		
		if (filters.size() == 0) {
			return true;
		}

		boolean ret = false;
		
		if (this.activationLayer != null) {
			ret = this.activationLayer.isActive(stateIndex);
		} // TODO add other possible context selectors
		
		return ret;
	}

	private void initialize(IContext context) {
		
		/*
		 * check out all filters and build something we can used when transform() is called.
		 */
		for (Object f : filters) {
			if (f instanceof ShapeValue && context.getSpace() instanceof GridExtent) {
				// if shape, rasterize in context to build mask
				try {
					this.activationLayer = ThinklabRasterizer.createMask((ShapeValue)f, (GridExtent)context.getSpace());
				} catch (ThinklabException e) {
					throw new ThinklabRuntimeException(e);
				}			
			}
		}
	}

	@Override
	public IContextTransformation newInstance() {
		return new FilteredTransformation(concept, value);
	}

	@Override
	public IConcept getObservableClass() {
		return this.concept;
	}
	
	public void addFilter(Object o) throws ThinklabException {
		
		if (o instanceof ShapeValue) {
			this.filters.add(o);
		} else {
			throw new ThinklabValidationException(
					"defcontext/transform: don't know how to use " + o + " as a filter");
		}
	}

}
