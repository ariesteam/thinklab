package org.integratedmodelling.utils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.list.Escape;
import org.integratedmodelling.thinklab.Thinklab;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public class ClassUtils {
	
	public static interface Visitor {

		/**
		 * Do whatever you want with the class, but do not throw an exception.
		 * @param clls
		 * @throws ThinklabException 
		 */
		public abstract void visit(Class<?> clls) throws ThinklabException;
		
	}
	
	public static interface AnnotationVisitor {
		public abstract void visit(Annotation acls, Class<?> target) throws ThinklabException;		
	}
	
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
	   */
	  public static List<String> getResourceListing(ClassLoader classLoader, String anyResourcePath, String path)  {
		  try {
			  return getResourceListingInternal(classLoader, anyResourcePath, path, new ArrayList<String>());
		  } catch (Exception e) {
			  throw new ThinklabRuntimeException(e);
		  }
	  }
		  
	  
	  static List<String> getResourceListingInternal(ClassLoader classLoader, String anyResourcePath, String path, List<String> addTo) 
			  throws Exception {
		  
		  /*
		   * right. If the resource is in some other jar as well, the first one on the classpath will be found, not
		   * the one in the class.
		   */
	      URL dirURL = classLoader.getResource(anyResourcePath);
	      
	      if (dirURL != null && dirURL.toString().endsWith("/")) {

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
	      
	      if (dirURL.toString().endsWith("jar")) {
	    	  
	        /* A JAR path */
	        String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
	        JarFile jar = new JarFile(jarPath.toString(), false);
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
	  
	
	public static Annotation getAnnotation(Class<?> target, Class<? extends Annotation> annClass) {

		for (Annotation annotation : target.getAnnotations()) {
			if (annotation.annotationType().equals(annClass)) {
				return annotation;
			}
		}
		return null;
	}
	
	private static Collection<Class<?>> findSubclasses(ArrayList<Class<?>> ret, Class<?> mainClass, String pckgname, ClassLoader cloader) {

		if (ret == null)
			ret = new ArrayList<Class<?>>();

		// Translate the package name into an absolute path
		String name = new String(pckgname).replace('.', '/');

		// Get a File object for the package
		URL url = cloader.getResource(name);
		
		if (url == null)
			return ret;
		
		File directory = new File(Escape.fromURL(url.getFile()));

		if (directory.exists()) {

			// Get the list of the files contained in the package
			String[] files = directory.list();

			for (int i = 0; i < files.length; i++) {

				// we are only interested in .class files
				if (files[i].endsWith(".class")) {
					// removes the .class extension
					String classname = files[i].substring(0,
							files[i].length() - 6);
					try {
						Class<?> clls = Class.forName(pckgname + "." + classname, true, cloader);
						if (mainClass.isAssignableFrom(clls)) {
							ret.add(clls);
						}
					} catch (ClassNotFoundException e) {
						Thinklab.get().logger().warn("task class " + pckgname + "." + classname + " could not be created: " + e.getMessage());
					}
				} else {
					
					File ff = new File(Escape.fromURL(url.getFile()) + "/" + files[i]);
					
					if (ff.isDirectory()) {
						String ppk = pckgname + "." + files[i];
						findSubclasses(ret, mainClass, ppk, cloader);
					}
				}				
			}
		}

		return ret;
	}

	
	public static void visitAnnotations(String packageName, Class<? extends Annotation> acls, AnnotationVisitor annotationVisitor)
				throws ThinklabException {
		
		Reflections reflections = new Reflections(new ConfigurationBuilder()
			.setUrls(ClasspathHelper.forPackage(packageName))
			.setScanners(new SubTypesScanner()));

		for (Class<?> of : reflections.getTypesAnnotatedWith(acls)) {
			annotationVisitor.visit(of.getAnnotation(acls), of);
		}
	}
	
	/**
	 * Visit all classes in a package, using the file structure (must be unpacked). Loads the
	 * classes in the process. Visits only member classes that are static and public.
	 * 
	 * @param packageName
	 * @param visitor
	 * @param cloader
	 * @deprecated
	 * @throws ThinklabException 
	 */
	public static void visitPackage(String packageName, Visitor visitor, ClassLoader cloader) throws ThinklabException {

		// Translate the package name into an absolute path
		String name = new String(packageName).replace('.', '/');

		// Get a File object for the package
		URL url = cloader.getResource(name);
		
		if (url == null)
			return;
		
		File directory = new File(Escape.fromURL(url.getFile()));

		if (directory.exists()) {

			// Get the list of the files contained in the package
			String[] files = directory.list();

			for (int i = 0; i < files.length; i++) {

				// we are only interested in .class files
				if (files[i].endsWith(".class")) {
					// removes the .class extension
					String classname = files[i].substring(0,
							files[i].length() - 6);
					try {
						Class<?> clls = Class.forName(packageName + "." + classname, true, cloader);
						visitor.visit(clls);
						
						/*
						 * scan all the direct, static and public member classes.
						 */
						for (Class<?> cllls : clls.getClasses())
							if (cllls.getDeclaringClass().equals(clls) &&
								Modifier.isPublic(cllls.getModifiers()) &&
								Modifier.isStatic(cllls.getModifiers()))
								visitor.visit(cllls);
						
					} catch (ClassNotFoundException e) {
						Thinklab.get().logger().warn("task class " + packageName + "." + classname + " could not be created: " + e.getMessage());
					}
				} else {
					
					File ff = new File(Escape.fromURL(url.getFile()) + "/" + files[i]);
					
					if (ff.isDirectory()) {
						String ppk = packageName + "." + files[i];
						visitPackage(ppk, visitor, cloader);
					}
				}				
			}
		}
	}
	
	/**
	 * Return all subclasses of given class in given package. Uses file structure in 
	 * classpath as seen by passed classloader. Loads ALL classes in package in 
	 * the process. Use with caution - it's sort of dirty, but it's the only way to obtain
	 * the class structure without preloading classes.
	 * 
	 * @param mainClass
	 * @param pckgname
	 * @return
	 */
	public static Collection<Class<?>> findSubclasses(Class<?> mainClass, String pckgname, ClassLoader cloader) {
		return findSubclasses(null, mainClass, pckgname, cloader);
	}
}
