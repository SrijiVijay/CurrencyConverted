package com.gio.exchange.business.storage;

import com.gio.exchange.business.parsing.ECBCurrencySAXStubParser;
import com.gio.exchange.storage.ConversionNoDataException;
import com.gio.exchange.storage.CurrencyKeeper;
import com.gio.exchange.storage.ECBCurrencyKeeper;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(value="classpath:/application.properties")
public class CurrencyKeeperTests {

    @Value(value = "${rate.daily.url}")
    private String rateDailyURL;

    @Value("${rate.90.days.url}")
    private String rateThreeMonthURL;

    CurrencyKeeper keeper;

    @Test
    public void loadDataTest(){
        initKeeper(SAMPLE_XML_NEW_DATE, 90);
        Assert.assertEquals(1, keeper.getNumberOfDaysWithRates());
        Assert.assertEquals(RECORDS_NOW, keeper.getRatesForDate(LocalDate.now()).size());
    }

    @Test
    public void getPreviousDateRate(){
        initKeeper(SAMPLE_XML_TWO_DATES, 2);
        Assert.assertEquals(RECORDS_TWO_DAYS_BEFORE, keeper.getRatesForDate(LocalDate.now()).size());
    }

    @Test(expected = ConversionNoDataException.class)
    public void getFutureDateRate(){
        initKeeper(SAMPLE_XML_TWO_DATES, 2);
        keeper.getRatesForDate(LocalDate.now().plusDays(1));
    }

    @Test(expected = ConversionNoDataException.class)
    public void getExpiredDateRate(){
        initKeeper(SAMPLE_XML_TWO_DATES, 2);
        keeper.getRatesForDate(LocalDate.now().minusDays(3));
    }


    @Test
    public void refreshDataTest(){
        ECBCurrencySAXStubParser parser = new ECBCurrencySAXStubParser();
        initKeeper(SAMPLE_XML_TWO_DATES, 3, parser);
        Assert.assertEquals(RECORDS_TWO_DAYS_BEFORE, keeper.getRatesForDate(TWO_DAYS_BEFORE).size());
        Assert.assertEquals(RECORDS_THREE_DAYS_BEFORE, keeper.getRatesForDate(THREE_DAYS_BEFORE).size());
        Assert.assertEquals(2, keeper.getNumberOfDaysWithRates());
        parser.setStubInputData(SAMPLE_XML_NEW_DATE);
        keeper.setDaysExpired(1);
        keeper.refresh();
        Assert.assertEquals(RECORDS_NOW, keeper.getRatesForDate(LocalDate.now()).size());
        Assert.assertEquals(1, keeper.getNumberOfDaysWithRates());
    }


    private void initKeeper(String stubData, int daysExpired){
        ECBCurrencySAXStubParser parser = new ECBCurrencySAXStubParser();
        initKeeper(stubData, daysExpired, parser);
    }

    private void initKeeper(String stubData, int daysExpired, ECBCurrencySAXStubParser parser){
        keeper = new ECBCurrencyKeeper();
        ((ECBCurrencyKeeper)keeper).setRateDailyURL(rateDailyURL);
        ((ECBCurrencyKeeper)keeper).setRateThreeMonthURL(rateThreeMonthURL);
        parser.setStubInputData(stubData);
        keeper.setParser(parser);
        keeper.setDaysExpired(daysExpired);
        keeper.load();
    }

    private static final LocalDate TWO_DAYS_BEFORE = LocalDate.now().minusDays(2);
    private static final LocalDate THREE_DAYS_BEFORE = LocalDate.now().minusDays(3);

    private static final int RECORDS_TWO_DAYS_BEFORE = 32;
    private static final int RECORDS_THREE_DAYS_BEFORE = 32;
    private static final int RECORDS_NOW = 3;


