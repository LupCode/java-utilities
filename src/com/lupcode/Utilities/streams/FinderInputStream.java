package com.lupcode.Utilities.streams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * {@link InputStream} that can find data in stream
 * @author LupCode.com (Luca Vogels)
 * @since 2021-01-03
 */
public class FinderInputStream extends InputStream {
	
	public class FinderResult {
		private String needle;
		private byte[] data;
		FinderResult(String needle, byte[] data) {
			this.needle = needle;
			this.data = data;
		}
		public String getNeedle() {
			return needle;
		}
		public byte[] getData() {
			return data;
		}
		public String getDataAsString() {
			return new String(data, StandardCharsets.UTF_8);
		}
		@Override
		public String toString() {
			return new StringBuilder(getClass().getSimpleName()).append("{needle='").
					append(needle).append("'; data(").append(data!=null?data.length:0).
					append(")=").append(data!=null?"'"+getDataAsString()+"'":null).append("}").
					toString();
		}
	}
	
	
	protected UTF8CharInputStream input;
	
	public FinderInputStream(UTF8CharInputStream input) {
		if(input==null)
			throw new NullPointerException("InputStream cannot be null");
		this.input = input;
	}
	
	public FinderInputStream(byte[] input) {
		this(new UTF8CharInputStream(new ByteArrayInputStream(input)));
	}
	
	public FinderInputStream(String input) {
		this(new UTF8CharInputStream(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))));
	}
	
	/**
	 * Skips until the given needle is found and skipped
	 * @param needle that should be found
	 * @return needle that was found or null if end of stream
	 * @throws IOException if {@link InputStream} closed before finding
	 */
	public String skipUntil(boolean ignoreCase, String... needle) throws IOException {
		return readUntil(needle, false, ignoreCase).needle;
	}
	
	/**
	 * Skips until the given needle is found and skipped
	 * @param needles that should be found
	 * @return needle that was found or null if end of stream
	 * @throws IOException if {@link InputStream} closed before finding
	 */
	public String skipUntil(boolean ignoreCase, Collection<String> needles) throws IOException {
		return readUntil(needles, false, ignoreCase).needle;
	}
	
	/**
	 * Reads until the given needle is found and returns the read data without containing the needle
	 * @param needle that should be found
	 * @return read data without containing the needle at the end (needle will be null if end of stream)
	 * @throws IOException if {@link InputStream} closed before finding
	 */
	public FinderResult readUntil(boolean ignoreCase, String... needle) throws IOException {
		return readUntil(needle, true, ignoreCase);
	}
	
	/**
	 * Reads until the given needle is found and returns the read data without containing the needle
	 * @param needles that should be found
	 * @return read data without containing the needle at the end (needle will be null if end of stream)
	 * @throws IOException if {@link InputStream} closed before finding
	 */
	public FinderResult readUntil(boolean ignoreCase, Collection<String> needles) throws IOException {
		return readUntil(needles, true, ignoreCase);
	}
	
	protected FinderResult readUntil(Collection<String> needle, boolean doRead, boolean ignoreCase) throws IOException {
		return readUntil(needle.toArray(new String[0]), doRead, ignoreCase);
	}
	protected FinderResult readUntil(String[] needle, boolean doRead, boolean ignoreCase) throws IOException {
		if(needle==null || needle.length==0)
			return new FinderResult(null, null);
		String ndl;
		HashMap<String, String> originalNeedles = new HashMap<String, String>();
		HashMap<String, ArrayList<Integer>> indexes = new HashMap<String, ArrayList<Integer>>();
		for(int i=0; i<needle.length; i++) {
			ndl = new String((ignoreCase?needle[i].toLowerCase():needle[i]).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
			originalNeedles.put(ndl, needle[i]);
			indexes.put(ndl, new ArrayList<>());
		}
		ndl = null;
		ArrayList<Integer> index;
		int idx = 0;
		ByteArrayOutputStream output = new ByteArrayOutputStream(0);
		boolean found = false;
		while(true) {
			String c = input.readChar();
			if(c==null) {
				idx = 0;
				ndl = null;
				break;
			}
			if(doRead)
				output.write(c.getBytes(StandardCharsets.UTF_8));
			if(ignoreCase)
				c = c.toLowerCase();
			for(Entry<String, ArrayList<Integer>> entry : indexes.entrySet()) {
				ndl = entry.getKey();
				index = entry.getValue();
				for(int i=0; i<index.size(); i++) {
					idx = index.get(i);
					if(c.equals(ndl.charAt(idx)+"")) { // TODO charAt doesn't return unicode char -> custom function needed
						idx++;
						if(idx == ndl.length()) {
							found = true;
							break;
						}
						index.set(i, idx);
					} else {
						index.remove(i--);
					}
					
				}
				if(found)
					break;
				if(c.equals(ndl.charAt(0)+"")) { // TODO charAt doesn't return unicode char -> custom function needed
					if(ndl.length()==1) {
						idx = 1;
						found = true;
						break;
					}
					entry.getValue().add(1);
				}
				if(found)
					break;
			}
			if(found)
				break;
		}
		if(!doRead)
			return new FinderResult(ndl!=null ? originalNeedles.get(ndl) : null, null);
		byte[] data = output.toByteArray();
		idx = Math.max(0, data.length - idx);
		byte[] res = new byte[idx];
		System.arraycopy(data, 0, res, 0, idx);
		return new FinderResult(ndl!=null ? originalNeedles.get(ndl) : null, res);
	}
	
	@Override
	public int available() throws IOException {
		return input.available();
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
		return input.read();
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return input.read(b);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return input.read(b, off, len);
	}

	@Override
	public synchronized void reset() throws IOException {
		input.reset();
	}
	
	@Override
	public long skip(long n) throws IOException {
		return input.skip(n);
	}
}
