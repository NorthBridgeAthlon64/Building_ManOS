<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ArrowRight, Building2, Grid2X2, List, MapPin, Pencil, Plus, Trash2, X } from '@lucide/vue'
import { dataStore } from '../store/dataStore'
import type { Building } from '../types'

const route = useRoute()
const state = dataStore.state
const search = ref('')
const view = ref<'grid' | 'table'>('grid')
const editorOpen = ref(false)
const editingId = ref<string | null>(null)
const detail = ref<Building | null>(null)
const deleteTarget = ref<Building | null>(null)
const error = ref('')
const saving = ref(false)

const draft = reactive({ name: '', landArea: 0, address: '', developer: '', remark: '' })

const filtered = computed(() => {
  const keyword = search.value.trim().toLowerCase()
  if (!keyword) return state.buildings
  return state.buildings.filter((item) => [item.name, item.address, item.developer, item.id].some((value) => value.toLowerCase().includes(keyword)))
})

function houseStats(buildingId: string) {
  const houses = dataStore.housesForBuilding(buildingId)
  const onSale = houses.filter((house) => house.status === 'ON_SALE').length
  return { total: houses.length, onSale, sold: houses.length - onSale }
}

function openCreate() {
  editingId.value = null
  Object.assign(draft, { name: '', landArea: 0, address: '', developer: '', remark: '' })
  error.value = ''
  editorOpen.value = true
}

function openEdit(building: Building) {
  editingId.value = building.id
  Object.assign(draft, {
    name: building.name,
    landArea: building.landArea,
    address: building.address,
    developer: building.developer,
    remark: building.remark,
  })
  detail.value = null
  error.value = ''
  editorOpen.value = true
}

async function save() {
  error.value = ''
  if (!draft.name.trim() || !draft.address.trim() || draft.landArea <= 0) {
    error.value = '请填写楼盘名称、有效占地面积和地址。'
    return
  }
  saving.value = true
  try {
    await dataStore.saveBuilding({
      id: editingId.value ?? undefined,
      name: draft.name.trim(),
      landArea: Number(draft.landArea),
      address: draft.address.trim(),
      developer: draft.developer.trim(),
      remark: draft.remark.trim(),
    })
    editorOpen.value = false
  } catch (cause) {
    error.value = cause instanceof Error ? cause.message : '保存失败'
  } finally {
    saving.value = false
  }
}

async function confirmDelete() {
  if (!deleteTarget.value) return
  error.value = ''
  try {
    await dataStore.removeBuilding(deleteTarget.value.id)
    deleteTarget.value = null
  } catch (cause) {
    error.value = cause instanceof Error ? cause.message : '删除失败'
    deleteTarget.value = null
  }
}

