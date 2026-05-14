package rinhacampusiv.api.v2.utils.tournaments;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class TeamCountUtils {

    private TeamCountUtils() {
    }

    public static Map<Long, Integer> toCountMap(List<Object[]> rows) {
        return rows.stream().collect(Collectors.toMap(
                row -> (Long) row[0],
                row -> ((Long) row[1]).intValue()
        ));
    }
}
