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
        log.info("Total {} simulations ran for Pick in {} milliseconds with result {} for hand {}, choice {} and deck {}", simulationCount, simulationTimeMillis, choice, playerCards.get(currentPlayer), choices, deck.get(deck.size() - 1));
        return choices.contains(choice) ? choice : AgentUtil.DECK;
    }


    public Node run(int simulationTimeMillis, boolean hasThrown) {
        Tree tree = buildTree(hasThrown);
        long endTime = System.currentTimeMillis() + simulationTimeMillis;
        while (System.currentTimeMillis() < endTime) {
            determinization(tree.getRootNode());
            Node selectedNode = selection(tree.getRootNode());
            if (selectedNode.isPlayed()) expansion(selectedNode);
            if (!selectedNode.getChildren().isEmpty())
                selectedNode = selectedNode.getRandomChild();
            String winner = simulation(selectedNode);
            backpropagation(selectedNode, winner);
            simulationCount++;
        }
        return tree.getRootNode().

                getChildWithMaxScore();
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
                game.getFloor().addAll(new ArrayList<>(game.getChoices()));
                game.setChoices(new ArrayList<>(game.getTempChoices()));
                game.setTempChoices(new ArrayList<>());
                game.setWinner(null);
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
            if (CardUtil.getCardValue(node.getGame().getPlayerCards().get(node.getTurn())) <= 5) {
                node.getGame().setWinner(node.getTurn());
            } else {
                List<List<String>> possibleMoves = node.getGame().getPossibleMoves(node.getTurn());
                for (List<String> move : possibleMoves) {
                    Game game = node.getGame().copy();
                    game.getPlayerCards().get(node.getTurn()).removeAll(move);
                    game.setTempChoices(move);
                    game.setHasThrown(true);
                    game.setWinner(null);
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

    }

    public String simulation(Node node) {
        Game game = node.getGame().copy();
        game.simulatePlayOut(node.getTurn());
        return game.getWinner();
    }

    public void backpropagation(Node node, String winner) {
        node.setPlayed(true);
        Node tempNode = node;
        while (tempNode != null) {
            tempNode.setVisits(tempNode.getVisits() + 1);
            if (winner != null) {
                if (tempNode.getTurn().equals(winner)) {
                    tempNode.setScore(tempNode.getScore() + 1);
                } else {
                    tempNode.setScore(tempNode.getScore() - 1);
                }
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
                .played(true)
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
