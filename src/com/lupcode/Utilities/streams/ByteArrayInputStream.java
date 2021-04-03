package com.lupcode.Utilities.streams;

import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Same behavior as a {@link java.io.ByteArrayInputStream} but can dynamically handle 
 * multiple byte arrays.
 * @author LupCode.com (Luca Vogels)
 * @since 2021-04-03
 */
public class ByteArrayInputStream extends InputStream {

	private static final boolean DEFAULT_COPY = true;
	
	protected class Node {
		byte[] buffer;
		int off=0, curr=0, endIdx;
		Node next = null;
		
		Node(byte[] buffer, int off, int len, boolean copy) {
			if(copy) {
				this.buffer = new byte[len];
				System.arraycopy(buffer, off, this.buffer, 0, len);
				this.endIdx = len;
			} else {
				this.buffer = buffer;
				this.off = off;
				this.curr = off;
				this.endIdx = off+len;
			}
		}
		
		Node cloneWithoutNext() {
			Node clone = new Node(buffer, off, endIdx-off, true);
			clone.curr = curr;
			return clone;
		}
	}
	
	protected Node head = null, tail = null, mark = null;
	int markStartOff = 0, markCounter = 0, markLimit = 0;
	protected long available = 0;
	protected boolean closed = false;
	
	/**
	 * Creates a new {@link ByteArrayInputStream} instance without data for reading
	 */
	public ByteArrayInputStream() {
		
	}
	
	/**
	 * Creates a new {@link ByteArrayInputStream} instance with given 
	 * data copied into it
	 * @param buf Array with data that should be added
	 * @throws NullPointerException if array is null
	 */
	public ByteArrayInputStream(byte[] buf) throws NullPointerException {
		add(buf);
	}
	
	/**
	 * Creates a new {@link ByteArrayInputStream} instance with the given 
	 * data copied into it
	 * @param buf Array with data that should be added
	 * @param off Offset from where bytes of input array should be read
	 * @param len Length of many bytes from input array should be read
	 * @throws NullPointerException if array is null
	 * @throws IndexOutOfBoundsException if off or len is out of bounds
	 */
	public ByteArrayInputStream(byte[] buf, int off, int len) throws NullPointerException, IndexOutOfBoundsException {
		add(buf, off, len);
	}
	
	/**
	 * Creates a new {@link ByteArrayInputStream} instance with the given 
	 * data copied into it
	 * @param buf Array with data that should be added
	 * @param off Offset from where bytes of input array should be read
	 * @param len Length of many bytes from input array should be read
	 * @param copy If bytes of array should be copied (default true) 
	 * otherwise input array gets referenced
	 * @throws NullPointerException if array is null
	 * @throws IndexOutOfBoundsException if off or len is out of bounds
	 */
	public ByteArrayInputStream(byte[] buf, int off, int len, boolean copy) throws NullPointerException, IndexOutOfBoundsException {
		add(buf, off, len, copy);
	}
	
	/**
	 * Adds a copy of the given data to this {@link ByteArrayInputStream}
	 * @param buf Array the data should be copied from
	 * @throws NullPointerException If array is null
	 */
	public void add(byte[] buf) throws NullPointerException {
		add(buf, DEFAULT_COPY);
	}
	
	/**
	 * Adds the given data to this {@link ByteArrayInputStream}
	 * @param buf Array that contains the data
	 * @param copy If bytes of array should be copied (default true) 
	 * otherwise input array gets referenced
	 * @throws NullPointerException If array is null
	 */
	public void add(byte[] buf, boolean copy) throws NullPointerException {
		if(buf == null) throw new NullPointerException("Buffer cannot be null");
		add(buf, 0, buf.length, copy);
	}
	
	/**
	 * Adds a copy of the given data to this {@link ByteArrayInputStream}
	 * @param buf Array the data should be copied from
	 * @param off Offset where to start copying data from the input array
	 * @param len How many bytes should be copied from the input array
	 * @throws NullPointerException If array is null
	 */
	public void add(byte[] buf, int off, int len) throws NullPointerException, IndexOutOfBoundsException {
		add(buf, off, len, DEFAULT_COPY);
	}
	
	/**
	 * Adds the given data to this {@link ByteArrayInputStream}
	 * @param buf Array that contains the data
	 * @param off Offset where to start adding data from the input array
	 * @param len How many bytes should be added from the input array
	 * @param copy If bytes of array should be copied (default true) 
	 * otherwise input array gets referenced
	 * @throws NullPointerException If array is null
	 */
	public synchronized void add(byte[] buf, int off, int len, boolean copy) throws NullPointerException, IndexOutOfBoundsException {
		if(buf == null) throw new NullPointerException("Buffer cannot be null");
		if(buf.length == 0 || len <= 0) return;
		if(off < 0 || off+len > buf.length) throw new IndexOutOfBoundsException();
		available += len;
		Node n = new Node(buf, off, len, copy);
		if(tail != null) {
			tail.next = n;
			tail = n;
		} else {
			head = n;
			tail = n;
			if(markLimit > 0) mark = n;
		}
	}
	
