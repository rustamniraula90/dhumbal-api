package com.fyp.dhumbal.game.dal;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("game")
public class GameEntity implements Serializable {
    @Id
    private String id;
    private String turn;
    private Boolean thrown = false;
    private Integer choiceCount = 0;
    private Boolean ended = false;
    private Integer endingPoint;
    private String endedBy;
    private String winner;
    private Integer winnerPoint;
    private List<String> players;
    private List<String> deck;
    private Map<String, List<String>> hands;
    private List<String> floor;
}
