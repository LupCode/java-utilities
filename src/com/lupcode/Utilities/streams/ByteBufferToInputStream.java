package com.lupcode.Utilities.streams;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteBufferToInputStream extends InputStream {

	private class Node {
		
		private Node next = null;
		private byte[] buffer;
		
		public Node(byte[] buffer){
			this.buffer = buffer;
		}
	}
	
	
	private Node first = null, last = null;
	private int remaining = 0, offset = 0;
	private boolean closed = false;
	
	public synchronized void write(ByteBuffer buffer){
		if(buffer==null){ throw new NullPointerException("ByteBuffer cannot be null"); }
		final int rem = buffer.remaining();
		if(rem>0){
			byte[] arr = new byte[rem];
			buffer.get(arr);
			remaining += rem;
			Node n = new Node(arr);
			if(last!=null){
				last.next = n;
				last = n;
			} else {
				last = n;
				first = n;
				synchronized (first) {
					first.notify();
				}
			}
		}
	}
	
	@Override
	public synchronized int read() throws IOException {
		if(closed){ return -1; }
		if(remaining<=0){
			synchronized (first) { try { first.wait(); } catch (InterruptedException e) { e.printStackTrace(); } }
		}
		remaining--;
		final int data = first.buffer[offset++] & 255;
		if(offset>=first.buffer.length){
			first = first.next; offset = 0;
			if(first==null){ last = null; }
		} return data;
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b!=null?b.length:0);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if(b==null){ throw new NullPointerException("Array cannot be null"); }
		off = Math.min(Math.max(0, off), b.length);
		len = Math.max(0, Math.min(len, b.length-off));
		for(int i=0; i<len; i++){
			int data = read();
			if(data<0){ return i; }
			b[off++] = (byte) data;
		} return len;
	}
	
	@Override
	public boolean markSupported() {
		return false;
	}
	
	public boolean hasRemaining(){
		return remaining>0 && !closed;
	}
	
	@Override
	public int available() throws IOException {
		return remaining;
	}
	
	public boolean isClosed(){
		return closed;
	}

	@Override
	public synchronized void close() throws IOException {
		super.close();
		closed = true;
		remaining = 0;
		first = null;
		last = null;
	}
}
