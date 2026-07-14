import { reactive } from 'vue'
import { buildingsApi, type BuildingInput } from '../api/buildings'
import { housesApi, type HouseInput } from '../api/houses'
import { purchasesApi } from '../api/purchases'
import { salesApi } from '../api/sales'
import type { Building, DiscountType, House, SaleRecord } from '../types'

const state = reactive({
  buildings: [] as Building[],
  houses: [] as House[],
  sales: [] as SaleRecord[],
  loaded: false,
  loading: false,
  error: '',
})

function buildingById(id: string) {
  return state.buildings.find((item) => item.id === id)
}

function houseById(id: string) {
  return state.houses.find((item) => item.id === id)
}

function housesForBuilding(buildingId: string) {
  return state.houses.filter((house) => house.buildingId === buildingId)
}

async function loadAll() {
  state.loading = true
  state.error = ''
  try {
    const [buildings, houses, sales] = await Promise.all([
      buildingsApi.list(),
      housesApi.list(),
      salesApi.list(),
    ])
    state.buildings.splice(0, state.buildings.length, ...buildings)
    state.houses.splice(0, state.houses.length, ...houses)
    state.sales.splice(0, state.sales.length, ...sales)
    state.loaded = true
  } catch (cause) {
    state.error = cause instanceof Error ? cause.message : '加载数据失败'
    throw cause
  } finally {
    state.loading = false
  }
}

async function saveBuilding(input: BuildingInput) {
  const saved = input.id
    ? await buildingsApi.update(input.id, input)
    : await buildingsApi.create(input)
  await loadAll()
  return saved
}

async function removeBuilding(id: string) {
  await buildingsApi.remove(id)
  await loadAll()
}

async function saveHouse(input: HouseInput) {
  const saved = input.id
    ? await housesApi.update(input.id, input)
    : await housesApi.create(input)
  await loadAll()
  return saved
}

async function removeHouse(id: string) {
  await housesApi.remove(id)
  await loadAll()
}

async function discountPreview(houseId: string, type: DiscountType) {
  return purchasesApi.preview(houseId, type)
}

async function purchase(houseId: string, type: DiscountType, customerName: string) {
  const record = await purchasesApi.purchase(houseId, type, customerName)
  await loadAll()
  return record
}

export const dataStore = {
  state,
  loadAll,
  buildingById,
  houseById,
  housesForBuilding,
  saveBuilding,
  removeBuilding,
  saveHouse,
  removeHouse,
  discountPreview,
  purchase,
}
