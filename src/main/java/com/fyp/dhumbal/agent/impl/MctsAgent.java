package com.fyp.dhumbal.agent.impl;

import com.fyp.dhumbal.agent.AgentConstant;
import com.fyp.dhumbal.agent.GameAgent;
import com.fyp.dhumbal.agent.model.AgentMoveRequest;
import com.fyp.dhumbal.game.service.GameService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component(AgentConstant.HARD_AGENT)
public class MctsAgent extends GameAgent {
    public MctsAgent(GameService gameService) {
        super(gameService);
    }

    @Override
    public boolean shouldShow(AgentMoveRequest request) {
        return true;
    }

    @Override
    public List<String> getCardsToThrow(AgentMoveRequest request) {
        sortCard(request.getHand());
        return Collections.singletonList(request.getHand().get(request.getHand().size() - 1));
    }

    @Override
    public String getCardToPick(AgentMoveRequest request) {
        return DECK;
    }

    private void sortCard(List<String> cards) {
        cards.sort((a, b) -> {
            String[] splitA = a.split("_");
            String[] splitB = b.split("_");
            return Integer.parseInt(splitA[1]) - Integer.parseInt(splitB[1]);
        });
    }
}
