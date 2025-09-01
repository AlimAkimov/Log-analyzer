package org.example;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public interface WebLogAnalyzer {
    Map<String, Long> getPageViewsTop5();

    Set<String> getUsersOnPage(String page); // Уникальные пользователи на странице

    double getAverageTimeOnPage(String page); // Среднее время на странице

    Set<String> getActiveUsers(int minPages); // Пользователи, посетившие 2+ разные страницы

    List<VisitLog> getLastNVisits(int n); // Последние N посещений сайта (по времени)

    Map<Integer, Long> getVisitsByHour(); // Количество визитов по часам суток (0-23)

    Map<String, List<List<VisitLog>>> getActiveUserSessions(long maxBreakMinutes);

    List<String> getUserPath(String userId);

    Set<String> getAbandonedCarts();
}
