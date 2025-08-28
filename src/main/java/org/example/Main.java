package org.example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.example.VisitLog.generateLogs;

public class Main {
    public static void main(String[] args) {
        Benchmark benchmark = new Benchmark();
        benchmark.benchStreamVsLoops();
    }
}