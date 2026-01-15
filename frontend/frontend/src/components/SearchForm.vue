<template>
  <form @submit.prevent="handleSearch" class="search-card">
    
    <!-- 第一行：核心搜索区 (加大尺寸) -->
    <div class="primary-search-area">
      <div class="input-wrapper main-input">
        <input 
          v-model="keyword" 
          type="text" 
          placeholder="请输入课程号 或 课程名进行搜索..." 
          class="hero-input" 
        />
      </div>
      <button type="submit" :disabled="loading" class="btn-search-hero">
        {{ loading ? '查询中...' : '查询' }}
      </button>
    </div>

    <!-- 第二行：辅助筛选区 (宽度标准化) -->
    <div class="filter-bar">
      <!-- 教师 -->
      <div class="filter-cell">
        <AutocompleteInput
          v-model="query.instructorName"
          placeholder="教师姓名"
          :fetch-suggestions="createFetch('instructorName')"
          class="full-width"
        />
      </div>

      <!-- 学院 -->
      <div class="filter-cell">
        <AutocompleteInput
          v-model="query.college"
          placeholder="开课学院"
          :fetch-suggestions="createFetch('college')"
          class="full-width"
        />
      </div>

      <!-- 校区 -->
      <div class="filter-cell">
        <AutocompleteInput
          v-model="query.campus"
          placeholder="校区"
          :fetch-suggestions="createFetch('campus')"
          class="full-width"
        />
      </div>

      <!-- 学分 (稍微窄一点，但保持固定宽度) -->
      <div class="filter-cell short">
        <AutocompleteInput
          v-model="query.credits"
          placeholder="学分"
          :fetch-suggestions="createFetch('credits')"
          class="full-width"
        />
      </div>

      <!-- 时间 (胶囊样式，高度与输入框对齐) -->
      <div class="time-group">
        <select v-model="query.weekday" class="std-select no-border">
          <option value="">礼拜</option>
          <option v-for="d in ['周一','周二','周三','周四','周五','周六','周日']" :key="d" :value="d">{{d}}</option>
        </select>
        <span class="divider">|</span>
        
        <!-- 这里的 input 我加了 placeholder 即使不显示也为了语义 -->
        <input v-model.number="query.startPeriod" type="number" min="1" max="12" class="std-input tiny-input no-border" />
        
        <!-- [修改点]：去掉了空格，改用了 En Dash -->
        <span class="sep">-</span>
        
        <input v-model.number="query.endPeriod" type="number" min="1" max="12" class="std-input tiny-input no-border" />
        <span class="unit">节</span>
      </div>

      <!-- 重置按钮 -->
      <button type="button" @click="handleReset" class="btn-reset-text">
        重置筛选
      </button>
    </div>

  </form>
</template>

<script>
import { reactive, ref, onMounted } from 'vue'
import AutocompleteInput from './AutocompleteInput.vue'
import { getAttributeValues } from '../api/courseApi'

export default {
  name: 'SearchForm',
  components: { AutocompleteInput },
  props: {
    loading: { type: Boolean, default: false }
  },
  emits: ['search', 'reset'],
  setup(props, { emit }) {
    // 1. 定义一个临时变量 keyword 用于主搜索框
    const keyword = ref('')

    // 2. 这里的 query 只保留需要传给后端的字段
    const query = reactive({
      courseId: null, 
      courseName: '', 
      credits: null, 
      college: '', 
      instructorName: '', 
      campus: '', 
      // 移除了 classroom, startWeek, endWeek
      weekday: '', 
      startPeriod: null, 
      endPeriod: null
    })

    const buildCondition = (excludeField = null) => {
      const condition = {}
      Object.keys(query).forEach(key => {
        if (key !== excludeField) {
          const value = query[key]
          if (value !== null && value !== '' && value !== undefined) {
            condition[key] = value
          }
        }
      })
      return condition
    }

    const createFetch = (fieldName) => {
      return async (queryString) => {
        try {
          const condition = buildCondition(fieldName)
          if (queryString) condition[fieldName] = queryString
          const values = await getAttributeValues({ condition: condition, attributeName: fieldName })
          return values || []
        } catch (err) {
          console.error(`Fetch ${fieldName} error:`, err)
          return []
        }
      }
    }

    const handleSearch = () => {
      // --- 核心逻辑：智能解析 keyword ---
      // 重置真正的查询字段
      query.courseId = null
      query.courseName = ''

      if (keyword.value) {
        const trimmed = keyword.value.trim()
        // 如果是纯数字，认为是课程号
        if (/^\d+$/.test(trimmed)) {
          query.courseId = trimmed
        } else {
          // 否则认为是课程名
          query.courseName = trimmed
        }
      }

      // 发射查询事件
      emit('search', { ...query })
    }

    const handleReset = () => {
      // 清空 keyword
      keyword.value = ''
      
      // 清空 query
      Object.keys(query).forEach(key => {
        if (['courseId', 'credits', 'startPeriod', 'endPeriod'].includes(key)) {
          query[key] = null
          return
        }
        query[key] = ''
      })
      emit('reset')
    }

    onMounted(() => {
      handleSearch()
    })

    return { 
      keyword, 
      query, 
      createFetch, 
      handleSearch, 
      handleReset 
    }
  }
}
</script>

