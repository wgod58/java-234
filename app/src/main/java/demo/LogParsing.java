package demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogParsing {
    public static void executeLogParsing() {

        // Read the log file and create a list of log entries
        List<LogEntry> logEntries = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new FileReader("./access.log"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                String url = parts[0];
                int responseTime = Integer.parseInt(parts[1]);
                int httpStatusCode = Integer.parseInt(parts[2]);
                logEntries.add(new LogEntry(url, responseTime, httpStatusCode));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Group the log entries by domain
        Map<String, List<LogEntry>> logEntriesByDomain = new HashMap<>();
        for (LogEntry entry : logEntries) {
            String domain = getDomainFromUrl(entry.getUrl());
            if (!logEntriesByDomain.containsKey(domain)) {
                logEntriesByDomain.put(domain, new ArrayList<>());
            }
            logEntriesByDomain.get(domain).add(entry);
        }

        // Print out the statistics for each domain
        for (Map.Entry<String, List<LogEntry>> domainEntries : logEntriesByDomain.entrySet()) {
            String domain = domainEntries.getKey();
            List<LogEntry> entries = domainEntries.getValue();
            System.out.println("--- Statistics for domain " + domain + " ---");

            // Count the occurrences of the domain grouping by HTTP status code
            Map<Integer, Integer> httpStatusCodeCount = new HashMap<>();
            for (LogEntry entry : entries) {
                int httpStatusCode = entry.getHttpStatusCode();
                if (!httpStatusCodeCount.containsKey(httpStatusCode)) {
                    httpStatusCodeCount.put(httpStatusCode, 0);
                }
                httpStatusCodeCount.put(httpStatusCode, httpStatusCodeCount.get(httpStatusCode) + 1);
            }
            System.out.println("HTTP status code count: " + httpStatusCodeCount);

            // Average the response time
            int totalResponseTime = 0;
            for (LogEntry entry : entries) {
                totalResponseTime += entry.getResponseTime();
            }
            double averageResponseTime = (double) totalResponseTime / entries.size();
            System.out.println("Average response time: " + averageResponseTime);

            // Calculate the 99th percentile of the response time
            List<Integer> responseTimes = new ArrayList<>();
            for (LogEntry entry : entries) {
                responseTimes.add(entry.getResponseTime());
            }
            Collections.sort(responseTimes);
            int percentileIndex = (int) (entries.size() * 0.99);
            double percentileResponseTime = responseTimes.get(percentileIndex);
            System.out.println("99th percentile response time: " + percentileResponseTime);
        }
    }

    private static String getDomainFromUrl(String url) {
        try {
            URL urlObject = new URL(url);
            String host = urlObject.getHost();
            return host;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class LogEntry {
        private String url;
        private int responseTime;
        private int httpStatusCode;

        public LogEntry(String url, int responseTime, int httpStatusCode) {
            this.url = url;
            this.responseTime = responseTime;
            this.httpStatusCode = httpStatusCode;
        }

        public String getUrl() {
            return url;
        }

        public int getResponseTime() {
            return responseTime;
        }

        public int getHttpStatusCode() {
            return httpStatusCode;
        }
    }
}

