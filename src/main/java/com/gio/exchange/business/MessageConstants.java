package com.gio.exchange.business;

/**
 * The Class MessageConstants.
 */
public class MessageConstants {
    
    /** The Constant NO_CURRENCY_MESSAGE. */
    public static final String NO_CURRENCY_MESSAGE = "No data for requested currency present.";
    
    /** The Constant PARSING_ERROR_MESSAGE. */
    public static final String PARSING_ERROR_MESSAGE  = "Error occurred while XML parsing";
    
    /** The Constant EXPIRED_DATE_MESSAGE. */
    public static final String EXPIRED_DATE_MESSAGE = "Rate requested for the date not in the range. No Conversion data present for the date.";
    
    /** The Constant FUTURE_DATE_MESSAGE. */
    public static final String FUTURE_DATE_MESSAGE = "No data conversion data for future dates is present.";
    
    /** The Constant SUCCESS_MESSAGE. */
    public static final String SUCCESS_MESSAGE = "SUCCESS";
    
    /** The Constant FAILED_MESSAGE. */
    public static final String FAILED_MESSAGE = "FAILED";
    
    /** The Constant UNKNOWN_EXCEPTION_MESSAGE. */
    public static final String UNKNOWN_EXCEPTION_MESSAGE = "Unknown issue occurred during calculation.";

}
