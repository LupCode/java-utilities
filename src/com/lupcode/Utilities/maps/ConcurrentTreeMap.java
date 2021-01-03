package com.lupcode.Utilities.maps;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * {@link TreeMap} but allows concurrent access
 * @author LupCode.com (Luca Vogels)
 * @since 2021-01-03
 * @param <K> Key that should be stored
 * @param <V> Value that should be stored
 */
public class ConcurrentTreeMap<K, V> extends TreeMap<K, V> {
	private static final long serialVersionUID = 1L;
	
	private ReadWriteLock lock = new ReentrantReadWriteLock();
	
	public ConcurrentTreeMap() {
		super();
	}
	public ConcurrentTreeMap(Comparator<? super K> comparator){
		super(comparator);
	}
	public ConcurrentTreeMap(Map<? extends K, ? extends V> m) {
		super();
		putAll(m);
	}
	public ConcurrentTreeMap(SortedMap<K, ? extends V> m) {
		super(m.comparator());
		putAll(m);
	}
	
	@Override
	public boolean equals(Object o) {
		lock.readLock().lock();
		boolean v = super.equals(o);
		lock.readLock().unlock();
		return v;
	}
	
	@Override
	public boolean isEmpty() {
		lock.readLock().lock();
		boolean v = super.isEmpty();
		lock.readLock().unlock();
		return v;
	}
	
	@Override
	public java.util.Map.Entry<K, V> ceilingEntry(K key) {
		lock.readLock().lock();
		java.util.Map.Entry<K, V> entry = super.ceilingEntry(key);
		lock.readLock().unlock();
		return entry;
	}
	
	@Override
	public K ceilingKey(K key) {
		lock.readLock().lock();
		key = super.ceilingKey(key);
		lock.readLock().unlock();
		return key;
	}
	
	@Override
	public void clear() {
		lock.writeLock().lock();
		super.clear();
		lock.writeLock().unlock();
	}
	
