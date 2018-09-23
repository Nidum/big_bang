package eleks.mentorship.bigbang.websocket.message.server;

import eleks.mentorship.bigbang.domain.Position;
import eleks.mentorship.bigbang.exception.MessageFromUnknownUserException;
import eleks.mentorship.bigbang.exception.TooMuchPlayersInRoomException;
import eleks.mentorship.bigbang.gameplay.GamePlayer;
import eleks.mentorship.bigbang.gameplay.PlayerInfo;
import eleks.mentorship.bigbang.gameplay.field.ExplosionRange;
import eleks.mentorship.bigbang.gameplay.field.GameField;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.MessageType;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eleks.mentorship.bigbang.websocket.message.MessageType.STATE;

public class GameState extends GameMessage {
    private static final int EXPLOSION_RADIUS = 3; // In cells.

    private final Set<GamePlayer> players;
    private final GameField gameField;
    private final List<ExplosionRange> explosions;

    public GameState(GameState other) {
        this.players = other.players;
        this.gameField = other.gameField;
        this.explosions = other.explosions;
        this.occurrence = other.occurrence;
    }

    public GameState(Set<GamePlayer> players, GameField gameField, List<ExplosionRange> explosions) {
        this.players = players;
        this.gameField = gameField;
        this.explosions = explosions;
        this.occurrence = Instant.now();
    }

    public Set<GamePlayer> getPlayers() {
        return players;
    }

    public GameField getGameField() {
        return gameField;
    }

    public List<ExplosionRange> getExplosions() {
        return explosions;
    }

    @Override
    public MessageType getType() {
        return STATE;
    }

    public Position getFreeSpawn() {
        return gameField.getSpawns()
                .stream()
                .filter(spawn -> players
                        .stream()
                        .noneMatch(p -> p.getPosition().equals(spawn)))
                .findFirst()
                .orElseThrow(TooMuchPlayersInRoomException::new);
    }

    @Override
    public String toString() {
        return "GameState{" +
                "players=" + players +
                ", gameField=" + gameField +
                ", occurrence=" + occurrence +
                '}';
    }

    public BombExplosionMessage placeBomb(Position position, GamePlayer gamePlayer) {
        ExplosionRange explosionRange = ExplosionRange.getExplosionRange(gameField, position, EXPLOSION_RADIUS);
        gameField.setBomb(position);
        GamePlayer newPlayer = new GamePlayer(
                gamePlayer.getPlayerInfo(),
                gamePlayer.getLivesLeft(),
                gamePlayer.getBombsLeft() - 1,
                gamePlayer.getPosition(),
                gamePlayer.getLastMoveTime()
        );
        players.remove(gamePlayer);
        players.add(newPlayer);

        return new BombExplosionMessage(newPlayer.getPlayerInfo(), position, explosionRange);
    }

    public void movePlayer(Position position, PlayerInfo playerInfo) {
        GamePlayer gamePlayer = getPlayers()
                .stream()
                .filter(p -> p.getPlayerInfo().equals(playerInfo))
                .findFirst()
                .orElseThrow(MessageFromUnknownUserException::new);
        GamePlayer newPlayer = new GamePlayer(
                gamePlayer.getPlayerInfo(),
                gamePlayer.getLivesLeft(),
                gamePlayer.getBombsLeft(),
                position,
                gamePlayer.getLastMoveTime()
        );

        this.getPlayers().remove(gamePlayer);
        this.getPlayers().add(newPlayer);
    }

    public void explodeBomb(BombExplosionMessage bombMsg) {
        ExplosionRange explosionRange = bombMsg.getExplosionRange();
        gameField.destroyBlocksOnExplosion(explosionRange);
        damagePlayers(explosionRange);

        GamePlayer gamePlayer = players.stream()
                .filter(p -> p.getPlayerInfo().equals(bombMsg.getPlayerInfo()))
                .findFirst().orElseThrow(MessageFromUnknownUserException::new);
        GamePlayer newPlayer = new GamePlayer(
                gamePlayer.getPlayerInfo(),
                gamePlayer.getLivesLeft(),
                gamePlayer.getBombsLeft() + 1,
                gamePlayer.getPosition(),
                gamePlayer.getLastMoveTime()
        );

        players.remove(gamePlayer);
        players.add(newPlayer);
        gameField.removeBomb(bombMsg.getPosition());

        this.explosions.add(explosionRange);
    }

    private void damagePlayers(ExplosionRange explosionRange) {
        List<GamePlayer> damagedPlayers = players
                .stream()
                .filter(player -> explosionRange.isInRange(player.getPosition()))
                .collect(Collectors.toList());

        List<GamePlayer> nonDamagedPlayers = players
                .stream()
                .filter(player -> !damagedPlayers.contains(player))
                .collect(Collectors.toList());

        List<GamePlayer> updateDamagedPlayers = players
                .stream()
                .filter(damagedPlayers::contains)
                .map(player -> new GamePlayer(
                        player.getPlayerInfo(),
                        player.getLivesLeft() - 1 > 0 ? player.getLivesLeft() - 1 : 0,
                        player.getBombsLeft(),
                        player.getPosition(),
                        player.getLastMoveTime()))
                .collect(Collectors.toList());

        Set<GamePlayer> updatedPlayers = Stream.concat(nonDamagedPlayers.stream(), updateDamagedPlayers.stream())
                .collect(Collectors.toSet());
        players.clear();
        players.addAll(updatedPlayers);
    }

    public void cleanExplosions() {
        this.explosions.clear();
    }
}