	/**
	 * Adds the given data to this {@link ByteArrayInputStream}
	 * Takes data from the byte buffer beginning from its current position 
	 * until the set limit.
	 * @param buf Buffer the data should be added from
	 * @throws NullPointerException if buffer is null
	 */
	public void add(ByteBuffer buf) throws NullPointerException {
		add(buf, DEFAULT_COPY);
	}
	
	/**
	 * Adds a copy of the given data to this {@link ByteArrayInputStream}.
	 * Takes data from the byte buffer beginning from its current position 
	 * until the set limit.
	 * @param buf Buffer the data should be copied from
	 * @param copy If bytes of {@link ByteBuffer} should be copied (default true) 
	 * otherwise array of the {@link ByteBuffer} gets referenced
	 * @throws NullPointerException if buffer is null
	 */
	public void add(ByteBuffer buf, boolean copy) throws NullPointerException {
		if(buf == null) throw new NullPointerException("Buffer cannot be null");
		add(buf.array(), buf.position(), buf.remaining(), copy);
	}
	
	/**
	 * Deletes all data stored in this {@link ByteArrayInputStream} and resets markers
	 */
	public void clear() {
		available = 0;
		head = null;
		tail = null;
		mark = null;
		markLimit = 0;
	}
	
	/**
	 * @return True if stream is closed (reads will return -1)
	 */
	public boolean isClosed() {
		return closed;
	}
	
	/**
	 * Continues at the point where it last has been closed
	 */
	public void reopen() {
		closed = false;
	}
	
	@Override
	public synchronized void reset() {
		if(mark == null) return;
		head = mark;
		head.curr = markStartOff;
		available += markCounter;
		markCounter = 0;
		markLimit = 0;
	}
	
	@Override
	public synchronized void mark(int readlimit) {
		mark = head;
		markStartOff = head!=null ? head.off : 0;
		markLimit = readlimit;
	}
	
	@Override
	public boolean markSupported() {
		return true;
	}
	
	
	@Override
	public int available() {
		return (int) available;
	}
	
	@Override
	public void close() {
		closed = true;
	}
	
	@Override
	public ByteArrayInputStream clone() {
		ByteArrayInputStream clone = new ByteArrayInputStream();
		if(head != null) {
			clone.markStartOff = markStartOff;
			clone.markCounter = markCounter;
			clone.markLimit = markLimit;
			clone.available = available;
			clone.closed = closed;
			clone.head = head.cloneWithoutNext();
			clone.tail = clone.head;
			if(head.equals(mark)) clone.mark = clone.tail;
			Node curr = head.next;
			while(curr != null) {
				clone.tail.next = curr.cloneWithoutNext();
				clone.tail = clone.tail.next;
				if(curr.equals(mark)) clone.mark = clone.tail;
				curr = curr.next;
			}
		} return clone;
	}
	
	@Override
	public long skip(long n) {
		long startN = n;
		while(n > 0 && head != null) {
			long l = Math.min(n, head.endIdx-head.curr);
			n -= l;
			head.curr += l;
			if(head.curr >= head.endIdx) {
				head = head.next;
				if(head != null) head.curr = head.off; // if read again because of marker
			}
			if(markLimit > 0) {
				markCounter += l;
				if(markCounter > markLimit) reset();
			}
		} return startN - n;
	}
	
	@Override
	public synchronized int read() {
		if(closed || head == null) return -1;
		available--;
		int b = head.buffer[head.curr++] & 255;
		if(head.curr >= head.endIdx) {
			head = head.next;
			if(head != null) head.curr = head.off; // if read again because of marker
			else tail = null;
		}
		if(markLimit > 0) {
			markCounter++;
			if(markCounter > markLimit) reset();
		}
		return b;
	}
	
	@Override
	public int read(byte[] b) {
		return read(b, 0, b.length);
	}
	
	@Override
	public synchronized int read(byte[] b, int off, int len) {
		if(off < 0 || off+len > b.length) throw new IndexOutOfBoundsException();
		if(closed) return -1;
		int startOff = off;
		while(off < len && head != null) {
			int l = Math.min(len, head.endIdx-head.curr);
			System.arraycopy(head.buffer, head.curr, b, off, l);
			available -= l;
			off += l;
			head.curr += l;
			if(head.curr >= head.endIdx) {
				head = head.next;
				if(head != null) head.curr = head.off; // if read again because of marker
				else tail = null;
			}
			if(markLimit > 0) {
				markCounter += l;
				if(markCounter > markLimit) reset();
			}
		} return off - startOff;
	}
}
