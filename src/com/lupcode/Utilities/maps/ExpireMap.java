package com.lupcode.Utilities.maps;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * {@link Map} that holds values up to a certain time until they expire
 * @author LupCode.com (Luca Vogels)
 * @since 2021-01-03
 * @param <K> Key that should be stored
 * @param <V> Value that should be stored
 */
public class ExpireMap<K, V> implements Map<K, V> {

	private class CacheEntry {
		K key;
		V value;
		long lastUsed;
		CacheEntry older = null, newer = null;
		private CacheEntry(K key, V value){
			this.key = key;
			this.value = value;
			lastUsed = System.currentTimeMillis();
		}
		@Override
		public String toString() {
			return value!=null ? value.toString() : "null";
		}
	}
	
	
	
	private Map<K, CacheEntry> map = new HashMap<>();
	private CacheEntry oldest = null, newest = null;
	private long expireTime;
	private boolean cleanupOnPut, cleanupOnGet, resetExpireOnGet;
	
	
	public ExpireMap(long expireTime, boolean cleanupOnPut, boolean cleanupOnGet, boolean resetExpireOnGet) {
		this.expireTime = expireTime;
		this.cleanupOnPut = cleanupOnPut;
		this.cleanupOnGet = cleanupOnGet;
		this.resetExpireOnGet = resetExpireOnGet;
	}
	
	public synchronized void cleanup() {
		final long time = System.currentTimeMillis();
		while(oldest!=null) {
			if(time - oldest.lastUsed < expireTime)
				break;
			map.remove(oldest.key);
			oldest = oldest.newer;
			if(oldest!=null)
				oldest.older = null;
		}
		if(oldest==null)
			newest = null;
	}
	
	protected synchronized void resetExpire(CacheEntry entry) {
		if(entry.newer!=null)
			entry.newer.older = entry.older;
		else
			return;
		if(entry.older!=null)
			entry.older.newer = entry.newer;
		else
			oldest = entry.newer;
		if(newest!=null)
			newest.newer = entry;
		entry.newer = null;
		entry.older = newest;
		newest = entry;
		entry.lastUsed = System.currentTimeMillis();
	}
	
	protected synchronized CacheEntry removeEntry(Object key) {
		CacheEntry entry = map.remove(key);
		if(entry!=null) {
			if(entry.newer!=null)
				entry.newer.older = entry.older;
			else
				newest = entry.older;
			if(entry.older!=null)
				entry.older.newer = entry.newer;
			else
				oldest = entry.newer;
		} return entry;
	}
	
	protected synchronized void resetExpireForAll() {
		final long time = System.currentTimeMillis();
		CacheEntry curr = newest;
		while(curr!=null) {
			curr.lastUsed = time;
			curr = curr.older;
		}
	}
	
	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		for(CacheEntry entry : map.values())
			if(entry.value==null ? value==null : entry.value.equals(value))
				return true;
		return false;
	}

	@Override
	public V get(Object key) {
		if(cleanupOnGet)
			cleanup();
		CacheEntry entry = map.get(key);
		if(entry!=null) {
			if(resetExpireOnGet)
				resetExpire(entry);
			return entry.value;
		} return null;
	}

	@Override
	public synchronized V put(K key, V value) {
		if(cleanupOnPut)
			cleanup();
		CacheEntry oldEntry = removeEntry(key);
		CacheEntry entry = new CacheEntry(key, value);
		if(newest!=null)
			newest.newer = entry;
		if(oldest==null)
			oldest = entry;
		entry.older = newest;
		newest = entry;
		map.put(key, entry);
		return oldEntry!=null ? oldEntry.value : null;
	}

	@Override
	public V remove(Object key) {
		CacheEntry entry = removeEntry(key);
		return entry!=null ? entry.value : null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for(Entry<? extends K, ? extends V> entry : m.entrySet())
			put(entry.getKey(), entry.getValue());
	}

	@Override
	public void clear() {
		oldest = null;
		newest = null;
		map.clear();
	}

	@Override
	public Set<K> keySet() {
		return keySet(false);
	}
	
	public Set<K> keySet(boolean ignoreExpireUpdate){
		if(!ignoreExpireUpdate && resetExpireOnGet)
			resetExpireForAll();
		if(cleanupOnGet)
			cleanup();
		return map.keySet();
	}

	@Override
	public Collection<V> values() {
		return values(false);
	}
	
	public Collection<V> values(boolean ignoreExpireUpdate){
		if(!ignoreExpireUpdate && resetExpireOnGet)
			resetExpireForAll();
		if(cleanupOnGet)
			cleanup();
		Collection<V> list = new LinkedList<>();
		for(CacheEntry entry : map.values())
			list.add(entry.value);
		return list;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return entrySet(false);
	}
	
	public Set<Entry<K, V>> entrySet(boolean ignoreExpireUpdate){
		if(!ignoreExpireUpdate && resetExpireOnGet)
			resetExpireForAll();
		if(cleanupOnGet)
			cleanup();
		Set<Entry<K, V>> set = new HashSet<>();
		for(Entry<K, CacheEntry> entry : map.entrySet())
			set.add(new Entry<K, V>() {
				public K getKey() { return entry.getKey(); }
				public V getValue() { return entry.getValue().value; }
				public V setValue(V value) { return put(entry.getKey(), value); }
			});
		return set;
	}

	@Override
	public String toString() {
		return map.toString();
	}
}
