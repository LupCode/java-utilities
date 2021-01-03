package com.lupcode.Utilities.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.lupcode.Utilities.concurrent.ConcurrentInteger;
import com.lupcode.Utilities.executors.DynamicThreadPoolExecutor;
import com.lupcode.Utilities.listeners.NetworkListener;

public class NetworkUtils {

	public static Executor NETWORK_PING_EXECUTOR = new DynamicThreadPoolExecutor(0, Runtime.getRuntime().availableProcessors()*32, 5000, TimeUnit.MILLISECONDS);
	
	public static int NETWORK_REACHABLE_TIMEOUT = 5000;
	
	public static long NETWORK_SCAN_INTERVAL = 1000;
	
	public static int WAKE_ON_LAN_PORT = 9;
	
	
	// Byte is bitmap:	3=allOnline, 2=firstOnline, 1=allOffline, 0=firstOffline
	protected static HashMap<NetworkListener, Byte> NETWORK_LISTENERS = new HashMap<>();
	protected static Set<InetAddress> NETWORK_ONLINE_ADDRESSES = new HashSet<>();
	protected static Runnable NETWORK_LISTENER_RUNNABLE = null;
	
	/**
	 * Returns all registered network listeners
	 * @return Set of registered listeners
	 */
	public static Set<NetworkListener> getNetworkListeners() {
		return new HashSet<>(NETWORK_LISTENERS.keySet());
	}
	
	/**
	 * Returns true if network listeners are registered
	 * @return True if listeners registered
	 */
	public static boolean hasNetworkListeners() {
		return !NETWORK_LISTENERS.isEmpty();
	}
	
	/**
	 * Returns how many network listeners are registered
	 * @return Amount of registered network listeners
	 */
	public static int getNetworkListenerCount() {
		return NETWORK_LISTENERS.size();
	}
	
	/**
	 * Removes the given network listener so it no longer gets called
	 * @param listener Listener that should be removed
	 */
	public static void removeNetworkListener(NetworkListener... listener) {
		if(listener != null && listener.length > 0)
			for(NetworkListener l : listener)
				if(l != null) NETWORK_LISTENERS.remove(l);
	}
	
