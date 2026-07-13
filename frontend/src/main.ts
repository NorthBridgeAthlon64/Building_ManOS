import { createApp } from 'vue'
import App from './App.vue'
import { router } from './router'
import './styles.css'
import './module-styles.css'
import './modern-theme.css'
import './saturated-architectural-theme.css'
import './accessibility-polish.css'

createApp(App).use(router).mount('#app')
