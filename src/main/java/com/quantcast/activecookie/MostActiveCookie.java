package com.quantcast.activecookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MostActiveCookie {

    private static final Logger LOG = LoggerFactory.getLogger(MostActiveCookie.class);

    public List<String> getMostActiveCookies(final String filename, final String date) throws IOException {
        LOG.info("Start processing cookies for date: {}", date);
        Map<String, Integer> cookieCounts = readCookieCountsFromFile(filename, date);

        int maxCount = findMaxCountInMap(cookieCounts);
        LOG.info("Max count of cookies for date {} is: {}", date, maxCount);

        return findMostActiveCookies(cookieCounts, maxCount);
    }

    public Map<String, Integer> readCookieCountsFromFile(final String filename, final String date) throws IOException {
        Map<String, Integer> cookieCounts = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                updateCookieCounts(line, date, cookieCounts);
            }
        }

        LOG.info("Read {} unique cookies from the file.", cookieCounts.size());
        return cookieCounts;
    }

    public void updateCookieCounts(final String line, final String date, final Map<String, Integer> cookieCounts) {
        String[] parts = line.split(",");
        String cookie = parts[0];
        String timestamp = parts[1];

        LocalDateTime logDateTime = LocalDateTime.parse(timestamp.trim(), DateTimeFormatter.ISO_DATE_TIME);
        LocalDate logDate = logDateTime.toLocalDate();

        if (logDate.toString().equals(date)) {
            cookieCounts.put(cookie, cookieCounts.getOrDefault(cookie, 0) + 1);
        }
    }

    public int findMaxCountInMap(final Map<String, Integer> cookieCounts) {
        int maxCount = cookieCounts.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        LOG.debug("Max count among all cookies is: {}", maxCount);
        return maxCount;
    }

    public List<String> findMostActiveCookies(final Map<String, Integer> cookieCounts, final int maxCount) {
        List<String> mostActiveCookies = cookieCounts.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCount)
                .map(Map.Entry::getKey).toList();

        LOG.info("Found {} most active cookies.", mostActiveCookies.size());
        return mostActiveCookies;
    }
}
