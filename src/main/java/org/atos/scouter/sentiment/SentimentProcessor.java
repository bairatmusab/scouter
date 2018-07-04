package org.atos.scouter.sentiment;

public class SentimentProcessor implements ISentimentProcessor {


	private SentimentAnalyzer analyzer;


	public SentimentProcessor(){
		analyzer = new SentimentAnalyzer();
	}


	@Override
	public String analyzeSentiment(String sentence) {
		return analyzer.analyze(sentence);
	}
}
