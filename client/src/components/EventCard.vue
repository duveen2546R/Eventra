<template>
  <div class="backdrop-blur-lg bg-white/5 border border-purple-400/20 rounded-2xl p-6 shadow-lg hover:shadow-2xl transition-all duration-300 flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
    <div class="flex flex-col gap-2 flex-1">
      <div class="flex items-center gap-3 flex-wrap">
        <h3 class="text-xl font-semibold text-purple-300">{{ event.title }}</h3>
        <span v-if="status === 'ongoing'" class="px-2 py-1 text-xs font-bold bg-red-500/20 text-red-400 border border-red-500/30 rounded-full animate-pulse">
          🔴 LIVE
        </span>
        <span v-if="status === 'past'" class="px-2 py-1 text-xs font-bold bg-gray-500/20 text-gray-400 border border-gray-500/30 rounded-full">
          ENDED
        </span>
      </div>
      
      <p class="text-gray-400 text-sm max-w-md">{{ event.description }}</p>
      
      <div class="flex gap-4 md:gap-8 text-sm mt-2 flex-wrap">
        <p class="flex items-center gap-1">
          <font-awesome-icon :icon="['fas', 'calendar']" class="text-purple-400" /> 
          {{ formatDate(event.eventTimestamp) }}
        </p>
        <p class="flex items-center gap-1">
          <font-awesome-icon :icon="['fas', 'clock']" class="text-purple-400" /> 
          {{ formatTime(event.eventTimestamp) }}
        </p>
        <p class="flex items-center gap-1">
          <font-awesome-icon :icon="['fas', 'map-marker-alt']" class="text-purple-400" /> 
          {{ event.location }}
        </p>
        <p class="flex items-center gap-1">
          <font-awesome-icon :icon="['fas', 'tag']" class="text-purple-400" />
          <span class="text-purple-400 font-semibold">
            {{ event.amount && event.amount > 0 ? '₹' + event.amount : 'Free' }}
          </span>
        </p>
        <p v-if="event.capacity" class="flex items-center gap-1">
          <font-awesome-icon :icon="['fas', 'users']" class="text-purple-400" />
          <span class="text-gray-400">
            {{ event.remainingCapacity || 0 }} / {{ event.capacity }} available
          </span>
        </p>
      </div>
    </div>

    <div class="flex flex-col gap-2 items-end">
      <button
        v-if="status !== 'past'"
        @click="handleRegister"
        :disabled="!loggedIn || isFull"
        :class="[
          'px-6 py-2.5 rounded-full font-semibold text-white transition-all whitespace-nowrap',
          loggedIn && !isFull
            ? 'bg-gradient-to-r from-purple-500 to-pink-500 hover:scale-105 hover:shadow-xl' 
            : 'bg-gray-500/50 cursor-not-allowed opacity-60'
        ]"
      >
        <font-awesome-icon 
          v-if="!loggedIn" 
          :icon="['fas', 'lock']" 
          class="mr-2" 
        />
        <font-awesome-icon 
          v-else-if="isFull" 
          :icon="['fas', 'exclamation-circle']" 
          class="mr-2" 
        />
        {{ buttonText }}
      </button>
      <span v-else class="px-6 py-2.5 text-gray-500 font-semibold">
        Event Ended
      </span>
      
      <span v-if="isFull && status !== 'past'" class="text-xs text-red-400">
        Event Full
      </span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { library } from '@fortawesome/fontawesome-svg-core';
import { fas } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';

library.add(fas);

const props = defineProps({
  event: {
    type: Object,
    required: true
  },
  loggedIn: {
    type: Boolean,
    default: false
  },
  status: {
    type: String,
    default: 'upcoming',
    validator: (value) => ['ongoing', 'upcoming', 'past'].includes(value)
  }
});

const emit = defineEmits(['register']);

const isFull = computed(() => {
  return props.event.remainingCapacity !== undefined && props.event.remainingCapacity <= 0;
});

const buttonText = computed(() => {
  if (!props.loggedIn) return 'Sign In to Register';
  if (isFull.value) return 'Event Full';
  if (props.event.amount && props.event.amount > 0) return 'Register';
  return 'Join Free';
});

const handleRegister = () => {
  if (props.loggedIn && !isFull.value) {
    emit('register', props.event);
  }
};

const formatDate = (dateStr) => {
  if (!dateStr) return "TBA";
  const d = new Date(dateStr);
  return d.toLocaleDateString("en-IN", { 
    year: "numeric", 
    month: "short", 
    day: "numeric" 
  });
};

const formatTime = (dateStr) => {
  if (!dateStr) return "TBA";
  const d = new Date(dateStr);
  return d.toLocaleTimeString("en-IN", { 
    hour: "2-digit", 
    minute: "2-digit" 
  });
};
</script>

<style scoped>
/* Add any component-specific styles here */
</style>