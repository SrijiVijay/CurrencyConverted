package com.gio.exchange.business.parsing;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Map;

import com.gio.exchange.business.parsing.ConversionDataParser;
import com.gio.exchange.business.parsing.ECBCurrencySAXParser;

public class ECBCurrencySAXStubParser implements ConversionDataParser {

    private ECBCurrencySAXParser parser = new ECBCurrencySAXParser();

    private String stubInputData;

    @Override
    public Map<LocalDate, Map<String, Float>> parse(InputStream inputData) {
        // ignores input stream, parses stubInputData in any case
        return parser.parse(new ByteArrayInputStream(stubInputData.getBytes()));
    }

    public void setStubInputData(String stubInputData) {
        this.stubInputData = stubInputData;
    }
}
