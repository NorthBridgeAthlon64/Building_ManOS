import type { Building } from '../types'
import { http } from './http'

export type BuildingInput = Omit<Building, 'id' | 'createdAt'> & Partial<Pick<Building, 'id' | 'createdAt'>>

export const buildingsApi = {
  list() {
    return http.get<Building[]>('/api/buildings')
  },
  get(id: string) {
    return http.get<Building>(`/api/buildings/${encodeURIComponent(id)}`)
  },
  create(input: BuildingInput) {
    return http.post<Building>('/api/buildings', {
      name: input.name,
      landArea: input.landArea,
      address: input.address,
      developer: input.developer,
      remark: input.remark,
    })
  },
  update(id: string, input: BuildingInput) {
    return http.put<Building>(`/api/buildings/${encodeURIComponent(id)}`, {
      name: input.name,
      landArea: input.landArea,
      address: input.address,
      developer: input.developer,
      remark: input.remark,
    })
  },
  remove(id: string) {
    return http.delete<null>(`/api/buildings/${encodeURIComponent(id)}`)
  },
}
