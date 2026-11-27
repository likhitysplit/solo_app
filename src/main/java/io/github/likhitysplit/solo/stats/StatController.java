package io.github.likhitysplit.solo.stats;

import io.github.likhitysplit.solo.auth.AppUserPrincipal;
import io.github.likhitysplit.solo.stats.dto.StatProgressResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatController {

    private final StatService statService;

    public StatController(StatService statService) {
        this.statService = statService;
    }

    @GetMapping
    public List<StatProgressResponse> getAll(@AuthenticationPrincipal AppUserPrincipal me) {
        List<StatProgressResponse> result = new ArrayList<>();
        for (Map.Entry<Stat, Integer> entry : statService.getTotalXpByStat(me.id()).entrySet()) {
            result.add(StatProgressResponse.of(entry.getKey(), entry.getValue()));
        }
        return result;
    }
}