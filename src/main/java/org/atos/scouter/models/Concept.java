package org.atos.scouter.models;

import java.util.List;


/**
 * A Model Class representing the concept used build a tree of keywords defined in external source file.
 * Created by Musab on 07/04/2017.
 */
public class Concept {

    /**
     * String representing tree root.
     */
    private String root;

    /**
     * int representing root score.
     */
    private int score;

    /**
     * List of {@link KeywordNode} representing root children
     */
    List<KeywordNode> children;

    /**
     * List of strings containing different words variations possible for certain root node
     */

    List<String> variation;

    // Setters & getters
    public List<String> getVariation() {
        return variation;
    }

    public void setVariation(List<String> variation) {
        this.variation = variation;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<KeywordNode> getChildren() {
        return children;
    }

    public void setChildren(List<KeywordNode> children) {
        this.children = children;
    }
}
