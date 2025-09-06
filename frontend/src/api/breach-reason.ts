import request from '@/utils/request'
import type { BreachReasonDTO, BreachReasonResponseDTO } from '@/types'

export const breachReasonApi = {
  // 创建违约原因
  createBreachReason(data: BreachReasonDTO) {
    return request.post<BreachReasonResponseDTO>('/breach-reasons', data)
  },

  // 根据ID获取违约原因
  getBreachReasonById(id: number) {
    return request.get<BreachReasonResponseDTO>(`/breach-reasons/${id}`)
  },

  // 获取所有违约原因
  getAllBreachReasons() {
    return request.get<BreachReasonResponseDTO[]>('/breach-reasons')
  },

  // 获取启用的违约原因
  getEnabledBreachReasons() {
    return request.get<BreachReasonResponseDTO[]>('/breach-reasons/enabled')
  },

  // 更新违约原因
  updateBreachReason(id: number, data: BreachReasonDTO) {
    return request.put<BreachReasonResponseDTO>(`/breach-reasons/${id}`, data)
  },

  // 删除违约原因
  deleteBreachReason(id: number) {
    return request.delete(`/breach-reasons/${id}`)
  },

  // 切换违约原因启用状态
  toggleBreachReasonStatus(id: number) {
    return request.put<BreachReasonResponseDTO>(`/breach-reasons/${id}/toggle-status`)
  }
}