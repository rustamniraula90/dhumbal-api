package com.fyp.dhumbal.game.service.impl;

import com.fyp.dhumbal.agent.AgentConstant;
import com.fyp.dhumbal.agent.event.AgentEventPayload;
import com.fyp.dhumbal.agent.model.AgentMoveRequest;
import com.fyp.dhumbal.game.dal.GameEntity;
import com.fyp.dhumbal.game.dal.GameRepository;
import com.fyp.dhumbal.game.rest.model.GamePickRequest;
import com.fyp.dhumbal.game.rest.model.GameStateResponse;
import com.fyp.dhumbal.game.rest.model.GameThrowRequest;
import com.fyp.dhumbal.game.rest.model.GameUserResultResponse;
import com.fyp.dhumbal.game.service.GameService;
import com.fyp.dhumbal.global.error.codes.ErrorCodes;
import com.fyp.dhumbal.global.error.exception.impl.BadRequestException;
import com.fyp.dhumbal.global.util.AuthUtil;
import com.fyp.dhumbal.global.util.CardUtil;
import com.fyp.dhumbal.global.util.RandomGenerator;
import com.fyp.dhumbal.room.dal.RoomEntity;
import com.fyp.dhumbal.room.dal.RoomRepository;
import com.fyp.dhumbal.room.dal.RoomStatusEnum;
import com.fyp.dhumbal.updater.model.UpdateType;
import com.fyp.dhumbal.updater.service.UpdaterService;
import com.fyp.dhumbal.user.dal.UserEntity;
import com.fyp.dhumbal.user.dal.UserRepository;
import com.fyp.dhumbal.userprofile.service.UserProfileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final UserProfileService userProfileService;
    private final UpdaterService updaterService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Value("${dhumbal.game.points.multiplier}")
    private Integer gamePointMultiplier;

    @Value("${dhumbal.game.card.count}")
    private Integer cardCount;

    @Override
    @Transactional
    public void startGame(String id) {
        gameRepository.findById(id).ifPresent(g -> {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Game has already started");
        });

        RoomEntity room = roomRepository.findById(id).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Room not found"));
        GameEntity game = new GameEntity();
        game.setId(id);
        game.setPlayers(new ArrayList<>(room.getMembers().stream().map(UserEntity::getId).toList()));
        List<String> allCard = CardUtil.getShuffledCard();
        Map<String, List<String>> hands = new HashMap<>();
        for (String player : game.getPlayers()) {
            hands.put(player, CardUtil.getRandomCard(allCard, cardCount));
        }
        for (Integer agent : room.getAgent()) {
            String agentId = "BOT_" + RandomGenerator.generateAlphabetic(3) + AgentConstant.AGENT_ID_SEPARATOR + agent;
            game.getPlayers().add(agentId);
            hands.put(agentId, CardUtil.getRandomCard(allCard, cardCount));
        }
        game.setHands(hands);
        game.setFloor(new ArrayList<>());
        game.setChoiceCount(1);
        game.getFloor().add(allCard.remove(allCard.size() - 1));
        game.setDeck(allCard);
        game.setTurn(game.getPlayers().get(0));
        gameRepository.save(game);
        updaterService.updateRoom(id, UpdateType.GAME_STARTED, null);
    }

    @Override
    public void pickCard(String id, GamePickRequest request, String userId, String username) {
        GameEntity game = validateGame(id, userId);
        Map<String, List<String>> hands = game.getHands();
        String pickedCard = "deck";

        if (!game.isThrown()) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Player hasn't thrown a card!");
        }
        if (game.isEnded()) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Card pick card after game ended");
        }
        if (request.isFloor()) {
            if (request.getChoice() >= game.getChoiceCount()) {
                throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Invalid choice card");
            }
            int index = (game.getFloor().size()) - (game.getChoiceCount() - request.getChoice());
            pickedCard = game.getFloor().get(index);
            hands.get(userId).add(game.getFloor().remove(index));
        } else {
            if (game.getDeck().isEmpty()) {
                List<String> lastCards = new ArrayList<>(game.getFloor().subList(game.getFloor().size() - (game.getChoiceCount() + 1), game.getFloor().size()));
                game.getFloor().removeAll(lastCards);
                Collections.shuffle(game.getFloor());
                game.setDeck(game.getFloor());
                game.setFloor(new ArrayList<>());
                game.getFloor().addAll(lastCards);
            }
            hands.get(userId).add(game.getDeck().remove(game.getDeck().size() - 1));
        }
        game.setThrown(false);
        String nextPlayer = game.getPlayers().get((game.getPlayers().indexOf(userId) + 1) % game.getPlayers().size());
        game.setTurn(nextPlayer);
        game.setChoiceCount(game.getTempFloor().size());
        game.getFloor().addAll(game.getTempFloor());
        game.setTempFloor(new ArrayList<>());
        game.setHands(hands);
        gameRepository.save(game);
        Map<String, String> data = new HashMap<>();
        data.put("card", pickedCard);
        data.put("name", username);
        updaterService.updateGame(id, UpdateType.CARD_PICKED, data);

        checkBotTurn(id);
    }

    @Override
    public void throwCard(String id, GameThrowRequest request, String userId, String username) {
        GameEntity game = validateGame(id, userId);
        Map<String, List<String>> hands = game.getHands();
        if (game.isThrown()) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Player has already thrown");
        }
        List<String> playerHand = hands.get(userId);
        for (String card : request.getCard()) {
            if (!playerHand.contains(card)) {
                throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Invalid card thrown");
            }
        }
        CardUtil.validateThrownCard(request.getCard());
        hands.get(userId).removeAll(request.getCard());
        game.setTempFloor(request.getCard());
        game.setThrown(true);
        game.setHands(hands);
        gameRepository.save(game);
        Map<String, Object> data = new HashMap<>();
        data.put("cards", request.getCard());
        data.put("name", username);
        updaterService.updateGame(id, UpdateType.CARD_THROWN, data);

        checkBotTurn(id);
    }

    private void checkBotTurn(String gameId) {
        GameEntity game = gameRepository.findById(gameId).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Game not found"));
        if (game.getTurn().startsWith("BOT")) {
            String[] agent = game.getTurn().split(AgentConstant.AGENT_ID_SEPARATOR);
            AgentMoveRequest agentMoveRequest = AgentMoveRequest.builder()
                    .agentId(game.getTurn())
                    .agentLevel(Integer.parseInt(agent[1]))
                    .hand(game.getHands().get(game.getTurn()))
                    .choices(game.getFloor().subList(game.getFloor().size() - game.getChoiceCount(), game.getFloor().size()))
                    .gameId(gameId)
                    .moveType(game.isThrown() ? AgentMoveRequest.MoveType.PICK : AgentMoveRequest.MoveType.THROW)
                    .build();
            applicationEventPublisher.publishEvent(new AgentEventPayload(this, agentMoveRequest));
        }
    }

    @Override
    public void endGame(String id, String userId) {
        GameEntity game;
        if (userId.startsWith("BOT")) {
            game = gameRepository.findById(id).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Game not found"));
        } else {
            game = validateGame(id, userId);
        }
        Map<String, List<String>> hands = game.getHands();

        if (game.isThrown()) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Can't end game after throwing a card");
        }
        int sum = CardUtil.getCardValue(hands.get(userId));

        if (sum > 5) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Can't end game with more than 5 points");
        }
        String winnerId = userId;
        int winningPoints = sum;
        for (String player : game.getPlayers()) {
            if (player.equals(userId)) {
                continue;
            }
            int playerSum = CardUtil.getCardValue(hands.get(player));
            if (playerSum < sum) {
                winnerId = player;
                winningPoints = playerSum;
            }
        }
        game.setEndingPoint(sum);
        game.setEnded(true);
        game.setEndedBy(userId);
        game.setWinner(winnerId);
        game.setWinnerPoint(winningPoints);
        gameRepository.save(game);
        updaterService.updateGame(id, UpdateType.GAME_ENDED, game);
        finalizeGame(id);
    }

    @Override
    public void finalizeGame(String id) {
        GameEntity game = gameRepository.findById(id).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Game not found"));
        if (!game.isEnded()) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Cannot finalize on running game");
        }
        int winningPoints = 0;
        for (String player : game.getPlayers()) {
            if (!player.startsWith("BOT") && !player.equals(game.getWinner())) {
                userProfileService.updateStatus(player, -1 * CardUtil.getCardValue(game.getHands().get(player)));
            }
            winningPoints += CardUtil.getCardValue(game.getHands().get(player));
        }
        if (!game.getWinner().startsWith("BOT")) {
            userProfileService.updateStatus(game.getWinner(), winningPoints);
        }
        RoomEntity room = roomRepository.findById(id).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Room not found"));
        room.setStatus(RoomStatusEnum.FINISHED);
        roomRepository.save(room);
        updaterService.updateRoom(id, UpdateType.GAME_FINALIZED, game);
    }

    @Override
    public GameStateResponse getGameState(String gameId) {
        GameEntity game = gameRepository.findById(gameId).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Game not found"));
        int floorLength = game.getFloor().size();
        String loggedInUser = AuthUtil.getLoggedInUserId();
        GameStateResponse response = new GameStateResponse();
        response.setTurn(game.getTurn());
        response.setThrown(game.isThrown());
        response.setChoiceCount(game.getChoiceCount());
        response.setPlayers(game.getPlayers());
        response.setChoices(game.getFloor().subList(floorLength - game.getChoiceCount(), floorLength));
        if (game.getPlayers().contains(loggedInUser)) {
            response.setHands(game.getHands().get(loggedInUser));
        }
        for (String player : game.getPlayers()) {
            response.getCardCount().put(player, game.getHands().get(player).size());
        }
        response.setPoints(CardUtil.getCardValue(response.getHands()));
        response.setEnded(game.isEnded());
        return response;
    }

    @Override
    public List<GameUserResultResponse> getResult(String gameId) {
        GameEntity game = gameRepository.findById(gameId).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Game not found"));
        if (!game.isEnded()) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Game has not ended yet");
        }
        List<GameUserResultResponse> responses = new ArrayList<>();
        Map<String, List<String>> hands = game.getHands();
        GameUserResultResponse winnerResponse = new GameUserResultResponse();
        Integer gamePoint = 0;
        for (String player : game.getPlayers()) {
            GameUserResultResponse response = new GameUserResultResponse();
            if (player.startsWith("BOT")) {
                response.setUserId(player);
                response.setUserName(player.split(AgentConstant.AGENT_ID_SEPARATOR)[1]);
            } else {
                UserEntity user = userRepository.findById(player).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "user not found"));
                response.setUserId(player);
                response.setUserName(user.getName());
            }
            response.setPoints(CardUtil.getCardValue(game.getHands().get(player)));
            response.setWinner(player.equals(game.getWinner()));
            if (response.getWinner()) {
                winnerResponse = response;
            } else {
                gamePoint += response.getPoints();
                response.setScore(-1 * response.getPoints() * gamePointMultiplier);
            }
            response.setCards(hands.get(player));
            responses.add(response);
        }
        winnerResponse.setScore(gamePoint * gamePointMultiplier);
        return responses;
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
