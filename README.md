# currency-exchange
Java 8 currency exchange Restful API to exchange currency based on ECB service.
Source data for conversion is taken from ECB service:
 http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml
 http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml
SAX parser is used to extract data and store in memory. Sping Scheduling is used to run job in backround which tries to update repository each 10 minutes. ConcurrentHashMap is used to handle concurrent calls (not-locking read, locking write). The most of functions are covered with Unit tests (JUnit).

Please use following command to run application from jar:
java -jar exchange-1.0-SNAPSHOT.jar

API:


REQUEST

To convert currency please make GET Call to service:

http://Server_URL/api/convert


parameters:

from - currency from name shortened by ISO standard, i.e. EUR

to - currency to name shortened by ISO standard, i.e. USD

amount - amount in currency from to be converted, i.e 123.21

date - optional date of operation in format yyyy-MM-dd, i.e. 2017-05-15. If date is not mentioned then today date by server time will be used. If request is done on weekend or holiday, rate will be calulated for the first bank work day prior to date requested.
If date is more than today or less then 90 days before today then corresponding error message will be provided.


request example:

http://localhost:8087/api/convert?from=USD&to=EUR&amount=100.2&date=2021-02-02
http://localhost:8087/api/convert?from=USD&to=EUR&amount=100.2 {Will consider as on today date}


RESPONSE

Success Response
----------------

{"status":200,"message":"SUCCESS","payload":83.30562}

HTTP Status OK used to indicate successful conversion,
payload used to deliver result of conversion


Error Response
-------------

{"status":500,"message":"FAILED","payload":"No data for requested currency present."}

HTTP ERROR CODES 400 and 500 are used to indicate conversion error

