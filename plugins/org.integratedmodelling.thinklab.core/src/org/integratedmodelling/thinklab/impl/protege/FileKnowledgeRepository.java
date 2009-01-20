/**
 * FileKnowledgeRepository.java
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.exception.ThinklabAmbiguousResultException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IOntology;
import org.integratedmodelling.utils.CopyURL;
import org.integratedmodelling.utils.FileTypeFilter;
import org.integratedmodelling.utils.NameGenerator;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ProtegeOWLReasoner;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ReasonerManager;
import edu.stanford.smi.protegex.owl.inference.util.ReasonerPreferences;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.jena.triplestore.JenaTripleStoreModel;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.RepositoryManager;
import edu.stanford.smi.protegex.owl.repository.util.OntologyNameExtractor;

/**
 * A Knowledge Repository that uses the Protege-OWL to create models. Abstract
 * and implemented according to different physical storage models in subclasses.
 * The current implementation requires that a Protege repository is defined for
 * the local files
 * 
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 * @author Ioannis N. Athanasiadis, Dalle Molle Institute for Artificial Intelligence, USI/SUPSI
 * 
 */

public class FileKnowledgeRepository implements IKnowledgeRepository {
	
	public final String DEFAULT_BASE = "kr://knowledgerepository/";
	
	File repositoryDirectory = null;
	
	// The OWL model is sufficient for storing multiple ontologies
	public JenaOWLModel owlModel;
	
	// namespace and repository managers are extracted for convinience
	private NamespaceManager nameSpaceManager;
	
	private RepositoryManager repositoryManager;
	
	private OWLOntology rootontology = null;
	
	protected Hashtable<URL, String> loadedFiles = new Hashtable<URL, String>();
	protected Hashtable<String, String> loadedNamespaces = new Hashtable<String, String>(); //Namespace-ConceptSpace
	protected Hashtable<String, Ontology> loadedOntologies = new Hashtable<String, Ontology>();
	
	private File tempDirectory = null;
	
	@SuppressWarnings("unused")
	private File backupDirectory = null;
    
    private Concept rootClass = null;

	protected ProtegeOWLReasoner reasoner;
    
	public FileKnowledgeRepository() throws ThinklabIOException {
		this(null);
	}
	
	/**
	 * This is the default constructor that loads ontologies from the repository
	 * directory and initializes the OWL model.
	 * 
	 * @throws ThinklabIOException
	 */
	public FileKnowledgeRepository(String protegePath) throws ThinklabIOException {

		/**
		 * Redirect plugin folder to system/protege directory, resorting to user dir of program if that
		 * does not exist. Throw informative exception if the OWL resources cannot be found anyway.
		 * TODO installer will need to put the protege stuff in the proper places.
		 */
		File protegeDir = 
				new File(protegePath == null ?
						LocalConfiguration.getProperties().getProperty("thinklab.protege.path",
						Thinklab.get().getLoadDirectory() + "/lib") : 
						protegePath);
		
		
		if (!protegeDir.exists()) {
			protegeDir = new File(System.getProperty("user.dir"), "lib");
		}
		
		File pluginDir = new File(protegeDir + "/plugins/edu.stanford.smi.protegex.owl");
		
		if (!pluginDir.exists()) {
			throw new ThinklabIOException(
					"FileKnowledgeRepository: Protege support ontologies not found in " + 
					pluginDir +
					"; please copy the plugins/ folder and its contents in it from the Thinklab/lib directory");
		}
		
		Thinklab.get().logger().info("protege system libraries read from " + pluginDir);
				
		System.setProperty("protege.dir", protegeDir.toString());

		ProtegeOWLParser.inUI = false;
				
		repositoryDirectory = LocalConfiguration.getDataDirectory("ontology/repository");
		backupDirectory     = LocalConfiguration.getDataDirectory("ontology/backup");
		tempDirectory       = LocalConfiguration.getDataDirectory("ontology/tmp");
		
		owlModel = (JenaOWLModel) (new JenaKnowledgeBaseFactory()).createKnowledgeBase(new HashSet());
		owlModel.setProject(new Project("ThinkLabFileKnowledgeRepository",new ArrayList()));
		nameSpaceManager = owlModel.getNamespaceManager();
		nameSpaceManager.setDefaultNamespace(DEFAULT_BASE + NameGenerator.newName("KR") + ".owl#");
		repositoryManager = owlModel.getRepositoryManager();
		rootontology = owlModel.getDefaultOWLOntology();
		rootClass = new Concept(owlModel.getOWLThingClass());
		
		ReasonerManager reasonerManager = ReasonerManager.getInstance(); 
		reasoner = reasonerManager.getReasoner(this.owlModel);		
		
	}
	
