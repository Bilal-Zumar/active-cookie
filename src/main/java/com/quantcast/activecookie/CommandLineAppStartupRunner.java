package com.quantcast.activecookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {

    private final MostActiveCookie mostActiveCookie;

    private static final Logger LOG =
            LoggerFactory.getLogger(CommandLineAppStartupRunner.class);

    public CommandLineAppStartupRunner(MostActiveCookie mostActiveCookie) {
        this.mostActiveCookie = mostActiveCookie;
    }

    @Override
    public void run(String... args) {
        LOG.info("Args" + Arrays.toString(args));

        if (args.length != 4 || !args[0].equals("-f") || !args[2].equals("-d")) {
            LOG.error("Usage: java MostActiveCookie -f <filename> -d <date>");
            return;
        }

        String filename = args[1];
        String date = args[3];

        try {
            List<String> mostActiveCookies = mostActiveCookie.getMostActiveCookies(filename, date);

            LOG.info("Most Active Cookies:");
            for (String cookie : mostActiveCookies) {
                System.out.println(cookie);
            }
        } catch (IOException e) {
            LOG.error("Error reading the file: " + e.getMessage());
        }
    }
}