import request from '@/utils/request'
import type { RebirthRecordDTO, RebirthRecordResponseDTO } from '@/types'

export const rebirthApi = {
  // 创建重生申请
  createRebirthRecord(data: RebirthRecordDTO) {
    return request.post<RebirthRecordResponseDTO>('/rebirth-records', data)
  },

  // 根据ID获取重生记录
  getRebirthRecordById(id: number) {
    return request.get<RebirthRecordResponseDTO>(`/rebirth-records/${id}`)
  },

  // 根据客户ID查询重生记录
  getRebirthRecordsByCustomerId(customerId: number) {
    return request.get<RebirthRecordResponseDTO[]>(`/rebirth-records/customer/${customerId}`)
  },

  // 根据违约记录ID查询重生记录
  getRebirthRecordsByBreachId(breachId: number) {
    return request.get<RebirthRecordResponseDTO[]>(`/rebirth-records/breach/${breachId}`)
  },

  // 根据状态查询重生记录
  getRebirthRecordsByStatus(status: 'PENDING' | 'APPROVED' | 'REJECTED') {
    return request.get<RebirthRecordResponseDTO[]>(`/rebirth-records/status/${status}`)
  },

  // 审核重生申请
  reviewRebirthRecord(id: number, status: 'APPROVED' | 'REJECTED', reviewerId: number) {
    return request.put<RebirthRecordResponseDTO>(`/rebirth-records/${id}/review`, null, {
      params: { status, reviewerId }
    })
  },

  // 删除重生记录
  deleteRebirthRecord(id: number) {
    return request.delete(`/rebirth-records/${id}`)
  },

  // 检查客户是否可以申请重生
  canCustomerRebirth(customerId: number) {
    return request.get<boolean>(`/rebirth-records/customer/${customerId}/can-rebirth`)
  }
}
