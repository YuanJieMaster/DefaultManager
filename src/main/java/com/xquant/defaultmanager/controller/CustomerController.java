package com.xquant.defaultmanager.controller;

import com.xquant.defaultmanager.dto.CustomerDTO;
import com.xquant.defaultmanager.dto.CustomerResponseDTO;
import com.xquant.defaultmanager.service.CustomerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "客户管理", description = "客户相关的CRUD操作")
@CrossOrigin(origins = "*")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * 创建新客户
     */
    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        CustomerResponseDTO createdCustomer = customerService.createCustomer(customerDTO);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    /**
     * 获取所有客户
     */
    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        List<CustomerResponseDTO> customers = customerService.findAllCustomers();
        return ResponseEntity.ok(customers);
    }

    /**
     * 根据ID获取客户
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {
        Optional<CustomerResponseDTO> customer = customerService.findById(id);
        return customer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据名称查找客户
     */
    @GetMapping("/search/name")
    public ResponseEntity<CustomerResponseDTO> getCustomerByName(@RequestParam String name) {
        Optional<CustomerResponseDTO> customer = customerService.findByName(name);
        return customer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据行业查找客户
     */
    @GetMapping("/search/industry")
    public ResponseEntity<List<CustomerResponseDTO>> getCustomersByIndustry(@RequestParam String industry) {
        List<CustomerResponseDTO> customers = customerService.findByIndustry(industry);
        return ResponseEntity.ok(customers);
    }

    /**
     * 根据地区查找客户
     */
    @GetMapping("/search/region")
    public ResponseEntity<List<CustomerResponseDTO>> getCustomersByRegion(@RequestParam String region) {
        List<CustomerResponseDTO> customers = customerService.findByRegion(region);
        return ResponseEntity.ok(customers);
    }

    /**
     * 查找违约客户
     */
    @GetMapping("/breached")
    public ResponseEntity<List<CustomerResponseDTO>> getBreachedCustomers() {
        List<CustomerResponseDTO> customers = customerService.findBreachedCustomers();
        return ResponseEntity.ok(customers);
    }

    /**
     * 根据外部评级查找客户
     */
    @GetMapping("/search/rating")
    public ResponseEntity<List<CustomerResponseDTO>> getCustomersByRating(@RequestParam String rating) {
        List<CustomerResponseDTO> customers = customerService.findByExternalRating(rating);
        return ResponseEntity.ok(customers);
    }

    /**
     * 更新客户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerDTO customerDTO) {
        try {
            CustomerResponseDTO updatedCustomer = customerService.updateCustomer(id, customerDTO);
            return ResponseEntity.ok(updatedCustomer);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除客户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
