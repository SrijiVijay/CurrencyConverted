package com.gio.exchange.business.calculation;

import com.gio.exchange.business.calculation.ExchangeCalculator;
import com.gio.exchange.business.calculation.ExchangeCalculatorImpl;
import com.gio.exchange.business.parsing.ECBCurrencySAXStubParser;
import com.gio.exchange.business.vo.CurrencyExchangeRequest;
import com.gio.exchange.storage.ConversionNoDataException;
import com.gio.exchange.storage.CurrencyKeeper;
import com.gio.exchange.storage.ECBCurrencyKeeper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(value="classpath:/application.properties")
public class ExchangeCalculatorTest {

    @Value(value = "${rate.daily.url}")
    private String rateDailyURL;

    @Value("${rate.90.days.url}")
    private String rateThreeMonthURL;

    @Value("${calculation.precision}")
    private int precision;

    private MathContext mathContext;

    private ExchangeCalculator calculator;

    @Before
    public void initCalculator(){
        CurrencyKeeper keeper = new ECBCurrencyKeeper();
        ((ECBCurrencyKeeper)keeper).setRateDailyURL(rateDailyURL);
        ((ECBCurrencyKeeper)keeper).setRateThreeMonthURL(rateThreeMonthURL);
        ECBCurrencySAXStubParser parser = new ECBCurrencySAXStubParser();
        parser.setStubInputData(SAMPLE_XML_NEW_DATE);
        keeper.setParser(parser);
        keeper.load();
        calculator = new ExchangeCalculatorImpl();
        ((ExchangeCalculatorImpl)calculator).setKeeper(keeper);
        ((ExchangeCalculatorImpl)calculator).setPrecision(precision);
        mathContext = new MathContext(precision, RoundingMode.HALF_UP);
    }

    @Test
    public void calculateEurToUsd(){
        CurrencyExchangeRequest request = new CurrencyExchangeRequest("EUR", "USD",
                LocalDate.now(), BigDecimal.valueOf(AMOUNT));
        BigDecimal result = calculator.calculate(request);
        Assert.assertEquals(calculateTestRate(AMOUNT, EUR_RATE, USD_RATE), result.stripTrailingZeros());
    }

    @Test
    public void calculateUsdToEur(){
        CurrencyExchangeRequest request = new CurrencyExchangeRequest("USD", "EUR",
                LocalDate.now(), BigDecimal.valueOf(AMOUNT));
        BigDecimal result = calculator.calculate(request);
        Assert.assertEquals(calculateTestRate(AMOUNT, USD_RATE, EUR_RATE),result);
    }

    @Test
    public void calculateUsdToJpy(){
        CurrencyExchangeRequest request = new CurrencyExchangeRequest("USD", "JPY",
                LocalDate.now(), BigDecimal.valueOf(AMOUNT));
        BigDecimal result = calculator.calculate(request);
        Assert.assertEquals(calculateTestRate(AMOUNT, USD_RATE, JPY_RATE),result.stripTrailingZeros());
    }

    @Test(expected = ConversionNoDataException.class)
    public void calculateNonExistingCurrencyTo(){
        CurrencyExchangeRequest request = new CurrencyExchangeRequest("USD", "AAA",
                LocalDate.now(), BigDecimal.valueOf(AMOUNT));
        calculator.calculate(request);
    }

    @Test(expected = ConversionNoDataException.class)
    public void calculateNonExistingCurrencyFrom(){
        CurrencyExchangeRequest request = new CurrencyExchangeRequest("AAA", "EUR",
                LocalDate.now(), BigDecimal.valueOf(AMOUNT));
        calculator.calculate(request);
    }


    @Test(expected = ConversionNoDataException.class)
    public void calculateFutureDate(){
        CurrencyExchangeRequest request = new CurrencyExchangeRequest("USD", "EUR",
                LocalDate.now().plusDays(1), BigDecimal.valueOf(AMOUNT));
        calculator.calculate(request);
    }

    @Test(expected = ConversionNoDataException.class)
    public void calculateNoDataDate(){
        CurrencyExchangeRequest request = new CurrencyExchangeRequest("USD", "EUR",
                LocalDate.now().minusDays(2), BigDecimal.valueOf(AMOUNT));
        calculator.calculate(request);
    }

    @Test(expected = ConversionNoDataException.class)
    public void calculateVeryOldDate(){
        CurrencyExchangeRequest request = new CurrencyExchangeRequest("USD", "EUR",
                LocalDate.now().minusDays(AMOUNT_OF_DAYS_MORE_THAN_EXPIRED), BigDecimal.valueOf(AMOUNT));
        calculator.calculate(request);
    }

    private BigDecimal calculateTestRate(double amount, double rateFrom, double rateTo){
        BigDecimal result = new BigDecimal(amount).multiply(new BigDecimal(rateTo), mathContext)
                .divide(new BigDecimal(rateFrom), mathContext).stripTrailingZeros();
        return result;
    }

    private static final double USD_RATE = 1.0876;
    private static final double JPY_RATE = 123.82;
    private static final double EUR_RATE = 1d;
    private static final double AMOUNT = 100d;
    private static final int AMOUNT_OF_DAYS_MORE_THAN_EXPIRED = 100;

    private static final String SAMPLE_XML_NEW_DATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<gesmes:Envelope xmlns:gesmes=\"http://www.gesmes.org/xml/2002-08-01\" xmlns=\"http://www.ecb.int/vocabulary/2002-08-01/eurofxref\">\n" +
            "\t<gesmes:subject>Reference rates</gesmes:subject>\n" +
            "\t<gesmes:Sender>\n" +
            "\t\t<gesmes:name>European Central Bank</gesmes:name>\n" +
            "\t</gesmes:Sender>\n" +
            "\t<Cube>\n" +
            "\t\t<Cube time='" + LocalDate.now() + "'>\n" +
            "\t\t\t<Cube currency='USD' rate='" + USD_RATE + "'/>\n" +
            "\t\t\t<Cube currency='JPY' rate='" + JPY_RATE + "'/>\n" +
            "\t\t</Cube>\n" +
            "\t</Cube>\n" +
            "</gesmes:Envelope>";
}
