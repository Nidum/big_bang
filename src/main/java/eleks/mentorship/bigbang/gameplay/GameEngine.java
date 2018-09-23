package eleks.mentorship.bigbang.gameplay;

import eleks.mentorship.bigbang.gameplay.field.GameField;
import eleks.mentorship.bigbang.mapper.JsonMessageMapper;
import eleks.mentorship.bigbang.websocket.MessageAggregator;
import eleks.mentorship.bigbang.websocket.WebSocketMessageSubscriber;
import eleks.mentorship.bigbang.websocket.message.GameMessage;
import eleks.mentorship.bigbang.websocket.message.server.*;
import eleks.mentorship.bigbang.websocket.message.user.PositioningMessage;
import eleks.mentorship.bigbang.websocket.message.user.ReadyMessage;
import eleks.mentorship.bigbang.websocket.message.user.UserMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static eleks.mentorship.bigbang.serialiazation.PlayerReadyConverter.convert;
import static eleks.mentorship.bigbang.websocket.Room.MAX_CONNECTIONS;
import static eleks.mentorship.bigbang.websocket.message.MessageType.*;
import static eleks.mentorship.bigbang.websocket.message.MessageUtils.IS_POSITIONING_MESSAGE;

@Data
@AllArgsConstructor
public class GameEngine {
    private static final int EXPLOSION_DELAY = 2; // In seconds.
    private static final int BUFFER_WINDOW = 100; // In milliseconds.

    private final JsonMessageMapper mapper;
    private final WebSocketMessageSubscriber messageSubscriber;
    private final MessageAggregator aggregator;

    private DirectProcessor<GameState> stateConsumer;
    private Flux<GameState> stateProducer;

    DirectProcessor<BombExplosionMessage> bombProducer;
    private FluxSink<BombExplosionMessage> bombConsumer;

    private Map<PlayerInfo, Boolean> playerReady;

    // TODO: inject game field (randomly).
    public GameEngine(JsonMessageMapper mapper, MessageAggregator aggregator) {
        this.mapper = mapper;
        this.messageSubscriber = new WebSocketMessageSubscriber();
        this.aggregator = aggregator;
        this.playerReady = new HashMap<>();

        this.bombProducer = DirectProcessor.create();
        this.bombConsumer = bombProducer.sink(FluxSink.OverflowStrategy.BUFFER);

        this.stateConsumer = DirectProcessor.create();
        this.stateProducer = stateConsumer
                .startWith(new GameState(new HashSet<>(), new GameField("gamefield"), new ArrayList<>()))
                .cache()
                .takeUntil(state ->
                        state.getPlayers().size() == MAX_CONNECTIONS &&
                                state.getPlayers().stream().filter(p -> p.getLivesLeft() == 0).count() >= MAX_CONNECTIONS - 1);

        buildGamePlay();
    }

    private void buildGamePlay() {
        Flux<GameMessage> cache = messageSubscriber.getOutputEvents()
                .cache(0);

        Mono<GameMessage> startGameCounterMono = Mono.just(new StartCounterMessage());
        Flux<GameMessage> gameStartFlux = Mono
                .just((GameMessage) new GameStartMessage())
                .mergeWith(stateProducer.take(1))
                .delaySubscription(Duration.ofSeconds(3));

        Flux<GameMessage> gamePlayMessageFlux = cache
                .filter(IS_POSITIONING_MESSAGE)
                .map(x -> (PositioningMessage) x)
                .mergeWith(bombProducer)
                .buffer(Duration.ofMillis(BUFFER_WINDOW))
                .zipWith(stateProducer, (messages, oldState) -> aggregator.aggregate(messages, oldState, bombConsumer))
                .doOnNext(msg -> {
                    if (msg.getType().equals(STATE) || msg.getType().equals(EXPLOSION)) {
                        stateConsumer.onNext((GameState) msg);
                    }
                })
                .concatWith(
                        stateProducer
                                .last()
                                .map(state -> {
                                    PlayerInfo winner = state
                                            .getPlayers()
                                            .stream()
                                            .filter(p -> p.getLivesLeft() > 0)
                                            .map(GamePlayer::getPlayerInfo)
                                            .findFirst()
                                            .orElse(new PlayerInfo(null, null));
                                    return new GameOverMessage(winner);
                                }))
                .cache();

        messageSubscriber.setOutputEvents(
                cache.filter(x -> x instanceof ReadyMessage || x instanceof RoomStateMessage)
                        .map(this::processReadyMessages)
                        .takeWhile(x -> playerReady.size() != MAX_CONNECTIONS || playerReady.values().contains(false))
                        .concatWith(startGameCounterMono
                                .concatWith(gameStartFlux)
                                .concatWith(gamePlayMessageFlux
                                )
                        )
        );
    }

    private GameMessage processReadyMessages(GameMessage msg) {
        if (msg.getType().equals(READY)) {
            playerReady.put(((ReadyMessage) msg).getPlayerInfo(), true);

            if (playerReady.size() == MAX_CONNECTIONS && !playerReady.values().contains(false)) {
                return new StartCounterMessage();
            } else {
                return new RoomStateMessage(convert(playerReady));
            }
        }
        return msg;
    }

    public Flux<GameMessage> subscribePlayer(Flux<UserMessage> messageFluxCache) {
        return messageFluxCache
                .filter(x -> x.getType().equals(CONNECT))
                .take(1)
                .withLatestFrom(stateProducer, (msg, currentGameState) -> {
                    registerPlayer(msg.getPlayerInfo(), currentGameState);
                    return msg;
                })
                .map(x -> new RoomStateMessage(convert(playerReady)))
                ;
    }

    private void registerPlayer(PlayerInfo playerInfo, GameState currentGameState) {
        GamePlayer gamePlayer = new GamePlayer(playerInfo, currentGameState.getFreeSpawn(), Instant.now());
        currentGameState.getPlayers().add(gamePlayer);
        playerReady.put(playerInfo, false);
    }

    public Flux<GameMessage> getGameFlow() {
        return messageSubscriber
                .getOutputEvents();
    }

}
