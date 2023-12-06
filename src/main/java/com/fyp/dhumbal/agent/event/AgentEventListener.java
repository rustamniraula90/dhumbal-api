package com.fyp.dhumbal.agent.event;

import com.fyp.dhumbal.agent.GameAgent;
import com.fyp.dhumbal.global.error.codes.ErrorCodes;
import com.fyp.dhumbal.global.error.exception.impl.InternalServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AgentEventListener implements ApplicationListener<AgentEventPayload> {
    private final Map<String, GameAgent> agentMap;

    @Override
    public void onApplicationEvent(AgentEventPayload event) {
        GameAgent agent = agentMap.get(event.getRequest().getAgentType());
        if (agent != null) {
            agent.move(event.getRequest());
        } else {
            throw new InternalServerException(ErrorCodes.INTERNAL_SERVER_ERROR, "Invalid agent type: " + event.getRequest().getAgentType());
        }
    }
}
