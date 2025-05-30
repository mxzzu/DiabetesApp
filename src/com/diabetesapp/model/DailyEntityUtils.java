package com.diabetesapp.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DailyEntityUtils {

    public static <T extends DailyEntity> List<T> getDailyEntities(String username, List<T> allEntities) {
        List<T> dailyEntities = new ArrayList<>();
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        allEntities.forEach(entity -> {
            if (entity.getUsername().equals(username) && entity.getDate().equals(date)) {
                dailyEntities.add(entity);
            }
        });

        return dailyEntities;
    }
}