    private static final String SAMPLE_XML_TWO_DATES = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<gesmes:Envelope xmlns:gesmes=\"http://www.gesmes.org/xml/2002-08-01\" xmlns=\"http://www.ecb.int/vocabulary/2002-08-01/eurofxref\">\n" +
            "\t<gesmes:subject>Reference rates</gesmes:subject>\n" +
            "\t<gesmes:Sender>\n" +
            "\t\t<gesmes:name>European Central Bank</gesmes:name>\n" +
            "\t</gesmes:Sender>\n" +
            "\t<Cube>\n" +
            "\t\t<Cube time='" + TWO_DAYS_BEFORE + "'>\n" +
            "\t\t\t<Cube currency='USD' rate='1.0876'/>\n" +
            "\t\t\t<Cube currency='JPY' rate='123.82'/>\n" +
            "\t\t\t<Cube currency='BGN' rate='1.9558'/>\n" +
            "\t\t\t<Cube currency='CZK' rate='26.576'/>\n" +
            "\t\t\t<Cube currency='DKK' rate='7.4402'/>\n" +
            "\t\t\t<Cube currency='GBP' rate='0.84588'/>\n" +
            "\t\t\t<Cube currency='HUF' rate='310.24'/>\n" +
            "\t\t\t<Cube currency='PLN' rate='4.2170'/>\n" +
            "\t\t\t<Cube currency='RON' rate='4.5450'/>\n" +
            "\t\t\t<Cube currency='SEK' rate='9.6673'/>\n" +
            "\t\t\t<Cube currency='CHF' rate='1.0963'/>\n" +
            "\t\t\t<Cube currency='NOK' rate='9.3665'/>\n" +
            "\t\t\t<Cube currency='HRK' rate='7.4225'/>\n" +
            "\t\t\t<Cube currency='RUB' rate='62.3148'/>\n" +
            "\t\t\t<Cube currency='TRY' rate='3.9038'/>\n" +
            "\t\t\t<Cube currency='AUD' rate='1.4731'/>\n" +
            "\t\t\t<Cube currency='BRL' rate='3.4227'/>\n" +
            "\t\t\t<Cube currency='CAD' rate='1.4941'/>\n" +
            "\t\t\t<Cube currency='CNY' rate='7.5047'/>\n" +
            "\t\t\t<Cube currency='HKD' rate='8.4761'/>\n" +
            "\t\t\t<Cube currency='IDR' rate='14497.16'/>\n" +
            "\t\t\t<Cube currency='ILS' rate='3.9203'/>\n" +
            "\t\t\t<Cube currency='INR' rate='69.9395'/>\n" +
            "\t\t\t<Cube currency='KRW' rate='1226.52'/>\n" +
            "\t\t\t<Cube currency='MXN' rate='20.5521'/>\n" +
            "\t\t\t<Cube currency='MYR' rate='4.7243'/>\n" +
            "\t\t\t<Cube currency='NZD' rate='1.5892'/>\n" +
            "\t\t\t<Cube currency='PHP' rate='54.087'/>\n" +
            "\t\t\t<Cube currency='SGD' rate='1.5314'/>\n" +
            "\t\t\t<Cube currency='THB' rate='37.778'/>\n" +
            "\t\t\t<Cube currency='ZAR' rate='14.6336'/>\n" +
            "\t\t</Cube>\n" +
            "\t\t<Cube time='"+ THREE_DAYS_BEFORE +"'>\n" +
            "\t\t\t<Cube currency='USD' rate='1.0876'/>\n" +
            "\t\t\t<Cube currency='JPY' rate='123.82'/>\n" +
            "\t\t\t<Cube currency='BGN' rate='1.9558'/>\n" +
            "\t\t\t<Cube currency='CZK' rate='26.576'/>\n" +
            "\t\t\t<Cube currency='DKK' rate='7.4402'/>\n" +
            "\t\t\t<Cube currency='GBP' rate='0.84588'/>\n" +
            "\t\t\t<Cube currency='HUF' rate='310.24'/>\n" +
            "\t\t\t<Cube currency='PLN' rate='4.2170'/>\n" +
            "\t\t\t<Cube currency='RON' rate='4.5450'/>\n" +
            "\t\t\t<Cube currency='SEK' rate='9.6673'/>\n" +
            "\t\t\t<Cube currency='CHF' rate='1.0963'/>\n" +
            "\t\t\t<Cube currency='NOK' rate='9.3665'/>\n" +
            "\t\t\t<Cube currency='HRK' rate='7.4225'/>\n" +
            "\t\t\t<Cube currency='RUB' rate='62.3148'/>\n" +
            "\t\t\t<Cube currency='TRY' rate='3.9038'/>\n" +
            "\t\t\t<Cube currency='AUD' rate='1.4731'/>\n" +
            "\t\t\t<Cube currency='BRL' rate='3.4227'/>\n" +
            "\t\t\t<Cube currency='CAD' rate='1.4941'/>\n" +
            "\t\t\t<Cube currency='CNY' rate='7.5047'/>\n" +
            "\t\t\t<Cube currency='HKD' rate='8.4761'/>\n" +
            "\t\t\t<Cube currency='IDR' rate='14497.16'/>\n" +
            "\t\t\t<Cube currency='ILS' rate='3.9203'/>\n" +
            "\t\t\t<Cube currency='INR' rate='69.9395'/>\n" +
            "\t\t\t<Cube currency='KRW' rate='1226.52'/>\n" +
            "\t\t\t<Cube currency='MXN' rate='20.5521'/>\n" +
            "\t\t\t<Cube currency='MYR' rate='4.7243'/>\n" +
            "\t\t\t<Cube currency='NZD' rate='1.5892'/>\n" +
            "\t\t\t<Cube currency='PHP' rate='54.087'/>\n" +
            "\t\t\t<Cube currency='SGD' rate='1.5314'/>\n" +
            "\t\t\t<Cube currency='THB' rate='37.778'/>\n" +
            "\t\t\t<Cube currency='ZAR' rate='14.6336'/>\n" +
            "\t\t</Cube>\n" +
            "\t</Cube>\n" +
            "</gesmes:Envelope>";

    private static final String SAMPLE_XML_NEW_DATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<gesmes:Envelope xmlns:gesmes=\"http://www.gesmes.org/xml/2002-08-01\" xmlns=\"http://www.ecb.int/vocabulary/2002-08-01/eurofxref\">\n" +
            "\t<gesmes:subject>Reference rates</gesmes:subject>\n" +
            "\t<gesmes:Sender>\n" +
            "\t\t<gesmes:name>European Central Bank</gesmes:name>\n" +
            "\t</gesmes:Sender>\n" +
            "\t<Cube>\n" +
            "\t\t<Cube time='" + LocalDate.now() + "'>\n" +
            "\t\t\t<Cube currency='USD' rate='1.0876'/>\n" +
            "\t\t\t<Cube currency='JPY' rate='123.82'/>\n" +
            "\t\t</Cube>\n" +
            "\t</Cube>\n" +
            "</gesmes:Envelope>";
}
