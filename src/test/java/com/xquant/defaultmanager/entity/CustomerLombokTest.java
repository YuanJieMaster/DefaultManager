package com.xquant.defaultmanager.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class CustomerLombokTest {

    @Test
    void testCustomerLombokAnnotations() {
        Customer customer = new Customer();
        
        // 测试 setter 方法
        customer.setId(1L);
        customer.setName("测试客户");
        customer.setIndustry("科技");
        customer.setRegion("北京");
        customer.setExternalRating("AA");
        customer.setIsBreached(false);
        customer.setCreateTime(LocalDateTime.now());
        
        // 测试 getter 方法
        assertEquals(1L, customer.getId());
        assertEquals("测试客户", customer.getName());
        assertEquals("科技", customer.getIndustry());
        assertEquals("北京", customer.getRegion());
        assertEquals("AA", customer.getExternalRating());
        assertFalse(customer.getIsBreached());
        assertNotNull(customer.getCreateTime());
        
        // 测试 toString 方法
        String toString = customer.toString();
        assertTrue(toString.contains("测试客户"));
        assertTrue(toString.contains("科技"));
        
        // 测试 equals 和 hashCode 方法
        Customer customer2 = new Customer();
        customer2.setId(1L);
        customer2.setName("测试客户");
        
        assertEquals(customer, customer2);
        assertEquals(customer.hashCode(), customer2.hashCode());
    }
    
    @Test
    void testCustomerDefaultValues() {
        Customer customer = new Customer();
        
        // 测试默认值
        assertNull(customer.getId());
        assertNull(customer.getName());
        assertNull(customer.getIndustry());
        assertNull(customer.getRegion());
        assertNull(customer.getExternalRating());
        assertFalse(customer.getIsBreached()); // 默认值应该是 false
        assertNull(customer.getCreateTime());
    }
} 