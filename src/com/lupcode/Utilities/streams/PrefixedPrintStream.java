package com.lupcode.Utilities.streams;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * {@link PrintStream} that adds a prefix to each line
 * @author LupCode.com (Luca Vogels)
 * @since 2021-01-03
 */
public class PrefixedPrintStream extends PrintStream {

	private boolean newLine = true;
	protected StringBuilder prefixBuilder = new StringBuilder();
	protected SimpleDateFormat format = new SimpleDateFormat("YYYY/MM/dd HH:mm:ss");
	
	public PrefixedPrintStream(PrintStream out) {
		super(out);
	}
	
	public String getPrefix() {
		prefixBuilder.setLength(0);
		prefixBuilder.append("[").append(format.format(new Date())).append("]: ");
		return prefixBuilder.toString();
	}

	private void checkPrefix() {
		if(!newLine)
			return;
		super.print(getPrefix());
		newLine = false;
	}
	
	@Override
	public PrintStream append(char c) {
		print(c);
        return this;
	}
	
	@Override
	public PrintStream append(CharSequence csq) {
		if (csq == null)
            print("null");
        else
            print(csq.toString());
        return this;
	}
	
	@Override
	public PrintStream append(CharSequence csq, int start, int end) {
		CharSequence cs = (csq == null ? "null" : csq);
        print(cs.subSequence(start, end).toString());
        return this;
	}
	
	
	@Override
	public PrintStream format(Locale l, String format, Object... args) {
		checkPrefix();
		super.format(l, format, args);
		return this;
	}
	
	@Override
	public PrintStream format(String format, Object... args) {
		checkPrefix();
		super.format(format, args);
		return this;
	}
	
	@Override
	public void print(boolean b) {
		checkPrefix();
		super.print(b);
	}
	
	@Override
	public void print(char c) {
		checkPrefix();
		super.print(c);
		if(c=='\n')
			newLine = true;
	}
	
	@Override
	public void print(double d) {
		checkPrefix();
		super.print(d);
	}
	
	@Override
	public void print(float f) {
		checkPrefix();
		super.print(f);
	}
	
	@Override
	public void print(int i) {
		checkPrefix();
		super.print(i);
	}
	
	@Override
	public void print(long l) {
		checkPrefix();
		super.print(l);
	}
	
	@Override
	public void print(Object obj) {
		checkPrefix();
		super.print(obj);
	}
	
	@Override
	public void print(char[] s) {
		if(s==null)
			super.print(s);
		for(int i=0; i<s.length; i++)
			print(s[i]);
	}
	
	@Override
	public void print(String s) {
		print(s.toCharArray());
	}
	
	@Override
	public PrintStream printf(Locale l, String format, Object... args) {
		checkPrefix();
		super.printf(l, format, args);
		return this;
	}
	
	@Override
	public PrintStream printf(String format, Object... args) {
		checkPrefix();
		super.printf(format, args);
		return this;
	}
	
	@Override
	public void println() {
		checkPrefix();
		super.println();
		newLine = true;
	}
	
	@Override
	public void println(boolean x) {
		print(x);
		println();
	}
	
	@Override
	public void println(char x) {
		print(x);
		println();
	}
	
	@Override
	public void println(char[] x) {
		print(x);
		println();
	}
	
	@Override
	public void println(double x) {
		print(x);
		println();
	}
	
	@Override
	public void println(float x) {
		print(x);
		println();
	}
	
	@Override
	public void println(int x) {
		print(x);
		println();
	}
	
	@Override
	public void println(long x) {
		print(x);
		println();
	}
	
	@Override
	public void println(Object x) {
		print(x);
		println();
	}
	
	@Override
	public void println(String x) {
		print(x);
		println();
	}
}
