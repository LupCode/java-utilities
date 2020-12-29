package com.lupcode.Utilities.streams;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.lupcode.Utilities.others.UTF8String;

/**
 * InputStream that reads UTF-8 characters
 * @author LupCode.com (Luca Vogels)
 * @since 2020-12-23
 */
public class UTF8CharInputStream extends InputStream {

	private InputStream input;
	private UTF8String readAgain = new UTF8String();
	private String buffer = null;
	
	/**
	 * Creates an UTF-8 character input stream
	 * @param input InputStream that should be read
	 */
	public UTF8CharInputStream(InputStream input){
		if(input==null){ throw new NullPointerException("InputStream cannot be null"); }
		this.input = input;
	}
	
	private String readUTF8Char() throws IOException {
		int b0 = read(); if(b0<0) return null;
		if(b0<=0b01111111)
			return new String(new byte[]{(byte)b0}, StandardCharsets.UTF_8);
		int b1 = read(); if(b1<0) return null;
		if(b0 <= 0b11011111)// two bytes
			return new String(new byte[]{ (byte)b0, (byte)b1 }, StandardCharsets.UTF_8);
		int b2 = read(); if(b2<0) return null;
		if(b0 <= 0b11101111) // three bytes
			return new String(new byte[]{ (byte)b0, (byte)b1, (byte)b2 }, StandardCharsets.UTF_8);
		int b3 = read(); if(b3<0) return null;
		return new String(new byte[]{ (byte)b0, (byte)b1, (byte)b2, (byte)b3 }, StandardCharsets.UTF_8);
	}
	
	/**
	 * Reads the next full UTF-8 character (blocks if not enough input)
	 * @return Next UTF-8 character
	 * @throws IOException if an error occurred while reading {@link InputStream}
	 */
	public synchronized String readChar() throws IOException {
		if(!readAgain.isEmpty()) return readAgain.poll();
		
		if(buffer!=null) {
			String tmp = buffer;
			buffer = null;
			return tmp;
		}
		
		String current = readUTF8Char();
		if(current!=null && current.equals("\\")) {
			buffer = readUTF8Char();
			if(buffer!=null && buffer.equals("u")) {
				buffer = null;
				StringBuffer buf = new StringBuffer();
				for(int i=0; i<4; i++) buf.append(readUTF8Char());
				current = new String(Character.toChars(Integer.parseInt(buf.toString(), 16)));
			}
		}
		return current;
	}
	
	/**
	 * Inserts UTF-8 characters from given string so they will be read 
	 * on following {@link UTF8CharInputStream#readChar()} calls. 
	 * Afterwards continues reading UTF-8 characters from {@link InputStream}
	 * @param readAgain UTF-8 characters that should be read next
	 */
	public synchronized void insertReadAgainAtBeginning(UTF8String readAgain){
		if(readAgain==null || readAgain.isEmpty()) return;
		this.readAgain.addAll(0, readAgain);
	}
	
	/**
	 * Inserts single UTF-8 characters so it will be read on a following 
	 * {@link UTF8CharInputStream#readChar()} call. 
	 * Afterwards continues reading UTF-8 characters from {@link InputStream}
	 * @param singleChar Single UTF-8 character that should be read next
	 */
	public synchronized void insertReadAgainAtBeginning(String singleChar) {
		if(singleChar==null || singleChar.length()==0) return;
		this.readAgain.add(0, singleChar);
	}
	
	@Override
	public int available() throws IOException {
		return input.available();
	}
	
	@Override
	public synchronized void mark(int arg0) {
		input.mark(arg0);
	}
	
	@Override
	public boolean markSupported() {
		return input.markSupported();
	}
	
	@Override
	public int read() throws IOException {
		return input.read();
	}
	
	@Override
	public int read(byte[] arg0) throws IOException {
		return input.read(arg0);
	}
	
	@Override
	public int read(byte[] arg0, int arg1, int arg2) throws IOException {
		return input.read(arg0, arg1, arg2);
	}
	
	@Override
	public synchronized void reset() throws IOException {
		input.reset();
	}
	
	@Override
	public long skip(long arg0) throws IOException {
		return input.skip(arg0);
	}
}
