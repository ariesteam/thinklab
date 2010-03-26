package org.integratedmodelling.utils.image.processing ;

import java.lang.* ;
import java.awt.Polygon ;
import java.awt.Point ;
import java.util.Vector ;
import java.util.Random ;
import java.util.Enumeration ;

/**
 * Some common image processing, clustering, sorting functions
 *
 * @author <A href=http://www.ctr.columbia.edu/~dzhong>Di Zhong (Columbia University)</a>
 */
public class PubFunc extends Object {

	/**
	 * Maximum iterations in k-means clustering algorithm (to avoid dead loop)<br>
	 * Default value is 100
	 */
	static public int MAX_ITERATION=100 ;

	/**
	* k-means clustering algorithm (using weighted Euclidean distance)<br>
	* The function returns k central vectors
	*
	* @param data an array of feature vectors: number=data.length; dimension=data[0].length
	* @param k number of desired classes
	* @param w wighting vector (null for no-weighting)
	* @param seed initial k seed (central) vectors (null for random selection)
	* @param nsamples number of samples to be classified at each iteration (0: classify all vectors)
	*/
	public static float[][] kmeans(float[][] data, int k, float[] w, float[][] seed, int nsamples) {
		if(k<=0) return null ;

		int num=data.length ;
		int dim=data[0].length ;

		//Choose k initial values for the means
		float[][] cv=null ;
    if(seed!=null)
			cv=seed ;
		else
			cv=randomSelect(data,k) ;
		
		if (cv==null) return null ;

    boolean flag=true ;
		Vector [] cls=new Vector[k] ;
		for(int i=0; i<k; i++) cls[i]=new Vector() ;

		for(int l=1; flag&&(l<MAX_ITERATION); l++){
			flag=false ;

    	//Classify the samples by assigning them to the closest class
			for(int j=0; j<k; j++) cls[j].removeAllElements();
			if(nsamples<=0 || nsamples>num)
				for(int i=0; i<num; i++){
					int min_i=nearest(cv,data[i],w);
					if(min_i>=0)
						cls[min_i].addElement(data[i]) ;
				}
			else{
				Random r=new Random(1118) ;
				for(int i=0; i<nsamples; i++){
					int idx=Math.abs(r.nextInt())%num ;
					int min_i=nearest(cv,data[idx],w);
					if(min_i>=0)
						cls[min_i].addElement(data[idx]) ;
				}
			}

			//Recalculated the means as the average of the samples in their classes
			for(int j=0; j<k; j++){
				if(!cls[j].isEmpty()){
					float[] mean=mean(cls[j]) ;
					if(sqdist(mean,cv[j],w)>1e-10){
						flag=true;
						cv[j]=mean ;
					}
				}
			}

		}

	  return cv ;
	}

	/**
	* Compute the mean of vectors
	* The function returns mean vector
	*
	* @param data an array of feature vectors: number=data.length; dimension=data[0].length
	*/
	public static final float[] mean(float[][] data) {
		int num=data.length ;
		int dim=data[0].length ;
		float m[]=new float[dim] ;

		for(int i=0; i<num; i++)
			add(m,data[i]) ;

		for(int i=0; i<dim; i++)
			m[i]/=num ;

		return m ;
	}

	/**
	* Compute the mean of vectors
	* The function returns mean vector
	*
	* @param data an array of feature vectors: number=data.size(); dimension=data[0].length
	*/
	public static final float[] mean(Vector data) {
		int num=data.size() ;

		int dim=((float[])(data.elementAt(0))).length ;
		float m[]=new float[dim] ;

		for(Enumeration e=data.elements(); e.hasMoreElements();)
			add(m,(float[])(e.nextElement())) ;

		for(int i=0; i<dim; i++)
			m[i]/=num ;

		return m ;
	}

