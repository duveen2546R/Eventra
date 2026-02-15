<template>
  <div 
    class="backdrop-blur-lg bg-white/5 border border-purple-400/20 rounded-2xl p-6 shadow-lg hover:shadow-2xl transition-all duration-300 flex flex-col gap-4 cursor-pointer hover:border-purple-400/40 group"
    @click="viewEventDetails"
  >
    <!-- Main Event Info -->
    <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
      <div class="flex flex-col gap-2 flex-1">
        <div class="flex items-center gap-3 flex-wrap">
          <h3 class="text-xl font-semibold text-purple-300 group-hover:text-purple-200 transition-colors">{{ event.title }}</h3>
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
          @click.stop="handleRegister"
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

    <!-- Contact Information Section -->
    <div v-if="hasContactInfo" class="pt-4 border-t border-purple-400/10">
      <div class="flex items-center gap-2 mb-3">
        <font-awesome-icon :icon="['fas', 'address-book']" class="text-purple-400" />
        <h4 class="text-sm font-semibold text-purple-300">Contact Information</h4>
      </div>
      
      <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
        <!-- Phone Numbers -->
        <div v-if="event.phoneNo && event.phoneNo.length > 0" class="space-y-2">
          <div 
            v-for="(phone, index) in event.phoneNo" 
            :key="`phone-${index}`"
            class="flex items-center gap-2 text-sm"
          >
            <font-awesome-icon :icon="['fas', 'phone']" class="text-purple-400 text-xs" />
            <a 
              :href="`tel:${phone}`" 
              @click.stop
              class="text-gray-300 hover:text-purple-300 transition-colors"
            >
              {{ phone }}
            </a>
          </div>
        </div>

        <!-- Email Addresses -->
        <div v-if="event.email && event.email.length > 0" class="space-y-2">
          <div 
            v-for="(email, index) in event.email" 
            :key="`email-${index}`"
            class="flex items-center gap-2 text-sm"
          >
            <font-awesome-icon :icon="['fas', 'envelope']" class="text-purple-400 text-xs" />
            <a 
              :href="`mailto:${email}`" 
              @click.stop
              class="text-gray-300 hover:text-purple-300 transition-colors truncate"
            >
              {{ email }}
            </a>
          </div>
        </div>
      </div>
    </div>

    <!-- Brochure and QR Code Section -->
    <div v-if="event.brochureUrl || event.qrCodeUrl" class="flex flex-col md:flex-row gap-4 pt-4 border-t border-purple-400/10">
      <!-- Brochure -->
      <div v-if="event.brochureUrl" class="flex items-center gap-3 flex-1">
        <div class="p-3 bg-purple-500/10 border border-purple-400/30 rounded-lg">
          <font-awesome-icon 
            :icon="['fas', getBrochureIcon]" 
            class="text-purple-400 text-2xl" 
          />
        </div>
        <div class="flex-1">
          <p class="text-sm font-semibold text-gray-300">Event Brochure</p>
          <a 
            :href="event.brochureUrl" 
            target="_blank"
            @click.stop
            class="text-xs text-purple-400 hover:text-purple-300 flex items-center gap-1 mt-1 transition-colors"
          >
            <font-awesome-icon :icon="['fas', 'external-link-alt']" />
            View Document
          </a>
        </div>
      </div>

      <!-- QR Code -->
      <div v-if="event.qrCodeUrl" class="flex items-center gap-3 flex-1">
        <div class="relative group cursor-pointer" @click.stop="showQRModal = true">
          <img 
            :src="event.qrCodeUrl" 
            alt="Event QR Code" 
            class="w-16 h-16 rounded-lg border border-purple-400/30 hover:border-purple-400/60 transition-all"
          />
          <div class="absolute inset-0 bg-black/60 rounded-lg opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
            <font-awesome-icon :icon="['fas', 'expand']" class="text-white text-lg" />
          </div>
        </div>
        <div class="flex-1">
          <p class="text-sm font-semibold text-gray-300">Event QR Code</p>
          <button 
            @click.stop="showQRModal = true"
            class="text-xs text-purple-400 hover:text-purple-300 flex items-center gap-1 mt-1 transition-colors"
          >
            <font-awesome-icon :icon="['fas', 'qrcode']" />
            View Full Size
          </button>
        </div>
      </div>
    </div>

    <!-- View Details Button (visible on hover) -->
    <div class="opacity-0 group-hover:opacity-100 transition-opacity pt-2 border-t border-purple-400/10">
      <button 
        @click.stop="viewEventDetails"
        class="w-full px-4 py-2 bg-purple-500/20 hover:bg-purple-500/30 border border-purple-400/30 rounded-lg transition-all duration-300 text-purple-300 font-semibold text-sm flex items-center justify-center gap-2"
      >
        <font-awesome-icon :icon="['fas', 'eye']" />
        View Full Event Details
      </button>
    </div>

    <!-- QR Code Modal -->
    <teleport to="body">
      <transition name="modal-fade">
        <div v-if="showQRModal" class="qr-modal-overlay" @click="showQRModal = false">
          <div class="qr-modal-content" @click.stop>
            <div class="qr-modal-header">
              <h3 class="text-xl font-bold text-purple-300">{{ event.title }}</h3>
              <button @click="showQRModal = false" class="qr-close-btn">
                <font-awesome-icon :icon="['fas', 'times']" />
              </button>
            </div>
            
            <div class="qr-modal-body">
              <img 
                :src="event.qrCodeUrl" 
                :alt="`QR Code for ${event.title}`"
                class="qr-large-image"
              />
              <p class="text-sm text-gray-400 mt-4">Scan this QR code to access the event</p>
            </div>

            <div class="qr-modal-footer">
              <button @click="downloadQR" class="qr-download-btn">
                <font-awesome-icon :icon="['fas', 'download']" class="mr-2" />
                Download QR Code
              </button>
            </div>
          </div>
        </div>
      </transition>
    </teleport>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { library } from '@fortawesome/fontawesome-svg-core';
