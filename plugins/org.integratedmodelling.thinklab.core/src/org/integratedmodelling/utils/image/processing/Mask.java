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
package org.integratedmodelling.utils.image.processing ;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Binary mask of a region/object<br>
 * It is represented internally in a set of polygons as follows:<br>
 * 1) Outmost polygons are boundaries of the region<br>
 * 2) Polygons contained by boundary polygons are holes<br>
 * 3) Polygons contained by holes are boundaries <br>
 * 4) Intersected polygons are treated the same as separated ones (avoid such situations if possible as holes in intersected part are not well handled)
 * @author <A href=http://www.ctr.columbia.edu/~dzhong>Di Zhong (Columbia University)</a>
 */
public class Mask implements Serializable{

  /**
   * Minimum perimeter when extract polygons from binary mask<br>
   * If a region/hole is smaller than MIN_LINE, it will be ignored/filled<br>
   * Default is 8
   */
  public static int MIN_LINE=8 ;

  /**
   * Minimum deviation in polygon fitting
   * Default is 1.0
   */
  public static float MIN_ACCURACY=(float)0.5 ;

  /**
   * Minimum length of an edge in polygon fitting
   * Default is 3
   */
  public static int MIN_SEGMENT=3 ;

  private int ulx, uly, lrx, lry ;
  private Vector polygons=new Vector() ;
  private Vector holes=new Vector() ;

  /**
   * Create image mask from a rectangle
   * @param rect input rectangle
   */
  public Mask(Rectangle rect){
    ulx=rect.x ; uly=rect.y ;
    lrx=rect.x+rect.width-1 ; lry=rect.y+rect.height-1 ;
    XPolygon poly=new XPolygon() ;
    poly.addPoint(ulx, uly) ;
    poly.addPoint(lrx, uly) ;
    poly.addPoint(lrx, lry) ;
    poly.addPoint(ulx, lry) ;
    polygons.addElement(poly) ;
  }

  /**
   * Create image mask from a polygon
   * @param poly input polygon
   */
  public Mask(XPolygon poly){
    Rectangle rect=poly.getBBox() ;
    ulx=rect.x ; uly=rect.y ;
    lrx=rect.x+rect.width-1 ; lry=rect.y+rect.height-1 ;
    polygons.addElement(poly) ;
  }

  /**
   * Create imagemask from a array of polygons
   * @param poly input polygon
   */
  public Mask(XPolygon[] poly){
    setPolygons(poly) ;
  }

  /**
   * Create mask from a array of polygons
   * @param poly input polygon
   */
  public Mask(Vector polys){
    setPolygons(polys) ;
  }

  /**
   * Create mask from a boolean array. Note only the first(scan order) region border will be found.  Holes are ignored.
   * @param mask binary mask
   * @param ul uppper-left corner (null for 0,0)
   */
  public Mask(boolean[][] mask, Point ul) {
    Vector tmp=fitpolys(traceInnerBorder(mask), MIN_ACCURACY, MIN_SEGMENT) ;

    if(ul!=null)
    for(int i=0; i<tmp.size(); i++) {
      XPolygon e=(XPolygon)tmp.elementAt(i) ;
      e.translate(ul.x, ul.y) ;
    }
    setPolygons( tmp ) ;
  }

  /**
   * Create mask from a float array.  Pixels with value larger than
   * threshold are masked
   * @param a 2 dimensional float array
   * @param th threshold
   * @param ul uppper-left corner (null for 0,0)
   */
  public Mask(float[][] a, double th, Point ul) {
    boolean[][] mask=new boolean[a.length][a[0].length] ;

    for(int i=0, ii=0; i<a.length; i++)
    for(int j=0; j<a[0].length; j++, ii++)
      if(a[i][j]>=th) mask[i][j]=true ;

    Vector tmp=fitpolys(traceInnerBorder(mask),MIN_ACCURACY, MIN_SEGMENT) ;

    if(ul!=null)
    for(int i=0; i<tmp.size(); i++) {
      XPolygon e=(XPolygon)tmp.elementAt(i) ;
      e.translate(ul.x, ul.y) ;
    }
    setPolygons(tmp) ;
  }

  private final void setPolygons(Vector polys) {
    XPolygon[] parr=new XPolygon[polys.size()] ;
    polys.copyInto(parr) ;
    setPolygons(parr) ;
  }

