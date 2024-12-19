package com.infodation.user.MongoDbOAuth.utils;

import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DynamicFilterParser {

    public static Criteria parseFilter(Map<String, Object> filter) {
        if (filter == null || filter.isEmpty()) {
            return new Criteria();
        }

        List<Criteria> criteriaList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            String operator = entry.getKey().toUpperCase(); // AND, OR, CON, EQ, GTE, LTE, NOT, IN, NE, KEY
            Object value = entry.getValue();

            switch (operator) {
                case "AND" -> criteriaList.add(new Criteria().andOperator(parseSubFilters((Map<String, Object>) value)));
                case "OR" -> criteriaList.add(new Criteria().orOperator(parseSubFilters((Map<String, Object>) value)));
                case "CON" -> criteriaList.add(
                        Criteria.where(parseSingleKey((Map<String, Object>) value))
                                .regex(".*" + parseSingleValue((Map<String, Object>) value) + ".*", "i"));
                case "EQ" -> criteriaList.add(
                        Criteria.where(parseSingleKey((Map<String, Object>) value))
                                .is(parseSingleValue((Map<String, Object>) value)));
                case "GTE" -> criteriaList.add(
                        Criteria.where(parseSingleKey((Map<String, Object>) value))
                                .gte(parseSingleValue((Map<String, Object>) value)));
                case "LTE" -> criteriaList.add(
                        Criteria.where(parseSingleKey((Map<String, Object>) value))
                                .lte(parseSingleValue((Map<String, Object>) value)));
                case "NE" -> criteriaList.add(
                        Criteria.where(parseSingleKey((Map<String, Object>) value))
                                .ne(parseSingleValue((Map<String, Object>) value)));
                case "IN" -> criteriaList.add(
                        Criteria.where(parseSingleKey((Map<String, Object>) value))
                                .in(parseSingleValue((Map<String, Object>) value)));
                case "NOT" -> criteriaList.add(parseFilter((Map<String, Object>) value).not());
                case "KEY" -> {
                    if (value instanceof Map) {
                        Map<String, Object> mapValue = (Map<String, Object>) value;
                        Map.Entry<String, Object> keyEntry = mapValue.entrySet().iterator().next();
                        String key = keyEntry.getKey();
                        boolean existsValue = Boolean.parseBoolean(keyEntry.getValue().toString());
                        criteriaList.add(Criteria.where(key).exists(existsValue));
                    } else {
                        throw new IllegalArgumentException("Invalid value type for KEY");
                    }
                }
                default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
            }
        }

        return new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
    }

    private static Criteria[] parseSubFilters(Map<String, Object> subFilters) {
        return subFilters.entrySet().stream()
                .map(entry -> parseFilter(Map.of(entry.getKey(), entry.getValue())))
                .toArray(Criteria[]::new);
    }

    private static String parseSingleKey(Map<String, Object> value) {
        return value.keySet().iterator().next();
    }

    private static Object parseSingleValue(Map<String, Object> value) {
        return value.values().iterator().next();
    }
}
