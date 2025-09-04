import request from '@/utils/request'
import type { BreachRecordDTO, BreachRecordResponseDTO } from '@/types'

export const breachApi = {
  // 创建违约记录
  createBreachRecord(data: BreachRecordDTO) {
    return request.post<BreachRecordResponseDTO>('/breach-records', data)
  },

  // 根据ID获取违约记录
  getBreachRecordById(id: number) {
    return request.get<BreachRecordResponseDTO>(`/breach-records/${id}`)
  },

  // 根据客户ID查询违约记录
  getBreachRecordsByCustomerId(customerId: number) {
    return request.get<BreachRecordResponseDTO[]>(`/breach-records/customer/${customerId}`)
  },

  // 根据状态查询违约记录
  getBreachRecordsByStatus(status: 'PENDING' | 'APPROVED' | 'REJECTED') {
    return request.get<BreachRecordResponseDTO[]>(`/breach-records/status/${status}`)
  },

  // 根据严重程度查询违约记录
  getBreachRecordsBySeverity(severity: 'HIGH' | 'MEDIUM' | 'LOW') {
    return request.get<BreachRecordResponseDTO[]>(`/breach-records/severity/${severity}`)
  },

  // 审核违约记录
  reviewBreachRecord(id: number, status: 'APPROVED' | 'REJECTED', reviewerId: number) {
    return request.put<BreachRecordResponseDTO>(`/breach-records/${id}/review`, null, {
      params: { status, reviewerId }
    })
  },

  // 删除违约记录
  deleteBreachRecord(id: number) {
    return request.delete(`/breach-records/${id}`)
  }
}
