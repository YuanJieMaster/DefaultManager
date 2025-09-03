package com.xquant.defaultmanager.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RebirthRecordTest {
    
    @Test
    void testRebirthRecordCreation() {
        RebirthRecord rebirthRecord = new RebirthRecord();
        assertNotNull(rebirthRecord);
    }
    
    @Test
    void testReviewStatusEnum() {
        assertEquals(3, RebirthRecord.ReviewStatus.values().length);
        assertTrue(RebirthRecord.ReviewStatus.PENDING.name().equals("PENDING"));
        assertTrue(RebirthRecord.ReviewStatus.APPROVED.name().equals("APPROVED"));
        assertTrue(RebirthRecord.ReviewStatus.REJECTED.name().equals("REJECTED"));
        
        // 测试中文描述
        assertEquals("待审核", RebirthRecord.ReviewStatus.PENDING.getDescription());
        assertEquals("已通过", RebirthRecord.ReviewStatus.APPROVED.getDescription());
        assertEquals("已拒绝", RebirthRecord.ReviewStatus.REJECTED.getDescription());
    }
    
    @Test
    void testDefaultStatus() {
        RebirthRecord rebirthRecord = new RebirthRecord();
        assertEquals(RebirthRecord.ReviewStatus.PENDING, rebirthRecord.getStatus());
    }
}
