package org.example.Analyzer;

import lombok.AllArgsConstructor;
import org.example.Model.VisitLog;

import java.time.temporal.ChronoUnit;
import java.util.*;

@AllArgsConstructor
public class WebLogAnalyzerWithoutStreamAPIImpl implements WebLogAnalyzer {
    private final List<VisitLog> logs;

    @Override
    public Map<String, Long> getPageViewsTop5() {
        Map<String, Long> pageCounts = new HashMap<>();
        for (VisitLog log : logs) {
            String page = log.getPage();
            pageCounts.put(page, pageCounts.getOrDefault(page, 0L) + 1);
        }
        List<Map.Entry<String, Long>> pageList = new ArrayList<>(pageCounts.entrySet());
        pageList.sort(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()));

        Map<String, Long> top5Pages = new LinkedHashMap<>();
        for (int i = 0; i < Math.min(5, pageList.size()); i++) {
            Map.Entry<String, Long> entry = pageList.get(i);
            top5Pages.put(entry.getKey(), entry.getValue());
        }
        return top5Pages;
    }

    @Override
    public Set<String> getUsersOnPage(String page) {
        Set<String> users = new HashSet<>();
        for (VisitLog log : logs) {
            if (log.getPage().equals(page)) {
                users.add(log.getUserId());
            }
        }
        return users;
    }

    @Override
    public double getAverageTimeOnPage(String page) {
        int totalDuration = 0;
        int count = 0;
        for (VisitLog log : logs) {
            if (log.getPage().equals(page)) {
                totalDuration += log.getDurationSeconds();
                count++;
            }
        }
        if (count == 0) {
            return 0.0;
        }
        return (double) totalDuration / count;
    }

    @Override
    public Set<String> getActiveUsers(int minPages) {
        Map<String, Set<String>> userPages = new HashMap<>();
        for (VisitLog log : logs) {
            String userId = log.getUserId();
            Set<String> pages = userPages.getOrDefault(userId, new HashSet<>());
            pages.add(log.getPage());
            userPages.put(userId, pages);
        }
        Set<String> activeUsers = new HashSet<>();
        for (Map.Entry<String, Set<String>> entry : userPages.entrySet()) {
            if (entry.getValue().size() >= minPages) {
                activeUsers.add(entry.getKey());
            }
        }
        return activeUsers;
    }

    @Override
    public List<VisitLog> getLastNVisits(int n) {
        List<VisitLog> sortedLogs = new ArrayList<>(logs);
        sortedLogs.sort(Comparator.comparing(VisitLog::getTimestamp, Comparator.reverseOrder()));
        List<VisitLog> result = new ArrayList<>();
        for (int i = 0; i < Math.min(n, sortedLogs.size()); i++) {
            result.add(sortedLogs.get(i));
        }
        return result;
    }

    @Override
    public Map<Integer, Long> getVisitsByHour() {
        Map<Integer, Long> visitsByHours = new TreeMap<>();
        for (VisitLog log : logs) {
            int hour = log.getTimestamp().getHour();
            visitsByHours.put(hour, visitsByHours.getOrDefault(hour, 0L) + 1);
        }
        return visitsByHours;
    }

    @Override
    public Map<String, List<List<VisitLog>>> getActiveUserSessions(long maxBreakMinutes) {
        Map<String, List<VisitLog>> logsByUser = groupLogsByUser();
        Map<String, List<List<VisitLog>>> result = new LinkedHashMap<>();

        for (Map.Entry<String, List<VisitLog>> entry : logsByUser.entrySet()) {
            String userId = entry.getKey();
            List<VisitLog> userLogs = entry.getValue();

            if (!isActiveUser(userLogs)) {
                continue;
            }
            List<List<VisitLog>> sessions = splitIntoSessions(userLogs, maxBreakMinutes);
            result.put(userId, sessions);
        }
        return result;
    }

    private Map<String, List<VisitLog>> groupLogsByUser() {
        Map<String, List<VisitLog>> logsByUser = new HashMap<>();
        for (VisitLog log : logs) {
            String userId = log.getUserId();
            if (!logsByUser.containsKey(userId)) {
                logsByUser.put(userId, new ArrayList<>());
            }
            logsByUser.get(userId).add(log);
        }
        return logsByUser;
    }

    private boolean isActiveUser(List<VisitLog> userLogs) {
        Set<String> uniquePages = new HashSet<>();
        for (VisitLog log : userLogs) {
            uniquePages.add(log.getPage());
        }
        return uniquePages.size() >= 2;
    }

    private List<List<VisitLog>> splitIntoSessions(List<VisitLog> userLogs, long maxBreakMinutes) {
        List<List<VisitLog>> sessions = new ArrayList<>();

        userLogs.sort(Comparator.comparing(VisitLog::getTimestamp));

        List<VisitLog> currentSession = new ArrayList<>();
        for (int i = 0; i < userLogs.size(); i++) {
            VisitLog current = userLogs.get(i);
            currentSession.add(current);

            if (i < userLogs.size() - 1) {
                VisitLog next = userLogs.get(i + 1);
                long minutes = ChronoUnit.MINUTES.between(
                        current.getTimestamp(),
                        next.getTimestamp()
                );
                if (minutes > maxBreakMinutes) {
                    sessions.add(new ArrayList<>(currentSession));
                    currentSession.clear();
                }
            }
        }
        if (!currentSession.isEmpty()) {
            sessions.add(new ArrayList<>(currentSession));
        }
        return sessions;
    }

    @Override
    public List<String> getUserPath(String userId) {
        List<String> path = new ArrayList<>();
        List<VisitLog> userLogs = new ArrayList<>();
        for (VisitLog log : logs) {
            if (log.getUserId().equals(userId)) {
                userLogs.add(log);
            }
        }
        userLogs.sort(Comparator.comparing(VisitLog::getTimestamp));

        for (VisitLog log : userLogs) {
            path.add(log.getPage());
        }
        return path;
    }

    @Override
    public Set<String> getAbandonedCarts() {
        Set<String> cartUsers = new HashSet<>();
        Set<String> checkoutUsers = new HashSet<>();
        for (VisitLog log : logs) {
            if (log.getPage().equals("/cart")) {
                cartUsers.add(log.getUserId());
            }
            if (log.getPage().equals("/checkout")) {
                checkoutUsers.add(log.getUserId());
            }
        }
        cartUsers.removeAll(checkoutUsers);
        return cartUsers;
    }
}
