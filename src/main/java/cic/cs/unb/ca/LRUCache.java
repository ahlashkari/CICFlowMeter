package cic.cs.unb.ca;

import java.util.LinkedHashMap;

public class LRUCache <K, V> extends LinkedHashMap<K, V> {

	private static final long serialVersionUID = 7910539587098354435L;

	private final int MAX_CACHE_SIZE;
	
	public LRUCache(int cacheSize) {
		super(16, (float) 0.75, true);
		MAX_CACHE_SIZE = cacheSize;
	}

	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		return size() >= MAX_CACHE_SIZE;
	}
	
}