	/**
	* Randomly select k vectors from a data set
	* The function returns k vectors
	*
	* @param data an array of feature vectors: number=data.length; dimension=data[0].length
	* @param k number of desired vectors
	*/
	public static final float[][] randomSelect(float[][] data, int k) {
		int num=data.length ;
		int dim=data[0].length ;

		if(num==0 || k<=0 || dim==0 ) return null ;

		float seeds[][]=new float[k][dim] ;

		//Choose k values randomly
		Random r=new Random(1118) ;
		for(int i=0; i<k; i++) {
		  int idx=Math.abs(r.nextInt())%num ;
			System.arraycopy(data[idx],0,seeds[i],0,dim) ;
		}

		return seeds ;
	}

	/**
	* Return index of the nearest vector in an array to the query one
	* Using weighted Euclidean distance
	*
	* @param data an array of feature vectors: number=data.length; dimension=data[0].length
	* @param v query vector
	* @param w weighting vector (null for no-weighting)
	*/
	public static final int nearest(float[][] data, float[] v, float[] w) {
		int num=data.length ;
		int dim=data[0].length ;

		if(num==0 || dim==0 ) return -1 ;

		float d=(float)1e+20 ;
		int minid=-1 ;

		for(int i=0; i<num; i++) {
			float tmp=sqdist(v, data[i], w, d) ;
			if(tmp<d) {
				d=tmp; minid=i ;
			}
		}

		return minid ;
	}

	/**
	* Compute the squared Euclidean distance (weighted) between two float vectors
	* @param f1 the first float vector
	* @param f2 the second float vector
	* @param w the wighting vector (null for no-weighting)
	*/
	static public final float sqdist(float[] f1, float[] f2, float[] w) {
		float d=(float)0.0 ;
		if(w==null)
			for(int i=0; i<f1.length; i++) {
				float tmp=f1[i]-f2[i] ;
				d+=tmp*tmp ;
			}
		else
			for(int i=0; i<f1.length; i++) {
				float tmp=f1[i]-f2[i] ;
				d+=tmp*tmp*w[i]*w[i] ;
			}
		return d ;
	}

	/**
	* Compute the squared Euclidean distance (weighted) between two float vectors
	* @param f1 the first float vector
	* @param f2 the second float vector
	* @param w the wighting vector (null for no-weighting)
	* @param th if distance (squared) between f1 and f2 is larger or equal th, return 1e+20; otherwise return distance. (To speed up searhing, esp for high dimensio vector)
	*/
	static public final float sqdist(float[] f1, float[] f2, float[] w, float th) {
		float d=(float)0.0 ;
		if(w==null)
			for(int i=0; i<f1.length; i++) {
				float tmp=f1[i]-f2[i] ;
				d+=tmp*tmp ;
				if(d>=th) return (float)1e+20 ;
			}
		else
			for(int i=0; i<f1.length; i++) {
				float tmp=f1[i]-f2[i] ;
				d+=tmp*tmp*w[i]*w[i] ;
				if(d>=th) return (float)1e+20 ;
			}
		return d ;
	}

	/**
	* Add the second float vector to the first one
	* @param f1 the first float vector
	* @param f2 the second float vector
	*/
	static public final void add(float[] f1, float[] f2) {
		for(int i=0; i<f1.length; i++) f1[i]+=f2[i] ;
	}

	/**
	* divide  float vector f1 by v
	* @param f1 the float vector
	* @param v  the divider
	*/
	static public final void divide(float[] f1, float v) {
		for(int i=0; i<f1.length; i++) f1[i]/=v ;
	}

	/**
	 * Return the median value of a float array,  
	 * i.e. return (a.length/2)th element after sorting<br>
	 *
	 * Note: original array is modified
	 */
	public static final float median(float [] a) {
    quicksort(a) ;
		return a[a.length/2] ;
	}

	/**
	 * Quick sort algorithm for integer array<br>
   */
  static public final void quicksort(int a[]) {
    quicksort(a, 0, a.length - 1);
  }

	/**
	 * Quick sort algorithm for float array<br>
   */
  static public final void quicksort(float a[]) {
    quicksort(a, 0, a.length - 1);
  }

	/**
	 * Quick sort algorithm for Sortable object array<br>
   */
  static public final void quicksort(Sortable a[]) {
    quicksort(a, 0, a.length - 1);
	}

