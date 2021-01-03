package com.lupcode.Utilities.color;

public class XY {
	
	protected double x=0, y=0;
	
	public XY() {

	}
	public XY(double x, double y) {
		set(x, y);
	}
	public XY(String str) {
		parse(str);
	}
	
	public XY set(double x, double y) {
		this.x = Math.max(0, Math.min(x, 1));
		this.y = Math.max(0, Math.min(y, 1));
		return this;
	}
	
	public double getX() {
		return x;
	}
	public XY setX(double x) {
		this.x = Math.max(0, Math.min(x, 1));
		return this;
	}
	
	public double getY() {
		return y;
	}
	public XY setY(double y) {
		this.y = Math.max(0, Math.min(y, 1));
		return this;
	}
	
	public XY parse(String str) {
		if(str == null || str.isEmpty()) throw new NullPointerException("String cannot be null nor blank");
		str = str.trim();
		if(str.startsWith("[")) str = str.substring(1);
		if(str.endsWith("]")) str = str.substring(0, str.length()-1);
		String[] args = str.split(",");
		if(args.length < 2) throw new IllegalArgumentException("String needs to contain two numbers");
		return set(Double.parseDouble(args[0]), Double.parseDouble(args[1]));
	}
	
	@Override
	protected XY clone() {
		return new XY(x, y);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof XY)) return false;
		XY o = (XY)obj;
		return o.x == x && o.y == y;
	}
	
	@Override
	public String toString() {
		return new StringBuilder("[").append(x).append(",").append(y).append("]").toString();
	}
	
	
	public static XY parseOrNull(String str) {
		try {
			return new XY(str);
		} catch (Exception ex) {}
		return null;
	}
}
