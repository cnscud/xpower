package com.cnscud.xpower.ddd.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

public class RoundRobinWithWeight<T> {
	private Map<T, Integer> _weights = new HashMap<T, Integer>();

	private int _totalWeight = 0;
	private Random _random = new Random();

	public synchronized T get() {
		if (_weights.isEmpty()) {
			return null;
		}
		int r = _random.nextInt(_totalWeight);
		for (Entry<T, Integer> entry : _weights.entrySet()) {
			if (r <= 0) {
				return entry.getKey();
			} else {
				r -= entry.getValue();
			}
		}
		// unreachable code.
		return null;
	}

	public synchronized void put(T obj, int weight) {
		if (_weights.get(obj) == null) {
			_weights.put(obj, weight);
			_totalWeight += weight;
		}
	}

	public synchronized Set<T> getAll() {
	    return _weights.keySet();
	}
}
