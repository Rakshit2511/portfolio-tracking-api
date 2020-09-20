package dev.rakshit.portfoliotrackingapi.service;

import dev.rakshit.portfoliotrackingapi.exceptions.InvalidTransactionException;
import dev.rakshit.portfoliotrackingapi.models.Holding;
import dev.rakshit.portfoliotrackingapi.models.Portfolio;
import dev.rakshit.portfoliotrackingapi.models.Trade;
import dev.rakshit.portfoliotrackingapi.models.TransactionType;
import dev.rakshit.portfoliotrackingapi.repository.HoldingRepository;
import dev.rakshit.portfoliotrackingapi.repository.TradeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static dev.rakshit.portfoliotrackingapi.util.HoldingUtil.*;


@Slf4j
@Service
public class PortfolioTrackingService {

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private HoldingRepository holdingRepository;

    /**
     * Deletes trade from trade table
     *
     * @param trade Trade to be deleted
     */
    public void deleteTrade(Trade trade) {
        tradeRepository.delete(trade);
    }

    /**
     * Gives holding by trade. Sends a default holding if no holding is present in db
     *
     * @param trade trade for which holding is to be sent
     * @return Holding from db if present else a new one
     */
    private Holding getHoldingByTrade(Trade trade) {
        return holdingRepository.findById(trade.getTicker())
                .orElse(Holding.builder()
                        .ticker(trade.getTicker())
                        .shares(BigInteger.ZERO)
                        .totalPrice(BigDecimal.ZERO)
                        .averageBuyPrice(BigDecimal.ZERO)
                        .build());
    }

    /**
     * Adds a trade and updates the holdings
     *
     * @param trade trade to be added.
     * @return Trade that is added. Throws InvalidTransactionException if trade cannot be added.
     */
    public Trade addTrade(Trade trade) {
        Holding holding = addTradeToHolding(getHoldingByTrade(trade), trade);
        saveHolding(holding);
        return tradeRepository.save(trade);
    }

    /**
     * updates a trade and updates the holdings
     *
     * @param trade trade to be updated.
     * @return Trade that is updated. Throws InvalidTransactionException if no trade is found or if it cannot be updated.
     */
    public Trade updateTrade(Trade trade, Trade newTrade) {
        assertHoldingPresent(trade.getTicker());
        List<Holding> holdings = updateTradeInHolding(getHoldingByTrade(trade), trade, getHoldingByTrade(newTrade), newTrade);
        holdingRepository.saveAll(holdings);
        return tradeRepository.save(newTrade);
    }

    /**
     * Checks if trade is valid or not. Throws InvalidTransactionException if trade is not valid.
     *
     * @param trade Trade to be validated
     */
    public void isValidTrade(Trade trade) {
        Optional<Holding> holding = getHolding(trade.getTicker());
        if (TransactionType.SELL == trade.getTransactionType() && !holding.isPresent()) {
            throw new InvalidTransactionException("No shares available to sell");
        } else if (0 == BigInteger.ZERO.compareTo(trade.getShares())) {
            throw new InvalidTransactionException("Shares cannot be zero");
        } else if (0 < BigInteger.ZERO.compareTo(trade.getShares())) {
            throw new InvalidTransactionException("Shares cannot less than zero");
        } else if (0 == BigDecimal.ZERO.compareTo(trade.getPrice())) {
            throw new InvalidTransactionException("Price cannot be zero");
        } else if (0 < BigDecimal.ZERO.compareTo(trade.getPrice())) {
            throw new InvalidTransactionException("Price cannot less than zero");
        }
    }

    /**
     * Deletes a trade and updates the holdings
     *
     * @param id trade id corresponding to which, the trade is to be deleted.
     * @return Trade that is deleted. Throws InvalidTransactionException if no trade is found or if it cannot be deleted.
     */
    public Optional<Trade> deleteTrade(Long id) {
//        getTrade(id).map(trade -> {
//            assertHoldingPresent(trade.getTicker());
//            Holding holding = deleteTradeFromHolding(getHoldingByTrade(trade), trade);
//            saveHolding(holding);
//            deleteTrade(trade);
//            return trade;
//        });
        Optional<Trade> tradeOptional = getTrade(id);
        if (!tradeOptional.isPresent()) {
            return Optional.empty();
        }
        Trade trade = tradeOptional.get();
        assertHoldingPresent(trade.getTicker());
        Holding holding = deleteTradeFromHolding(getHoldingByTrade(trade), trade);
        saveHolding(holding);
        deleteTrade(trade);
        return tradeOptional;
    }

