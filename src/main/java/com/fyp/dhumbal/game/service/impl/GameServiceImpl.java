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
    }

    @Override
    public void pickCard(String id, GamePickRequest request) {
        String userId = AuthUtil.getLoggedInUserId();

        GameEntity game = validateGame(id, userId);

        if (game.getPicked()) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Card already picked");
        }
        if (game.getEnded()) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Card pick card after game ended");
        }
        if (request.isFloor()) {
            game.getHands().get(userId).add(game.getFloor().remove(game.getFloor().size() - 1));
        } else {
            if (game.getDeck().isEmpty()) {
                String lastCard = game.getFloor().remove(game.getFloor().size() - 1);
                Collections.shuffle(game.getFloor());
                game.setDeck(game.getFloor());
                game.setFloor(new ArrayList<>());
                game.getFloor().add(lastCard);
            }
            game.getHands().get(userId).add(game.getDeck().remove(game.getDeck().size() - 1));
        }
        game.setPicked(true);
        gameRepository.save(game);
    }

    @Override
    public void throwCard(String id, GameThrowRequest request) {
        String userId = AuthUtil.getLoggedInUserId();

        GameEntity game = validateGame(id, userId);

        if (!game.getPicked()) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Player has not picked a card");
        }
        String card = game.getHands().get(userId).stream().filter(c -> c.equals(request.getCard())).findFirst().orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Card not found"));
        game.getHands().get(userId).remove(card);
        game.getFloor().add(card);
        game.setPicked(false);
        String nextPlayer = game.getPlayers().get((game.getPlayers().indexOf(userId) + 1) % game.getPlayers().size());
        game.setTurn(nextPlayer);
        gameRepository.save(game);
    }

    @Override
    public void endGame(String id) {
        String userId = AuthUtil.getLoggedInUserId();
        GameEntity game = validateGame(id, userId);

        if (game.getPicked()) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Can't end game after picking a card");
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
        game.setPicked(false);
        game.setWinner(userId);
        game.setWinnerPoint(game.getWinnerPoint());
        String nextPlayer = game.getPlayers().get((game.getPlayers().indexOf(userId) + 1) % game.getPlayers().size());
        game.setTurn(nextPlayer);
        gameRepository.save(game);
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
        game.setPicked(false);
        String nextPlayer = game.getPlayers().get((game.getPlayers().indexOf(userId) + 1) % game.getPlayers().size());
        game.setTurn(nextPlayer);
        gameRepository.save(game);

    }

    @Override
    public void passGame(String id) {
        String userId = AuthUtil.getLoggedInUserId();
        GameEntity game = validateGame(id, userId);
        if (!game.getEnded()) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Cannot pass on running game");
        }
        game.setPicked(false);
        String nextPlayer = game.getPlayers().get((game.getPlayers().indexOf(userId) + 1) % game.getPlayers().size());
        game.setTurn(nextPlayer);
        gameRepository.save(game);
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
