import { reactive } from 'vue'
import type { Building, DiscountPreview, DiscountType, House, SaleRecord } from '../types'

const seedBuildings: Building[] = [
  {
    id: 'B202607001',
    name: '澄湾序',
    landArea: 58600,
    address: '滨江新区云汀路 18 号',
    developer: '北辰置业',
    remark: '滨水改善型住宅，强调公共空间与景观轴线。',
    createdAt: '2026-07-01T09:20:00',
  },
  {
    id: 'B202607002',
    name: '云阶壹号',
    landArea: 42100,
    address: '中央商务区经纬大道 66 号',
    developer: '寰宇地产',
    remark: '城市核心区高层住宅与商业综合体。',
    createdAt: '2026-07-02T10:10:00',
  },
  {
    id: 'B202607003',
    name: '栖山府',
    landArea: 78300,
    address: '青麓区松风路 9 号',
    developer: '远山发展',
    remark: '低密度山景住区，强调庭院与自然边界。',
    createdAt: '2026-07-03T14:45:00',
  },
  {
    id: 'B202607004',
    name: '光屿公馆',
    landArea: 36500,
    address: '科技新城星港路 120 号',
    developer: '恒屿集团',
    remark: '面向年轻家庭的现代城市住区。',
    createdAt: '2026-07-04T16:30:00',
  },
]

const seedHouses: House[] = [
  { id: 'H2607001', buildingId: 'B202607001', buildingNo: '1', roomNo: '1801', area: 128.5, unitPrice: 23800, totalPrice: 3058300, status: 'ON_SALE', soldAt: null },
  { id: 'H2607002', buildingId: 'B202607001', buildingNo: '1', roomNo: '1202', area: 96.8, unitPrice: 22100, totalPrice: 2139280, status: 'SOLD', soldAt: '2026-07-11T15:36:00' },
  { id: 'H2607003', buildingId: 'B202607001', buildingNo: '2', roomNo: '0901', area: 142.3, unitPrice: 24600, totalPrice: 3500580, status: 'ON_SALE', soldAt: null },
  { id: 'H2607004', buildingId: 'B202607002', buildingNo: 'A', roomNo: '2601', area: 168.0, unitPrice: 32600, totalPrice: 5476800, status: 'ON_SALE', soldAt: null },
  { id: 'H2607005', buildingId: 'B202607002', buildingNo: 'A', roomNo: '1802', area: 121.6, unitPrice: 30800, totalPrice: 3745280, status: 'SOLD', soldAt: '2026-07-09T11:18:00' },
  { id: 'H2607006', buildingId: 'B202607002', buildingNo: 'B', roomNo: '0803', area: 89.5, unitPrice: 28600, totalPrice: 2559700, status: 'ON_SALE', soldAt: null },
  { id: 'H2607007', buildingId: 'B202607003', buildingNo: '叠院', roomNo: 'D05', area: 212.0, unitPrice: 27800, totalPrice: 5893600, status: 'SOLD', soldAt: '2026-07-07T09:42:00' },
  { id: 'H2607008', buildingId: 'B202607003', buildingNo: '叠院', roomNo: 'D08', area: 198.6, unitPrice: 27100, totalPrice: 5382060, status: 'ON_SALE', soldAt: null },
  { id: 'H2607009', buildingId: 'B202607003', buildingNo: '洋房', roomNo: '301', area: 156.4, unitPrice: 25200, totalPrice: 3941280, status: 'ON_SALE', soldAt: null },
  { id: 'H2607010', buildingId: 'B202607004', buildingNo: '3', roomNo: '1501', area: 105.2, unitPrice: 19800, totalPrice: 2082960, status: 'SOLD', soldAt: '2026-07-05T16:05:00' },
  { id: 'H2607011', buildingId: 'B202607004', buildingNo: '3', roomNo: '1502', area: 105.2, unitPrice: 19800, totalPrice: 2082960, status: 'ON_SALE', soldAt: null },
  { id: 'H2607012', buildingId: 'B202607004', buildingNo: '5', roomNo: '0602', area: 78.9, unitPrice: 18500, totalPrice: 1459650, status: 'ON_SALE', soldAt: null },
]

const seedSales: SaleRecord[] = [
  { id: 'S260711001', houseId: 'H2607002', originalPrice: 2139280, discountType: 'PERCENTAGE', discountValue: 0.97, finalPrice: 2075101.6, customerName: '林舒雅', soldAt: '2026-07-11T15:36:00' },
  { id: 'S260709001', houseId: 'H2607005', originalPrice: 3745280, discountType: 'THRESHOLD', discountValue: 150000, finalPrice: 3595280, customerName: '周明远', soldAt: '2026-07-09T11:18:00' },
  { id: 'S260707001', houseId: 'H2607007', originalPrice: 5893600, discountType: 'PERCENTAGE', discountValue: 0.92, finalPrice: 5422112, customerName: '陈思齐', soldAt: '2026-07-07T09:42:00' },
  { id: 'S260705001', houseId: 'H2607010', originalPrice: 2082960, discountType: 'THRESHOLD', discountValue: 50000, finalPrice: 2032960, customerName: '许知行', soldAt: '2026-07-05T16:05:00' },
]

