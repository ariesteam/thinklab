/**
 * ThinklabLocalRepository.java
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.utils.HttpDocumentCache;
import org.integratedmodelling.utils.MiscUtilities;

import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.util.OntologyNameExtractor;

/**
 * The local repository class will be using the Protege
 * FileRepository methods for analyzing imports and assign 
 * local repositories from where ontologies can be loaded.
 * 
 * @author Ioannis N. Athanasiadis, Dalle Molle Institute for Artificial Intelligence, USI/SUPSI
 *
 * @since 19 Apr 2006
 */
public class ThinklabLocalRepository implements Repository {

    private URL fileURL; // i.e. file:///bla/bla.owl
  
    private URI name;// i.e. http://bla.com 
    private String namespace;// i.e. http://bla.com#
    private boolean writable = true;
    private static Logger log = Logger.getLogger(ThinklabLocalRepository.class);
    static HttpDocumentCache httpCache = null;
    
    /**
     * Filters URL and turns any web-based into a file-based one, caching as necessary.
     * 
     * TODO Ioannis please check - is this ok? Were you preventing http: imports for other reasons?
     * 
     * @param url
     * @return
     * @throws ThinklabIOException 
     */
    private URL cacheImports(URL url) throws ThinklabIOException  {

    	URL ret = url;
    	
    	if (ret.getProtocol().equals("http")) {
    		
    		if (httpCache == null)
    			httpCache = new HttpDocumentCache("ontology/imports/http/cache");
    		
    		ret = httpCache.cache(ret);
    	}
    	
    	return ret;
    }
 
    public ThinklabLocalRepository(URL fileurl, boolean writable) throws ThinklabIOException{

    	// Filter URL through http importer
    	fileurl = cacheImports(fileurl);
    	setFileURL(fileurl);
    	setWritable(writable);
    	try {
			InputStream io = fileURL.openStream();
			OntologyNameExtractor one = new OntologyNameExtractor(io,fileURL);
			setName(one.getOntologyName());
			io.close();
		} catch (IOException e) {
			throw new ThinklabIOException("The file "+fileURL+" is incompatible with OWL standards.");
		}
    	
       
        
    }
    public ThinklabLocalRepository(URL fileurl, URI actualURI, boolean writable) throws ThinklabIOException{
//    	if(!fileurl.getProtocol().equals("file")){
//			throw new ThinklabIOException("The url "+fileurl+" is required to be a file.");
//		}
    	// Filter URL through http importer
    	fileurl = cacheImports(fileurl);
    	
    	setFileURL(fileurl);
    	setWritable(writable);
    	setName(actualURI);
    }
    

    
   



    public boolean contains(URI ontologyName) {
        if(ontologyName.toString().equalsIgnoreCase(name.toString())){
        	return true;
        }
        if(ontologyName.toString().equalsIgnoreCase(fileURL.toString())){
        	return true;
        }
    	return false;
    }


    public void refresh() {
    	
    	if(writable){   
    	   	try {
    			InputStream io = fileURL.openStream();
    			OntologyNameExtractor one = new OntologyNameExtractor(io,fileURL);
    			setName(one.getOntologyName());
    			io.close();
    		} catch (IOException e) {
    			e.printStackTrace();
//    			throw new ThinklabIOException("The file "+fileURL+" is incompatible with OWL standards.");
    		}
     	}
    }


    public Collection getOntologies() {
//    	URI fileURI=null;
//		try {
//			fileURI = fileURL.toURI();
//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        return Collections.singleton(name);
    }


    public InputStream getInputStream(URI ontologyName)
            throws IOException {
        if (fileURL != null) {
            return fileURL.openStream();
        }
        else {
            return null;
        }
    }


    public boolean isWritable(URI ontologyName) {
    	if (this.contains(ontologyName))
    		return writable;
    	else return false;
    }


    public OutputStream getOutputStream(URI ontologyName)
            throws IOException {
		File file = new File(getFileURL().getFile());
		try {
			MiscUtilities.backupFile(file);
		} catch (ThinklabIOException e) {
			log.error(e);
		}
		if(file.canWrite()){
			OutputStream os = new FileOutputStream(file);
            return os;
		}
		return null;
    }



    public boolean isSystem() {
        return !writable;
    }


    public String getRepositoryDescription() {
        return "IMA Ontology Repository";
    }


    public String getOntologyLocationDescription(URI ontologyName) {
        return name + " [actual ontology at this location: " + fileURL + "]";
    }


    public String getRepositoryDescriptor() {
        return fileURL.toString();
    }
    public String toString() {
        return fileURL.toString();
    }

	

	public final void setWritable(boolean writable) {
		this.writable = writable;
	}
	public final URI getName() {
		return name;
	}
	public final void setName(URI actualOntologyName) {
		this.name = actualOntologyName;
		this.namespace = name+"#";
	}
	public final URL getFileURL() {
		return fileURL;
	}
	public final void setFileURL(URL fileURL) {
		this.fileURL = fileURL;
	}
	public final String getNamespace() {
		return namespace;
	}
	public final void setNamespace(String namespace) {
		if((namespace.substring(namespace.length()-1,0)).equals("#")){
			this.namespace = namespace;
			try {
				setName(new URI(namespace.substring(0,namespace.length()-1)));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}
	public final boolean isWritable() {
		return writable;
	}
   
	public String getDirectory(){
		return getFileURL().getFile().substring(0,getFileURL().getFile().lastIndexOf("/"));
	}
	public String getFileName(){
		return getFileURL().getFile().substring(getFileURL().getFile().lastIndexOf("/")+1,getFileURL().getFile().length());
	}
    
}