package com.fyp.dhumbal.agent;

import com.fyp.dhumbal.global.error.exception.impl.BadRequestException;
import com.fyp.dhumbal.global.util.CardUtil;

import java.util.ArrayList;
import java.util.List;

public class AgentUtil {

    public static final String DECK = "DECK";

    public static List<String> getCardsToThrow(List<String> hand) {
        List<List<String>> possibleMoves = AgentUtil.filterValidMoves(AgentUtil.getAllPossibleMoves(hand));
        return AgentUtil.getMaxValueMove(possibleMoves);
    }

    public static String getCardToPick(List<String> hands, List<String> choices, String deck) {
        choices.add(deck);
        String pick = getCardToPick(hands, choices);
        if (pick.equals(deck)) {
            return DECK;
        }
        return pick;
    }

    public static String getCardToPick(List<String> hands, List<String> choices) {
        for (String choice : choices) {
            List<String> newHand = new ArrayList<>(hands);
            newHand.add(choice);
            int value = CardUtil.getCardValue(newHand);
            if (value <= 5) {
                return choice;
            }
        }
        List<List<String>> possibleMoves = AgentUtil.getAllPossibleMoves(hands);
        int max = 0;
        String pick = DECK;
        for (List<String> move : possibleMoves) {
            try {
                for (String choice : choices) {
                    List<String> newMove = new ArrayList<>(move);
                    newMove.add(choice);
                    CardUtil.validateThrownCard(newMove);
                    int value = CardUtil.getCardValue(newMove);
                    if (value > max) {
                        max = value;
                        pick = choice;
                    }
                }
            } catch (BadRequestException e) {
                // do nothing
            }
        }
        return pick;
    }

    public static List<String> getMaxValueMove(List<List<String>> possibleMoves) {
        int max = 0;
        List<String> maxMove = new ArrayList<>();
        for (List<String> move : possibleMoves) {
            int value = CardUtil.getCardValue(move);
            if (value > max) {
                max = value;
                maxMove = move;
            }
        }
        return maxMove;
    }

    public static List<List<String>> getAllPossibleMoves(List<String> hand) {
        List<List<String>> allPossibleMoves = new ArrayList<>();
        for (int i = 0; hand.size() >= 2 && i < hand.size(); i++) {
            List<String> oneCombination = new ArrayList<>();
            oneCombination.add(hand.get(i));
            allPossibleMoves.add(oneCombination);
            for (int j = i + 1; hand.size() >= 3 && j < hand.size(); j++) {
                List<String> twoCombination = new ArrayList<>();
                twoCombination.add(hand.get(i));
                twoCombination.add(hand.get(j));
                allPossibleMoves.add(twoCombination);
                for (int k = j + 1; hand.size() >= 4 && k < hand.size(); k++) {
                    List<String> threeCombination = new ArrayList<>();
                    threeCombination.add(hand.get(i));
                    threeCombination.add(hand.get(j));
                    threeCombination.add(hand.get(k));
                    allPossibleMoves.add(threeCombination);
                    for (int l = k + 1; hand.size() == 5 && l < hand.size(); l++) {
                        List<String> fourCombination = new ArrayList<>();
                        fourCombination.add(hand.get(i));
                        fourCombination.add(hand.get(j));
                        fourCombination.add(hand.get(k));
                        fourCombination.add(hand.get(l));
                        allPossibleMoves.add(fourCombination);
                    }
                }
            }
        }
        allPossibleMoves.add(hand);
        return allPossibleMoves;
    }

    public static List<List<String>> filterValidMoves(List<List<String>> moves) {
        List<List<String>> validMoves = new ArrayList<>();
        for (List<String> move : moves) {
            try {
                CardUtil.validateThrownCard(move);
                validMoves.add(move);
            } catch (BadRequestException e) {
                // do nothing
            }
        }
        return validMoves;
    }
}