	@Override
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		lock.writeLock().lock();
		V v = super.compute(key, remappingFunction);
		lock.writeLock().unlock();
		return v;
	}
	
	@Override
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
		lock.writeLock().lock();
		V v = super.computeIfAbsent(key, mappingFunction);
		lock.writeLock().unlock();
		return v;
	}
	
	@Override
	public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		lock.writeLock().lock();
		V v = super.computeIfPresent(key, remappingFunction);
		lock.writeLock().unlock();
		return v;
	}
	
	@Override
	public boolean containsKey(Object key) {
		lock.readLock().lock();
		boolean v = super.containsKey(key);
		lock.readLock().unlock();
		return v;
	}
	
	@Override
	public boolean containsValue(Object value) {
		lock.readLock().lock();
		boolean v = super.containsValue(value);
		lock.readLock().unlock();
		return v;
	}
	
	@Override
	public NavigableSet<K> descendingKeySet() {
		lock.readLock().lock();
		NavigableSet<K> set = super.descendingKeySet();
		lock.readLock().unlock();
		return set;
	}
	
	@Override
	public NavigableMap<K, V> descendingMap() {
		lock.readLock().lock();
		NavigableMap<K, V> set = super.descendingMap();
		lock.readLock().unlock();
		return set;
	}
	
	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		lock.readLock().lock();
		Set<java.util.Map.Entry<K, V>> set = super.entrySet();
		lock.readLock().unlock();
		return set;
	}
	
	@Override
	public java.util.Map.Entry<K, V> firstEntry() {
		lock.readLock().lock();
		java.util.Map.Entry<K, V> entry = super.firstEntry();
		lock.readLock().unlock();
		return entry;
	}
	
	@Override
	public K firstKey() {
		lock.readLock().lock();
		K key = super.firstKey();
		lock.readLock().unlock();
		return key;
	}
	
	@Override
	public java.util.Map.Entry<K, V> floorEntry(K key) {
		lock.readLock().lock();
		java.util.Map.Entry<K, V> entry = super.floorEntry(key);
		lock.readLock().unlock();
		return entry;
	}
	
	@Override
	public K floorKey(K key) {
		lock.readLock().lock();
		key = super.floorKey(key);
		lock.readLock().unlock();
		return key;
	}
	
	@Override
	public void forEach(BiConsumer<? super K, ? super V> action) {
		lock.writeLock().lock();
		super.forEach(action);
		lock.writeLock().unlock();
	}
	
	@Override
	public V get(Object key) {
		lock.readLock().lock();
		V v = super.get(key);
		lock.readLock().unlock();
		return v;
	}
	
	@Override
	public V getOrDefault(Object key, V defaultValue) {
		lock.readLock().lock();
		V v = super.getOrDefault(key, defaultValue);
		lock.readLock().unlock();
		return v;
	}
	
	@Override
	public SortedMap<K, V> headMap(K toKey) {
		lock.readLock().lock();
		SortedMap<K, V> map = super.headMap(toKey);
		lock.readLock().unlock();
		return map;
	}
	
	@Override
	public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
		lock.readLock().lock();
		NavigableMap<K, V> map = super.headMap(toKey, inclusive);
		lock.readLock().unlock();
		return map;
	}
	
	@Override
	public java.util.Map.Entry<K, V> higherEntry(K key) {
		lock.readLock().lock();
		java.util.Map.Entry<K, V> entry = super.higherEntry(key);
		lock.readLock().unlock();
		return entry;
	}
	
	@Override
	public K higherKey(K key) {
		lock.readLock().lock();
		key = super.higherKey(key);
		lock.readLock().unlock();
		return key;
	}
	
	@Override
	public Set<K> keySet() {
		lock.readLock().lock();
		Set<K> set = super.keySet();
		lock.readLock().unlock();
		return set;
	}
	
	@Override
	public java.util.Map.Entry<K, V> lastEntry() {
		lock.readLock().lock();
		java.util.Map.Entry<K, V> entry = super.lastEntry();
		lock.readLock().unlock();
		return entry;
	}
	
	@Override
	public K lastKey() {
		lock.readLock().lock();
		K key = super.lastKey();
		lock.readLock().unlock();
		return key;
	}
	
	@Override
	public java.util.Map.Entry<K, V> lowerEntry(K key) {
		lock.readLock().lock();
		java.util.Map.Entry<K, V> entry = super.lowerEntry(key);
		lock.readLock().unlock();
		return entry;
	}
	
	@Override
	public K lowerKey(K key) {
		lock.readLock().lock();
		key = super.lowerKey(key);
		lock.readLock().unlock();
		return key;
	}
	
	@Override
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		lock.writeLock().lock();
		V v = super.merge(key, value, remappingFunction);
		lock.writeLock().unlock();
		return v;
	}
	
	@Override
	public NavigableSet<K> navigableKeySet() {
		lock.readLock().lock();
		NavigableSet<K> set = super.navigableKeySet();
		lock.readLock().unlock();
		return set;
	}
	
	@Override
	public java.util.Map.Entry<K, V> pollFirstEntry() {
		lock.writeLock().lock();
		java.util.Map.Entry<K, V> entry = super.pollFirstEntry();
		lock.writeLock().unlock();
		return entry;
	}
	
	@Override
	public java.util.Map.Entry<K, V> pollLastEntry() {
		lock.writeLock().lock();
		java.util.Map.Entry<K, V> entry = super.pollLastEntry();
		lock.writeLock().unlock();
		return entry;
	}
	
	@Override
	public V put(K key, V value) {
		lock.writeLock().lock();
		value = super.put(key, value);
		lock.writeLock().unlock();
		return value;
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		lock.writeLock().lock();
		super.putAll(map);
		lock.writeLock().unlock();
	}
	
	@Override
	public V putIfAbsent(K key, V value) {
		lock.writeLock().lock();
		value = super.putIfAbsent(key, value);
		lock.writeLock().unlock();
		return value;
	}
	
	@Override
	public V remove(Object key) {
		lock.writeLock().lock();
		V value = super.remove(key);
		lock.writeLock().unlock();
		return value;
	}
	
	@Override
	public boolean remove(Object key, Object value) {
		lock.writeLock().lock();
		boolean v = super.remove(key, value);
		lock.writeLock().unlock();
		return v;
	}
	
	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		lock.writeLock().lock();
		boolean v = super.replace(key, oldValue, newValue);
		lock.writeLock().unlock();
		return v;
	}
	
	@Override
	public V replace(K key, V value) {
		lock.writeLock().lock();
		value = super.replace(key, value);
		lock.writeLock().unlock();
		return value;
	}
	
	@Override
	public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
		lock.writeLock().lock();
		super.replaceAll(function);
		lock.writeLock().unlock();
	}
	
	@Override
	public int size() {
		lock.readLock().lock();
		int size = super.size();
		lock.readLock().unlock();
		return size;
	}
	
	@Override
	public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
		lock.readLock().lock();
		NavigableMap<K, V> map = super.subMap(fromKey, fromInclusive, toKey, toInclusive);
		lock.readLock().unlock();
		return map;
	}
	
	@Override
	public SortedMap<K, V> subMap(K fromKey, K toKey) {
		lock.readLock().lock();
		SortedMap<K, V> map = super.subMap(fromKey, toKey);
		lock.readLock().unlock();
		return map;
	}
	
	@Override
	public SortedMap<K, V> tailMap(K fromKey) {
		lock.readLock().lock();
		SortedMap<K, V> map = super.tailMap(fromKey);
		lock.readLock().unlock();
		return map;
	}
	
	@Override
	public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
		lock.readLock().lock();
		NavigableMap<K, V> map = super.tailMap(fromKey, inclusive);
		lock.readLock().unlock();
		return map;
	}
	
	@Override
	public Collection<V> values() {
		lock.readLock().lock();
		Collection<V> c = super.values();
		lock.readLock().unlock();
		return c;
	}
}
