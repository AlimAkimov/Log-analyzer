package org.example.Analyzer;

import org.example.Model.VisitLog;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebLogAnalyzerTest {

    private static List<VisitLog> createLogs() {
        return List.of(
                new VisitLog("user_1", "/home", LocalDateTime.of(2025, 8, 26, 12, 34), 5),
                new VisitLog("user_2", "/home", LocalDateTime.of(2025, 8, 26, 10, 12), 78),
                new VisitLog("user_4", "/home", LocalDateTime.of(2025, 8, 26, 10, 0), 48),
                new VisitLog("user_3", "/product/1", LocalDateTime.of(2025, 8, 26, 11, 18), 250),
                new VisitLog("user_2", "/cart", LocalDateTime.of(2025, 8, 26, 11, 48), 40),
                new VisitLog("user_4", "/checkout", LocalDateTime.of(2025, 8, 26, 6, 11), 106),
                new VisitLog("user_4", "/product/1", LocalDateTime.of(2025, 8, 26, 11, 12), 130)
        );
    }

    private static List<Arguments> analyzers() {
        List<VisitLog> logs = createLogs();
        return List.of(
                Arguments.of("Stream API", new WebLogAnalyzerImpl(logs)),
                Arguments.of("Loops", new WebLogAnalyzerWithoutStreamAPIImpl(logs))
        );
    }

    private WebLogAnalyzer createEmptyAnalyzer(String name) {
        if (name.equals("Stream API")) {
            return new WebLogAnalyzerImpl(new ArrayList<>());
        } else {
            return new WebLogAnalyzerWithoutStreamAPIImpl(new ArrayList<>());
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("analyzers")
    void testGetPageViewsTop5(String name, WebLogAnalyzer analyzer) {
        Map<String, Long> result = analyzer.getPageViewsTop5();
        assertEquals(4, result.size());
        assertEquals(1, result.get("/cart"));
        List<String> pages = new ArrayList<>(result.keySet());
        assertEquals("/home", pages.get(0));
        assertEquals("/product/1", pages.get(1));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("analyzers")
    void testGetPageViewsTop5EmptyLogs(String name, WebLogAnalyzer analyzer) {
        WebLogAnalyzer emptyAnalyzer = createEmptyAnalyzer(name);
        Map<String, Long> result = emptyAnalyzer.getPageViewsTop5();
        assertTrue(result.isEmpty());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("analyzers")
    void testGetUsersOnPage(String name, WebLogAnalyzer analyzer) {
        Set<String> users = analyzer.getUsersOnPage("/product/1");
        assertEquals(2, users.size());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("analyzers")
    void testGetAverageTimeOnPage(String name, WebLogAnalyzer analyzer) {
        double avg = analyzer.getAverageTimeOnPage("/home");
        assertEquals((5 + 78 + 48) / 3.0, avg);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("analyzers")
    void testGetActiveUsers(String name, WebLogAnalyzer analyzer) {
        Set<String> active = analyzer.getActiveUsers(2);
        assertEquals(2, active.size());
        assertTrue(active.contains("user_4"));
        assertTrue(active.contains("user_2"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("analyzers")
    void testGetLastVisits(String name, WebLogAnalyzer analyzer) {
        List<VisitLog> result = analyzer.getLastNVisits(3);
        assertEquals(3, result.size());
        assertTrue(result.get(0).getTimestamp().isAfter(result.get(1).getTimestamp()));
        assertTrue(result.get(1).getTimestamp().isAfter(result.get(2).getTimestamp()));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("analyzers")
    void testGetVisitsByHour(String name, WebLogAnalyzer analyzer) {
        Map<Integer, Long> visits = analyzer.getVisitsByHour();
        assertEquals(4, visits.size());
        assertEquals(3L, visits.get(11));
        assertEquals(2L, visits.get(10));
        assertEquals(1L, visits.get(6));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("analyzers")
    void testGetActiveUserSessions(String name, WebLogAnalyzer analyzer) {
        Map<String, List<List<VisitLog>>> sessions = analyzer.getActiveUserSessions(30);
        assertEquals(2, sessions.size());

        List<List<VisitLog>> user2Sessions = sessions.get("user_2");
        assertEquals(2, user2Sessions.size());
        assertEquals("/home", user2Sessions.get(0).get(0).getPage());
        assertEquals("/cart", user2Sessions.get(1).get(0).getPage());

        List<List<VisitLog>> user4Sessions = sessions.get("user_4");
        assertEquals(3, user4Sessions.size());
        assertEquals("/checkout", user4Sessions.get(0).get(0).getPage());
        assertEquals("/product/1", user4Sessions.get(2).get(0).getPage());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("analyzers")
    void testGetActiveUserSessionsEmptyLogs(String name, WebLogAnalyzer analyzer) {
        WebLogAnalyzer emptyAnalyzer = createEmptyAnalyzer(name);
        Map<String, List<List<VisitLog>>> sessions = emptyAnalyzer.getActiveUserSessions(30);
        assertTrue(sessions.isEmpty());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("analyzers")
    void testGetUserPath(String name, WebLogAnalyzer analyzer) {
        List<String> path = analyzer.getUserPath("user_2");
        assertEquals(2, path.size());
        assertEquals("/home", path.get(0));
        assertEquals("/cart", path.get(1));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("analyzers")
    void testGetUserPathNonExistent(String name, WebLogAnalyzer analyzer) {
        List<String> path = analyzer.getUserPath("user_999");
        assertTrue(path.isEmpty());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("analyzers")
    void testGetAbandonedCarts(String name, WebLogAnalyzer analyzer) {
        Set<String> abandonedCarts = analyzer.getAbandonedCarts();
        assertEquals(1, abandonedCarts.size());
        assertTrue(abandonedCarts.contains("user_2"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("analyzers")
    void testGetAbandonedCartsEmptyLogs(String name, WebLogAnalyzer analyzer) {
        WebLogAnalyzer emptyAnalyzer = createEmptyAnalyzer(name);
        Set<String> abandonedCarts = emptyAnalyzer.getAbandonedCarts();
        assertTrue(abandonedCarts.isEmpty());
    }
}

