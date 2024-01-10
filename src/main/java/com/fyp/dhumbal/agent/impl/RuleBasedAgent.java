package com.fyp.dhumbal.agent.impl;

import com.fyp.dhumbal.agent.AgentConstant;
import com.fyp.dhumbal.agent.AgentUtil;
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
        return AgentUtil.getCardsToThrow(request.getHand());
    }

    @Override
    public String getCardToPick(AgentMoveRequest request) {
        return AgentUtil.getCardToPick(request.getHand(), request.getChoices());
    }
}
