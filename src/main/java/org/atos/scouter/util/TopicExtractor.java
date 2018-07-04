package org.atos.scouter.util;

import org.atos.scouter.KeaTopicExtraction.main.KEAKeyphraseExtractor;
import org.atos.scouter.KeaTopicExtraction.main.KEAModelBuilder;
import org.atos.scouter.KeaTopicExtraction.stemmers.FrenchStemmer;
import org.atos.scouter.KeaTopicExtraction.stemmers.PorterStemmer;
import org.atos.scouter.KeaTopicExtraction.stemmers.SpanishStemmer;
import org.atos.scouter.KeaTopicExtraction.stopwords.StopwordsEnglish;
import org.atos.scouter.KeaTopicExtraction.stopwords.StopwordsFrench;
import org.atos.scouter.KeaTopicExtraction.stopwords.StopwordsSpanish;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by Musab on 10/05/2017.
 */
public class TopicExtractor {

    private KEAModelBuilder km;
    private KEAKeyphraseExtractor ke;

    private static final PropertiesManager PROPERTIES_MANAGER = PropertiesManager.getInstance();

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicExtractor.class);

    public static final TopicExtractor topicExtractor = new TopicExtractor();

    private String trainingDirectory ;
    private String languange;

    private String trainingModelPath = "scouter/src/main/resources/topic-extraction-model";

    public static TopicExtractor getInstance(){
        return topicExtractor;
    }

    public TopicExtractor(){
        try {
            this.trainingDirectory = PROPERTIES_MANAGER.getProperty("kea.topic.trainDirectory");
            this.languange = PROPERTIES_MANAGER.getProperty("kea.topic.lang");
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid configuration {} ", e);
            throw new IllegalStateException("Invalid configuration");
        }
    }

    private void setOptionsTraining() {

        km = new KEAModelBuilder();

        // A. required arguments (no defaults):

        // 1. Name of the directory -- give the path to your directory with documents and keyphrases
        //    documents should be in txt format with an extention "txt"
        //    keyphrases with the same name as documents, but extension "key"
        //    one keyphrase per line!
        km.setDirName(this.trainingDirectory);

        // 2. Name of the model -- give the path to where the model is to be stored and its name
        km.setModelName(trainingModelPath);

        // 3. Name of the vocabulary -- name of the file (without extension) that is stored in VOCABULARIES
        //    or "none" if no Vocabulary is used (free keyphrase extraction).
        km.setVocabulary("none");

        // 4. Format of the vocabulary in 3. Leave empty if vocabulary = "none", use "skos" or "txt" otherwise.
        //km.setVocabularyFormat("skos");

//		 B. optional arguments if you want to change the defaults
        // 5. Encoding of the document
        km.setEncoding("UTF-8");

        // 6. Language of the document -- use "es" for Spanish, "fr" for French
        //    or other languages as specified in your "skos" vocabulary
        km.setDocumentLanguage(this.languange); // es for Spanish, fr for French

        // 7. Stemmer -- adjust if you use a different language than English or if you want to alterate results
        // (We have obtained better results for Spanish and French with NoStemmer)
        if (this.languange.equals("fr")){
            km.setStemmer(new FrenchStemmer());
            km.setStopwords(new StopwordsFrench());
        }else if (this.languange.equals("es")){
            km.setStemmer(new SpanishStemmer());
            km.setStopwords(new StopwordsSpanish());
        }else {
            km.setStemmer(new PorterStemmer());
            km.setStopwords(new StopwordsEnglish());
        }


        // 9. Maximum length of a keyphrase
        km.setMaxPhraseLength(5);

        // 10. Minimum length of a keyphrase
        km.setMinPhraseLength(1);

        // 11. Minumum occurrence of a phrase in the document -- use 2 for long documents!
        km.setMinNumOccur(2);

        //  Optional: turn off the keyphrase frequency feature
        //	km.setUseKFrequency(false);

    }


    private void setOptionsEvaluation() {

        ke = new KEAKeyphraseExtractor();

        // A. required arguments (no defaults):

        // 1. Name of the directory -- give the path to your directory with documents
        //    documents should be in txt format with an extention "txt".
        //    Note: keyphrases with the same name as documents, but extension "key"
        //    one keyphrase per line!


        // 2. Name of the model -- give the path to the model
        ke.setModelName(trainingModelPath);

        // 3. Name of the vocabulary -- name of the file (without extension) that is stored in VOCABULARIES
        //    or "none" if no Vocabulary is used (free keyphrase extraction).
        //ke.setVocabulary("agrovoc");
        ke.setVocabulary("none");

        // 4. Format of the vocabulary in 3. Leave empty if vocabulary = "none", use "skos" or "txt" otherwise.
        //ke.setVocabularyFormat("skos");

//		 B. optional arguments if you want to change the defaults
        // 5. Encoding of the document
        ke.setEncoding("UTF-8");

        // 6. Language of the document -- use "es" for Spanish, "fr" for French
        //    or other languages as specified in your "skos" vocabulary
        ke.setDocumentLanguage(this.languange); // es for Spanish, fr for French

        // 7. Stemmer -- adjust if you use a different language than English or want to alterate results
        // (We have obtained better results for Spanish and French with NoStemmer)
        if (this.languange.equals("fr")){
            ke.setStemmer(new FrenchStemmer());
            ke.setStopwords(new StopwordsFrench());
        }else if (this.languange.equals("es")){
            ke.setStemmer(new SpanishStemmer());
            ke.setStopwords(new StopwordsSpanish());
        }else {
            ke.setStemmer(new PorterStemmer());
            ke.setStopwords(new StopwordsEnglish());
        }

        // 9. Number of Keyphrases to extract
        ke.setNumPhrases(10);

        // 10. Set to true, if you want to compute global dictionaries from the test collection
        ke.setBuildGlobal(false);


    }

    private void createModel() {
        try {
            km.buildModel(km.collectStems());
            km.saveModel();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ArrayList<String> extractKeyphrasesFromText(String input) {
        try {
            //ke.loadModel();
            //ke.extractKeyphrases(ke.collectStems());
            return ke.extractKeyphrasesFromText(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void trainTopicExtractionModel() throws Exception {
        setOptionsTraining();
        createModel();
        setOptionsEvaluation();
        ke.loadModel();
    }

    public ArrayList<String> extractPhrases(String input){
        return extractKeyphrasesFromText(input);
    }

}
