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
package fi.metropolia.mediaworks.juju.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

public class Tools {
	static org.apache.log4j.Logger log = Logger.getLogger(Tools.class);

	/**
	 * rip off start and end tags + entities.
	 * 
	 * @return
	 */
	public static String ripTags(String text) {
		String result = "";
		Pattern p = Pattern.compile("</?\\w+\\s?/?>");
		Matcher m = p.matcher(text);
		result = m.replaceAll(" ");
		while (m.find()) {
			System.out.println("MATCH: " + text.substring(m.start(), m.end()));
		}
		return result;
	}

	/**
	 * based on the given class, retrieves the directory path of the project
	 * NOTE: assuming that folder /bin indicates the project path!!
	 */
	public static String getProjectPath(Object instance) {
		String packagepath = instance.getClass().getName().replace('.', '/') + ".class";

		ClassLoader loader = instance.getClass().getClassLoader();
		String fullpath = loader.getResource(packagepath).toString();
		String projectFolder = fullpath.substring(0, fullpath.indexOf("bin")); // rip
																				// /bin
																				// off

		// System.out.println(System.getProperty("os.name")); FIXME, melko ruma
		if (System.getProperty("os.name").equals("Linux")) {
			projectFolder = projectFolder.substring(5);
		} else {
			projectFolder = projectFolder.substring(6);
		}

		return projectFolder;
	}

	// numerical entities of form &#34; --> TODO: replace entities with correct
	// characters
	public static String ripEntities(String text) {
		// System.out.println("text before: "+text);
		String result = StringEscapeUtils.unescapeXml(text);
		// System.out.println("text after : "+result);
		return result;
		/*
		 * String result = ""; Pattern p = Pattern.compile("&#\\d{1,4};");
		 * Matcher m = p.matcher(text); result = m.replaceAll(" ");
		 * while(m.find()) { System.out.println("MATCH: "
		 * +text.substring(m.start(), m.end())); } return result;
		 */
	}

	public static String ripCrap(String content) {

		return ripEntities(ripTags(content));
	}

	/**
	 * extract lemma from omorfi raw output. in addition, the feed contains
	 * _NORESULT__ tags for the content that didn't have correspondence from
	 * omorfi
	 * 
	 * @param token
	 * @return
	 */

	static String nor = "___NORESULT___";
	static Pattern p = Pattern.compile("\\[LEMMA=.+?\\]");

	public static String ripLemma(String token, boolean debug) {

		String debugStr = "";
		if (debug) {
			debugStr = "(" + token.substring(0, token.indexOf(' ')).toLowerCase() + ")";
		}

		if (token.endsWith(nor)) { // __NORESULT__, return string
			return token.substring(0, token.indexOf(' ')).toLowerCase() + " " + debugStr;
		} else {

			Matcher m = p.matcher(token);

			int begin = -1;
			int end = -1;
			String lemma = "";
			while (m.find()) {
				begin = m.start();
				end = m.end();

				String rawToken = token.substring(begin, end);
				// log.debug("raw: "+rawToken);
				lemma += rawToken.substring(7, rawToken.length() - 1).toLowerCase();
			}

			if (begin == -1) {
				log.error("NOT FOUND, ERROR, EXIT! Token: '" + token + "'");
				System.exit(0);
			}

			return lemma + " " + debugStr;
		}
	}

	public static void writeFile(String file, String text) {
		try {
			// Create file
			FileWriter fstream = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(text);
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public static Reader openFile(File file) throws FileNotFoundException {
		return openStream(new FileInputStream(file));
	}
	
	public static Reader openStream(InputStream inputStream) {
		BufferedInputStream bis = new BufferedInputStream(inputStream);
		try {
			CharsetDetector cd = new CharsetDetector();
			cd.setText(bis);
			CharsetMatch cm = cd.detect();
		
			return cm.getReader();
		} catch (Exception e) {
			log.error("Could not detect encoding", e);
		}
		return new InputStreamReader(inputStream);
	}

	public static String readFile(File file) throws Exception {
		try {
			BufferedReader br = new BufferedReader(openFile(file));

			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
			}

			return sb.toString();
		} catch (Exception e) {
			throw new Exception("Could not load file", e);
		}
	}

	/**
	 * print first xx items of map, ordered by value frequency
	 * 
	 * @param count
	 * @param map
	 * @return
	 */
	public static Map<String, Integer> head(int count, Map<String, Integer> map) {
		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(map.entrySet());

		Collections.sort(list, new Comparator<Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> o2, Entry<String, Integer> o1) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});

		Map<String, Integer> result = new LinkedHashMap<String, Integer>();
		int i = 0;
		for (Iterator<Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> entry = it.next();
			result.put(entry.getKey(), entry.getValue());
			if (i >= count) {
				break;
			}
			i++;
		}
		return result;
	}

	public static void doSerialize(Serializable obj, String filename) throws IOException {
		FileOutputStream fos = new FileOutputStream(filename);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(obj);
		oos.close();
	}

	@SuppressWarnings("unchecked")
	public static <A> A readSerialized(String filename) throws Exception {
		try {
			FileInputStream fis = new FileInputStream(filename);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object obj = ois.readObject();
			ois.close();

			return (A) obj;
		} catch (Exception e) {
			throw new Exception("Cannot read", e);
		}
	}

	public static void orderRowsInFile(String inputFile, String outputFile) throws Exception {
		String content = Tools.readFile(new File(inputFile));

		Scanner s = new Scanner(content);
		TreeSet<String> temp = new TreeSet<String>();
		while (s.hasNextLine()) {
			temp.add(s.nextLine());
		}
		s.close();
		StringBuffer result = new StringBuffer();

		for (String line : temp) {
			result.append(line + "\n");
		}

		Tools.writeFile(outputFile, result.toString());
	}

	public static ArrayList<String> containsRipStartEnd(String content, String regex, String start, String end) {

		int startL = start.length();
		int endL = end.length();

		ArrayList<String> result = new ArrayList<String>();

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(content);
		while (m.find()) {
			String match = content.substring(m.start(), m.end());
			match = match.substring(startL, match.length() - endL);
			result.add(match);
		}
		return result;
	}

	public static void main(String[] args) {
		try {
			System.out.println(Tools.readFile(new File("test.txt")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static <A> List<A> getRandomElements(List<A> list, int count) {
		Random r = new Random();
		
		Set<Integer> indexes = Sets.newHashSet();
		while (indexes.size() < count) {
			indexes.add(r.nextInt(list.size()));
		}
		
		List<A> result = Lists.newLinkedList();
		for (Integer i : indexes) {
			result.add(list.get(i));
		}
		
		Collections.shuffle(result, r);
		return result;
	}
	
	public static <A> void removeElementsByClass(List<A> list, Class<?> clazz) {
		String name = clazz.toString().split(" ",2)[1];
		Iterator<A> iter = list.iterator();
		
		while (iter.hasNext()) {
			A i = iter.next();
			if (i.getClass().equals(clazz) || i.toString().contains(name)) {
				iter.remove();
			}
		}
	}
}
