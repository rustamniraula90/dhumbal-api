package com.fyp.dhumbal.agent.event;

import com.fyp.dhumbal.agent.model.AgentMoveRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AgentEventPayload extends ApplicationEvent {
    private final AgentMoveRequest request;

    public AgentEventPayload(Object source, AgentMoveRequest request) {
        super(source);
        this.request = request;
    }
}
