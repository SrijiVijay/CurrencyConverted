package com.gio.exchange.business.calculation;

import com.gio.exchange.business.MessageConstants;
import com.gio.exchange.business.vo.CurrencyExchangeRequest;
import com.gio.exchange.storage.ConversionNoDataException;
import com.gio.exchange.storage.CurrencyKeeper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Map;

@Component
public class ExchangeCalculatorImpl implements ExchangeCalculator {

    @Value("${calculation.precision}")
    private int precision;

    @Autowired
    CurrencyKeeper keeper;

    @Scheduled(fixedRateString = "${refresh.interval.milliseconds}")
    public void refreshData(){
        keeper.refresh();
    }

    @Override
    public BigDecimal calculate(CurrencyExchangeRequest request) {
        Map<String, Float> rates = keeper.getRatesForDate(request.getDate());
        final String currencyFrom = request.getCurrencyFrom();
        final String currencyTo = request.getCurrencyTo();
        if(rates.containsKey(currencyFrom) && rates.containsKey(currencyTo)){
            if(currencyFrom.equals(currencyTo))
                return request.getAmount();
            final MathContext rounding = new MathContext(precision, RoundingMode.HALF_UP);
            final BigDecimal currencyFromRate = new BigDecimal(rates.get(currencyFrom));
            final BigDecimal currencyToRate = new BigDecimal(rates.get(currencyTo));
            BigDecimal amount = request.getAmount();
            return amount.multiply(currencyToRate).divide(currencyFromRate, rounding);
        } else{
            throw new ConversionNoDataException(MessageConstants.NO_CURRENCY_MESSAGE);
        }

    }

    public void setKeeper(CurrencyKeeper keeper) {
        this.keeper = keeper;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }
}
