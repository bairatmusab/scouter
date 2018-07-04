package org.atos.scouter.SIMetrix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

    /**
     * Main module for evaluation. The main function takes a file containing input summary mappings and returns
     *  evaluation scores for features specified in the config file.
     * @author Annie Louis
     */ 
public class InputBasedEvaluation
{

    public static final String KLInputSummary = "KLInputSummary" ;
    public static final String KLSummaryInput = "KLSummaryInput";
    public static final String unsmoothedJSD = "unsmoothedJSD";
    public static final String smoothedJSD = "smoothedJSD";
    public static final String cosineAllWords = "cosineAllWords";
    public static final String percentTopicTokens = "percentTopicTokens";
    public static final String fractionTopicWords = "fractionTopicWords";
    public static final String topicWordOverlap = "topicWordOverlap";
    public static final String unigramProb = "unigramProb";
    public static final String multinomialProb = "multinomialProb";

	private CorpusBasedUtilities cbu;

	private EvalFeatures feat;

	private LinkedList<String> featuresToCompute;

	private ConfigOptions opt;

	/**
     * Given the vocabulary distributions of an input and its summary and a list of features to compute,
     * the function returns a string containing the values of these features.
     * <br><br> For smoothing KL and JS divergence the following values are assumed for bins and gamma
     * <br> LidStone smoothing: The total possible outcomes ie, words in the vocabulary distribution 
     * are set as 1.5 times the input vocabulary size. The fractional count added to the original count 
     * for each vocabulary item is 0.005.
     */
    public HashMap<String,Double> generateFeatures(vocabDist inputDist, vocabDist summaryDist, LinkedList<String> listOfFeatures, EvalFeatures feat, CorpusBasedUtilities cbu, double topicCutoff)
    {
	// for smoothing kl and js divergence the following values are assumed for bins and gamma
	// LidStone smoothing:
        // the total possible outcomes ie, words in the vocabulary distribution are set as 1.5 times the input vocabulary size
        // the fractional count added to the original count for each vocabulary item is 0.005
	//DecimalFormat myFormatter = new DecimalFormat("0.#####");

	HashMap<String,Double> featuresScores = new HashMap<>();

	if(listOfFeatures.contains("KLInputSummary"))
	    {
		    double klarray[] = feat.getKLdivergenceSmoothed(inputDist, summaryDist, 0.005, 1.5 * inputDist.vocabWords.size());
	        featuresScores.put(KLInputSummary,klarray[0]);
	        featuresScores.put(KLSummaryInput,klarray[1]);
            featuresScores.put(unsmoothedJSD,feat.getJSDivergence(inputDist, summaryDist));
            featuresScores.put(smoothedJSD,feat.getSmoothedJSDivergence(inputDist, summaryDist, 0.005, 1.5 * inputDist.vocabWords.size()));
	    }
	if(listOfFeatures.contains("cosineAllWords"))
        featuresScores.put(cosineAllWords,cbu.computeTfIdfCosineGivenVocabDists(inputDist, summaryDist));
	if(listOfFeatures.contains("percentTopicTokens"))    
	    {
		LinkedList<String> inputTopicWords = cbu.getTopicSignatures(inputDist, topicCutoff);  
		Double percentTokensTopicWords = feat.getPercentTokensThatIsSignTerms(inputTopicWords, summaryDist);
		Double fractionTopicWordsCovered = feat.getPercentTopicWordsCoveredByGivenDist(inputTopicWords, summaryDist);
		featuresScores.put(percentTopicTokens,percentTokensTopicWords);
		featuresScores.put(fractionTopicWords,fractionTopicWordsCovered);
		LinkedList<Integer> topicWordFrequencies = new LinkedList<Integer>();
		int totalCount = 0;
		for(int tp = 0; tp < inputTopicWords.size(); tp++)
		    {
			int indtopic = inputDist.vocabWords.indexOf(inputTopicWords.get(tp));
			int freq = inputDist.vocabFreq.get(indtopic);
			topicWordFrequencies.add(freq);
			totalCount += freq;
		    }
		vocabDist inputTopicDist = new vocabDist(inputTopicWords, topicWordFrequencies, totalCount);
		Double topicOverlap = cbu.computeTfIdfCosineGivenVocabDists(inputTopicDist, summaryDist);
		featuresScores.put(topicWordOverlap,topicOverlap);
	    }
	if(listOfFeatures.contains("unigramProb"))
	    {
		Double uniprob = feat.getUnigramProbability(inputDist, summaryDist);
		Double multprob = feat.getMultinomialProbability(inputDist, summaryDist);
		featuresScores.put(unigramProb,uniprob);
		featuresScores.put(multinomialProb,multprob);
	    }
	return featuresScores;
    }
	

