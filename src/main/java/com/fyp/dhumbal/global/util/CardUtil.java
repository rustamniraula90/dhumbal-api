package com.fyp.dhumbal.global.util;

import com.fyp.dhumbal.global.error.codes.ErrorCodes;
import com.fyp.dhumbal.global.error.exception.impl.BadRequestException;

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

    public static int getCardValue(List<String> cards) {
        int sum = 0;
        for (String card : cards) {
            String[] split = card.split("_");
            sum += Integer.parseInt(split[1]);
        }
        return sum;
    }

    public static void validateThrownCard(List<String> thrownCards) {
        int size = thrownCards.size();

        if (size == 1 || isSameValueCards(thrownCards) || isSameColorRun(thrownCards)) {
            return;
        }
        throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Invalid card thrown");
    }

    public static boolean isSameValueCards(List<String> cards) {
        String val = cards.get(0).split("_")[1];
        for (String card : cards) {
            if (!card.split("_")[1].equals(val)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSameColorRun(List<String> cards) {
        if (cards.size() < 3) return false;
        List<Integer> run = new ArrayList<>();
        String color = cards.get(0).split("_")[0];
        for (String card : cards) {
            String[] colorValue = card.split("_");
            if (!color.equals(colorValue[0])) {
                return false;
            }
            run.add(Integer.parseInt(colorValue[1]));
        }
        Collections.sort(run);
        for (int i = 1; i < run.size(); i++) {
            if (run.get(i) != (run.get(i - 1) + 1)) {
                return false;
            }
        }
        return true;
    }
}
