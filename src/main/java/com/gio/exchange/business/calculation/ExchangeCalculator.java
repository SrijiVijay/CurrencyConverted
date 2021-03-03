package com.gio.exchange.business.calculation;

import java.math.BigDecimal;

import com.gio.exchange.business.vo.CurrencyExchangeRequest;

public interface ExchangeCalculator {

    BigDecimal calculate(CurrencyExchangeRequest request);

}
