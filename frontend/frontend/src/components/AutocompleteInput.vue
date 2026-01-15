<template>
  <div class="autocomplete-container" ref="containerRef">
    <input
      ref="inputRef"
      :value="modelValue"
      @input="handleInput"
      @focus="handleFocus"
      @blur="handleBlur"
      :placeholder="placeholder"
      :disabled="disabled"
      class="autocomplete-input"
      type="text"
    />
    <div v-if="loading" class="loading-indicator"></div>
    <ul v-show="showSuggestions && suggestions.length > 0" class="suggestions-list">
      <li
        v-for="(item, index) in suggestions"
        :key="index"
        @mousedown.prevent="handleSelect(item)"
        class="suggestion-item"
      >
        {{ item }}
      </li>
    </ul>
  </div>
</template>

<script>
import { ref, watch, onMounted, onUnmounted } from 'vue'

export default {
  name: 'AutocompleteInput',
  props: {
    modelValue: {
      type: [String, Number],
      default: ''
    },
    placeholder: {
      type: String,
      default: ''
    },
    disabled: {
      type: Boolean,
      default: false
    },
    fetchSuggestions: {
      type: Function,
      required: true
    }
  },
  emits: ['update:modelValue', 'select'],
  setup(props, { emit }) {
    const containerRef = ref(null)
    const inputRef = ref(null)
    const suggestions = ref([])
    const showSuggestions = ref(false)
    const loading = ref(false)
    let debounceTimer = null

    const handleInput = (e) => {
      const value = e.target.value
      emit('update:modelValue', value)
      querySuggestions(value)
    }

    const handleFocus = () => {
      // Activate suggestions on focus
      querySuggestions(props.modelValue)
    }

    const handleBlur = () => {
      // Delay hiding to allow click event on list item to fire
      setTimeout(() => {
        showSuggestions.value = false
      }, 200)
    }

    const handleSelect = (item) => {
      emit('update:modelValue', item)
      emit('select', item)
      showSuggestions.value = false
    }

    const querySuggestions = (queryString) => {
      loading.value = true
      
      // Call the fetch function provided by parent
      // We expect it to return a Promise or call a callback
      // Here we assume it returns a Promise or data directly
      // If the user wants to match Element Plus exactly: (queryString, cb)
      // Let's support both or just async
      
      if (debounceTimer) clearTimeout(debounceTimer)
      
      debounceTimer = setTimeout(async () => {
        try {
          // Convert to string for query if it's number
          const q = queryString === null || queryString === undefined ? '' : String(queryString)
          
          // We pass a callback to match the user's example pattern roughly, 
          // or just await the result if it returns a promise.
          // Let's assume the parent passes an async function that returns an array.
          const results = await props.fetchSuggestions(q)
          suggestions.value = results || []
          showSuggestions.value = true
        } catch (error) {
          console.error('Fetch suggestions error:', error)
          suggestions.value = []
        } finally {
          loading.value = false
        }
      }, 300)
    }

    // Close on click outside
    const handleClickOutside = (e) => {
      if (containerRef.value && !containerRef.value.contains(e.target)) {
        showSuggestions.value = false
      }
    }

    onMounted(() => {
      document.addEventListener('click', handleClickOutside)
    })

    onUnmounted(() => {
      document.removeEventListener('click', handleClickOutside)
    })

    return {
      containerRef,
      inputRef,
      suggestions,
      showSuggestions,
      loading,
      handleInput,
      handleFocus,
      handleBlur,
      handleSelect
    }
  }
}
</script>

<style scoped>
.autocomplete-container {
  position: relative;
  width: 100%;
}

.autocomplete-input {
  width: 100%;
  padding: 6px 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 13px;
  box-sizing: border-box;
  height: 32px;
}

.autocomplete-input:focus {
  outline: none;
  border-color: #1890ff;
}

.suggestions-list {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-top: 5px;
  padding: 0;
  list-style: none;
  z-index: 1000;
  max-height: 200px;
  overflow-y: auto;
}

.suggestion-item {
  padding: 8px 10px;
  font-size: 13px;
  color: #606266;
  cursor: pointer;
  line-height: 1.5;
}

.suggestion-item:hover {
  background-color: #f5f7fa;
}

.loading-indicator {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  width: 14px;
  height: 14px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #1890ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  pointer-events: none;
}

@keyframes spin {
  0% { transform: translateY(-50%) rotate(0deg); }
  100% { transform: translateY(-50%) rotate(360deg); }
}
</style>
