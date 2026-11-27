package io.github.likhitysplit.solo.stats;

import io.github.likhitysplit.solo.user.User;
import io.github.likhitysplit.solo.user.UserNotFoundException;
import io.github.likhitysplit.solo.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.Map;

@Service
public class StatService {
    private final StatProgressRepository statProgress;
    private final UserRepository users;

    public StatService(StatProgressRepository statProgress, UserRepository users) {
        this.statProgress = statProgress;
        this.users = users;
    }

    @Transactional
    public StatProgress awardXp(Long userId, Stat stat, int amount) {
        StatProgress progress = statProgress.findByUserIdAndStat(userId, stat)
                .orElseGet(() -> createProgress(userId, stat));
        progress.addXp(amount);
        return progress;
    }

    private StatProgress createProgress(Long userId, Stat stat) {
        User user = users.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return statProgress.save(new StatProgress(user, stat));
    }

    @Transactional(readOnly = true)
    public Map<Stat, Integer> getTotalXpByStat(Long userId) {
        if (!users.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        Map<Stat, Integer> result = new EnumMap<>(Stat.class);
        for (Stat stat : Stat.values()) {
            result.put(stat, 0);
        }
        for (StatProgress p : statProgress.findByUserId(userId)) {
            result.put(p.getStat(), p.getTotalXp());
        }
        return result;
    }
}
