package com.fyp.dhumbal.global.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardUtil {

    private CardUtil() {
    }

    private static final List<String> SUITS = List.of("H", "D", "S", "C");

    public static List<String> getRandomCard(List<String> cards, int size) {
        List<String> randomCards = new ArrayList<>();
        while (size > 0) {
            int index = (int) (Math.random() * cards.size());
            randomCards.add(cards.remove(index));
            size--;
        }
        return randomCards;
    }

    public static List<String> getShuffledCard() {
        List<String> allCards = getAllCards();
        Collections.shuffle(allCards);
        return allCards;
    }

    private static List<String> getAllCards() {
        List<String> allCards = new ArrayList<>();
        for (String suit : SUITS) {
            for (int i = 1; i <= 13; i++) {
                allCards.add(suit + "_" + i);
            }
        }
        return allCards;
    }
}
