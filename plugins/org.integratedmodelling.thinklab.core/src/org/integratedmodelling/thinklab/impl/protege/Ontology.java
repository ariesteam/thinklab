/**
 * Ontology.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinklab.
 * 
 * Thinklab is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinklab is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.impl.protege;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.exception.ThinklabAmbiguousResultException;
import org.integratedmodelling.thinklab.exception.ThinklabDuplicateNameException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabMalformedSemanticTypeException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IOntology;
import org.integratedmodelling.thinklab.interfaces.IProperty;
import org.integratedmodelling.thinklab.interfaces.IResource;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.Polylist;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.arp.ARP;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileUtils;

import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser.ARPInvokation;
import edu.stanford.smi.protegex.owl.jena.protege2jena.Protege2Jena;
import edu.stanford.smi.protegex.owl.jena.triplestore.JenaTripleStore;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.repository.RepositoryManager;
import edu.stanford.smi.protegex.owl.repository.impl.LocalFolderRepository;

/**
 * @author Ioannis N. Athanasiadis, Dalle Molle Institute for Artificial Intelligence, USI/SUPSI
 * @author Ferdinando Villa
 * 
 * @since 13 ��� 2006
 */
public class Ontology implements IOntology {
	
	class ReferenceRecord {
		public IInstance target;
		public String reference;
		public IProperty property;
		
		public ReferenceRecord(IInstance t, IProperty prop, String ref) {
			target = t;
			reference = ref;
			property = prop;
		}
	}

	public static final String DEFAULT_TEMP_URI = "http://www.integratedmodelling.org/temporary/";

	private String conceptSpace; // i.e. owl or xml or fs

	private FileKnowledgeRepository kr;
	
	private JenaOWLModel owlModel;

	private JenaTripleStore tripleStore;

	public ThinklabLocalRepository repository;

	protected OWLOntology ontology;

	private long lastModified;
	
	private boolean acceptDuplicateIDs = false;
	    
	// private boolean lazy = true;
	// such an option could be used for switching the behavior of
	// an ontology in from lazy (i.e. load all concepts, instances,
	// relationships etc etc at startup)
	// to "fast reader" i.e. load things on request.\
	// In the second case a single call of list instances/concepts/etc
	// could results either (a) switching the ontology back to the lazy mode or
	// (b) return only the recently used conmcepts/instances/etc

	public Ontology(FileKnowledgeRepository kRepository, URL fileURL, String conceptSpace,
			boolean editable) throws ThinklabException {
		Thinklab.get().logger().debug("  Load ontology from "+fileURL);

		this.kr = kRepository;
		this.owlModel = kr.owlModel;
		read(fileURL, conceptSpace, editable);
//		updateKR();		
	}

