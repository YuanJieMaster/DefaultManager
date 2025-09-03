package com.xquant.defaultmanager.service.impl;

import com.xquant.defaultmanager.dto.CustomerDTO;
import com.xquant.defaultmanager.dto.CustomerResponseDTO;
import com.xquant.defaultmanager.entity.Customer;
import com.xquant.defaultmanager.repository.CustomerRepository;
import com.xquant.defaultmanager.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public CustomerResponseDTO createCustomer(CustomerDTO customerDTO) {
        Customer customer = convertToEntity(customerDTO);
        customer.setCreateTime(LocalDateTime.now());
        Customer savedCustomer = customerRepository.save(customer);
        return convertToResponseDTO(savedCustomer);
    }

    @Override
    public Optional<CustomerResponseDTO> findById(Long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        return customer.map(this::convertToResponseDTO);
    }

    @Override
    public Optional<CustomerResponseDTO> findByName(String name) {
        Optional<Customer> customer = customerRepository.findByName(name);
        return customer.map(this::convertToResponseDTO);
    }

    @Override
    public List<CustomerResponseDTO> findAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponseDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isPresent()) {
            Customer existingCustomer = optionalCustomer.get();
            
            // 更新字段，保留创建时间
            existingCustomer.setName(customerDTO.getName());
            existingCustomer.setIndustry(customerDTO.getIndustry());
            existingCustomer.setRegion(customerDTO.getRegion());
            existingCustomer.setExternalRating(customerDTO.getExternalRating());
            existingCustomer.setIsBreached(customerDTO.getIsBreached());
            
            Customer updatedCustomer = customerRepository.save(existingCustomer);
            return convertToResponseDTO(updatedCustomer);
        }
        throw new RuntimeException("客户不存在，ID: " + id);
    }

    @Override
    public void deleteCustomer(Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
        } else {
            throw new RuntimeException("客户不存在，ID: " + id);
        }
    }

    @Override
    public List<CustomerResponseDTO> findByIndustry(String industry) {
        List<Customer> customers = customerRepository.findByIndustry(industry);
        return customers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerResponseDTO> findByRegion(String region) {
        List<Customer> customers = customerRepository.findByRegion(region);
        return customers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerResponseDTO> findBreachedCustomers() {
        List<Customer> customers = customerRepository.findByIsBreachedTrue();
        return customers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerResponseDTO> findByExternalRating(String externalRating) {
        List<Customer> customers = customerRepository.findByExternalRating(externalRating);
        return customers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将 DTO 转换为实体
     */
    private Customer convertToEntity(CustomerDTO customerDTO) {
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
    private CustomerResponseDTO convertToResponseDTO(Customer customer) {
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
} 