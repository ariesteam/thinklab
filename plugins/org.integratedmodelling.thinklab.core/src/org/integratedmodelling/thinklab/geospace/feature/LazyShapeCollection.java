package org.integratedmodelling.thinklab.geospace.feature;
///**
// * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
// * www.integratedmodelling.org. 
//
//   This file is part of Thinklab.
//
//   Thinklab is free software: you can redistribute it and/or modify
//   it under the terms of the GNU General Public License as published
//   by the Free Software Foundation, either version 3 of the License,
//   or (at your option) any later version.
//
//   Thinklab is distributed in the hope that it will be useful, but
//   WITHOUT ANY WARRANTY; without even the implied warranty of
//   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//   General Public License for more details.
//
//   You should have received a copy of the GNU General Public License
//   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
// */
//package org.integratedmodelling.geospace.feature;
//
//import java.util.Collection;
//import java.util.Iterator;
//
//import org.geotools.feature.FeatureIterator;
//import org.integratedmodelling.exceptions.ThinklabRuntimeException;
//import org.integratedmodelling.geospace.literals.ShapeValue;
//import org.opengis.feature.simple.SimpleFeature;
//import org.opengis.referencing.crs.CoordinateReferenceSystem;
//
//import com.vividsolutions.jts.geom.Geometry;
//
//
///**
// * A collection of shapes that come from a vector coverage and will only be extracted when
// * next() is called. The only thing it can do is to be
// * iterated, just about everything else, including calling size(), will throw an exception.
// * 
// * @author Ferdinando
// *
// */
//public class LazyShapeCollection implements Collection<ISemanticLiteral> {
//
//	FeatureIterator<SimpleFeature> _features = null;
//	CoordinateReferenceSystem _crs;
//	
//	public class ShapeIterator implements Iterator<ISemanticLiteral> {
//
//		@Override
//		public boolean hasNext() {
//			return _features != null && _features.hasNext();
//		}
//
//		@Override
//		public ShapeValue next() {
//			SimpleFeature ff = _features.next();
//			return new ShapeValue((Geometry)ff.getDefaultGeometry(), _crs);
//		}
//
//		@Override
//		public void remove() {
//			throw new ThinklabRuntimeException("remove() unsupported on LazyShapeCollection.Iterator");
//		}	
//	}
//	
//	public LazyShapeCollection(FeatureIterator<SimpleFeature> features, CoordinateReferenceSystem crs) {
//		this._features = features;
//		this._crs = crs;
//	}
//	
//	@Override
//	public boolean add(ISemanticLiteral e) {
//		throw new ThinklabRuntimeException("add unsupported on LazyShapeCollection");
//	}
//
//	@Override
//	public boolean addAll(Collection<? extends ISemanticLiteral> c) {
//		throw new ThinklabRuntimeException("addAll unsupported on LazyShapeCollection");
//	}
//
//	@Override
//	public void clear() {
//		_features = null;
//	}
//
//	@Override
//	public boolean contains(Object o) {
//		throw new ThinklabRuntimeException("contains unsupported on LazyShapeCollection");
//	}
//
//	@Override
//	public boolean containsAll(Collection<?> c) {
//		throw new ThinklabRuntimeException("containsAll unsupported on LazyShapeCollection");
//	}
//
//	@Override
//	public boolean isEmpty() {
//		return _features == null || !_features.hasNext();
//	}
//
//	@Override
//	public Iterator<ISemanticLiteral> iterator() {
//		return new ShapeIterator();
//	}
//
//	@Override
//	public boolean remove(Object o) {
//		throw new ThinklabRuntimeException("remove unsupported on LazyShapeCollection");
//	}
//
//	@Override
//	public boolean removeAll(Collection<?> c) {
//		throw new ThinklabRuntimeException("removeAll unsupported on LazyShapeCollection");
//	}
//
//	@Override
//	public boolean retainAll(Collection<?> c) {
//		throw new ThinklabRuntimeException("retainAll unsupported on LazyShapeCollection");
//	}
//
//	@Override
//	public int size() {
//		throw new ThinklabRuntimeException("size() unsupported on LazyShapeCollection");
//	}
//
//	@Override
//	public Object[] toArray() {
//		throw new ThinklabRuntimeException("toArray unsupported on LazyShapeCollection");
//	}
//
//	@Override
//	public <T> T[] toArray(T[] a) {
//		throw new ThinklabRuntimeException("toArray unsupported on LazyShapeCollection");
//	}
//
//}
