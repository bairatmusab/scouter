
package org.atos.scouter.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class provides only static method {@link RulesReader#parseJSONRules(String)}
 * @version 1.0
 */
public class RulesReader {
    /**
     * Logger used to log all information in this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RulesReader.class);

    /**
     * Private constructor to block instantiation
     */
    private RulesReader() {

    }

    /**
     * Parse file
     *
     * @param filename as JSON to parse
     * @return Return a map that contains Json elements within rules.json
     * @throws NullPointerException if filename is null
     */
    public static Map<String, Integer> parseJSONRules(String filename) {
        Objects.requireNonNull(filename);
        Map<String, Integer> map = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(filename);
        JsonNode root;
        try {
            root = objectMapper.readTree(file);
            JsonNode keywordNode = root.path("keyword");
            if (keywordNode.isArray()) {
                for (JsonNode knode : keywordNode) {
                    JsonNode wordNode = knode.path("word");
                    int scoreNode = knode.path("score").asInt();
                    map.put(wordNode.asText(), scoreNode);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Rules file does not exist\n" + e.getMessage());
        }
        return map;
    }
}

