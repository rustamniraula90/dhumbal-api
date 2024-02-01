package com.fyp.dhumbal.agent.model;

import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AgentMoveRequest implements Serializable {
    private String agentName;
    private String agentId;
    private Integer agentLevel;
    private String gameId;
    private MoveType moveType;
    private List<String> players;
    private List<String> hand;
    private List<String> choices;
    private Map<String, Integer> playersHandSize;
    private List<String> floor;
    private List<String> tempFloor;

    public enum MoveType {
        THROW, PICK
    }
}
