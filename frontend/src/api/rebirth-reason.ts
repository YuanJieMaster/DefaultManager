import request from '@/utils/request'
import type { RebirthReasonDTO, RebirthReasonResponseDTO } from '@/types'

export const rebirthReasonApi = {
  // 创建重生原因
  createRebirthReason(data: RebirthReasonDTO) {
    return request.post<RebirthReasonResponseDTO>('/rebirth-reasons', data)
  },

  // 根据ID获取重生原因
  getRebirthReasonById(id: number) {
    return request.get<RebirthReasonResponseDTO>(`/rebirth-reasons/${id}`)
  },

  // 获取所有重生原因
  getAllRebirthReasons() {
    return request.get<RebirthReasonResponseDTO[]>('/rebirth-reasons')
  },

  // 获取启用的重生原因
  getEnabledRebirthReasons() {
    return request.get<RebirthReasonResponseDTO[]>('/rebirth-reasons/enabled')
  },

  // 更新重生原因
  updateRebirthReason(id: number, data: RebirthReasonDTO) {
    return request.put<RebirthReasonResponseDTO>(`/rebirth-reasons/${id}`, data)
  },

  // 删除重生原因
  deleteRebirthReason(id: number) {
    return request.delete(`/rebirth-reasons/${id}`)
  },

  // 切换重生原因启用状态
  toggleRebirthReasonStatus(id: number) {
    return request.put<RebirthReasonResponseDTO>(`/rebirth-reasons/${id}/toggle-status`)
  }
}