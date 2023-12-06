package com.fyp.dhumbal.agent.model;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AgentMoveRequest implements Serializable {
    private String agentId;
    private String agentType;
    private String gameId;
    private MoveType moveType;
    private List<String> hand;
    private List<String> choices;

    public enum MoveType {
        THROW, PICK
    }
}
