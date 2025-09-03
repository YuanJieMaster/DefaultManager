package com.xquant.defaultmanager.repository;

import com.xquant.defaultmanager.entity.RebirthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RebirthRecordRepository extends JpaRepository<RebirthRecord, Long> {
    
    List<RebirthRecord> findByCustomerId(Long customerId);
    
    List<RebirthRecord> findByBreachRecordId(Long breachId);
    
    List<RebirthRecord> findByStatus(RebirthRecord.ReviewStatus status);
    
    @Query("SELECT rr FROM RebirthRecord rr WHERE rr.customer.id = :customerId AND rr.status = :status")
    List<RebirthRecord> findByCustomerIdAndStatus(@Param("customerId") Long customerId, 
                                                 @Param("status") RebirthRecord.ReviewStatus status);
    
    @Query("SELECT COUNT(rr) FROM RebirthRecord rr WHERE rr.customer.id = :customerId AND rr.status = 'APPROVED'")
    Long countApprovedRebirthsByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT rr FROM RebirthRecord rr WHERE rr.breachRecord.id = :breachId AND rr.status = 'APPROVED'")
    List<RebirthRecord> findApprovedRebirthsByBreachId(@Param("breachId") Long breachId);
}
