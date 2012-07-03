/**
 * 
 */
package org.integratedmodelling.thinklab.tests;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.utils.MiscUtilities;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;



/**
 * @author Ferd
 *
 */
public class MixedTests {

	 /**
	   * List directory contents into a given collection for a resource folder. Recursive.
	   * This is basically a brute-force implementation.
	   * Works for regular files and also JARs.
	   * 
	   * @author Greg Briggs, Ferdinando Villa
	   * @param classLoader a classloader to use to resolve it.
	   * @param anyResourcePath used to locate the collection/jar: a resource that we know lives in the same folder or
	   * 	    jar of the path we look for.
	   * @param path Should end with "/", but not start with one.
	   * @return Just the name of each member item, not the full paths.
	   * @throws URISyntaxException 
	   * @throws IOException 
	   */
	  List<String> getResourceListing(ClassLoader classLoader, String anyResourcePath, String path) 
			  throws Exception {
		  return getResourceListingInternal(classLoader, anyResourcePath, path, new ArrayList<String>());
	  }
		  
		  
		  List<String> getResourceListingInternal(ClassLoader classLoader, String anyResourcePath, String path, List<String> addTo) 
			  throws Exception {
		  
		  /*
		   * right. If the resource is in some other jar as well, the first one on the classpath will be found, not
		   * the one in the class.
		   */
	      URL dirURL = classLoader.getResource(anyResourcePath);
	      
	      if (dirURL != null && dirURL.getProtocol().equals("file")) {

	    	  dirURL = classLoader.getResource(path);
	    	  if (dirURL != null) {
	    		  for (String s : new File(dirURL.toURI()).list()) {
	    			  File ff = MiscUtilities.resolveUrlToFile(dirURL.toURI() + "/" + s);
	    			  if (ff.isDirectory())
	    				getResourceListingInternal(classLoader, anyResourcePath, path + s + "/", addTo);
	    			  else
	    				addTo.add(path + s);
	    		  }
	    	  }
	   
	    	  return addTo;
	      } 

	      if (dirURL == null) {
	    	  
	    	  /*
	    	   * no way
	    	   */
	    	  return addTo;
	      }
	      
	      if (dirURL.getPath().indexOf("!") >= 0)
	    	  dirURL = new URL(dirURL.getPath().substring(0, dirURL.getPath().indexOf("!"))); //strip out only the JAR file
	
	      if (dirURL.toString().endsWith(".jar")) {
	    	  
	    	File fuck = new File(dirURL.getFile());
	    	if (fuck.exists()) 
	    		System.out.println("fuck");
	        JarFile jar = new JarFile(fuck);
	        Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
	        Set<String> result = new HashSet<String>(); //avoid duplicates in case it is a subdirectory
	        while(entries.hasMoreElements()) {
	          String name = entries.nextElement().getName();
	          if (name.startsWith(path)) { //filter according to the path
	            String entry = name.substring(path.length());
	            int checkSubdir = entry.indexOf("/");
	            if (checkSubdir >= 0) {
	              entry = entry.substring(0, checkSubdir);
	              getResourceListingInternal(classLoader, anyResourcePath, path + entry + "/", addTo);
	            } else {
	            	result.add(entry);
	            }
	          }
	        }
	      }
	      
	      return addTo;
	  }
	
	@Test
	public void testReferenceList() throws Exception {

		Reflections reflections = new Reflections(new ConfigurationBuilder()
      	.setUrls(ClasspathHelper.forPackage("knowledge"))
      	.setScanners(new ResourcesScanner()));
		
		for (String of : reflections.getResources(Pattern.compile(".*\\.owl"))) {
			System.out.println(of);
		}

		Reflections reflections2 = new Reflections(new ConfigurationBuilder()
      	.setUrls(ClasspathHelper.forPackage("org.integratedmodelling.thinklab"))
      	.setScanners(new SubTypesScanner()));

		for (Class<?> of : reflections2.getTypesAnnotatedWith(Concept.class)) {
			System.out.println(of);
		}

//		
//		for (String zio : getResourceListing(this.getClass().getClassLoader(), "knowledge/thinklab.owl", "knowledge/")) {
//			System.out.println(zio);
//		}
//		
//		for (String zio : getResourceListing(this.getClass().getClassLoader(), "org/geotools/map/DefaultMapLayer.class", "META-INF/")) {
//			System.out.println(zio);
//		}
	}
		

}
