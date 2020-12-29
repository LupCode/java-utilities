package com.lupcode.Utilities.streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Clones a given {@link InputStream} by  reading it and 
 * offering the that to a certain amount of cloned {@link InputStream}s. 
 * The same that can be read by each cloned {@link InputStream} individually 
 * @author LupCode.com (Luca Vogels)
 * @since 2017-06-21
 */
public class InputStreamCloner {
	
	private final PipedInputStream[] inputs;
	private final PipedOutputStream[] outputs;
	private boolean closed = false;
	
	/**
	 * Creates an input stream cloner
	 * @param input Stream that should be cloned
	 * @param clones How many clones should be created
	 * @param buffer_size Size of buffer used to clone the input stream
	 */
	public InputStreamCloner(InputStream input, int clones, final int buffer_size){
		if(input==null){ throw new NullPointerException("InputStream cannot be null"); }
		if(clones<1){ throw new IllegalArgumentException("Clones cannot be smaller than 1"); }
		if(buffer_size<1){ throw new IllegalArgumentException("BufferSize cannot be smaller than 1"); }
		inputs = new PipedInputStream[clones];
		outputs = new PipedOutputStream[clones];
		for(int i=0; i<clones; i++){
			try {
				inputs[i] = new PipedInputStream();
				outputs[i] = new PipedOutputStream(inputs[i]);
			} catch (IOException e) {
				throw new IllegalStateException("Could not setup clones", e);
			}
		}
		
		new Thread(new Runnable() { public void run() {
			byte[] buffer = new byte[buffer_size];
			int len;
			try {
				while((len = input.read(buffer))>-1 && !closed){
					for(PipedOutputStream out : outputs){
						try { out.write(buffer, 0, len); } catch (Exception e){}
						try { out.flush(); } catch (Exception e){}
					}
				}
			} catch (IOException e) {  }
			try { input.close(); } catch (Exception ex) {}
			closed = true;
			
			for(PipedOutputStream out : outputs){
				try { out.flush(); } catch (Exception e){}
				try { out.close(); } catch (Exception e){}
			}
			
		} }).start();
	}
	
	/**
	 * Closes this cloner so it stops reading from the given {@link InputStream} 
	 * and closes it
	 */
	public void close() {
		closed = true;
	}
	
	/**
	 * Returns if {@link InputStream} is closed
	 * @return True if closed
	 */
	public boolean isClosed(){
		return closed;
	}
	
	/**
	 * Returns how many clones are available
	 * @return Amount of available clones
	 */
	public int getCloneCount(){
		return inputs.length;
	}
	
	/** 
	 * Returns one of the cloned {@link InputStream}s
	 * @param index Index of the cloned {@link InputStream} that should be returned
	 * @return Cloned {@link InputStream}
	 */
	public InputStream getClone(int index){
		if(index<0||index>=inputs.length){ throw new IndexOutOfBoundsException("Invalid index "+index+" out of (0-"+(inputs.length-1)+")"); }
		return inputs[index];
	}
	
}
