import request from '@/utils/request'
import type { CustomerDTO, CustomerResponseDTO } from '@/types'

export const customerApi = {
  // 创建客户
  createCustomer(data: CustomerDTO) {
    return request.post<CustomerResponseDTO>('/customers', data)
  },

  // 获取所有客户
  getAllCustomers() {
    return request.get<CustomerResponseDTO[]>('/customers')
  },

  // 根据ID获取客户
  getCustomerById(id: number) {
    return request.get<CustomerResponseDTO>(`/customers/${id}`)
  },

  // 根据名称查找客户
  getCustomerByName(name: string) {
    return request.get<CustomerResponseDTO>('/customers/search/name', {
      params: { name }
    })
  },

  // 根据行业查找客户
  getCustomersByIndustry(industry: string) {
    return request.get<CustomerResponseDTO[]>('/customers/search/industry', {
      params: { industry }
    })
  },

  // 根据地区查找客户
  getCustomersByRegion(region: string) {
    return request.get<CustomerResponseDTO[]>('/customers/search/region', {
      params: { region }
    })
  },

  // 查找违约客户
  getBreachedCustomers() {
    return request.get<CustomerResponseDTO[]>('/customers/breached')
  },

  // 根据外部评级查找客户
  getCustomersByRating(rating: string) {
    return request.get<CustomerResponseDTO[]>('/customers/search/rating', {
      params: { rating }
    })
  },

  // 更新客户信息
  updateCustomer(id: number, data: CustomerDTO) {
    return request.put<CustomerResponseDTO>(`/customers/${id}`, data)
  },

  // 删除客户
  deleteCustomer(id: number) {
    return request.delete(`/customers/${id}`)
  }
}
