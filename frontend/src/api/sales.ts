import type { SaleRecord } from '../types'
import { http } from './http'

export const salesApi = {
  list(houseId?: string) {
    const query = houseId ? `?houseId=${encodeURIComponent(houseId)}` : ''
    return http.get<SaleRecord[]>(`/api/sales${query}`)
  },
}
