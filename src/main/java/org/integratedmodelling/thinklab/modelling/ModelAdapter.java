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
//package org.integratedmodelling.thinklab.modelling;
//
//import org.integratedmodelling.collections.Pair;
//import org.integratedmodelling.exceptions.ThinklabException;
//import org.integratedmodelling.lang.model.AgentModel;
//import org.integratedmodelling.lang.model.ConditionalObserver;
//import org.integratedmodelling.lang.model.Context;
//import org.integratedmodelling.lang.model.Model;
//import org.integratedmodelling.lang.model.ModelObject;
//import org.integratedmodelling.lang.model.Namespace;
//import org.integratedmodelling.lang.model.Observer;
//import org.integratedmodelling.lang.model.Scenario;
//import org.integratedmodelling.lang.model.Storyline;
//import org.integratedmodelling.thinklab.Thinklab;
//import org.integratedmodelling.thinklab.api.knowledge.IExpression;
//import org.integratedmodelling.thinklab.api.knowledge.IOntology;
//import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;
//import org.integratedmodelling.thinklab.api.modelling.IContext;
//import org.integratedmodelling.thinklab.api.modelling.IModelObject;
//import org.integratedmodelling.thinklab.api.modelling.INamespace;
//import org.integratedmodelling.thinklab.api.modelling.IObserver;
//import org.integratedmodelling.thinklab.api.project.IProject;
//import org.integratedmodelling.thinklab.owlapi.FileKnowledgeRepository;
//
///**
// * A class that translates the API beans output by the language parsers into
// * actual namespaces and model objects. Contains all the translation logics for
// * thinklab.
// * 
// * @author Ferd
// *
// */
//public class ModelAdapter {
//
//
//	/**
//	 * The main entry point.
//	 * 
//	 * @param namespace
//	 * @return
//	 * @throws ThinklabException 
//	 */
//	public INamespace createNamespace(Namespace namespace) throws ThinklabException {
//		
//		NamespaceImpl ret = new NamespaceImpl(namespace);
//		
//		/*
//		 * ontology first - ask for one, complain if not there
//		 */
//		IProject proj = namespace.getProject();
//		String urlPrefix = 
//				proj == null ? 
//					FileKnowledgeRepository.DEFAULT_TEMP_URI : 
//					proj.getOntologyNamespacePrefix();
//		
//		IOntology ont = 
//				Thinklab.get().getKnowledgeRepository().createOntology(namespace.getId(), urlPrefix);
//		ont.define(namespace.getAxioms());
//
//		ret._ontology = ont;
//		
//		for (ModelObject o : namespace.getModelObjects()) {
//			ret._modelObjects.add(createModelObject(o));
//		}
//		
//		/*
//		 * TODO
//		 * sync with designated kbox
//		 */
//		syncNamespace(ret);
//		
//		return ret;
//	}
//
//	private void syncNamespace(INamespace ns) throws ThinklabException {
//
//		IKbox kbox = Thinklab.get().getStorageKboxForNamespace(ns);
//		
//		if (kbox == null) 
//			return;
//		
//		/*
//		 * see if namespace was there; if so, compare last modification
//		 * dates, only proceed if ns is newer
//		 */
////		IQuery query = new Constraint(NS.NAMESPACE).restrict(MN.HAS_ID, IOperator.EQUALS, ns.getNamespace());
////		List<Object> ret = 
////				kbox.query(
////						query,
////						IKbox.KBOX_LITERALS_ONLY | IKbox.KBOX_RETRIEVE_ANNOTATIONS);
////		
////		if (ret.size() > 0) {
//			
////			SemanticAnnotation ans = (SemanticAnnotation) ret.get(0);
////			ISemanticLiteral tst = ans.getValue(MN.HAS_TIMESTAMP);
////			if (tst != null && tst.asLong() >= ns.getLastModification())
////				return;
////			
////			kbox.removeAll(query);
////		}
//		
//		for (IModelObject o : ns.getModelObjects()) {
//
//			if (! (o instanceof IContext))
//				continue;
//
//			/*
//			 * TODO
//			 * determine if this context is wrapping an observation to
//			 * be stored; if so, save it for possible future storage in 
//			 * the designated kbox.
//			 * 
//			 * Criteria for storage: context wraps 
//			 * at least one observation that has an external datasource
//			 * that has no errors.
//			 */
//
//		
//		}
//	}
//
//	private IModelObject createModelObject(ModelObject o) {
//		
//		IModelObject ret = null;
//		
//		if (o instanceof Model) {
//			
//			ret = new ModelImpl((Model)o);
//			((ModelImpl)ret)._observer = createObserver(((Model)o).getObserver());
////			((ModelImpl)ret)._observable = 
////					new SemanticAnnotation(((Model)o).getObservable(), Thinklab.get());
//			
//			/*
//			 * TODO submit other observables for the accessor and validate them against
//			 * existing models in namespace.
//			 */
//			
//		} else if (o instanceof Context) {
//			ret = new ContextImpl((Context)o);
//		} else if (o instanceof AgentModel) {
//			ret = new AgentModelImpl((AgentModel)o);
//		} else if (o instanceof Scenario) {
//			ret = new ScenarioImpl((Scenario)o);
//		} else if (o instanceof Storyline) {
//			ret = new StorylineImpl((Storyline)o);
//		}
//		return ret;
//	}
//
//	private IObserver createObserver(Observer observer) {
//
//		IObserver ret = null;
//		if (observer != null) {
//			
//			if (observer instanceof ConditionalObserver) {
//				ret = new ConditionalObserverImpl(observer);
//				for (Pair<Observer, IExpression>  o : ((ConditionalObserver)observer).getObservers()) {
//					((ConditionalObserverImpl)ret).addObserver(createObserver(o.getFirst()), o.getSecond());
//				}
//			}
//		}
//		return ret;
//	}
//
//	/**
//	 * Assume the namespace has been incrementally modified and just parse the last
//	 * model object defined in it. Used only in interactive sessions when statements
//	 * are evaluated one by one.
//	 * 
//	 * @param evaluate
//	 * @return
//	 */
//	public IModelObject createModelObject(Namespace evaluate) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
//	
//}
