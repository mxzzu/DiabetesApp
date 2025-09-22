package com.diabetesapp.config;

import io.github.palexdev.materialfx.beans.BiPredicateBean;
import io.github.palexdev.materialfx.filter.base.AbstractFilter;
import io.github.palexdev.materialfx.utils.FXCollectors;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Stream;

public class DateFilter<T> extends AbstractFilter<T, LocalDate> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Constructor for the DateFilter object
     * @param name Name of the filter
     * @param extractor Extractor function used to access the LocalDate object
     */
    public DateFilter(String name, Function<T, LocalDate> extractor) {
        this(name, extractor, new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return FORMATTER.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string == null || string.trim().isEmpty()) {
                    return null;
                }
                try {
                    return LocalDate.parse(string, FORMATTER);
                } catch (DateTimeParseException e) {
                    System.err.println("Formato data non valido per '" + string + "'. Usare il formato dd/MM/yyyy.");
                    return null;
                }
            }
        });
    }

    /**
     * Constructor for the DateFilter object
     * @param name Name of the filter
     * @param extractor Extractor function used to access the LocalDate object
     * @param dateStringConverter Converter used to parse a String to a LocalDate and vice versa
     */
    public DateFilter(String name, Function<T, LocalDate> extractor, StringConverter<LocalDate> dateStringConverter) {
        super(name, extractor, dateStringConverter);
    }

    @Override
    protected ObservableList<BiPredicateBean<LocalDate, LocalDate>> defaultPredicates() {
        return Stream.<BiPredicateBean<LocalDate, LocalDate>>of(
                new BiPredicateBean<>("Is After", (date1, date2) -> date1 != null && date2 != null && date1.isAfter(date2)),
                new BiPredicateBean<>("Is Before", (date1, date2) -> date1 != null && date2 != null && date1.isBefore(date2)),
                new BiPredicateBean<>("Is", (date1, date2) -> date1 != null && date2 != null && date1.isEqual(date2)),
                new BiPredicateBean<>("Is Not", (date1, date2) -> date1 != null && date2 != null && !date1.isEqual(date2)),
                new BiPredicateBean<>("Is On or After", (date1, date2) -> date1 != null && date2 != null && (date1.isAfter(date2) || date1.isEqual(date2))),
                new BiPredicateBean<>("Is On or Before", (date1, date2) -> date1 != null && date2 != null && (date1.isBefore(date2) || date1.isEqual(date2)))
        ).collect(FXCollectors.toList());
    }

    @SafeVarargs
    @Override
    protected final DateFilter<T> extend(BiPredicateBean<LocalDate, LocalDate>... predicateBeans) {
        Collections.addAll(super.predicates, predicateBeans);
        return this;
    }
}
