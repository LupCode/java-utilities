package com.lupcode.Utilities.network.upnp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Set;

import com.lupcode.Utilities.collections.ConcurrentHashSet;


/**
 * Implementation of a UPnP client for discovering devices in the local network
 * @author LupCode.com (Luca Vogels)
 * @since 2021-02-03
 */
public class UPnPClient {
	
	public static final InetSocketAddress DEFAULT_UPNP_BROADCAST_ADDRESS = new InetSocketAddress("239.255.255.250", 1900);
	public static final int DEFAULT_UPNP_LISTEN_PORT = 1901;
	public static final int DEFAULT_TTL = 4;
	public static final int DEFAULT_REPEATS = 2;
	public static final int DEFAULT_RECEIVE_TIMEOUT = 100;
	public static final int DEFAULT_SEARCH_TIMEOUT = 5000;
	
	public static final String SEARCH_TARGET_ALL = "ssdp:all";
	public static final String SEARCH_TARGET_ROOT = "upnp:rootdevice";
	
	protected int listenPort, defaultRepeats = 3, defaultSearchTimeout, receiveTimeout, ttl=8;
	protected InetSocketAddress broadcastAddress;
	
	/**
	 * Creates an UPnP client
	 */
	public UPnPClient() {
		this(DEFAULT_SEARCH_TIMEOUT);
	}
	
	/**
	 * Creates an UPnP client
	 * @param defaultSearchTimeout Milliseconds for default duration of a search
	 */
	public UPnPClient(int defaultSearchTimeout) {
		this(defaultSearchTimeout, DEFAULT_REPEATS);
	}
	
	/**
	 * Creates an UPnP client
	 * @param defaultSearchTimeout Milliseconds for default duration of a search
	 * @param defaultRepeats How many packets should be sent by default during the timeout duration of a search
	 */
	public UPnPClient(int defaultSearchTimeout, int defaultRepeats) {
		this(defaultSearchTimeout, defaultRepeats, DEFAULT_UPNP_BROADCAST_ADDRESS);
	}
	
	/**
	 * Creates an UPnP client
	 * @param defaultSearchTimeout Milliseconds for default duration of a search
	 * @param defaultRepeats How many packets should be sent by default during the timeout duration of a search
	 * @param broadcastAddress UPnP broadcast address the packets should be sent to
	 */
	public UPnPClient(int defaultSearchTimeout, int defaultRepeats, InetSocketAddress broadcastAddress) {
		this(defaultSearchTimeout, defaultRepeats, broadcastAddress, DEFAULT_UPNP_LISTEN_PORT, DEFAULT_RECEIVE_TIMEOUT, DEFAULT_TTL);
	}
	
	/**
	 * Creates an UPnP client
	 * @param defaultSearchTimeout Milliseconds for default duration of a search
	 * @param defaultRepeats How many packets should be sent by default during the timeout duration of a search
	 * @param broadcastAddress UPnP broadcast address the packets should be sent to
	 * @param listenPort Port on which should be listened for responses
	 * @param receiveTimeout Timeout milliseconds for receiving individual packets
	 * @param ttl Time-to-live for UDP broadcast packets (how many hops)
	 */
	public UPnPClient(int defaultSearchTimeout, int defaultRepeats, InetSocketAddress broadcastAddress, int listenPort, int receiveTimeout, int ttl) {
		this.defaultSearchTimeout = defaultSearchTimeout > 0 ? defaultSearchTimeout : DEFAULT_SEARCH_TIMEOUT;
		this.defaultRepeats = defaultRepeats > 0 ? defaultRepeats : DEFAULT_REPEATS;
		this.broadcastAddress = broadcastAddress != null ? broadcastAddress : DEFAULT_UPNP_BROADCAST_ADDRESS;
		this.listenPort = listenPort > 0 ? listenPort : DEFAULT_UPNP_LISTEN_PORT;
		this.receiveTimeout = receiveTimeout > 0 ? receiveTimeout : DEFAULT_RECEIVE_TIMEOUT;
		this.ttl = ttl > 0 ? ttl : DEFAULT_TTL;
	}
	
	/**
	 * Looks up all UPnP devices in the local network
	 * @return Set containing addresses of found devices
	 * @throws IOException if lookup failed
	 */
	public Set<InetAddress> search() throws IOException {
		return search((String[])null);
	}
	
