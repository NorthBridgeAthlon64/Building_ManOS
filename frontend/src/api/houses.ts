import type { House } from '../types'
import { http } from './http'

export type HouseInput = Omit<House, 'id' | 'totalPrice' | 'soldAt' | 'status'> & Partial<Pick<House, 'id' | 'soldAt' | 'status'>>

export const housesApi = {
  list() {
    return http.get<House[]>('/api/houses')
  },
  get(id: string) {
    return http.get<House>(`/api/houses/${encodeURIComponent(id)}`)
  },
  create(input: HouseInput) {
    return http.post<House>('/api/houses', {
      buildingId: input.buildingId,
      buildingNo: input.buildingNo,
      roomNo: input.roomNo,
      area: input.area,
      unitPrice: input.unitPrice,
    })
  },
  update(id: string, input: HouseInput) {
    return http.put<House>(`/api/houses/${encodeURIComponent(id)}`, {
      buildingId: input.buildingId,
      buildingNo: input.buildingNo,
      roomNo: input.roomNo,
      area: input.area,
      unitPrice: input.unitPrice,
    })
  },
  remove(id: string) {
    return http.delete<null>(`/api/houses/${encodeURIComponent(id)}`)
  },
}
