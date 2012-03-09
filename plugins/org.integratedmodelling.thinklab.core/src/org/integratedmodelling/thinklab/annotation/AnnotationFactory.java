package org.integratedmodelling.thinklab.annotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.collections.Triple;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabInternalErrorException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.lang.Semantics;
import org.integratedmodelling.list.PolyList;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.annotations.Property;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.lang.IParseable;
import org.integratedmodelling.thinklab.interfaces.knowledge.datastructures.IntelligentMap;
import org.integratedmodelling.thinklab.knowledge.SemanticObject;

/**
 * Class doing the hard work of instantiation and conceptualization from class
 * annotations. Implements these functionalities for the KnowledgeManager.
 * 
 * @author Ferd
 *
 */
public class AnnotationFactory {

	IntelligentMap<Class<?>> _concept2class    = new IntelligentMap<Class<?>>();
	HashMap<Class<?>, IConcept> _class2literal    = new HashMap<Class<?>, IConcept>();
	HashMap<Class<?>, String>_class2datatype   = new HashMap<Class<?>, String>();
	HashMap<Class<?>, IConcept> _class2concept = new HashMap<Class<?>, IConcept>();
	HashMap<IConcept, Class<?>> _annotatedLiteralClass =
			new HashMap<IConcept, Class<?>>();
	HashMap<String, Class<?>> _annotatedLiteralDatatype =
			new HashMap<String, Class<?>>();

	/*
	 * -----------------------------------------------------------------------------
	 * the actually useful methods
	 * -----------------------------------------------------------------------------
	 */
	
