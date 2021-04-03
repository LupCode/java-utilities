package com.lupcode.Utilities.streams.optional;

import java.io.IOException;
import java.io.OutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NullCipher;

/**
 * Same behavior as a {@link CipherOutputStream} but allows dynamic bypassing 
 * of the cipher so data can also be written unchanged
 * @author LupCode.com (Luca Vogels)
 * @since 2021-04-03
 */
public class OptionalCipherOutputStream extends OutputStream {

	protected OutputStream output;
	protected Cipher cipher = null;
	protected CipherOutputStream cipherOutput;
	protected boolean doCipher = false;
	
	/**
	 * Creates a new {@link OptionalCipherOutputStream} instance that does not
	 * cipher data until it gets enabled
	 * @param output Stream that should be written to
	 */
	public OptionalCipherOutputStream(OutputStream output) {
		if(output == null) throw new NullPointerException("OutputStream cannot be null");
		this.output = output;
		this.cipherOutput = new CipherOutputStream(output, new NullCipher());
	}
	
	/**
	 * Creates a new {@link OptionalCipherOutputStream} instance that does 
	 * cipher from the beginning on if the given {@link Cipher} is not null 
	 * otherwise it won't start ciphering until it gets enabled later on
	 * @param output Stream that should be written to
	 * @param cipher Cipher that should be used for ciphering
	 */
	public OptionalCipherOutputStream(OutputStream output, Cipher cipher) {
		if(output == null) throw new NullPointerException("OutputStream cannot be null");
		this.output = output;
		this.doCipher = cipher != null;
		setCipher(cipher);
	}
	
	/**
	 * Creates a new {@link OptionalCipherOutputStream} instance
	 * @param output Stream that should be written to
	 * @param cipher Cipher that should be used for ciphering
	 * @param doCipher If true then cipher will immediately take place otherwise it can be enabled later on
	 */
	public OptionalCipherOutputStream(OutputStream output, Cipher cipher, boolean doCipher) {
		if(output == null) throw new NullPointerException("OutputStream cannot be null");
		this.output = output;
		setCipher(cipher, doCipher);
	}
	
	/**
	 * True if cipher is enabled and therefore the written data gets ciphered
	 * @return True if ciphering is enabled
	 */
	public boolean isCipherEnabled() {
		return doCipher;
	}
	
	/**
	 * Sets if data should be ciphered otherwise gets bypassed without ciphering
	 * @param doCipher True if cipher should be used
	 */
	public void doCipher(boolean doCipher) {
		this.doCipher = doCipher;
	}
	
	/**
	 * Returns the used cipher or null if no cipher set
	 * @return Cipher used for ciphering or null if not set
	 */
	public Cipher getCipher() {
		return cipher;
	}
	
	/**
	 * Sets the cipher that should be used for ciphering if
	 * null is provided the {@link NullCipher} will be used. 
	 * Does not change state if cipher should actually be applied or not: 
	 * For that use {@link OptionalCipherOutputStream#setCipher(Cipher, boolean)} instead
	 * @param cipher Cipher that should be used
	 */
	public synchronized void setCipher(Cipher cipher) {
		this.cipher = cipher;
		this.cipherOutput = new CipherOutputStream(output, cipher!=null ? cipher : new NullCipher());
	}
	
	/**
	 * Sets the cipher that should be used for ciphering
	 * @param cipher Cipher that should be used
	 * @param doCipher If cipher should actually be applied
	 */
	public void setCipher(Cipher cipher, boolean doCipher) {
		setCipher(cipher);
		this.doCipher = doCipher;
	}

	@Override
	public void write(int b) throws IOException {
		if(doCipher) cipherOutput.write(b); else output.write(b);
	}
	
	@Override
	public void write(byte[] b) throws IOException {
		if(doCipher) cipherOutput.write(b); else output.write(b);
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if(doCipher) cipherOutput.write(b, off, len); else output.write(b, off, len);
	}
	
	@Override
	public void flush() throws IOException {
		cipherOutput.flush();
		try { output.flush(); } catch (Exception ex) {}
	}
	
	@Override
	public void close() throws IOException {
		cipherOutput.close();
		try { output.close(); } catch (Exception ex) {}
	}
}
