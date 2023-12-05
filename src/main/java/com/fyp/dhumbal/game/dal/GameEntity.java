package com.fyp.dhumbal.game.dal;

import jakarta.persistence.Convert;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("game")
public class GameEntity implements Serializable {
    @Id
    private String id;
    private String turn;
    private boolean thrown = false;
    private int choiceCount = 0;
    private boolean ended = false;
    private Integer endingPoint;
    private String endedBy;
    private String winner;
    private Integer winnerPoint;
    private List<String> players = new ArrayList<>();
    private List<String> deck = new ArrayList<>();
    private Map<String, String> hands = new HashMap<>();
    private List<String> floor = new ArrayList<>();
    private List<String> tempFloor = new ArrayList<>();

    public void setHands(Map<String, List<String>> hands) {
        Map<String, String> parsed = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : hands.entrySet()) {
            parsed.put(entry.getKey(), String.join(";", entry.getValue()));
        }
        this.hands = parsed;
    }

    public Map<String, List<String>> getHands() {
        Map<String, List<String>> object = new HashMap<>();
        for (Map.Entry<String, String> entry : this.hands.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty())
                object.put(entry.getKey(), new ArrayList<>(Arrays.asList(entry.getValue().split(";"))));
            else object.put(entry.getKey(), new ArrayList<>());
        }
        return object;
    }
}
