//package com.fyp.dhumbal.agent;
//
//import com.fyp.dhumbal.agent.impl.ismcts.MonteCarloTreeSearch;
//import com.fyp.dhumbal.global.util.CardUtil;
//import lombok.Getter;
//import lombok.Setter;
//import org.junit.jupiter.api.Test;
//
//import java.util.*;
//
//class BotBattleTest {
//
//    private static final Integer THREAD_COUNT = 5;
//    private static final Integer GAME_COUNT = 10;
//    private static final Map<String, Integer> PLAYER_LEVEL = new HashMap<>();
//
//    static {
//        PLAYER_LEVEL.put("MCTS-1", 3);
//        PLAYER_LEVEL.put("BASIC-1", 0);
//    }
//
//    static class BattleThreadPool {
//
//        private final Integer poolSize;
//        private final List<BattleThread> battleThreads;
//
//
//        public BattleThreadPool(int poolSize) {
//            this.poolSize = poolSize;
//            this.battleThreads = new ArrayList<>();
//        }
//
//        public boolean available() {
//            return battleThreads.size() < poolSize;
//        }
//
//        public BattleThread get(List<String> players, Map<String, Integer> level) {
//            BattleThread battleThread = new BattleThread(players, level);
//            battleThreads.add(battleThread);
//            return battleThread;
//        }
//
//        public void release(BattleThread battleThread) {
//            battleThreads.remove(battleThread);
//        }
//
//    }
//
//    static class BattleThread extends Thread {
//
//        @Getter
//        private String winner;
//
//        @Getter
//        private int round;
//
//        @Getter
//        @Setter
//        private boolean isDone;
//
//        private final List<String> players;
//        private final Map<String, Integer> level;
//
//        public BattleThread(List<String> players, Map<String, Integer> level) {
//            this.players = players;
//            this.level = level;
//        }
//
//        @Override
//        public void run() {
//            this.winner = battle();
//        }
//
//        private String battle() {
//            List<String> allCard = CardUtil.getShuffledCard();
//            HashMap<String, List<String>> playerCards = new HashMap<>();
//            for (String player : players) {
//                playerCards.put(player, CardUtil.getRandomCard(allCard, 5));
//            }
//            HashMap<String, List<String>> playerCardsCopy = new HashMap<>();
//            for (Map.Entry<String, List<String>> entry : playerCards.entrySet()) {
//                playerCardsCopy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
//            }
//
//            List<String> choices = new ArrayList<>();
//            choices.add(allCard.remove(0));
//
//            List<String> deck = new ArrayList<>(allCard);
//            List<String> floor = new ArrayList<>();
//            List<String> tempChoices = new ArrayList<>();
//
//            String turn = players.get(0);
//            while (true) {
//                if (CardUtil.getCardValue(playerCards.get(turn)) <= 5) {
//                    int min = CardUtil.getCardValue(playerCards.get(turn));
//                    for (String player : players) {
//                        int temp = CardUtil.getCardValue(playerCards.get(player));
//                        if (temp < min) {
//                            min = temp;
//                            turn = player;
//                        }
//                    }
//                    break;
//                }
//                List<String> move;
//                if (turn.startsWith("MCTS")) {
//                    Map<String, Integer> playerHandSize = new HashMap<>();
//                    for (String player : players) {
//                        playerHandSize.put(player, playerCards.get(player).size());
//                    }
//                    Map<String, List<String>> playersHand = new HashMap<>();
//                    for (String player : players) {
//                        if (turn.equals(player)) {
//                            playersHand.put(player, new ArrayList<>(playerCards.get(player)));
//                        } else {
//                            playersHand.put(player, new ArrayList<>());
//                        }
//                    }
//                    move = MonteCarloTreeSearch.builder()
//                            .players(players)
//                            .playerCards(playersHand)
//                            .floor(floor)
//                            .choices(choices)
//                            .currentPlayer(turn)
//                            .playerHandSize(playerHandSize)
//                            .build().getNextThrow(level.get(turn) * 500);
//                } else {
//                    move = AgentUtil.getCardsToThrow(playerCards.get(turn));
//                }
//                playerCards.get(turn).removeAll(move);
//                tempChoices.addAll(move);
//
//                if (deck.isEmpty()) {
//                    deck.addAll(floor);
//                    floor = new ArrayList<>();
//                    Collections.shuffle(deck);
//                }
//
//                String choice;
//                if (turn.startsWith("MCTS")) {
//                    Map<String, Integer> playerHandSize = new HashMap<>();
//                    for (String player : players) {
//                        playerHandSize.put(player, playerCards.get(player).size());
//                    }
//                    Map<String, List<String>> playersHand = new HashMap<>();
//                    for (String player : players) {
//                        if (turn.equals(player)) {
//                            playersHand.put(player, new ArrayList<>(playerCards.get(player)));
//                        } else {
//                            playersHand.put(player, new ArrayList<>());
//                        }
//                    }
//                    choice = MonteCarloTreeSearch.builder()
//                            .players(players)
//                            .playerCards(playersHand)
//                            .playerHandSize(playerHandSize)
//                            .floor(floor)
//                            .choices(choices)
//                            .currentPlayer(turn)
//                            .build().getNextChoice(level.get(turn) * 500);
//                } else
//                    choice = AgentUtil.getCardToPick(playerCards.get(turn), choices);
//                if (choice.equals(AgentUtil.DECK)) {
//                    playerCards.get(turn).add(deck.remove(deck.size() - 1));
//                } else {
//                    choices.remove(choice);
//                    playerCards.get(turn).add(choice);
//                }
//                if (!choices.isEmpty()) {
//                    floor.addAll(choices);
//                    choices = new ArrayList<>();
//                }
//                choices.addAll(new ArrayList<>(tempChoices));
//                tempChoices = new ArrayList<>();
//
//                turn = players.get((players.indexOf(turn) + 1) % players.size());
//                round++;
//            }
//
//            System.out.println("Round " + (round / players.size()) + ": " + turn + " win");
//            for (String player : players) {
//                System.out.println(player + " " + (turn.equals(player) ? "win" : "lose") + " " + playerCardsCopy.get(player).toString() + "==>" + playerCards.get(player).toString());
//            }
//            return turn;
//        }
//    }
//
//
//    @Test
//    void battler() {
//        List<String> players = new ArrayList<>();
//        Map<String, Integer> level = new HashMap<>();
//        Map<String, Integer> wins = new HashMap<>();
//        for (Map.Entry<String, Integer> entry : PLAYER_LEVEL.entrySet()) {
//            players.add(entry.getKey());
//            level.put(entry.getKey(), entry.getValue());
//        }
//        BattleThreadPool battleThreadPool = new BattleThreadPool(THREAD_COUNT);
//        List<BattleThread> battleThreads = new ArrayList<>();
//        int i = 0;
//        int done = 0;
//        while (true) {
//            if (done == GAME_COUNT) break;
//            if (i < GAME_COUNT && battleThreadPool.available()) {
//                Collections.shuffle(players);
//                BattleThread battleThread = battleThreadPool.get(players, level);
//                battleThread.setName("Game-" + i);
//                battleThreads.add(battleThread);
//                battleThread.start();
//                i++;
//                System.out.println("Game " + i + " started");
//            }
//            for (BattleThread battleThread : battleThreads) {
//                if (battleThread.getWinner() != null && !battleThread.isDone()) {
//                    wins.put(battleThread.getWinner(), wins.getOrDefault(battleThread.getWinner(), 0) + 1);
//                    battleThread.setDone(true);
//                    battleThreadPool.release(battleThread);
//                    done++;
//                }
//            }
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        System.out.println("\n\nResult:");
//
//        for (Map.Entry<String, Integer> entry : wins.entrySet()) {
//            System.out.println(entry.getKey() + " " + entry.getValue());
//        }
//    }
//}
