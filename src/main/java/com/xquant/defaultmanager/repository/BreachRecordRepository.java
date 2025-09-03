package com.xquant.defaultmanager.repository;

import com.xquant.defaultmanager.entity.BreachRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BreachRecordRepository extends JpaRepository<BreachRecord, Long> {
    
    List<BreachRecord> findByCustomerId(Long customerId);
    
    List<BreachRecord> findByStatus(BreachRecord.ReviewStatus status);
    
    List<BreachRecord> findBySeverity(BreachRecord.Severity severity);
    
    @Query("SELECT br FROM BreachRecord br WHERE br.customer.id = :customerId AND br.status = :status")
    List<BreachRecord> findByCustomerIdAndStatus(@Param("customerId") Long customerId, 
                                                @Param("status") BreachRecord.ReviewStatus status);
    
    @Query("SELECT COUNT(br) FROM BreachRecord br WHERE br.customer.id = :customerId AND br.status = 'APPROVED'")
    Long countApprovedBreachesByCustomerId(@Param("customerId") Long customerId);
}
