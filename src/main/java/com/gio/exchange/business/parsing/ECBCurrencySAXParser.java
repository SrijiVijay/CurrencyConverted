package com.gio.exchange.business.parsing;

import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.gio.exchange.business.ConversionConstants;
import com.gio.exchange.business.MessageConstants;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
public class ECBCurrencySAXParser extends DefaultHandler implements ConversionDataParser {

    public LocalDate referenceDate;
    private Map<LocalDate, Map<String,Float>> parsedMap = new HashMap<>();

    public static final String ELEMENT_NAME = "Cube";
    public static final String DATE_ATTRIBUTE_NAME = "time";
    public static final String CURRENCY_ATTRIBUTE_NAME = "currency";
    public static final String RATE_ATTRIBUTE_NAME = "rate";
    public static final String DEFAULT_CURRENCY = "EUR";
    public static final Float DEFAULT_RATE = 1.0000000f;


    @Override
    public Map<LocalDate, Map<String,Float>> parse(InputStream inputData){
        parsedMap = new HashMap<>();
        try {

            XMLReader saxReader = XMLReaderFactory.createXMLReader();
            saxReader.setContentHandler(this);
            saxReader.setErrorHandler(this);
            saxReader.parse(new InputSource(inputData));

        } catch (Exception e) {
            throw new ConversionParsingException(MessageConstants.PARSING_ERROR_MESSAGE, e);
        }
        return parsedMap;
    }

    @Override
    public void startElement (String uri, String localName,
                              String qName, Attributes attributes) throws SAXException {
        if (ELEMENT_NAME.equals(localName)) {
            parseDate(attributes);
            parseCurrencyAndRate(attributes);
        }
    }

    private void parseDate(Attributes attributes){
        String date = attributes.getValue(DATE_ATTRIBUTE_NAME);
        if (date != null) {
            referenceDate = LocalDate.parse(date, ConversionConstants.DATE_FORMATTER);
            Map<String, Float> currencyMap = new HashMap<>();
            currencyMap.put(DEFAULT_CURRENCY, DEFAULT_RATE);
            parsedMap.put(referenceDate, currencyMap);
        }
    }

    private void parseCurrencyAndRate(Attributes attributes){
        String currency = attributes.getValue(CURRENCY_ATTRIBUTE_NAME);
        String rate = attributes.getValue(RATE_ATTRIBUTE_NAME);
        if (currency != null && rate != null) {
            try {
                parsedMap.get(referenceDate).put(currency, Float.valueOf(rate));
            } catch (Exception e) {
                throw new ConversionParsingException(MessageConstants.PARSING_ERROR_MESSAGE, e);
            }
        }
    }




}
