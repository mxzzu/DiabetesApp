package com.diabetesapp.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailyEntityUtils {

    /**
     * Returns a list of daily entities based on the username and list given
     * @param username Username to filter
     * @param allEntities List of all entities to filter
     * @return Returns a List of T elements
     * @param <T> Can be either Detection or Intake
     */
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
