package org.atos.scouter.models;

import java.util.List;

/**
 * Class representing tree node in the concept topology.
 * Created by Musab on 07/04/2017.
 */
public class KeywordNode {

    // Node word
    private String word;

    // Node score.
    private int score;

    // List of children
    private List<KeywordNode> children;

    // List of different words variation possible for the node
    List<String> variation;


    // Getters & Setters
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
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

    public List<String> getVariation() {
        return variation;
    }

    public void setVariation(List<String> variation) {
        this.variation = variation;
    }

    public boolean hasChildren(){
        return children.size() > 0;
    }
}
