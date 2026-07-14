<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Calculator, Grid2X2, House as HouseIcon, List, Pencil, Plus, Search, ShoppingBag, Trash2, X } from '@lucide/vue'
import { dataStore } from '../store/dataStore'
import type { House } from '../types'

const route = useRoute()
const router = useRouter()
const state = dataStore.state
const view = ref<'table' | 'grid'>('table')
const editorOpen = ref(false)
const editingId = ref<string | null>(null)
const detail = ref<House | null>(null)
const deleteTarget = ref<House | null>(null)
const error = ref('')

const filters = reactive({
  keyword: String(route.query.keyword ?? ''),
  buildingNo: '',
  minPrice: null as number | null,
  maxPrice: null as number | null,
  minArea: null as number | null,
  maxArea: null as number | null,
  status: 'ALL' as 'ALL' | 'ON_SALE' | 'SOLD',
})

const draft = reactive({ buildingId: '', buildingNo: '', roomNo: '', area: 0, unitPrice: 0 })

const filtered = computed(() => state.houses.filter((house) => {
  const building = dataStore.buildingById(house.buildingId)
  const keyword = filters.keyword.trim().toLowerCase()
  const matchesKeyword = !keyword || [building?.name ?? '', house.buildingNo, house.roomNo, house.id].some((value) => value.toLowerCase().includes(keyword))
  return matchesKeyword
    && (!filters.buildingNo.trim() || house.buildingNo.toLowerCase().includes(filters.buildingNo.trim().toLowerCase()))
    && (filters.minPrice === null || house.totalPrice >= filters.minPrice)
    && (filters.maxPrice === null || house.totalPrice <= filters.maxPrice)
    && (filters.minArea === null || house.area >= filters.minArea)
    && (filters.maxArea === null || house.area <= filters.maxArea)
    && (filters.status === 'ALL' || house.status === filters.status)
}))

const previewTotal = computed(() => Number(draft.area || 0) * Number(draft.unitPrice || 0))

function buildingName(id: string) {
  return dataStore.buildingById(id)?.name ?? '未知楼盘'
}