function formatArea(value: number) {
  return `${value.toLocaleString('zh-CN')} ㎡`
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
      <div>
        <span class="eyebrow">BUILDING PORTFOLIO</span>
        <h2>楼盘资产档案</h2>
        <p>以项目为第一层级组织地址、体量、开发商与关联库存，建立清晰的城市资产目录。</p>
      </div>
      <div class="page-actions">
        <button class="button button-primary" @click="openCreate"><Plus :size="16" /> 新增楼盘</button>
      </div>
    </section>

    <div v-if="error" class="form-error building-page-error">{{ error }}</div>
    <div v-if="state.loading" class="form-error building-page-error">正在从 API 加载数据…</div>

    <div class="toolbar">
      <div class="toolbar-group">
        <input v-model="search" class="search-field" aria-label="搜索楼盘" placeholder="搜索名称、地址、开发商或编号" />
        <span class="result-count">{{ filtered.length }} / {{ state.buildings.length }} 个楼盘</span>
      </div>
      <div class="segmented" aria-label="楼盘视图切换">
        <button :class="{ 'is-active': view === 'grid' }" @click="view = 'grid'"><Grid2X2 :size="14" /> 卡片</button>
        <button :class="{ 'is-active': view === 'table' }" @click="view = 'table'"><List :size="14" /> 表格</button>
      </div>
    </div>

    <section v-if="filtered.length && view === 'grid'" class="card-grid">
      <article v-for="(building, index) in filtered" :key="building.id" class="entity-card building-card" @click="detail = building">
        <span class="card-index">ASSET {{ String(index + 1).padStart(2, '0') }} / {{ building.id }}</span>
        <div class="building-silhouette" aria-hidden="true"><i /><i /><i /></div>
        <h3>{{ building.name }}</h3>
        <p class="address"><MapPin :size="12" /> {{ building.address }}</p>
        <div class="card-facts">
          <span>占地面积<strong>{{ formatArea(building.landArea) }}</strong></span>
          <span>开发商<strong>{{ building.developer || '未填写' }}</strong></span>
        </div>
        <div class="inventory-strip">
          <span class="on-sale" :style="{ width: `${houseStats(building.id).total ? houseStats(building.id).onSale / houseStats(building.id).total * 100 : 0}%` }" />
          <span class="sold" :style="{ width: `${houseStats(building.id).total ? houseStats(building.id).sold / houseStats(building.id).total * 100 : 0}%` }" />
        </div>
        <div class="card-footer">
          <span>{{ houseStats(building.id).onSale }} 在售 · {{ houseStats(building.id).sold }} 已售</span>
          <span>查看档案 <ArrowRight :size="13" /></span>
        </div>
      </article>
    </section>

    <section v-else-if="filtered.length" class="panel">
      <div class="data-table-wrap">
        <table class="data-table">
          <thead><tr><th>楼盘</th><th>地址</th><th class="numeric">占地面积</th><th>开发商</th><th>库存</th><th /></tr></thead>
          <tbody>
            <tr v-for="building in filtered" :key="building.id">
              <td class="entity-cell"><strong>{{ building.name }}</strong><small>{{ building.id }}</small></td>
              <td>{{ building.address }}</td>
              <td class="numeric">{{ formatArea(building.landArea) }}</td>
              <td>{{ building.developer || '—' }}</td>
              <td><span class="status-badge status-on-sale">{{ houseStats(building.id).onSale }} 套在售</span></td>
              <td><div class="row-actions"><button class="button button-small" @click="detail = building">查看</button><button class="button button-small" @click="openEdit(building)"><Pencil :size="12" /></button></div></td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <section v-else class="panel empty-state">
      <div><div class="empty-blueprint" /><h3>没有匹配的楼盘</h3><p>当前关键词未命中任何资产档案，可以清除搜索或新建一个楼盘。</p><button class="button" @click="search = ''">清除搜索</button></div>
    </section>

    <div v-if="editorOpen" class="drawer-backdrop" @click.self="editorOpen = false">
      <aside class="drawer" aria-label="楼盘编辑抽屉">
        <header class="drawer-header">
          <div><span class="eyebrow">BUILDING PROFILE</span><h2>{{ editingId ? '编辑楼盘' : '建立楼盘档案' }}</h2><p>编号由后端 IdGenerator 生成，数据写入 MySQL。</p></div>
          <button class="icon-button" aria-label="关闭" @click="editorOpen = false"><X :size="18" /></button>
        </header>
        <div class="form-grid">
          <div class="field is-full"><label>楼盘名称 <em>*</em></label><input v-model="draft.name" placeholder="例如：澄湾序" /></div>
          <div class="field"><label>占地面积 <em>*</em></label><input v-model.number="draft.landArea" type="number" min="0" step="100" /><span class="help">单位：平方米</span></div>
          <div class="field"><label>开发商</label><input v-model="draft.developer" placeholder="可选" /></div>
          <div class="field is-full"><label>地址 <em>*</em></label><input v-model="draft.address" placeholder="项目详细地址" /></div>
          <div class="field is-full"><label>项目备注</label><textarea v-model="draft.remark" placeholder="记录项目定位、景观或资产备注" /></div>
        </div>
        <p v-if="error" class="form-error">{{ error }}</p>
        <footer class="drawer-footer"><button class="button" @click="editorOpen = false">取消</button><button class="button button-primary" :disabled="saving" @click="save">{{ saving ? '保存中…' : '保存楼盘' }}</button></footer>
      </aside>
    </div>

    <div v-if="detail" class="drawer-backdrop" @click.self="detail = null">
      <aside class="drawer">
        <header class="drawer-header"><div><span class="eyebrow">ASSET DOSSIER</span><h2>楼盘资产档案</h2></div><button class="icon-button" aria-label="关闭" @click="detail = null"><X :size="18" /></button></header>
        <div class="detail-hero">
          <span class="entity-id">{{ detail.id }}</span>
          <h2>{{ detail.name }}</h2>
          <p>{{ detail.address }}</p>
        </div>
        <div class="detail-facts">
          <div class="detail-fact"><span>占地面积</span><strong>{{ formatArea(detail.landArea) }}</strong></div>
          <div class="detail-fact"><span>开发商</span><strong>{{ detail.developer || '未填写' }}</strong></div>
          <div class="detail-fact"><span>在售库存</span><strong>{{ houseStats(detail.id).onSale }} 套</strong></div>
          <div class="detail-fact"><span>已售资产</span><strong>{{ houseStats(detail.id).sold }} 套</strong></div>
        </div>
        <div class="asset-note"><span>项目备注</span><p>{{ detail.remark || '暂无项目备注。' }}</p></div>
        <footer class="drawer-footer">
          <button class="button button-danger" :disabled="houseStats(detail.id).total > 0" @click="deleteTarget = detail; detail = null"><Trash2 :size="14" /> 删除</button>
          <button class="button button-primary" @click="openEdit(detail)"><Pencil :size="14" /> 编辑档案</button>
        </footer>
        <p v-if="houseStats(detail.id).total > 0" class="constraint-note"><Building2 :size="14" /> 该楼盘关联 {{ houseStats(detail.id).total }} 套房屋，因此删除动作已锁定。</p>
      </aside>
    </div>

    <div v-if="deleteTarget" class="modal-backdrop" @click.self="deleteTarget = null">
      <div class="modal-card"><h3>删除楼盘档案？</h3><p>将删除“{{ deleteTarget.name }}（{{ deleteTarget.id }}）”。只有没有关联房屋的楼盘可以删除，此操作写入 MySQL。</p><div class="modal-actions"><button class="button" @click="deleteTarget = null">取消</button><button class="button button-danger" @click="confirmDelete">确认删除</button></div></div>
    </div>
  </div>
</template>