  private final void setPolygons(XPolygon[] polys) {
    polygons.removeAllElements() ;
    holes.removeAllElements() ;
		if(polys.length==0)  return ;

    ulx=999999 ; uly=999999 ;
    lrx=0 ; lry=0 ;
    boolean [][] tag=new boolean[polys.length][polys.length] ;

    for(int i=0; i<polys.length; i++)
    for(int j=0; j<i; j++){
      if(i!=j) {
        tag[i][j]=polys[j].contains(polys[i]) ;
        tag[j][i]=polys[i].contains(polys[j]) ;
      }
    }

    for(int i=0; i<polys.length; i++){
      XPolygon e=polys[i] ;
      boolean contained=false ;
      for(int j=0; j<polys.length; j++){
        if(tag[i][j])
          contained=!contained ;
      }
      if(contained) 
        holes.addElement(e) ;
      else
        polygons.addElement(e) ;

      Rectangle rect=e.getBBox() ;
      if(ulx>rect.x) ulx=rect.x ;
      if(uly>rect.y) uly=rect.y ;
      if(lrx<rect.x+rect.width-1) lrx=rect.x+rect.width-1 ;
      if(lry<rect.y+rect.height-1) lry=rect.y+rect.height-1 ;
    }
  }

	/**
	 * Update internal data of the mask if returned polygons are modified.
	 *
   * @see getAllBoundary
   * @see getInBoundary
   * @see getOutBoundary
   * @see getUL
   * @see getBoundingBox
   * @see insideBoundingBox
	 */
  public void update(){
    setPolygons(getAllBoundary()) ;
	}

	/**
	 * Remove a polygon from the mask; can be used to remove holes
   * @param p the polygon to be removed
	 */
  public boolean remove(Polygon p){
		boolean tag=false ;
    tag=polygons.removeElement(p) ;
    if(!tag)
			tag=holes.removeElement(p) ;

		if(tag) {
			if(polygons.size()==0) {
				ulx=0 ; uly=0 ;
				lrx=0 ; lry=0 ;
				if(holes.size()!=0) holes.removeAllElements() ;
			}else {

				ulx=999999 ; uly=999999 ;
				lrx=0 ; lry=0 ;

				for(int i=0; i<polygons.size(); i++){
					XPolygon e=(XPolygon)(polygons.elementAt(i)) ;

					Rectangle rect=e.getBBox() ;
					if(ulx>rect.x) ulx=rect.x ;
					if(uly>rect.y) uly=rect.y ;
					if(lrx<rect.x+rect.width-1) lrx=rect.x+rect.width-1 ;
					if(lry<rect.y+rect.height-1) lry=rect.y+rect.height-1 ;
				}
			}
		}
		return tag;
	}

	/**
	 * Add a array of polygons to the mask
   * @param p the array of polygons to be added
	 */
  public void add(XPolygon[] p){
    XPolygon[] old=getAllBoundary() ;
		XPolygon[] tmp=new XPolygon[old.length+p.length] ;
		System.arraycopy(old, 0, tmp, 0, old.length) ;
		System.arraycopy(p, 0, tmp, old.length, p.length) ;
    setPolygons(tmp) ;
	}

  /**
   * Get binary mask as a 2-D array of boolean<br>
   * Return null if a mask contains no polygons
   * Note size of output arry is the size of mask's bounding box(height&width)
   * @see getUL
   */
  public boolean[][] getMask(){
    if(polygons.size()==0) {
      System.err.println("-- WARNING -- : null mask!") ;
      return null ;
    }

    int width=lrx-ulx+1 ;
    int height=lry-uly+1 ;

    if(width<=0 || height<=0) {
      System.err.println("-- WARNING  --: invalid mask at "+ulx+","+uly+" "+lrx+","+lry) ;
      return null ;
    }

    boolean[][] mask=new boolean[height][width] ;
    for(int i=0; i<height; i++)
    for(int j=0; j<width; j++){
			int x=j+ulx ;
			int y=i+uly ;
      for(Enumeration e=polygons.elements(); e.hasMoreElements();)
        if(((XPolygon)(e.nextElement())).contains(x, y))
          mask[i][j]=!mask[i][j] ;
      for(Enumeration e=holes.elements(); e.hasMoreElements();)
        if(((XPolygon)(e.nextElement())).contains(x, y))
          mask[i][j]=!mask[i][j] ;
    }
    return mask ;
  }

