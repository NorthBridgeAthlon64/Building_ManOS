import type { DiscountPreview, DiscountType, SaleRecord } from '../types'
import { http } from './http'

export const purchasesApi = {
  preview(houseId: string, discountType: DiscountType) {
    return http.post<DiscountPreview>('/api/purchases/preview', { houseId, discountType })
  },
  purchase(houseId: string, discountType: DiscountType, customerName: string) {
    return http.post<SaleRecord>('/api/purchases', { houseId, discountType, customerName })
  },
}
