/**
 * TopologicalSorter.java
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
package org.integratedmodelling.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * <p>Generic topological sorter. Sorts installed content by dependency. 
 * Not the most efficient or convenient, but will do for now.</p>
 * <p>Usage:</p>
 * <pre>
 * 		TopologicalSorter<String> sorter = new TopologicalSorter<String>({"second", "first", "third"});
 *      sorter.addDependency(1,0);
 *      sorter.addDependency(0,2);
 *      sorter.sort();
 * </pre>
 * <p>Order will contain {"first", "second", "third"}</p>
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 *
 */
public class TopologicalSorter {
		
	private Object vertexList[]; // list of vertices
	private int matrix[][]; // adjacency matrix
	private int numVerts; // current number of vertices
	private Object sortedArray[];
	private boolean initialized = false;
	private ArrayList<Pair<Integer,Integer>> dependencies = null;
	private ArrayList<Object> objects = null;
	private HashMap<String, Integer> objectDictionary = null;
	private ArrayList<String> keys = null;
	
	private void initialize(Object[] collection) {

		if (collection == null)
			collection = objects.toArray();
		
		vertexList = collection;
		int nv = collection.length;
		matrix = new int[nv][nv];
		numVerts = nv;
		for (int i = 0; i < nv; i++)
			for (int k = 0; k < nv; k++)
				matrix[i][k] = 0;
		sortedArray = new Object[nv];
				
		initialized = true;		
	}
	
	/**
	 * If this constructor is used, nothing should be done except 
	 * addDependency() and sort(). 
	 * 
	 * @param collection
	 */
	public TopologicalSorter(Object[] collection) {
		dependencies = new ArrayList<Pair<Integer,Integer>>();
		initialize(collection);
	}
	
	/**
	 * If this constructor is used, addObject() and addDependency() should be used
	 * to define the contents.
	 */
	public TopologicalSorter() {
		dependencies = new ArrayList<Pair<Integer,Integer>>();
		objects = new ArrayList<Object>();
		objectDictionary = new HashMap<String, Integer>();
		keys = new ArrayList<String>();
	}
	
	/**
	 * Add an object and return its index.
	 * @param o
	 * @return
	 */
	public int addObject(Object o) {
		
		objects.add(o);
		return objects.size()-1;
	}

	/**
	 * Add an object and return its index. Do not use if the array constructor has
	 * been called. Pass a string to use as a key so that the object passed can be
	 * identified later and its index can be retrieved. Object can be added multiple
	 * times, and if it was entered before it is not added again, but the proper index
	 * is returned.
	 * 
	 * @param o
	 * @return
	 */
	public int addObject(Object o, String key) {
		
		Integer index = objectDictionary.get(key);
		if (index != null)
			return index;
		
		objects.add(o);
		keys.add(key);
		index = objects.size()-1;
		objectDictionary.put(key, index);
		return index;
	}

	
	/**
	 * Return true if an object has been added for the passed key.
	 * @param key
	 * @return
	 */
	public boolean haveObject(String key) {
		return objectDictionary.get(key) != null;
	}	

	/**
	 * Retrieve the index of the key associated with an object when addObject(o, key)
	 * was called. Don't even think of calling it if you haven't done so.
	 */
	public int getObjectIndexForKey(String key) {
		return objectDictionary.get(key);
	}
	
	/**
	 * Add a dependency between elements of the collection passed in the constructor.
	 * @param from the index of the element that should come first
	 * @param to the index of the element that depends on the other
	 */
	public void addDependency(int from, int to) {
		dependencies.add(new Pair<Integer, Integer>(from,to));
	}
	
