<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  ArrowRight,
  ArrowUpRight,
  Building2,
  CircleDollarSign,
  House,
  KeyRound,
  MousePointer2,
  Plus,
  Sparkles,
  TrendingUp,
} from '@lucide/vue'
import CityHouseScene from '../components/CityHouseSceneV2.vue'
import { dataStore } from '../store/dataStore'

const router = useRouter()
const state = dataStore.state
const loadError = ref('')

const onSaleCount = computed(() => state.houses.filter((house) => house.status === 'ON_SALE').length)
const soldCount = computed(() => state.houses.filter((house) => house.status === 'SOLD').length)
const salesTotal = computed(() => state.sales.reduce((sum, item) => sum + item.finalPrice, 0))
const recentSales = computed(() => state.sales.slice(0, 4))

const buildingSummary = computed(() =>
  state.buildings.map((building, index) => {
    const houses = dataStore.housesForBuilding(building.id)
    const onSale = houses.filter((house) => house.status === 'ON_SALE').length
    return {
      ...building,
      total: houses.length,
      onSale,
      sold: houses.length - onSale,
      tone: ['blue', 'coral', 'purple', 'cyan'][index % 4],
    }
  }),
)

function buildingName(id: string) {
  return dataStore.buildingById(id)?.name ?? '未知楼盘'
}

function houseLabel(houseId: string) {
  const house = dataStore.houseById(houseId)
  return house ? `${buildingName(house.buildingId)} · ${house.buildingNo}栋 ${house.roomNo}` : houseId
}

function formatMoney(value: number) {
  return `${(value / 10_000).toLocaleString('zh-CN', { maximumFractionDigits: 2 })} 万`
}

