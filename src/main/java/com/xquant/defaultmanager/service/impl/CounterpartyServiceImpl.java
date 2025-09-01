package com.xquant.defaultmanager.service.impl;

import com.xquant.defaultmanager.entity.Counterparty;
import com.xquant.defaultmanager.repository.CounterpartyRepository;
import com.xquant.defaultmanager.service.CounterpartyService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CounterpartyServiceImpl implements CounterpartyService {

    @Resource
    private CounterpartyRepository counterpartyRepository;

    @Override
    public Counterparty createCounterparty(Counterparty counterparty) {
        if (counterpartyRepository.existsByCounterpartyCode(counterparty.getCounterpartyCode())) {
            throw new RuntimeException("交易对手代码已存在");
        }
        return counterpartyRepository.save(counterparty);
    }

    @Override
    public Counterparty updateCounterparty(Long id, Counterparty counterpartyDetails) {
        return counterpartyRepository.findById(id)
                .map(existing -> {
                    existing.setName(counterpartyDetails.getName());
                    existing.setCreditRating(counterpartyDetails.getCreditRating());
                    existing.setIndustry(counterpartyDetails.getIndustry());
                    existing.setRegion(counterpartyDetails.getRegion());
                    existing.setActive(counterpartyDetails.getActive());
                    return counterpartyRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("交易对手不存在"));
    }

    @Override
    public void deleteCounterparty(Long id) {
        counterpartyRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Counterparty> getCounterpartyById(Long id) {
        return counterpartyRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Counterparty> getCounterpartyByCode(String counterpartyCode) {
        return counterpartyRepository.findByCounterpartyCode(counterpartyCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Counterparty> getAllCounterparties() {
        return counterpartyRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Counterparty> getCounterparties(Pageable pageable) {
        return counterpartyRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Counterparty> getCounterpartiesByRating(String rating) {
        return counterpartyRepository.findByCreditRating(rating);
    }

    @Override
    public boolean existsByCounterpartyCode(String counterpartyCode) {
        return counterpartyRepository.existsByCounterpartyCode(counterpartyCode);
    }
}
