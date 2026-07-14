<script setup lang="ts">
import { computed, ref } from 'vue'
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import ArchitecturalBackdrop from './components/ArchitecturalBackdrop.vue'
import {
  Building2,
  ChevronDown,
  Database,
  HandCoins,
  House,
  LayoutDashboard,
  Menu,
  Plus,
  ReceiptText,
  Search,
  X,
} from '@lucide/vue'

const route = useRoute()
const router = useRouter()
const sidebarOpen = ref(false)
const quickOpen = ref(false)
const globalSearch = ref('')

const navItems = [
  { to: '/dashboard', label: '资产总览', caption: 'Overview', icon: LayoutDashboard },
  { to: '/buildings', label: '楼盘资产', caption: 'Buildings', icon: Building2 },
  { to: '/houses', label: '房源中心', caption: 'Inventory', icon: House },
  { to: '/transactions/new', label: '成交工作台', caption: 'Transaction', icon: HandCoins },
  { to: '/sales', label: '成交记录', caption: 'Ledger', icon: ReceiptText },
]

const pageTitle = computed(() => String(route.meta.title ?? 'Building ManOS'))
const pageEyebrow = computed(() => String(route.meta.eyebrow ?? 'URBAN LEDGER'))

function closeSidebar() {
  sidebarOpen.value = false
}

function submitSearch() {
  const keyword = globalSearch.value.trim()
  router.push({ path: '/houses', query: keyword ? { keyword } : {} })
}

function quickCreate(target: 'building' | 'house') {
  quickOpen.value = false
  router.push({ path: target === 'building' ? '/buildings' : '/houses', query: { create: '1' } })
}
</script>

<template>
  <div class="app-frame">
    <button
      v-if="sidebarOpen"
      class="sidebar-scrim"
      aria-label="关闭导航"
      @click="sidebarOpen = false"
    />

    <aside class="app-sidebar" :class="{ 'is-open': sidebarOpen }">
      <div class="brand-lockup">
        <div class="brand-mark" aria-hidden="true">
          <span />
          <span />
        </div>
        <div>
          <strong>Building ManOS</strong>
          <small>城市资产中枢</small>
        </div>
        <button class="icon-button sidebar-close" aria-label="关闭导航" @click="sidebarOpen = false">
          <X :size="18" />
        </button>
      </div>

      <div class="portfolio-stamp">
        <span>PORTFOLIO / 2026</span>
        <strong>04</strong>
        <small>在管楼盘</small>
      </div>

      <nav class="primary-nav" aria-label="主导航">
        <RouterLink
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          class="nav-item"
          @click="closeSidebar"
        >
          <component :is="item.icon" :size="19" :stroke-width="1.8" />
          <span>
            <strong>{{ item.label }}</strong>
            <small>{{ item.caption }}</small>
          </span>
        </RouterLink>
      </nav>

      <div class="sidebar-footer">
        <RouterLink to="/system" class="system-link" @click="closeSidebar">
          <Database :size="18" />
          <span>
            <strong>系统状态</strong>
            <small>API / MySQL 联调</small>
          </span>
          <i />
        </RouterLink>
        <p>BUILDING ASSET OPERATING SYSTEM</p>
      </div>
    </aside>

    <section class="workspace">
      <ArchitecturalBackdrop />
      <header class="top-command-bar">
        <button class="icon-button mobile-menu" aria-label="打开导航" @click="sidebarOpen = true">
          <Menu :size="20" />
        </button>

        <div class="page-identity">
          <span>{{ pageEyebrow }}</span>
          <h1>{{ pageTitle }}</h1>
        </div>

        <form class="global-search" role="search" @submit.prevent="submitSearch">
          <Search :size="18" />
          <input v-model="globalSearch" aria-label="全局房源搜索" placeholder="搜索楼盘、楼号或房号" />
          <kbd>↵</kbd>
        </form>

        <div class="quick-create">
          <button class="button button-primary" @click="quickOpen = !quickOpen">
            <Plus :size="17" />
            快捷创建
            <ChevronDown :size="15" />
          </button>
          <div v-if="quickOpen" class="quick-menu">
            <button @click="quickCreate('building')">
              <Building2 :size="17" />
              <span><strong>新增楼盘</strong><small>建立新的资产项目</small></span>
            </button>
            <button @click="quickCreate('house')">
              <House :size="17" />
              <span><strong>新增房屋</strong><small>录入一套在售房源</small></span>
            </button>
          </div>
        </div>
      </header>

      <main class="page-shell">
        <RouterView />
      </main>
    </section>
  </div>
</template>
