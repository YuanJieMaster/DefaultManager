package com.xquant.defaultmanager.utils;

import com.xquant.defaultmanager.dto.CustomerDTO;
import com.xquant.defaultmanager.dto.CustomerResponseDTO;
import com.xquant.defaultmanager.entity.Customer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerConverter {

    /**
     * 将 DTO 转换为实体
     */
    public static Customer toEntity(CustomerDTO customerDTO) {
        if (customerDTO == null) {
            return null;
        }
        
        Customer customer = new Customer();
        customer.setName(customerDTO.getName());
        customer.setIndustry(customerDTO.getIndustry());
        customer.setRegion(customerDTO.getRegion());
        customer.setExternalRating(customerDTO.getExternalRating());
        customer.setIsBreached(customerDTO.getIsBreached());
        return customer;
    }

    /**
     * 将实体转换为响应 DTO
     */
    public static CustomerResponseDTO toResponseDTO(Customer customer) {
        if (customer == null) {
            return null;
        }
        
        CustomerResponseDTO responseDTO = new CustomerResponseDTO();
        responseDTO.setId(customer.getId());
        responseDTO.setName(customer.getName());
        responseDTO.setIndustry(customer.getIndustry());
        responseDTO.setRegion(customer.getRegion());
        responseDTO.setExternalRating(customer.getExternalRating());
        responseDTO.setIsBreached(customer.getIsBreached());
        responseDTO.setCreateTime(customer.getCreateTime());
        return responseDTO;
    }

    /**
     * 将实体列表转换为响应 DTO 列表
     */
    public static List<CustomerResponseDTO> toResponseDTOList(List<Customer> customers) {
        if (customers == null) {
            return null;
        }
        
        return customers.stream()
                .map(CustomerConverter::toResponseDTO)
                .collect(Collectors.toList());
    }
} 