	private void updateKR(){
		kr.loadedOntologies.put(conceptSpace, this);
		kr.loadedFiles.put(repository.getFileURL(), conceptSpace);
		kr.loadedNamespaces.put(repository.getNamespace(),conceptSpace);
//		add import statement
		owlModel.getDefaultOWLOntology().addImports(repository.getName());
		Thinklab.get().logger().debug("Files: "+ kr.loadedFiles.entrySet());
		Thinklab.get().logger().debug("Namespaces"+ kr.loadedNamespaces.entrySet());
		Thinklab.get().logger().debug("loadedOntologies: "+ kr.loadedOntologies.entrySet());
		Thinklab.get().logger().debug("loadedTripleStores : " + owlModel.getTripleStoreModel().getTripleStores());
	}
	
	
	private String read(URL url, String conceptSpace, boolean editable)
			throws ThinklabException {
		Thinklab.get().logger().debug("  Reading ontology from "+url);
		this.repository = new ThinklabLocalRepository(url, editable);

		// Assign the repository
		RepositoryManager r = owlModel.getRepositoryManager();
		r.addProjectRepository(repository);
		LocalFolderRepository lfr = new LocalFolderRepository(new File(new File(repository.getFileURL().getPath()).getParent()));
		r.addProjectRepository(lfr);
		
		//		 Assign the prefered namespace
		assignConceptSpace(conceptSpace, repository.getNamespace());

		// start the real thing
		
		TripleStore activeTripleStore = owlModel.getTripleStoreModel()
				.getActiveTripleStore();
		JenaTripleStore ts = (JenaTripleStore) owlModel.getTripleStoreModel()
				.createTripleStore(repository.getFileURL().toString());
		owlModel.getTripleStoreModel().setActiveTripleStore(ts);

		ProtegeOWLParser parser = new ProtegeOWLParser(owlModel, true);
		try {
			parser.loadTriples(ts, repository.getName().toString(), this
					.createARPInvokation(repository.getFileURL().openStream(),
							repository.getFileURL().toString()));
			// parser.run(repository.getFileURL().openStream(),
		} catch (Exception e) {
			throw new ThinklabAmbiguousResultException(e);
		}
		owlModel.getTripleStoreModel().setActiveTripleStore(activeTripleStore);
		owlModel.copyFacetValuesIntoNamedClses();
	
		// Assign the triplestore
		tripleStore = ts;
        
		updateKR();
		
		@SuppressWarnings("unchecked")
		List<TripleStore> cts =  owlModel.getTripleStoreModel().getTripleStores();
		int index = cts.indexOf(tripleStore);
		if(index+1<cts.size()){ //there are more triplestores loaded due to the nested imports!
			for(int i=index+1; i<cts.size();i++){
				
				URI ontologyURI;
				try {
					ontologyURI = new URI(owlModel.getTripleStoreModel().getTripleStore(i).toString().substring(12,owlModel.getTripleStoreModel().getTripleStore(i).toString().length()-1));
				} catch (Exception e) {
					throw new ThinklabIOException("Cant get default namespace for "+owlModel.getTripleStoreModel().getTripleStore(i).toString() +e);
				} 
				
				
				
				String localFile =(lfr.getOntologyLocationDescription(ontologyURI));
				URL localFileURL;
				try {
					localFileURL = (new File(localFile)).toURL();
				} catch (MalformedURLException e) {
					try {
						localFileURL = ontologyURI.toURL();
					} catch (MalformedURLException e1) {
						throw new ThinklabIOException(e1);
					}
				}
		
				@SuppressWarnings("unused")
				Ontology o = new Ontology(kr,(JenaTripleStore) owlModel.getTripleStoreModel().getTripleStore(i),localFileURL);
			}
		}
		
		r.remove(lfr);
		return this.conceptSpace;

	}

	private ARPInvokation createARPInvokation(final InputStream inputStream,
			final String uri) {
		ARPInvokation invokation = new ARPInvokation() {
			public void invokeARP(ARP arp) throws Exception {
				arp.load(inputStream, uri);
				inputStream.close();
			}
		};
		return invokation;
	}


	
	public Ontology(FileKnowledgeRepository kr, String cName, URL url) throws ThinklabIOException {
		Thinklab.get().logger().debug("  Loading ontology from "+url);
		this.kr = kr;
		this.owlModel = kr.owlModel;
		try {
			this.repository = new ThinklabLocalRepository(url,new URI(DEFAULT_TEMP_URI+cName), true);
		} catch (Exception e) {
			throw new ThinklabIOException("Cant create file ontology " + toString());
		}

		// Assign the repository
		RepositoryManager r = owlModel.getRepositoryManager();
		r.addProjectRepository(repository);
		
		//		 Assign the prefered namespace
		assignConceptSpace(cName, repository.getNamespace());

		tripleStore = (JenaTripleStore) owlModel.getTripleStoreModel().createTripleStore(repository.getFileURL().toString());
		owlModel.getTripleStoreModel().setActiveTripleStore(tripleStore);
		ontology = owlModel.createOWLOntology(repository.getNamespace());
		tripleStore.setDefaultNamespace(repository.getNamespace());
		owlModel.getTripleStoreModel().setActiveTripleStore(owlModel.getTripleStoreModel().getTopTripleStore());
		owlModel.copyFacetValuesIntoNamedClses();
		
		updateKR();
		}


