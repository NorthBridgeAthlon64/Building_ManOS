<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowRight, BadgeCheck, Building2, Check, CircleDollarSign, LockKeyhole, Percent, ReceiptText, ShieldCheck, UserRound } from '@lucide/vue'
import { mockStore } from '../store/mockStore'
import type { DiscountType, SaleRecord } from '../types'

const route = useRoute()
const router = useRouter()
const state = mockStore.state
const availableHouses = computed(() => state.houses.filter((house) => house.status === 'ON_SALE'))
const initialId = String(route.query.houseId ?? availableHouses.value[0]?.id ?? '')
const selectedHouseId = ref(initialId)
const strategy = ref<DiscountType>('PERCENTAGE')
const customerName = ref('')
const confirmOpen = ref(false)
const receipt = ref<SaleRecord | null>(null)
const error = ref('')

const selectedHouse = computed(() => mockStore.houseById(selectedHouseId.value))
const selectedBuilding = computed(() => selectedHouse.value ? mockStore.buildingById(selectedHouse.value.buildingId) : undefined)
const preview = computed(() => selectedHouse.value ? mockStore.discountPreview(selectedHouse.value.totalPrice, strategy.value) : null)
const percentagePreview = computed(() => selectedHouse.value ? mockStore.discountPreview(selectedHouse.value.totalPrice, 'PERCENTAGE') : null)
const thresholdPreview = computed(() => selectedHouse.value ? mockStore.discountPreview(selectedHouse.value.totalPrice, 'THRESHOLD') : null)

