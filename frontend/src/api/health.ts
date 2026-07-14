import { http } from './http'

export interface HealthInfo {
  status: string
  db: string
  service: string
}

export const healthApi = {
  check() {
    return http.get<HealthInfo>('/api/health')
  },
}