	/**
	 * Return a collection of all the objects that depend on the one identified by passed
	 * key. Don't even think of using if you haven't used the dictionary functions.
	 * @param key
	 * @return
	 */
	public Collection<Object> getDependents(String key) {
		
		ArrayList<Object> ret = new ArrayList<Object>();
		
		// find id of object
		int id = getObjectIndexForKey(key);
		
		for (Pair<Integer, Integer> deps : dependencies) {
			if (deps.first == id) {
				ret.add(objects.get(deps.second));
			}
		}
		
		return ret;
	}
	
	/**
	 * Create topological sort
	 * 
	 * @return a topologically sorted list of objects.
	 * @throws ThinklabCircularDependencyException
	 */
	public Object[] sort() throws ThinklabCircularDependencyException
	{
		if (!initialized) {
			initialize(null);
		}

		/* setup all dependencies we may have */
		if (dependencies != null) {
			for (Pair<Integer,Integer> dep : dependencies) {
				matrix[dep.first][dep.second] = 1;
			}
		}

		while (numVerts > 0) // while vertices remain,
		{
			// get a vertex with no successors, or -1
			int currentVertex = noSuccessors();
			if (currentVertex == -1) // must be a cycle
				throw new ThinklabCircularDependencyException();
			
			// insert vertex label in sorted array (start at end)
			sortedArray[numVerts - 1] = vertexList[currentVertex];
			
			deleteVertex(currentVertex); // delete vertex
		}
        return sortedArray;
	}
	
	private int noSuccessors() // returns vert with no successors (or -1 if no such verts)
	{ 
		boolean isEdge; // edge from row to column in adjMat
		
		for (int row = 0; row < numVerts; row++) {
			isEdge = false; // check edges
			for (int col = 0; col < numVerts; col++) {
				if (matrix[row][col] > 0) // if edge to another,
				{
					isEdge = true;
					break; // this vertex has a successor try another
				}
			}
			if (!isEdge) // if no edges, has no successors
				return row;
		}
		return -1; // no
	}
	
	public void deleteVertex(int delVert) {
		if (delVert != numVerts - 1) // if not last vertex, delete from vertexList
		{
			for (int j = delVert; j < numVerts - 1; j++)
				vertexList[j] = vertexList[j + 1];
			
			for (int row = delVert; row < numVerts - 1; row++)
				moveRowUp(row, numVerts);
			
			for (int col = delVert; col < numVerts - 1; col++)
				moveColLeft(col, numVerts - 1);
		}
		numVerts--; // one less vertex
	}
	
	private void moveRowUp(int row, int length) {
		for (int col = 0; col < length; col++)
			matrix[row][col] = matrix[row + 1][col];
	}
	
	private void moveColLeft(int col, int length) {
		for (int row = 0; row < length; row++)
			matrix[row][col] = matrix[row][col + 1];
	}
	
	
	public static void main(String[] args) {
		
		String[] strings = {"second", "first", "third"};
		
		TopologicalSorter sorter =
			new TopologicalSorter(strings);
		
		sorter.addDependency(1,0);
		sorter.addDependency(0,2);
		
		try {
			Object[] oo = sorter.sort();
			
			for (Object o : oo)
				System.out.println(o);
			
		} catch (ThinklabCircularDependencyException e) {
			e.printStackTrace();
		}
	
	}

	public Collection<String> getDependentKeys(String key) {

		ArrayList<String> ret = new ArrayList<String>();
		
		// find id of object
		int id = getObjectIndexForKey(key);
		
		for (Pair<Integer, Integer> deps : dependencies) {
			if (deps.first == id) {
				ret.add(keys.get(deps.second));
			}
		}
		
		return ret;
	}

	/**
	 * Number of objects added
	 * @return
	 */
	public int size() {
		return objects.size();
	}

	public Collection<String> getDependencyKeys(String key) {
		
	ArrayList<String> ret = new ArrayList<String>();
		
		// find id of object
		int id = getObjectIndexForKey(key);
		
		for (Pair<Integer, Integer> deps : dependencies) {
			if (deps.second == id) {
				ret.add(keys.get(deps.first));
			}
		}
		
		return ret;	}

}