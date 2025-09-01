package org.example.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class VisitLog {
    private String userId;           // например, "user_123"
    private String page;             // например, "/home", "/product/5"
    private LocalDateTime timestamp;
    private int durationSeconds; // время на страниц

    @Override
    public String toString() {
        return "VisitLog {\n" +
                "\tuserId = '" + userId + "',\n" +
                "\tpage = '" + page + "',\n" +
                "\ttimestamp = " + timestamp + ",\n" +
                "\tdurationSeconds = " + durationSeconds + "\n" +
                "}";
    }
}

