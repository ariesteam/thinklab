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
//package org.integratedmodelling.thinklab.modelling.model.implementation;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.Set;
//
//import org.integratedmodelling.collections.Pair;
//import org.integratedmodelling.exceptions.ThinklabException;
//import org.integratedmodelling.lang.model.LanguageElement;
//import org.integratedmodelling.thinklab.api.knowledge.IConcept;
//import org.integratedmodelling.thinklab.api.knowledge.IExpression;
//import org.integratedmodelling.thinklab.api.knowledge.IInstance;
//import org.integratedmodelling.thinklab.api.knowledge.IProperty;
//import org.integratedmodelling.thinklab.api.modelling.IContext;
//import org.integratedmodelling.thinklab.api.modelling.IExtent;
//import org.integratedmodelling.thinklab.api.modelling.IObservation;
//import org.integratedmodelling.thinklab.api.modelling.IObserver;
//import org.integratedmodelling.thinklab.api.modelling.metadata.IMetadata;
//import org.integratedmodelling.thinklab.metadata.Metadata;
//
///**
// * Base class implementing IObservation. Holds an instance for the observable and 
// * collections for dependencies and extents. Not abstract: can be used to implement
// * an identification, which doesn't have a corresponding type. As it is not an
// * IndirectObservation, its observable is its state and it's not expected to 
// * correspond to an IState after contextualization.
// * 
// * In thinklab 1.0, Observations are not InstanceImplementations - only a State
// * is. Therefore they're created directly and not through their correspondent
// * observation instances.
// * 
// * @author Ferd
// * @deprecated see modelling.model package for the actual things.
// */
//public class Observation implements IObservation {
//
//	protected IMetadata _metadata = new Metadata();
//	
//	// all dependencies except extents, never null
//	protected ArrayList<IObservation> _dependencies = new ArrayList<IObservation>();
//
//	// all natively defined extents, never null; usually only States have native extents, 
//	// but not necessarily (observations should also be able to have partially specified
//	// extents to constrain contextualization, e.g. to a minimum resolution, without
//	// specifying the full extent. That's unimplemented for now).
//	protected ArrayList<IExtent> _extents = new ArrayList<IExtent>();
//
//	// this must be defined in the constructor
//	protected IInstance _observable = null;
//	
//	// cached for speed - it's just _observable.getDirectType()
//	protected IConcept  _type = null;
//	
//	protected String formalName = null;
//	
//	// public API below
//	
//	@Override
//	public IMetadata getMetadata() {
//		return _metadata;
//	}
//
//	@Override
//	public Collection<IObservation> getDependencies() {
//		return _dependencies;
//	}
//
//	// --- internal API -----------------------------------
//	
//	public void addDependency(IObservation obs) {
//		_dependencies.add(obs);
//	}
//
//	public void setFormalName(String localName) {
//		formalName = localName;
//	}
//
//	@Override
//	public IObservation contextualize(IContext context)
//			throws ThinklabException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IContext getContext() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//	@Override
//	public IInstance getObservable() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IObserver getObserver() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Set<IInstance> getObservables() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public LanguageElement getLanguageElement() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getId() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getNamespace() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getName() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IProperty getContextProperty() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<IObservation> getContextObjects() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//}
