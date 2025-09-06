// 客户相关类型
export interface Customer {
  id: number
  name: string
  industry: string
  region: string
  externalRating: string
  isBreached: boolean
  createTime: string
}

export interface CustomerDTO {
  id?: number
  name: string
  contactPerson?: string
  phone?: string
  industry: string
  region?: string
  address?: string
  email?: string
  externalRating?: string
  status?: 'NORMAL' | 'WARNING' | 'BLOCKED'
  remark?: string
}

export interface CustomerResponseDTO extends CustomerDTO {
  id: number
  isBreached?: boolean
  breachCount?: number
  createTime: string
  updateTime?: string
}

// 违约记录相关类型
export interface BreachRecordDTO {
  customerId: number
  breachReasonId: number
  severity: 'HIGH' | 'MEDIUM' | 'LOW'
  applicantId: number
}

export interface BreachRecordResponseDTO {
  id: number
  customerId: number
  customerName: string
  breachReasonId: number
  breachReasonContent: string
  reason: string // 为了向后兼容保留的字段
  severity: 'HIGH' | 'MEDIUM' | 'LOW'
  applicantId: number
  reviewerId?: number
  status: 'PENDING' | 'APPROVED' | 'REJECTED'
  reviewTime?: string
  createTime: string
}

// 重生记录相关类型
export interface RebirthRecordDTO {
  breachId: number
  customerId: number
  reason: string
  applicantId: number
}

export interface RebirthRecordResponseDTO {
  id: number
  breachId: number
  customerId: number
  customerName: string
  reason: string
  applicantId: number
  reviewerId?: number
  status: 'PENDING' | 'APPROVED' | 'REJECTED'
  reviewTime?: string
  createTime: string
}

// 重生申请表单类型（前端用）
export interface RebirthApplyDTO {
  customerId: number | null
  customerName: string
  breachRecordIds: number[]
  reason: string
  rectificationMeasures: string
  expectedEffects: string
  applyTime: string
  attachments: any[]
}

// 用户相关类型
export interface User {
  id: number
  username: string
  email: string
  firstName: string
  lastName: string
  role: string
  createTime: string
}

// 重生申请相关类型
export interface RebirthApplyDTO {
  customerId: number | null
  customerName: string
  breachRecordIds: number[]
  reason: string
  rectificationMeasures: string
  expectedEffects: string
  applyTime: string
  attachments: string[] // 或者根据实际情况调整为 File[] 或其他类型
}

export interface RebirthApplyResponseDTO {
  id: number
  customerId: number
  customerName: string
  breachRecordIds: number[]
  breachRecordReasons: string[]
  reason: string
  rectificationMeasures: string
  expectedEffects: string
  applyTime: string
  attachments: string[]
  applicantId: number
  applicantName: string
  reviewerId?: number
  reviewerName?: string
  status: 'PENDING' | 'APPROVED' | 'REJECTED'
  reviewTime?: string
  createTime: string
}

// 如果需要，还可以添加审核相关的 DTO
export interface RebirthReviewDTO {
  id: number
  status: 'APPROVED' | 'REJECTED'
  reviewComments?: string
}

// 分页相关类型
export interface PaginationParams {
  page: number
  size: number
  sort?: string
}

export interface PaginatedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

// 违约原因相关类型
export interface BreachReasonDTO {
  reasonContent: string
  isEnabled?: boolean
  sortOrder?: number
}

export interface BreachReasonResponseDTO {
  id: number
  reasonContent: string
  isEnabled: boolean
  sortOrder: number
  createTime: string
  updateTime?: string
}

// 重生原因相关类型
export interface RebirthReasonDTO {
  reasonContent: string
  isEnabled?: boolean
  sortOrder?: number
}

export interface RebirthReasonResponseDTO {
  id: number
  reasonContent: string
  isEnabled: boolean
  sortOrder: number
  createTime: string
  updateTime?: string
}

// 统计相关类型
export interface BreachStatistics {
  totalBreaches: number
  pendingBreaches: number
  approvedBreaches: number
  rejectedBreaches: number
  highSeverityBreaches: number
  mediumSeverityBreaches: number
  lowSeverityBreaches: number
}

export interface IndustryBreachStats {
  industry: string
  breachCount: number
  breachRate: number
}

export interface RegionBreachStats {
  region: string
  breachCount: number
  breachRate: number
}