	/**
   * This is a generic version of C.A.R Hoare's Quick Sort 
   * algorithm.  This will handle arrays that are already
   * sorted, and arrays with duplicate keys.<BR>
   * @param a       an integer array
   * @param lo0     left boundary of array partition
   * @param hi0     right boundary of array partition
   */
	static void quicksort(int a[], int lo0, int hi0) {
		int lo = lo0;
		int hi = hi0;
		int mid;

		if ( hi0 > lo0) {

			 /* Arbitrarily establishing partition element as the midpoint */
			 mid = a[ ( lo0 + hi0 ) / 2 ];

			 // loop through the array until indices cross
			 while( lo <= hi ) {
					/* find the first element that is greater than or equal to 
					 * the partition element starting from the left Index.  */
					while( ( lo < hi0 ) && ( a[lo] < mid ) ) ++lo;

					/* find an element that is smaller than or equal to 
					 * the partition element starting from the right Index.  */
					while( ( hi > lo0 ) && ( a[hi] > mid ) ) --hi;

					// if the indexes have not crossed, swap
					if( lo <= hi ) {
						int t=a[lo] ; a[lo]=a[hi] ; a[hi]=t ;

						 ++lo; --hi;
					}
			 }

			 /* If the right index has not reached the left side of array
				* must now sort the left partition.  */
			 if( lo0 < hi )
					quicksort( a, lo0, hi );

			 /* If the left index has not reached the right side of array
				* must now sort the right partition. */
			 if( lo < hi0 )
					quicksort( a, lo, hi0 );
		}
  }

	static void quicksort(float a[], int lo0, int hi0) {
		int lo = lo0;
		int hi = hi0;
		float mid;

		if ( hi0 > lo0) {

			 /* Arbitrarily establishing partition element as the midpoint */
			 mid = a[ ( lo0 + hi0 ) / 2 ];

			 // loop through the array until indices cross
			 while( lo <= hi ) {
					/* find the first element that is greater than or equal to 
					 * the partition element starting from the left Index.  */
					while( ( lo < hi0 ) && ( a[lo] < mid ) ) ++lo;

					/* find an element that is smaller than or equal to 
					 * the partition element starting from the right Index.  */
					while( ( hi > lo0 ) && ( a[hi] > mid ) ) --hi;

					// if the indexes have not crossed, swap
					if( lo <= hi ) {
						float t=a[lo] ; a[lo]=a[hi] ; a[hi]=t ;

						 ++lo; --hi;
					}
			 }

			 /* If the right index has not reached the left side of array
				* must now sort the left partition.  */
			 if( lo0 < hi )
					quicksort( a, lo0, hi );

			 /* If the left index has not reached the right side of array
				* must now sort the right partition. */
			 if( lo < hi0 )
					quicksort( a, lo, hi0 );
		}
  }

	static void quicksort(Sortable a[], int lo0, int hi0) {
		int lo = lo0;
		int hi = hi0;
		Sortable mid;

		if ( hi0 > lo0) {

			 /* Arbitrarily establishing partition element as the midpoint */
			 mid = a[ ( lo0 + hi0 ) / 2 ];

			 // loop through the array until indices cross
			 while( lo <= hi ) {
					/* find the first element that is greater than or equal to 
					 * the partition element starting from the left Index.  */
					while( ( lo < hi0 ) && a[lo].lessThan(mid)) ++lo;

					/* find an element that is smaller than or equal to 
					 * the partition element starting from the right Index.  */
					while( ( hi > lo0 ) && mid.lessThan(a[hi]) ) --hi;

					// if the indexes have not crossed, swap
					if( lo <= hi ) {
						Sortable t=a[lo] ; a[lo]=a[hi] ; a[hi]=t ;

						 ++lo; --hi;
					}
			 }

			 /* If the right index has not reached the left side of array
				* must now sort the left partition.  */
			 if( lo0 < hi )
					quicksort( a, lo0, hi );

			 /* If the left index has not reached the right side of array
				* must now sort the right partition. */
			 if( lo < hi0 )
					quicksort( a, lo, hi0 );
		}
  }
}
