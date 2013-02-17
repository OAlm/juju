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
package fi.metropolia.mediaworks.juju.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PRFBean {
	@XmlElement public double precision = 0.0;
	@XmlElement public double recall = 0.0;
	@XmlElement public double fMeasure = 0.0;
	
	public PRFBean() {
		
	}
	
	public PRFBean(double precision, double recall, double fMeasure) {
		this.precision = precision;
		this.recall = recall;
		this.fMeasure = fMeasure;
	}
	
	@Override
	public String toString() {
		return String.format("P: %.4f R: %.4f F: %.4f", precision, recall, fMeasure);
	}
	
	public static PRFBean sum(List<PRFBean> list) {
		PRFBean r = new PRFBean();
		for (PRFBean p : list) {
			r.precision += p.precision;
			r.recall += p.recall;
			r.fMeasure += p.fMeasure;
		}
		return r;
	}
	
	public static PRFBean avg(List<PRFBean> list) {
		PRFBean r = sum(list);
		r.precision /= list.size();
		r.recall /= list.size();
		r.fMeasure /= list.size();
		return r;
	}
}
