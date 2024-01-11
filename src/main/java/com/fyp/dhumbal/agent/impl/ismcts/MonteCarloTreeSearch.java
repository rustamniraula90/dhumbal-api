package com.fyp.dhumbal.agent.impl.ismcts;

import com.fyp.dhumbal.agent.AgentUtil;
import com.fyp.dhumbal.global.util.CardUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Builder
@Getter
@Slf4j
public class MonteCarloTreeSearch {
    private List<String> players;
    private String currentPlayer;
    private HashMap<String, List<String>> playerCards;
    private List<String> choices;
    private List<String> deck;
    private List<String> floor;
    private List<String> unknownCards;
    private boolean randomize;
    private int handSize;

    private int simulationCount;


    public List<String> getNextThrow(int simulationTimeMillis) {
        List<String> moves = run(simulationTimeMillis, false).getMove();
        log.info("Total {} simulations ran for Throw in {} milliseconds with result {} for hand {} and choice {}", simulationCount, simulationTimeMillis, moves, playerCards.get(currentPlayer), choices);
        return moves;
    }

    public String getNextChoice(int simulationTimeMillis) {
        String choice = run(simulationTimeMillis, true).getChoice();
        log.info("Total {} simulations ran for Pick in {} milliseconds with result {} for hand {} and choice {}", simulationCount, simulationTimeMillis, choice, playerCards.get(currentPlayer), choices);
        return choices.contains(choice) ? choice : AgentUtil.DECK;
    }


    public Node run(int simulationTimeMillis, boolean hasThrown) {
        Tree tree = buildTree(hasThrown);
        long endTime = System.currentTimeMillis() + simulationTimeMillis;
        while (System.currentTimeMillis() < endTime) {
            determinization(tree.getRootNode());
            Node selectedNode = selection(tree.getRootNode());
            if (selectedNode.getChildren().isEmpty()) {
                expansion(selectedNode);
            }
            Node nodeToExplore = selectedNode.getRandomChild();
            String winner = simulation(nodeToExplore);
            backpropagation(nodeToExplore, winner);
            simulationCount++;
        }
        return tree.getRootNode().getChildWithMaxScore();
    }

    public void determinization(Node node) {
        if (randomize) {
            List<String> unknownCardsCopy = new ArrayList<>(unknownCards);
            for (String player : players) {
                if (!player.equals(node.getTurn())) {
                    playerCards.put(player, CardUtil.getRandomCard(unknownCardsCopy, handSize));
                }
            }
            deck = new ArrayList<>(unknownCardsCopy);
        }
    }

    public Node selection(Node rootNode) {
        Node node = rootNode;
        while (!node.getChildren().isEmpty()) {
            node = UCT.findBestNodeWithUCT(node);
        }
        return node;
    }

    public void expansion(Node node) {
        if (node.getGame().isHasThrown()) {
            if (node.getGame().getDeck().isEmpty()) {
                node.getGame().setDeck(new ArrayList<>(node.getGame().getFloor()));
                node.getGame().setFloor(new ArrayList<>());
            }
            List<String> possibleChoices = node.getGame().getPossibleChoice();
            for (String choice : possibleChoices) {
                Game game = node.getGame().copy();
                game.getChoices().remove(choice);
                game.getDeck().remove(choice);
                game.getPlayerCards().get(node.getTurn()).add(choice);
                game.setHasThrown(false);
                game.getFloor().addAll(choices);
                game.setChoices(game.getTempChoices());
                Node newNode = Node.builder()
                        .parent(node)
                        .children(new ArrayList<>())
                        .turn(game.getNextPlayer(node.getTurn()))
                        .game(game)
                        .choice(choice)
                        .score(0)
                        .visits(0)
                        .build();
                node.getChildren().add(newNode);
            }
        } else {
            List<List<String>> possibleMoves = node.getGame().getPossibleMoves(node.getTurn());
            for (List<String> move : possibleMoves) {
                Game game = node.getGame().copy();
                game.getPlayerCards().get(node.getTurn()).removeAll(move);
                game.setTempChoices(move);
                game.setHasThrown(true);
                Node newNode = Node.builder()
                        .parent(node)
                        .children(new ArrayList<>())
                        .turn(node.getTurn())
                        .game(game)
                        .move(move)
                        .score(0)
                        .visits(0)
                        .build();
                node.getChildren().add(newNode);
            }
        }

    }

    public String simulation(Node node) {
        Game game = node.getGame().copy();
        game.simulatePlayOut(node.getTurn());
        return game.getWinner();
    }

    public void backpropagation(Node node, String winner) {
        Node tempNode = node;
        while (tempNode != null) {
            tempNode.setVisits(tempNode.getVisits() + 1);
            if (currentPlayer.equals(winner)) {
                int winScore = 0;
                for (String player : players) {
                    if (!player.equals(winner)) {
                        winScore += CardUtil.getCardValue(tempNode.getGame().getPlayerCards().get(player));
                    }
                }
                tempNode.setScore(tempNode.getScore() + winScore);
            }
            tempNode = tempNode.getParent();
        }
    }

    private Tree buildTree(boolean hasThrown) {
        Game game = Game.builder()
                .choices(choices)
                .tempChoices(new ArrayList<>())
                .deck(deck)
                .floor(floor)
                .playerCards(playerCards)
                .players(players)
                .hasThrown(hasThrown)
                .build();
        Node rootNode = Node.builder()
                .parent(null)
                .children(new ArrayList<>())
                .turn(currentPlayer)
                .game(game)
                .score(0)
                .visits(0)
                .build();
        return Tree.builder().rootNode(rootNode).build();
    }
}
