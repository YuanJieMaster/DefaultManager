package com.xquant.defaultmanager.service;

import com.xquant.defaultmanager.dto.CustomerDTO;
import com.xquant.defaultmanager.dto.CustomerResponseDTO;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    
    /**
     * 创建新客户
     */
    CustomerResponseDTO createCustomer(CustomerDTO customerDTO);
    
    /**
     * 根据ID查找客户
     */
    Optional<CustomerResponseDTO> findById(Long id);
    
    /**
     * 根据名称查找客户
     */
    Optional<CustomerResponseDTO> findByName(String name);
    
    /**
     * 获取所有客户
     */
    List<CustomerResponseDTO> findAllCustomers();
    
    /**
     * 更新客户信息
     */
    CustomerResponseDTO updateCustomer(Long id, CustomerDTO customerDTO);
    
    /**
     * 删除客户
     */
    void deleteCustomer(Long id);
    
    /**
     * 根据行业查找客户
     */
    List<CustomerResponseDTO> findByIndustry(String industry);
    
    /**
     * 根据地区查找客户
     */
    List<CustomerResponseDTO> findByRegion(String region);
    
    /**
     * 查找违约客户
     */
    List<CustomerResponseDTO> findBreachedCustomers();
    
    /**
     * 根据外部评级查找客户
     */
    List<CustomerResponseDTO> findByExternalRating(String externalRating);
} 