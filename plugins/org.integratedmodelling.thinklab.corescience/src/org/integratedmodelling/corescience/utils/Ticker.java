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
package org.integratedmodelling.corescience.utils;

import java.util.ArrayList;

/**
 * A simple object that can be incremented along a number of dimensions.  Mimicks one
 * of those clickable counters with wheels, except each wheel can have different number
 * of states. It knows what dimension indexes have been changed by the latest increment.
 * 
 * @author Ferdinando Villa
 *
 */
public class Ticker {

	/** total number of states (product of dimension sizes) */
	long states;

	/** total number of dimensions */
	int dims;

	ArrayList<Integer> sizes = new ArrayList<Integer>();
	int[] current = null;
	boolean[] changed = null;

	private long currentIndex = 0L;

	public Ticker() {
		states = 1L;
		dims = 0;
	}

	/** 
	 * add all dimensions one by one in order, before doing anything. The last 
	 * dimension added changes the fastest. 
	 */
	public void addDimension(int size) {
		
		sizes.add(size);
		current = new int[sizes.size()];
		changed = new boolean[sizes.size()];
		dims++;
		reset();
	}

	/** reset ticker to 0  */
	public long reset() {
		states = 1L;
		currentIndex = 0L;

		/* create 'current' vector, set all initial states as changed
		 and calculate total number of states */
		for (int i = 0; i < dims; i++) {
			current[i] = 0;
			changed[i] = true;
			states *= sizes.get(i);
		}

		/* return it */
		return states;
	}

	/**
	 * Total number of possible states
	 * @return
	 */
	public long size() {
		return states;
	}
	
	/** the current index along the passed dimension */
	public int current(int i) {
		return current[i];
	}

	/** return the current states  */
	public int[] retrieveState() {
		return current;
	}

	/** increment, recording changes along dimensions */
	public void increment() {

		currentIndex ++;
		
		for (int i = dims - 1; i >= 0; i--) {
			if (current[i] == sizes.get(i) - 1) {
				current[i] = 0;
				changed[i] = true;
			} else {
				current[i]++;
				changed[i] = true;
				break;
			}
		}
	}

	/** 
	 * Returns whether the last increment changed the specified dimension. It
	 * resets the status to false after that, so call it only once and
	 * save the value if required more than once. 
	 */
	public boolean hasChanged(int dim) {
		boolean ret = changed[dim];
		changed[dim] = false;
		return ret;
	}

	/** prints the current values for the dimension */
	public String toString() {

		String ret = "";

		for (int i = 0; i < dims; i++)
			ret += current[i] + (i < dims - 1 ? " " : "");

		return ret;
	}
	
	public static void main(String[] args) {
		
		Ticker ticker = new Ticker();
		
		ticker.addDimension(4);
		ticker.addDimension(3);
		ticker.addDimension(2);
		
		for (int i = 0; i < ticker.size(); i++) {
			
			System.out.println(ticker.toString());
			ticker.increment();
		}
		
	}

	/**
	 * Current overall state;
	 * @return
	 */
	public long current() {
		return currentIndex ;
	}
	
	/**
	 * Index of last overall state
	 * @return
	 */
	public long last() {
		return states - 1L;
	}

	public boolean[] changedStates() {
		return changed;
	}

	/*
	 * return whethere there are more states to cover. 
	 */
	public boolean expired() {
		return !(currentIndex < (states-1));
	}

};
