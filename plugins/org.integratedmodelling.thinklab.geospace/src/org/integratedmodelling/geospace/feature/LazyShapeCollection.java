package org.integratedmodelling.geospace.feature;

import java.util.Collection;
import java.util.Iterator;

import org.geotools.feature.FeatureIterator;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;


/**
 * A collection of shapes that come from a vector coverage and will only be extracted when
 * next() is called. The only thing it can do is to be
 * iterated, just about everything else, including calling size(), will throw an exception.
 * 
 * @author Ferdinando
 *
 */
public class LazyShapeCollection implements Collection<IValue> {

	FeatureIterator<SimpleFeature> _features = null;
	CoordinateReferenceSystem _crs;
	
	public class ShapeIterator implements Iterator<IValue> {

		@Override
		public boolean hasNext() {
			return _features != null && _features.hasNext();
		}

		@Override
		public ShapeValue next() {
			SimpleFeature ff = _features.next();
			return new ShapeValue((Geometry)ff.getDefaultGeometry(), _crs);
		}

		@Override
		public void remove() {
			throw new ThinklabRuntimeException("remove() unsupported on LazyShapeCollection.Iterator");
		}	
	}
	
	public LazyShapeCollection(FeatureIterator<SimpleFeature> features, CoordinateReferenceSystem crs) {
		this._features = features;
		this._crs = crs;
	}
	
	@Override
	public boolean add(IValue e) {
		throw new ThinklabRuntimeException("add unsupported on LazyShapeCollection");
	}

	@Override
	public boolean addAll(Collection<? extends IValue> c) {
		throw new ThinklabRuntimeException("addAll unsupported on LazyShapeCollection");
	}

	@Override
	public void clear() {
		_features = null;
	}

	@Override
	public boolean contains(Object o) {
		throw new ThinklabRuntimeException("contains unsupported on LazyShapeCollection");
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new ThinklabRuntimeException("containsAll unsupported on LazyShapeCollection");
	}

	@Override
	public boolean isEmpty() {
		return _features == null || !_features.hasNext();
	}

	@Override
	public Iterator<IValue> iterator() {
		return new ShapeIterator();
	}

	@Override
	public boolean remove(Object o) {
		throw new ThinklabRuntimeException("remove unsupported on LazyShapeCollection");
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new ThinklabRuntimeException("removeAll unsupported on LazyShapeCollection");
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new ThinklabRuntimeException("retainAll unsupported on LazyShapeCollection");
	}

	@Override
	public int size() {
		throw new ThinklabRuntimeException("size() unsupported on LazyShapeCollection");
	}

	@Override
	public Object[] toArray() {
		throw new ThinklabRuntimeException("toArray unsupported on LazyShapeCollection");
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new ThinklabRuntimeException("toArray unsupported on LazyShapeCollection");
	}

}
