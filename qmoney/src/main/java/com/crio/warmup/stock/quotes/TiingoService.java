
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {

  private RestTemplate restTemplate;

  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest



  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) throws JsonProcessingException {
    String uri = buildUri(symbol, from, to);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    //TiingoCandle[] results = restTemplate.getForObject(uri, TiingoCandle[].class);

    String responseString = restTemplate.getForObject(uri, String.class);
    // TiingoCandle[] results = objectMapper.readValue(responseString, 
    //     TiingoCandle[].class);
    List<TiingoCandle> results = objectMapper.readValue(responseString, 
          new TypeReference<ArrayList<TiingoCandle>>() {});
    if (results == null) {
      return new ArrayList<Candle>();
    } else {
      List<Candle> stock = new ArrayList<Candle> (results);
      return stock;
    }

  }


  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.


  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    //  String uriTemplate = "https:api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
    //       + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";

          String uri = "https://api.tiingo.com/tiingo/daily/" + symbol + 
          "/prices?token=acd82756bb95c48fb77c514cfac69cae9a080550" + "&startDate=" + 
          startDate.toString() + "&endDate=" + endDate.toString();

          return uri;
  }

}
