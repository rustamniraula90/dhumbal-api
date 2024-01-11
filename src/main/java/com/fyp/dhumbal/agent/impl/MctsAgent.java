package com.fyp.dhumbal.agent.impl;

import com.fyp.dhumbal.agent.AgentConstant;
import com.fyp.dhumbal.agent.GameAgent;
import com.fyp.dhumbal.agent.impl.ismcts.MonteCarloTreeSearch;
import com.fyp.dhumbal.agent.model.AgentMoveRequest;
import com.fyp.dhumbal.game.dal.GameEntity;
import com.fyp.dhumbal.game.dal.GameRepository;
import com.fyp.dhumbal.game.service.GameService;
import com.fyp.dhumbal.global.error.codes.ErrorCodes;
import com.fyp.dhumbal.global.error.exception.impl.BadRequestException;
import com.fyp.dhumbal.global.util.CardUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component(AgentConstant.MCTS_AGENT)
public class MctsAgent extends GameAgent {

    private final GameRepository gameRepository;

    @Value("${dhumbal.agent.randomize}")
    private boolean randomize;

    @Value("${dhumbal.game.card.count}")
    private int handCount;

    public MctsAgent(GameService gameService, GameRepository gameRepository) {
        super(gameService);
        this.gameRepository = gameRepository;
    }

    @Override
    public boolean shouldShow(AgentMoveRequest request) {
        return true;
    }

    @Override
    public List<String> getCardsToThrow(AgentMoveRequest request) {
        return buildMcts(request.getGameId(), request.getAgentId()).getNextThrow(request.getAgentLevel() * 500);
    }

    @Override
    public String getCardToPick(AgentMoveRequest request) {
        return buildMcts(request.getGameId(), request.getAgentId()).getNextChoice(request.getAgentLevel() * 500);
    }

    private MonteCarloTreeSearch buildMcts(String gameId, String agentId) {
        List<String> allCards = CardUtil.getShuffledCard();
        GameEntity game = gameRepository.findById(gameId).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Game not found"));
        int floorLength = game.getFloor().size();
        List<String> choices = game.getFloor().subList(floorLength - game.getChoiceCount(), floorLength);
        allCards.removeAll(game.getHands().get(game.getTurn()));
        allCards.removeAll(game.getFloor());
        allCards.removeAll(choices);
        allCards.removeAll(game.getTempFloor());
        return MonteCarloTreeSearch.builder()
                .players(new ArrayList<>(game.getPlayers()))
                .playerCards(new HashMap<>(game.getHands()))
                .floor(new ArrayList<>(game.getFloor()))
                .choices(new ArrayList<>(choices))
                .deck(new ArrayList<>(game.getDeck()))
                .unknownCards(allCards)
                .currentPlayer(agentId)
                .randomize(randomize)
                .handSize(handCount)
                .build();
    }
}