    /**
     * Saves or updates holding in db. If given holding has shares or price as zero,
     * it deletes the holding from db
     *
     * @param holding Holding to be saved into db
     */
    private void saveHolding(Holding holding) {
        if (BigInteger.ZERO.equals(holding.getShares())) {
            holdingRepository.deleteById(holding.getTicker());
        }
        holdingRepository.save(holding);
    }

    /**
     * Gets a trade based on trade id.
     *
     * @return Trade based on trade id.
     */
    public Optional<Trade> getTrade(Long id) {
        return tradeRepository.findById(id);
    }

    /**
     * Gets all the trades present.
     *
     * @return List of all trades corresponding to the user.
     */
    public List<Trade> getTrades() {
        return tradeRepository.findAll();
    }

    /**
     * Gets a holdings based on ticker.
     *
     * @return Holding based on ticker.
     */
    public Optional<Holding> getHolding(String ticker) {
        return holdingRepository.findById(ticker);
    }

    /**
     * Gets all the holdings present.
     *
     * @return List of all holdings corresponding to the user.
     */
    public List<Holding> getHoldings() {
        return holdingRepository.findAll();
    }

    /**
     * Refreshes all the holdings by recalculating based on all trades present.
     *
     * @return List of all holdings corresponding to the user.
     */
    public List<Holding> refreshHoldings() {
        return holdingRepository.saveAll(getHoldingsFromTrades(getTrades()));
    }

    /**
     * Groups all the securities and trades corresponding to it.
     *
     * @return All the securities and trades corresponding to it.
     */
    public List<Portfolio> getPortfolio() {
        return getHoldings().stream().map(Holding::getTicker)
                .map(this::getPortfolioByTicker)
                .collect(Collectors.toList());
    }

    /**
     * Takes all the holdings and calculates the returns
     *
     * @return Net Return calculated from the holdings and Optional.empty() if no holding present
     */
    public Optional<BigDecimal> getReturns() {
        return getHoldings().stream()
                .map(this::getReturnsByHolding)
                .reduce(BigDecimal::add);
    }

    /**
     * Groups all the trades corresponding to a security.
     *
     * @return Security and trades corresponding to it grouped together.
     */
    private Portfolio getPortfolioByTicker(String ticker) {
        return Portfolio.builder()
                .ticker(ticker)
                .trades(getTradesByTicker(ticker))
                .build();
    }

    /**
     * Confirms that Holding is present for a ticker.
     * Logs error if no holding is found for the given ticker
     *
     * @param ticker Ticker for which the holding is to be checked
     */
    private void assertHoldingPresent(String ticker) {
        Optional<Holding> holdingOptional = getHolding(ticker);
        if (!holdingOptional.isPresent()) {
            log.error("No Holding found with ticker : {}", ticker);
        }
    }

    /**
     * Gets all the trades corresponding to a ticker.
     *
     * @return All the trades corresponding to a ticker.
     */
    private List<Trade> getTradesByTicker(String ticker) {
        return tradeRepository.findByTicker(ticker);
    }

    /**
     * Takes holding and calculates its return
     *
     * @return Net Return calculated from the given holding
     */
    private BigDecimal getReturnsByHolding(Holding holding) {
        BigDecimal priceDifference = getCurrentPrice(holding).subtract(holding.getAverageBuyPrice());
        return priceDifference.multiply(BigDecimal.valueOf(holding.getShares().intValue()));
    }

    /**
     * Takes the holding and tells it's current price using it's ticker
     *
     * @return Current price of a holding by it's ticker
     */
    private BigDecimal getCurrentPrice(Holding holding) {
        return BigDecimal.valueOf(100);
    }

}
