package com.lupcode.Utilities.streams;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * {@link OutputStream} that writes to multiple given {@link OutputStream}s at once
 * @author LupCode.com (Luca Vogels)
 * @since 2017-06-21
 */
public class MultiOutputStream extends OutputStream {

	private LinkedList<OutputStream> outputs = new LinkedList<>();

	/**
	 * Creates an {@link OutputStream} that writes to multiple given ones
	 * @param output Streams this {@link OutputStream} should write to
	 */
	public MultiOutputStream(OutputStream... output){
		if(output!=null){ addOutput(output); }
	}
	
	
	/**
	 * Adds further {@link OutputStream}
	 * @param output Streams that should be added
	 * @return This instance
	 */
	public MultiOutputStream addOutput(OutputStream... output){
		if(output==null) throw new NullPointerException("OutputStream cannot be null");
		for(OutputStream o : output) {
			if(o == null) throw new NullPointerException("OutputStream cannot be null");
			if(o.equals(this)) throw new IllegalArgumentException("Cannot add this OutputStream to itself");
			outputs.add(o);
		}
		return this;
	}
	

	@Override
	public void write(int b) throws IOException {
		Iterator<OutputStream> it = outputs.iterator();
		while(it.hasNext()){
			it.next().write(b);
		}
	}
	
	@Override
	public void write(byte[] b) throws IOException {
		Iterator<OutputStream> it = outputs.iterator();
		while(it.hasNext()){
			it.next().write(b);
		}
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		Iterator<OutputStream> it = outputs.iterator();
		while(it.hasNext()){
			it.next().write(b, off, len);
		}
	}
	
	@Override
	public void close() throws IOException {
		Iterator<OutputStream> it = outputs.iterator();
		while(it.hasNext()){
			it.next().close();
		}
		super.close();
	}
	
	@Override
	public void flush() throws IOException {
		Iterator<OutputStream> it = outputs.iterator();
		while(it.hasNext()){
			it.next().flush();
		}
	}
}