	/**
	 * Adds a network listener 
	 * @param listener Listener that should be called
	 */
	public static void addNetworkListener(NetworkListener... listener) {
		if(listener != null && listener.length > 0)
			for(NetworkListener l : listener)
				if(l != null) NETWORK_LISTENERS.put(l, (byte)0b0010);
		if(NETWORK_LISTENER_RUNNABLE == null && !NETWORK_LISTENERS.isEmpty()) {
			NETWORK_LISTENER_RUNNABLE = new Runnable() { public void run() {
				long startTime = System.currentTimeMillis();
				
				// Collect all addresses that are interested
				Set<InetAddress> interestedAddresses = new HashSet<>();
				boolean scanLocalNetwork = false;
				for(NetworkListener listener : NETWORK_LISTENERS.keySet()) {
					Set<InetAddress> list = listener.getInterestedAddresses();
					if(list == null || list.isEmpty())
						scanLocalNetwork = true;
					else
						for(InetAddress addr : list)
							if(addr != null) interestedAddresses.add(addr);
				}
				
				// Check reachability of addresses
				final AtomicInteger running = new AtomicInteger(interestedAddresses.size());
				final ConcurrentLinkedQueue<InetAddress> onlineAddresses = new ConcurrentLinkedQueue<>();
				for(final InetAddress addr : interestedAddresses) {
					NETWORK_PING_EXECUTOR.execute(new Runnable() { public void run() {
						try {
							if(isReachable(addr, NETWORK_REACHABLE_TIMEOUT))
								onlineAddresses.add(addr);
						} catch (Exception e) {
							e.printStackTrace();
						}
						running.decrementAndGet();
					} });
				}
				if(scanLocalNetwork)
					onlineAddresses.addAll(getLocalDevices());
				
				while(running.get() > 0)
					try { Thread.sleep(10); } catch (Exception e) {}
				
				// Compute changes
				Set<InetAddress> newOnline = new HashSet<>();
				Set<InetAddress> newOffline = new HashSet<>();
				for(InetAddress addr : onlineAddresses) {
					if(!NETWORK_ONLINE_ADDRESSES.contains(addr))
						newOnline.add(addr);
				}
				for(InetAddress addr : NETWORK_ONLINE_ADDRESSES) {
					if(!onlineAddresses.contains(addr))
						newOffline.add(addr);
				}
				
				// Call listeners
				for(Entry<NetworkListener, Byte> entry : NETWORK_LISTENERS.entrySet()) {
					NetworkListener listener = entry.getKey();
					byte flag = entry.getValue();
					Set<InetAddress> interested = listener.getInterestedAddresses();
					boolean allOnline = true, allOffline = true;
					Set<InetAddress> newOn = new HashSet<>(), newOff = new HashSet<>();
					for(InetAddress addr : interested) {
						if(newOnline.contains(addr)) newOn.add(addr);
						if(newOffline.contains(addr)) newOff.add(addr);
						if(onlineAddresses.contains(addr))
							allOffline = false;
						else
							allOnline = false;
					}
					
					// Execute for new offline devices
					if(!newOff.isEmpty())
						try {
							listener.onOffline(newOff);
						} catch (Exception ex) {
							try { listener.onError(ex); } catch (Exception ex1) {}
						}
					
					// Execute for new online devices
					if(!newOn.isEmpty())
						try {
							listener.onOnline(newOn);
						} catch (Exception ex) {
							try { listener.onError(ex); } catch (Exception ex1) {}
						}
					
					// Execute if all offline
					if(((flag >> 1) & 0x01)==0 && allOffline) {
						flag |= 0b0010;
						try {
							listener.onAllOffline();
						} catch (Exception ex) {
							try { listener.onError(ex); } catch (Exception ex1) {}
						}
					}
					
					boolean flagAllOffline = ((flag >> 1) & 0x01)==1;
					boolean flagAllOnline = ((flag >> 3) & 0x01)==1;
					
					// Execute if first offline
					if(((flag >> 0) & 0x01)==0 && flagAllOnline && !newOff.isEmpty()) {
						flag |= 0b0001;
						try {
							listener.onFirstOffline(newOffline);
						} catch (Exception ex) {
							try { listener.onError(ex); } catch (Exception ex1) {}
						}
					} else 
						flag &= ~(0b0001); // reset
					
					// Execute if all offline
					if(!flagAllOffline && allOffline) {
						flag |= 0b0010;
						try {
							listener.onAllOffline();
						} catch (Exception ex) {
							try { listener.onError(ex); } catch (Exception ex1) {}
						}
					}
					if(!allOffline) flag &= ~(0b0010); // reset
					
					// Execute if first online
					if(((flag >> 2) & 0x01)==0 && flagAllOffline && !newOn.isEmpty()) {
						flag |= 0b0100;
						try {
							listener.onFirstOnline(newOnline);
						} catch (Exception ex) {
							try { listener.onError(ex); } catch (Exception ex1) {}
						}
					} else 
						flag &= ~(0b0100); // reset
					
					// Execute if all online
					if(!flagAllOnline && allOnline) {
						flag |= 0b1000;
						try {
							listener.onAllOnline();
						} catch (Exception ex) {
							try { listener.onError(ex); } catch (Exception ex1) {}
						}
					}
					if(!allOnline) flag &= ~(0b1000); // reset
					
					// Update if state flags have changed
					if(flag != entry.getValue())
						NETWORK_LISTENERS.put(listener, flag);
				}
				
				// update state
				NETWORK_ONLINE_ADDRESSES.clear();
				NETWORK_ONLINE_ADDRESSES.addAll(onlineAddresses);
				
				// Sleep if needed
				if(NETWORK_SCAN_INTERVAL > 0) {
					long sleep =  NETWORK_SCAN_INTERVAL - (System.currentTimeMillis() - startTime);
					if(sleep > 0)
						try { Thread.sleep(sleep); } catch (Exception e) {} 
				}
				
				if(!NETWORK_LISTENERS.isEmpty() && NETWORK_LISTENER_RUNNABLE != null)
					NETWORK_PING_EXECUTOR.execute(NETWORK_LISTENER_RUNNABLE);
				else
					NETWORK_LISTENER_RUNNABLE = null;
			} };
			NETWORK_PING_EXECUTOR.execute(NETWORK_LISTENER_RUNNABLE);
		}
	}
	
	
	
