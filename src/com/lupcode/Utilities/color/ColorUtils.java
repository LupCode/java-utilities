package com.lupcode.Utilities.color;

public class ColorUtils {
	
	public static int getRGB(int red, int green, int blue) {
		return ((red&0xff) << 16) | ((green&0xff) << 8) | (blue&0xff);
	}
	
	public static int[] getRGB(int rgb) {
		return new int[] { (rgb>>16)&0xff, (rgb>>8)&0xff, rgb&0xff };
	}
	
	
	public static double convertMiredToKelvin(double mired) {
		return 1000000 / mired;
	}
	
	public static int convertMiredToRGB(double mired) {
		return convertKelvinToRGB(convertMiredToKelvin(mired));
	}
	
	
	public static double convertKelvinToMired(double kelvin) {
		return 1000000 / kelvin;
	}
	
	public static int convertKelvinToRGB(double kelvin) {
		int red = 255, green, blue = 255;
		if(kelvin <= 66) {
			double r = 329.698727446  * Math.pow(kelvin - 60, -0.1332047592);
			red = (int) Math.max(0, Math.min(r, 255));
			
			double g = 99.4708025861 * Math.log(kelvin) - 161.1195681661;
			green = (int) Math.max(0, Math.min(g, 255));
			
			if(kelvin < 66) {
				double b = 138.5177312231 * Math.log(kelvin - 10) - 305.0447927307;
				blue = (int) Math.max(0, Math.min(b, 255));
			}
		} else {
			double g = 288.1221695283 * Math.pow(kelvin - 60, -0.0755148492);
			green = (int) Math.max(0, Math.min(g, 255));
		} return getRGB(red, green, blue);
	}
	
	
	
	public static double convertCIEtoKelvin(XY xy) {
		if(xy == null) throw new NullPointerException("XY cannot be null");
		return convertCIEtoKelvin(xy.getX(), xy.getY());
	}
	public static double convertCIEtoKelvin(XYB xyb) {
		if(xyb == null) throw new NullPointerException("XYB cannot be null");
		return convertCIEtoKelvin(xyb.getX(), xyb.getY());
	}
	public static double convertCIEtoKelvin(double x, double y) {
		double n = (x - 0.3320) / (0.1858 - y);
		return ((437*n + 3601)*n + 6861)*n + 5517;
	}
	
	public static double convertCIEtoMired(XY xy) {
		if(xy == null) throw new NullPointerException("XY cannot be null");
		return convertCIEtoMired(xy.getX(), xy.getY());
	}
	public static double convertCIEtoMired(XYB xyb) {
		if(xyb == null) throw new NullPointerException("XYB cannot be null");
		return convertCIEtoMired(xyb.getX(), xyb.getY());
	}
	public static double convertCIEtoMired(double x, double y) {
		return convertKelvinToMired(convertCIEtoKelvin(x, y));
	}
	
	public static int convertCIEtoRGB(XY xy, double brightness) {
		if(xy == null) throw new NullPointerException("XY cannot be null");
		return convertCIEtoRGB(xy.getX(), xy.getY(), brightness);
	}
	public static int convertCIEtoRGB(XYB xyb) {
		if(xyb == null) throw new NullPointerException("XYB cannot be null");
		return convertCIEtoRGB(xyb.getX(), xyb.getY(), xyb.getBrightness());
	}
	public static int convertCIEtoRGB(double x, double y, double brightness) {
		double z = 1.0 - x - y;
		double X = (brightness / y) * x;
		double Z = (brightness / y) * z;
		
		// Convert to RGB using Wide RGB D65 conversion
		double r =  X * 1.656492f - brightness * 0.354851f - Z * 0.255038f;
		double g = -X * 0.707196f + brightness * 1.655397f + Z * 0.036152f;
		double b =  X * 0.051713f - brightness * 0.121364f + Z * 1.011530f;
		
		// Apply reverse gamma correction
		r = r <= 0.0031308f ? 12.92f * r : (1.0f + 0.055f) * Math.pow(r, (1.0f / 2.4f)) - 0.055f;
		g = g <= 0.0031308f ? 12.92f * g : (1.0f + 0.055f) * Math.pow(g, (1.0f / 2.4f)) - 0.055f;
		b = b <= 0.0031308f ? 12.92f * b : (1.0f + 0.055f) * Math.pow(b, (1.0f / 2.4f)) - 0.055f;
		
		return (((int)(r * 255)) << 16) | (((int)(g * 255)) << 8) | ((int)(b * 255));
	}
	
	
	
	public static double convertRGBtoKelvin(int rgb) {
		return convertCIEtoKelvin(convertRGBtoCIE(rgb));
	}
	public static double convertRGBtoKelvin(int r, int g, int b) {
		return convertCIEtoKelvin(convertRGBtoCIE(r, g, b));
	}
	
	public static double convertRGBtoMired(int rgb) {
		return convertCIEtoMired(convertRGBtoCIE(rgb));
	}
	public static double convertRGBtoMired(int r, int g, int b) {
		return convertCIEtoMired(convertRGBtoCIE(r, g, b));
	}
	
	public static XYB convertRGBtoCIE(int rgb) {
		return convertRGBtoCIE((rgb>>16)&0xff, (rgb>>8)&0xff, rgb&0xff);
	}
	public static XYB convertRGBtoCIE(int r, int g, int b) {
		double R = r/255.0, G = g/255.0, B = b/255.0;
		
		// Apply gamma correction
		R = (R > 0.04045f) ? Math.pow((R + 0.055f) / (1.0f + 0.055f), 2.4f) : (R / 12.92f);
		G = (G > 0.04045f) ? Math.pow((G + 0.055f) / (1.0f + 0.055f), 2.4f) : (G / 12.92f);
		B = (B > 0.04045f) ? Math.pow((B + 0.055f) / (1.0f + 0.055f), 2.4f) : (B / 12.92f);
		
		// Wide gamut conversion D65
		double X = R * 0.664511f + G * 0.154324f + B * 0.162028f;
		double Y = R * 0.283881f + G * 0.668433f + B * 0.047685f;
		double brightness = R * 0.000088f + G * 0.072310f + B * 0.986039f;
		
		double div = X + Y + brightness;
		double x = (div != 0.0 ? X / div : 0.0);
		double y = (div != 0.0 ? Y / div : 0.0);
		
		return new XYB(x, y, brightness);
	}
	
}
