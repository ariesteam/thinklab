package org.integratedmodelling.utils.image.processing ;

/**
 * Interface allowing a class to be used with sorting algorithms
 *
 * @see PubFunc#quicksort(Sortable[])
 */
public interface Sortable {

  /**
	 * True if this object is strictly smaller than the target. 
	 * Return false if they are equal. 
	 */
  public abstract boolean lessThan(Object target) ;
}
