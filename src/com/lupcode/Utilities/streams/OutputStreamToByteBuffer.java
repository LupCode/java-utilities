package com.lupcode.Utilities.streams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class OutputStreamToByteBuffer extends OutputStream {

	private ByteArrayOutputStream buffer;
	private byte[] buff = null;
	private int buff_offset = 0;
	
	public OutputStreamToByteBuffer() {
		buffer = new ByteArrayOutputStream();
	}
	
	public synchronized ByteBuffer fill(ByteBuffer b){
		if(b==null){ throw new NullPointerException("ByteBuffer cannot be null"); }
		if(!b.hasRemaining()){ return b; }
		if(buff==null){
			buff = buffer.toByteArray();
			buffer.reset();
			buff_offset = 0;
			if(buff.length==0){ buff = null; return b; }
		}
		final int remaining = b.remaining(), len = buff.length-buff_offset;
		if(remaining>=len){
			b.put(buff, buff_offset, len);
			buff = null;
			return fill(b);
		} else if(remaining>0){
			b.put(buff, buff_offset, remaining);
			buff_offset += remaining;
		}
		return b;
	}
	
	@Override
	public synchronized void write(int b) throws IOException {
		buffer.write(b);
	}
	
	@Override
	public synchronized void write(byte[] b) throws IOException {
		buffer.write(b);
	}
	
	@Override
	public synchronized void write(byte[] b, int off, int len) throws IOException {
		buffer.write(b, off, len);
	}

	@Override
	public synchronized void close() throws IOException {
		buffer.close();
	}
	
	@Override
	public synchronized void flush() throws IOException {
		buffer.flush();
	}
}
