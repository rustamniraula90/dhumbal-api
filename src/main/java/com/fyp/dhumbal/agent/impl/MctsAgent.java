package com.fyp.dhumbal.agent.impl;

import com.fyp.dhumbal.agent.AgentConstant;
import com.fyp.dhumbal.agent.GameAgent;
import com.fyp.dhumbal.agent.impl.ismcts.MonteCarloTreeSearch;
import com.fyp.dhumbal.agent.model.AgentMoveRequest;
import com.fyp.dhumbal.game.dal.GameRepository;
import com.fyp.dhumbal.game.service.GameService;
import com.fyp.dhumbal.global.util.CardUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component(AgentConstant.MCTS_AGENT)
public class MctsAgent extends GameAgent {

    public MctsAgent(GameService gameService, GameRepository gameRepository) {
        super(gameService);
    }

    @Override
    public boolean shouldShow(AgentMoveRequest request) {
        return true;
    }

    @Override
    public List<String> getCardsToThrow(AgentMoveRequest request) {
        return buildMcts(request).getNextThrow(request.getAgentLevel() * 500);
    }

    @Override
    public String getCardToPick(AgentMoveRequest request) {
        return buildMcts(request).getNextChoice(request.getAgentLevel() * 500);
    }

    private MonteCarloTreeSearch buildMcts(AgentMoveRequest request) {
        List<String> allCards = CardUtil.getShuffledCard();
        allCards.removeAll(request.getHand());
        allCards.removeAll(request.getFloor());
        allCards.removeAll(request.getChoices());
        allCards.removeAll(request.getTempFloor());
        HashMap<String ,List<String>> playerCards = new HashMap<>();
        for (String player : request.getPlayers()) {
            if(player.equals(request.getAgentId()))
                playerCards.put(player, new ArrayList<>(request.getHand()));
            else
                playerCards.put(player, new ArrayList<>());
        }
        return MonteCarloTreeSearch.builder()
                .players(new ArrayList<>(request.getPlayers()))
                .playerCards(playerCards)
                .floor(new ArrayList<>(request.getFloor()))
                .choices(new ArrayList<>(request.getChoices()))
                .currentPlayer(request.getAgentId())
                .playerHandSize(request.getPlayersHandSize())
                .build();
    }
}
