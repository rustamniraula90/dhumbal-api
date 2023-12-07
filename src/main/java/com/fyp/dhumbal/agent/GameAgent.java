package com.fyp.dhumbal.agent;

import com.fyp.dhumbal.agent.model.AgentMoveRequest;
import com.fyp.dhumbal.game.rest.model.GamePickRequest;
import com.fyp.dhumbal.game.rest.model.GameThrowRequest;
import com.fyp.dhumbal.game.service.GameService;
import com.fyp.dhumbal.global.util.CardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

@RequiredArgsConstructor
public abstract class GameAgent {

    private final GameService gameService;
    protected static final String DECK = "deck";

    @Async
    public void move(AgentMoveRequest request) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String[] agent = request.getAgentId().split(AgentConstant.AGENT_ID_SEPARATOR);
        if (request.getMoveType() == AgentMoveRequest.MoveType.THROW) {
            int cardValue = CardUtil.getCardValue(request.getHand());
            if (cardValue <= 5 && shouldShow(request)) {
                gameService.endGame(request.getGameId(), request.getAgentId());
            }
            List<String> cardsToThrow = getCardsToThrow(request);
            gameService.throwCard(request.getGameId(), new GameThrowRequest(cardsToThrow), request.getAgentId(), agent[1]);
        } else {
            String cardToPick = getCardToPick(request);
            boolean deck = cardToPick.equals(DECK);
            gameService.pickCard(request.getGameId(), new GamePickRequest(!deck, deck ? 0 : request.getChoices().indexOf(cardToPick)), request.getAgentId(), agent[1]);
        }
    }

    public abstract boolean shouldShow(AgentMoveRequest request);

    public abstract List<String> getCardsToThrow(AgentMoveRequest request);

    public abstract String getCardToPick(AgentMoveRequest request);

}