  /**
   * save the mask to a PGM file: 255-masked pixels; 0-unmasked pixels
	 * @param fname output file name
   */
  public void savePGM(String fname){
    int width=lrx-ulx+1 ;
    int height=lry-uly+1 ;
    int s=width*height ;
    byte[] mask=new byte[s] ;
    for(int i=0, ii=0; i<height; i++)
    for(int j=0; j<width; j++, ii++){
      for(Enumeration e=polygons.elements(); e.hasMoreElements();)
        if(((XPolygon)(e.nextElement())).contains(j+ulx, i+uly))
          mask[ii]=(mask[ii]==(byte)0)?(byte)255:(byte)0 ;
      for(Enumeration e=holes.elements(); e.hasMoreElements();)
        if(((XPolygon)(e.nextElement())).contains(j+ulx, i+uly))
          mask[ii]=(mask[ii]==(byte)0)?(byte)255:(byte)0 ;
    }
    try {
    FileOutputStream out=new FileOutputStream(fname) ;
    ImageIO.savePGM(out, mask, width, height);
    out.close() ;
    }catch(IOException e) {
      System.err.println(e.getMessage()) ;
    }
  }

  /**
   * save a given binary array to a PGM file: 255-masked pixels; 0-unmasked pixels
	 * @param fname output file name
	 * @param m the binary mask
   */
  public static void savePGM(String fname, boolean[][] m){
    int width=m[0].length ;
    int height=m.length ;
    int s=width*height ;
    byte[] mask=new byte[s] ;
    for(int i=0, ii=0; i<height; i++)
    for(int j=0; j<width; j++, ii++){
      mask[ii]=m[i][j]?(byte)255:(byte)0 ;
    }
    try {
    FileOutputStream out=new FileOutputStream(fname) ;
    ImageIO.savePGM(out, mask, width, height);
    out.close() ;
    }catch(IOException e) {
      System.err.println(e.getMessage()) ;
    }
  }

  /**
   * Save a binary array to a PGM file: 255-masked pixels; 0-unmasked pixels
	 * @param fname output file name
	 * @param m the binary mask
	 * @param ul upper-left conner of the binary mask in frame
	 * @param frame frame size
   */
  public static void savePGM(String fname, boolean[][] m, Point ul, Dimension frame){
    int width=m[0].length ;
    int height=m.length ;
    int s=frame.width*frame.height ;
    byte[] mask=new byte[s] ;
    for(int i=0; i<height; i++)
    for(int j=0; j<width; j++){
      if(i+ul.y<0 || i+ul.y>frame.height-1 ||j+ul.x<0 || j+ul.x>frame.width-1 )
        continue ;
			int ii=(i+ul.y)*frame.width+j+ul.x ;
      mask[ii]=m[i][j]?(byte)255:(byte)0 ;
    }
    try {
    FileOutputStream out=new FileOutputStream(fname) ;
    ImageIO.savePGM(out, mask, frame.width, frame.height);
    out.close() ;
    }catch(IOException e) {
      System.err.println(e.getMessage()) ;
    }
  }

  /**
   * Get outer border polygons of mask;<br>
	 * Note returned polygons are references to internal representation, thus
	 * modification of returned polygons will change the mask
	 *
   * @see update
   */
  public XPolygon[] getOutBoundary(){
    XPolygon [] p=new XPolygon[polygons.size()] ;
    polygons.copyInto(p) ;
    return p ;
  }

  /**
   * Get hole polygons of mask ;<br>
	 * Note returned polygons are references to internal representation, thus
	 * modification of returned polygons will change the mask
	 *
   * @see update
   */
  public XPolygon[] getInBoundary(){
    XPolygon [] p=new XPolygon[holes.size()] ;
    holes.copyInto(p) ;
    return p ;
  }

  /**
   * Get border&hole polygons of the mask;<br>
	 * Note returned polygons are references to internal representation, thus
	 * modification of returned polygons will change the mask
	 *
   * @see update
   */
  public XPolygon[] getAllBoundary(){
    XPolygon [] p=new XPolygon[holes.size()+polygons.size()] ;
    polygons.copyInto(p) ;
    int i=polygons.size() ;
    for(Enumeration e=holes.elements(); e.hasMoreElements();)
      p[i++]=(XPolygon)(e.nextElement()) ;
    return p ;
  }

  /**
   * Get upper-left corner of the bounding box
   * @see getMask
   */
  public Point getUL(){
    return new Point(ulx, uly) ;
  }

