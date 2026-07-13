export type HouseStatus = 'ON_SALE' | 'SOLD'
export type DiscountType = 'PERCENTAGE' | 'THRESHOLD'

export interface Building {
  id: string
  name: string
  landArea: number
  address: string
  developer: string
  remark: string
  createdAt: string
}

export interface House {
  id: string
  buildingId: string
  buildingNo: string
  roomNo: string
  area: number
  unitPrice: number
  totalPrice: number
  status: HouseStatus
  soldAt: string | null
}

export interface SaleRecord {
  id: string
  houseId: string
  originalPrice: number
  discountType: DiscountType
  discountValue: number
  finalPrice: number
  customerName: string
  soldAt: string
}

export interface DiscountPreview {
  type: DiscountType
  value: number
  finalPrice: number
  saving: number
  tierLabel: string
  formula: string
}
