package org.example.LogGenerator;

import org.example.Model.VisitLog;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GenerateLogs {
    public List<VisitLog> generateLogs(int count) {
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
}