function money(value: number, compact = false) {
  if (compact) return `${(value / 10_000).toLocaleString('zh-CN', { maximumFractionDigits: 2 })} 万`
  return `¥${value.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
}

function submit() {
  error.value = ''
  if (!selectedHouse.value || !preview.value) {
    error.value = '请先选择一套在售房屋。'
    return
  }
  if (!customerName.value.trim()) {
    error.value = '请输入客户姓名后再确认成交。'
    confirmOpen.value = false
    return
  }
  try {
    receipt.value = mockStore.purchase(selectedHouse.value.id, strategy.value, customerName.value)
    confirmOpen.value = false
  } catch (cause) {
    error.value = cause instanceof Error ? cause.message : '成交失败'
    confirmOpen.value = false
  }
}

function anotherTransaction() {
  receipt.value = null
  customerName.value = ''
  strategy.value = 'PERCENTAGE'
  selectedHouseId.value = availableHouses.value[0]?.id ?? ''
}
</script>

<template>
  <div>
    <section class="page-intro">
      <div><span class="eyebrow">TRANSACTION DESK</span><h2>完成一笔可信成交</h2><p>资产核对、优惠比较、客户确认和成交回执在同一个工作台内完成，所有操作仅作用于 Mock 数据。</p></div>
      <span class="status-badge status-mock"><LockKeyhole :size="12" /> MOCK TRANSACTION</span>
    </section>

    <section v-if="receipt" class="transaction-success panel">
      <div class="success-architecture" aria-hidden="true"><i /><i /><i /></div>
      <BadgeCheck :size="46" :stroke-width="1.4" />
      <span class="eyebrow">TRANSACTION ARCHIVED</span>
      <h2>模拟成交已归档</h2>
      <p>{{ selectedBuilding?.name }} · {{ selectedHouse?.buildingNo }}栋 {{ selectedHouse?.roomNo }} 已更新为“已售”，并生成一份本地成交票据。</p>
      <div class="receipt-preview">
        <span><small>成交编号</small><strong>{{ receipt.id }}</strong></span>
        <span><small>客户</small><strong>{{ receipt.customerName }}</strong></span>
        <span><small>最终实付</small><strong class="money">{{ money(receipt.finalPrice) }}</strong></span>
      </div>
      <div class="transaction-success-actions"><button class="button" @click="anotherTransaction">继续模拟成交</button><button class="button button-primary" @click="router.push({ path: '/sales', query: { houseId: receipt.houseId } })"><ReceiptText :size="15" /> 查看成交票据</button></div>
    </section>

    <section v-else-if="selectedHouse" class="transaction-layout">
      <div class="transaction-main">
        <div class="transaction-steps">
          <span class="is-active"><i>01</i><strong>确认资产</strong></span><b /><span class="is-active"><i>02</i><strong>选择优惠</strong></span><b /><span><i>03</i><strong>归档成交</strong></span>
        </div>

        <article class="panel transaction-section">
          <div class="transaction-section-title"><span>01</span><div><h3>选择并核对在售资产</h3><p>已售房屋不会出现在可选列表中。</p></div></div>
          <label class="asset-selector"><span>在售房源</span><select v-model="selectedHouseId"><option v-for="house in availableHouses" :key="house.id" :value="house.id">{{ mockStore.buildingById(house.buildingId)?.name }} · {{ house.buildingNo }}栋 {{ house.roomNo }} · {{ money(house.totalPrice, true) }}</option></select></label>
          <div class="selected-asset-card">
            <div class="selected-asset-mark"><Building2 :size="26" /></div>
            <div><small>{{ selectedHouse.id }}</small><h3>{{ selectedBuilding?.name }}</h3><p>{{ selectedHouse.buildingNo }}栋 · {{ selectedHouse.roomNo }} · {{ selectedHouse.area.toFixed(2) }} ㎡</p></div>
            <div class="selected-asset-price"><small>原始总价</small><strong>{{ money(selectedHouse.totalPrice, true) }}</strong></div>
          </div>
        </article>

        <article class="panel transaction-section">
          <div class="transaction-section-title"><span>02</span><div><h3>比较折扣策略</h3><p>系统按原价档位自动选择对应优惠参数。</p></div></div>
          <div class="strategy-grid">
            <button class="strategy-card" :class="{ 'is-selected': strategy === 'PERCENTAGE' }" @click="strategy = 'PERCENTAGE'">
              <span class="strategy-icon"><Percent :size="22" /></span><i v-if="strategy === 'PERCENTAGE'"><Check :size="13" /></i>
              <small>PERCENTAGE</small><h3>比例折扣</h3><p>{{ percentagePreview?.tierLabel }}档 · {{ percentagePreview?.formula }}</p><strong>{{ money(percentagePreview?.finalPrice ?? 0, true) }}</strong><em>节省 {{ money(percentagePreview?.saving ?? 0, true) }}</em>
            </button>
            <button class="strategy-card" :class="{ 'is-selected': strategy === 'THRESHOLD' }" @click="strategy = 'THRESHOLD'">
              <span class="strategy-icon"><CircleDollarSign :size="22" /></span><i v-if="strategy === 'THRESHOLD'"><Check :size="13" /></i>
              <small>THRESHOLD</small><h3>满额减</h3><p>{{ thresholdPreview?.tierLabel }}档 · {{ thresholdPreview?.formula }}</p><strong>{{ money(thresholdPreview?.finalPrice ?? 0, true) }}</strong><em>节省 {{ money(thresholdPreview?.saving ?? 0, true) }}</em>
            </button>
          </div>
          <div class="tier-scale">
            <span :class="{ active: selectedHouse.totalPrice < 1_000_000 }"><i>Ⅰ</i><strong>100 万以下</strong><small>原价档</small></span>
            <b />
            <span :class="{ active: selectedHouse.totalPrice >= 1_000_000 && selectedHouse.totalPrice < 3_000_000 }"><i>Ⅱ</i><strong>100–300 万</strong><small>改善档</small></span>
            <b />
            <span :class="{ active: selectedHouse.totalPrice >= 3_000_000 }"><i>Ⅲ</i><strong>300 万及以上</strong><small>高端档</small></span>
          </div>
        </article>

        <article class="panel transaction-section">
          <div class="transaction-section-title"><span>03</span><div><h3>填写客户并确认</h3><p>客户姓名将出现在不可编辑的模拟成交票据上。</p></div></div>
          <label class="customer-field"><UserRound :size="18" /><span><small>客户姓名</small><input v-model="customerName" maxlength="50" placeholder="输入成交客户姓名" /></span><em>{{ customerName.length }}/50</em></label>
          <p v-if="error" class="form-error">{{ error }}</p>
        </article>
      </div>

      <aside class="settlement-panel">
        <span class="eyebrow">SETTLEMENT SUMMARY</span><h3>价格结算</h3>
        <div class="settlement-building"><small>{{ selectedBuilding?.name }}</small><strong>{{ selectedHouse.buildingNo }}栋 {{ selectedHouse.roomNo }}</strong><span>{{ selectedHouse.area.toFixed(2) }} ㎡</span></div>
        <dl><div><dt>房屋原价</dt><dd>{{ money(selectedHouse.totalPrice) }}</dd></div><div><dt>{{ strategy === 'PERCENTAGE' ? '比例优惠' : '满额减免' }}</dt><dd class="saving">− {{ money(preview?.saving ?? 0) }}</dd></div></dl>
        <div class="final-price"><small>最终实付</small><strong>{{ money(preview?.finalPrice ?? 0) }}</strong><span>{{ preview?.formula }}</span></div>
        <div class="settlement-guard"><ShieldCheck :size="18" /><p><strong>成交保护</strong><span>确认后房屋状态将锁定为已售，并生成唯一票据。</span></p></div>
        <button class="button button-accent settlement-button" :disabled="!customerName.trim()" @click="confirmOpen = true">确认成交 {{ money(preview?.finalPrice ?? 0, true) }} <ArrowRight :size="16" /></button>
      </aside>
    </section>

    <section v-else class="panel empty-state"><div><div class="empty-blueprint" /><h3>没有可成交的房屋</h3><p>当前 Mock 数据中不存在在售资产，可以先到房源中心新增一套房屋。</p><button class="button button-primary" @click="router.push({ path: '/houses', query: { create: '1' } })">新增房屋</button></div></section>

    <div v-if="confirmOpen && selectedHouse && preview" class="modal-backdrop" @click.self="confirmOpen = false">
      <div class="modal-card transaction-confirm"><span class="eyebrow">FINAL CONFIRMATION</span><h3>确认归档这笔成交？</h3><p>“{{ selectedBuilding?.name }} · {{ selectedHouse.buildingNo }}栋 {{ selectedHouse.roomNo }}”将变更为已售，客户为“{{ customerName }}”，模拟实付金额为：</p><strong class="money-large">{{ money(preview.finalPrice) }}</strong><p class="irreversible-note"><LockKeyhole :size="14" /> 成交记录在界面中不可编辑或删除。</p><div class="modal-actions"><button class="button" @click="confirmOpen = false">返回核对</button><button class="button button-accent" @click="submit">确认成交</button></div></div>
    </div>
  </div>
</template>
