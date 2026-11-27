package io.github.likhitysplit.solo.stats;

import io.github.likhitysplit.solo.stats.dto.StatProgressResponse;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users/{userId}/stats")
public class StatController {

    private final StatService statService;

    public StatController(StatService statService) {
        this.statService = statService;
    }

    @GetMapping
    public List<StatProgressResponse> getAll(@PathVariable Long userId) {
        List<StatProgressResponse> result = new ArrayList<>();
        for (Map.Entry<Stat, Integer> entry : statService.getTotalXpByStat(userId).entrySet()) {
            result.add(StatProgressResponse.of(entry.getKey(), entry.getValue()));
        }
        return result;
    }
}