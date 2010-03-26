package org.integratedmodelling.utils.image.processing ;

import java.awt.Point;
import java.awt.Polygon;

/**
 * Some common image processing functions
 *
 * @author <A href=http://www.ctr.columbia.edu/~dzhong>Di Zhong (Columbia University)</a>
 */
public class ImageProc extends Object {

  /**
   *  FitLine from sample points
	 *  @param npoints number of sample points
	 *  @param xpoints x-coordinates
	 *  @param ypoints y-coordinates
	 *  @param eddpt0 output, one end point
	 *  @param eddpt1 output, another end point
	 *
	 *  @see image.Mask#fitpolys
   **/
  public static final void fitline(int npoints, int[] xpoints, int[] ypoints,
    Point endpt0, Point endpt1) {
    float x, y, xm, ym, x2, y2, xy, xm2, ym2, a, b, c, cost, sint, rho, t;

    /* Compute mean of coordinates and sum of their products: */
    xm = ym = x2 = y2 = xy = (float)0.0;
    for(int i = 0; i < npoints; i++) {
      xm += x = xpoints[i];
      ym += y = ypoints[i];
      x2 += x * x;
      y2 += y * y;
      xy += x * y;
    }
    xm /= npoints;
    ym /= npoints;
  
    /* Compute parameters of the fitted line: */
    xm2 = xm * xm;
    ym2 = ym * ym;
    a = (float)2.0 * (xy - npoints * xm * ym);
    b = x2 - y2 - npoints * (xm2 - ym2);
    c = (float)Math.sqrt (a * a + b * b);
    cost = (float)Math.sqrt ((b + c) / (2.0 * c));
    if (Math.abs(cost) < 0.001) {
      cost = (float)0.0;
      sint = (float)1.0;
    } else sint = a / ((float)2.0 * c * cost);
 
    /* Project the endpoints onto the line: */
    rho = -xm * sint + ym * cost;
    t = cost * xpoints[0] + sint * ypoints[0];
    endpt0.x = Math.round(t * cost - rho * sint);
    endpt0.y = Math.round(t * sint + rho * cost);
    t = cost * xpoints[npoints - 1] + sint * ypoints[npoints - 1];
    endpt1.x = Math.round(t * cost - rho * sint);
    endpt1.y = Math.round(t * sint + rho * cost);
  }

  private static final float M11=(float) 0.431 ;
  private static final float M12=(float) 0.342 ;
  private static final float M13=(float) 0.178 ;
  private static final float M21=(float) 0.222 ;
  private static final float M22=(float) 0.707 ;
  private static final float M23=(float) 0.071 ;
  private static final float M31=(float) 0.020 ;
  private static final float M32=(float) 0.130 ;
  private static final float M33=(float) 0.939 ;
  private static final float Un=(float) 0.19793943 ;  // 4*0.951/(0.951+15+1.089*3)
  private static final float Vn=(float) 0.46831096 ;  // 9.0/(0.951+15+1.089*3)

  /**
   * Convert RGB to L*u*v*
   * @param r red value(0-255)
   * @param g green value(0-255)
   * @param b blue value(0-255)
   * @param luv output L*u*v* triple (i.e. float[3])
   */
  public static final void rgb2luv(int r, int g, int b, float[] luv) {
    float x, y, z ;
    float u,v ;
    float tmp ;

    x=(M11*r+M12*g+M13*b)/(float)255.0 ;
    y=(M21*r+M22*g+M23*b)/(float)255.0 ;
    z=(M31*r+M32*g+M33*b)/(float)255.0 ;
    tmp=x+15*y+3*z ;
    if(tmp==0.0) tmp=(float)1.0 ;
    u=(float)4.0*x/tmp ;
    v=(float)9.0*y/tmp ;
    if(y>0.008856) luv[0]=116*(float)Math.pow(y,1.0/3)-16;
    else luv[0]=(float)903.3*y;
    luv[1]=13*luv[0]*(u-Un) ;
    luv[2]=13*luv[0]*(v-Vn) ;
  }