	public void initialize() throws ThinklabException {
		
		// // load the repository managers
		// FilenameFilter repfilter = new
		// FileTypeFilter(FileTypeFilter.RepositoryFileType);
		// for (File f : repositoryDirectory.listFiles(repfilter)) {
		// LocalRepository r = new LocalRepository(f);
		// r.refresh();
		// owlModel.getRepositoryManager().addProjectRepository(r);
		// log.debug("loading repository for file " + f + " : \n"
		// + r.contains(f.toURI()) + " " + r.getOntologies().size());
		// }
		
		FilenameFilter owlfilter = new FileTypeFilter(
				FileTypeFilter.OWLFileType);
		for (File f : repositoryDirectory.listFiles(owlfilter)) {
			String nspace = null;
			try {
				URL url = f.toURL();
				// This is the filename without the .owl ending. It will be used
				// as the short namespace!
				nspace = f.getName().substring(0, f.getName().length() - 4);
				importOntology(url, nspace, false);
				
			} catch (Exception ex) {
				Thinklab.get().logger().warn("Cant load ontology for the file: " + f.getName()
						+ " " + ex.getMessage());
			}
		}
		if (LocalConfiguration.hasResource("thinklab.reasoner.url")) {
			connectReasoner(LocalConfiguration.getResource("thinklab.reasoner.url"));
			Thinklab.get().logger().info(reasonerConnected()? "Connected to reasoner: "+ reasoner.getIdentity().getName() : "Reasoner not connected.");
		} else {
			Thinklab.get().logger().info("Reasoner support not configured");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.ima.core.IKnowledgeRepository#importOntology(java.net.URL,
	 *      java.lang.String)
	 */
	
	public String importOntology(URL url, String nspace, boolean saveToRepository) throws ThinklabException {
		//TODO: check if the url ends with # and eliminate it
		Thinklab.get().logger().debug("Importing ontology " + nspace + " from :" + url);
		URI ontoURI;
		try {
			InputStream io = url.openStream();
			OntologyNameExtractor one = new OntologyNameExtractor(io,url);	
			ontoURI = one.getOntologyName();
			io.close();
		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}
		if(ontoURI == null)
			throw new ThinklabAmbiguousResultException("The URI for the resource "+ url + "is unknown.");
		@SuppressWarnings("unchecked")
		Iterator<Repository> reps = repositoryManager.getProjectRepositories().iterator();
		Repository r = reps.next();
		boolean flag = false;
		while(reps.hasNext() && !flag){
			r=reps.next();
			flag = r.contains(ontoURI);			
		}

		if (flag ){
			Thinklab.get().logger().debug("Ontology exists    : "+ url + " is alrady loaded.");
			if(r instanceof ThinklabLocalRepository)
				return loadedNamespaces.get(((ThinklabLocalRepository) r).getNamespace());
			else
				// things have gone wrong - should never happen
				// most probably ontologies in the /repository directory
				// do not import the thinklab-core one!
				throw new ThinklabException("Repository at "+repositoryDirectory+" is corrupted. Please clean it and start again!");
		}
		else{

			File tf = new File(url.getFile());

			boolean isSystem = 
				tf.getParent().equals(repositoryDirectory.getPath());
			
			if (saveToRepository) {
		
				File f = new File(repositoryDirectory + "/" + tf.getName());

				if (!isSystem && f.exists() && tf.lastModified() > f.lastModified()) {
					moveFileToBackup(f);		
				}

				/* copy to local repository unless already there */
				if (!isSystem)
					CopyURL.copy(url, f);
			}
			
			Ontology onto = new Ontology(this, url, nspace, !isSystem);

			if (saveToRepository)
				onto.setLastModificationDate(tf.lastModified());

			String cspace = onto.getConceptSpace();
//			loadedOntologies.put(cspace, onto);
//			loadedFiles.put(url, cspace);

			Thinklab.get().logger().info("Loaded   ontology: " + url);

			return cspace;
		}
	}
	
	public void exportOntologyByName(URI fileuri, String oname) {
		Ontology onto = loadedOntologies.get(oname);
		File outfile = new File(fileuri);
		try {
			outfile.createNewFile();
		} catch (IOException e) {
			Thinklab.get().logger().warn("Cant create file. " + e.getStackTrace());
		}
		if (outfile.canWrite()) {
			OutputStream os;
			try {
				os = new FileOutputStream(outfile);
				onto.write(os);
			} catch (FileNotFoundException e) {
				Thinklab.get().logger().warn("Cant create file. " + e.getStackTrace());
			}
		} else
			Thinklab.get().logger().warn("File " + fileuri + " does not exist.");
	}
	
	public IOntology retrieveOntology(String ontName) {
		return loadedOntologies.get(ontName);
	}
	
	public IOntology requireOntology(String ontName)
	throws ThinklabResourceNotFoundException {		
		if (!loadedOntologies.containsKey(ontName))
			throw new ThinklabResourceNotFoundException("Ontology " + ontName
					+ " does not exist");
		return loadedOntologies.get(ontName);
	}
	
	public Collection<IOntology> retrieveAllOntologies() {
		
		HashSet<IOntology> set = new HashSet<IOntology>();
		Iterator<Ontology> i = loadedOntologies.values().iterator();
		while (i.hasNext()) {
			set.add(i.next());
		}
		return set;
		
	}
	
	public String getURI() {
		return nameSpaceManager.getDefaultNamespace();
	}
	
	public String refreshOntology(URL url, String name, boolean saveToRepository) throws ThinklabException {
		return importOntology(url, name, saveToRepository);
	}
	
	public String toString() {
		return getURI();
	}
	
	public IOntology createTemporaryOntology(String string) throws ThinklabException {
		try {
			File f = new File(tempDirectory, string + ".owl");
			f.createNewFile();
			URL url = f.toURL();
			if (loadedFiles.containsKey(url))
				throw new ThinklabIOException(
						"Can't create temporary ontology with name " + string
						+ ". File already exists.");
			else if (loadedOntologies.containsKey(string))
				throw new ThinklabIOException(
						"Can't create temporary ontology with name " + string
						+ ". Ontology with this name already exists.");
			else {
				Ontology o = new Ontology(this, string, url);
				
				return o;
			}
		} catch (Exception e) {
			
			throw new ThinklabException(e);
		}
		
	}
	
	public void releaseOntology(String s) {
		if (loadedFiles.contains(s) && loadedOntologies.containsKey(s)) {
			Ontology ontology = loadedOntologies.get(s); // the ontology to
			// be deleted
			String ont = ontology.repository.getName().toString();
			repositoryManager.remove(ontology.repository);
			
			// nameSpaceManager.removePrefix(s);
			
			JenaTripleStoreModel tsm = (JenaTripleStoreModel) owlModel
			.getTripleStoreModel();
			TripleStore top = tsm.getTopTripleStore();
			TripleStore ts = tsm.getTripleStore(ontology.repository
					.getFileURL().toString());
			tsm.setActiveTripleStore(top);
			
			// ((AbstractTripleStoreModel)tsm).mnfs.removeFrameStore(ts.getNarrowFrameStore());
			
			// for (Iterator<FrameStore> i =
			// owlModel.getFrameStores().iterator(); i.hasNext();){
			// String name = i.next().getName();
			// if (name.equals(ontology.repository.getName().toString()) ||
			// name.equals(ontology.repository.getFileURL().toString())){
			// owlModel.removeFrameStore((FrameStore)ts.getNarrowFrameStore());
			// }
			// }
			
			tsm.deleteTripleStore(ts);
			owlModel.flushCache();
			
			// Here i try have to remove the unused triples and convert to
			// unnamed resources
			// for(Iterator<Triple> i = (Iterator<Triple>)ts.listTriples();
			// i.hasNext();){
			// Triple currentTriple = i.next();
			// Node subject = currentTriple.getSubject().`;
			// for(Iterator<JenaTripleStore> j = tsm.listUserTripleStores();
			// j.hasNext();){
			// JenaTripleStore currentTripleStore = j.next();
			// //
			// (currentTripleStore.listObjects(subject.getOWLModel().getRDFTypeProperty()),subject)
			
			//					
			// }
			// }
			
			// NarrowFrameStore f = ts.getNarrowFrameStore();
			// for(f)
			
			ts = null;
			ontology = null;
			
			tsm.endTripleStoreChanges();
			
			tsm.setActiveTripleStore(top);
			
			owlModel.copyFacetValuesIntoNamedClses();
			
			owlModel.getDefaultOWLOntology().removeImports(ont);
			
			loadedFiles.remove(s);
			loadedOntologies.remove(s);
			
			System.gc();
			System.out.println("done");
		}
		
	}
	
	public void releaseAllOntologies() {
		owlModel = (JenaOWLModel) (new JenaKnowledgeBaseFactory())
		.createKnowledgeBase(new HashSet());
	}
	
	public void saveAll() throws Exception {
		String name = getURI().substring(getURI().lastIndexOf("/") + 1,getURI().length() - 1);
		File f = new File(tempDirectory, name);
		if (!f.createNewFile()) moveFileToBackup(f);
			owlModel.getTripleStoreModel().getTopTripleStore().setName(f.toURI().toString());
		
//		Enumeration<Ontology> ontos = loadedOntologies.elements();
//		while(ontos.hasMoreElements()){
//			Ontology o = ontos.nextElement();
//			String ontologyName = o.repository.getFileURL().toString();
//			Repository rep = owlModel.getRepositoryManager().getRepository(new URI(ontologyName));
//			if(!rep.isSystem()){
//				OutputStream os = rep.getOutputStream(new URI(ontologyName));
//				o.write(os);
//			}
////				} else{
////					log.error("Cant write at: " + file.toURL());
////				}
////			}	
//		}
		owlModel.save(f.toURI());
	}
	
	public void printDetails() {
		String s = "\n";
		s += ("Knowledge Repository " + getURI()) + "\n";
		Iterator<?> c = nameSpaceManager.getPrefixes().iterator();
		while (c.hasNext()) {
			String ns = c.next().toString();
			s += ("Namespace : " + ns + ": " + nameSpaceManager
					.getNamespaceForPrefix(ns))
					+ "\n";
		}
		Thinklab.get().logger().info(s);
	}
	
	private void moveFileToBackup(File f) throws ThinklabIOException {
		
		File newp = new File(f.getParent() + "/backup");
		
		if (!newp.exists())
			newp.mkdir();
		
		File newf = new File(newp.toString() + "/" + f.getName());
		
		/*
		 * add version number if we have it already. TODO we may condition this
		 * behavior to a system option, and maybe condition the whole backup
		 * thing to an option.
		 */
		int ver = 1;
		while (newf.exists()) {
			newf = new File(newp.toString() + "/" + f.getName() + "."
					+ Integer.toString(ver++));
		}
		try {
			CopyURL.copy(f.toURL(), newf);
		} catch (MalformedURLException e) {
			throw new ThinklabIOException(e.getMessage());
		}
		
		f.delete();
	}
	
	/**
	 * Added for convinience during development
	 * 
	 * @return a list of all resources
	 */
	@SuppressWarnings("unchecked")
	public Iterator<RDFResource> resourceIterator() {
		return (Iterator<RDFResource>) owlModel.listRDFSNamedClasses();
	}
	
	/**
	 * Added for convinience during development
	 * 
	 * @return a list of all resources
	 */
	public void printResources() {
		for (Iterator<RDFResource> i = resourceIterator(); i.hasNext();)
			System.out.println(i.next().toString());
	}
	
	public void exportOntologyByName(OutputStream writer, String oname)
	throws ThinklabIOException {
		Ontology onto = loadedOntologies.get(oname);
		onto.write(writer);
		
	}

    public IConcept getRootConceptType() {
        return rootClass;
    }

	public long getOntologyLastModifiedDate(String ontName) throws ThinklabResourceNotFoundException {
		return ((Ontology)requireOntology(ontName)).getLastModificationDate();
	}
	
	public void connectReasoner(URL address){
		try {
			reasoner.setURL(address.toString());
			ReasonerPreferences.getInstance().setReasonerURL(address.toString());
			
		} catch (Exception e) {
			Thinklab.get().logger().warn("Reasoner address can't be located.");
		}
	}
	public boolean reasonerConnected(){
		return reasoner.isConnected();
	}

	public void classifyTaxonomy() {
		if(reasonerConnected())
			try {
				reasoner.classifyTaxonomy(null);
			} catch (DIGReasonerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

	public OWLModel getOWLModel() {
		return owlModel;
	}

	@Override
	public IConcept getNothingType() {
		// TODO Auto-generated method stub
		return null;
	}
	
}