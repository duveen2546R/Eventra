<template>
  <teleport to="body">
    <transition name="alert-fade">
      <div v-if="isVisible" :class="['alert-container', alertType]" @click="close">
        <div class="alert-content">
          <font-awesome-icon :icon="['fas', alertIcon]" class="alert-icon" />
          <p class="alert-message">{{ message }}</p>
          <button @click.stop="close" class="alert-close" aria-label="Close alert">
            <font-awesome-icon :icon="['fas', 'times']" />
          </button>
        </div>
      </div>
    </transition>
  </teleport>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { library } from '@fortawesome/fontawesome-svg-core';
import { fas } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';

library.add(fas);

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  message: {
    type: String,
    default: ''
  },
  type: {
    type: String,
    default: 'info', // 'success', 'error', 'info', 'warning'
    validator: (value) => ['success', 'error', 'info', 'warning'].includes(value)
  },
  duration: {
    type: Number,
    default: 3000 // Auto-close after 3 seconds
  },
  autoClose: {
    type: Boolean,
    default: true
  }
});

const emit = defineEmits(['update:modelValue', 'close']);

const isVisible = ref(props.modelValue);
let timeoutId = null;

const alertType = computed(() => props.type);

const alertIcon = computed(() => {
  const icons = {
    success: 'check-circle',
    error: 'exclamation-circle',
    info: 'info-circle',
    warning: 'exclamation-triangle'
  };
  return icons[props.type] || 'info-circle';
});

const close = () => {
  isVisible.value = false;
  emit('update:modelValue', false);
  emit('close');
  if (timeoutId) {
    clearTimeout(timeoutId);
    timeoutId = null;
  }
};

watch(() => props.modelValue, (newValue) => {
  isVisible.value = newValue;
  
  if (newValue && props.autoClose && props.duration > 0) {
    // Clear existing timeout
    if (timeoutId) {
      clearTimeout(timeoutId);
    }
    
    // Set new timeout
    timeoutId = setTimeout(() => {
      close();
    }, props.duration);
  }
});

watch(() => props.message, () => {
  // Reset timer when message changes
  if (isVisible.value && props.autoClose && props.duration > 0) {
    if (timeoutId) {
      clearTimeout(timeoutId);
    }
    timeoutId = setTimeout(() => {
      close();
    }, props.duration);
  }
});
</script>

<style scoped>
/* Alert Container Styles */
.alert-container {
  position: fixed;
  top: 2rem;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10000;
  padding: 0.75rem 1.5rem;
  border-radius: 0.75rem;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease-in-out;
  cursor: pointer;
  min-width: 300px;
  max-width: 500px;
}

.alert-container.success {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.alert-container.error {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.alert-container.info {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.alert-container.warning {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.alert-content {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  width: 100%;
}

.alert-icon {
  font-size: 1.25rem;
  flex-shrink: 0;
}

.alert-message {
  font-weight: 500;
  flex: 1;
  margin: 0;
}

.alert-close {
  background: none;
  border: none;
  color: inherit;
  font-size: 1rem;
  cursor: pointer;
  opacity: 0.8;
  padding: 0.25rem;
  transition: opacity 0.2s ease;
  flex-shrink: 0;
}

.alert-close:hover {
  opacity: 1;
}

/* Alert Fade Transition */
.alert-fade-enter-active,
.alert-fade-leave-active {
  transition: all 0.3s ease;
}

.alert-fade-enter-from {
  opacity: 0;
  transform: translate(-50%, -20px);
}

.alert-fade-leave-to {
  opacity: 0;
  transform: translate(-50%, -20px);
}

/* Responsive */
@media (max-width: 640px) {
  .alert-container {
    top: 1rem;
    left: 1rem;
    right: 1rem;
    transform: none;
    min-width: auto;
  }
  
  .alert-fade-enter-from,
  .alert-fade-leave-to {
    transform: translateY(-20px);
  }
}
</style>