<style scoped>
/* ... 前面的布局样式保持不变 ... */
.search-card {
  background: white;
  padding: 24px 30px;
  border-radius: 16px;
  margin-bottom: 24px;
  box-shadow: 0 8px 24px rgba(124, 31, 137, 0.06);
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.primary-search-area { display: flex; gap: 16px; align-items: center; }
.main-input { flex: 1; position: relative; display: flex; align-items: center; }
.hero-input {
  width: 100%; height: 52px; padding: 0 20px; border: 2px solid #ebeef5; border-radius: 10px; font-size: 16px; background: #fdfdfd; transition: all 0.2s; color: #333;
}
.hero-input:focus { outline: none; border-color: #7C1F89; background: white; box-shadow: 0 0 0 4px rgba(124, 31, 137, 0.1); }
.hero-input::placeholder { color: #a8abb2; font-weight: 400; }
.btn-search-hero {
  height: 52px; padding: 0 40px; background: #7C1F89; color: white; border: none; border-radius: 10px; font-size: 16px; font-weight: 600; cursor: pointer; transition: all 0.2s; box-shadow: 0 6px 16px rgba(124, 31, 137, 0.25); white-space: nowrap;
}
.btn-search-hero:hover:not(:disabled) { background: #9027a0; transform: translateY(-2px); box-shadow: 0 8px 20px rgba(124, 31, 137, 0.35); }
.filter-bar { display: flex; align-items: center; flex-wrap: wrap; gap: 16px; padding-top: 4px; }
.filter-cell { width: 150px; flex-shrink: 0; }
.filter-cell.short { width: 100px; }
.full-width { width: 100%; }

/* --- [重点修改] 统一控件样式 --- */

/* 1. 只给“容器”和“原生输入框”加边框，不给内部 input 加 */
.std-input, 
.std-select,
:deep(.autocomplete-container) {
  height: 36px !important;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  font-size: 14px;
  padding: 0 10px;
  background: #fff;
  box-sizing: border-box;
  transition: all 0.2s;
  color: #333;
  width: 100%;
}

/* 2. 彻底清除组件内部 input 的所有样式 */
:deep(.autocomplete-input) { 
  border: none !important; 
  outline: none !important;
  height: 100% !important; 
  width: 100% !important;
  background: transparent !important; 
  padding: 0 !important; 
  margin: 0 !important;
  box-shadow: none !important; /* 关键：去掉阴影 */
  border-radius: 0 !important;
}

/* 3. 聚焦高亮逻辑优化 */
/* 当普通输入框聚焦，或者 容器内部(focus-within) 有焦点时，高亮外层 */
.std-input:focus, 
.std-select:focus,
:deep(.autocomplete-container):focus-within {
  outline: none;
  border-color: #7C1F89;
  box-shadow: 0 0 0 2px rgba(124, 31, 137, 0.1);
  background: #fff;
}

/* --- 下面的样式保持不变 --- */
.time-group { background: #f7f8fa; border-radius: 8px; padding: 0 10px; height: 36px; display: flex; align-items: center; gap: 4px; border: 1px solid #e0e0e0; }
.no-border { border: none !important; background: transparent !important; box-shadow: none !important; padding: 0 4px; }
.tiny-input { width: 36px !important; text-align: center; color: #333; display: flex; align-items: center; justify-content: center; }
.divider { color: #ddd; margin: 0 6px; }
.sep { color: #a8abb2; margin: 0 4px; font-size: 14px; transform: translateY(-1px); display: inline-block; }
.unit { font-size: 13px; color: #888; margin-left: 2px; }
.btn-reset-text { background: none; border: none; color: #909399; font-size: 14px; cursor: pointer; margin-left: auto; padding: 6px 12px; border-radius: 6px; transition: all 0.2s; }
.btn-reset-text:hover { color: #7C1F89; background: #f9f0ff; }
@media (max-width: 1000px) { .filter-cell, .filter-cell.short { width: auto; flex: 1 1 120px; } }
@media (max-width: 768px) { .primary-search-area { flex-direction: column; } .btn-search-hero { width: 100%; } .filter-bar { gap: 10px; } .filter-cell { flex: 1 1 45%; max-width: 48%; } .time-group { flex: 1 1 100%; max-width: 100%; order: 10; } .btn-reset-text { width: 100%; text-align: center; order: 11; margin-top: 8px;} }
</style>