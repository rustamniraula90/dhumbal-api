package com.fyp.dhumbal.agent.impl;

import com.fyp.dhumbal.agent.AgentConstant;
import com.fyp.dhumbal.agent.GameAgent;
import com.fyp.dhumbal.agent.model.AgentMoveRequest;
import com.fyp.dhumbal.game.service.GameService;
import com.fyp.dhumbal.global.error.exception.impl.BadRequestException;
import com.fyp.dhumbal.global.util.CardUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component(AgentConstant.BASIC_AGENT)
public class RuleBasedAgent extends GameAgent {
    public RuleBasedAgent(GameService gameService) {
        super(gameService);
    }

    @Override
    public boolean shouldShow(AgentMoveRequest request) {
        return true;
    }

    @Override
    public List<String> getCardsToThrow(AgentMoveRequest request) {
        List<List<String>> possibleMoves = getAllPossibleMoves(request.getHand());
        return getMaxValueMove(possibleMoves);
    }

    @Override
    public String getCardToPick(AgentMoveRequest request) {
        for (String choice : request.getChoices()) {
            List<String> newHand = new ArrayList<>(request.getHand());
            newHand.add(choice);
            int value = CardUtil.getCardValue(newHand);
            if (value <= 5) {
                return choice;
            }
        }
        List<List<String>> possibleMoves = getAllPossibleMoves(request.getHand());
        int max = 0;
        String pick = DECK;
        for (List<String> move : possibleMoves) {
            try {
                for (String choice : request.getChoices()) {
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

    private List<String> getMaxValueMove(List<List<String>> possibleMoves) {
        int max = 0;
        List<String> maxMove = new ArrayList<>();
        for (List<String> move : possibleMoves) {
            try {
                CardUtil.validateThrownCard(move);
                int value = CardUtil.getCardValue(move);
                if (value > max) {
                    max = value;
                    maxMove = move;
                }
            } catch (BadRequestException e) {
                // do nothing
            }
        }
        return maxMove;
    }

    private List<List<String>> getAllPossibleMoves(List<String> hand) {
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
}
