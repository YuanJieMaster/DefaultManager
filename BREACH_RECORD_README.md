# BreachRecord 违约记录管理

## 概述

BreachRecord 模块用于管理客户的违约记录，包括违约记录的创建、审核、查询和删除等功能。

## 功能特性

- 创建违约记录
- 查询违约记录（按客户、状态、严重程度等）
- 审核违约记录（批准/拒绝）
- 删除违约记录
- 自动更新客户违约状态

## 数据模型

### BreachRecord 实体

```java
@Entity
@Table(name = "breach_record")
public class BreachRecord {
    private Long id;                    // 主键ID
    private Customer customer;          // 关联客户
    private String reason;              // 违约原因
    private Severity severity;          // 严重程度
    private Long applicantId;           // 申请人ID
    private Long reviewerId;            // 审核人ID
    private ReviewStatus status;        // 审核状态
    private LocalDateTime reviewTime;   // 审核时间
    private LocalDateTime createTime;   // 创建时间
}
```

### 枚举类型

#### Severity（严重程度）
- `HIGH` - 高
- `MEDIUM` - 中
- `LOW` - 低

#### ReviewStatus（审核状态）
- `PENDING` - 待审核
- `APPROVED` - 已批准
- `REJECTED` - 已拒绝

## API 接口

### 1. 创建违约记录

```http
POST /api/breach-records
Content-Type: application/json

{
    "customerId": 1,
    "reason": "逾期付款超过30天",
    "severity": "HIGH",
    "applicantId": 100
}
```

### 2. 查询违约记录

```http
# 按ID查询
GET /api/breach-records/{id}

# 按客户ID查询
GET /api/breach-records/customer/{customerId}

# 按状态查询
GET /api/breach-records/status/{status}

# 按严重程度查询
GET /api/breach-records/severity/{severity}
```

### 3. 审核违约记录

```http
PUT /api/breach-records/{id}/review?status=APPROVED&reviewerId=200
```

### 4. 删除违约记录

```http
DELETE /api/breach-records/{id}
```

## 业务逻辑

### 创建违约记录
1. 验证客户是否存在
2. 设置默认状态为 PENDING
3. 记录创建时间
4. 保存到数据库

### 审核违约记录
1. 更新审核状态
2. 记录审核人和审核时间
3. 如果审核通过，自动更新客户的违约状态为 true

### 数据关联
- 违约记录与客户是多对一关系
- 删除客户时，相关的违约记录会被级联删除
- 支持按客户、状态、严重程度等维度查询

## 数据库设计

```sql
CREATE TABLE breach_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    reason VARCHAR(500) NOT NULL,
    severity ENUM('HIGH', 'MEDIUM', 'LOW') NOT NULL,
    applicant_id BIGINT NOT NULL,
    reviewer_id BIGINT,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    review_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE,
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status),
    INDEX idx_severity (severity),
    INDEX idx_create_time (create_time)
);
```

## 使用示例

### 创建违约记录
```java
BreachRecordDTO dto = new BreachRecordDTO();
dto.setCustomerId(1L);
dto.setReason("逾期付款超过30天");
dto.setSeverity(BreachRecord.Severity.HIGH);
dto.setApplicantId(100L);

BreachRecordResponseDTO response = breachRecordService.createBreachRecord(dto);
```

### 审核违约记录
```java
BreachRecordResponseDTO response = breachRecordService.reviewBreachRecord(
    1L, 
    BreachRecord.ReviewStatus.APPROVED, 
    200L
);
```

## 注意事项

1. 创建违约记录时，客户ID必须存在
2. 审核通过后，客户的违约状态会自动更新
3. 删除客户时，相关违约记录会被级联删除
4. 支持事务管理，确保数据一致性
5. 使用 JPA 审计功能自动管理创建时间

