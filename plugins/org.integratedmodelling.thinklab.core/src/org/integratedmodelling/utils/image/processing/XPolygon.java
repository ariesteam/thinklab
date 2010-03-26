package org.integratedmodelling.utils.image.processing ;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;

/**
 * Extended polygon with following changes:<br>
 * 1. getBBox() returns bounding box 1 pixel wider&higher than getBounds()<br>
 * 2. contains() returns true for edge points<br>
 * 3. Noet that deprecated methods in Polygon can't be used 
 **/
public class XPolygon extends Polygon {

	private static final long serialVersionUID = -5047948039667958042L;
	private Rectangle bbox=null ;

  public XPolygon() {} ;
	public XPolygon(int[] x, int[] y, int n) {
	  super(x,y,n) ;
	}
	public XPolygon(Polygon p) {
	  super(p.xpoints,p.ypoints,p.npoints) ;
	}

	/**
	 * scale the polygon
	 * @param scale the scale (must be >0)
	 */
	public final void scale(double s) {
	  if(s<=0) return  ;
		for(int i=0; i<npoints; i++){
			xpoints[i]=(int)(Math.round(xpoints[i]*s)) ;
			ypoints[i]=(int)(Math.round(ypoints[i]*s)) ;
		}
	}

	public final boolean contains(int x, int y) {
	  Rectangle rect=getBBox() ;
		if(!rect.contains(x,y)) return false ;

		if(rect.width>1 && rect.height>1)
			if(super.contains(x,y)) return true ;

		int x0=xpoints[npoints-1] ;
		int y0=ypoints[npoints-1] ;
		for(int i=0; i<npoints; i++){
			int x1=xpoints[i] ;
			int y1=ypoints[i] ;
			if(x1==x0) {
				if(x0==x && ( (y>=y0 && y<=y1) || (y>=y1 && y<=y0) ) )
					return true ;
			}else if(y1==y0) {
				if(y0==y && ( (x>=x0 && x<=x1) || (x>=x1 && x<=x0) ) )
					return true ;
			}else {
				if( ((y>=y0 && y<=y1) || (y>=y1 && y<=y0)) && 
						((x>=x0 && x<=x1) || (x>=x1 && x<=x0)) ) {
					int a=x1-x0 ;
					int b=y1-y0 ;
					if(a*y-b*x==a*y0-b*x0) return true ;
				}
			}
			x0=x1 ; y0=y1 ;
		}
	
		return false ;
	}

	public final boolean contains(Point p) {
	  return contains(p.x, p.y) ;
	}

	/**
	* Test if contains the given poly<br>
	* Note the function returns false when the given polygon has common vertices with the first one<br>
	* @param f the second polygon
	*/
	public final boolean contains(XPolygon f) {
		for(int i=0; i<f.npoints; i++) {
			if(!contains(f.xpoints[i], f.ypoints[i])) return false ;
		}
		for(int i=0; i<npoints; i++) {
			if(f.contains(xpoints[i], ypoints[i])) return false ;
		}
		return true ;
	}

	public final Rectangle getBBox() {
		if(bbox==null)
		if(npoints<=0){
			bounds=bbox=new Rectangle() ;
		}else {
			int ulx=999999, uly=999999 ;
			int lrx=-1, lry=-1 ;
			for(int i=0; i<npoints; i++) {
				if(ulx>xpoints[i]) ulx=xpoints[i] ;
				if(uly>ypoints[i]) uly=ypoints[i] ;
				if(lrx<xpoints[i]) lrx=xpoints[i] ;
				if(lry<ypoints[i]) lry=ypoints[i] ;
			}
			bbox=new Rectangle(ulx, uly, lrx-ulx+1, lry-uly+1) ;
			bounds=bbox ;
		}
		return bbox ;
	}

	public final void addPoint(int x, int y) {
		bbox=null ;
		super.addPoint(x,y) ;
	}

	public final void setPoint(int i, int x, int y) {
		xpoints[i]=x ;
		ypoints[i]=y ;
		bbox=null ;
	}

	public final void translate(int x, int y) {
		bbox=null ;
		super.translate(x,y) ;
	}
}
