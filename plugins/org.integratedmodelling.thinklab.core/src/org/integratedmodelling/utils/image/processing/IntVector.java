package org.integratedmodelling.utils.image.processing ;

import java.io.Serializable;

/**
 * A fast dynamic array of primitive integers
 *
 * @author <A href=http://www.ctr.columbia.edu/~dzhong>Di Zhong (Columbia University)</a>
 */
public class IntVector extends Object implements Serializable{

  /**
	 * The actual integer array where one can get values
	 */
	public int [] v=null ;

	private int inc ;
	private int size ;
	private int n ;

	/**
	 * @param i_inc step size of buffer increasement
	 * @param i_size initial buffer size
	 */
	public IntVector(int i_inc, int i_size) {
		inc=i_inc ;
		size=i_size ;
		n=0 ;
		v=new int[size] ;
	}

	public IntVector() {
		size=80; inc=200 ;
		n=0 ;
		v=new int[size] ;
	}

  /**
	 * Number of integers in the array
	 */
	public int number() {
	  return n ;
	}

  /**
	 * Add an integer to the array
	 */
	public void add(int value) {

		if(n>=size) {
			int[] tmp=new int[size+inc] ;
      System.arraycopy(v, 0, tmp, 0, size);
			size+=inc ;
			v=tmp ;
		}
	  v[n++]=value ;

	}
}