import { fas } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';

library.add(fas);

const router = useRouter();
console.log(event);
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

const showQRModal = ref(false);

const isFull = computed(() => {
  return props.event.remainingCapacity !== undefined && props.event.remainingCapacity <= 0;
});

const buttonText = computed(() => {
  if (!props.loggedIn) return 'Sign In to Register';
  if (isFull.value) return 'Event Full';
  if (props.event.amount && props.event.amount > 0) return 'Register';
  return 'Join Free';
});

const getBrochureIcon = computed(() => {
  if (!props.event.brochureUrl) return 'file';
  const url = props.event.brochureUrl.toLowerCase();
  if (url.includes('.pdf')) return 'file-pdf';
  return 'file-image';
});

const hasContactInfo = computed(() => {
  return (props.event.phoneNo && props.event.phoneNo.length > 0) ||
         (props.event.email && props.event.email.length > 0);
});

const viewEventDetails = () => {
  router.push(`/events/${props.event.eventId}`);
};

const handleRegister = () => {
  if (props.loggedIn && !isFull.value) {
    emit('register', props.event);
  }
};

const downloadQR = () => {
  if (!props.event.qrCodeUrl) return;
  
  const link = document.createElement('a');
  link.href = props.event.qrCodeUrl;
  link.download = `${props.event.title}-qr-code.png`;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
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
/* QR Modal Styles */
.qr-modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.85);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  padding: 20px;
}

.qr-modal-content {
  background: linear-gradient(135deg, rgba(20, 20, 20, 0.98), rgba(40, 20, 60, 0.95));
  border: 1px solid rgba(168, 85, 247, 0.3);
  border-radius: 1.5rem;
  padding: 2rem;
  max-width: 500px;
  width: 100%;
  box-shadow: 0 20px 60px rgba(168, 85, 247, 0.3);
}

.qr-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid rgba(168, 85, 247, 0.2);
}

.qr-close-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(168, 85, 247, 0.1);
  border: 1px solid rgba(168, 85, 247, 0.3);
  border-radius: 50%;
  color: #a855f7;
  transition: all 0.2s;
}

.qr-close-btn:hover {
  background: rgba(168, 85, 247, 0.2);
  transform: rotate(90deg);
}

.qr-modal-body {
  text-align: center;
  padding: 1.5rem 0;
}

.qr-large-image {
  width: 100%;
  max-width: 300px;
  height: auto;
  border: 2px solid rgba(168, 85, 247, 0.3);
  border-radius: 1rem;
  box-shadow: 0 8px 32px rgba(168, 85, 247, 0.2);
  margin: 0 auto;
  display: block;
}

.qr-modal-footer {
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid rgba(168, 85, 247, 0.2);
  display: flex;
  justify-content: center;
}

.qr-download-btn {
  padding: 0.75rem 1.5rem;
  background: linear-gradient(90deg, #7c3aed, #ec4899);
  color: white;
  border-radius: 9999px;
  font-weight: 600;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  box-shadow: 0 4px 20px rgba(168, 85, 247, 0.3);
}

.qr-download-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 30px rgba(168, 85, 247, 0.5);
}

/* Modal Transitions */
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: all 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-from .qr-modal-content,
.modal-fade-leave-to .qr-modal-content {
  transform: scale(0.9);
}
</style>