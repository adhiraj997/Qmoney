
package com.crio.warmup.stock.quotes;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AlphavantageCandle;
import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
import com.crio.warmup.stock.dto.Candle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class AlphavantageService implements StockQuotesService {
  
  private RestTemplate restTemplate;

  protected AlphavantageService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement the StockQuoteService interface as per the contracts. Call Alphavantage service
  //  to fetch daily adjusted data for last 20 years.
  //  Refer to documentation here: https://www.alphavantage.co/documentation/
  //  --
  //  The implementation of this functions will be doing following tasks:
  //    1. Build the appropriate url to communicate with third-party.
  //       The url should consider startDate and endDate if it is supported by the provider.
  //    2. Perform third-party communication with the url prepared in step#1
  //    3. Map the response and convert the same to List<Candle>
  //    4. If the provider does not support startDate and endDate, then the implementation
  //       should also filter the dates based on startDate and endDate. Make sure that
  //       result contains the records for for startDate and endDate after filtering.
  //    5. Return a sorted List<Candle> sorted ascending based on Candle#getDate
  // Note:
  // 1. Make sure you use {RestTemplate#getForObject(URI, String)} else the test will fail.
  // 2. Run the tests using command below and make sure it passes:
  //    ./gradlew test --tests AlphavantageServiceTest
  //CHECKSTYLE:OFF
    //CHECKSTYLE:ON

    public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
    throws JsonProcessingException {

      String uri = buildUri(symbol);
      //RestTemplate restTemplate = new RestTemplate();
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      // AlphavantageDailyResponse response = 
      //     restTemplate.getForObject(uri, AlphavantageDailyResponse.class);

      String responseString = restTemplate.getForObject(uri, String.class);
      //below line is to test whether the response is a null string or not 
      System.out.println(responseString);

      AlphavantageDailyResponse response = objectMapper.readValue(responseString, 
          AlphavantageDailyResponse.class);

      Map<LocalDate, AlphavantageCandle> dailyPricesMap = response.getCandles();

      //initializing Candle list
      List<Candle> dailyPrices = new ArrayList<Candle> ();

      Iterator<Map.Entry<LocalDate, AlphavantageCandle>> it = 
          dailyPricesMap.entrySet().iterator();

      while(it.hasNext()) {
        Map.Entry<LocalDate, AlphavantageCandle> pair = it.next();
        LocalDate curDate = pair.getKey();

        //insert the AlphavantageCandle object to list if date lies between from and to 
        if((curDate.isAfter(from) && curDate.isBefore(to)) || curDate.isEqual(from) 
            || curDate.isEqual(to)) {

          AlphavantageCandle avCandle = pair.getValue();
          avCandle.setDate(curDate);

          // Candle candle = new AlphavantageCandle(avCandle.getOpen(), avCandle.getClose()
          //     , avCandle.getHigh(), avCandle.getLow(), avCandle.getDate());

          dailyPrices.add(avCandle);
        }

        //if the date is greater than the end date, break the loop
        // if(curDate.isAfter(to))
        //   break;
      }

      Collections.sort(dailyPrices, new Comparator<Candle>() {
        public int compare(Candle t1, Candle t2) {
          return (int) (t1.getDate().compareTo(t2.getDate()));
        }
      });

      return dailyPrices;

      // private Comparator<AlphavantageCandle> getComparator() {
      //   return Comparator.comparing(AlphavantageCandle::getDate);
      // }
      

      

      // if (results == null) {
      //   return new ArrayList<Candle>();
      // } else {
      //   List<Candle> stock = Arrays.asList(results);
      //   return stock;
      // }


    }


  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  1. Write a method to create appropriate url to call Alphavantage service. The method should
  //     be using configurations provided in the {@link @application.properties}.
  //  2. Use this method in #getStockQuote.

  protected String buildUri(String symbol) {
    String uri = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol="
        + symbol + "&outputsize=full&apikey=98TPXQ6UO37PKX6R";

    return uri;
  }

}

