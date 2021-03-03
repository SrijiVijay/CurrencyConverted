package com.gio.exchange.storage;

import com.gio.exchange.business.MessageConstants;
import com.gio.exchange.business.parsing.ConversionDataParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Class ECBCurrencyKeeper.
 */
@Repository
public class ECBCurrencyKeeper implements CurrencyKeeper {

    /** The days expired. */
    @Value("${rate.days.expired}")
    private int daysExpired;

    /** The rate daily URL. */
    @Value("${rate.daily.url}")
    private String rateDailyURL;

    /** The rate three month URL. */
    @Value("${rate.90.days.url}")
    private String rateThreeMonthURL;


    /** The currency rates. */
    private Map<LocalDate, Map<String,Float>> currencyRates = new ConcurrentHashMap<>();

    /** The parser. */
    @Autowired
    private ConversionDataParser parser;

    /**
     * Sets the days expired.
     *
     * @param days the new days expired
     */
    @Override
    public void setDaysExpired(int days) {
        daysExpired = days;
    }

    /**
     * Load.
     */
    @Override
    public void load() {
    	RestTemplate restTemplate = new RestTemplate();
	    ResponseEntity<String> response = restTemplate.getForEntity(rateThreeMonthURL, String.class);
        try(InputStream input = new URL(rateThreeMonthURL).openStream()) {
        	currencyRates.putAll(parser.parse(new ByteArrayInputStream(response.getBody().toString().getBytes(StandardCharsets.UTF_8))));
        } catch (IOException e) {
            throw new ECBConnectionException(e.getMessage(), e);
        }

    }

    /**
     * Refresh.
     */
    @Override
    public void refresh() {
        if(currencyRates.size() == 0)
            load();
        else if(currencyRates.containsKey(LocalDate.now())) // no need to refresh
            return;
        else {
            removeExpiredRates();
            loadTodayRate();
        }
    }

    /**
     * Removes the expired rates.
     */
    private void removeExpiredRates(){
        Set<LocalDate> datesToRemove = new HashSet<>();
        currencyRates.forEach((key, value) -> {if(isDateExpired(key)) datesToRemove.add(key);});
        datesToRemove.forEach(currencyRates::remove);
    }

    /**
     * Checks if is date expired.
     *
     * @param date the date
     * @return true, if is date expired
     */
    private boolean isDateExpired(LocalDate date){
        return LocalDate.now().minusDays(daysExpired).isAfter(date);
    }

    /**
     * Load today rate.
     */
    private void loadTodayRate(){
    	RestTemplate restTemplate = new RestTemplate();
	    ResponseEntity<String> response = restTemplate.getForEntity(rateDailyURL, String.class);
        try(InputStream input = new URL(rateDailyURL).openStream()) {
        	currencyRates.putAll(parser.parse(new ByteArrayInputStream(response.getBody().toString().getBytes(StandardCharsets.UTF_8))));
        } catch (IOException e) {
            throw new ECBConnectionException(e.getMessage(), e);
        }
    }


    /**
     * Gets the rates for date.
     *
     * @param requestDate the request date
     * @return the rates for date
     */
    @Override
    public Map<String, Float> getRatesForDate(LocalDate requestDate){
        if(isDateExpired(requestDate)){
            throw new ConversionNoDataException(MessageConstants.EXPIRED_DATE_MESSAGE);
        }
        if(requestDate.isAfter(LocalDate.now())){
            throw new ConversionNoDataException(MessageConstants.FUTURE_DATE_MESSAGE);
        }
        return getRateForNonHolidayDate(requestDate);
    }

    /**
     * Gets the number of days with rates.
     *
     * @return the number of days with rates
     */
    @Override
    public int getNumberOfDaysWithRates() {
        return currencyRates.size();
    }

    /**
     * Gets the rate for non holiday date.
     *
     * @param requestDate the request date
     * @return the rate for non holiday date
     */
    private Map<String, Float> getRateForNonHolidayDate(LocalDate requestDate){
        LocalDate lastNonHolidayDate = requestDate;
        while (isDatePresentInCurrencyRatesAndNotExpired(lastNonHolidayDate) ){
            lastNonHolidayDate = lastNonHolidayDate.minusDays(1);
        }
        if(isDateExpired(lastNonHolidayDate))
            throw new ConversionNoDataException(MessageConstants.EXPIRED_DATE_MESSAGE);
        return currencyRates.get(lastNonHolidayDate);
    }

    /**
     * Checks if is date present in currency rates and not expired.
     *
     * @param lastNonHolidayDate the last non holiday date
     * @return true, if is date present in currency rates and not expired
     */
    private boolean isDatePresentInCurrencyRatesAndNotExpired(LocalDate lastNonHolidayDate){
        return !currencyRates.containsKey(lastNonHolidayDate)
                && !isDateExpired(lastNonHolidayDate);
    }

    /**
     * Sets the parser.
     *
     * @param parser the new parser
     */
    @Override
    public void setParser(ConversionDataParser parser) {
        this.parser = parser;
    }

    /**
     * Gets the days expired.
     *
     * @return the days expired
     */
    @Override
    public int getDaysExpired() {
        return daysExpired;
    }

    /**
     * Sets the rate daily URL.
     *
     * @param rateDailyURL the new rate daily URL
     */
    public void setRateDailyURL(String rateDailyURL) {
        this.rateDailyURL = rateDailyURL;
    }

    /**
     * Sets the rate three month URL.
     *
     * @param rateThreeMonthURL the new rate three month URL
     */
    public void setRateThreeMonthURL(String rateThreeMonthURL) {
        this.rateThreeMonthURL = rateThreeMonthURL;
    }
}
