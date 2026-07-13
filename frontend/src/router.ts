import { createRouter, createWebHistory } from 'vue-router'
const DashboardView = () => import('./views/ModernDashboardView.vue')
import BuildingsView from './views/BuildingsView.vue'
import HousesView from './views/HousesView.vue'
import TransactionView from './views/TransactionView.vue'
import SalesView from './views/SalesView.vue'
import SystemView from './views/SystemView.vue'

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/dashboard' },
    { path: '/dashboard', component: DashboardView, meta: { title: '资产总览', eyebrow: 'PORTFOLIO OVERVIEW' } },
    { path: '/buildings', component: BuildingsView, meta: { title: '楼盘资产', eyebrow: 'BUILDING PORTFOLIO' } },
    { path: '/houses', component: HousesView, meta: { title: '房源中心', eyebrow: 'INVENTORY CENTER' } },
    { path: '/transactions/new', component: TransactionView, meta: { title: '成交工作台', eyebrow: 'TRANSACTION DESK' } },
    { path: '/sales', component: SalesView, meta: { title: '成交记录', eyebrow: 'SALES LEDGER' } },
    { path: '/system', component: SystemView, meta: { title: '系统状态', eyebrow: 'SYSTEM STATUS' } },
    { path: '/:pathMatch(.*)*', redirect: '/dashboard' },
  ],
})