    /** 
     * Reads configuration options as provided in config file argument to main function
     */
    public ConfigOptions readAndStoreConfigOptions(String configFile) throws java.io.IOException, java.io.FileNotFoundException
    {
	ConfigOptions cf = new ConfigOptions();
	BufferedReader bcr = new BufferedReader(new FileReader(configFile));
	String cline;
	
	while((cline = bcr.readLine())!=null)
	    {
		cline = cline.trim();
		if(cline.equals(""))
		    continue;
		if(cline.startsWith("-"))
		    continue;
		if(cline.startsWith("="))
		    continue;
		cline = cline.replaceAll(" ","");
		String clineToks[] = cline.split("[=]");
		if(clineToks[0].equals("performStemming"))
		    {
			if(clineToks[1].equalsIgnoreCase("n"))
			    cf.performStemming = false;
		    }
		if(clineToks[0].equals("removeStopWords"))
		    {
			if(clineToks[1].equalsIgnoreCase("n"))
			    cf.removeStopWords = false;
		    }
		if(clineToks[0].equals("divergence"))
		    {
			if(clineToks[1].equalsIgnoreCase("n"))
			    cf.divergence = false;
		    }
		if(clineToks[0].equals("cosineOverlap"))
		    {
			if(clineToks[1].equalsIgnoreCase("n"))
			    cf.cosine = false;
		    }
		if(clineToks[0].equals("topicWordFeatures"))
		    {
			if(clineToks[1].equalsIgnoreCase("n"))
			    cf.topic = false;
		    }
		if(clineToks[0].equals("frequencyFeatures"))
		    {
			if(clineToks[1].equalsIgnoreCase("n"))
			    cf.summProb = false;
		    }

		if(clineToks[0].equals("stopFilePath"))
		    cf.stopFile = clineToks[1];

		if(clineToks[0].equals("backgroundCorpusFreqCounts"))
		    cf.bgCountFile = clineToks[1];

		if(clineToks[0].equals("backgroundIdfUnstemmed"))
		    cf.bgIdfUnstemmedFile = clineToks[1];
		if(clineToks[0].equals("backgroundIdfStemmed"))
		    cf.bgIdfStemmedFile = clineToks[1];

		if(clineToks[0].equals("topicWordCutoff"))
		    cf.topicCutoff = Double.parseDouble(clineToks[1]);
	    }

	if(cf.removeStopWords)
	    {
		if(cf.stopFile.equals(""))
		    error("Error in config file: must specify file with stopwords for removeStopWords=Y option");				
	    }
	else
	    cf.stopFile = "";

	if(cf.cosine)
	    {
		if(cf.bgIdfUnstemmedFile.equals("") && !cf.performStemming)
		    error("Error in config file: must specify file with idf file (unstemmed) to compute cosine overlap");				

		if(cf.bgIdfStemmedFile.equals("") && cf.performStemming)
		    error("Error in config file: must specify file with idf file (stemmed) to compute cosine overlap");				
	    }

	if(cf.topic)
	    {
		//System.out.println(cf.bgFile);
		if(cf.bgCountFile.equals(""))
		    error("Error in config file: must specify file with background corpus counts to compute topic word based features");		
		if(cf.topicCutoff <= 0.0)
		    error("Topic word cutoff must be greater than zero to be meaningful, default value is 10.0");
	    }

	// if(!(cf.cosine || cf.topic))
	//     {
	// 	cf.bgFile = "";
	// 	cf.topicCutoff = 0.0;
	//     }
	return cf;
    }

	public void init(String configFile) throws IOException {


		if(!(new File(configFile)).exists())
			error("config file does not exist: "+ configFile);

		String stopFile = "";
		String bgFile = "";
		String idfFile = "";
		opt = readAndStoreConfigOptions(configFile);
		featuresToCompute = new LinkedList<String>();
		if(opt.divergence)
		{
			featuresToCompute.add("KLInputSummary");
			featuresToCompute.add("KLSummaryInput");
			featuresToCompute.add("unsmoothedJSD");
			featuresToCompute.add("smoothedJSD");
		}
		if(opt.cosine)
			featuresToCompute.add("cosineAllWords");
		if(opt.topic)
		{
			featuresToCompute.add("percentTopicTokens");
			featuresToCompute.add("fractionTopicWords");
			featuresToCompute.add("topicWordOverlap");
		}
		if(opt.summProb)
		{
			featuresToCompute.add("unigramProb");
			featuresToCompute.add("multinomialProb");
		}
		cbu = new CorpusBasedUtilities(opt);
		feat = new EvalFeatures();
	}

	public HashMap<String,Double> runTermsEvaluation(String sourceText, String summery){

		vocabDist inputVocabDist = cbu.computeVocabularyFromText(sourceText);
		vocabDist summaryVocabDist = cbu.computeVocabularyFromText(summery);
		HashMap<String,Double> features = generateFeatures(inputVocabDist, summaryVocabDist, featuresToCompute, feat, cbu, opt.topicCutoff);
		//other tables no longer needed
		cbu.clearAll();
		//compute average feature values - sys level evaluation
		//int numFeatures = featuresToCompute.size();
		//AverageScores.computeAndWriteMacroScores(mappingsFile+".ieval.micro", 2, numFeatures+1, mappingsFile+".ieval.macro");
	    return features;
    }

    /**
     * print error and quit
     */
    public void error(String msg)
    {
	System.out.println("\n\n"+msg+"\n\n");
	Runtime cur = Runtime.getRuntime();
	cur.exit(1);
    }

}

