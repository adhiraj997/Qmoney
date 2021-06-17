
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {

  private RestTemplate restTemplate;


  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward. 

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF

  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades, LocalDate endDate)
      throws JsonProcessingException {

    List<AnnualizedReturn> annualizedReturn = new ArrayList<AnnualizedReturn>();

    //RestTemplate restTemplate = new RestTemplate();
    for (PortfolioTrade t : portfolioTrades) {
      List<Candle> candle = getStockQuote(t.getSymbol(), t.getPurchaseDate(), endDate);
      // String uri = buildUri(t.getSymbol(), t.getPurchaseDate(), endDate);
      // TiingoCandle[] results = restTemplate.getForObject(uri, TiingoCandle[].class);

      // if (results != null) {
      //   annualizedReturn.add(calculateAnnualizedReturnsIndividual(endDate, t, results[0].getOpen(),
      //       results[results.length - 1].getClose()));
      // }

      if(!candle.isEmpty()) {
        annualizedReturn.add(calculateAnnualizedReturnsIndividual(endDate, t, 
            candle.get(0).getOpen(), candle.get(candle.size() - 1).getClose()));

      }

    }
    Collections.sort(annualizedReturn, getComparator());

    return annualizedReturn;

  }

  public static AnnualizedReturn calculateAnnualizedReturnsIndividual(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {

    Double totalReturn = (sellPrice - buyPrice) / buyPrice;
    
    LocalDate startDate = trade.getPurchaseDate();
    //Period intervalPeriod = Period.between(startDate, endDate);
    //int years = intervalPeriod.getYears();
    //OR
    Double years = startDate.until(endDate, ChronoUnit.DAYS) / 365.24;
    Double annualizedReturns = Math.pow(1 + totalReturn, 1/years) - 1;
    return new AnnualizedReturn(trade.getSymbol(), annualizedReturns,
        totalReturn);
  }




  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
    
    String uri = buildUri(symbol, from, to);
    RestTemplate restTemplate = new RestTemplate();
    TiingoCandle[] results = restTemplate.getForObject(uri, TiingoCandle[].class);

    if (results == null) {
      return new ArrayList<Candle>();
    } 
    else {
      List<Candle> stock = Arrays.asList(results);
      return stock;
    }

  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
      //  String uriTemplate = "https:api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
      //       + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";

            String uri = "https://api.tiingo.com/tiingo/daily/" + symbol + 
            "/prices?token=acd82756bb95c48fb77c514cfac69cae9a080550" + "&startDate=" + 
            startDate.toString() + "&endDate=" + endDate.toString();

            return uri;
  }


  // ¶TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Modify the function #getStockQuote and start delegating to calls to
  //  stockQuoteService provided via newly added constructor of the class.
  //  You also have a liberty to completely get rid of that function itself, however, make sure
  //  that you do not delete the #getStockQuote function.

}
