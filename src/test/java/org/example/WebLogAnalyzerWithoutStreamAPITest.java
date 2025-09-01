package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebLogAnalyzerWithoutStreamAPITest {
    private List<VisitLog> logs;
    private WebLogAnalyzer logAnalyzer;

    @BeforeEach
    void setUp() {
        logs = List.of(
                new VisitLog("user_1", "/home", LocalDateTime.of(2025, 8, 26, 12, 34), 5),
                new VisitLog("user_2", "/home", LocalDateTime.of(2025, 8, 26, 10, 12), 78),
                new VisitLog("user_4", "/home", LocalDateTime.of(2025, 8, 26, 10, 0), 48),
                new VisitLog("user_3", "/product/1", LocalDateTime.of(2025, 8, 26, 11, 18), 250),
                new VisitLog("user_2", "/cart", LocalDateTime.of(2025, 8, 26, 11, 48), 40),
                new VisitLog("user_4", "/checkout", LocalDateTime.of(2025, 8, 26, 6, 11), 106),
                new VisitLog("user_4", "/product/1", LocalDateTime.of(2025, 8, 26, 11, 12), 130)

        );
        logAnalyzer = new WebLogAnalyzerWithoutStreamAPIImpl(logs);
    }

    @Test
    void testGetPageViewsTop5() {
        Map<String, Long> result = logAnalyzer.getPageViewsTop5();
        assertEquals(4, result.size());
        assertEquals(1, result.get("/cart"));
        List<String> pages = new ArrayList<>(result.keySet());
        assertEquals("/home", pages.get(0));
        assertEquals("/product/1", pages.get(1));
    }

    @Test
    void testGetPageViewsTop5EmptyLogs() {
        WebLogAnalyzerImpl emptyAnalyzer = new WebLogAnalyzerImpl(new ArrayList<>());
        Map<String, Long> result = emptyAnalyzer.getPageViewsTop5();
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUsersOnPage() {
        Set<String> users = logAnalyzer.getUsersOnPage("/product/1");
        assertEquals(2, users.size());
    }

    @Test
    void testGetAverageTimeOnPage() {
        double avg = logAnalyzer.getAverageTimeOnPage("/home");
        assertEquals((5 + 78 + 48) / 3.0, avg);
    }

    @Test
    void testGetActiveUsers() {
        Set<String> active = logAnalyzer.getActiveUsers(2);
        assertEquals(2, active.size());
        assertTrue(active.contains("user_4"));
        assertTrue(active.contains("user_2"));
    }

    @Test
    void testGetLastVisits() {
        List<VisitLog> result = logAnalyzer.getLastNVisits(3);
        assertEquals(3, result.size());
        assertTrue(result.get(0).getTimestamp().isAfter(result.get(1).getTimestamp()));
        assertTrue(result.get(1).getTimestamp().isAfter(result.get(2).getTimestamp()));
    }

    @Test
    void testGetVisitsByHour() {
        Map<Integer, Long> visits = logAnalyzer.getVisitsByHour();
        assertEquals(4, visits.size());
        assertEquals(3L, visits.get(11));
        assertEquals(2L, visits.get(10));
        assertEquals(1L, visits.get(6));
    }

    @Test
    void testGetActiveUserSessions() {
        Map<String, List<List<VisitLog>>> sessions = logAnalyzer.getActiveUserSessions(30);
        assertEquals(2, sessions.size());

        List<List<VisitLog>> user2Sessions = sessions.get("user_2");
        assertEquals(2, user2Sessions.size());
        assertEquals(1, user2Sessions.get(0).size());
        assertEquals("/home", user2Sessions.get(0).get(0).getPage());
        assertEquals("/cart", user2Sessions.get(1).get(0).getPage());

        List<List<VisitLog>> user4Sessions = sessions.get("user_4");
        assertEquals(3, user4Sessions.size());
        assertEquals(1, user4Sessions.get(0).size());
        assertEquals("/checkout", user4Sessions.get(0).get(0).getPage());
        assertEquals("/product/1", user4Sessions.get(2).get(0).getPage());
    }

    @Test
    void testGetActiveUserSessionsEmptyLogs() {
        WebLogAnalyzerImpl emptyAnalyzer = new WebLogAnalyzerImpl(new ArrayList<>());
        Map<String, List<List<VisitLog>>> sessions = emptyAnalyzer.getActiveUserSessions(30);
        assertTrue(sessions.isEmpty());
    }

    @Test
    void testGetUserPath() {
        List<String> path = logAnalyzer.getUserPath("user_2");
        assertEquals(2, path.size());
        assertEquals("/home", path.get(0));
        assertEquals("/cart", path.get(1));
    }

    @Test
    void testGetUserPathNonExistent() {
        List<String> path = logAnalyzer.getUserPath("user_999");
        assertTrue(path.isEmpty());
    }

    @Test
    void testGetAbandonedCarts() {
        Set<String> abandonedCarts = logAnalyzer.getAbandonedCarts();
        assertEquals(1, abandonedCarts.size());
        assertTrue(abandonedCarts.contains("user_2"));
    }

    @Test
    void testGetAbandonedCartsEmptyLogs() {
        WebLogAnalyzerImpl emptyAnalyzer = new WebLogAnalyzerImpl(new ArrayList<>());
        Set<String> abandonedCarts = emptyAnalyzer.getAbandonedCarts();
        assertTrue(abandonedCarts.isEmpty());
    }

}
