package dev.rakshit.portfoliotrackingapi.repository;

import dev.rakshit.portfoliotrackingapi.models.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, String> {

}