package com.lupcode.Utilities.color;

public class XYB extends XY {
	
	protected double brightness=0;
	
	public XYB() {
		super();
	}
	public XYB(double x, double y) {
		super(x, y);
	}
	public XYB(double x, double y, double brightness) {
		super(x, y);
		setBrightness(brightness);
	}
	public XYB(String str) {
		parse(str);
	}
	
	@Override
	public XYB set(double x, double y) {
		super.set(x, y);
		return this;
	}
	
	public XYB set(double x, double y, double brightness) {
		set(x, y);
		setBrightness(brightness);
		return this;
	}
	
	@Override
	public XYB setX(double x) {
		super.setX(x);
		return this;
	}
	
	@Override
	public XYB setY(double y) {
		super.setY(y);
		return this;
	}
	
	public double getBrightness() {
		return brightness;
	}
	
	public XYB setBrightness(double brightness) {
		this.brightness = Math.max(0, Math.min(brightness, 1));
		return this;
	}
	
	public XY getXY() {
		return new XY(x, y);
	}
	
	public XYB parse(String str) {
		if(str == null || str.isEmpty()) throw new NullPointerException("String cannot be null nor blank");
		str = str.trim();
		if(str.startsWith("[")) str = str.substring(1);
		if(str.endsWith("]")) str = str.substring(0, str.length()-1);
		String[] args = str.split(",");
		if(args.length < 2) throw new IllegalArgumentException("String needs to contain two numbers");
		set(Double.parseDouble(args[0]), Double.parseDouble(args[1]));
		return setBrightness(args.length >= 3 ? Double.parseDouble(args[2]) : 0);
	}
	
	@Override
	protected XYB clone() {
		return new XYB(x, y, brightness);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof XY)) return false;
		if(obj instanceof XYB && ((XYB) obj).brightness != brightness) return false;
		XY o = (XY)obj;
		return o.x == x && o.y == y;
	}

	@Override
	public String toString() {
		return new StringBuilder("[").append(x).append(",").append(y).append(",").append(brightness).
				append("]").toString();
	}
	
	
	public static XYB parseOrNull(String str) {
		try {
			return new XYB(str);
		} catch (Exception ex) {}
		return null;
	}
}
