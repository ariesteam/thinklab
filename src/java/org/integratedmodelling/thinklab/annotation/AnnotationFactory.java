package org.integratedmodelling.thinklab.annotation;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.collections.Triple;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabInternalErrorException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.list.PolyList;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.annotation.utils.SKeyValue;
import org.integratedmodelling.thinklab.api.annotations.Property;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.ISemantics;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.lang.IParseable;
import org.integratedmodelling.thinklab.interfaces.knowledge.datastructures.IntelligentMap;
import org.integratedmodelling.thinklab.knowledge.SemanticObject;
import org.integratedmodelling.thinklab.knowledge.Semantics;
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
	 * Assignable fields have 
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
	public ISemantics conceptualize(Object o) throws ThinklabException {
		
		Map<Object, Triple<Long, Long, IList>> hash = 
				Collections.synchronizedMap(new WeakHashMap<Object, Triple<Long,Long,IList>>());
		IList list = conceptualizeInternal(o, hash);
		return new Semantics(resolveReferences(list, hash), Thinklab.get());
	}
	
	private IList resolveReferences(IList list,
			Map<Object, Triple<Long, Long, IList>> hash) throws ThinklabException {

		/*
		 * 1st pass: translate all lists with refcount > 1 to reference lists and 
		 * index by ID.
		 */
		HashMap<Long,IList> refs = new HashMap<Long,IList>();
		for (Triple<Long, Long, IList> t : hash.values()) {
			IList rl = t.getThird();
			if (t.getSecond() > 1)
				rl = PolyList.referencedList(t.getFirst(), rl.toArray());
			refs.put(t.getFirst(), rl);
		}
		
		/*
		 * 2nd pass - actually substitute those refs recursively
		 */
		return substituteRefs(list, refs, new HashSet<Long>());
	}
		
	private IList substituteRefs(IList list, Map<Long, IList> refs, HashSet<Long> seen) {
		
		if (list == null)
			return null;
		
		if (!list.isEmpty() && "#".equals(list.first())) {
			return substituteRefs(refs.get(list.nth(1)), refs, seen);
		}
		ArrayList<Object> ret = new ArrayList<Object>();
		for (Object o : list) {
			ret.add(o instanceof IList ? substituteRefs((IList) o, refs, seen) : o);
		}
		
		return PolyList.fromCollection(ret);
	}

	/*
	 * Final list contains unresolved references. The value in the hash contains for
	 * each object:
	 * 1. an incremental, unique ID
	 * 2. the number of times the object was seen
	 * 3. the IList that describes it, containing unresolved refs in the form ("#", id) 
	 * 	  for all linked objects, to be resolved later.
	 */
	private IList conceptualizeInternal(Object o, Map<Object, Triple<Long, Long, IList>> hash) 
			throws ThinklabException {

		if (o instanceof IConceptualizable) {
			return ((IConceptualizable) o).conceptualize().asList();
		}

		/*
		 * first check if it can be stored as a literal
		 * FIXME it would be nice to not do this treatment to save some of the thinklab-api
		 * types, but at the moment I don't know how to.
		 */
		if (o instanceof Map.Entry<?,?>) {
			
			return 
				PolyList.list(
					Thinklab.c(NS.KEY_VALUE_PAIR),
					PolyList.list(Thinklab.p(NS.HAS_FIRST_FIELD), conceptualizeInternal(((Map.Entry<?,?>)o).getKey(), hash)),
					PolyList.list(Thinklab.p(NS.HAS_SECOND_FIELD), conceptualizeInternal(((Map.Entry<?,?>)o).getValue(), hash)));
		} 
			
		Class<?> cls = o.getClass();
		IConcept literalType = _class2literal.get(cls);
		if (literalType != null) {
			return PolyList.list(literalType, o);
		} 	

		
		/*
		 * if we get here, we need a @Concept annotation to proceed.
		 */
		IConcept mainc = _class2concept.get(cls);
		if (mainc == null)
			return null;


		/*
		 * if object was already seen, increment ref count. 
		 */
		long objId;
		if (hash.containsKey(o)) {
			Triple<Long,Long,IList> oo = hash.get(o);
			objId = oo.getFirst();
			hash.put(o, new Triple<Long,Long,IList> (objId, oo.getSecond()+1, oo.getThird()));
			return PolyList.list("#", objId);
		} else {
			objId = hash.size() + 1;
			hash.put(o, new Triple<Long,Long,IList>(objId,1l, null));
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
					
						IList semantics = conceptualizeInternal(v, hash);
						if (semantics == null) {
							throw new ThinklabValidationException("cannot conceptualize field " + f.getName() + " of object " + o);
						}
						sa.add(PolyList.list(p, semantics));
					}
				}
			}
		}

		/*
		 * store unresolved object semantics for later resolution
		 */
		Triple<Long,Long,IList> oo = hash.get(o);
		objId = oo.getFirst();
		hash.put(o, new Triple<Long,Long,IList> (objId, oo.getSecond()+1, 
				sa.size() == 0 ? null : PolyList.fromCollection(sa)));
		
		/*
		 * return unresolved reference
		 */
		return PolyList.list("#", objId);
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

	public Object instantiate(ISemantics annotation) throws ThinklabException {
		return instantiateInternal(annotation, new HashMap<Long,Object>());
	}
	
	@SuppressWarnings("unchecked")
	private Object instantiateInternal(ISemantics annotation, Map<Long, Object> refs) throws ThinklabException {
	
//		if (((Semantics)annotation).isReference() && refs.containsKey(((Semantics)annotation).getReference())) {
//			return refs.get(((Semantics)annotation).getReferenceId());
//		}
		
		Object ret = null;
		
		/*
		 * check first if it's just a literal we're instantiating. If so, we
		 * have it already, and the way Semantics works it should be already
		 * in the right form.
		 */
		Class<?> cls = _annotatedLiteralClass.get(annotation.getConcept());
		if (cls != null) {
			return annotation.getLiteral();
		}
		
		/*
		 * find class. If an IConceptualizable, create object, call instantiate() and
		 * return it.
		 */
		cls = _concept2class.get(annotation.getConcept());
		
		if (cls == null)
			return null;
		
		/*
		 * create object. Find the most appropriate constructor - if there is one with 
		 * a SemanticAnnotation use that. Otherwise find an empty constructor.
		 */
		boolean hasEmptyConstructor = false;
		for (Constructor<?> cc : cls.getConstructors()) {
			Class<?>[] pt = cc.getParameterTypes();
			if (pt.length == 1 && ISemantics.class.isAssignableFrom(pt[0])) {
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
		 * if it's conceptualizable, just call its define() method
		 * and leave everything else to the user.
		 */
		if (IConceptualizable.class.isAssignableFrom(cls)) {
			((IConceptualizable)ret).define(annotation); 
			return ret; // checkReference(annotation, ret, refs);
		} 		

		/*
		 * find all fields with property annotations and process the content of the 
		 * semantic annotation.
		 */
		for (Pair<Field,IProperty> fp : getAssignableFields(cls)) {

			Field f = fp.getFirst();
			IProperty p = fp.getSecond();
			
			int rcount = annotation.getRelationshipsCount(p);	
			if (rcount == 0)
				continue;
			/*
			 * if object is a collection to fill in, see if the constructor
			 * created it, and if not, create it. If any of the following three
			 * isn't null, that's what we add the target to. Otherwise we set the field
			 * to the target.
			 */
			Map<?,?> map = null;
			Collection<?> collection = null;
			Object[] array = null;
			boolean mustSet = false;
			
			try {
				if (Map.class.isAssignableFrom(f.getType())) {
					map = (Map<?, ?>) f.getType().newInstance();
					mustSet = true;
				} else if (Collection.class.isAssignableFrom(f.getType())) {
					collection = (Collection<?>) f.getType().newInstance();
					mustSet = true;
				} else if (f.getType().isArray()) {
					array = (Object[]) Array.newInstance(f.getType().getComponentType(), rcount);
					mustSet = true;
				}
			} catch (Exception e) {
				throw new ThinklabInternalErrorException(e);
			}

			int n = 0;
			for (ISemantics r : annotation.getRelationships(p)) {	

				ISemanticObject tg = r.getTarget();
				Object obj = tg.getObject();					
				try {
					if (map != null && obj instanceof SKeyValue) {
						((Map<Object,Object>)map).put(((SKeyValue)obj).key, ((SKeyValue)obj).value);
					} else if (collection != null) {
						((Collection<Object>)collection).add(obj);
					} else if (array != null) {
						array[n] = obj;
					} else {
						f.setAccessible(true);
						f.set(ret, obj);
					}
				} catch (Exception e) {
					throw new ThinklabInternalErrorException(e);
				}
				
				n++;
			}
				
			if (mustSet) {
				try {
					f.setAccessible(true);
					if (map != null) {
						f.set(ret, map);
					} else if (collection != null) {
						f.set(ret, collection);
					} else if (array != null) {
						f.set(ret, array);
					}
				} catch (Exception e) {
					throw new ThinklabInternalErrorException(e);
				}
			}
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
		
		return ret; // checkReference(annotation, ret, refs);
	}

//	private Object checkReference(ISemantics annotation, Object ret,
//			Map<Long, Object> refs) {
//		Semantics ann = (Semantics) annotation;
//		if (ann.isReference() && !refs.containsKey(ann.getReferenceId()))
//			refs.put(ann.getReferenceId(), ret);
//		return ret;
//	}

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
		
		if (object instanceof ISemantics)
			return new SemanticObject((ISemantics)object, null);

		ISemanticObject ret = null;
		ISemantics semantics = conceptualize(object);
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
