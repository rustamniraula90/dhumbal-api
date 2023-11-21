package com.fyp.dhumbal.game.service.impl;

import com.fyp.dhumbal.game.dal.GameEntity;
import com.fyp.dhumbal.game.dal.GameRepository;
import com.fyp.dhumbal.game.rest.model.GamePickRequest;
import com.fyp.dhumbal.game.rest.model.GameThrowRequest;
import com.fyp.dhumbal.game.service.GameService;
import com.fyp.dhumbal.global.error.codes.ErrorCodes;
import com.fyp.dhumbal.global.error.exception.impl.BadRequestException;
import com.fyp.dhumbal.global.util.AuthUtil;
import com.fyp.dhumbal.global.util.CardUtil;
import com.fyp.dhumbal.room.dal.member.RoomMemberEntity;
import com.fyp.dhumbal.room.dal.member.RoomMemberRepository;
import com.fyp.dhumbal.updater.model.UpdateType;
import com.fyp.dhumbal.updater.service.UpdaterService;
import com.fyp.dhumbal.userprofile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final UserProfileService userProfileService;
    private final UpdaterService updaterService;

    @Override
    public void startGame(String id) {
        gameRepository.findById(id).ifPresent(g -> {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Game has already started");
        });

        RoomMemberEntity roomMemberEntity = roomMemberRepository.findById(id).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Room not found"));
        GameEntity game = new GameEntity();
        game.setId(id);
        game.setPlayers(roomMemberEntity.getMembers());
        List<String> allCard = CardUtil.getShuffledCard();
        for (String player : roomMemberEntity.getMembers()) {
            game.getHands().put(player, CardUtil.getRandomCard(allCard, 5));
        }
        game.setFloor(new ArrayList<>());
        game.getFloor().add(allCard.remove(allCard.size() - 1));
        game.setDeck(allCard);
        game.setTurn(game.getPlayers().get(0));
        gameRepository.save(game);
        // TODO: Add game global information
        updaterService.updateRoom(id, UpdateType.GAME_STARTED, game);
        for (String player : roomMemberEntity.getMembers()) {
            // TODO: Add player local information
            updaterService.updatePlayer(player, UpdateType.GAME_STARTED, game);
        }
    }

    @Override
    public void pickCard(String id, GamePickRequest request) {
        String userId = AuthUtil.getLoggedInUserId();

        GameEntity game = validateGame(id, userId);

        if (!game.getThrown()) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Player hasn't thrown a card!");
        }
        if (game.getEnded()) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Card pick card after game ended");
        }
        if (request.isFloor()) {
            if (request.getChoice() >= game.getChoiceCount()) {
                throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Invalid choice card");
            }
            int index = (game.getFloor().size()) - (game.getChoiceCount() - request.getChoice());
            game.getHands().get(userId).add(game.getFloor().remove(index));
        } else {
            if (game.getDeck().isEmpty()) {
                List<String> lastCards = game.getFloor().subList(game.getFloor().size() - (game.getChoiceCount() + 1), game.getFloor().size());
                game.getFloor().removeAll(lastCards);
                Collections.shuffle(game.getFloor());
                game.setDeck(game.getFloor());
                game.setFloor(new ArrayList<>());
                game.getFloor().addAll(lastCards);
            }
            game.getHands().get(userId).add(game.getDeck().remove(game.getDeck().size() - 1));
        }
        game.setThrown(false);
        game.setChoiceCount(0);
        String nextPlayer = game.getPlayers().get((game.getPlayers().indexOf(userId) + 1) % game.getPlayers().size());
        game.setTurn(nextPlayer);
        gameRepository.save(game);
        // TODO: Add game global information
        updaterService.updateRoom(id, UpdateType.CARD_PICKED, game);
        // TODO: Add player local information
        updaterService.updatePlayer(userId, UpdateType.CARD_PICKED, game);
    }

    @Override
    public void throwCard(String id, GameThrowRequest request) {
        String userId = AuthUtil.getLoggedInUserId();

        GameEntity game = validateGame(id, userId);

        if (game.getThrown()) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Player has already thrown");
        }
        validateThrownCard(game.getHands().get(userId), request.getCard());
        game.getHands().get(userId).removeAll(request.getCard());
        game.setChoiceCount(request.getCard().size());
        game.getFloor().addAll(request.getCard());
        game.setThrown(true);
        gameRepository.save(game);
        // TODO: Add game global information
        updaterService.updateRoom(id, UpdateType.CARD_THROWN, game);
        // TODO: Add player local information
        updaterService.updatePlayer(userId, UpdateType.CARD_THROWN, game);
    }

    private void validateThrownCard(List<String> hand, List<String> thrownCards) {
        for (String card : thrownCards) {
            if (!hand.contains(card)) {
                throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Invalid card thrown");
            }
        }
        int size = thrownCards.size();

        if (size == 1 || isSameValueCards(thrownCards) || isSameColorRun(thrownCards)) {
            return;
        }
        throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Invalid card thrown");
    }

    private boolean isSameValueCards(List<String> cards) {
        String val = cards.get(0).split("_")[1];
        for (String card : cards) {
            if (!card.split("_")[1].equals(val)) {
                return false;
            }
        }
        return true;
    }

    private boolean isSameColorRun(List<String> cards) {
        List<Integer> run = new ArrayList<>();
        String color = cards.get(0).split("_")[0];
        for (String card : cards) {
            String[] colorValue = card.split("_");
            if (color.equals(colorValue[0])) {
                return false;
            }
            run.add(Integer.parseInt(colorValue[1]));
        }
        Collections.sort(run);
        for (int i = 1; i < run.size(); i++) {
            if (!(run.get(i) == (run.get(i - 1) + 1))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void endGame(String id) {
        String userId = AuthUtil.getLoggedInUserId();
        GameEntity game = validateGame(id, userId);

        if (game.getThrown()) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Can't end game after throwing a card");
        }
        int sum = 0;
        for (String card : game.getHands().get(userId)) {
            String[] split = card.split("_");
            sum += Integer.parseInt(split[1]);
        }
        if (sum > 5) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Can't end game with more than 5 points");
        }
        game.setEndingPoint(sum);
        game.setEnded(true);
        game.setEndedBy(userId);
        game.setThrown(false);
        game.setWinner(userId);
        game.setWinnerPoint(game.getWinnerPoint());
        String nextPlayer = game.getPlayers().get((game.getPlayers().indexOf(userId) + 1) % game.getPlayers().size());
        game.setTurn(nextPlayer);
        gameRepository.save(game);
        // TODO: Add game global information
        updaterService.updateRoom(id, UpdateType.GAME_ENDED, game);
        // TODO: Add player local information
        updaterService.updatePlayer(userId, UpdateType.GAME_ENDED, game);
    }

    @Override
    public void dhumbal(String id) {
        String userId = AuthUtil.getLoggedInUserId();
        GameEntity game = validateGame(id, userId);

        if (!game.getEnded()) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Cannot dhumbal on running game");
        }
        int sum = 0;
        for (String card : game.getHands().get(userId)) {
            String[] split = card.split("_");
            sum += Integer.parseInt(split[1]);
        }
        if (sum > game.getWinnerPoint()) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Can't dhumbal with more than winner");
        }
        game.setWinner(userId);
        game.setWinnerPoint(sum);
        game.setThrown(false);
        String nextPlayer = game.getPlayers().get((game.getPlayers().indexOf(userId) + 1) % game.getPlayers().size());
        game.setTurn(nextPlayer);
        gameRepository.save(game);
        // TODO: Add game global information
        updaterService.updateRoom(id, UpdateType.DHUMBAL, game);
        // TODO: Add player local information
        updaterService.updatePlayer(userId, UpdateType.DHUMBAL, game);
    }

    @Override
    public void passGame(String id) {
        String userId = AuthUtil.getLoggedInUserId();
        GameEntity game = validateGame(id, userId);
        if (!game.getEnded()) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Cannot pass on running game");
        }
        game.setThrown(false);
        String nextPlayer = game.getPlayers().get((game.getPlayers().indexOf(userId) + 1) % game.getPlayers().size());
        game.setTurn(nextPlayer);
        gameRepository.save(game);
        // TODO: Add game global information
        updaterService.updateRoom(id, UpdateType.PASS, game);
        // TODO: Add player local information
        updaterService.updatePlayer(userId, UpdateType.PASS, game);
    }

    @Override
    public void finalizeGame(String id) {
        GameEntity game = gameRepository.findById(id).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Game not found"));
        if (!game.getEnded()) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Cannot finalize on running game");
        }
        for (String player : game.getPlayers()) {
            userProfileService.updateStatus(player, player.equals(game.getWinner()));
        }
        gameRepository.delete(game);
        // TODO: Add game global information
        updaterService.updateRoom(id, UpdateType.GAME_FINALIZED, game);
    }

    private GameEntity validateGame(String gameId, String userId) {
        GameEntity game = gameRepository.findById(gameId).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Game not found"));
        if (!game.getPlayers().contains(userId)) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Player not found");
        }
        if (!game.getTurn().equals(userId)) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Not your turn");
        }
        return game;
    }
}
