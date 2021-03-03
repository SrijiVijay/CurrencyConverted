package com.gio.exchange.storage;

import java.time.LocalDate;
import java.util.Map;

import com.gio.exchange.business.parsing.ConversionDataParser;

public interface CurrencyKeeper {

    void setDaysExpired(int days);

    void load();

    void refresh();

    Map<String, Float> getRatesForDate(LocalDate requestDate);

    int getNumberOfDaysWithRates();

    void setParser(ConversionDataParser parser);

    int getDaysExpired();

}
