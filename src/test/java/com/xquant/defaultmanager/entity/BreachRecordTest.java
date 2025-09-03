package com.xquant.defaultmanager.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BreachRecordTest {
    
    @Test
    void testBreachRecordCreation() {
        BreachRecord breachRecord = new BreachRecord();
        assertNotNull(breachRecord);
    }
    
    @Test
    void testSeverityEnum() {
        assertEquals(3, BreachRecord.Severity.values().length);
        assertTrue(BreachRecord.Severity.HIGH.name().equals("HIGH"));
        assertTrue(BreachRecord.Severity.MEDIUM.name().equals("MEDIUM"));
        assertTrue(BreachRecord.Severity.LOW.name().equals("LOW"));
    }
    
    @Test
    void testReviewStatusEnum() {
        assertEquals(3, BreachRecord.ReviewStatus.values().length);
        assertTrue(BreachRecord.ReviewStatus.PENDING.name().equals("PENDING"));
        assertTrue(BreachRecord.ReviewStatus.APPROVED.name().equals("APPROVED"));
        assertTrue(BreachRecord.ReviewStatus.REJECTED.name().equals("REJECTED"));
        
        // 测试中文描述
        assertEquals("待审核", BreachRecord.ReviewStatus.PENDING.getDescription());
        assertEquals("已通过", BreachRecord.ReviewStatus.APPROVED.getDescription());
        assertEquals("已拒绝", BreachRecord.ReviewStatus.REJECTED.getDescription());
    }
    
    @Test
    void testDefaultStatus() {
        BreachRecord breachRecord = new BreachRecord();
        assertEquals(BreachRecord.ReviewStatus.PENDING, breachRecord.getStatus());
    }
}
