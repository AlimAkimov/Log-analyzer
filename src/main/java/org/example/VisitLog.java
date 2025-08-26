package org.example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VisitLog {

    private String userId;           // например, "user_123"
    private String page;             // например, "/home", "/product/5"
    private LocalDateTime timestamp;
    private int durationSeconds; // время на страниц

    public VisitLog(String userId, String page, LocalDateTime timestamp, int durationSeconds) {
        this.userId = userId;
        this.page = page;
        this.timestamp = timestamp;
        this.durationSeconds = durationSeconds;
    }

    static List<VisitLog> generateLogs(int count) {
        List<String> pages = List.of("/home", "/catalog", "/product/1", "/product/2", "/cart", "/checkout");
        Random random = new Random();
        List<VisitLog> logs = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String userId = "user_" + random.nextInt(100);  // 100 пользователей
            String page = pages.get(random.nextInt(pages.size()));
            LocalDateTime time = LocalDateTime.now().minusMinutes(random.nextInt(10080)); // за неделю
            int duration = random.nextInt(300); // до 5 минут
            logs.add(new VisitLog(userId, page, time, duration));
        }
        return logs;
    }

    @Override
    public String toString() {
        return "VisitLog {\n" +
                "\tuserId = '" + userId + "',\n" +
                "\tpage = '" + page + "',\n" +
                "\ttimestamp = " + timestamp + ",\n" +
                "\tdurationSeconds = " + durationSeconds + "\n" +
                "}";
    }

    public String getUserId() {
        return userId;
    }

    public String getPage() {
        return page;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

}

