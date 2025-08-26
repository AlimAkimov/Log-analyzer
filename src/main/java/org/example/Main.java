package org.example;

import java.util.List;

import static org.example.VisitLog.generateLogs;

public class Main {
    public static void main(String[] args) {
        List<VisitLog> logs = generateLogs(300);
        WebLogAnalyzer analyzer = new WebLogAnalyzer(logs);

        System.out.println("Топ 5 страниц: " + analyzer.getPageViewsTop5());
        System.out.println("Уникальные пользователи на странице: " + analyzer.getUsersOnPage("/catalog"));
        System.out.println("Среднее время на странице: " + analyzer.getAverageTimeOnPage("/catalog"));
        System.out.println("Пользователи, посетившие минимум 2 страницы: " + analyzer.getActiveUsers(2));
        System.out.println("Последние n посещения сайта по времени: " + analyzer.getLastNVisits(2));
        System.out.println("Количество визитов по часам суток (0-23): " + analyzer.getVisitsByHour());
        System.out.println("Сессии активных пользователей: " + analyzer.getActiveUserSessions(30));
        System.out.println("Путь пользователя user_20: " + analyzer.getUserPath("user_20"));
        System.out.println("Брошенные корзины: " + analyzer.getAbandonedCarts());

    }
}