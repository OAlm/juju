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
package fi.metropolia.mediaworks.juju.test;

import java.util.List;
import java.util.Random;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.google.common.collect.Lists;

public class StuffTest {
	private static double getMean(List<Double> numbars) {
		DescriptiveStatistics test = new DescriptiveStatistics();
		for (double i : numbars) {
			test.addValue(i);
		}
		return test.getMean();
	}
	
	private static double getAverage(List<Double> numbars) {
		int sum = 0;
		for (double i : numbars) {
			sum += i;
		}
		return (double)sum / (double)numbars.size();
	}
	
	private static boolean test() {
		List<Double> numbars = Lists.newArrayList();
		Random r = new Random();
		int numbrs = r.nextInt(1000)+1;
		for (int i = 0; i < numbrs; i++) {
			numbars.add(r.nextDouble() * 1000);
		}
		
		double mean = getMean(numbars);
		double average = getAverage(numbars);
		
		double diff = mean - average;
		
		System.out.printf("%f = %f (%s)\n", mean, average, diff);
		return Math.abs(diff) < 1;
	}
	
	public static void main(String[] args) {
		while (true) {
			if (!test()) {
				break;
			}
		}
		System.out.println("FAIL!");
	}
}