	private Ontology(FileKnowledgeRepository kRepository, JenaTripleStore tripleStore, URL fileURL) throws ThinklabIOException {

		Thinklab.get().logger().debug("  Diverting to ontology "+fileURL);

		this.kr = kRepository;
		this.owlModel = kr.owlModel;
		this.tripleStore =tripleStore;
		
		try {
			this.repository = new ThinklabLocalRepository(fileURL, new URI(tripleStore.toString().substring(12,tripleStore.toString().length()-1)), false);
		} catch (URISyntaxException e) {
			throw new ThinklabIOException(e);
		}
		this.owlModel.getRepositoryManager().addProjectRepository(repository);
		this.tripleStore.setName(repository.getFileURL().toString());
		String cs = owlModel.getNamespaceManager().getPrefix(repository.getNamespace());
		this.conceptSpace = assignConceptSpace(cs, repository.getNamespace());
		
		Thinklab.get().logger().info("Imported ontology: " + repository.toString() );
		
		updateKR();
		
	}

	/**
	 * When a short name is requested, this could be rejected, because is
	 * already used by another ontology
	 * 
	 * @param conceptSpace
	 * @param actualURI
	 * @return conceptSpace
	 */
	private String assignConceptSpace(String cSpace, String namespace) {
		if(cSpace == null) cSpace = "p1";
		NamespaceManager nsm = owlModel.getNamespaceManager();
// Case 1: prefix exists
		if (nsm.getPrefixes().contains(cSpace)) {
// Case 1a: prefix points to the same URI, i.e. is set already!
			if (nsm.getNamespaceForPrefix(cSpace).equalsIgnoreCase(namespace)) {
				this.conceptSpace = cSpace;
			}
// Case 1b : prefix points to another URI --> Create new prefix and add it
			else {
				String prefix = nsm.getPrefix(namespace);
//	Case 1bi: might the URI exists
				if (prefix != null || nsm.getDefaultNamespace().equalsIgnoreCase(namespace)) {
					this.conceptSpace = prefix;
				} else {
//	Case 1bii : have to add uri and generate cspace
					int index = 1;
					do {
						prefix = "p" + index++;
					} while (owlModel.getNamespaceManager().getNamespaceForPrefix(prefix) != null);
					this.conceptSpace = prefix;
					nsm.setPrefix(namespace, conceptSpace);
				}
			}
		}
// Case 2: Prefix does not exist
		else {
			String prefix = nsm.getPrefix(namespace);
// Case 2a: URI has an other prefix registered so return that one!
			if (prefix != null || nsm.getDefaultNamespace().equalsIgnoreCase(namespace)) {
				this.conceptSpace = prefix;
			}
// Case 2b: URI is not registered, add it
			else {
				this.conceptSpace = cSpace;
				nsm.setPrefix(namespace, conceptSpace);
			}
		}
		
		
		try {
			KnowledgeManager.get().registerURIMapping(getURI(), this.conceptSpace);
		} catch (ThinklabNoKMException e) {
			// won't happen
		}
		return this.conceptSpace;
	}

	public IProperty createClassificationProperty(String ID, IConcept range, IProperty parent) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public IConcept createConcept(String ID, IConcept parent) throws ThinklabException {
		
		if(!repository.isWritable()){
			throw new ThinklabIOException(" cannot create concept " + ID + " in read-only concept space " +
					conceptSpace);
		}
		
		TripleStore ts = owlModel.getTripleStoreModel().getActiveTripleStore();
		owlModel.getTripleStoreModel().setActiveTripleStore(tripleStore);
		
		OWLClass owlClass = 
			parent == null ?
					owlModel.createOWLNamedClass(ID) :
					owlModel.createOWLNamedSubclass(new SemanticType(conceptSpace,ID).toString(),owlModel.getOWLNamedClass(parent.getSemanticType().toString()));
					
		owlModel.copyFacetValuesIntoNamedClses();
		owlModel.getTripleStoreModel().setActiveTripleStore(ts);
		
		return new Concept(owlClass);
	}

