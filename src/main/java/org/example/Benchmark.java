package org.example;

import java.util.List;

import static org.example.VisitLog.generateLogs;

public class Benchmark {
    public void benchStreamVsLoops() {
        List<VisitLog> logs = generateLogs(1000000);

        WebLogAnalyzer analyzerStream = new WebLogAnalyzerImpl(logs);
        WebLogAnalyzer analyzerLoops = new WebLogAnalyzerWithoutStreamAPIImpl(logs);

        // Тест для getPageViewsTop5
        analyzerStream.getPageViewsTop5();
        analyzerLoops.getPageViewsTop5();
        long start = System.nanoTime();
        analyzerStream.getPageViewsTop5();
        double timeStream = (System.nanoTime() - start) / 1000000.0;
        start = System.nanoTime();
        analyzerLoops.getPageViewsTop5();
        double timeLoops = (System.nanoTime() - start) / 1000000.0;
        System.out.println("Топ-5 страниц:");
        System.out.println("Stream API: " + rounding(timeStream) + " ms");
        System.out.println("Циклы: " + rounding(timeLoops) + " ms");
        System.out.println("Разница: " + rounding(timeStream - timeLoops) + " ms");

        // Тест для getUsersOnPage
        analyzerStream.getUsersOnPage("/home");
        analyzerLoops.getUsersOnPage("/home");
        start = System.nanoTime();
        analyzerStream.getUsersOnPage("/home");
        timeStream = (System.nanoTime() - start) / 1000000.0;
        start = System.nanoTime();
        analyzerLoops.getUsersOnPage("/home");
        timeLoops = (System.nanoTime() - start) / 1000000.0;
        System.out.println("\nПользователи на странице:");
        System.out.println("Stream API: " + rounding(timeStream) + " ms");
        System.out.println("Циклы: " + rounding(timeLoops) + " ms");
        System.out.println("Разница: " + rounding(timeStream - timeLoops) + " ms");

        // Тест для getAverageTimeOnPage
        analyzerStream.getAverageTimeOnPage("/home");
        analyzerLoops.getAverageTimeOnPage("/home");
        start = System.nanoTime();
        analyzerStream.getAverageTimeOnPage("/home");
        timeStream = (System.nanoTime() - start) / 1000000.0;
        start = System.nanoTime();
        analyzerLoops.getAverageTimeOnPage("/home");
        timeLoops = (System.nanoTime() - start) / 1000000.0;
        System.out.println("\nСреднее время на странице:");
        System.out.println("Stream API: " + rounding(timeStream) + " ms");
        System.out.println("Циклы: " + rounding(timeLoops) + " ms");
        System.out.println("Разница: " + rounding(timeStream - timeLoops) + " ms");

        // Тест для getActiveUsers
        analyzerStream.getActiveUsers(2);
        analyzerLoops.getActiveUsers(2);
        start = System.nanoTime();
        analyzerStream.getActiveUsers(2);
        timeStream = (System.nanoTime() - start) / 1000000.0;
        start = System.nanoTime();
        analyzerLoops.getActiveUsers(2);
        timeLoops = (System.nanoTime() - start) / 1000000.0;
        System.out.println("\nАктивные пользователи:");
        System.out.println("Stream API: " + rounding(timeStream) + " ms");
        System.out.println("Циклы: " + rounding(timeLoops) + " ms");
        System.out.println("Разница: " + rounding(timeStream - timeLoops) + " ms");

        // Тест для getLastNVisits
        analyzerStream.getLastNVisits(5);
        analyzerLoops.getLastNVisits(5);
        start = System.nanoTime();
        analyzerStream.getLastNVisits(5);
        timeStream = (System.nanoTime() - start) / 1000000.0;
        start = System.nanoTime();
        analyzerLoops.getLastNVisits(5);
        timeLoops = (System.nanoTime() - start) / 1000000.0;
        System.out.println("\nПоследние посещения:");
        System.out.println("Stream API: " + rounding(timeStream) + " ms");
        System.out.println("Циклы: " + rounding(timeLoops) + " ms");
        System.out.println("Разница: " + rounding(timeStream - timeLoops) + " ms");

        // Тест для getVisitsByHour
        analyzerStream.getVisitsByHour();
        analyzerLoops.getVisitsByHour();
        start = System.nanoTime();
        analyzerStream.getVisitsByHour();
        timeStream = (System.nanoTime() - start) / 1000000.0;
        start = System.nanoTime();
        analyzerLoops.getVisitsByHour();
        timeLoops = (System.nanoTime() - start) / 1000000.0;
        System.out.println("\nПосещения по часам:");
        System.out.println("Stream API: " + rounding(timeStream) + " ms");
        System.out.println("Циклы: " + rounding(timeLoops) + " ms");
        System.out.println("Разница: " + rounding(timeStream - timeLoops) + " ms");

        // Тест для getActiveUserSessions
        analyzerStream.getActiveUserSessions(30);
        analyzerLoops.getActiveUserSessions(30);
        start = System.nanoTime();
        analyzerStream.getActiveUserSessions(30);
        timeStream = (System.nanoTime() - start) / 1000000.0;
        start = System.nanoTime();
        analyzerLoops.getActiveUserSessions(30);
        timeLoops = (System.nanoTime() - start) / 1000000.0;
        System.out.println("\nСессии пользователей:");
        System.out.println("Stream API: " + rounding(timeStream) + " ms");
        System.out.println("Циклы: " + rounding(timeLoops) + " ms");
        System.out.println("Разница: " + rounding(timeStream - timeLoops) + " ms");

        // Тест для getUserPath
        analyzerStream.getUserPath("user_1");
        analyzerLoops.getUserPath("user_1");
        start = System.nanoTime();
        analyzerStream.getUserPath("user_1");
        timeStream = (System.nanoTime() - start) / 1000000.0;
        start = System.nanoTime();
        analyzerLoops.getUserPath("user_1");
        timeLoops = (System.nanoTime() - start) / 1000000.0;
        System.out.println("\nПуть пользователя:");
        System.out.println("Stream API: " + rounding(timeStream) + " ms");
        System.out.println("Циклы: " + rounding(timeLoops) + " ms");
        System.out.println("Разница: " + rounding(timeStream - timeLoops) + " ms");

        // Тест для getAbandonedCarts
        analyzerStream.getAbandonedCarts();
        analyzerLoops.getAbandonedCarts();
        start = System.nanoTime();
        analyzerStream.getAbandonedCarts();
        timeStream = (System.nanoTime() - start) / 1000000.0;
        start = System.nanoTime();
        analyzerLoops.getAbandonedCarts();
        timeLoops = (System.nanoTime() - start) / 1000000.0;
        System.out.println("\nБрошенные корзины:");
        System.out.println("Stream API: " + rounding(timeStream) + " ms");
        System.out.println("Циклы: " + rounding(timeLoops) + " ms");
        System.out.println("Разница: " + rounding(timeStream - timeLoops) + " ms");
    }

    private static double rounding(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
    }
