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
package org.integratedmodelling.utils;

public class Quantifier {

	static public final int ERROR = -1;
    static public final int ANY = 0;
    static public final int ALL = 1;
    static public final int EXACT = 2;
    static public final int RANGE = 3;
    static public final int NONE = 4;
     
    public int type = ERROR;
    public int min = 0;
    public int max = 0;
    
    public Quantifier() {
    }
    
    public Quantifier(int type) {
    	this.type = type;
    }
    
    public int getExactValue() {
    	return min;
    }
    
    public int getMinValue() {
    	return min;
    }
    
    public int getMaxValue() {
    	return max;
    }
    
    public Quantifier(String s) {

        if (s.toLowerCase().equals("any")) {
            type = ANY;            
        } else if (s.toLowerCase().equals("all")) {
            type = ALL;
        } else if (s.toLowerCase().equals("none")) {
            type = NONE;
            min = max = 0;
        } else if (s.startsWith(":")) {
            type = RANGE;
            max = Integer.parseInt(s.substring(1));
            min = -1;
            if (max == 0)
            	type = NONE;
        } else if (s.endsWith(":")) {
            type = RANGE;
            min = Integer.parseInt(s.substring(0, s.length()-1));
            max = -1;
        } else if (s.contains(":")) {
            type = RANGE;
            String[] ss = s.split(":");
            min = Integer.parseInt(ss[0]);
            max = Integer.parseInt(ss[1]);
            if (min == 0 && max == 0)
            	type = NONE;
        } else {
        	type = EXACT;
        	min = max = Integer.parseInt(s);
        	if (min == 0 && max == 0)
        		type = NONE;
        }
    }
    
    /**
     * Compares the type of a quantifier but not the actual ranges if any.
     * @return
     */
    public boolean is(int quantifierType) {
    	return type == quantifierType;
    }
    
    public static Quantifier parseQuantifier(String s) throws MalformedQuantifierException {

    	Quantifier q = null;
    	try {
    		q = new Quantifier(s);
    	} catch (Exception e) {
    		throw new MalformedQuantifierException(s);
    	}
    	if (q.type == ERROR)
    		throw new MalformedQuantifierException(s);
    	return q;
    }
    
    public static boolean isQuantifier(String s) {
        boolean ret = false;
        
        try {
            ret = parseQuantifier(s) != null;
        } catch (MalformedQuantifierException e) {
        } 
        return ret;
    }
    
    public String toString() {

        String ret = null;

        switch (type)
          {
          case ANY:
            ret = "any"; 
            break;
          case ALL:
            ret = "all"; 
            break;
          case EXACT:
            ret = (min + max) == 0 ?  "none" : Integer.toString(min);
            break;
          case RANGE:
            ret = 
            	(min < 0? "" : Integer.toString(min)) + 
            	":" + 
            	(max < 0 ? "" : Integer.toString(max));
            break;
          case NONE:
        	ret = "none";
        	break;
          }
        
        return ret;
    }

	public boolean isMaxUnbound() {
		return max < 0;
	}

	public boolean isMinUnbound() {
		return min < 0;
	}

}
