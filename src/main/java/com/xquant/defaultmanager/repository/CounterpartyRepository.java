package com.xquant.defaultmanager.repository;

import com.xquant.defaultmanager.entity.Counterparty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface CounterpartyRepository extends
        JpaRepository<Counterparty, Long>,
        JpaSpecificationExecutor<Counterparty> {

    Optional<Counterparty> findByCounterpartyCode(String counterpartyCode);
    List<Counterparty> findByCreditRating(String creditRating);
    List<Counterparty> findByActiveTrue();
    boolean existsByCounterpartyCode(String counterpartyCode);
}
