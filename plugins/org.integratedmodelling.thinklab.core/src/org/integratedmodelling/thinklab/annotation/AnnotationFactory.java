package org.integratedmodelling.thinklab.annotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabInternalErrorException;
import org.integratedmodelling.exceptions.ThinklabUnimplementedFeatureException;
import org.integratedmodelling.lang.Semantics;
import org.integratedmodelling.list.PolyList;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.annotations.Property;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticLiteral;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.interfaces.knowledge.datastructures.IntelligentMap;

/**
 * Class doing the hard work of instantiation and conceptualization from class
 * annotations. Implements these functionalities for the KnowledgeManager.
 * 
 * @author Ferd
 *
 */
public class AnnotationFactory {

	IntelligentMap<Class<?>> _concept2class    = new IntelligentMap<Class<?>>();
	HashMap<Class<?>, Class<?>> _class2literal    = new HashMap<Class<?>, Class<?>>();
	HashMap<Class<?>, IConcept>_class2datatype   = new HashMap<Class<?>, IConcept>();
	HashMap<Class<?>, IConcept> _class2concept = new HashMap<Class<?>, IConcept>();
	
	/*
	 * -----------------------------------------------------------------------------
	 * register knowledge with factory
	 * -----------------------------------------------------------------------------
	 */
	
	/*
	 * -----------------------------------------------------------------------------
	 * the actually useful methods
	 * -----------------------------------------------------------------------------
	 */
	
	public Semantics conceptualize(Object o) throws ThinklabException {

		if (o instanceof IConceptualizable) {
			return ((IConceptualizable) o).conceptualize();
		}

		Class<?> cls = o.getClass();
		IConcept mainc = _class2concept.get(cls);

		if (mainc == null)
			return null;

		ArrayList<Object> sa = new ArrayList<Object>();
		sa.add(mainc);

		for (Field f : cls.getFields()) {
			if (f.isAnnotationPresent(Property.class)) {

				Property pann = f.getAnnotation(Property.class);
				
				IProperty p = Thinklab.get().getProperty(pann.value());

				if (p != null) {
					try {
						Object value = f.get(o);

						for (Object v : getAllInstances(value)) {
							
							/*
							 * special cases: Key, Pair, Triple
							 */

							/*
							 * first check if it can be stored as a literal
							 */

							/*
							 * check if we can store it as an object
							 */
							}

					} catch (Exception e) {
						throw new ThinklabInternalErrorException(e);
					}
				}
			}
		}

		return sa.size() == 0 ? null : new Semantics(
				PolyList.fromCollection(sa), Thinklab.get());
	}
	
	private Collection<Object> getAllInstances(Object value) {

		Collection<Object> ret = null;
		if (value.getClass().isArray()) {
			ret = Arrays.asList((Object[])value);
		} else if (value instanceof Collection<?>) {
			ret = (Collection<Object>)(value);
		} else {
			ret = Collections.singleton(value);
		}
		return ret;
	}

	public Object instantiate(Semantics annotation) throws ThinklabException {
	
		Object ret = null;
		
		/*
		 * find class. If an IConceptualizable, create object, call instantiate() and
		 * return it.
		 */
		Class<?> cls = _concept2class.get(annotation.getConcept());
		
		if (cls == null)
			return null;
		
		/*
		 * create object. Find the most appropriate constructor - if there is one with 
		 * a SemanticAnnotation use that. Otherwise find an empty constructor.
		 */
		boolean hasEmptyConstructor = false;
		for (Constructor<?> cc : cls.getConstructors()) {
			Class<?>[] pt = cc.getParameterTypes();
			if (pt.length == 1 && Semantics.class.isAssignableFrom(pt[0])) {
				try {
					ret = cc.newInstance(annotation);
					break;
				} catch (Exception e) {
					throw new ThinklabInternalErrorException(e);
				}
			}
			if (pt.length == 0)
				hasEmptyConstructor = true;
		}
		
		if (ret == null && hasEmptyConstructor) {
			try {
				ret = cls.newInstance();
			} catch (Exception e) {
				throw new ThinklabInternalErrorException(e);
			}
		}
		
		if (ret == null)
			return null;
		
		if (IConceptualizable.class.isAssignableFrom(cls)) {
			((IConceptualizable)ret).define(annotation); 
		}
		
		
		/*
		 * TODO 
		 * find and instantiate all fields
		 */
		
		/*
		 * if there is a public initialize() method, invoke it.
		 */
		try {
			Method init = cls.getMethod("initialize", (Class<?>[])null);
			if (init != null)
				init.invoke(ret, (Object[])null);
		} catch (Exception e) {
			throw new ThinklabInternalErrorException(e);			
		}
		
		return null;
	}
	
//	public ISemanticLiteral conceptualizeLiteral(Object o) throws ThinklabException {
//		
//		if (o instanceof ISemanticLiteral)
//			return (ISemanticLiteral)o;
//		
//		/*
//		 * find registered class; complain if not found
//		 */
//		Class<?> cls = _class2literal.get(o.getClass());
//		if (cls == null)
//			throw new ThinklabUnimplementedFeatureException("can't conceptualize a literal of class " + o.getClass());
//		
//		Object ret = null;
//		
//		try {
//			ret = cls.newInstance();
//		} catch (Exception e) {
//			throw new ThinklabInternalErrorException(e);
//		}
//		
//		if (ret instanceof ISemanticLiteral) {
//			
//		}
//		
//		return null;
//	}

	public ISemanticObject parse(String literal, IConcept concept) {
		// TODO Auto-generated method stub
		return null;
	}

	public ISemanticObject annotate(Object object) {
		if (object instanceof ISemanticObject)
			return (ISemanticObject)object;
		return null;
	}
}
