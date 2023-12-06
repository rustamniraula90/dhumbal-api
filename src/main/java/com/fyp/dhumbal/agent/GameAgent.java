package com.fyp.dhumbal.agent;

import com.fyp.dhumbal.agent.model.AgentMoveRequest;
import com.fyp.dhumbal.game.rest.model.GamePickRequest;
import com.fyp.dhumbal.game.rest.model.GameThrowRequest;
import com.fyp.dhumbal.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

@RequiredArgsConstructor
public abstract class GameAgent {

    private final GameService gameService;
    protected static final String DECK = "deck";

    @Async
    public void move(AgentMoveRequest request) {
        String[] agent = request.getAgentId().split(";");
        int cardValue = getCardValue(request.getHand());
        if (cardValue <= 5 && shouldShow(request)) {
            gameService.endGame(request.getGameId(), agent[0]);
        } else {
            if (request.getMoveType() == AgentMoveRequest.MoveType.THROW) {
                List<String> cardsToThrow = getCardsToThrow(request);
                gameService.throwCard(request.getGameId(), new GameThrowRequest(cardsToThrow), request.getAgentId(), agent[1]);
            } else {
                String cardToPick = getCardToPick(request);
                boolean deck = cardToPick.equals(DECK);
                gameService.pickCard(request.getGameId(), new GamePickRequest(!deck, deck ? 0 : request.getChoices().indexOf(cardToPick)), request.getAgentId(), agent[1]);
            }
        }
    }

    public abstract boolean shouldShow(AgentMoveRequest request);

    public abstract List<String> getCardsToThrow(AgentMoveRequest request);

    public abstract String getCardToPick(AgentMoveRequest request);

    private int getCardValue(List<String> cards) {
        int sum = 0;
        for (String card : cards) {
            String[] split = card.split("_");
            sum += Integer.parseInt(split[1]);
        }
        return sum;
    }

}
