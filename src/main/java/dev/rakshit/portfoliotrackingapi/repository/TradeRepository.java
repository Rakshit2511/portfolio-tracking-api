package dev.rakshit.portfoliotrackingapi.repository;

import dev.rakshit.portfoliotrackingapi.models.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    List<Trade> findByTicker(String ticker);

}
