package com.lupcode.Utilities.streams.optional;

import java.io.IOException;
import java.io.InputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NullCipher;

/**
 * Same behavior as a {@link CipherInputStream} but allows dynamic bypassing
 * of the cipher so the data can also be read unchanged
 * @author LupCode.com (Luca Vogels)
 * @since 2021-04-03
 */
public class OptionalCipherInputStream extends InputStream {

	protected InputStream input;
	protected Cipher cipher = null;
	protected CipherInputStream cipherInput;
	protected boolean doCipher = false;
	
	/**
	 * Creates a new {@link OptionalCipherInputStream} instance that does not
	 * cipher data until it gets enabled
	 * @param input Stream that should be read
	 * @throws NullPointerException if {@link InputStream} is null
	 */
	public OptionalCipherInputStream(InputStream input) throws NullPointerException {
		setInputStream(input);
		this.cipherInput = new CipherInputStream(input, new NullCipher());
	}
	
	/**
	 * Creates a new {@link OptionalCipherInputStream} instance that does 
	 * cipher from the beginning on if the given {@link Cipher} is not null 
	 * otherwise it won't start ciphering until it gets enabled later on
	 * @param input Stream that should be read
	 * @param cipher Cipher that should be used for ciphering
	 * @throws NullPointerException if {@link InputStream} is null
	 */
	public OptionalCipherInputStream(InputStream input, Cipher cipher) throws NullPointerException {
		setInputStream(input);
		this.doCipher = cipher != null;
		setCipher(cipher);
	}
	
	/**
	 * Creates a new {@link OptionalCipherInputStream} instance
	 * @param input Stream that should be read
	 * @param cipher Cipher that should be used for ciphering
	 * @param doCipher If true then cipher will immediately take place otherwise it can be enabled later on
	 * @throws NullPointerException if {@link InputStream} is null
	 */
	public OptionalCipherInputStream(InputStream input, Cipher cipher, boolean doCipher) throws NullPointerException {
		setInputStream(input);
		setCipher(cipher, doCipher);
	}
	
	/**
	 * Returns the {@link InputStream} that is used as input for the cipher
	 * @return {@link InputStream} used as input
	 */
	public InputStream getInputStream() {
		return input;
	}
	
	/**
	 * Sets the {@link InputStream} that is used as input for the cipher
	 * @param input Stream used as input for the cipher
	 * @throws NullPointerException if {@link InputStream} is null
	 */
	public synchronized void setInputStream(InputStream input) throws NullPointerException {
		if(input == null) throw new NullPointerException("InputStream cannot be null");
		this.input = input;
	}
	
	/**
	 * True if cipher is enabled and therefore the read data is ciphered
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
	 * For that use {@link OptionalCipherInputStream#setCipher(Cipher, boolean)} instead
	 * @param cipher Cipher that should be used
	 */
	public synchronized void setCipher(Cipher cipher) {
		this.cipher = cipher;
		this.cipherInput = new CipherInputStream(input, cipher!=null ? cipher : new NullCipher());
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
	public int available() throws IOException {
		return doCipher ? cipherInput.available() : input.available();
	}
	
	@Override
	public int read() throws IOException {
		return doCipher ? cipherInput.read() : input.read();
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return doCipher ? cipherInput.read(b) : input.read(b);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return doCipher ? cipherInput.read(b, off, len) : input.read(b, off, len);
	}
	
	@Override
	public void close() throws IOException {
		cipherInput.close(); 
		try { input.close(); } catch (Exception ex) {}
	}
	
	@Override
	public synchronized void mark(int readlimit) {
		cipherInput.mark(readlimit);
	}
	
	@Override
	public boolean markSupported() {
		return cipherInput.markSupported();
	}
	
	@Override
	public synchronized void reset() throws IOException {
		cipherInput.reset();
	}
	
	@Override
	public long skip(long n) throws IOException {
		return doCipher ? cipherInput.skip(n) : input.skip(n);
	}
}
