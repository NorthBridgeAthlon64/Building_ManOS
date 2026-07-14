<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { Braces, CheckCircle2, Database, HardDrive, Layers3, MonitorCog, RefreshCw, ShieldCheck } from '@lucide/vue'
import { apiBaseUrl } from '../api/http'
import { healthApi, type HealthInfo } from '../api/health'
import { dataStore } from '../store/dataStore'

const health = ref<HealthInfo | null>(null)
const error = ref('')
const reloading = ref(false)
const reloadDone = ref(false)

async function refreshHealth() {
  try {
    health.value = await healthApi.check()
    error.value = ''
  } catch (cause) {
    health.value = null
    error.value = cause instanceof Error ? cause.message : '健康检查失败'
  }
}

async function reloadData() {
  reloading.value = true
  try {
    await dataStore.loadAll()
    await refreshHealth()
    reloadDone.value = true
    window.setTimeout(() => { reloadDone.value = false }, 2600)
  } catch (cause) {
    error.value = cause instanceof Error ? cause.message : '重新加载失败'
  } finally {
    reloading.value = false
  }
}

onMounted(async () => {
  await refreshHealth()
  if (!dataStore.state.loaded) {
    try {
      await dataStore.loadAll()
    } catch (cause) {
      error.value = cause instanceof Error ? cause.message : '加载失败'
    }
  }
})
</script>

<template>
  <div>
    <section class="page-intro">
      <div><span class="eyebrow">SYSTEM STATUS</span><h2>前后端联调状态</h2><p>前端通过 HTTP API 访问 Java 服务与 MySQL，不再使用内存 Mock。</p></div>
      <span class="status-badge" :class="health?.db === 'UP' ? 'status-on-sale' : 'status-mock'">
        <ShieldCheck :size="12" /> {{ health?.status ?? 'UNKNOWN' }}
      </span>
    </section>

    <p v-if="error" class="form-error">{{ error }}</p>

    <section class="system-status-grid">
      <article class="system-status-card is-primary"><span><MonitorCog :size="20" /></span><small>FRONTEND</small><h3>Vue SPA</h3><p>本地 Vite / 可穿透</p><i><CheckCircle2 :size="14" /> Ready</i></article>
      <article class="system-status-card"><span><Braces :size="20" /></span><small>API BASE</small><h3>{{ apiBaseUrl() }}</h3><p>VITE_API_BASE_URL</p><i><CheckCircle2 :size="14" /> Configured</i></article>
      <article class="system-status-card"><span><Database :size="20" /></span><small>DATABASE</small><h3>{{ health?.db ?? '—' }}</h3><p>JDBC → MySQL</p><i :class="health?.db === 'UP' ? '' : 'is-neutral'"><ShieldCheck :size="14" /> {{ health?.db === 'UP' ? 'Connected' : 'Check API' }}</i></article>
      <article class="system-status-card"><span><HardDrive :size="20" /></span><small>PERSISTENCE</small><h3>MySQL</h3><p>building_manos</p><i><CheckCircle2 :size="14" /> Durable</i></article>
    </section>

    <section class="system-layout">
      <article class="panel architecture-panel">
        <div class="panel-header"><div><h3>运行链路</h3><p>Vue → HTTP /api → service → dao → MySQL</p></div><span class="status-badge status-on-sale">LIVE</span></div>
        <div class="architecture-stack">
          <div class="stack-node is-live"><span><Layers3 :size="20" /></span><div><small>PRESENTATION</small><strong>Vue SPA</strong><p>页面、组件、筛选、表单与成交</p></div><i>ACTIVE</i></div>
          <b><span>HTTP API</span></b>
          <div class="stack-node is-live"><span><Braces :size="20" /></span><div><small>APPLICATION</small><strong>Javalin Controllers / Service</strong><p>{{ health?.service ?? 'Building ManOS API' }}</p></div><i>CONNECTED</i></div>
          <b><span>JDBC</span></b>
          <div class="stack-node" :class="health?.db === 'UP' ? 'is-live' : 'is-off'"><span><Database :size="20" /></span><div><small>PERSISTENCE</small><strong>DAO / MySQL</strong><p>真实数据库读写</p></div><i>{{ health?.db === 'UP' ? 'UP' : 'DOWN' }}</i></div>
        </div>
      </article>

      <article class="panel mock-control-panel">
        <div class="panel-header"><div><h3>数据控制</h3><p>从服务端重新拉取最新数据（不提供危险重置接口）</p></div></div>
        <div class="panel-body">
          <div class="mock-data-counts"><span><small>楼盘</small><strong>{{ dataStore.state.buildings.length }}</strong></span><span><small>房屋</small><strong>{{ dataStore.state.houses.length }}</strong></span><span><small>成交</small><strong>{{ dataStore.state.sales.length }}</strong></span></div>
          <div class="reset-zone"><RefreshCw :size="24" /><h3>重新加载服务端数据</h3><p>清除当前页面缓存状态，重新请求 /api/buildings、/houses、/sales。</p><button class="button button-primary" :disabled="reloading" @click="reloadData"><RefreshCw :size="14" /> {{ reloading ? '加载中…' : '重新加载' }}</button></div>
          <p v-if="reloadDone" class="success-note"><CheckCircle2 :size="14" /> 数据已从 API 重新加载。</p>
        </div>
      </article>
    </section>
  </div>
</template>