	private void resolveReferences (Collection<ReferenceRecord> refs) throws ThinklabException {

		for (ReferenceRecord r : refs) {
			
			IInstance rr = this.getInstance(r.reference);
			if (rr == null) {
				throw new ThinklabResourceNotFoundException("internal error: can't resolve reference to object " + r.reference);
			}
			
			r.target.addObjectRelationship(r.property, rr);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.ima.core.IOntology#createInstance(java.lang.String,
	 *      org.integratedmodelling.ima.core.IConcept)
	 */

	public IInstance createInstance(String ID, IConcept c)
			throws ThinklabMalformedSemanticTypeException, ThinklabDuplicateNameException, ThinklabIOException {
		
		String label = null;
		
		if(!repository.isWritable()){
			throw new ThinklabIOException(" cannot create instance " + ID + " in read-only concept space " +
					conceptSpace);
		}

		if(c.isAbstract()) {
			throw new ThinklabIOException(" cannot create an instance of abstract concept " + c);
		}

		if (ID == null) {
			ID = getUniqueObjectName(getConceptSpace());
		}
		
		if (getInstance(ID) != null) {
			
			if (!acceptDuplicateIDs) {
				
				throw new ThinklabDuplicateNameException("instance with name " + ID
						+ " already exists in concept space " + conceptSpace);
			}
			
			/* if we find a duplicated ID, we add progressive numbers to it, and
			 * we set the desired instance as a label, which can later be overwritten
			 * by any label the user has specified.
			 */
			int cnt = 1;
			label = ID;
			
			do {
				ID = ID + "_" + cnt++;
			} while (getInstance(ID) != null);
			

		}
		
		owlModel.getTripleStoreModel().setActiveTripleStore(tripleStore);
		OWLClass cls = owlModel.getOWLNamedClass(c.getSemanticType().toString());
		 
		String cs = c.getConceptSpace();
		if (!cs.equals(conceptSpace)) {
			try {
				String uri = KnowledgeManager.get().getURIFromConceptSpace(cs);
				tripleStore.setPrefix(uri,cs);
			} catch (ThinklabException e) { 
				throw new ThinklabMalformedSemanticTypeException(" Concept " + c.getSemanticType() + "cannot be resolved");
			}			
		}
		
		RDFIndividual resource =  (RDFIndividual) cls.createInstance(conceptSpace+":"+ID);
		
		owlModel.getTripleStoreModel().setActiveTripleStore(owlModel.getTripleStoreModel().getTopTripleStore());
		Instance ret = new Instance(resource);
		
		/*
		 * if we had a duplicated ID, set the originally wanted ID as an initial label.
		 */
		if (label != null) {
			ret.addLabel(label);
		}
		
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.ima.core.IOntology#createInstance(org.integratedmodelling.ima.core.IInstance)
	 */
	public IInstance createInstance(IInstance i) throws ThinklabException {
		
		IInstance inst = getInstance(i.getLocalName());
		if (inst != null)
			return inst;
		return createInstance(i.toList(null));
	}


	public IInstance createInstance(String ID, Polylist list) throws ThinklabException {
		
		ArrayList<ReferenceRecord> reftable = new ArrayList<ReferenceRecord>();	
		IInstance ret  = createInstanceInternal(ID, list, reftable);
		resolveReferences(reftable);
		
		return ret;
		
	}

	public IInstance createInstance(Polylist list) throws ThinklabException {

		ArrayList<ReferenceRecord> reftable = new ArrayList<ReferenceRecord>();	
		IInstance ret  = createInstanceInternal(list, reftable);
		resolveReferences(reftable);
		
		return ret;

	
	}
	
	IInstance createInstanceInternal(String ID, Polylist list, Collection<ReferenceRecord> reftable) 
		throws ThinklabException {

		IInstance ret = null;
		
		for (Object o : list.array()) {
			
			if (ret == null) {

				Pair<IConcept, String> cc = ThinklabOWLManager.getConceptFromListObject(o);
				ret = createInstance(ID,  cc.getFirst());		
				
			} else if (o instanceof Polylist) {
				ThinklabOWLManager.get().interpretPropertyList((Polylist)o, this, ret, reftable);
			}
		}
		
		return ret;
	}

	IInstance createInstanceInternal(Polylist list, Collection<ReferenceRecord> reftable)
		throws ThinklabException {

		IInstance ret = null;
		
		for (Object o : list.array()) {
			
			/**
			 * We may simply add an instance as the only list element, meaning we just want to 
			 * use it without creating anything.
			 */
			if (o instanceof IInstance) {
				return (IInstance)o;
			}
			
			if (ret == null) {

				Pair<IConcept, String> cc = ThinklabOWLManager.getConceptFromListObject(o);
				ret = createInstance(
						cc.getSecond() == null ? getUniqueObjectName(getConceptSpace()) : cc.getSecond(),  
						cc.getFirst());		
				
			} else if (o instanceof Polylist) {
				ThinklabOWLManager.get().interpretPropertyList((Polylist)o, this, ret, reftable);
			}
			
		}
		
		return ret;
	}

	public IProperty createLiteralProperty(String ID, IConcept range, IProperty parent) throws ThinklabException {

		// TODO Auto-generated method stub
		
		/* if range is POD, create appropriate DataProperty */
		
		/* else: */
		
		/* if parent is not null, it must be a LiteralProperty */
		
		/* if parent is null, use configured extendedLiteralPropertyType as superproperty */
		
		return null;
	}

	public IProperty createObjectProperty(String ID, IConcept range, IProperty parent) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getConceptSpace() {
//		FIXME: The ConceptSpace of an ontology should be that of the Knowledge Repository
		// TODO FV: we could mandate the two to be the same, for simplicity?
		return conceptSpace;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.ima.core.IOntology#getConcepts()
	 */
	public Collection<IConcept> getConcepts() {

		ArrayList<IConcept> ret = new ArrayList<IConcept>();

		Collection classes = owlModel.getUserDefinedOWLNamedClasses();
	    
		for (Iterator it = classes.iterator(); it.hasNext();) {
	        OWLNamedClass cls = (OWLNamedClass) it.next();
	        
	    	// FIXME do I really have to do this? Is it because we use a unique OWLModel?
	        // There must be a way to get these from OWLOntology
	        // If not, no problem - this won't get called a lot
	        if (cls.getURI().startsWith(getURI()))
	        	ret.add(new Concept(cls));
	    }
	    return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.ima.core.IOntology#getProperties()
	 */
	public Collection<IProperty> getProperties() {

		ArrayList<IProperty> ret = new ArrayList<IProperty>();
		
		Collection classes = owlModel.getUserDefinedOWLProperties();
		
	    for (Iterator it = classes.iterator(); it.hasNext();) {
	    	
	        OWLProperty cls = (OWLProperty) it.next();
	    	// FIXME do I really have to do this? Is it because we use a unique OWLModel?
	        // There must be a way to get these from OWLOntology
	        // If not, no problem - this won't get called a lot
	        if (cls.getURI().startsWith(getURI()))
	        	ret.add(new Property(cls));
	    }
	    return ret;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.ima.core.IOntology#getInstances()
	 */
	public Collection<IInstance> getInstances() throws ThinklabException {

		ArrayList<IInstance> ret = new ArrayList<IInstance>();
		
		/*
		 * FIXME - or better FIXYOU, the frickin' getRDFInstances does not work.
		 */
		Collection classes = owlModel.getUserDefinedOWLNamedClasses();
		for (Iterator it = classes.iterator();it.hasNext();) {
			OWLNamedClass rr = (OWLNamedClass)it.next();
			
			Collection col2 = rr.getInstances(false);
			Iterator iter2 = col2.iterator();
			while (iter2.hasNext()){
				
				Object in = iter2.next();
				
				// TODO this one also gets RDFLists (and who knows what else)
				if (in instanceof OWLIndividual) {
					OWLIndividual ind = (OWLIndividual)in;
					if (ind.getURI().startsWith(getURI()) && 
							!ThinklabOWLManager.get().isReifiedLiteral(ind.getURI()))
						ret.add(new Instance(ind));
				} 
			}
		}
	    return ret;
	
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.ima.core.IOntology#getConcept(java.lang.String)
	 */
	public IConcept getConcept(String ID) {
		
		// The conceptSpace is the actual concept space Protege' knows
		OWLNamedClass oc = null;
		
		oc = owlModel.getOWLNamedClass(conceptSpace + ":" + ID);
		if (oc != null)
			return new Concept(oc);
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.ima.core.IOntology#getInstance(java.lang.String)
	 */
	public IInstance getInstance(String ID) {
		// TODO check that conceptSpace is the actual concept space Protege knows
		RDFIndividual oc = null;
		try {
			oc = owlModel.getRDFIndividual(conceptSpace + ":" + ID);
		} catch (Exception e) {
			// this happens if we look for e.g. a property name: the sucker will retrieve the resource
			// and fail when casting it to RDFIndividual...
		}
		
		if (oc != null)
			return new Instance(oc);
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.ima.core.IOntology#getProperty(java.lang.String)
	 */
	public IProperty getProperty(String ID) {
		// TODO check that conceptSpace is the actual concept space Protege knows
		OWLProperty oc = null;
		try {
			oc = owlModel.getOWLProperty(conceptSpace + ":" + ID);
		} catch (Exception e) {
			// see above
		}
		if (oc != null)
			return new Property(oc);
		
		return null;

	}

	public String getURI() {
		return repository.getNamespace();
	}

	public String getUniqueObjectName(String prefix) {
	
		return owlModel.createNewResourceName(prefix);
	}

	public void read(URL url) {
		try {
			Thinklab.get().logger().debug("  Loading ontology from "+url);
			if (conceptSpace == null) 
				conceptSpace = MiscUtilities.getNameFromURL(url.toString());
			read(url, conceptSpace, repository.isWritable());
		} catch (ThinklabException e) {
			Thinklab.get().logger().fatal(e);
		}
	}

	public void removeInstance(String id) throws ThinklabException {

		Instance i = (Instance) getInstance(id);
		if (i != null) {
			String uri = i.getURI();
			i.instance.delete();
			ThinklabOWLManager.get().removeInstanceData(uri);
		}
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.ima.core.IOntology#write(java.io.OutputStream)
	 */
	@SuppressWarnings("unchecked")
	public void write(OutputStream os) {
		List fillTripleStores = new ArrayList();
		fillTripleStores.add(tripleStore);
		Map tripleStore2Model = new HashMap();
        @SuppressWarnings("unused") OntModel ontModel = Protege2Jena.createOntModel(owlModel, fillTripleStores, tripleStore2Model);
        Model model = (Model) tripleStore2Model.get(tripleStore);
        try {
			JenaOWLModel.saveModel(os, model, FileUtils.langXMLAbbrev, repository.getNamespace());
		} catch (IOException e) {
			Thinklab.get().logger().warn("Saving of ontology " + repository.getName() + " has failed.");
		}
	}


	public void addDescription(String desc) {
		ontology.addComment(desc);
	}

	public void addDescription(String desc, String language) {
		// TODO Protege does not support languages in comments
		ontology.addComment(desc);
	}

	public void addLabel(String desc) {
		ontology.addLabel(desc, null);
	}

	public void addLabel(String desc, String language) {
		ontology.addLabel(desc, language);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.ima.core.IResource#equals(java.lang.String)
	 */
	public boolean equals(String s) {
		return getURI().equals(s);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.ima.core.IResource#equals(org.integratedmodelling.ima.core.IResource)
	 */
	public boolean equals(IResource r) {
		return r.getURI().equals(getURI());
	}
	
	public String getLabel() {
		
		return ThinklabOWLManager.getLabel(ontology, null);	
	}
	
	public String getDescription() {
		
		return ThinklabOWLManager.getComment(ontology, null);	
	}
	
	public String getLabel(String languageCode) {
		
		return ThinklabOWLManager.getLabel(ontology, languageCode);
	}
	
	public String getDescription(String languageCode) {

		return ThinklabOWLManager.getComment(ontology, null);
	}

	public String toString() {
		return conceptSpace + " (" + getURI() + ")";
	}

	public void setLastModificationDate(long l) {
		lastModified = l;
	}

	public void createEquivalence(IInstance o1, IInstance o2) {
		((Instance)o1).instance.addSameAs(((Instance)o2).instance);
	}

	public void allowDuplicateInstanceIDs() {
		this.acceptDuplicateIDs = true;
	}

	public long getLastModificationDate() {
		return lastModified;
	}


}