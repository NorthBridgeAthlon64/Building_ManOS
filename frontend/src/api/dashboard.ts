import { http } from './http'

export interface DashboardSummary {
  buildingCount: number
  houseCount: number
  onSaleCount: number
  soldCount: number
  saleCount: number
  totalSalesAmount: number
}

export const dashboardApi = {
  summary() {
    return http.get<DashboardSummary>('/api/dashboard/summary')
  },
}
