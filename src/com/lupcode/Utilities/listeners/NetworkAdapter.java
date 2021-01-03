package com.lupcode.Utilities.listeners;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

public abstract class NetworkAdapter implements NetworkListener {

	protected Set<InetAddress> interestedAddresses = new HashSet<>();
	
	/**
	 * Creates a listener that is interested in all devices in the local network
	 */
	public NetworkAdapter() {
		
	}
	
	/**
	 * Creates a listener that is interested in the given addresses. 
	 * If empty or null then listens for all devices in the local network
	 * @param address Addresses the listener should listen for
	 */
	public NetworkAdapter(InetAddress... address) {
		addInterestedAddress(address);
	}
	
	@Override
	public Set<InetAddress> getInterestedAddresses() {
		return new HashSet<>(interestedAddresses);
	}
	
	/**
	 * Returns true if the listener is not listening for specific addresses 
	 * but instead for all devices in the local network
	 * @return True if listening for all devices in the local networks
	 */
	public boolean isListeneningForAllDevices() {
		return this.interestedAddresses.isEmpty();
	}
	
	/**
	 * Returns the amount of addresses the listener is listening for. 
	 * If zero then the listener listens for all devices in the local network
	 * @return Amount of addresses listener listens to
	 */
	public int getInterestedAddressCount() {
		return this.interestedAddresses.size();
	}
	
	/**
	 * Removes all addresses so the listener listens for all devices in the local network
	 */
	public void clearInterestedAddresses() {
		this.interestedAddresses.clear();
	}
	
	/**
	 * Adds the given addresses to the listener so it listens for them
	 * @param address Addresses that should be added
	 */
	public void addInterestedAddress(InetAddress... address) {
		if(address != null && address.length > 0)
			for(InetAddress addr : address)
				if(addr != null) interestedAddresses.add(addr);
	}
	
	/**
	 * Removes the given addresses from the listener so it no longer listens for them. 
	 * @param address Addresses that should be removed
	 */
	public void removeInterestedAddress(InetAddress... address) {
		if(address != null && address.length > 0)
			for(InetAddress addr : address)
				if(addr != null) interestedAddresses.remove(addr);
	}
	
	
	@Override
	public void onAllOffline() {}
	
	@Override
	public void onAllOnline() {}
	
	@Override
	public void onError(Throwable throwable) {
		throwable.printStackTrace();
	}
	
	@Override
	public void onFirstOffline(Set<InetAddress> addresses) {}
	
	@Override
	public void onFirstOnline(Set<InetAddress> addresses) {}
	
	@Override
	public void onOffline(Set<InetAddress> addresses) {}
	
	@Override
	public void onOnline(Set<InetAddress> addresses) {}
}
