package com.fyp.dhumbal.agent.impl.ismcts;

import com.fyp.dhumbal.agent.AgentUtil;
import com.fyp.dhumbal.global.util.CardUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
@Slf4j
public class Game {
    private static final int MAX_TURNS_SIMULATION = 1000;
    private String winner;
    private boolean hasThrown;
    private List<String> players;
    private List<String> choices;
    private List<String> tempChoices;
    private List<String> deck;
    private List<String> floor;
    private HashMap<String, List<String>> playerCards;

    public Game copy() {
        HashMap<String, List<String>> playerCardsCopy = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : this.playerCards.entrySet()) {
            playerCardsCopy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return Game.builder()
                .winner(this.winner)
                .hasThrown(this.hasThrown)
                .players(new ArrayList<>(this.players))
                .choices(new ArrayList<>(this.choices))
                .tempChoices(new ArrayList<>(this.tempChoices))
                .deck(new ArrayList<>(this.deck))
                .floor(new ArrayList<>(this.floor))
                .playerCards(playerCardsCopy)
                .build();
    }

    public List<String> getPossibleChoice() {
        List<String> possibleChoices = new ArrayList<>();
        possibleChoices.add(deck.get(deck.size() - 1));
        possibleChoices.addAll(new ArrayList<>(this.choices));
        return possibleChoices;
    }

    public List<List<String>> getPossibleMoves(String turn) {
        List<String> hand = playerCards.get(turn);
        return AgentUtil.filterValidMoves(AgentUtil.getAllPossibleMoves(hand));
    }

    public void simulatePlayOut(String turn) {
        int playCount = 0;
        while (winner == null && playCount < MAX_TURNS_SIMULATION) {
            List<String> hand = playerCards.get(turn);
            if (hasThrown) {
                if (CardUtil.getCardValue(hand) <= 5) {
                    winner = turn;
                } else {
                    if (deck.isEmpty()) {
                        deck.addAll(new ArrayList<>(floor));
                        floor = new ArrayList<>();
                    }
                    String card = AgentUtil.getCardToPick(hand, choices, deck.get(deck.size() - 1));
                    if (card.equals(AgentUtil.DECK)) {
                        card = deck.remove(deck.size() - 1);
                    } else {
                        choices.remove(card);
                    }
                    hand.add(card);
                    if (!choices.isEmpty())
                        floor.addAll(choices);
                    choices = tempChoices;
                    hasThrown = false;
                    turn = getNextPlayer(turn);
                    playCount++;
                }
            } else {
                List<String> move = AgentUtil.getCardsToThrow(hand);
                tempChoices = new ArrayList<>(move);
                hand.removeAll(move);
                hasThrown = true;
            }
        }
        if (winner == null) {
            log.info("winner not determined in " + MAX_TURNS_SIMULATION / players.size() + " turns");
            int winnerValue = 0;
            for (String player : players) {
                int value = CardUtil.getCardValue(playerCards.get(player));
                if (value > winnerValue) {
                    winnerValue = value;
                    winner = player;
                }
            }
        }
    }

    public String getNextPlayer(String turn) {
        int index = (players.indexOf(turn) + 1) % players.size();
        return players.get(index);
    }
}
