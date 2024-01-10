package com.fyp.dhumbal.agent;

import com.fyp.dhumbal.agent.model.AgentMoveRequest;
import com.fyp.dhumbal.game.rest.model.GamePickRequest;
import com.fyp.dhumbal.game.rest.model.GameThrowRequest;
import com.fyp.dhumbal.game.service.GameService;
import com.fyp.dhumbal.global.util.CardUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class GameAgent {

    private final GameService gameService;
    protected static final String DECK = "deck";

    @Async
    public void move(AgentMoveRequest request) {
        if (request.getMoveType() == AgentMoveRequest.MoveType.THROW) {
            int cardValue = CardUtil.getCardValue(request.getHand());
            if (cardValue <= 5 && shouldShow(request)) {
                gameService.endGame(request.getGameId(), request.getAgentId());
                return;
            }
            List<String> cardsToThrow = getCardsToThrow(request);
            gameService.throwCard(request.getGameId(), new GameThrowRequest(cardsToThrow), request.getAgentId(), request.getAgentName());
        } else {
            String cardToPick = getCardToPick(request);
            boolean deck = cardToPick.equals(DECK);
            gameService.pickCard(request.getGameId(), new GamePickRequest(deck, deck ? 0 : request.getChoices().indexOf(cardToPick)), request.getAgentId(), request.getAgentName());
        }
    }

    public abstract boolean shouldShow(AgentMoveRequest request);

    public abstract List<String> getCardsToThrow(AgentMoveRequest request);

    public abstract String getCardToPick(AgentMoveRequest request);

}