function formatDate(value: string) {
  return new Intl.DateTimeFormat('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(value))
}

onMounted(async () => {
  try {
    if (!state.loaded) await dataStore.loadAll()
  } catch (cause) {
    loadError.value = cause instanceof Error ? cause.message : '加载失败'
  }
})
</script>

<template>
  <div class="modern-dashboard">
    <p v-if="loadError" class="form-error">{{ loadError }}</p>
    <section class="modern-hero">
      <div class="hero-copy">
        <div class="hero-kicker">
          <span class="live-dot" />
          URBAN ASSET LIVEBOARD
        </div>
        <h2>把每一套房，<br><em>放回城市里看。</em></h2>
        <p>
          用更直观的空间视角连接楼盘、房源与成交状态。聚焦今天需要推进的资产，而不是被密集表格淹没。
        </p>
        <div class="hero-actions">
          <button class="button button-primary" @click="router.push({ path: '/houses', query: { create: '1' } })">
            <Plus :size="17" /> 录入房源
          </button>
          <button class="button button-ghost-modern" @click="router.push('/buildings')">
            查看城市资产 <ArrowUpRight :size="16" />
          </button>
        </div>
        <div class="hero-interaction-note">
          <MousePointer2 :size="15" />
          移动光标，探索城市体块
        </div>
      </div>

      <div class="hero-scene-wrap">
        <CityHouseScene />
        <div class="scene-chip scene-chip-top">
          <span>在管楼盘</span>
          <strong>{{ state.buildings.length.toString().padStart(2, '0') }}</strong>
        </div>
        <div class="scene-chip scene-chip-bottom">
          <Sparkles :size="14" />
          <span>Three.js 实时场景</span>
        </div>
        <div class="scene-legend">
          <span><i class="is-blue" /> 在售</span>
          <span><i class="is-coral" /> 已成交</span>
          <span><i class="is-cyan" /> 城市公共体块</span>
        </div>
      </div>
    </section>

    <section class="metric-grid modern-metric-grid">
      <article class="metric-card is-accent">
        <div class="metric-icon"><Building2 :size="19" /></div>
        <span>BUILDING PORTFOLIO</span>
        <strong>{{ state.buildings.length.toString().padStart(2, '0') }}<small>座楼盘</small></strong>
        <p>覆盖 {{ state.houses.length }} 套可管理资产</p>
      </article>
      <article class="metric-card">
        <div class="metric-icon is-cyan"><House :size="19" /></div>
        <span>AVAILABLE INVENTORY</span>
        <strong>{{ onSaleCount.toString().padStart(2, '0') }}<small>套在售</small></strong>
        <p>可以继续编辑或发起成交</p>
      </article>
      <article class="metric-card">
        <div class="metric-icon is-coral"><KeyRound :size="19" /></div>
        <span>CLOSED ASSETS</span>
        <strong>{{ soldCount.toString().padStart(2, '0') }}<small>套已售</small></strong>
        <p>已形成 {{ state.sales.length }} 份成交票据</p>
      </article>
      <article class="metric-card">
        <div class="metric-icon is-purple"><TrendingUp :size="19" /></div>
        <span>RECORDED VOLUME</span>
        <strong>{{ (salesTotal / 10_000).toFixed(0) }}<small>万元</small></strong>
        <p>Mock 账本累计实付金额</p>
      </article>
    </section>

    <section class="dashboard-grid modern-overview-grid">
      <article class="panel modern-portfolio-panel">
        <div class="panel-header">
          <div>
            <span class="panel-kicker">PORTFOLIO PULSE</span>
            <h3>楼盘库存分布</h3>
            <p>拉开层次，快速识别每个项目的可售压力</p>
          </div>
          <button class="button button-small" @click="router.push('/buildings')">
            全部楼盘 <ArrowRight :size="13" />
          </button>
        </div>

        <div class="project-pulse-list">
          <button
            v-for="item in buildingSummary"
            :key="item.id"
            :class="`tone-${item.tone}`"
            @click="router.push('/buildings')"
          >
            <span class="project-index">{{ item.id.slice(-2) }}</span>
            <span class="project-copy">
              <strong>{{ item.name }}</strong>
              <small>{{ item.address }}</small>
            </span>
            <span class="project-progress">
              <i :style="{ width: `${item.total ? (item.onSale / item.total) * 100 : 0}%` }" />
            </span>
            <span class="project-count"><strong>{{ item.onSale }}</strong><small>/ {{ item.total }} 在售</small></span>
            <ArrowUpRight :size="16" />
          </button>
        </div>
      </article>

      <article class="panel momentum-card">
        <span class="panel-kicker">THIS WEEK</span>
        <div class="momentum-orbit" aria-hidden="true">
          <span class="orbit-core"><TrendingUp :size="28" /></span>
          <i class="orbit-dot dot-one" />
          <i class="orbit-dot dot-two" />
        </div>
        <h3>资产流转保持活跃</h3>
        <p>成交与库存信息都来自浏览器内 Mock 数据，可放心体验完整前端流程。</p>
        <div class="momentum-facts">
          <span><strong>{{ state.sales.length }}</strong><small>成交记录</small></span>
          <span><strong>{{ Math.round((soldCount / state.houses.length) * 100) }}%</strong><small>去化比例</small></span>
        </div>
        <button class="button button-primary" @click="router.push('/transactions/new')">
          发起新成交 <ArrowRight :size="15" />
        </button>
      </article>
    </section>

    <section class="dashboard-lower-grid modern-lower-grid">
      <article class="panel">
        <div class="panel-header">
          <div>
            <span class="panel-kicker">LATEST DEALS</span>
            <h3>最近成交</h3>
            <p>按成交时间倒序展示的只读资产账本</p>
          </div>
          <button class="button button-small" @click="router.push('/sales')">
            进入账本 <ArrowRight :size="13" />
          </button>
        </div>
        <div class="data-table-wrap">
          <table class="data-table">
            <thead><tr><th>资产</th><th>客户</th><th>成交时间</th><th class="numeric">实付金额</th></tr></thead>
            <tbody>
              <tr v-for="sale in recentSales" :key="sale.id">
                <td class="entity-cell"><strong>{{ houseLabel(sale.houseId) }}</strong><small>{{ sale.id }}</small></td>
                <td>{{ sale.customerName }}</td>
                <td>{{ formatDate(sale.soldAt) }}</td>
                <td class="numeric money">{{ formatMoney(sale.finalPrice) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </article>

      <article class="panel quick-brief modern-quick-brief">
        <div class="panel-header">
          <div>
            <span class="panel-kicker">QUICK ROUTES</span>
            <h3>快捷工作流</h3>
            <p>一步进入最常用的资产任务</p>
          </div>
        </div>
        <div class="quick-brief-list">
          <button @click="router.push('/buildings')"><Building2 :size="19" /><span><strong>管理楼盘资产</strong><small>查看项目体量与房源</small></span><ArrowRight :size="15" /></button>
          <button @click="router.push('/houses')"><House :size="19" /><span><strong>检索在售房源</strong><small>按面积、价格与状态筛选</small></span><ArrowRight :size="15" /></button>
          <button @click="router.push('/transactions/new')"><KeyRound :size="19" /><span><strong>发起一笔成交</strong><small>比较折扣并生成模拟票据</small></span><ArrowRight :size="15" /></button>
          <button @click="router.push('/sales')"><CircleDollarSign :size="19" /><span><strong>核对成交账本</strong><small>查看金额、客户与时间</small></span><ArrowRight :size="15" /></button>
        </div>
      </article>
    </section>
  </div>
</template>
