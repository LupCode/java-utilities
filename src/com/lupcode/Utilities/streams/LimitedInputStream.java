package com.lupcode.Utilities.streams;

import java.io.IOException;
import java.io.InputStream;

public class LimitedInputStream extends InputStream {

	protected InputStream input;
	protected long remaining;
	
	public LimitedInputStream(InputStream input, long limit){
		if(input==null){ throw new NullPointerException("InputStream cannot be null"); }
		this.input = input;
		this.remaining = limit;
	}
	
	@Override
	public int available() throws IOException {
		return (int) (remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : remaining);
	}
	
	@Override
	public void close() throws IOException {
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
	public int read() throws IOException {
		if(remaining <= 0)
			return -1;
		remaining--;
		return input.read();
	}
	
	@Override
	public synchronized void reset() throws IOException {
		input.reset();
	}
	
	@Override
	public long skip(long n) throws IOException {
		n = input.skip(n);
		if(n > 0){ remaining -= n; }
		return n;
	}
}