  /**
   * Get bounding box
   */
  public Rectangle getBoundingBox(){
    return new Rectangle(ulx, uly, lrx-ulx+1, lry-uly+1) ;
  }

  /**
   * Test if a point is inside the bounding box
   * @param x x-coordinate relative to image (0,0)
   * @param y y-coordinate relative to image (0,0)
   */
  public final boolean insideBoundingBox(int x, int y) {
    if(x<ulx || x>lrx || y<uly || y>lry) return false ;
    return true ;
  }

  /**
   * Test if a point is masked
   * @param x x-coordinate relative to image (0,0)
   * @param y y-coordinate relative to image (0,0)
	 * 
   * see Mask#xy
   */
  public final boolean imgxy(int x, int y) {
    if(x<ulx || x>lrx || y<uly || y>lry) return false ;

    boolean contained=false ;
    for(Enumeration e=polygons.elements(); e.hasMoreElements();)
      if(((XPolygon)(e.nextElement())).contains(x, y))
        contained=!contained ;
    for(Enumeration e=holes.elements(); e.hasMoreElements();)
      if(((XPolygon)(e.nextElement())).contains(x, y))
        contained=!contained ;
    return contained ;
  }

  /**
   * Test if a point is masked
   * @param x x-coordinate relative to upperleft corner of bounding box
   * @param y y-coordinate relative to upperleft corner of bounding box
	 * 
   * see Mask#imgxy
   */
  public final boolean xy(int x, int y) {
    boolean contained=false ;
    x+=ulx ;
    y+=uly ;
    for(Enumeration e=polygons.elements(); e.hasMoreElements();)
      if(((XPolygon)(e.nextElement())).contains(x, y))
        contained=!contained ;
    for(Enumeration e=holes.elements(); e.hasMoreElements();)
      if(((XPolygon)(e.nextElement())).contains(x, y))
        contained=!contained ;
    return contained ;
  }

  /**
   * empty a filled binary mask to get its boundaries
   * @param mask original binary mask
   * @param bbox bounding box
   *
   * see Mask#link
   **/
  public static final boolean[][] empty(boolean[][] mask){
    int h=mask.length ;
    int w=mask[0].length ;
    boolean[][] nmask=new boolean[h][w] ;
    for(int i=0; i<h; i++)
    for(int j=0; j<w; j++){
      if(mask[i][j]) {
        if(i==0 ||i==h-1||j==0||j==w-1) {
          nmask[i][j]=true ;
        }else if((!mask[i][j-1])||(!mask[i][j+1])||
             (!mask[i-1][j])||(!mask[i+1][j]) ){
          nmask[i][j]=true ;
        }
      }
    }
    return nmask ;
  }

  private static boolean[][] cur_mask ;

  /**
   * Scan the binary mask and link adjacent non-zero points into lists.
   * Note that the mask is modified as scanned edge points are zeroed. 
   * Lists are returned in an array of polygons
   *
   * @param mask binary mask of an edge map
   * @param minlength minimum perimeter of returned polygons
   *
   * @see Mask#empty
  **/
  public static final Vector link(boolean[][] mask,int minlength){
    Vector edges=new Vector();

    cur_mask=mask ;

    for (int r = 0; r < cur_mask.length; r++)
    for (int c = 0; c < cur_mask[0].length; c++)
      if(mask[r][c])
        track(r, c, edges, minlength);

    cur_mask=null ;
    return edges;
  }

  /**
   * Start tracking an edge from the given position. As each point is added
   * to an edge, zero its pixel in the image.  After the end of the edge
   * is reached, then reverse points in the array and start tracking from
   * the beginning in the other direction.  Finally, the new edge is added to
   * the VEdges structure.
  **/
  private static final void track(int row,int col,Vector edges,int minlength) {
    XPolygon pointlist=new XPolygon() ;

    Point p=new Point(col, row) ;

    /* Put first point in PointData array. */
    pointlist.addPoint(p.x,p.y) ;
    cur_mask[p.y][p.x]=false ;

    /* Track to end of connected points in first direction. */
    while (nextPoint(p)){
      pointlist.addPoint(p.x,p.y) ;
      cur_mask[p.y][p.x]=false ;
    }

    /* Reverse list of points by exchanging pairs of points. */
    int pcn=pointlist.npoints ;
    for(int i=0; i <pcn/2; i++) {
      Point tmp=new Point(pointlist.xpoints[i], pointlist.ypoints[i]) ;
      pointlist.xpoints[i]=pointlist.xpoints[pcn-i-1] ;
      pointlist.ypoints[i]=pointlist.ypoints[pcn-i-1] ;
      pointlist.xpoints[pcn-i-1]=tmp.x ;
      pointlist.ypoints[pcn-i-1]=tmp.y ;
    }

    /* Start tracking from original point in opposite direction. */
    p.y = row;
    p.x = col;
    while (nextPoint(p)){
      pointlist.addPoint(p.x,p.y) ;
      cur_mask[p.y][p.x]=false ;
    }

    /* Add this edge to the set of edges. */
    if (pointlist.npoints >= minlength)
      edges.addElement(pointlist) ;
  }