	/**
	 * Looks up all UPnP devices in the local network that match the given targets
	 * @param searchTargets UPnP search targets
	 * @return Set containing addresses of found devices
	 * @throws IOException if lookup failed
	 */
	public Set<InetAddress> search(String... searchTargets) throws IOException {
		return search(defaultSearchTimeout, defaultRepeats, searchTargets);
	}
	
	/**
	 * Looks up all UPnP devices in the local network that match the given targets
	 * @param timeout Milliseconds how long search should wait for responses
	 * @param searchTargets UPnP search targets
	 * @return Set containing addresses of found devices
	 * @throws IOException if lookup failed
	 */
	public Set<InetAddress> search(int timeout, String... searchTargets) throws IOException {
		return search(timeout, defaultRepeats, searchTargets);
	}
	
	/**
	 * Looks up all UPnP devices in the local network that match the given targets
	 * @param timeout Milliseconds how long search should wait for responses
	 * @param repeats How many search packets should be sent during the timeout duration
	 * @param searchTargets UPnP search targets
	 * @return Set containing addresses of found devices
	 * @throws IOException if lookup failed
	 */
	public Set<InetAddress> search(int timeout, int repeats, String... searchTargets) throws IOException {
		if(searchTargets==null || searchTargets.length==0)
			searchTargets = new String[] { SEARCH_TARGET_ALL, SEARCH_TARGET_ROOT };
		repeats = repeats > 0 ? repeats : defaultRepeats;
		timeout = (timeout - (timeout % receiveTimeout)) / repeats;
		
		final int t = timeout;
		final int totalTimeout = timeout * repeats;
		
		StringBuilder sb = new StringBuilder();
		LinkedList<String> msgs = new LinkedList<>();
		for(String target : searchTargets) {
			if(target == null || target.isEmpty()) continue;
			sb.setLength(0);
			sb.append("M-SEARCH * HTTP/1.1\r\n");
			sb.append("HOST: ").append(broadcastAddress.getHostString()).append(":").append(broadcastAddress.getPort()).append("\r\n");
			sb.append("MAN: \"ssdp:discover\"\r\n");
			sb.append("MX: ").append(Math.max(1, defaultSearchTimeout/defaultRepeats/1000)).append("\r\n");
			sb.append("ST: ").append(target).append("\r\n");
			sb.append("\r\n");
			msgs.add(sb.toString());
		}
		if(msgs.isEmpty()) throw new IllegalArgumentException("Search targets cannot be empty");
		
		
		final ConcurrentHashSet<InetAddress> foundAddresses = new ConcurrentHashSet<>();
		LinkedList<Thread> threads = new LinkedList<>();
		
		Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
		while(nics.hasMoreElements()) {
			NetworkInterface nic = nics.nextElement();
			if(!nic.isUp() || nic.isLoopback()) continue;
			Enumeration<InetAddress> addrs = nic.getInetAddresses();
			while(addrs.hasMoreElements()) {
				InetAddress addr = addrs.nextElement();
				if(!(addr instanceof Inet4Address) || addr.isLoopbackAddress()) continue;
				
				Thread thread = new Thread(new Runnable() { public void run() {
					try {
						MulticastSocket socket = new MulticastSocket((SocketAddress)null);
						socket.bind(new InetSocketAddress(addr, listenPort));
						socket.setTimeToLive(ttl);
						socket.setBroadcast(true);
						socket.setSoTimeout(receiveTimeout);
						socket.joinGroup(broadcastAddress.getAddress());
						
						long started = System.currentTimeMillis();
						long nextSend = started;
						do {
							
							// Send
							if(nextSend <= System.currentTimeMillis()) {
								nextSend += t;
								for(String msg : msgs) {
									byte[] buf = msg.getBytes(StandardCharsets.UTF_8);
									try {
										socket.send(new DatagramPacket(buf, buf.length, broadcastAddress));
									} catch (Exception ex) {}
								}
							}
							
							// Receive
							byte[] buf = new byte[2048];
							DatagramPacket packet = new DatagramPacket(buf, buf.length);
							try {
								socket.receive(packet);
								if(!packet.getAddress().isLoopbackAddress())
									if(foundAddresses.add(packet.getAddress()))
										System.out.println(new String(packet.getData())+"\n");
							} catch (SocketTimeoutException ex) {}
							
						} while(System.currentTimeMillis() - started < totalTimeout);
						
						socket.disconnect();
						socket.close();
					} catch (Exception ex) { ex.printStackTrace(); }
				} });
				thread.start();
				threads.add(thread);
			}
		}
		
		for(Thread thread : threads)
			try { thread.join(); } catch (Exception ex) {}
		
		return foundAddresses;
	}
}
