package eleks.mentorship.bigbang.serialiazation;

import eleks.mentorship.bigbang.domain.PlayerReady;
import eleks.mentorship.bigbang.gameplay.PlayerInfo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerReadyConverter {

    public static List<PlayerReady> convert(Map<PlayerInfo, Boolean> value) {
        return value
                .entrySet()
                .stream()
                .map(x -> new PlayerReady(x.getKey().getUserId(), x.getKey().getNickname(), x.getValue()))
                .collect(Collectors.toList());
    }
}