  /**
   * Given the location of an image point, find the best edge continuation
   * from this point to one of its neighbors.  Return the position
   * of the new point.
   **/
  private final static boolean nextPoint(Point p) {
    int i, r, c;

    /* These arrays give the row and column offsets for each of the eight
       neighbors of a point, starting with those that are 4-connected. */
    int[] roff = {1, 0, -1, 0, 1, 1, -1, -1};
    int[] coff = {0, 1, 0, -1, 1, -1, -1, 1};

    /* Check each of the neighbors for a pixel above the low threshold. */
    for (i = 0; i < 8; i++) {
      r = p.y + roff[i];
      c = p.x + coff[i];
      if (r>=0 && c>=0 && r<cur_mask.length && c<cur_mask[0].length)
      if (cur_mask[r][c]) {
        p.y += roff[i];
        p.x += coff[i];
        return true;
      }
    }
    return false;
  }

  private static int granularity ;   //Current granularity
  private static float sqaccuracy;

  /**
   *  Breaks edges into straight-line segments, essentially by recursively<br>
   *  subdividing at the point of maximum deviation from a straight line<br>
   *  joining the edgepoints. Based on a method described in D.G. Lowe,<br>
   *  `Three-dimensional object recognition from single two-dimensional<br>
   *  images', Art. Intel. 31 (1987), pp. 355-395.
   *
   * @param src an array of orginal polygons (i.e. edge lists from edge following algorithm)
   * @param accuracy minimum deviation (~ several pixels)
   * @param g minimum length of edges
   *
   *  @see Mask#link
   *  @see image.ImageProc#fitline
   */
   public static Vector fitpolys(Vector src,float accuracy,int g) {
    Vector result=new Vector();
    int max_npoints, npoints;

    sqaccuracy= accuracy * accuracy;    /* square is used in calcs */
    granularity=g ;

    /* Segment each edge: */
    for(Enumeration en=src.elements(); en.hasMoreElements();){
      XPolygon e = (XPolygon)(en.nextElement()) ;
      Vector breakpoints=new Vector() ; 
      breakpoints.addElement(new Integer(0)) ;

			if(e.npoints<=4*(g+1)){ //don't fit, use orginal vertices
				for(int i=1; i<e.npoints; i++)
					breakpoints.addElement(new Integer(i));
			}else{
				splitSegment(e, breakpoints, 0, e.npoints - 1);
				breakpoints.addElement(new Integer(e.npoints-1));
			}

      XPolygon newe=new XPolygon() ;
      for(Enumeration e1=breakpoints.elements(); e1.hasMoreElements();){
        int b0=((Integer)(e1.nextElement())).intValue() ;
        newe.addPoint(e.xpoints[b0], e.ypoints[b0]) ;
      }
      result.addElement(newe) ;
    }

    return result;
  }


  /**
   *  Recursively split an edge segment into shorter segments as long as
   *  any shorter segment has a better length/deviation ratio.
   *
   *  Returns the maximum length/deviation ratio attained for the segment.
   */
  private static final float splitSegment(XPolygon e, Vector bpoints,
    int first,int last) {

    int maxp, save_nbreakpoints = bpoints.size();
    float [] sig=new float[1] ;
    float sig1, sig2, maxsig;

    /* If the segment is too short, don't split it further: */
    if ((last - first) <= granularity) return (float)0.0;

    /* Find the point of maximum deviation: */
    maxp = maxPoint (e, first, last, sig);

    /* Create subsegments with recursive calls: */
    sig1 = splitSegment (e,bpoints,first, maxp);
    bpoints.addElement(new Integer(maxp));
    sig2 = splitSegment (e,bpoints, maxp, last);
    maxsig = Math.max(sig1, sig2);

    /* If the best of lower segments is better than the current segment
       then retain the breakpoints defining the lower segments,
       otherwise drop them: */
    if (maxsig > sig[0]) {
      return maxsig;
    } else {
      int n=bpoints.size() ;
      while(n>save_nbreakpoints){
        bpoints.removeElementAt(n-1) ;
        n=bpoints.size() ;
      }
      return sig[0];
    }
  }


