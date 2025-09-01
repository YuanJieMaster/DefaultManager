package com.xquant.defaultmanager.service;

import com.xquant.defaultmanager.entity.Counterparty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface CounterpartyService {
    Counterparty createCounterparty(Counterparty counterparty);
    Counterparty updateCounterparty(Long id, Counterparty counterparty);
    void deleteCounterparty(Long id);
    Optional<Counterparty> getCounterpartyById(Long id);
    Optional<Counterparty> getCounterpartyByCode(String counterpartyCode);
    List<Counterparty> getAllCounterparties();
    Page<Counterparty> getCounterparties(Pageable pageable);
    List<Counterparty> getCounterpartiesByRating(String rating);
    boolean existsByCounterpartyCode(String counterpartyCode);
}