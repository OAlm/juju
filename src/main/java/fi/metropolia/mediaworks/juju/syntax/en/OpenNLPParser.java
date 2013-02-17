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
package fi.metropolia.mediaworks.juju.syntax.en;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import opennlp.tools.chunker.Chunker;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import org.apache.tika.Tika;

import com.google.common.collect.Lists;

import fi.metropolia.mediaworks.juju.document.Document;
import fi.metropolia.mediaworks.juju.document.Sentence;
import fi.metropolia.mediaworks.juju.syntax.parser.IDocumentParser;

public class OpenNLPParser implements IDocumentParser {
	private static final List<String> SUPPORTED_LANGUAGES = Lists.newArrayList("en"); 
	
	private POSTagger tagger;
	private SentenceDetector sentenceDetector;
	private Tokenizer tokenizer;
	private Chunker chunker;

	public Chunker getChunker() {
		if (chunker == null) {
			try {
				InputStream stream = getClass().getResourceAsStream("/opennlp/en-chunker.bin");
				ChunkerModel chunkerModel = new ChunkerModel(stream);
				stream.close();
				chunker = new ChunkerME(chunkerModel);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return chunker;
	}
	
	public Tokenizer getTokenizer() {
		if (tokenizer == null) {
			try {
				InputStream stream = getClass().getResourceAsStream("/opennlp/en-token.bin");
				TokenizerModel tokenizerModel = new TokenizerModel(stream);
				stream.close();
				tokenizer = new TokenizerME(tokenizerModel);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return tokenizer;
	}
	
	public SentenceDetector getSentenceDetector() {
		if (sentenceDetector == null) {
			try {
				InputStream stream = getClass().getResourceAsStream("/opennlp/en-sent.bin");
				SentenceModel sentenceModel = new SentenceModel(stream);
				stream.close();
				sentenceDetector = new SentenceDetectorME(sentenceModel);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sentenceDetector;
	}
	
	public POSTagger getTagger() {
		if (tagger == null) {
			try {
				InputStream stream = getClass().getResourceAsStream("/opennlp/en-pos-maxent.bin");
				POSModel posModel = new POSModel(stream);
				stream.close();
				tagger = new POSTaggerME(posModel);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return tagger;
	}

	public String[] getSentences(String input) {
		String[] sentences = getSentenceDetector().sentDetect(input);
		return sentences;
	}
	
	public String[] getTokens(String input) {
		String[] tokens = getTokenizer().tokenize(input);
		return tokens;
	}

	@Override
	public Document parseDocument(String content) {
		Document document = new Document();
		
		for (String sentenceString : getSentences(content)) {
			Sentence sentence = new Sentence();
			
			String[] tokens = getTokens(sentenceString);
			String[] tags = getTagger().tag(tokens);
			
//			String[] chunks = getChunker().chunk(tokens, tags);
			
			for (int i = 0; i < tokens.length; i++) {
				OpenNLPPartOfSpeech pos;
				if (tags[i].matches("[a-zA-Z]{1,}(\\$)?")) {
					pos = OpenNLPPartOfSpeech.getWithTag(tags[i]);
				} else {
					pos = OpenNLPPartOfSpeech.SYM;
				}
				
				OpenNLPToken token = new OpenNLPToken(tokens[i].trim(), pos);
				sentence.add(token);
			}
			
			if (sentence.size() > 0) {
				document.add(sentence);
			}
		}
		
		return document;
	}

	@Override
	public List<String> getSupportedLanguages() {
		return SUPPORTED_LANGUAGES;
	}

	@Override
	public int getPriority() {
		return 1;
	}

	public static void main(String[] args) throws Exception {
		Tika t = new Tika();
		String text = t.parseToString(new URL("http://en.wikipedia.org/w/index.php?title=Building&printable=yes"));
		OpenNLPParser p = new OpenNLPParser();
		Document d = p.parseDocument(text);
		d.print();
	}
}
