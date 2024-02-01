package com.fyp.dhumbal.agent.impl.ismcts;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Random;

@Builder
@Getter
@Setter
public class Node {
    private int level;
    private boolean played;
    private Game game;
    private String turn;
    private Node parent;
    private List<Node> children;
    private String choice;
    private List<String> move;
    private int visits;
    private int score;
    private final Random random = new Random();

    public Node getChildWithMaxScore() {
        Node maxChild = null;
        int maxScore = Integer.MIN_VALUE;
        for (Node child : children) {
            if (child.getScore() > maxScore) {
                maxScore = child.getScore();
                maxChild = child;
            }
        }
        return maxChild;
    }

    public Node getRandomChild() {
        return children.get(random.nextInt(children.size()));
    }
}