  /**
   * Convert L*u*v* to RGB
   * @param l L* value
   * @param u u* value
   * @param v v* value
   */
  public static final int[] luv2rgb(float l, float u, float v){
    int[] rgb=new int[3] ;
    float y ;
    if(l>=8) y=(float)Math.pow((l+16)/116.0,3) ;
    else y=l/(float)903.3 ;

    float u1=u/((float)13.0*l)+Un ;
    float v1=v/((float)13.0*l)+Vn ;
    float x=(float)2.25*u1*y/v1 ;
    float z=((9/v1-15)*y-x)/(float)3.0 ;
    x*=255 ; y*=255; z*=255 ;
    rgb[0]=(int)(3.0596*x-1.3927*y-0.4747*z+0.5) ;
    rgb[1]=(int)(-0.9676*x+1.8748*y+0.0417*z+0.5) ;
    rgb[2]=(int)(0.0688*x-0.2299*y+1.0693*z+0.5) ;

    return rgb ;
  }

  /**
   * Median filtering (3x3 window)
   * @param input input image (float, 3 channels)
   */
  public static final float[][][] medianfilter(float [][][] input) {
    float[][][] output=new float[input.length][input[0].length][3] ;
    float a[]=new float[9] ;

    for(int j=0;j<input.length;j++)
    for(int i=0;i<input[0].length;i++) 
		for(int k=0; k<3; k++) {          
      if(j==0 && i==0) {
         a[0] =   input[j+1][i+1][k];
         a[1] =   input[j+1][i][k];
         a[2] =   input[j+1][i+1][k];
         a[3] =   input[j][i+1][k];
         a[4] =   input[j][i][k];
         a[5] =   input[j][i+1][k];
         a[6] =   input[j+1][i+1][k];
         a[7] =   input[j+1][i][k];
         a[8] =   input[j+1][i+1][k];
       } else  if (j==0 && i==input[0].length-1) {
         a[0] =   input[j+1][i-1][k];
         a[1] =   input[j+1][i][k];
         a[2] =   input[j+1][i-1][k];
         a[3] =   input[j][i-1][k];
         a[4] =   input[j][i][k];
         a[5] =   input[j][i-1][k];
         a[6] =   input[j+1][i-1][k];
         a[7] =   input[j+1][i][k];
         a[8] =   input[j+1][i-1][k];
       } else if(j==0 && i!=0 && i!=input[0].length-1) {
         a[0] =   input[j+1][i-1][k];
         a[1] =   input[j+1][i][k];
         a[2] =   input[j+1][i+1][k];
         a[3] =   input[j][i-1][k];
         a[4] =   input[j][i][k];
         a[5] =   input[j][i+1][k];
         a[6] =   input[j+1][i-1][k];
         a[7] =   input[j+1][i][k];
         a[8] =   input[j+1][i+1][k];
       } else  if (j==input.length-1 && i==0)  {
         a[0] =   input[j-1][i+1][k];
         a[1] =   input[j-1][i][k];
         a[2] =   input[j-1][i+1][k];
         a[3] =   input[j][i+1][k];
         a[4] =   input[j][i][k];
         a[5] =   input[j][i+1][k];
         a[6] =   input[j-1][i+1][k];
         a[7] =   input[j-1][i][k];
         a[8] =   input[j-1][i+1][k];
       } else  if (j==input.length-1 && i==input[0].length-1)  {
         a[0] =   input[j-1][i-1][k];
         a[1] =   input[j-1][i][k];
         a[2] =   input[j-1][i-1][k];
         a[3] =   input[j][i-1][k];
         a[4] =   input[j][i][k];
         a[5] =   input[j][i-1][k];
         a[6] =   input[j-1][i-1][k];
         a[7] =   input[j-1][i][k];
         a[8] =   input[j-1][i-1][k];
       } else if (j==input.length-1 && i!=0 && i!=input[0].length-1)    {
         a[0] =   input[j-1][i-1][k];
         a[1] =   input[j-1][i][k];
         a[2] =   input[j-1][i+1][k];
         a[3] =   input[j][i-1][k];
         a[4] =   input[j][i][k];
         a[5] =   input[j][i+1][k];
         a[6] =   input[j-1][i-1][k];
         a[7] =   input[j-1][i][k];
         a[8] =   input[j-1][i+1][k];
       } else  if (i==0 && j!=0 && j!=input.length-1) {
         a[0] =   input[j+1][i+1][k];
         a[1] =   input[j+1][i][k];
         a[2] =   input[j+1][i+1][k];
         a[3] =   input[j][i+1][k];
         a[4] =   input[j][i][k];
         a[5] =   input[j][i+1][k];
         a[6] =   input[j-1][i+1][k];
         a[7] =   input[j-1][i][k];
         a[8] =   input[j-1][i+1][k];
       } else  if (i==input[0].length-1 && j!=0 && j!=input.length-1) {
         a[0] =   input[j+1][i-1][k];
         a[1] =   input[j+1][i][k];
         a[2] =   input[j+1][i-1][k];
         a[3] =   input[j][i-1][k];
         a[4] =   input[j][i][k];
         a[5] =   input[j][i-1][k];
         a[6] =   input[j-1][i-1][k];
         a[7] =   input[j-1][i][k];
         a[8] =   input[j-1][i-1][k];
       } else {
         a[0] =   input[j-1][i-1][k];
         a[1] =   input[j-1][i][k];
         a[2] =   input[j-1][i+1][k];
         a[3] =   input[j][i-1][k];
         a[4] =   input[j][i][k];
         a[5] =   input[j][i+1][k];
         a[6] =   input[j+1][i-1][k];
         a[7] =   input[j+1][i][k];
         a[8] =   input[j+1][i+1][k];
      }
			output[j][i][k]=PubFunc.median(a) ;
    }
		return output ;
  }

