package org.atos.scouter.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atos.scouter.models.Concept;
import org.atos.scouter.models.KeywordNode;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Musab on 07/04/2017.
 */
public class ConceptTopologyUtility {

    /**
     * Properties of this class
     *
     * @see PropertiesManager
     * @see PropertiesManager#getProperty(String)
     * @see PropertiesManager#getInstance()
     */
    private static final PropertiesManager PROPERTIES_MANAGER = PropertiesManager.getInstance();

    /**
     * Jackson mapper to convert to Json and vice versa
     */
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Logger used to log all information in this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConceptTopologyUtility.class);

    /**
     * List of {@link Concept} to apply operations on.
     */
    private static List<Concept> concepts;

    private static Model model ;

    /**
     * A hashmap containing each word in the tree including variations with it's score
     */
    private static Map<String, Integer> wordsWeights;

    private static List<String> keywords;

    /**
     * Public constructor
     */
    public ConceptTopologyUtility(){

    }

    /**
     * Read concepts from a resource file
     * @return List of {@link Concept}
     */
    private static List<Concept> readConcepts(){

        try {
            String ontologyAsString = PROPERTIES_MANAGER.getProperty("keywords.ontology");
            List<HashMap<String,Objects>> jsonList = mapper.readValue(ontologyAsString,List.class);
            concepts = new ArrayList<>();
            for(HashMap<String,Objects> node : jsonList){
                Concept concept = mapper.convertValue(node,Concept.class);
                concepts.add(concept);
            }
            return  concepts;
        } catch (IOException e) {
            LOGGER.error("Exception in reading keywords json {}",e.getMessage());
        }

        return null;
    }

    /**
     * Method to retrieve all words from the concept tree.
     * @return list of string keywords
     */
    private static List<String> extractAllKeywords(){

        Objects.requireNonNull(concepts);
        List<String> keywords = new ArrayList<>();

        // TODO: enhance reading recursuing
        for(Concept con : concepts){
            keywords.add(con.getRoot());
            keywords.addAll(con.getVariation());
            for(KeywordNode node : con.getChildren()){
                keywords.add(node.getWord());
                keywords.addAll(node.getVariation());
            }
        }
        return keywords;
    }

    /**
     * Method to get the topologies, read it if its not read yet.
     * @return List of {@link Concept}
     */
    private static List<Concept> getConcepts(){
        return concepts;
    }

    /**
     * Method to extract words with weights as a hashmap
     * @return HashMap including words and weights
     */
    private static Map<String,Integer> getWordsWieghtsMap(){
        if(wordsWeights == null){
            if(concepts == null){
                concepts = getConcepts();
            }

            Map<String,Integer> weights = new HashMap<>();

            // TODO: enhance reading recursuing for more than two levels
            for(Concept con : concepts){
                weights.put(con.getRoot(),con.getScore());
                for(String v : con.getVariation()){
                    weights.put(v,con.getScore());
                }
                for(KeywordNode node : con.getChildren()){
                    weights.put(node.getWord(),node.getScore());
                    for(String v : node.getVariation()){
                        weights.put(v,node.getScore());
                    }
                }
            }
            wordsWeights = weights;
        }
        return wordsWeights;
    }

    public  static void initilizeInputTopology(){
        String ontologyStr = PROPERTIES_MANAGER.getProperty("keywords.ontology");
        readOntology(ontologyStr);

    }

    private static void readOntology(String ontologyStr){

        // if ontology is received as string, save it to local file and then load it.
        String tempFilePath = "scouter/src/main/resources/ontology-temp.jsonld";

        File file = new File(tempFilePath);
        try {
            FileUtils.write(file,ontologyStr,"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (ontologyStr == null) {
            model = RDFDataMgr.loadModel("scouter/src/main/resources/ontology.jsonld", RDFLanguages.JSONLD);
        }else {
            model = RDFDataMgr.loadModel(tempFilePath, RDFLanguages.JSONLD);
        }



        StmtIterator infStmts = model.listStatements(); //.filterKeep(new Filter<Statement>() {
//            @Override
//            public boolean accept(Statement o) {
//                boolean ex = false;
//                Property prop1 = model.getProperty("http://webprotege.stanford.edu/project/9B17WGAEvuCiy1ZShWSMPu#label");
//                String predicateName  = o.asTriple().getPredicate().getLocalName();
//                if(predicateName.equalsIgnoreCase(prop1.getLocalName()) )
//                    ex = true;
//                return ex;
//
//            }
//        });


        ArrayList<String> words = new ArrayList<>();
        ArrayList<Integer> scores = new ArrayList<>();

        while(infStmts.hasNext()){
            Statement s = infStmts.next();
            //Statement st = ResourceFactory.createStatement(s.getSubject(), s.getPredicate(), s.getObject());
            //System.out.println(st.getSubject().toString() + "****" + model.getRDFNode(st.getSubject().asNode()));
            RDFNode node = s.getObject();
            if (node.isLiteral()) {
                Literal lit = node.asLiteral();
                if (lit.getValue() instanceof  String){
                    words.add((String)lit.getValue());
                }else if(lit.getValue() instanceof  Integer){
                    scores.add((Integer)lit.getValue());
                }
            }
        }

        // remove root concept
        if (words.contains("concept")){
            words.remove("concept");
        }
        if (words.contains("score")){
            words.remove("score");
        }

        wordsWeights = new HashMap<>();
        for (int i = 0 ; i < words.size(); i++){
            wordsWeights.put(words.get(i),scores.get(i));
        }
        keywords = words;

    }


    public static Map<String, Integer> getWordsWeights() {
        return wordsWeights;
    }

    public static List<String> getKeywords() {
        return keywords;
    }

    public static void printKeywordsScoresMap(){
        System.out.println("keyword,score");
        for(Map.Entry<String,Integer> entry : getWordsWeights().entrySet()){
            System.out.println(entry.getKey() + "," + entry.getValue());
        }
    }
}