	/**
	 * Checks if the device with a given address is reachable
	 * @param address Address that should be checked
	 * @param timeout Timeout in milliseconds
	 * @return True if device is reachable at address
	 */
	public static boolean isReachable(InetAddress address, int timeout) {
		try {
			if(address.isReachable(timeout))
				return true;
		} catch (Exception ex) {}
		
		String addr = address.toString();
		int index = addr.indexOf("/");
		addr = (index <= 0 ? addr.substring(index+1) : addr.substring(0, index));
		
		try { // windows
			ProcessBuilder builder = new ProcessBuilder("ping", "-n", "1", "-w", timeout+"", addr);
			Process proc = builder.start();
			if(proc.waitFor(timeout+500, TimeUnit.MILLISECONDS))
				if(proc.exitValue() == 0)
					return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		try { // linux
			ProcessBuilder builder = new ProcessBuilder("ping", "-c", "1", "-W", Math.ceil(timeout/1000)+"", addr);
			Process proc = builder.start();
			if(proc.waitFor(timeout+500, TimeUnit.MILLISECONDS))
				if(proc.exitValue() == 0)
					return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	
	
	/**
	 * Scans local network and returns a list with the addresses 
	 * of all found devices
	 * @param subnet IP prefix for the subnet that should be checked for devices
	 * @return List of found addresses
	 */
	public static Set<InetAddress> getLocalDevices(){
		return getLocalDevices((byte[]) null);
	}
	
	/**
	 * Scans local network and returns a list with the addresses 
	 * of all found devices
	 * @param subnet IP prefix for the subnet that should be checked for devices (e.g. 192.168.0)
	 * @return List of found addresses
	 * @throws IllegalArgumentException if the subnet format could not be parsed
	 */
	public static Set<InetAddress> getLocalDevices(String subnet) throws IllegalArgumentException {
		ArrayList<Byte> arr = new ArrayList<>(3);
		String[] args = subnet.split("\\.");
		for(String arg : args) {
			if(arg.length()==0) continue;
			try {
				arr.add((byte)Integer.parseInt(arg));
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}
		byte[] a = new byte[arr.size()];
		for(int i=0; i<a.length; i++)
			a[i] = arr.get(i);
		return getLocalDevices(a);
	}
	
	/**
	 * Scans local network and returns a list with the addresses 
	 * of all found devices
	 * @param subnet IP prefix for the subnet that should be checked for devices
	 * @return List of found addresses
	 */
	public static Set<InetAddress> getLocalDevices(byte[] subnet){
		ConcurrentLinkedQueue<InetAddress> foundAddresses = new ConcurrentLinkedQueue<>();
		int timeout = NETWORK_REACHABLE_TIMEOUT;
		ConcurrentInteger running = new ConcurrentInteger(0);
		try {
			if(subnet == null || subnet.length == 0) {
				Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
				while(interfaces.hasMoreElements()) {
					NetworkInterface ni = interfaces.nextElement();
					Enumeration<InetAddress> addresses = ni.getInetAddresses();
					while(addresses.hasMoreElements()) {
						final InetAddress ownAddr = addresses.nextElement();
						if(ownAddr == null || ownAddr.isLoopbackAddress()) continue;
						byte[] ownIp = ownAddr.getAddress();
						for(int i=0; i<256; i++) {
							final byte b = (byte)i;
							running.incrementAndGet();
							NETWORK_PING_EXECUTOR.execute(new Runnable() {public void run() {
								byte[] testIp = new byte[ownIp.length];
								System.arraycopy(ownIp, 0, testIp, 0, testIp.length);
								testIp[testIp.length-1] = b;
								try {
									InetAddress addr = InetAddress.getByAddress(testIp);
									if(!addr.isLoopbackAddress() && isReachable(addr, timeout)) {
										foundAddresses.add(addr);
									}
								} catch (Exception e) {}
								running.decrementAndGet();
							}});
						}
					}
				}
			} else {
				byte[] ip = new byte[subnet.length <= 4 ? 4 : Math.max(6, subnet.length)];
				int v = ip.length - subnet.length;
				long max = (long) Math.pow(256, v);
				for(long c=0; c<max; c++) {
					long x = c;
					for(int i=0; i < v; i++) {
						ip[ip.length-i-1] = (byte)((x >>> (i*8)) & 255);
					}
					final byte[] testIp = new byte[ip.length];
					System.arraycopy(ip, 0, testIp, 0, ip.length);
					running.incrementAndGet();
					NETWORK_PING_EXECUTOR.execute(new Runnable() { public void run() {
						try {
							InetAddress addr = InetAddress.getByAddress(testIp);
							if(!addr.isLoopbackAddress() && isReachable(addr, timeout)) {
								foundAddresses.add(addr);
							}
						} catch (Exception e) {}
						running.decrementAndGet();
					} });
				}
			}
		} catch (SocketException ex) {}
		running.awaitZero(); // wait until all tasks have finished
		return new HashSet<>(foundAddresses);
	}
	
	
	public static void discoverUPnP() {
		// IP and port for UPnP
		try {
			MulticastSocket socket = new MulticastSocket(1900);
			socket.setReuseAddress(true);
			socket.setSoTimeout(30000);
			socket.joinGroup(InetAddress.getByName("239.255.255.250"));
			
			byte[] buffer = new byte[255];
			while(!socket.isClosed()) {
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				System.out.println(new String(packet.getData(), packet.getOffset(), packet.getLength()));
			}
			
			socket.close();
			
		} catch (IOException e) {
			e.printStackTrace(); // TODO REMOVE
		}
	}
	
	
	
	
	
	
	
	public static String convertMacAddress(byte[] macAddress) {
		if(macAddress == null) throw new NullPointerException("MAC address cannot be null");
		if(macAddress.length != 6) throw new IllegalArgumentException("Length of MAC address must be exacty 6");
		StringBuilder sb = new StringBuilder();
		for(byte b : macAddress)
			sb.append("-").append(((int)b) & 0xff);
		return sb.substring(1);
	}
	
	public static byte[] convertMacAddress(String macAddress) {
		if(macAddress==null) throw new NullPointerException("MAC address cannot be null");
		String[] args = macAddress.replaceAll(":", "-").split("-");
		if(args.length != 6) throw new IllegalArgumentException("Invalid format of MAC address");
		byte[] mac = new byte[6];
		for(int i=0; i<mac.length; i++)
			mac[i] = (byte) Integer.parseInt(args[i], 16); // hex to byte
		return mac;
	}
	
	
	public static Set<InetAddress> getBroadcastAddresses(){
		HashSet<InetAddress> broadcasts = new HashSet<>();
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while(interfaces.hasMoreElements()) {
				NetworkInterface i = interfaces.nextElement();
				try { if(i.isLoopback()) continue; } catch (Exception ex) {}
				for(InterfaceAddress addr : i.getInterfaceAddresses()) {
					InetAddress broadcast = addr.getBroadcast();
					if(broadcast != null) broadcasts.add(broadcast);
				}
			}
		} catch (Exception ex) {}
		return broadcasts;
	}
	
	public static void sendWakeOnLAN(String targetMacAddress) throws IOException {
		sendWakeOnLAN(targetMacAddress,(InetAddress[])null);
	}
	
	public static void sendWakeOnLAN(byte[] targetMacAddress) throws IOException {
		sendWakeOnLAN(targetMacAddress, (InetAddress[])null);
	}
	
	public static void sendWakeOnLAN(String targetMacAddress, InetAddress... broadcastAddress) throws IOException {
		sendWakeOnLAN(convertMacAddress(targetMacAddress), broadcastAddress);
	}
	
	public static void sendWakeOnLAN(byte[] targetMacAddress, InetAddress... broadcastAddress) throws IOException {
		sendWakeOnLAN(targetMacAddress, Arrays.asList(broadcastAddress));
	}
	
	public static void sendWakeOnLAN(String targetMacAddress, Collection<InetAddress> broadcastAddress) throws IOException {
		sendWakeOnLAN(convertMacAddress(targetMacAddress), broadcastAddress);
	}
	
	public static void sendWakeOnLAN(byte[] targetMacAddress, Collection<InetAddress> broadcastAddress) throws IOException {
		if(broadcastAddress == null || broadcastAddress.isEmpty())
			broadcastAddress = getBroadcastAddresses();
		
		byte[] buf = new byte[102];
		for(int i=0; i<buf.length; i++) {
			if(i < 6) buf[i] = (byte) 0xff;
			else buf[i] = targetMacAddress[i % 6];
		}
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		packet.setPort(WAKE_ON_LAN_PORT);
		DatagramSocket socket = new DatagramSocket();
		try {
			socket.setBroadcast(true);
			for(InetAddress addr : broadcastAddress) {
				if(addr == null) throw new NullPointerException("Broadcast address cannot be null");
				try {
					packet.setAddress(addr);
					socket.send(packet);
				} catch (Exception ex) { ex.printStackTrace(); }
			}
		} catch (Exception ex){
			ex.printStackTrace();
		} finally {
			socket.close();
		}
	}
	
}
