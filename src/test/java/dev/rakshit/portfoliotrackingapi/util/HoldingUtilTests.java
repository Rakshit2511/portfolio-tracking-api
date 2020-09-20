package dev.rakshit.portfoliotrackingapi.util;

import dev.rakshit.portfoliotrackingapi.exceptions.InvalidTransactionException;
import dev.rakshit.portfoliotrackingapi.models.Holding;
import dev.rakshit.portfoliotrackingapi.models.Trade;
import dev.rakshit.portfoliotrackingapi.models.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static dev.rakshit.portfoliotrackingapi.util.HoldingUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HoldingUtilTests {

    private static final String LAST_UPDATED = "lastUpdated";
    private static final String TICKER1 = "WIPRO";
    private static final String TICKER2 = "TCS";
    private static final String TICKER3 = "GODREJIND";

    @Test
    public void testAddBuyTradeToHoldingOfSamePrice() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(1000))
                .shares(BigInteger.valueOf(10))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade trade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(100))
                .shares(BigInteger.valueOf(20))
                .build();
        Holding expectedHolding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(3000))
                .shares(BigInteger.valueOf(30))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .lastUpdated(LocalDateTime.now())
                .build();
        Holding actualHolding = addTradeToHolding(holding, trade);
        assertThat(actualHolding).isEqualToIgnoringGivenFields(expectedHolding, LAST_UPDATED);
    }

    @Test
    public void testAddBuyTradeToHoldingOfMorePrice() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(1000))
                .shares(BigInteger.valueOf(10))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade trade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(150))
                .shares(BigInteger.valueOf(10))
                .build();
        Holding expectedHolding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2500))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(125))
                .lastUpdated(LocalDateTime.now())
                .build();
        Holding actualHolding = addTradeToHolding(holding, trade);
        assertThat(actualHolding).isEqualToIgnoringGivenFields(expectedHolding, LAST_UPDATED);
    }

    @Test
    public void testAddBuyTradeToHoldingOfLessPrice() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(1000))
                .shares(BigInteger.valueOf(10))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade trade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(50))
                .shares(BigInteger.valueOf(10))
                .build();
        Holding expectedHolding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(1500))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(75))
                .lastUpdated(LocalDateTime.now())
                .build();
        Holding actualHolding = addTradeToHolding(holding, trade);
        assertThat(actualHolding).isEqualToIgnoringGivenFields(expectedHolding, LAST_UPDATED);
    }

    @Test
    public void testAddSellTradeToHoldingSameShares() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(1000))
                .shares(BigInteger.valueOf(10))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade trade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(150))
                .shares(BigInteger.valueOf(10))
                .build();
        Holding expectedHolding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.ZERO)
                .shares(BigInteger.ZERO)
                .averageBuyPrice(BigDecimal.ZERO)
                .lastUpdated(LocalDateTime.now())
                .build();
        Holding actualHolding = addTradeToHolding(holding, trade);
        assertThat(actualHolding).isEqualToIgnoringGivenFields(expectedHolding, LAST_UPDATED);
    }

    @Test
    public void testAddSellTradeToHoldingOfSamePriceSuccessful() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2000))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade trade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(100))
                .shares(BigInteger.valueOf(10))
                .build();
        Holding expectedHolding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(1000))
                .shares(BigInteger.valueOf(10))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .lastUpdated(LocalDateTime.now())
                .build();
        Holding actualHolding = addTradeToHolding(holding, trade);
        assertThat(actualHolding).isEqualToIgnoringGivenFields(expectedHolding, LAST_UPDATED);
    }

    @Test
    public void testAddSellTradeToHoldingOfMorePriceSuccessful() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2000))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade trade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(150))
                .shares(BigInteger.valueOf(10))
                .build();
        Holding expectedHolding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(1000))
                .shares(BigInteger.valueOf(10))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .lastUpdated(LocalDateTime.now())
                .build();
        Holding actualHolding = addTradeToHolding(holding, trade);
        assertThat(actualHolding).isEqualToIgnoringGivenFields(expectedHolding, LAST_UPDATED);
    }

    @Test
    public void testAddSellTradeToHoldingOfLessPriceSuccessful() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2000))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade trade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(75))
                .shares(BigInteger.valueOf(10))
                .build();
        Holding expectedHolding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(1000))
                .shares(BigInteger.valueOf(10))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .lastUpdated(LocalDateTime.now())
                .build();
        Holding actualHolding = addTradeToHolding(holding, trade);
        assertThat(actualHolding).isEqualToIgnoringGivenFields(expectedHolding, LAST_UPDATED);
    }

    @Test
    public void testAddSellTradeToHoldingOfSamePriceUnsuccessful() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2000))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade trade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(100))
                .shares(BigInteger.valueOf(30))
                .build();
        Exception exception = assertThrows(InvalidTransactionException.class, () -> addTradeToHolding(holding, trade));
        String expectedMessage = "Invalid Transaction Causing Price Negative";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testAddSellTradeToHoldingOfMorePriceUnsuccessful() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2000))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade trade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(150))
                .shares(BigInteger.valueOf(25))
                .build();
        Exception exception = assertThrows(InvalidTransactionException.class, () -> addTradeToHolding(holding, trade));
        String expectedMessage = "Invalid Transaction Causing Price Negative";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testAddSellTradeToHoldingOfLessPriceUnsuccessful() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2000))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade trade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(75))
                .shares(BigInteger.valueOf(25))
                .build();
        Exception exception = assertThrows(InvalidTransactionException.class, () -> addTradeToHolding(holding, trade));
        String expectedMessage = "Invalid Transaction Causing Price Negative";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testDeleteSellTradeToHoldingOfSamePrice() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2000))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade trade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(100))
                .shares(BigInteger.valueOf(10))
                .build();
        Holding expectedHolding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(3000))
                .shares(BigInteger.valueOf(30))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .lastUpdated(LocalDateTime.now())
                .build();
        Holding actualHolding = deleteTradeFromHolding(holding, trade);
        assertThat(actualHolding).isEqualToIgnoringGivenFields(expectedHolding, LAST_UPDATED);
    }

    @Test
    public void testDeleteSellTradeToHoldingOfMorePrice() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2000))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade trade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(150))
                .shares(BigInteger.valueOf(10))
                .build();
        Holding expectedHolding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(3000))
                .shares(BigInteger.valueOf(30))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .lastUpdated(LocalDateTime.now())
                .build();
        Holding actualHolding = deleteTradeFromHolding(holding, trade);
        assertThat(actualHolding).isEqualToIgnoringGivenFields(expectedHolding, LAST_UPDATED);
    }

    @Test
    public void testDeleteSellTradeToHoldingOfLessPrice() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2000))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade trade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(75))
                .shares(BigInteger.valueOf(10))
                .build();
        Holding expectedHolding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(3000))
                .shares(BigInteger.valueOf(30))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .lastUpdated(LocalDateTime.now())
                .build();
        Holding actualHolding = deleteTradeFromHolding(holding, trade);
        assertThat(actualHolding).isEqualToIgnoringGivenFields(expectedHolding, LAST_UPDATED);
    }

    @Test
    public void testDeleteBuyTradeToHoldingSameShares() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(1000))
                .shares(BigInteger.valueOf(10))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade trade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(100))
                .shares(BigInteger.valueOf(10))
                .build();
        Holding expectedHolding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.ZERO)
                .shares(BigInteger.ZERO)
                .averageBuyPrice(BigDecimal.ZERO)
                .lastUpdated(LocalDateTime.now())
                .build();
        Holding actualHolding = deleteTradeFromHolding(holding, trade);
        assertThat(actualHolding).isEqualToIgnoringGivenFields(expectedHolding, LAST_UPDATED);
    }

    @Test
    public void testDeleteBuyTradeToHoldingOfSamePriceSuccessful() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(1000))
                .shares(BigInteger.valueOf(10))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade trade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(100))
                .shares(BigInteger.valueOf(5))
                .build();
        Holding expectedHolding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(500))
                .shares(BigInteger.valueOf(5))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .lastUpdated(LocalDateTime.now())
                .build();
        Holding actualHolding = deleteTradeFromHolding(holding, trade);
        assertThat(actualHolding).isEqualToIgnoringGivenFields(expectedHolding, LAST_UPDATED);
    }

    @Test
    public void testDeleteBuyTradeToHoldingOfMorePriceSuccessful() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(1000))
                .shares(BigInteger.valueOf(10))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade trade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(150))
                .shares(BigInteger.valueOf(5))
                .build();
        Holding expectedHolding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(250))
                .shares(BigInteger.valueOf(5))
                .averageBuyPrice(BigDecimal.valueOf(50))
                .lastUpdated(LocalDateTime.now())
                .build();
        Holding actualHolding = deleteTradeFromHolding(holding, trade);
        assertThat(actualHolding).isEqualToIgnoringGivenFields(expectedHolding, LAST_UPDATED);
    }

    @Test
    public void testDeleteBuyTradeToHoldingOfLessPriceSuccessful() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(1000))
                .shares(BigInteger.valueOf(10))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade trade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(50))
                .shares(BigInteger.valueOf(5))
                .build();
        Holding expectedHolding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(750))
                .shares(BigInteger.valueOf(5))
                .averageBuyPrice(BigDecimal.valueOf(150))
                .lastUpdated(LocalDateTime.now())
                .build();
        Holding actualHolding = deleteTradeFromHolding(holding, trade);
        assertThat(actualHolding).isEqualToIgnoringGivenFields(expectedHolding, LAST_UPDATED);
    }

    @Test
    public void testDeleteBuyTradeToHoldingOfSamePriceUnsuccessful() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2000))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade trade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(100))
                .shares(BigInteger.valueOf(30))
                .build();
        Exception exception = assertThrows(InvalidTransactionException.class, () -> deleteTradeFromHolding(holding, trade));
        String expectedMessage = "Invalid Transaction Causing Price Negative";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testDeleteBuyTradeToHoldingOfMorePriceUnsuccessful() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2000))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade trade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(150))
                .shares(BigInteger.valueOf(25))
                .build();
        Exception exception = assertThrows(InvalidTransactionException.class, () -> deleteTradeFromHolding(holding, trade));
        String expectedMessage = "Invalid Transaction Causing Price Negative";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testDeleteBuyTradeToHoldingOfLessPriceUnsuccessful() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2000))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade trade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(75))
                .shares(BigInteger.valueOf(25))
                .build();
        Exception exception = assertThrows(InvalidTransactionException.class, () -> deleteTradeFromHolding(holding, trade));
        String expectedMessage = "Invalid Transaction Causing Shares Negative";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }


    @Test
    public void testUpdateBuySellTradeToHoldingOfSamePrice() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2000))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade oldTrade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(100))
                .shares(BigInteger.valueOf(10))
                .build();
        Trade newTrade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(100))
                .shares(BigInteger.valueOf(10))
                .build();
        Holding expectedHolding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(0))
                .shares(BigInteger.valueOf(0))
                .averageBuyPrice(BigDecimal.valueOf(0))
                .build();
        List<Holding> expectedHoldings = Collections.singletonList(expectedHolding);
        List<Holding> actualHoldings = updateTradeInHolding(holding, oldTrade, holding,newTrade);
        assertThat(actualHoldings)
                .usingElementComparatorIgnoringFields(LAST_UPDATED)
                .isEqualTo(expectedHoldings);
    }

    @Test
    public void testUpdateSellBuyTradeToHoldingOfSamePrice() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2000))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade oldTrade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(100))
                .shares(BigInteger.valueOf(10))
                .build();
        Trade newTrade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(100))
                .shares(BigInteger.valueOf(10))
                .build();
        Holding expectedHolding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(4000))
                .shares(BigInteger.valueOf(40))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        List<Holding> expectedHoldings = Collections.singletonList(expectedHolding);
        List<Holding> actualHoldings = updateTradeInHolding(holding, oldTrade, holding,newTrade);
        assertThat(actualHoldings)
                .usingElementComparatorIgnoringFields(LAST_UPDATED)
                .isEqualTo(expectedHoldings);
    }

    @Test
    public void testUpdateBuyTradeToHoldingOfMorePrice() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2000))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade oldTrade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(100))
                .shares(BigInteger.valueOf(10))
                .build();
        Trade newTrade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(150))
                .shares(BigInteger.valueOf(10))
                .build();
        Holding expectedHolding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2500))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(125))
                .build();
        List<Holding> expectedHoldings = Collections.singletonList(expectedHolding);
        List<Holding> actualHoldings = updateTradeInHolding(holding, oldTrade, holding,newTrade);
        assertThat(actualHoldings)
                .usingElementComparatorIgnoringFields(LAST_UPDATED)
                .isEqualTo(expectedHoldings);
    }

    @Test
    public void testUpdateBuyTradeToHoldingOfLessPrice() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2000))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade oldTrade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(100))
                .shares(BigInteger.valueOf(10))
                .build();
        Trade newTrade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(50))
                .shares(BigInteger.valueOf(10))
                .build();
        Holding expectedHolding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(1500))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(75))
                .build();
        List<Holding> expectedHoldings = Collections.singletonList(expectedHolding);
        List<Holding> actualHoldings = updateTradeInHolding(holding, oldTrade, holding,newTrade);
        assertThat(actualHoldings)
                .usingElementComparatorIgnoringFields(LAST_UPDATED)
                .isEqualTo(expectedHoldings);
    }

    @Test
    public void testUpdateBuyTradeToHoldingOfMoreShares() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2000))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade oldTrade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(100))
                .shares(BigInteger.valueOf(10))
                .build();
        Trade newTrade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(100))
                .shares(BigInteger.valueOf(20))
                .build();
        Holding expectedHolding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(3000))
                .shares(BigInteger.valueOf(30))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        List<Holding> expectedHoldings = Collections.singletonList(expectedHolding);
        List<Holding> actualHoldings = updateTradeInHolding(holding, oldTrade, holding,newTrade);
        assertThat(actualHoldings)
                .usingElementComparatorIgnoringFields(LAST_UPDATED)
                .isEqualTo(expectedHoldings);
    }

    @Test
    public void testUpdateBuyTradeToHoldingOfLessShares() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2000))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade oldTrade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(100))
                .shares(BigInteger.valueOf(10))
                .build();
        Trade newTrade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(100))
                .shares(BigInteger.valueOf(5))
                .build();
        Holding expectedHolding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(1500))
                .shares(BigInteger.valueOf(15))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        List<Holding> expectedHoldings = Collections.singletonList(expectedHolding);
        List<Holding> actualHoldings = updateTradeInHolding(holding, oldTrade, holding,newTrade);
        assertThat(actualHoldings)
                .usingElementComparatorIgnoringFields(LAST_UPDATED)
                .isEqualTo(expectedHoldings);
    }

    @Test
    public void testUpdateSellTradeToHoldingOfMorePrice() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2000))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade oldTrade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(100))
                .shares(BigInteger.valueOf(10))
                .build();
        Trade newTrade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(150))
                .shares(BigInteger.valueOf(10))
                .build();
        Holding expectedHolding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2000))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        List<Holding> expectedHoldings = Collections.singletonList(expectedHolding);
        List<Holding> actualHoldings = updateTradeInHolding(holding, oldTrade, holding,newTrade);
        assertThat(actualHoldings)
                .usingElementComparatorIgnoringFields(LAST_UPDATED)
                .isEqualTo(expectedHoldings);
    }

    @Test
    public void testUpdateSellTradeToHoldingOfLessPrice() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2000))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade oldTrade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(100))
                .shares(BigInteger.valueOf(10))
                .build();
        Trade newTrade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(50))
                .shares(BigInteger.valueOf(10))
                .build();
        Holding expectedHolding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2000))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        List<Holding> expectedHoldings = Collections.singletonList(expectedHolding);
        List<Holding> actualHoldings = updateTradeInHolding(holding, oldTrade, holding,newTrade);
        assertThat(actualHoldings)
                .usingElementComparatorIgnoringFields(LAST_UPDATED)
                .isEqualTo(expectedHoldings);
    }

    @Test
    public void testUpdateSellTradeToHoldingOfMoreShares() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2000))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade oldTrade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(100))
                .shares(BigInteger.valueOf(10))
                .build();
        Trade newTrade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(100))
                .shares(BigInteger.valueOf(20))
                .build();
        Holding expectedHolding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(1000))
                .shares(BigInteger.valueOf(10))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        List<Holding> expectedHoldings = Collections.singletonList(expectedHolding);
        List<Holding> actualHoldings = updateTradeInHolding(holding, oldTrade, holding,newTrade);
        assertThat(actualHoldings)
                .usingElementComparatorIgnoringFields(LAST_UPDATED)
                .isEqualTo(expectedHoldings);
    }

    @Test
    public void testUpdateSellTradeToHoldingOfLessShares() {
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2000))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade oldTrade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(100))
                .shares(BigInteger.valueOf(10))
                .build();
        Trade newTrade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(100))
                .shares(BigInteger.valueOf(5))
                .build();
        Holding expectedHolding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2500))
                .shares(BigInteger.valueOf(25))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        List<Holding> expectedHoldings = Collections.singletonList(expectedHolding);
        List<Holding> actualHoldings = updateTradeInHolding(holding, oldTrade, holding,newTrade);
        assertThat(actualHoldings)
                .usingElementComparatorIgnoringFields(LAST_UPDATED)
                .isEqualTo(expectedHoldings);
    }

    @Test
    public void testUpdateBuyTradeToDifferentHolding() {
        Holding oldHolding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(1000))
                .shares(BigInteger.valueOf(10))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Holding newHolding = Holding.builder()
                .ticker(TICKER2)
                .totalPrice(BigDecimal.valueOf(1000))
                .shares(BigInteger.valueOf(10))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade oldTrade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(100))
                .shares(BigInteger.valueOf(10))
                .build();
        Trade newTrade = Trade.builder()
                .ticker(TICKER2)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(100))
                .shares(BigInteger.valueOf(10))
                .build();
        Holding expectedHolding1 = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(0))
                .shares(BigInteger.valueOf(0))
                .averageBuyPrice(BigDecimal.valueOf(0))
                .lastUpdated(LocalDateTime.now())
                .build();
        Holding expectedHolding2 = Holding.builder()
                .ticker(TICKER2)
                .totalPrice(BigDecimal.valueOf(2000))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .lastUpdated(LocalDateTime.now())
                .build();
        List<Holding> expectedHoldings = Arrays.asList(expectedHolding1, expectedHolding2);
        List<Holding> actualHoldings = updateTradeInHolding(oldHolding, oldTrade, newHolding,newTrade);
        assertThat(actualHoldings)
                .usingElementComparatorIgnoringFields(LAST_UPDATED)
                .isEqualTo(expectedHoldings);
    }

    @Test
    public void testUpdateSellTradeToDifferentHolding() {
        Holding oldHolding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(1000))
                .shares(BigInteger.valueOf(10))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Holding newHolding = Holding.builder()
                .ticker(TICKER2)
                .totalPrice(BigDecimal.valueOf(1000))
                .shares(BigInteger.valueOf(10))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .build();
        Trade oldTrade = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(150))
                .shares(BigInteger.valueOf(10))
                .build();
        Trade newTrade = Trade.builder()
                .ticker(TICKER2)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(150))
                .shares(BigInteger.valueOf(10))
                .build();
        Holding expectedHolding1 = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(2000))
                .shares(BigInteger.valueOf(20))
                .averageBuyPrice(BigDecimal.valueOf(100))
                .lastUpdated(LocalDateTime.now())
                .build();
        Holding expectedHolding2 = Holding.builder()
                .ticker(TICKER2)
                .totalPrice(BigDecimal.valueOf(0))
                .shares(BigInteger.valueOf(0))
                .averageBuyPrice(BigDecimal.valueOf(0))
                .lastUpdated(LocalDateTime.now())
                .build();
        List<Holding> expectedHoldings = Arrays.asList(expectedHolding1, expectedHolding2);
        List<Holding> actualHoldings = updateTradeInHolding(oldHolding, oldTrade, newHolding,newTrade);
        assertThat(actualHoldings)
                .usingElementComparatorIgnoringFields(LAST_UPDATED)
                .isEqualTo(expectedHoldings);
    }

    @Test
    public void testGetHoldingsFromTradesTicker() {
        Trade trade1 = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(500))
                .shares(BigInteger.valueOf(20))
                .lastUpdated(LocalDateTime.now())
                .build();
        Trade trade2 = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(900))
                .shares(BigInteger.valueOf(10))
                .lastUpdated(LocalDateTime.now())
                .build();
        Trade trade3 = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(450))
                .shares(BigInteger.valueOf(25))
                .lastUpdated(LocalDateTime.now())
                .build();
        Trade trade4 = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(600))
                .shares(BigInteger.valueOf(25))
                .lastUpdated(LocalDateTime.now())
                .build();
        Holding holding = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(212500).divide(BigDecimal.valueOf(45), MathContext.DECIMAL128))
                .shares(BigInteger.valueOf(10))
                .averageBuyPrice(BigDecimal.valueOf(21250).divide(BigDecimal.valueOf(45), MathContext.DECIMAL128))
                .build();
        List<Trade> trades = Arrays.asList(trade1, trade2, trade3, trade4);
        List<Holding> expectedHoldings = Collections.singletonList(holding);
        List<Holding> actualHoldings = getHoldingsFromTrades(trades);
        assertThat(actualHoldings)
                .usingElementComparatorIgnoringFields(LAST_UPDATED)
                .isEqualTo(expectedHoldings);
    }

    @Test
    public void testGetHoldingsFromTradesTickers() {
        Trade trade1 = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(500))
                .shares(BigInteger.valueOf(20))
                .lastUpdated(LocalDateTime.now())
                .build();
        Trade trade2 = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(900))
                .shares(BigInteger.valueOf(10))
                .lastUpdated(LocalDateTime.now())
                .build();
        Trade trade3 = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(450))
                .shares(BigInteger.valueOf(25))
                .lastUpdated(LocalDateTime.now())
                .build();
        Trade trade4 = Trade.builder()
                .ticker(TICKER1)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(600))
                .shares(BigInteger.valueOf(25))
                .lastUpdated(LocalDateTime.now())
                .build();
        Trade trade5 = Trade.builder()
                .ticker(TICKER2)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(550))
                .shares(BigInteger.valueOf(20))
                .lastUpdated(LocalDateTime.now())
                .build();
        Trade trade6 = Trade.builder()
                .ticker(TICKER2)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(650))
                .shares(BigInteger.valueOf(10))
                .lastUpdated(LocalDateTime.now())
                .build();
        Trade trade7 = Trade.builder()
                .ticker(TICKER2)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(450))
                .shares(BigInteger.valueOf(25))
                .lastUpdated(LocalDateTime.now())
                .build();
        Trade trade8 = Trade.builder()
                .ticker(TICKER2)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(900))
                .shares(BigInteger.valueOf(55))
                .lastUpdated(LocalDateTime.now())
                .build();
        Trade trade9 = Trade.builder()
                .ticker(TICKER3)
                .transactionType(TransactionType.BUY)
                .price(BigDecimal.valueOf(550))
                .shares(BigInteger.valueOf(20))
                .lastUpdated(LocalDateTime.now())
                .build();
        Trade trade10 = Trade.builder()
                .ticker(TICKER3)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(650))
                .shares(BigInteger.valueOf(5))
                .lastUpdated(LocalDateTime.now())
                .build();
        Trade trade11 = Trade.builder()
                .ticker(TICKER3)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(450))
                .shares(BigInteger.valueOf(5))
                .lastUpdated(LocalDateTime.now())
                .build();
        Trade trade12 = Trade.builder()
                .ticker(TICKER3)
                .transactionType(TransactionType.SELL)
                .price(BigDecimal.valueOf(900))
                .shares(BigInteger.valueOf(5))
                .lastUpdated(LocalDateTime.now())
                .build();
        Holding holding1 = Holding.builder()
                .ticker(TICKER1)
                .totalPrice(BigDecimal.valueOf(212500).divide(BigDecimal.valueOf(45), MathContext.DECIMAL128))
                .shares(BigInteger.valueOf(10))
                .averageBuyPrice(BigDecimal.valueOf(21250).divide(BigDecimal.valueOf(45), MathContext.DECIMAL128))
                .build();
        Holding holding2 = Holding.builder()
                .ticker(TICKER3)
                .totalPrice(BigDecimal.valueOf(2750))
                .shares(BigInteger.valueOf(5))
                .averageBuyPrice(BigDecimal.valueOf(550))
                .build();
        List<Trade> trades = Arrays.asList(trade1, trade2, trade3, trade4, trade5, trade6,
                trade7, trade8, trade9, trade10, trade11, trade12);
        List<Holding> expectedHoldings = Arrays.asList(holding1, holding2);
        List<Holding> actualHoldings = getHoldingsFromTrades(trades);
        assertThat(actualHoldings)
                .usingElementComparatorIgnoringFields(LAST_UPDATED)
                .isEqualTo(expectedHoldings);
    }

    @Test
    public void testGetHoldingsFromEmptyTrades() {
        List<Trade> trades = Collections.emptyList();
        List<Holding> expectedHoldings = Collections.emptyList();
        List<Holding> actualHoldings = getHoldingsFromTrades(trades);
        assertThat(actualHoldings).isEqualTo(expectedHoldings);
    }

}
