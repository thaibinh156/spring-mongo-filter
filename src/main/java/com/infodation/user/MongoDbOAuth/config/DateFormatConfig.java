package com.infodation.user.MongoDbOAuth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Configuration
public class DateFormatConfig {
    @Bean
    public SimpleDateFormat dateOfBirthFormatter() {
        return new SimpleDateFormat("dd-MM-yyyy");
    }

    @Bean
    public SimpleDateFormat getDdMMyyyyFormatter() {
        return new SimpleDateFormat("dd-MM-yyyy");
    }

    @Bean
    public SimpleDateFormat getIsoDateFormatter() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    @Bean
    public SimpleDateFormat getDateFormatter() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    }

    public DateTimeFormatter getFormatter(String dateString) {
        if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}", dateString)) {
            return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        }

        if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}", dateString)) {
            return DateTimeFormatter.ofPattern("yyyy-MM-dd");
        }

        if (Pattern.matches("\\d{2}-\\d{2}-\\d{4}", dateString)) {
            return DateTimeFormatter.ofPattern("dd-MM-yyyy");
        }

        if (Pattern.matches("\\d{2}/\\d{2}/\\d{4}", dateString)) {
            return DateTimeFormatter.ofPattern("MM/dd/yyyy");
        }

        if (Pattern.matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}", dateString)) {
            return DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        }

        if (Pattern.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{6}", dateString)) {
            return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        }
        throw new IllegalArgumentException("Unknown date format for string: " + dateString);
    }
}
