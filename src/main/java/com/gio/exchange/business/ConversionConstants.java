package com.gio.exchange.business;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * The Class ConversionConstants.
 */
public class ConversionConstants {
    
    /** The Constant DATE_ATTRIBUTE_FORMAT. */
    public static final String DATE_ATTRIBUTE_FORMAT = "yyyy-MM-dd";
    
    /** The Constant DATE_FORMATTER. */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_ATTRIBUTE_FORMAT).withZone(ZoneId.of("CET"));
}
