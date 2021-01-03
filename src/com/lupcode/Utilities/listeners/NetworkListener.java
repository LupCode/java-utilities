package com.lupcode.Utilities.listeners;

import java.net.InetAddress;
import java.util.Set;

public interface NetworkListener {

	
	/**
	 * Return a set of addresses the listener should listen for. 
	 * If empty or null the listener will listen for all devices in the local network
	 * @return Set of interested addresses
	 */
	public Set<InetAddress> getInterestedAddresses();
	
	/**
	 * Gets called if a device came online
	 * @param addresses Addresses of the devices that came online
	 */
	public void onOnline(Set<InetAddress> addresses);
	
	/**
	 * Gets called for the first device(s) that came online after 
	 * all devices were offline
	 * @param addresses Addresses of the devices that came online at first
	 */
	public void onFirstOnline(Set<InetAddress> addresses);
	
	/**
	 * Gets called if all interested devices are online. 
	 * Never gets called if listener listens for all devices 
	 * in local network ({@link NetworkListener#getInterestedAddresses()} is empty or null)
	 */
	public void onAllOnline();
	
	/**
	 * Gets called if a device went offline
	 * @param addresses Addresses of the devices that went offline
	 */
	public void onOffline(Set<InetAddress> addresses);
	
	/**
	 * Gets called for the first device that went offline after 
	 * all devices were online. 
	 * Never gets called if listener listens for all devices 
	 * in local network ({@link NetworkListener#getInterestedAddresses()} is empty or null)
	 * @param addresses Addresses of the devices that went offline at first
	 */
	public void onFirstOffline(Set<InetAddress> addresses);
	
	/**
	 * Gets called after all devices went offline
	 */
	public void onAllOffline();
	
	/**
	 * Gets called if an error occurs while processing one of the other methods
	 * @param throwable Throwable that occurred
	 */
	public void onError(Throwable throwable);
}