	/**
	 * Build quantization table (color palette) using kmeans clustering
	 * @param img image array (three channels)
	 * @param ncolors number of colors
	 * @param w weighting of three color channels
	 **/
	public static final float[][] palette(float[][][] img,int ncolors,float[] w) {
		int height=img.length ;
		int width=img[0].length ;
		float[][] clrs=new float[width*height][] ;

		for(int i=0, ii=0; i<height; i++)
		for(int j=0; j<width; j++, ii++) clrs[ii]=img[i][j] ;

		return PubFunc.kmeans(clrs,ncolors,w,null,2400) ; 
	}

	/**
	 * Quantize an image given a quantization table (color palette)
	 * @param img image array (three channels), it will be overwritten
	 * @param clrs color palette
	 * @param w weighting of three color channels
	 *
	 * @see ImageProc#palette
	 **/
	public static final void quantize(float[][][] img,float[][] clrs,float[] w){
		int height=img.length ;
		int width=img[0].length ;
		for(int i=0, ii=0; i<height; i++)
		for(int j=0; j<width; j++, ii++){
			float[] tmp=img[i][j] ;
			int idx=PubFunc.nearest(clrs,tmp,w) ;
			tmp[0]=clrs[idx][0] ;
			tmp[1]=clrs[idx][1] ;
			tmp[2]=clrs[idx][2] ;
		}
	}

