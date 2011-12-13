package org.integratedmodelling.modelling.context;

import java.util.ArrayList;
import java.util.Map;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.internal.IContextTransformation;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.geospace.gis.ThinklabRasterizer;
import org.integratedmodelling.geospace.interfaces.IGridMask;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.Symbol;

/**
 * Created by the "transform" form. Can apply a variety of transformations
 * to a state, optionally using generalized context and value filters.
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
	private boolean isNull = false;
	
	public FilteredTransformation(IConcept concept, Object value) {
		this.concept = concept;
		this.value = interpretValue(value);
	}
	
	private FilteredTransformation(IConcept concept, Object value, ArrayList<Object> filters) {
		this(concept, value);
		this.filters = filters;
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
		
		/*
		 *  only allow to convert a nodata if there is explicit code that 
		 *  can handle it. Will not turn nodata into data otherwise.
		 */
		if (original == null && closure == null)
			return null;
		
		if (!_initialized)
			try {
				initialize(context);
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);
			}
		
		if (match(original, context, stateIndex)) {
			try {
				return closure == null ? value : closure.invoke(original);
			} catch (Exception e) {
				throw new ThinklabRuntimeException(e);
			}
		}
		
		return original;
	}

	private boolean match(Object original, IContext context, int stateIndex) {
		
		if (this.activationLayer != null) {
			if (!this.activationLayer.isActive(stateIndex))
				return false;
		} 
		
		for (Object filter : filters) {
			
			if (filter instanceof IFn && !(filter instanceof Keyword)) {
				Object o = null;
				try {
					o = ((IFn)filter).invoke(original);
				} catch (Exception e) {
					throw new ThinklabRuntimeException(e);
				}
				if (o == null || (o instanceof Boolean && !((Boolean)o)))
					return false;
			} else if (filter instanceof Number && original instanceof Number) {
				if (!((Number)filter).equals((Number)original))
					return false;
			} else if (filter instanceof IConcept && original instanceof IConcept) {
				if (! ((IConcept)original).equals((IConcept)filter))
					return false;
			} else if (filter instanceof String && original instanceof String) {
				if (! ((String)filter).equals((String)original))
					return false;
			} else if (filter instanceof Boolean) {
				if (!((Boolean)filter))
					return false;
			}
		}
		
		return true;
	}

	private void initialize(IContext context) throws ThinklabException {
		
		/*
		 * check out all filters and build something we can used when transform() is called.
		 */
		ShapeValue shape = null;
		boolean invert = false;
		
		for (Object f : filters) {

			if (f instanceof Keyword && ((Keyword)f).toString().equals(":except")) {
				invert = true;
			}
			
			if (f instanceof ShapeValue && context.getSpace() instanceof GridExtent) {
				
				if (shape == null)
					shape = (ShapeValue)f;
				else 
					shape = shape.union((ShapeValue)f);
			} 
		}

		// union all shapes we got
		if (shape != null) {
			try {
				this.activationLayer = 
					ThinklabRasterizer.createMask(shape, 
						(GridExtent)context.getSpace());
			
				if (invert) {
					this.activationLayer.invert();
				}
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);
			}			
		}
		
		_initialized = true;
	}

	@Override
	public IContextTransformation newInstance() {
		return new FilteredTransformation(concept, value, filters);
	}

	@Override
	public IConcept getObservableClass() {
		return this.concept;
	}
	
	public void addFilter(Object o) throws ThinklabException {
		
		if (o instanceof ShapeValue || 
	        o instanceof Keyword ||
			o instanceof IFn ||
			o instanceof Number ||
			o instanceof Boolean ||
			o instanceof String) {
			this.filters.add(o);
		} else if (o == null) {			
			ModellingPlugin.get().logger().warn(
					"filter expression in transformation evaluates to nil: transformation invalidates context");
			isNull = true;
		} else {
			throw new ThinklabValidationException(
					"defcontext/transform: don't know how to use " + o + " as a filter");
		}
	}

	@Override
	public boolean isNull() {
		return isNull;
	}

}