const clone = <T>(value: T): T => JSON.parse(JSON.stringify(value)) as T

const state = reactive({
  buildings: clone(seedBuildings),
  houses: clone(seedHouses),
  sales: clone(seedSales),
})

function nextId(prefix: string, length = 3) {
  const stamp = String(Date.now()).slice(-7)
  return `${prefix}${stamp.slice(-length)}${String(Math.floor(Math.random() * 90 + 10))}`
}

function buildingById(id: string) {
  return state.buildings.find((item) => item.id === id)
}

function houseById(id: string) {
  return state.houses.find((item) => item.id === id)
}

function housesForBuilding(buildingId: string) {
  return state.houses.filter((house) => house.buildingId === buildingId)
}

function saveBuilding(input: Omit<Building, 'id' | 'createdAt'> & Partial<Pick<Building, 'id' | 'createdAt'>>) {
  if (input.id) {
    const existing = buildingById(input.id)
    if (!existing) throw new Error('楼盘不存在')
    Object.assign(existing, input)
    return existing
  }
  const building: Building = {
    ...input,
    id: nextId('B202607', 3),
    createdAt: new Date().toISOString(),
  }
  state.buildings.unshift(building)
  return building
}

function removeBuilding(id: string) {
  if (housesForBuilding(id).length) throw new Error('该楼盘仍有房屋，无法删除')
  const index = state.buildings.findIndex((item) => item.id === id)
  if (index >= 0) state.buildings.splice(index, 1)
}

function saveHouse(input: Omit<House, 'id' | 'totalPrice' | 'soldAt'> & Partial<Pick<House, 'id' | 'soldAt'>>) {
  const duplicate = state.houses.some(
    (item) => item.id !== input.id && item.buildingId === input.buildingId && item.buildingNo === input.buildingNo && item.roomNo === input.roomNo,
  )
  if (duplicate) throw new Error('同一楼盘下楼号与房号不可重复')
  const totalPrice = Number((input.area * input.unitPrice).toFixed(2))
  if (input.id) {
    const existing = houseById(input.id)
    if (!existing) throw new Error('房屋不存在')
    if (existing.status === 'SOLD') throw new Error('已售房屋不可修改')
    Object.assign(existing, input, { totalPrice, status: 'ON_SALE', soldAt: null })
    return existing
  }
  const house: House = {
    ...input,
    id: nextId('H2607', 3),
    totalPrice,
    status: 'ON_SALE',
    soldAt: null,
  }
  state.houses.unshift(house)
  return house
}

function removeHouse(id: string) {
  const house = houseById(id)
  if (!house) throw new Error('房屋不存在')
  if (house.status === 'SOLD') throw new Error('已售房屋不可删除')
  const index = state.houses.findIndex((item) => item.id === id)
  state.houses.splice(index, 1)
}

function discountPreview(originalPrice: number, type: DiscountType): DiscountPreview {
  const tierLabel = originalPrice < 1_000_000 ? '100 万以下' : originalPrice < 3_000_000 ? '100–300 万' : '300 万及以上'
  if (type === 'PERCENTAGE') {
    const value = originalPrice < 1_000_000 ? 1 : originalPrice < 3_000_000 ? 0.97 : 0.92
    const finalPrice = Number((originalPrice * value).toFixed(2))
    return { type, value, finalPrice, saving: originalPrice - finalPrice, tierLabel, formula: `原价 × ${(value * 100).toFixed(0)}%` }
  }
  const value = originalPrice < 1_000_000 ? 20_000 : originalPrice < 3_000_000 ? 50_000 : 150_000
  const finalPrice = Math.max(0, originalPrice - value)
  return { type, value, finalPrice, saving: value, tierLabel, formula: `原价 − ¥${value.toLocaleString('zh-CN')}` }
}

function purchase(houseId: string, type: DiscountType, customerName: string) {
  const house = houseById(houseId)
  if (!house) throw new Error('房屋不存在')
  if (house.status !== 'ON_SALE') throw new Error('房屋已售出，无法重复成交')
  if (!customerName.trim()) throw new Error('客户姓名不能为空')
  const preview = discountPreview(house.totalPrice, type)
  const soldAt = new Date().toISOString()
  house.status = 'SOLD'
  house.soldAt = soldAt
  const record: SaleRecord = {
    id: nextId('S2607', 4),
    houseId,
    originalPrice: house.totalPrice,
    discountType: type,
    discountValue: preview.value,
    finalPrice: preview.finalPrice,
    customerName: customerName.trim(),
    soldAt,
  }
  state.sales.unshift(record)
  return record
}

function resetMockData() {
  state.buildings.splice(0, state.buildings.length, ...clone(seedBuildings))
  state.houses.splice(0, state.houses.length, ...clone(seedHouses))
  state.sales.splice(0, state.sales.length, ...clone(seedSales))
}

export const mockStore = {
  state,
  buildingById,
  houseById,
  housesForBuilding,
  saveBuilding,
  removeBuilding,
  saveHouse,
  removeHouse,
  discountPreview,
  purchase,
  resetMockData,
}
