package com.xquant.defaultmanager.repository;

import com.xquant.defaultmanager.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    /**
     * 根据名称查找客户
     */
    Optional<Customer> findByName(String name);
    
    /**
     * 根据行业查找客户列表
     */
    List<Customer> findByIndustry(String industry);
    
    /**
     * 根据地区查找客户列表
     */
    List<Customer> findByRegion(String region);
    
    /**
     * 查找违约的客户
     */
    List<Customer> findByIsBreachedTrue();
    
    /**
     * 根据外部评级查找客户
     */
    List<Customer> findByExternalRating(String externalRating);
} 