function formatMoney(value: number, precise = false) {
  if (precise) return `¥${value.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
  return `${(value / 10_000).toLocaleString('zh-CN', { maximumFractionDigits: 2 })} 万`
}

function openCreate() {
  editingId.value = null
  Object.assign(draft, { buildingId: state.buildings[0]?.id ?? '', buildingNo: '', roomNo: '', area: 0, unitPrice: 0 })
  error.value = ''
  editorOpen.value = true
}

function openEdit(house: House) {
  if (house.status === 'SOLD') return
  editingId.value = house.id
  Object.assign(draft, { buildingId: house.buildingId, buildingNo: house.buildingNo, roomNo: house.roomNo, area: house.area, unitPrice: house.unitPrice })
  detail.value = null
  error.value = ''
  editorOpen.value = true
}

async function save() {
  error.value = ''
  if (!draft.buildingId || !draft.buildingNo.trim() || !draft.roomNo.trim() || draft.area <= 0 || draft.unitPrice <= 0) {
    error.value = '请选择楼盘，并填写有效的楼号、房号、面积和单价。'
    return
  }
  try {
    await dataStore.saveHouse({
      id: editingId.value ?? undefined,
      buildingId: draft.buildingId,
      buildingNo: draft.buildingNo.trim(),
      roomNo: draft.roomNo.trim(),
      area: Number(draft.area),
      unitPrice: Number(draft.unitPrice),
      status: 'ON_SALE',
    })
    editorOpen.value = false
  } catch (cause) {
    error.value = cause instanceof Error ? cause.message : '保存失败'
  }
}

async function confirmDelete() {
  if (!deleteTarget.value) return
  try {
    await dataStore.removeHouse(deleteTarget.value.id)
    deleteTarget.value = null
  } catch (cause) {
    error.value = cause instanceof Error ? cause.message : '删除失败'
    deleteTarget.value = null
  }
}

function clearFilters() {
  Object.assign(filters, { keyword: '', buildingNo: '', minPrice: null, maxPrice: null, minArea: null, maxArea: null, status: 'ALL' })
}

onMounted(async () => {
  try {
    if (!state.loaded) await dataStore.loadAll()
  } catch (cause) {
    error.value = cause instanceof Error ? cause.message : '加载失败'
  }
  if (route.query.create === '1') openCreate()
})
</script>

<template>
  <div>
    <section class="page-intro">
      <div><span class="eyebrow">INVENTORY CENTER</span><h2>房源库存矩阵</h2><p>按楼盘、楼号、价格、面积和销售状态检索资产，并根据状态呈现正确的管理动作。</p></div>
      <div class="page-actions"><button class="button button-primary" @click="openCreate"><Plus :size="16" /> 录入房屋</button></div>
    </section>

    <section class="panel filter-panel">
      <div class="filter-main-row">
        <div class="filter-search"><Search :size="17" /><input v-model="filters.keyword" placeholder="搜索楼盘、楼号、房号或编号" /></div>
        <div class="segmented">
          <button :class="{ 'is-active': filters.status === 'ALL' }" @click="filters.status = 'ALL'">全部 {{ state.houses.length }}</button>
          <button :class="{ 'is-active': filters.status === 'ON_SALE' }" @click="filters.status = 'ON_SALE'">在售</button>
          <button :class="{ 'is-active': filters.status === 'SOLD' }" @click="filters.status = 'SOLD'">已售</button>
        </div>
      </div>
      <div class="advanced-filters">
        <label><span>楼号</span><input v-model="filters.buildingNo" class="filter-input" placeholder="例如 A / 1" /></label>
        <label><span>总价下限（元）</span><input v-model.number="filters.minPrice" class="filter-input" type="number" min="0" placeholder="不限" /></label>
        <label><span>总价上限（元）</span><input v-model.number="filters.maxPrice" class="filter-input" type="number" min="0" placeholder="不限" /></label>
        <label><span>面积下限（㎡）</span><input v-model.number="filters.minArea" class="filter-input" type="number" min="0" placeholder="不限" /></label>
        <label><span>面积上限（㎡）</span><input v-model.number="filters.maxArea" class="filter-input" type="number" min="0" placeholder="不限" /></label>
      </div>
    </section>

    <div v-if="error" class="form-error building-page-error">{{ error }}</div>

    <div class="toolbar inventory-toolbar">
      <span class="result-count">找到 <strong>{{ filtered.length }}</strong> 套房源</span>
      <div class="toolbar-group">
        <button class="button button-ghost button-small" @click="clearFilters">清除筛选</button>
        <div class="segmented"><button :class="{ 'is-active': view === 'table' }" @click="view = 'table'"><List :size="14" /> 表格</button><button :class="{ 'is-active': view === 'grid' }" @click="view = 'grid'"><Grid2X2 :size="14" /> 卡片</button></div>
      </div>
    </div>

    <section v-if="filtered.length && view === 'table'" class="panel house-table-panel">
      <div class="data-table-wrap">
        <table class="data-table house-table">
          <thead><tr><th>房源定位</th><th class="numeric">面积</th><th class="numeric">单价</th><th class="numeric">总价</th><th>状态</th><th /></tr></thead>
          <tbody>
            <tr v-for="house in filtered" :key="house.id" @click="detail = house">
              <td class="entity-cell"><strong>{{ buildingName(house.buildingId) }} · {{ house.buildingNo }}栋 {{ house.roomNo }}</strong><small>{{ house.id }}</small></td>
              <td class="numeric">{{ house.area.toFixed(2) }} ㎡</td>
              <td class="numeric">¥{{ house.unitPrice.toLocaleString('zh-CN') }}/㎡</td>
              <td class="numeric money"><strong>{{ formatMoney(house.totalPrice) }}</strong></td>
              <td><span class="status-badge" :class="house.status === 'ON_SALE' ? 'status-on-sale' : 'status-sold'">{{ house.status === 'ON_SALE' ? '在售' : '已售' }}</span></td>
              <td><div class="row-actions">
                <button class="button button-small" @click.stop="detail = house">查看</button>
                <button v-if="house.status === 'ON_SALE'" class="button button-small" @click.stop="openEdit(house)"><Pencil :size="12" /></button>
                <button v-if="house.status === 'ON_SALE'" class="button button-primary button-small" @click.stop="router.push({ path: '/transactions/new', query: { houseId: house.id } })">成交</button>
                <button v-else class="button button-small" @click.stop="router.push({ path: '/sales', query: { houseId: house.id } })">票据</button>
              </div></td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <section v-else-if="filtered.length" class="house-card-grid">
      <article v-for="house in filtered" :key="house.id" class="house-card" @click="detail = house">
        <div class="house-card-top"><span class="status-badge" :class="house.status === 'ON_SALE' ? 'status-on-sale' : 'status-sold'">{{ house.status === 'ON_SALE' ? '在售' : '已售' }}</span><small>{{ house.id }}</small></div>
        <div class="house-line-art" aria-hidden="true"><i /><i /><i /><i /></div>
        <h3>{{ buildingName(house.buildingId) }}</h3>
        <p>{{ house.buildingNo }}栋 · {{ house.roomNo }}</p>
        <strong class="house-card-price">{{ formatMoney(house.totalPrice) }}</strong>
        <div class="house-card-facts"><span>{{ house.area }} ㎡</span><span>¥{{ house.unitPrice.toLocaleString('zh-CN') }}/㎡</span></div>
      </article>
    </section>

    <section v-else class="panel empty-state"><div><div class="empty-blueprint" /><h3>没有匹配的房源</h3><p>当前筛选组合没有结果。可以调整价格、面积或状态条件后继续浏览。</p><button class="button" @click="clearFilters">清除全部筛选</button></div></section>

    <div v-if="editorOpen" class="drawer-backdrop" @click.self="editorOpen = false">
      <aside class="drawer">
        <header class="drawer-header"><div><span class="eyebrow">HOUSE PROFILE</span><h2>{{ editingId ? '编辑在售房屋' : '录入一套房屋' }}</h2><p>房屋总价由面积与单价实时预览，保存后以服务端规则为准。</p></div><button class="icon-button" aria-label="关闭" @click="editorOpen = false"><X :size="18" /></button></header>
        <div class="form-grid">
          <div class="field is-full"><label>所属楼盘 <em>*</em></label><select v-model="draft.buildingId"><option v-for="building in state.buildings" :key="building.id" :value="building.id">{{ building.name }} · {{ building.id }}</option></select></div>
          <div class="field"><label>楼号 <em>*</em></label><input v-model="draft.buildingNo" placeholder="例如：1 / A / 洋房" /></div>
          <div class="field"><label>房号 <em>*</em></label><input v-model="draft.roomNo" placeholder="例如：1801" /></div>
          <div class="field"><label>建筑面积 <em>*</em></label><input v-model.number="draft.area" type="number" min="0" step="0.01" /><span class="help">单位：平方米</span></div>
          <div class="field"><label>销售单价 <em>*</em></label><input v-model.number="draft.unitPrice" type="number" min="0" step="100" /><span class="help">单位：元 / ㎡</span></div>
          <div class="field is-full price-formula"><Calculator :size="18" /><span><small>总价预览</small><strong>{{ formatMoney(previewTotal, true) }}</strong><em>{{ draft.area || 0 }} ㎡ × ¥{{ Number(draft.unitPrice || 0).toLocaleString('zh-CN') }}/㎡</em></span></div>
        </div>
        <p v-if="error" class="form-error">{{ error }}</p>
        <footer class="drawer-footer"><button class="button" @click="editorOpen = false">取消</button><button class="button button-primary" @click="save">保存房屋</button></footer>
      </aside>
    </div>

    <div v-if="detail" class="drawer-backdrop" @click.self="detail = null">
      <aside class="drawer">
        <header class="drawer-header"><div><span class="eyebrow">HOUSE DOSSIER</span><h2>房屋资产详情</h2></div><button class="icon-button" aria-label="关闭" @click="detail = null"><X :size="18" /></button></header>
        <div class="house-detail-hero">
          <div><span>{{ detail.id }}</span><h2>{{ buildingName(detail.buildingId) }}</h2><p>{{ detail.buildingNo }}栋 · {{ detail.roomNo }}</p></div>
          <span class="status-badge" :class="detail.status === 'ON_SALE' ? 'status-on-sale' : 'status-sold'">{{ detail.status === 'ON_SALE' ? '在售资产' : '已售资产' }}</span>
        </div>
        <div class="house-price-block"><small>资产总价</small><strong>{{ formatMoney(detail.totalPrice, true) }}</strong><p>{{ detail.area.toFixed(2) }} ㎡ × ¥{{ detail.unitPrice.toLocaleString('zh-CN') }}/㎡</p></div>
        <div class="detail-facts"><div class="detail-fact"><span>所属楼盘</span><strong>{{ buildingName(detail.buildingId) }}</strong></div><div class="detail-fact"><span>空间定位</span><strong>{{ detail.buildingNo }}栋 {{ detail.roomNo }}</strong></div><div class="detail-fact"><span>建筑面积</span><strong>{{ detail.area.toFixed(2) }} ㎡</strong></div><div class="detail-fact"><span>销售状态</span><strong>{{ detail.status === 'ON_SALE' ? 'ON_SALE / 在售' : 'SOLD / 已售' }}</strong></div></div>
        <p v-if="detail.status === 'SOLD'" class="constraint-note"><HouseIcon :size="14" /> 已售资产已锁定，不可编辑或删除；可前往成交账本查看票据。</p>
        <footer class="drawer-footer">
          <button v-if="detail.status === 'ON_SALE'" class="button button-danger" @click="deleteTarget = detail; detail = null"><Trash2 :size="14" /> 删除</button>
          <button v-if="detail.status === 'ON_SALE'" class="button" @click="openEdit(detail)"><Pencil :size="14" /> 编辑</button>
          <button v-if="detail.status === 'ON_SALE'" class="button button-accent" @click="router.push({ path: '/transactions/new', query: { houseId: detail.id } })"><ShoppingBag :size="14" /> 进入成交</button>
          <button v-else class="button button-primary" @click="router.push({ path: '/sales', query: { houseId: detail.id } })">查看成交票据</button>
        </footer>
      </aside>
    </div>

    <div v-if="deleteTarget" class="modal-backdrop" @click.self="deleteTarget = null">
      <div class="modal-card"><h3>删除在售房屋？</h3><p>将删除“{{ buildingName(deleteTarget.buildingId) }} · {{ deleteTarget.buildingNo }}栋 {{ deleteTarget.roomNo }}”。此操作只修改浏览器中的 Mock 数据。</p><div class="modal-actions"><button class="button" @click="deleteTarget = null">取消</button><button class="button button-danger" @click="confirmDelete">确认删除</button></div></div>
    </div>
  </div>
</template>
