package org.integratedmodelling.thinklab.annotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabInternalErrorException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.list.ReferenceList;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.annotation.utils.SKeyValue;
import org.integratedmodelling.thinklab.api.annotations.Property;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.lang.IParseable;
import org.integratedmodelling.thinklab.api.lang.IReferenceList;
import org.integratedmodelling.thinklab.interfaces.knowledge.datastructures.IntelligentMap;
import org.integratedmodelling.utils.CamelCase;
import org.integratedmodelling.utils.StringUtils;

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

	/**
	 * Assignable fields have properties associated, either through annotation or by
	 * naming convention.
	 * 
	 * @param cls
	 * @return
	 */
	private Collection<Pair<Field, IProperty>> getAssignableFields(Class<?> cls) {
		return getAssignableFieldsInternal(cls, new ArrayList<Pair<Field,IProperty>>());
	}


	private IProperty getPropertyFromFieldName(Field f, IConcept main) {

		String name = f.getName();
		Class<?> ptype = f.getClass();

		boolean multiple =
				ptype.isArray() ||
				Map.class.isAssignableFrom(ptype) ||
				Collection.class.isAssignableFrom(ptype);
		
		while (name.startsWith("_"))
			name = name.substring(1);
		
		if (multiple && name.endsWith("s")) {
			name = StringUtils.chomp(name, "s");
		}
		
		name = CamelCase.toUpperCamelCase(name, '_');
		
		/* determine if the primitive type is boolean */
		boolean isBoolean = 
				ptype.equals(Boolean.class) || ptype.getName().equals("boolean");
		
		if (!isBoolean && multiple) {
			if (ptype.isArray()) {
				isBoolean = 
						ptype.getComponentType().equals(Boolean.class) || 
						ptype.getComponentType().getName().equals("boolean");
			} else if (Map.class.isAssignableFrom(ptype)) {
				isBoolean = 
						ptype.getTypeParameters()[1].equals(Boolean.class);				
			} else if (Collection.class.isAssignableFrom(ptype)) {
				isBoolean = 
						ptype.getTypeParameters()[0].equals(Boolean.class);
			}
		}
		
		name =  isBoolean ? ("is" + name) : ("has" + name);
		
		return Thinklab.get().getProperty(main.getConceptSpace() + ":" + name);
	}

	/*
	 * Collect fields we can use from the class to annotate. Rules are:
	 * 1. the class (or superclass) must be registered with the 
	 *    annotation factory, either directly or through a @Concept annotation;
	 * 2. if the class has one or more @Property field annotations, we only check fields 
	 *     that have it; otherwise all fields are game.
	 * 3. If we use non-annotated fields, their name must map to an existing
	 *    property in the namespace of the concept mapped to the class they're defined in.
	 * 4. Property names are obtained by removing any leading underscores, capitalizing
	 *    the first letter and prefixing the resulting string with "is" for boolean
	 *    fields and "has" for all others. If the field points to a collection or
	 *    map, a trailing "s" is also removed to make the property a singular. If the
	 *    string contains underscores, those are removed and the segments between 
	 *    underscores are capitalized to a nice camelcase syntax, more typical of
	 *    OWL properties.
	 */
	private Collection<Pair<Field,IProperty>> getAssignableFieldsInternal(Class<?> cls, List<Pair<Field, IProperty>> ret) {

		/*
		 * eventually we get passed null as a superclass.
		 */
		if (cls == null)
			return ret;
		
		/*
		 * scan parents first. This is done even if our own type isn't registered.
		 */
		getAssignableFieldsInternal(cls.getSuperclass(), ret);
		
		/* no fun if we are not registered. */
		IConcept main = _class2concept.get(cls);
		if (main == null)
			return ret;
		
		boolean useAnnotation = false;
		for (Field f : cls.getDeclaredFields()) {
			if (f.isAnnotationPresent(Property.class)) {
				useAnnotation = true;
				break;
			}
		}
		
		/*
		 * now go collect them
		 */
		for (Field f : cls.getDeclaredFields()) {

			IProperty p = null; 
			if (useAnnotation && f.isAnnotationPresent(Property.class)) {
				p = Thinklab.p(f.getAnnotation(Property.class).value());
			} else {
				p = getPropertyFromFieldName(f, main);
			}
			
			if (p != null) {
				ret.add(new Pair<Field, IProperty>(f,p));
			}
		}
		
		return ret;
	}

	/*
	 * -----------------------------------------------------------------------------
	 * the actually useful methods
	 * -----------------------------------------------------------------------------
	 */
	public IList conceptualize(Object o) throws ThinklabException {
		
		Map<Object, IReferenceList> hash = 
				Collections.synchronizedMap(new WeakHashMap<Object, IReferenceList>());
		return conceptualizeInternal(o, hash, new ReferenceList());
	}

	private IList conceptualizeInternal(Object o, Map<Object, IReferenceList> objectHash, IReferenceList list) 
			throws ThinklabException {

		/*
		 * If literal, we always create a full list.
		 */
		Class<?> cls = o.getClass();
		IConcept literalType = _class2literal.get(cls);
		if (literalType != null) {
			return list.list(literalType, o);
		} 	
		

		/*
		 * special treatment for map entries. TODO see if we can associate the actual
		 * Entry with a concept, although the handling of Map needs to remain special
		 * because they're not Collections of Entry.
		 */
		if (o instanceof Map.Entry<?,?>) {

			return 
					conceptualizeInternal(
							new SKeyValue(
									((Map.Entry<?,?>)o).getKey(),
									((Map.Entry<?,?>)o).getValue()),
							objectHash,
							list);
		}

		/*
		 * not literal. If we've seen this, just add the reference to it. Otherwise 
		 * get a new reference, add it and work on that.
		 */
		IReferenceList ref = null;
		if (objectHash.containsKey(o)) {
			return objectHash.get(o);
		} else {
			ref = list.getForwardReference();
			objectHash.put(o, ref);
		}
		
		/*
		 * if conceptualizable, that's all we need to do, and it's not going to
		 * add references to objects upstream.
		 */
		if (o instanceof IConceptualizable) {
			return ref.resolve(((IConceptualizable) o).conceptualize());
		}
		
		/*
		 * if we get here, we need a @Concept annotation to proceed.
		 */
		IConcept mainc = _class2concept.get(cls);
		if (mainc == null) {
			/*
			 * list will have unresolved reference
			 */
			return list;
		}
		
		ArrayList<Object> sa = new ArrayList<Object>();
		sa.add(mainc);

		for (Pair<Field, IProperty> pp : getAssignableFields(cls)) {

			Field f = pp.getFirst();
			IProperty p = pp.getSecond();

			if (p != null) {
					
				Object value = null;
				try {
					f.setAccessible(true);
					value = f.get(o);
				} catch (Exception e) {
					throw new ThinklabInternalErrorException(e);
				}
				if (value != null) {
					for (Object v : getAllInstances(value)) {
					
						IList semantics = conceptualizeInternal(v, objectHash, list);
						if (semantics == null) {
							throw new ThinklabValidationException("cannot conceptualize field " + f.getName() + " of object " + o);
						}
						sa.add(list.list(p, semantics));
					}
				}
			}
		}
		
		return ref.resolve(list.list(sa.toArray()));
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

	public ISemanticObject instantiate(IList annotation) throws ThinklabException {
		
		return instantiateInternal( annotation, new HashMap<IReferenceList,ISemanticObject>());
	}
	
	@SuppressWarnings("unchecked")
	private ISemanticObject instantiateInternal(IList annotation, HashMap<IReferenceList,ISemanticObject> refs) 
				throws ThinklabException {
	
		ISemanticObject ret = null;
//		if (annotation instanceof IList.Ref) {
//			if (objHash.containsKey(annotation)) {
//				return objHash.get(annotation);
//			} else {
//				return instantiateInternal(((IList.Ref)annotation).get(), objHash, refs);
//			}
//		}
//		
//		/*
//		 * check first if it's just a literal we're instantiating. If so, we
//		 * have it already.
//		 */
//		Class<?> cls = _annotatedLiteralClass.get(annotation.first());
//		if (cls != null) {
//			return new SemanticObject(annotation, annotation.nth(1));
//		}
//		
//		/*
//		 * find class. If an IConceptualizable, create object, call instantiate() and
//		 * return it.
//		 */
//		cls = _concept2class.get(annotation.first());
//		
//		if (cls == null)
//			return null;
//		
//		/*
//		 * create object. Find the most appropriate constructor - if there is one with 
//		 * a SemanticAnnotation use that. Otherwise find an empty constructor.
//		 */
//		SemanticObject ret = new SemanticObject(annotation, null);
//
//		boolean hasEmptyConstructor = false;
//		for (Constructor<?> cc : cls.getConstructors()) {
//			Class<?>[] pt = cc.getParameterTypes();
//			if (pt.length == 1 && IList.class.isAssignableFrom(pt[0])) {
//				try {
//					ret.setObject(cc.newInstance(annotation));
//					break;
//				} catch (Exception e) {
//					throw new ThinklabInternalErrorException(e);
//				}
//			}
//			if (pt.length == 0)
//				hasEmptyConstructor = true;
//		}
//		
//		if (ret.getObject() == null && hasEmptyConstructor) {
//			try {
//				ret.setObject(cls.newInstance());
//			} catch (Exception e) {
//				throw new ThinklabInternalErrorException(e);
//			}
//		}
//		
//		if (ret.getObject() == null) {
//
//			Thinklab.get().logger().warn(
//					"instantiate: couldn't find a suitable constructor for class " + cls.getCanonicalName() +
//					" associated to concept " + annotation.first());
//			
//			return null;
//		}
//
//		/*
//		 * Put it there before it's fully defined so we don't get in trouble
//		 * with circular refs.
//		 */
//		objHash.put(PolyList.reference(annotation, refs), ret);
//		
//		/*
//		 * if it's conceptualizable, just call its define() method
//		 * and leave everything else to the user.
//		 */
//		if (IConceptualizable.class.isAssignableFrom(cls)) {
//			((IConceptualizable)(ret.getObject())).define(annotation); 
//			return ret; 
//		} 		
//
//		/*
//		 * find all fields with property annotations and process the content of the 
//		 * semantic annotation.
//		 */
//		for (Pair<Field,IProperty> fp : getAssignableFields(cls)) {
//
//			Field f = fp.getFirst();
//			IProperty p = fp.getSecond();
//			
//			/*
//			 * if object is a collection to fill in, see if the constructor
//			 * created it, and if not, create it. If any of the following three
//			 * isn't null, that's what we add the target to. Otherwise we set the field
//			 * to the target.
//			 */
//			Map<?,?> map = null;
//			Collection<?> collection = null;
//			Object[] array = null;
//			boolean mustSet = false;
//			
//			/*
//			 * collect all applicable relationships
//			 */
//			ArrayList<IList> props = new ArrayList<IList>();
//			Object[] oo = annotation.toArray();
//			for (int i = 1; i < oo.length; i++) {
//				if (oo[i] instanceof IList && 
//					((IList)(oo[i])).first() instanceof IProperty &&
//					p.is((IProperty)((IList)(oo[i])).first()))
//					props.add((IList)(oo[i]));
//			}
//			
//			if (props.size() == 0)
//				continue;
//			
//			try {
//				if (Map.class.isAssignableFrom(f.getType())) {
//					map = (Map<?, ?>) f.getType().newInstance();
//					mustSet = true;
//				} else if (Collection.class.isAssignableFrom(f.getType())) {
//					collection = (Collection<?>) f.getType().newInstance();
//					mustSet = true;
//				} else if (f.getType().isArray()) {
//					array = (Object[]) Array.newInstance(f.getType().getComponentType(), props.size());
//					mustSet = true;
//				}
//			} catch (Exception e) {
//				throw new ThinklabInternalErrorException(e);
//			}
//
//			int n = 0;
//			for (IList r : props) {	
//
//				/*
//				 * may be a ref, semantics, or another semantic object
//				 */
//				ISemanticObject tg = r.nth(1) instanceof IList.Ref ?
//						objHash.get(r) :
//						(r.nth(1) instanceof IList ?
//								instantiateInternal((IList) r.nth(1), objHash, refs) :
//								(ISemanticObject) r.nth(1));
//						
//				Object obj = tg.getObject();					
//				try {
//					if (map != null && obj instanceof SKeyValue) {
//						((Map<Object,Object>)map).put(((SKeyValue)obj).key, ((SKeyValue)obj).value);
//					} else if (collection != null) {
//						((Collection<Object>)collection).add(obj);
//					} else if (array != null) {
//						array[n] = obj;
//					} else {
//						f.setAccessible(true);
//						f.set(ret.getObject(), obj);
//					}
//				} catch (Exception e) {
//					throw new ThinklabInternalErrorException(e);
//				}
//				
//				n++;
//			}
//				
//			if (mustSet) {
//				try {
//					f.setAccessible(true);
//					if (map != null) {
//						f.set(ret.getObject(), map);
//					} else if (collection != null) {
//						f.set(ret.getObject(), collection);
//					} else if (array != null) {
//						f.set(ret.getObject(), array);
//					}
//				} catch (Exception e) {
//					throw new ThinklabInternalErrorException(e);
//				}
//			}
//		}
//		
//		/*
//		 * if there is a public initialize() method with no parameters, invoke it.
//		 */
//		Method init = null;
//		try {
//			init = cls.getMethod("initialize", (Class<?>[])null);
//			if (init != null)
//				init.invoke(ret.getObject(), (Object[])null);
//		} catch (Exception e) {
//			// no method, the stupid thing throws an exception instead of returning null.
//		}
//		
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

		IList list = conceptualize(object);
		return 
			list == null ?
					null :
					new SemanticObject(list, null);
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
