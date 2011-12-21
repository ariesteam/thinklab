/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.persistence.factory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IOntology;
import org.integratedmodelling.utils.FileTypeFilter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * The main factory for the code generation. It contains a 
 * 
 * @author Ioannis N. Athanasiadis
 * @since Feb 5, 2007
 * @version 0.2
 */
public final class PersistentStorageFactory {
	static Logger log = Logger.getLogger("org.integratedmodelling.persistence.factory.PersistentStorageFactory");
	public static Set<String> processedConcepts;
	
	public static void initialize(){
		processedConcepts = new HashSet<String>();
	}
	
	public static void createPersistentStorage(IOntology onto) throws ThinklabException{
		for(IConcept concept : onto.getConcepts())
			createPersistentStorage(concept);
	}

	public static void createPersistentStorage(IConcept concept)
			throws ThinklabException {
		if(processedConcepts==null)
			PersistentStorageFactory.initialize();
		if (!processedConcepts.contains(concept.getSemanticType().toString())) {
			Set<IConcept> concepts = new HashSet<IConcept>();
			concepts = KMUtils.getAllKnownConcepts(concept, concepts);
			for (IConcept c : concepts) {
				if (!processedConcepts.contains(c.getSemanticType().toString())) {
					log.debug("Processing concept "
							+ c.getSemanticType());
					JavaBeanTemplate jbt = new JavaBeanTemplate(c);
					jbt.storeJavaSource();
//					jbt.storeHJBManager();
//					jbt.storeHJBManagerRemote();
//					jbt.storeHJBManagerLocal();
					jbt.storeHibernateSource();
				}
			}
		}
	}


	public static void createPersistentStorage(String resource) throws Exception{
		if(resource.contains(":")){
			createPersistentStorage(KnowledgeManager.get().requireConcept(resource));
		}else
			createPersistentStorage(KnowledgeManager.get().getKnowledgeRepository().retrieveOntology(resource));
	}

	public static void updatePersistenceConfiguration() throws ThinklabException{
		Document dom = HibernateConfigurator.getPMConfiguration();
		File PM_File = CGUtils.createFileInPluginScratchFolder("src/META-INF/persistence.xml");
		XMLUtils.storeDocumentToFile(dom, PM_File);
	}
	public static void updateHibernateConfiguration() throws ThinklabException{
		

		Document dom = HibernateConfigurator.getHMConfiguration();
		Node sessionfactory = dom.getElementsByTagName("session-factory").item(0);
		// CREATE AND APPEND ALL MAPPINGS
		for (String s: PersistentStorageFactory.processedConcepts){
			IConcept c = KnowledgeManager.KM.requireConcept(s);
			Element mapping = dom.createElement("mapping");
			sessionfactory.appendChild(mapping);
			mapping.setAttribute("resource", CGUtils.getJavaNameWithPath(c).replaceAll("\\.", "/")+".hbm.xml");
		}

		File CFG_File = CGUtils.createFileInPluginScratchFolder("src/hibernate.cfg.xml");
		XMLUtils.storeDocumentToFile(dom, CFG_File);

	}

	/*
	 *See also: http://www.juixe.com/techknow/index.php/2006/12/12/invoke-javac-at-runtime/
	 *and: http://www.juixe.com/techknow/index.php/2006/12/13/java-se-6-compiler-api/ 
	 * http://www.exampledepot.com/egs/java.io/GetFiles.html
	 */
	public static void sealPackage(String name) throws ThinklabException{ 
		HibernateConfigurator.setName(name);
		updatePersistenceConfiguration();
		
		ArrayList<String> opt = new ArrayList<String>();
		
		File srcFolder = new File(CGUtils.getPluginScratchFolder(),"src");
		File binFolder = new File(CGUtils.getPluginScratchFolder(),"src");
//		binFolder.mkdirs();
		
		opt.add("-g:none");
		opt.add("-classpath");  opt.add(System.getProperty("java.class.path"));
		opt.add("-sourcepath"); opt.add(srcFolder.toString());
		opt.add("-d");           opt.add(binFolder.toString());
        //opt.add("-Xstdout /log/compiler.log ");

		FileTypeFilter ftf = new FileTypeFilter(FileTypeFilter.JavaFileType);
		
		for (File s: ftf.listFilesSubFoldersIncluded(srcFolder)){
			opt.add(s.toString());
		}
		log.debug("Javac arguments are: "+opt);
		
		String[] options = new String[opt.size()];
		options = opt.toArray(options);
		com.sun.tools.javac.Main.compile(options);		
		
	
	

		
		String[] jarOptions = new String[]{
		"cvf", CGUtils.getPluginScratchFolder().toString() + 
		              "/"+name+".jar",
		              "-C", binFolder.toString(),
		"."};
		sun.tools.jar.Main.main(jarOptions);
		
	}
	
	
	


	
}