  /**
   *  Return the point with maximum perpendicular distance from the line
   *  joining first and last. Also returns the square of the length/deviation
   *  ratio via *sigp.
   */
  private static int maxPoint(XPolygon e, int first, int last, float[] sigp){
    int x0, y0, dx, dy, d ;
    float dev, t, maxdev, px, py;
    int maxp, i;
    
    x0 = e.xpoints[first];
    y0 = e.ypoints[first];
    dx = e.xpoints[last] - x0;
    dy = e.ypoints[last] - y0;
    d=dx*dx+dy*dy ;
    
    /* Locate the point of maximum deviation from a straight line through
       the first and last points. */
    maxdev=(float)0.0 ;
    for (i = maxp = first + 1; i < last; i++) {
      px =(float)(e.xpoints[i] - x0);
      py =(float)(e.ypoints[i] - y0);

      /* Compute squared distance to the line: */
      t = (dx * px + dy * py) / d;
      px -= dx * t;
      py -= dy * t;
      dev = px * px + py * py;

      if (dev > maxdev) {
          maxdev = dev;
          maxp = i;
      }
    }
    
    /* The maximum deviation is assumed to be at least a couple of pixels
       in size to account for limitations on measurement accuracy. */
    if (maxdev < sqaccuracy) maxdev = sqaccuracy;

    /* Significance equals length over maximum deviation. We return its
       square, which equals length squared over maxdev value above. */
    sigp[0] = d/maxdev;
    return maxp;
  }

