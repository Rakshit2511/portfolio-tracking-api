package dev.rakshit.portfoliotrackingapi.controller;

import dev.rakshit.portfoliotrackingapi.exceptions.BadRequestException;
import dev.rakshit.portfoliotrackingapi.exceptions.NotFoundException;
import dev.rakshit.portfoliotrackingapi.models.Holding;
import dev.rakshit.portfoliotrackingapi.models.Portfolio;
import dev.rakshit.portfoliotrackingapi.models.Trade;
import dev.rakshit.portfoliotrackingapi.service.PortfolioTrackingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class PortfolioTrackingController {

    @Autowired
    private PortfolioTrackingService portfolioTrackingService;

    @PostMapping("/trades")
    public ResponseEntity<Trade> addOrUpdateTrade(@RequestBody Trade trade) {
        log.info("Add or update trade request came for user for trade id : {}", trade.getTradeId());
        portfolioTrackingService.isValidTrade(trade);
        return Optional.ofNullable(trade.getTradeId())
                .flatMap(portfolioTrackingService::getTrade)
                .map(value -> new ResponseEntity<>(portfolioTrackingService.updateTrade(value, trade), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(portfolioTrackingService.addTrade(trade), HttpStatus.CREATED));
    }

    @DeleteMapping("/trades/{id}")
    public ResponseEntity<Trade> deleteTrade(@PathVariable String id) {
        log.info("Delete trade request came for user for trade id : {}", id);
        return portfolioTrackingService.deleteTrade(getTradeId(id))
                .map(trade -> new ResponseEntity<>(trade, HttpStatus.OK))
                .orElseThrow(() -> new NotFoundException("No Trade found for id : " + id));
    }

    @GetMapping("/trades/{id}")
    public ResponseEntity<Trade> getTrade(@PathVariable String id) {
        log.info("Show trade request came for user for trade id : {}", id);
        return portfolioTrackingService.getTrade(getTradeId(id))
                .map(trade -> new ResponseEntity<>(trade, HttpStatus.OK))
                .orElseThrow(() -> new NotFoundException("No Trade found for id : " + id));
    }

    @GetMapping("/trades")
    public ResponseEntity<List<Trade>> getTrades() {
        log.info("Show trades request came for user");
        return Optional.of(portfolioTrackingService.getTrades())
                .filter(list -> !list.isEmpty())
                .map(trades -> new ResponseEntity<>(trades, HttpStatus.OK))
                .orElseThrow(() -> new NotFoundException("No Trade found for user"));
    }

    @GetMapping("/holdings/{ticker}")
    public ResponseEntity<Holding> getHolding(@PathVariable String ticker) {
        log.info("Show holding request came for user for ticker : {}", ticker);
        return portfolioTrackingService.getHolding(ticker)
                .map(holding -> new ResponseEntity<>(holding, HttpStatus.OK))
                .orElseThrow(() -> new NotFoundException(String.format("No Holding found for ticker : %s", ticker)));
    }

    @GetMapping("/holdings")
    public ResponseEntity<List<Holding>> getHoldings() {
        log.info("Show holdings request came for user");
        return Optional.of(portfolioTrackingService.getHoldings())
                .filter(list -> !list.isEmpty())
                .map(holdings -> new ResponseEntity<>(holdings, HttpStatus.OK))
                .orElseThrow(() -> new NotFoundException("User has no securities"));
    }

    @GetMapping("/holdings/refresh")
    public ResponseEntity<List<Holding>> refreshHoldings() {
        log.info("Refresh holding request came for user");
        return Optional.of(portfolioTrackingService.refreshHoldings())
                .filter(list -> !list.isEmpty())
                .map(holdings -> new ResponseEntity<>(holdings, HttpStatus.OK))
                .orElseThrow(() -> new NotFoundException("User has no securities"));
    }

    @GetMapping("/portfolio")
    public ResponseEntity<List<Portfolio>> getPortfolio() {
        log.info("Show portfolio request came for user");
        return Optional.of(portfolioTrackingService.getPortfolio())
                .filter(list -> !list.isEmpty())
                .map(holdings -> new ResponseEntity<>(holdings, HttpStatus.OK))
                .orElseThrow(() -> new NotFoundException("User has no securities"));
    }

    @GetMapping("/returns")
    public ResponseEntity<BigDecimal> getReturns() {
        log.info("Show returns request came for user");
        return portfolioTrackingService.getReturns()
                .map(returns -> new ResponseEntity<>(returns, HttpStatus.OK))
                .orElseThrow(() -> new NotFoundException("User has no securities"));
    }

    private Long getTradeId(String id) {
        try {
            return Long.valueOf(id);
        }  catch (NumberFormatException numberFormatException) {
            throw new BadRequestException("Trade id should be a number");
        }
    }

}