	/**
	 * Gaussian smooth, note original image array will be over written
	 * @param img two dimensional image array (gray level)
	 * @param sigma smooth coefficient
	 **/
	public static final void gaussianSmooth(float[][] img, float sigma){
		float mult,fact;
		int op_rad, op_diam ;
		
		/* compute regions and the operator size */
		op_rad=(int) (sigma*3.0+0.5);
		op_diam=op_rad*2+1;
		int width=img[0].length ;
		int height=img.length ;

		/* create image and malloc operator */
		float[] kernel=new float[op_diam];

		/* make the kernel */
		mult=(float)(1.0/(sigma*(Math.sqrt(2.0*3.14159265)))) ;
		fact=(float)(-1.0/(2.0*sigma*sigma));
		float sum=(float)0.0;
		for(int i=0,r=-op_rad; r<=op_rad; r++, i++) {
			kernel[i] =(float)(mult*Math.exp(fact*r*r));
			sum+= kernel[i];
		}
		/* normalise to area of 1.0 */
		for(int i=0; i<op_diam; i++)
			kernel[i]/=sum;

		float[][] result=new float[height][width] ;

		/* smooth horizontally */
		for(int y=0;y<height;y++) {
			for(int x=0;x<op_rad;x++) result[y][x]=img[y][x] ;

			for(int x=op_rad;x<width-op_rad;x++) {
				sum=(float)0.0;
				for(int i=0, xx=x-op_rad; i<op_diam; xx++, i++)
					sum+=kernel[i]*img[y][xx];
				result[y][x] =sum;
			}

			for(int x=width-op_rad;x<width;x++) result[y][x]=img[y][x] ;
		}

		/* smooth vertically */
		for(int x=0;x<width;x++) {
			for(int y=0;y<op_rad;y++) img[y][x]=result[y][x] ;

			for(int y=op_rad;y<height-op_rad;y++) {
				sum=(float)0.0;
				for(int i=0, yy=y-op_rad; i<op_diam; yy++, i++)
					sum+=kernel[i]*result[yy][x];
				img[y][x] =sum;
			}

			for(int y=height-op_rad;y<height;y++) img[y][x]=result[y][x] ;
		}
	}

	/**
	 * Gaussian smooth, return smoothed image
	 * @param img two dimensional image array (gray level)
	 * @param sigma smooth coefficient
	 **/
	public static final double[][] gaussianSmooth0(double[][] img, double sigma){
		double mult,fact;
		int op_rad, op_diam ;
		
		/* compute regions and the operator size */
		op_rad=(int) (sigma*3.0+0.5);
		op_diam=op_rad*2+1;
		int width=img[0].length ;
		int height=img.length ;

		/* create image and malloc operator */
		double[] kernel=new double[op_diam];

		/* make the kernel */
		mult=(double)(1.0/(sigma*(Math.sqrt(2.0*3.14159265)))) ;
		fact=(double)(-1.0/(2.0*sigma*sigma));
		double sum=(double)0.0;
		for(int i=0,r=-op_rad; r<=op_rad; r++, i++) {
			kernel[i] =(float)(mult*Math.exp(fact*r*r));
			sum+= kernel[i];
		}
		/* normalise to area of 1.0 */
		for(int i=0; i<op_diam; i++)
			kernel[i]/=sum;

		double[][] result=new double[height][width] ;

		/* smooth horizontally */
		for(int y=0;y<height;y++) {
			for(int x=0;x<op_rad;x++) result[y][x]=img[y][x] ;

			for(int x=op_rad;x<width-op_rad;x++) {
				sum=(float)0.0;
				for(int i=0, xx=x-op_rad; i<op_diam; xx++, i++)
					sum+=kernel[i]*img[y][xx];
				result[y][x] =sum;
			}

			for(int x=width-op_rad;x<width;x++) result[y][x]=img[y][x] ;
		}

		double[][] ret=new double[height][width] ;
		/* smooth vertically */
		for(int x=0;x<width;x++) {
			for(int y=0;y<op_rad;y++) ret[y][x]=result[y][x] ;

			for(int y=op_rad;y<height-op_rad;y++) {
				sum=(double)0.0;
				for(int i=0, yy=y-op_rad; i<op_diam; yy++, i++)
					sum+=kernel[i]*result[yy][x];
				ret[y][x] =sum;
			}

			for(int y=height-op_rad;y<height;y++) ret[y][x]=result[y][x] ;
		}
		return ret ;
	}