	public Semantics conceptualize(Object o) throws ThinklabException {

		if (o instanceof IConceptualizable) {
			return ((IConceptualizable) o).conceptualize();
		}

		Class<?> cls = null;
		
		/*
		 * first check if it can be stored as a literal
		 */
		if (o instanceof Pair<?,?>) {
			
			return new Semantics(
					PolyList.list(
						Thinklab.c(NS.PAIR),
						PolyList.list(Thinklab.p(NS.HAS_FIRST_FIELD), conceptualize(((Pair<?,?>)o).getFirst()).asList()),
						PolyList.list(Thinklab.p(NS.HAS_SECOND_FIELD), conceptualize(((Pair<?,?>)o).getSecond()).asList())),
					Thinklab.get());
			
		} else if (o instanceof Triple<?,?,?>) {
			
			return new Semantics(
					PolyList.list(
						Thinklab.c(NS.TRIPLE),
						PolyList.list(Thinklab.p(NS.HAS_FIRST_FIELD), conceptualize(((Triple<?,?,?>)o).getFirst()).asList()),
						PolyList.list(Thinklab.p(NS.HAS_SECOND_FIELD), conceptualize(((Triple<?,?,?>)o).getSecond()).asList()),
						PolyList.list(Thinklab.p(NS.HAS_THIRD_FIELD), conceptualize(((Triple<?,?,?>)o).getThird()).asList())),
					Thinklab.get());
			
		} else if (o instanceof Map.Entry<?,?>) {
			
			return new Semantics(
					PolyList.list(
						Thinklab.c(NS.KEY_VALUE_PAIR),
						PolyList.list(Thinklab.p(NS.HAS_FIRST_FIELD), conceptualize(((Map.Entry<?,?>)o).getKey()).asList()),
						PolyList.list(Thinklab.p(NS.HAS_SECOND_FIELD), conceptualize(((Map.Entry<?,?>)o).getValue()).asList())),
					Thinklab.get());
			
		} else {
			
			cls = o.getClass();
			IConcept literalType = _class2literal.get(cls);
			if (literalType != null) {
				return new Semantics(
						PolyList.list(literalType, o),
						Thinklab.get());
			} 	
		}
		
		/*
		 * if we get here, we need a @Concept annotation to proceed.
		 */
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
					
					Object value = null;
					try {
						value = f.get(o);
					} catch (Exception e) {
							throw new ThinklabInternalErrorException(e);
					}
					for (Object v : getAllInstances(value)) {
							
						Semantics semantics = conceptualize(v);
						if (semantics == null) {
							throw new ThinklabValidationException("cannot conceptualize field " + f.getName() + " of object " + o);
						}
						sa.add(PolyList.list(p, semantics.asList()));
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
			ret = new ArrayList<Object>();
			ret.addAll((Collection<?>)value);
		} else if (value instanceof Map<?,?>) {
			ret = new ArrayList<Object>();
			ret.addAll(((Map<?,?>)value).entrySet());
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
		
		/*
		 * TODO find all fields with property annotations and process the content of the 
		 * semantic annotation.
		 */
		for (Field f : cls.getFields()) {
			if (f.isAnnotationPresent(Property.class)) {

				Property pann = f.getAnnotation(Property.class);
				IProperty p = Thinklab.get().getProperty(pann.value());
				
				for (Semantics r : annotation.getRelationships(p)) {
					
					System.out.println("trying to attribute " + r.getTarget() + " to field " + f.getName());
				}
			}
		}
		
		if (IConceptualizable.class.isAssignableFrom(cls)) {
			((IConceptualizable)ret).define(annotation); 
		} 		
		
		/*
		 * if there is a public initialize() method with no parameters, invoke it.
		 */
		Method init = null;
		try {
			init = cls.getMethod("initialize", (Class<?>[])null);
			if (init != null)
				init.invoke(ret, (Object[])null);
		} catch (Exception e) {
			// no method, the stupid thing throws an exception instead of returning null.
		}
		
		return ret;
	}

	/**
	 * Create the semantic object from a textual representation and a concept. The concept must have
	 * been registered with a semantic object class that should either implement IParseable or have
	 * a public constructor that takes a string parameter.
	 * 
	 * @param literal
	 * @param concept
	 * @return
	 * @throws ThinklabException
	 */
	public ISemanticObject parse(String literal, IConcept concept) throws ThinklabException {

		ISemanticObject ret = null;
		Class<?> cls = _annotatedLiteralClass.get(concept);
		if (cls != null && ISemanticObject.class.isAssignableFrom(cls)) {
			
			/*
			 * it must be a IParseable or have a constructor that accepts a string
			 */
			if (IParseable.class.isAssignableFrom(cls)) {
				ret = (ISemanticObject) newInstance(cls);
				((IParseable)ret).parse(literal);
			} else {
				try {
					Constructor<?> cs = cls.getConstructor(String.class);
					ret = (ISemanticObject) cs.newInstance(literal);
				} catch (Exception e) {
					return null;
				}
			}
		}
		return ret;
	}
	
	public ISemanticObject annotate(Object object) throws ThinklabException {

		if (object instanceof ISemanticObject)
			return (ISemanticObject)object;

		ISemanticObject ret = null;
		Semantics semantics = conceptualize(object);
		if (semantics != null) {
			ret = new SemanticObject(semantics, object);
		}
		
		return ret;
	}
	
	private Object newInstance(Class<?> cls) throws ThinklabException {
		try {
			return cls.newInstance();
		} catch (Exception e) {
			throw new ThinklabInternalErrorException(e);
		}
	}
	
	/*
	 * -----------------------------------------------------------------------------
	 * register knowledge with factory
	 * -----------------------------------------------------------------------------
	 */
	

	public void registerAnnotationConcept(IConcept concept, Class<?> clls) {
		_class2concept.put(clls, concept);
		_concept2class.put(concept, clls);
	}

	public void registerLiteralAnnotation(Class<?> clls, IConcept concept,
			String datatype, Class<?> javaClass) {
		_class2literal.put(javaClass, concept);
		_class2datatype.put(javaClass, datatype);
		_annotatedLiteralClass.put(concept, clls);
		_annotatedLiteralDatatype.put(datatype, clls);
	}	
}
