/*******************************************************************************
 * Copyright (c) 2013 Olli Alm / Metropolia www.metropolia.fi
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package fi.metropolia.mediaworks.juju.weighting;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import fi.metropolia.mediaworks.juju.util.ValueSortedMap;

public class Weighter<A> {
	private Map<A, Double> map = new ValueSortedMap<A, Double>(true);
	
	Set<Map.Entry<A, Double>> getMap() {
		return map.entrySet();
	}
	
	public Weighter() {
	}
	
	
	public void addItem(A item, double value) {
		map.put(item, value);
	}
	
	public void weight(ItemWeighter<A> weighter) {
		weighter.beforeProcess(this);
		
		Map<A, Double> newMap = new ValueSortedMap<A, Double>(true);
		
		for (A k : map.keySet()) {
			double w = weighter.process(this, k, map.get(k));
			if (w > 0 && !Double.isInfinite(w) && !Double.isNaN(w)) {
				newMap.put(k, w);
			}
		}
		
		map = newMap;
		
		weighter.afterProcess(this);
	}
	
	@Override
	public String toString() {
		return map.toString();
	}
	
	public Map<A, Double> getResult(boolean normalized) {
		if (normalized) {
			Map<A, Double> result = new ValueSortedMap<A, Double>(true);
			
			double max = Double.MIN_VALUE;
			for (Entry<A, Double> e : map.entrySet()) {
				if (e.getValue() > max) {
					max = e.getValue();
				}
			}
			
			for (Entry<A, Double> e : map.entrySet()) {
				result.put(e.getKey(), e.getValue() / max);
			}
			
			return result;
		} else {
			return ImmutableMap.copyOf(map);
		}
	}
}