  /**
	 * Smooth image by taking average over a window
	 * @param img 2D float array of a gray-level image
	 * @param rad radius of averaging widow
	 */
  public static final float[][] averageSmooth(float[][] img, int rad) { 
	  int h=img.length ;
	  int w=img[0].length ;
	  float result[][]=new float[h][w] ;

		for(int i=0; i<h; i++)
		for(int j=0; j<w; j++){
			int bx=j-rad, ex=j+rad ;
			int by=i-rad, ey=i+rad ;
			if(bx<0) bx=0 ;  if(ex>=w) ex=w-1 ;
			if(by<0) by=0 ;  if(ey>=h) ey=h-1 ;

			float tmp=0 ;
			int k=0 ;
			for (int y=by; y<=ey; y++)
			for (int x=bx; x<=ex; k++, x++)
				tmp+=img[y][x] ;

			result[i][j]=tmp/k ;
		}
		return result ;
	}

	/**
	 * Morphological operation: erosion.  Return result mask array (return null if there is no pixel masked after erosison)
	 * @param mask 2D binary mask
	 * @param rad radius of erosion operator
	 */
	public static boolean[][] erosion(boolean[][] mask, float rad){
		float rr=rad*rad ;
		int h=mask.length ;
		int w=mask[0].length ;
		boolean [][]m1=new boolean[h][w] ;

		int tag=0 ;

		for(int i=0; i<h; i++)
		for(int j=0; j<w; j++){
			if(mask[i][j]) {
				m1[i][j]=true ;
				int x0=j-(int)rad ;
				int y0=i-(int)rad ;
				int x1=j+(int)rad ;
				int y1=i+(int)rad ;
				for(int y=y0; (y<=y1) && m1[i][j]; y++)
				for(int x=x0; x<=x1; x++){
					int dx=x-j ;
					int dy=y-i ;
					if(dx*dx+dy*dy<=rr) {
						if(y<0 || x<0 || x>=w || y>=h || !mask[y][x]){
							m1[i][j]=false ;
							break ;
						}
					}
				}
			}
			if(m1[i][j]) tag++ ;
		}
		if(tag>0) return m1 ;
		return null ;
	}

	/**
	 * Morphological operation: dilation.  Return result mask array.
	 * Note, return mask has the same size as original one.
	 * If expanded mask is desierd, add margin before dilation
	 * @param mask 2D binary mask
	 * @param rad radius of dilation operator
	 */
	public static boolean[][] dilation(boolean[][] mask, float rad) {
		float rr=rad*rad ;
		int r=(int)rad ;
		int h=mask.length ;
		int w=mask[0].length ;
		boolean [][]m1=new boolean[h][w] ;

		for(int i=0; i<h; i++)
		for(int j=0; j<w; j++){
			if(!mask[i][j]) {
				int x0=j-r>0?j-r:0 ;
				int y0=i-r>0?i-r:0 ;
				int x1=j+r<w?j+r:w-1 ;
				int y1=i+r<h?i+r:h-1 ;
				for(int y=y0; (y<=y1) && !m1[i][j]; y++)
				for(int x=x0; x<=x1; x++){
					int dx=x-j ;
					int dy=y-i ;
					if(dx*dx+dy*dy<=rr) {
						if(mask[y][x]) {
							m1[i][j]=true ;
							break ;
						}
					}
				}
			}else
				m1[i][j]=true ;
		}
		return m1 ;
	}

	/**
	 * Compute the perimeter of a given polygon
	 * @param p polygon
	 */
	public static float perimeter(Polygon p) {
		float len=0 ;
		for(int i=0, j=p.npoints-1; i<p.npoints; i++) {
			int dx=p.xpoints[j]-p.xpoints[i] ;
			int dy=p.ypoints[j]-p.ypoints[i] ;
			len+=Math.sqrt(dx*dx+dy*dy) ;
			j=i ;
		}
		return len ;
	}

	/**
	 * Compute the centroid of a given mask
	 * @param p 2-d binary mask
	 */
	public static Point getCenter(boolean p[][]) {

		int i,j ;
		int x0=0,y0=0 ;
		int area=0 ;
		for(i=0; i<p.length; i++)
		for(j=0; j<p[0].length; j++){
			if(p[i][j]){
				area++ ;
				x0+=j ; y0+=i ;
			}
    }
		if(area>0) {
			x0=Math.round(x0/(float)area) ;
			y0=Math.round(y0/(float)area) ;
		}
		return new Point(x0,y0) ;
  }
}
