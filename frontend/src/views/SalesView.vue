<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { FileLock2, ReceiptText, Search, ShieldCheck, X } from '@lucide/vue'
import { mockStore } from '../store/mockStore'
import type { SaleRecord } from '../types'

const route = useRoute()
const state = mockStore.state
const search = ref(String(route.query.houseId ?? ''))
const detail = ref<SaleRecord | null>(null)

const filtered = computed(() => {
  const keyword = search.value.trim().toLowerCase()
  if (!keyword) return state.sales
  return state.sales.filter((sale) => {
    const house = mockStore.houseById(sale.houseId)
    const building = house ? mockStore.buildingById(house.buildingId) : undefined
    return [sale.id, sale.houseId, sale.customerName, building?.name ?? '', house?.buildingNo ?? '', house?.roomNo ?? ''].some((value) => value.toLowerCase().includes(keyword))
  })
})

const total = computed(() => filtered.value.reduce((sum, item) => sum + item.finalPrice, 0))
const average = computed(() => filtered.value.length ? total.value / filtered.value.length : 0)
const saving = computed(() => filtered.value.reduce((sum, item) => sum + item.originalPrice - item.finalPrice, 0))

function houseLabel(houseId: string) {
  const house = mockStore.houseById(houseId)
  const building = house ? mockStore.buildingById(house.buildingId) : undefined
  return house ? `${building?.name ?? '未知楼盘'} · ${house.buildingNo}栋 ${house.roomNo}` : houseId
}

function money(value: number, compact = false) {
  if (compact) return `${(value / 10_000).toLocaleString('zh-CN', { maximumFractionDigits: 2 })} 万`
  return `¥${value.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
}

function formatDate(value: string, detailed = false) {
  return new Intl.DateTimeFormat('zh-CN', detailed
    ? { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit' }
    : { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' }).format(new Date(value))
}

function discountLabel(sale: SaleRecord) {
  return sale.discountType === 'PERCENTAGE' ? `比例 ${(sale.discountValue * 100).toFixed(0)}%` : `满减 ${money(sale.discountValue, true)}`
}

onMounted(() => {
  const houseId = String(route.query.houseId ?? '')
  if (houseId) detail.value = state.sales.find((sale) => sale.houseId === houseId) ?? null
})
</script>

<template>
  <div>
    <section class="page-intro">
      <div><span class="eyebrow">SALES LEDGER</span><h2>不可变成交账本</h2><p>每套已售房屋对应唯一成交票据，以契约式信息层级呈现价格、优惠、客户与归档时间。</p></div>
      <span class="ledger-lock"><FileLock2 :size="16" /> 只读记录</span>
    </section>

    <section class="ledger-metrics">
      <article><span>成交笔数</span><strong>{{ filtered.length.toString().padStart(2, '0') }}</strong><small>TRANSACTIONS</small></article>
      <article><span>累计实付</span><strong>{{ money(total, true) }}</strong><small>CLOSED VOLUME</small></article>
      <article><span>平均成交</span><strong>{{ money(average, true) }}</strong><small>AVERAGE VALUE</small></article>
      <article><span>累计优惠</span><strong>{{ money(saving, true) }}</strong><small>CLIENT SAVING</small></article>
    </section>

    <section class="panel ledger-panel">
      <div class="panel-header ledger-header">
        <div><h3>成交明细</h3><p>按照模拟成交时间倒序归档</p></div>
        <label class="ledger-search"><Search :size="16" /><input v-model="search" placeholder="搜索成交号、房屋、客户" /></label>
      </div>
      <div v-if="filtered.length" class="data-table-wrap">
        <table class="data-table ledger-table">
          <thead><tr><th>成交票据</th><th>房屋资产</th><th>客户</th><th>优惠策略</th><th>归档时间</th><th class="numeric">最终实付</th><th /></tr></thead>
          <tbody>
            <tr v-for="sale in filtered" :key="sale.id" @click="detail = sale">
              <td class="entity-cell"><strong>{{ sale.id }}</strong><small>SEALED RECORD</small></td>
              <td>{{ houseLabel(sale.houseId) }}</td>
              <td>{{ sale.customerName }}</td>
              <td>{{ discountLabel(sale) }}</td>
              <td>{{ formatDate(sale.soldAt) }}</td>
              <td class="numeric money"><strong>{{ money(sale.finalPrice, true) }}</strong></td>
              <td><button class="button button-small" @click.stop="detail = sale">查看票据</button></td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-else class="empty-state"><div><div class="empty-blueprint" /><h3>没有匹配的成交记录</h3><p>当前查询未命中票据，可清除关键词查看完整账本。</p><button class="button" @click="search = ''">清除搜索</button></div></div>
    </section>

    <div v-if="detail" class="drawer-backdrop" @click.self="detail = null">
      <aside class="drawer sale-drawer">
        <header class="drawer-header"><div><span class="eyebrow">SEALED TRANSACTION</span><h2>成交票据</h2><p>Mock 数据中的只读归档记录。</p></div><button class="icon-button" aria-label="关闭" @click="detail = null"><X :size="18" /></button></header>
        <div class="sale-seal"><ReceiptText :size="28" /><span>BUILDING MANOS</span><strong>TRANSACTION SEALED</strong><small>{{ detail.id }}</small></div>
        <div class="receipt-asset"><span>成交资产</span><h3>{{ houseLabel(detail.houseId) }}</h3><p>{{ detail.houseId }} · {{ formatDate(detail.soldAt, true) }}</p></div>
        <dl class="receipt-prices">
          <div><dt>房屋原价</dt><dd>{{ money(detail.originalPrice) }}</dd></div>
          <div><dt>优惠策略</dt><dd>{{ discountLabel(detail) }}</dd></div>
          <div><dt>优惠金额</dt><dd class="saving">− {{ money(detail.originalPrice - detail.finalPrice) }}</dd></div>
          <div class="receipt-total"><dt>最终实付</dt><dd>{{ money(detail.finalPrice) }}</dd></div>
        </dl>
        <div class="receipt-customer"><span>成交客户</span><strong>{{ detail.customerName }}</strong><small>本票据仅用于前端概念演示</small></div>
        <p class="constraint-note"><ShieldCheck :size="14" /> 一房一成交：该记录无编辑与删除入口。</p>
      </aside>
    </div>
  </div>
</template>
