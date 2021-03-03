package com.gio.exchange.business.parsing;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Map;

public interface ConversionDataParser {
    Map<LocalDate, Map<String,Float>> parse(InputStream inputData);
}
