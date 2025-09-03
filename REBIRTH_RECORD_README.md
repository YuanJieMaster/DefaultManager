# RebirthRecord 重生记录管理

## 概述

RebirthRecord 模块用于管理客户的重生申请记录，允许违约客户申请重生以恢复其正常状态。重生申请需要经过审核流程，审核通过后客户的违约状态将被重置。

## 功能特性

- 创建重生申请记录
- 查询重生记录（按客户、违约记录、状态等）
- 审核重生申请（批准/拒绝）
- 删除重生记录
- 自动更新客户违约状态（重生成功后重置为 false）
- 检查客户是否可以申请重生

## 数据模型

### RebirthRecord 实体

```java
@Entity
@Table(name = "rebirth_record")
public class RebirthRecord {
    private Long id;                    // 主键ID
    private BreachRecord breachRecord;  // 关联的违约记录
    private Customer customer;          // 关联客户
    private String reason;              // 重生申请原因
    private Long applicantId;           // 申请人ID
    private Long reviewerId;            // 审核人ID
    private ReviewStatus status;        // 审核状态
    private LocalDateTime reviewTime;   // 审核时间
    private LocalDateTime createTime;   // 创建时间
}
```

### 枚举类型

#### ReviewStatus（审核状态）
- `PENDING` - 待审核
- `APPROVED` - 已批准
- `REJECTED` - 已拒绝

## API 接口

### 1. 创建重生申请

```http
POST /api/rebirth-records
Content-Type: application/json

{
    "breachId": 1,
    "customerId": 1,
    "reason": "已整改完成，申请恢复正常状态",
    "applicantId": 100
}
```

### 2. 查询重生记录

```http
# 按ID查询
GET /api/rebirth-records/{id}

# 按客户ID查询
GET /api/rebirth-records/customer/{customerId}

# 按违约记录ID查询
GET /api/rebirth-records/breach/{breachId}

# 按状态查询
GET /api/rebirth-records/status/{status}
```

### 3. 审核重生申请

```http
PUT /api/rebirth-records/{id}/review?status=APPROVED&reviewerId=200
```

### 4. 删除重生记录

```http
DELETE /api/rebirth-records/{id}
```

### 5. 检查客户是否可以申请重生

```http
GET /api/rebirth-records/customer/{customerId}/can-rebirth
```

## 业务逻辑

### 创建重生申请
1. 验证客户和违约记录是否存在
2. 检查客户是否可以申请重生（有违约记录且无待审核/已批准的重生申请）
3. 设置默认状态为 PENDING
4. 记录创建时间
5. 保存到数据库

### 审核重生申请
1. 更新审核状态
2. 记录审核人和审核时间
3. 如果审核通过，自动更新客户的违约状态为 false（重生成功）

### 重生申请条件检查
- 客户必须有违约记录
- 不能有正在进行的重生申请（待审核或已批准）
- 每个违约记录只能申请一次重生

### 数据关联
- 重生记录与违约记录是多对一关系
- 重生记录与客户是多对一关系
- 删除违约记录或客户时，相关的重生记录会被级联删除

## 数据库设计

```sql
CREATE TABLE rebirth_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    breach_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    reason VARCHAR(500) NOT NULL,
    applicant_id BIGINT NOT NULL,
    reviewer_id BIGINT,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    review_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (breach_id) REFERENCES breach_record(id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE,
    INDEX idx_breach_id (breach_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
);
```

## 使用示例

### 创建重生申请
```java
RebirthRecordDTO dto = new RebirthRecordDTO();
dto.setBreachId(1L);
dto.setCustomerId(1L);
dto.setReason("已整改完成，申请恢复正常状态");
dto.setApplicantId(100L);

RebirthRecordResponseDTO response = rebirthRecordService.createRebirthRecord(dto);
```

### 审核重生申请
```java
RebirthRecordResponseDTO response = rebirthRecordService.reviewRebirthRecord(
    1L, 
    RebirthRecord.ReviewStatus.APPROVED, 
    200L
);
```

### 检查客户是否可以申请重生
```java
boolean canRebirth = rebirthRecordService.canCustomerRebirth(1L);
```

## 业务流程

### 完整的违约-重生流程
1. 客户发生违约 → 创建违约记录 → 客户状态变为违约
2. 客户整改 → 申请重生 → 创建重生记录
3. 审核重生申请 → 批准/拒绝
4. 批准后 → 客户状态重置为正常（违约状态变为 false）

### 状态流转
```
违约状态: false → true (违约) → false (重生成功)
重生申请: PENDING → APPROVED/REJECTED
```

## 注意事项

1. 创建重生申请时，客户ID和违约记录ID都必须存在
2. 只有有违约记录的客户才能申请重生
3. 每个客户同时只能有一个有效的重生申请
4. 审核通过后，客户的违约状态会自动重置为 false
5. 支持事务管理，确保数据一致性
6. 使用 JPA 审计功能自动管理创建时间
7. 重生申请与违约记录是一对一的关系

## 与 BreachRecord 的关系

- RebirthRecord 是 BreachRecord 的补充功能
- 违约记录创建后，客户可以通过重生申请来恢复状态
- 重生成功后，违约记录仍然保留，但客户状态会重置
- 支持完整的违约-重生-恢复的业务闭环

