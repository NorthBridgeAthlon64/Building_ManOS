<script setup lang="ts">
import { ref } from 'vue'
import { Braces, CheckCircle2, Database, HardDrive, Layers3, MonitorCog, RefreshCw, ShieldCheck, Sparkles } from '@lucide/vue'
import { mockStore } from '../store/mockStore'

const resetDone = ref(false)

function resetData() {
  mockStore.resetMockData()
  resetDone.value = true
  window.setTimeout(() => { resetDone.value = false }, 2600)
}
</script>

<template>
  <div>
    <section class="page-intro">
      <div><span class="eyebrow">SYSTEM STATUS</span><h2>前端原型运行状态</h2><p>本页面明确展示 P0 的数据边界：当前原型独立运行，不访问 Java、HTTP API 或 MySQL。</p></div>
      <span class="status-badge status-mock"><Sparkles :size="12" /> MOCK MODE</span>
    </section>

    <section class="system-status-grid">
      <article class="system-status-card is-primary"><span><MonitorCog :size="20" /></span><small>FRONTEND</small><h3>Vue P0 原型</h3><p>本地组件与路由正常工作</p><i><CheckCircle2 :size="14" /> Ready</i></article>
      <article class="system-status-card"><span><Braces :size="20" /></span><small>DATA SOURCE</small><h3>In-memory Mock</h3><p>刷新页面后恢复种子数据</p><i><CheckCircle2 :size="14" /> Isolated</i></article>
      <article class="system-status-card"><span><Database :size="20" /></span><small>DATABASE</small><h3>未连接 MySQL</h3><p>浏览器不持有数据库配置</p><i class="is-neutral"><ShieldCheck :size="14" /> Safe</i></article>
      <article class="system-status-card"><span><HardDrive :size="20" /></span><small>PERSISTENCE</small><h3>无持久化写入</h3><p>所有操作只存在于当前标签页</p><i class="is-neutral"><ShieldCheck :size="14" /> Ephemeral</i></article>
    </section>

    <section class="system-layout">
      <article class="panel architecture-panel">
        <div class="panel-header"><div><h3>P0 运行边界</h3><p>当前仅激活表现层，后端链路全部处于断开状态</p></div><span class="status-badge status-mock">CONCEPT ONLY</span></div>
        <div class="architecture-stack">
          <div class="stack-node is-live"><span><Layers3 :size="20" /></span><div><small>PRESENTATION</small><strong>Vue SPA / Mock Store</strong><p>页面、组件、筛选、表单与模拟成交</p></div><i>ACTIVE</i></div>
          <b><span>HTTP API</span></b>
          <div class="stack-node is-off"><span><Braces :size="20" /></span><div><small>APPLICATION</small><strong>Controller / Service</strong><p>P0 不发起任何网络调用</p></div><i>DISCONNECTED</i></div>
          <b><span>JDBC</span></b>
          <div class="stack-node is-off"><span><Database :size="20" /></span><div><small>PERSISTENCE</small><strong>DAO / MySQL</strong><p>真实数据库完全不受原型操作影响</p></div><i>DISCONNECTED</i></div>
        </div>
      </article>

      <article class="panel mock-control-panel">
        <div class="panel-header"><div><h3>Mock 数据控制</h3><p>用于反复演示完整交互状态</p></div></div>
        <div class="panel-body">
          <div class="mock-data-counts"><span><small>楼盘</small><strong>{{ mockStore.state.buildings.length }}</strong></span><span><small>房屋</small><strong>{{ mockStore.state.houses.length }}</strong></span><span><small>成交</small><strong>{{ mockStore.state.sales.length }}</strong></span></div>
          <div class="reset-zone"><RefreshCw :size="24" /><h3>恢复初始演示数据</h3><p>清除本次浏览过程中新增、修改、删除和成交产生的内存变化。</p><button class="button button-primary" @click="resetData"><RefreshCw :size="14" /> 重置 Mock 数据</button></div>
          <p v-if="resetDone" class="success-note"><CheckCircle2 :size="14" /> Mock 数据已恢复到初始状态。</p>
        </div>
      </article>
    </section>

    <section class="panel p0-checklist">
      <div class="panel-header"><div><h3>P0 概念覆盖</h3><p>每个模块已具备对应界面和本地交互状态</p></div></div>
      <div class="checklist-grid">
        <span><CheckCircle2 :size="16" /><strong>资产总览</strong><small>指标、Three.js 城市、最近成交</small></span>
        <span><CheckCircle2 :size="16" /><strong>楼盘资产</strong><small>列表、详情、表单、删除约束</small></span>
        <span><CheckCircle2 :size="16" /><strong>房源中心</strong><small>五类筛选、状态动作、价格预览</small></span>
        <span><CheckCircle2 :size="16" /><strong>成交工作台</strong><small>优惠比较、确认、回执</small></span>
        <span><CheckCircle2 :size="16" /><strong>成交账本</strong><small>摘要、检索、只读票据</small></span>
        <span><CheckCircle2 :size="16" /><strong>系统状态</strong><small>Mock 边界与重置控制</small></span>
      </div>
    </section>
  </div>
</template>
