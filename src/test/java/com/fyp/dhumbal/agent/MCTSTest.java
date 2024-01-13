package com.fyp.dhumbal.agent;

import com.fyp.dhumbal.agent.impl.ismcts.MonteCarloTreeSearch;
import com.fyp.dhumbal.global.util.CardUtil;
import org.junit.jupiter.api.Test;

import java.util.*;

class MCTSTest {

    @Test
    void testMCTSThrow() {
        List<String> allCard = CardUtil.getShuffledCard();
        List<String> players = new ArrayList<>();
        players.add("1");
        players.add("2");
        HashMap<String, List<String>> hands = new HashMap<>();
        hands.put("1", Arrays.asList("S_1", "S_2", "S_3", "H_12", "D_12"));
        allCard.removeAll(hands.get("1"));
        String choice = "C_12";
        allCard.remove(choice);
        List<String> choices = new ArrayList<>();
        choices.add(choice);

        MonteCarloTreeSearch mcts = MonteCarloTreeSearch.builder()
                .players(players)
                .playerCards(hands)
                .floor(new ArrayList<>())
                .choices(choices)
                .deck(allCard)
                .currentPlayer("1")
                .randomize(true)
                .handSize(5)
                .unknownCards(new ArrayList<>(allCard))
                .build();
        System.out.println(mcts.getNextThrow(1000));
        System.out.println(mcts.getNextChoice(1000));
    }

    @Test
    void testMCTSAgent() {
        MonteCarloTreeSearch mcts = buildMonteCarloTreeSearch();
        System.out.println(mcts.getNextThrow(1000));
        System.out.println(mcts.getNextChoice(1000));
    }

    private MonteCarloTreeSearch buildMonteCarloTreeSearch() {
        List<String> allCard = CardUtil.getShuffledCard();
        List<String> players = new ArrayList<>();
        HashMap<String, List<String>> hands = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            String player = UUID.randomUUID().toString();
            players.add(player);
            hands.put(player, CardUtil.getRandomCard(allCard, 7));
        }
        String choice = allCard.remove(0);
        List<String> choices = new ArrayList<>();
        choices.add(choice);

        return MonteCarloTreeSearch.builder()
                .players(players)
                .playerCards(hands)
                .floor(new ArrayList<>())
                .choices(choices)
                .deck(allCard)
                .randomize(false)
                .currentPlayer(players.get(0))
                .unknownCards(new ArrayList<>(allCard))
                .build();
    }
}
