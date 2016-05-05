
import java.io.File;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.net.*;
import java.io.*;
import java.awt.Color;
/**
 * CRD Test subclass of ImageView, which manages the various scaled-down Bitmaps
 * and knows how to render and manipulate the image captions.
 */
public class Algorithm1 {
	private static double[][] red;
	private static double[][] blue;
	private static double threshold = 7;


	public static double runTest(URL url) {
		double result;

		imageTo2DArray(url);

		double[] correctionValuesR = calculateValues(red);
		double[] correctionValuesB = calculateValues(blue);

		correctImage(red, correctionValuesR);
		correctImage(blue, correctionValuesB);
        double[][] biBlue = constructBinary(red);
        red=null;

        biBlue = postprocess(biBlue);
        double[][] biBlueFlipped = flip(biBlue);
        biBlue=null;
        biBlueFlipped = postprocess(biBlueFlipped);
        biBlue = flip(biBlueFlipped);
        double blue2 = count(biBlue);

		double[][] biRed= constructBinary(blue);
        blue=null;

        biRed = postprocess(biRed);
        double[][] biRedFlipped = flip(biRed);
        biRedFlipped = postprocess(biRedFlipped);
        biRed = flip(biRedFlipped);
        double red2 = count(biRed);

		double[][] together2 = combine(biRed,biBlue);

        double both2= count(together2);

		//double[][] gRed = constructGray(biRed);
		//double[][] gBlue = constructGray(bi2Blue);

		// System.out.println(" Red:   "+red + "  Red2:   " + red2);
     // System.out.println("Blue: "+blue +" Blue2: "+blue2);
     // System.out.println("Both: "+both +" Both2: "+both2);
     // System.out.println("Ratio: "+red/both+" Ratio2: "+red2/both2);

        // System.out.println("  Red2:   " + red2);
        // System.out.println(" Blue2: "+blue2);
        // System.out.println(" Both2: "+both2);
        // System.out.println(" Ratio2: "+red2/both2);
        // System.out.println(" Ratio3: "+blue2/both2);
        result = red2/both2;
//		result = Math.round(result * 100);
//		result = result/100;
		return result;
	}
	/**
	 * First step to runningTest is getting 2D array of doubles from image for each channel.
	 * Green is not important. It also rescales the image.
	 */
	public static void imageTo2DArray(URL url) {
        // System.out.println("In Image to 2D Array for url:"+url);
        try{BufferedImage img = ImageIO.read(url);
		// define the array size
        int height = img.getHeight();
        int width = img.getWidth();
        int scaleX = (int)Math.floor((double)width/256);
        int scaleY = (int)Math.floor((double)height/256);
        int scale = Math.min(scaleX,scaleY);
        
        if(scale>1) {
            int newY =(int)Math.floor(height/scale)+1;
            int newX =(int)Math.floor(width/scale)+1;
            red = new double[newY][newX];
            blue = new double[newY][newX];
            for (int y = 0; y < newY && y*scale<height ; y++) {
                for (int x = 0; x < newX && x*scale<width; x++) {
                    Color p = new Color(img.getRGB(x, y));
                    red[y][x] = p.getRed();
                    blue[y][x] = p.getBlue();
                }
            }
        }
        else{
            red   = new double[height][width];
            blue  = new double[height][width];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // get the RGB value from each pixel and store it into the array
                    Color p = new Color(img.getRGB(x, y));
                    red[y][x] = p.getRed();
                    blue[y][x] = p.getBlue();
                }
            }
        }}
        catch(IOException e){
            ;
        }
	}

    public static double[][] flip(double[][] image){
        // System.out.println("In count");
        int height = image.length;
        int width = image[0].length;
        for(int y=0; y<height;y++)
            for(int x=0; x<width;x++)
                image[y][x]=1-image[y][x];
        return image;
    }

	
	/**
	 * This method takes an image and calculates and returns the a,b,c values 
	 * of an image in order to eliminate illumination gradients in an image later. 
	 */
	public static double[] calculateValues(double[][] image) {
        // System.out.println("In calculate values");
		double beta1 = 0, beta2 = 0, beta3 = 0;
		double a1 = 0;// a2=b1, a3=c1
		double b1 = 0, b2 = 0;// b3=c2
		double c1 = 0, c2 = 0, c3 = 0;
		
		double highest = 0;
		double lowest = 255;
		
		int border = 1;
		int height = image.length;
		int width = image[0].length;
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++){
				if(image[y][x]>highest)
					highest = image[y][x];
				if(image[y][x]<lowest)
					lowest = image[y][x];
			}

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < border; x++) {
				beta1 += image[y][x] * x;
				beta2 += image[y][x] * y;
				beta3 += image[y][x];

				a1 += x * x;
				b1 += y * x;
				c1 += x;

				b2 += y * y;
				c2 += y;

				c3 += 1;
				
			}
		}
		for (int y = 0; y < height; y++) {
			for (int x = width - border; x < width; x++) {
				beta1 += image[y][x] * x;
				beta2 += image[y][x] * y;
				beta3 += image[y][x];

				a1 += x * x;
				b1 += y * x;
				c1 += x;

				b2 += y * y;
				c2 += y;

				c3 += 1;
			}
		}
		for (int y = 0; y < border; y++) {
			for (int x = border; x < width - border; x++) {
				beta1 += image[y][x] * x;
				beta2 += image[y][x] * y;
				beta3 += image[y][x];

				a1 += x * x;
				b1 += y * x;
				c1 += x;

				b2 += y * y;
				c2 += y;

				c3 += 1;
			}
		}
		for (int y = height - border; y < height; y++) {
			for (int x = border; x < width - border; x++) {
				beta1 += image[y][x] * x;
				beta2 += image[y][x] * y;
				beta3 += image[y][x];

				a1 += x * x;
				b1 += y * x;
				c1 += x;

				b2 += y * y;
				c2 += y;

				c3 += 1;
			}
			
		}

		double[] inverted = invert3x3Matrix(a1, b1, c1, b1, b2, c2, c1, c2, c3);

		double aVal, bVal, cVal;
		aVal = beta1 * inverted[0] + beta2 * inverted[1] + beta3 * inverted[2];
		bVal = beta1 * inverted[3] + beta2 * inverted[4] + beta3 * inverted[5];
		cVal = beta1 * inverted[6] + beta2 * inverted[7] + beta3 * inverted[8];

		double[] values = { aVal, bVal, cVal };
		return values;
	}

	/**
	 * This is a helper method to calculate values that inverts a matrix A to A^-1
	 * In order to calculate the a,b,c, illumination gradient values.
	 */
	public static double[] invert3x3Matrix(double a, double b, double c,
			double d, double e, double f, double g, double h, double i) {
		double det = a * (e * i - f * h) - b * (d * i - f * g) + c
				* (d * h - e * g);

		double[] inverse = { e * i - f * h, c * h - b * i, b * f - c * e,
				f * g - d * i, a * i - c * g, c * d - a * f, d * h - e * g,
				b * g - a * h, a * e - b * d };

		for (int j = 0; j < inverse.length; j++)
			inverse[j] = (inverse[j] / det);

		return inverse;
	}
	
	/**
	 * This method takes a 2D array of an image and the a,b,c illumination 
	 * gradient values and corrects the image for the illumination.
	 */
	public static double[][] correctImage(double[][] original,
			double[] correctionValues) {
        // System.out.println("In correct Image");
        int height = original.length;
        int width = original[0].length;
        double min = 0;
        double max = 0;
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                original[y][x] = original[y][x] - correctionValues[0] * x
                        - correctionValues[1] * y - correctionValues[2];
                if (original[y][x] < min || (x == 0 && y == 0))
                    min = original[y][x];
                if (original[y][x] > max || (x == 0 && y == 0))
                    max = original[y][x];
            }
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                original[y][x] = (((original[y][x] - min) / (max - min)) * 255);
        return original;
	}
	
	/**\
	 * This method segments the a color channel, taking a 2D double array of image values 
	 * between 0 and 255 and returns a 2D double of binary values. 0 corresponds to pixels
	 * with color, and 1 corresponds to pixels that are background. In our case, the background
	 * will be white, and the parts of the image with color will be black.
	 */
	public static double[][] constructBinary(double[][] orig) {
        // System.out.println("In construct binary");
        int height = orig.length;
        int width = orig[0].length;
        double high = 255;
        double low = 0;

        double sumHigh = 0, sumLow = 0, sumHighC = 0, sumLowC = 0, sumHighsq = 0, sumLowsq = 0;
        double[][] newV = new double[height][width];
        for (int k = 0; k < 5; k++) {
            sumHigh = 0;
            sumLow = 0;
            sumHighC = 0;
            sumLowC = 0;
            sumHighsq = 0;
            sumLowsq = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (Math.abs(orig[y][x] - high) <= Math.abs(orig[y][x] - low)) {
                        newV[y][x] = 1;
                        sumHigh += orig[y][x];
                        sumHighC += 1;
                        sumHighsq += (orig[y][x] * orig[y][x]);
                    } else {
                        newV[y][x] = 0;
                        sumLow += orig[y][x];
                        sumLowC += 1;
                        sumLowsq += (orig[y][x] * orig[y][x]);
                    }
                }
            }
            high = sumHigh / sumHighC;
            low = sumLow / sumLowC;
            // System.out.println("High "+high+"low"+low);
        }

        double sig1 = Math.sqrt((sumHighsq / sumHighC) - (high * high));
        double sig2 = Math.sqrt((sumLowsq / sumLowC) - (low * low));

        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++) {
                if ((((
                        (orig[y][x] - high) * (orig[y][x] - high))
                        / (2 * sig1 * sig1))
                        + Math.log(sig1))
                        <=
                        (((
                                (orig[y][x] - low) * (orig[y][x] - low))
                                / (2 * sig2 * sig2))
                                + Math.log(sig2))) {
                    newV[y][x] = 1;
                } else {
                    newV[y][x] = 0;
                }
            }
        }

		return newV;
	}

    /**
     * This method takes a 2D double array (in binary) and strips it of any outliers
     */
    public static double[][] postprocess(double [][] orig){
        // System.out.println("In postprocess");
        int height = orig.length;
        int width = orig[0].length;

        int sum = 0;
        for(int y=0; y<height; y++){
            //count number of foreground elements, 0 - foreground, 1 - background
            for(int x = 0; x< width; x++){
               // System.out.println("Value:" + orig[y][x]);
                if(orig[y][x]==0)
                    sum +=1;
            }
            //if exceeds threshold, then set row to background
            if(sum<=threshold){
                for(int x = 0; x< width; x++){
                   orig[y][x]=1;
                }
            }
            sum = 0;
        }
        for(int x = 0; x< width; x++){
            //count number of foreground elements 0 - foreground, 1 - background
            for(int y=0; y<height; y++){
                if(orig[y][x]==0)
                    sum +=1;
            }
            //if exceeds threshold, then set column to background
            if(sum<=threshold){
                for(int y=0; y<height; y++){
                    orig[y][x]=1;
                }
            }
            sum = 0;
        }
        return orig;
    }

	/**
	 * This method takes two 2D double arrays (in binary) and combines the zero
	 * values of the two arrays. In our case, it combines the red and blue pixels
	 * of an image.
	 */
	public static double[][] combine(double[][] one, double[][] two){
        // System.out.println("In combine");
		int height = one.length;
		int width = one[0].length;
		double[][] newV = new double[height][width];
		for(int y=0; y<height;y++)
			for(int x=0; x<width;x++){
				if(one[y][x]<1||two[y][x]<1)
					newV[y][x]=0;
				else
					newV[y][x]=1;
			}
		return newV;
	}
	
	/**
	 * In this app, the background of an image is white (1) and the 
	 * part of dot is black. This method returns the number of pixels 
	 * that are black (part of the dot).
	 */
	public static double count(double[][] image){
        // System.out.println("In count");
		int height = image.length;
		int width = image[0].length;
		int sum=0;
		for(int y=0; y<height;y++)
			for(int x=0; x<width;x++)
				if(image[y][x]==0)
					sum+=1;
		return sum;
	}

    public static void main(String[] args){
        for(int i=0; i < args.length; i++){
            String surl = args[i];
            String name = args[i].substring(args[i].lastIndexOf("/")+1);
            name = name.replace("\'","");
            name = name.replace("\"","");
            surl = surl.replace("\'","");
            surl = surl.replace("\"","");
            try{
                URL url= new URL(surl);
                // System.out.println(url);
                System.out.print(String.format("%-10s | %.2f | %s", name,(double)runTest(url),url));
            } catch(MalformedURLException e){
                System.out.println("MalformedURLException"+surl);
                e.printStackTrace();
            }
        }
        System.exit(0);
        
    }

}