	/**
	 * Trace all region borders of a given image mask;<br>
	 * Image mask may have several separated regions and may also have holes;<br>
	 * Return is a list of polygons; Each polygon represents a detected border (pixelwise, no fitting)
	 */
  public static Vector traceInnerBorder(boolean [][]mask) {
		if(mask==null || mask.length<=0) {
			System.err.println("Warning: invalid mask passed to traceInnerBorder!") ;
			return null ;
		}
    int w=mask[0].length ;
    int h=mask.length ;
    int[] dx = {1, 1,  0,  -1, -1, -1, 0, 1};
    int[] dy = {0, -1, -1, -1, 0,  1,  1, 1};
    int[] dx4 = {1,  0,   -1,  0 };
    int[] dy4 = {0,  -1,  0,   1 };
    boolean[][] border=new boolean[h][w] ;
    Vector blist=new Vector() ;

    while(true) {

      int i, j=0 ;
      boolean found=false ;
      boolean hole=false ;

      for(i=0; (i<h)&&(!found); i++)
      for(j=0; j<w; j++){
        if(mask[i][j] && (
            i==0 || j==0 || i==h-1 || j==w-1 ||
            (i>0 && !mask[i-1][j]) || (i<h-1 && !mask[i+1][j]) ||
            (j>0 && !mask[i][j-1]) || (j<w-1 && !mask[i][j+1]) ) ){
          hole=false ;
          for(Enumeration e=blist.elements(); e.hasMoreElements();){
            XPolygon p=(XPolygon)(e.nextElement()) ;
            if(p.contains(j,i)) hole=!hole ;
          }
          
          if(hole) {
            int tx=j ;
            int ty=i ;
            if((j<w-1) && (!mask[i][j+1])) tx++ ;
            else if((i<h-1) && (!mask[i+1][j])) ty++ ;
            if((!mask[ty][tx]) && (!border[ty][tx])){
              found=false ;
							//Test is tx,ty is contained by at least one region
							for(Enumeration e=blist.elements(); e.hasMoreElements();){
								XPolygon p=(XPolygon)(e.nextElement()) ;
								if(p.contains(tx,ty)){
									found=true ; break ;
								}
							}
              if(found) {
								i=ty; j=tx ;
								break ;
							}
            }
          }else {
						if(!border[i][j]) {
							found=true ;
							break ;
						}
          }
        }
      }
      if(!found) break ;

      i-- ;
      XPolygon edge=new XPolygon() ;
      edge.addPoint(j,i) ;
      border[i][j]=true ;

			if(!hole) {
				int dir=7 ; //8-connectivity
				while(found) {
					int k ;
					if(dir%2==1) k=(dir+6)%8 ;
					else k=(dir+7)%8 ;
					found=false ;
					for(int kk=0; kk<8; kk++, k=(k+1)%8) {
						int x=j+dx[k] ;
						int y=i+dy[k] ;
						if(x>=0 && y>=0 && x<w  && y<h && mask[y][x]) {
							i=y ; j=x ; dir=k ; found=true ;
							break ;
						}
					}
					if(found) {
						if(edge.npoints>=2 &&
							 edge.xpoints[1]==j && edge.ypoints[1]==i &&
							 edge.xpoints[0]==edge.xpoints[edge.npoints-1] &&
							 edge.ypoints[0]==edge.ypoints[edge.npoints-1]) 
							found=false ;
						else {
							edge.addPoint(j,i) ;
							border[i][j]=true ;
						}
					}
					if(edge.npoints>50000) { //maybe some error, print debug info
						System.out.println("already have "+blist.size()) ;
						for(int ni=0; ni<blist.size(); ni++) {
							XPolygon tmpedge=(XPolygon)blist.elementAt(ni) ;
							if(mask[tmpedge.ypoints[0]][tmpedge.xpoints[0]])
								System.out.print("OutBorder: ") ;
							else
								System.out.print("Hole: ") ;
							for(int nj=0; nj<tmpedge.npoints; nj++) 
							System.out.print("("+tmpedge.xpoints[nj]+","+tmpedge.ypoints[nj]+") ") ;
							System.out.println("") ;
						}
						if(mask[edge.ypoints[0]][edge.xpoints[0]])
							System.out.print("OutBorder: ") ;
						else
							System.out.print("Hole: ") ;
						for(int nj=0; nj<500; nj++) 
							System.out.print("("+edge.xpoints[nj]+","+edge.ypoints[nj]+") ") ;
						System.out.println("") ;
						System.exit(1) ;
					}
				}
			}else {
				int dir=3 ; //4-connectivity
				while(found) {
					int k ;
					k=(dir+3)%4 ;
					found=false ;
					for(int kk=0; kk<4; kk++, k=(k+1)%4) {
						int x=j+dx4[k] ;
						int y=i+dy4[k] ;
						if(x>=0 && y>=0 && x<w  && y<h && (!mask[y][x])) {
							i=y ; j=x ; dir=k ; found=true ;
							break ;
						}
					}
					if(found) {
						if(edge.npoints>=2 &&
							 edge.xpoints[1]==j && edge.ypoints[1]==i &&
							 edge.xpoints[0]==edge.xpoints[edge.npoints-1] &&
							 edge.ypoints[0]==edge.ypoints[edge.npoints-1]) 
							found=false ;
						else {
							edge.addPoint(j,i) ;
							border[i][j]=true ;
						}
					}
					if(edge.npoints>50000) { //maybe some error, print debug info
						System.out.println("already have "+blist.size()) ;
						for(int ni=0; ni<blist.size(); ni++) {
							XPolygon tmpedge=(XPolygon)blist.elementAt(ni) ;
							if(mask[tmpedge.ypoints[0]][tmpedge.xpoints[0]])
								System.out.print("OutBorder: ") ;
							else
								System.out.print("Hole: ") ;
							for(int nj=0; nj<tmpedge.npoints; nj++) 
							System.out.print("("+tmpedge.xpoints[nj]+","+tmpedge.ypoints[nj]+") ") ;
							System.out.println("") ;
						}
						if(mask[edge.ypoints[0]][edge.xpoints[0]])
							System.out.print("OutBorder: ") ;
						else
							System.out.print("Hole: ") ;
						for(int nj=0; nj<500; nj++) 
							System.out.print("("+edge.xpoints[nj]+","+edge.ypoints[nj]+") ") ;
						System.out.println("") ;
						System.exit(1) ;
					}
				}
			}
      if(edge.npoints>2) edge.npoints-- ;
      blist.addElement(edge) ;
    }
    
    /*
    System.out.println("# of polygons (traceInnerBorder): "+blist.size()) ;
    */
    
    return blist ;
  }
}
