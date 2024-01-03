package com.quantcast.activecookie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MostActiveCookieTest {

    @TempDir
    public File temporaryFolder;

    @Spy
    private MostActiveCookie mostActiveCookie;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenFileContent_whenGetMostActiveCookie_thenMostActiveCookiesReturned() throws IOException {
        Map<String, Integer> cookieCounts = spy(new HashMap<>());
        cookieCounts.put("cookie1", 2);
        cookieCounts.put("cookie2", 2);
        cookieCounts.put("cookie3", 1);

        doReturn(cookieCounts).when(mostActiveCookie).readCookieCountsFromFile(anyString(), anyString());

        List<String> cookies= mostActiveCookie.getMostActiveCookies(anyString(), anyString());

        assertEquals(Arrays.asList("cookie1", "cookie2"), cookies);
    }

    @Test
    void givenFileExists_whenReadCookie_mapWIthProperCountIsReturned() throws IOException {
        File cookieDataFile = new File(temporaryFolder, "cookie_data.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cookieDataFile))) {
            writer.write("cookie,timestamp\n");
            writer.write("cookie1,2023-01-01T12:00:00\n");
            writer.write("cookie1,2023-01-01T12:30:00\n");
            writer.write("cookie2,2023-01-01T13:00:00\n");
        }

        Map<String, Integer> expectedCookieCounts = new HashMap<>();
        expectedCookieCounts.put("cookie1", 2);
        expectedCookieCounts.put("cookie2", 1);

        Map<String, Integer> actualCookieCounts = mostActiveCookie
                .readCookieCountsFromFile(cookieDataFile.getPath(), "2023-01-01");

        assertEquals(expectedCookieCounts, actualCookieCounts);
    }

    @Test
    void givenCookieLine_whenUpdatingCounts_thenCountIncremented() {
        Map<String, Integer> mockCookieCounts = spy(Map.class);

        mostActiveCookie.updateCookieCounts("cookie1,2024-01-01T12:00:00", "2024-01-01", mockCookieCounts);
        verify(mockCookieCounts).put(eq("cookie1"), eq(1));
    }

    @Test
    void givenCookieCounts_whenFindingMaxCountInMap_thenReturnsMaxCount() {
        Map<String, Integer> mockCookieCounts = Map.of("cookie1", 3, "cookie2", 5, "cookie3", 2);

        int maxCount = mostActiveCookie.findMaxCountInMap(mockCookieCounts);
        assert (maxCount == 5);
    }
}
