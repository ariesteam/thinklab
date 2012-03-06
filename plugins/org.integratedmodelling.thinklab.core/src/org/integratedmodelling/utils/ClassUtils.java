package org.integratedmodelling.utils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.list.Escape;
import org.integratedmodelling.thinklab.Thinklab;

public class ClassUtils {
	
	public static interface Visitor {

		/**
		 * Do whatever you want with the class, but do not throw an exception.
		 * @param clls
		 */
		public abstract void visit(Class<?> clls);
		
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

	/**
	 * Visit all classes in a package, using the file structure (must be unpacked). Loads the
	 * classes in the process.
	 * 
	 * @param packageName
	 * @param visitor
	 * @param cloader
	 */
	public static void visitPackage(String packageName, Visitor visitor, ClassLoader cloader) {

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
