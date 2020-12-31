package com.lupcode.Utilities.streams;

import java.io.IOException;
import java.io.InputStream;

public class NonBlockingInputStream extends InputStream {

	protected InputStream input;
	protected boolean closed = false;
	
	public NonBlockingInputStream(InputStream input){
		if(input==null){ throw new NullPointerException("InputStream cannot be null"); }
		this.input = input;
	}

	/**
	 * @return true if {@link InputStream} is closed
	 */
	public boolean isClosed(){
		return closed;
	}
	
	@Override
	public int read() throws IOException {
		return input.read();
	}
	
	/** Reads next byte
	 * @param block
	 * @return 0-255 valid data, -1 if close, 256 if no data found (non-blocking mode)
	 * @throws IOException
	 */
	public int read(boolean block) throws IOException{
		return block ? read() : readNonBlocking();
	}
	
	/**
	 * @return 0-255 valid data, -1 if close, 256 if no data found
	 */
	public int readNonBlocking() throws IOException {
		if(closed)
			return -1;
		if(input.available()<=0)
			return 256;
		return input.read();
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return input.read(b);
	}
	
	public int readNonBlocking(byte[] b, boolean block) throws IOException {
		return read(b, 0, b!=null ? b.length : 0, block);
	}
	
	public int readNonBlocking(byte[] b) throws IOException {
		return read(b, 0, b!=null ? b.length : 0, false);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return input.read(b, off, len);
	}
	
	public int read(byte[] b, int off, int len, boolean block) throws IOException {
		int data;
		for(int i=0; i<len; i++){
			data = read(block);
			if(data<0 || data>255)
				return (data<0 && i==0) ? data : i;
			b[off++] = (byte) data;
		} return len;
	}
	
	public int readNonBlocking(byte[] b, int off, int len) throws IOException {
		return read(b, off, len, false);
	}
	
	@Override
	public int available() throws IOException {
		return input.available();
	}
	
	@Override
	public void close() throws IOException {
		closed = true;
		input.close();
	}
	
	@Override
	public synchronized void mark(int readlimit) {
		input.mark(readlimit);
	}
	
	@Override
	public boolean markSupported() {
		return input.markSupported();
	}
	
	@Override
	public synchronized void reset() throws IOException {
		input.reset();
	}
	
	@Override
	public long skip(long n) throws IOException {
		return input.skip(n);
	}
	
	@Override
	public String toString() {
		int available = -1;
		try { available = available(); } catch (Exception e){}
		return new StringBuilder(getClass().getSimpleName()).append("{available=").append(available).
				append("; closed=").append(isClosed()).append("}").toString();
	}
}
