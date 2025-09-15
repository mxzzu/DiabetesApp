package com.diabetesapp.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailyEntityUtils {

    public static <T extends DailyEntity> List<T> getDailyEntities(String username, List<T> allEntities) {
        List<T> dailyEntities = new ArrayList<>();
        LocalDate date = LocalDate.now();

        allEntities.forEach(entity -> {
            if (entity.username().equals(username) && entity.date().equals(date)) {
                dailyEntities.add(entity);
            }
        });

        return dailyEntities;
    }
}
