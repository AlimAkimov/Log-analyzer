package org.example.Analyzer;

import lombok.AllArgsConstructor;
import org.example.Model.VisitLog;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class WebLogAnalyzerImpl implements WebLogAnalyzer {
    private final List<VisitLog> logs;

    @Override
    public Map<String, Long> getPageViewsTop5() {
        Map<String, Long> counts = logs.stream()
                .collect(Collectors.groupingBy(VisitLog::getPage, Collectors.counting()));

        List<Map.Entry<String, Long>> pageList = new ArrayList<>(counts.entrySet());
        pageList.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

        Map<String, Long> top5 = new LinkedHashMap<>();
        for (int i = 0; i < Math.min(5, pageList.size()); i++) {
            Map.Entry<String, Long> entry = pageList.get(i);
            top5.put(entry.getKey(), entry.getValue());
        }
        return top5;
    }

    @Override
    public Set<String> getUsersOnPage(String page) // Уникальные пользователи на странице
    {
        return logs.stream()
                .filter(log -> log.getPage().equals(page))
                .map(VisitLog::getUserId)
                .collect(Collectors.toSet());
    }

    @Override
    public double getAverageTimeOnPage(String page) // Среднее время на странице
    {
        return logs.stream()
                .filter(log -> log.getPage().equals(page))
                .mapToInt(VisitLog::getDurationSeconds)
                .average()
                .orElse(0.0);
    }

    @Override
    public Set<String> getActiveUsers(int minPages) // Пользователи, посетившие 2+ разные страницы
    {
        return logs.stream()
                .collect(Collectors.groupingBy(VisitLog::getUserId,
                        Collectors.mapping(VisitLog::getPage, Collectors.toSet())))
                .entrySet().stream()
                .filter(e -> e.getValue().size() >= minPages)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    @Override
    public List<VisitLog> getLastNVisits(int n) // Последние N посещений сайта (по времени)
    {
        return logs.stream()
                .sorted(Comparator.comparing(VisitLog::getTimestamp).reversed())
                .limit(n)
                .toList();
    }

    @Override
    public Map<Integer, Long> getVisitsByHour() // Количество визитов по часам суток (0-23)
    {
        return logs.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getTimestamp().getHour(),
                        TreeMap::new,
                        Collectors.counting()
                ));
    }

    @Override
    public Map<String, List<List<VisitLog>>> getActiveUserSessions(long maxBreakMinutes) {
        Map<String, List<List<VisitLog>>> result = new LinkedHashMap<>();

        Map<String, List<VisitLog>> logsByUser = logs.stream()
                .collect(Collectors.groupingBy(VisitLog::getUserId));

        for (Map.Entry<String, List<VisitLog>> entry : logsByUser.entrySet()) {
            String userId = entry.getKey();
            List<VisitLog> userLogs = entry.getValue();

            Set<String> uniquePages = userLogs.stream()
                    .map(VisitLog::getPage)
                    .collect(Collectors.toSet());
            if (uniquePages.size() < 2) {
                continue;
            }

            List<VisitLog> sortedLogs = userLogs.stream()
                    .sorted(Comparator.comparing(VisitLog::getTimestamp))
                    .toList();

            List<List<VisitLog>> sessions = new ArrayList<>();
            List<VisitLog> currentSession = new ArrayList<>();
            for (int i = 0; i < sortedLogs.size(); i++) {
                VisitLog currentLog = sortedLogs.get(i);
                currentSession.add(currentLog);

                if (i < sortedLogs.size() - 1) {
                    VisitLog nextLog = sortedLogs.get(i + 1);
                    long minutesBetween = ChronoUnit.MINUTES.between(
                            currentLog.getTimestamp(),
                            nextLog.getTimestamp()
                    );
                    if (minutesBetween > maxBreakMinutes) {
                        sessions.add(new ArrayList<>(currentSession));
                        currentSession.clear();
                    }
                }
            }
            if (!currentSession.isEmpty()) {
                sessions.add(new ArrayList<>(currentSession));
            }
            result.put(userId, sessions);
        }

        return result;
    }

    @Override
    public List<String> getUserPath(String userId) {
        return logs.stream()
                .filter(log -> log.getUserId().equals(userId))
                .sorted(Comparator.comparing(VisitLog::getTimestamp))
                .map(VisitLog::getPage)
                .toList();
    }

    @Override
    public Set<String> getAbandonedCarts() {
        Set<String> cartUser = logs.stream()
                .filter(log -> log.getPage().equals("/cart"))
                .map(VisitLog::getUserId)
                .collect(Collectors.toSet());
        Set<String> checkoutUser = logs.stream()
                .filter(log -> log.getPage().equals("/checkout"))
                .map(VisitLog::getUserId)
                .collect(Collectors.toSet());
        return cartUser.stream()
                .filter(userId -> !checkoutUser.contains(userId))
                .collect(Collectors.toSet());

    }
}
