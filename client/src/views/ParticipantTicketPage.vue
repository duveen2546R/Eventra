<template>
  <div
    class="relative min-h-screen w-screen transition-all duration-700"
    :class="theme === 'dark' ? 'bg-[#0a0a0a] text-white' : 'bg-gray-100 text-gray-900'"
  >
    <!-- Global Alert Component -->
    <GlobalAlert 
      v-model="alertState.show"
      :message="alertState.message"
      :type="alertState.type"
      :duration="alertState.duration"
    />

    <!-- Background -->
    <div class="absolute inset-0 overflow-hidden">
      <div class="fog"></div>
      <div class="light"></div>
      <div class="light-sweep"></div>
    </div>

    <!-- Navbar -->
    <header
      class="flex justify-between items-center px-6 md:px-10 py-6 z-20 relative text-sm tracking-wide bg-transparent"
    >
      <div class="flex items-center gap-3">
        <img src="@/assets/app-icon.png" alt="Eventra Logo" class="w-10 h-10 drop-shadow-md rounded-full" />
        <h1 class="text-2xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-purple-400 to-pink-400">
          Eventra
        </h1>
      </div>

      <nav class="flex items-center gap-3 md:gap-6">
        <router-link to="/home" class="nav-link">
          <font-awesome-icon :icon="['fas', 'home']" /> Home
        </router-link>
        <router-link to="/myevents" class="nav-link">
          <font-awesome-icon :icon="['fas', 'star']" /> My Events
        </router-link>
        <button
          @click="toggleTheme"
          class="border border-purple-400/30 px-3 md:px-4 py-2 rounded-full transition-all duration-300 text-sm"
        >
          <font-awesome-icon :icon="['fas', theme === 'dark' ? 'sun' : 'moon']" />
        </button>
      </nav>
    </header>

    <!-- Main Content -->
    <main class="pt-20 px-6 md:px-16 lg:px-24 relative z-20 pb-20">
      <!-- Back Button -->
      <button
        @click="goBack"
        class="mb-6 flex items-center gap-2 text-purple-400 hover:text-purple-300 transition-all duration-300"
      >
        <font-awesome-icon :icon="['fas', 'arrow-left']" />
        <span>Back to My Events</span>
      </button>

      <div v-if="loading" class="flex justify-center items-center py-24">
        <div class="animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-purple-500"></div>
      </div>

      <div v-else-if="ticket" class="max-w-4xl mx-auto">
        <!-- Digital Ticket Card -->
        <div class="glass-card rounded-3xl p-8 md:p-12 mb-8 relative overflow-hidden">
          <!-- Ticket Header with Pattern -->
          <div class="absolute top-0 left-0 right-0 h-32 bg-gradient-to-br from-purple-500/20 to-pink-500/20 -z-10">
            <div class="absolute inset-0 opacity-10">
              <svg class="w-full h-full" xmlns="http://www.w3.org/2000/svg">
                <pattern id="grid" width="40" height="40" patternUnits="userSpaceOnUse">
                  <circle cx="20" cy="20" r="1" fill="white" />
                </pattern>
                <rect width="100%" height="100%" fill="url(#grid)" />
              </svg>
            </div>
          </div>

          <!-- Event Title & Status -->
          <div class="text-center mb-8 relative">
            <div class="flex justify-center mb-4">
              <span
                class="px-6 py-2 rounded-full text-sm font-bold backdrop-blur-md shadow-lg inline-flex items-center gap-2"
                :class="checkedInStatusClass"
              >
                <font-awesome-icon :icon="['fas', checkedIn ? 'check-circle' : 'clock']" />
                {{ checkedIn ? 'CHECKED IN' : 'NOT CHECKED IN' }}
              </span>
            </div>
            
            <h1 class="text-3xl md:text-4xl font-bold mb-2 bg-gradient-to-r from-purple-400 to-pink-400 bg-clip-text text-transparent">
              {{ eventTitle }}
            </h1>
            <p class="text-gray-400">{{ formatDateTime(eventTimestamp) }}</p>
          </div>

          <!-- QR Code Section - Main Focus -->
          <div class="flex justify-center mb-8">
            <div class="relative group">
              <!-- QR Code Container with Border Animation -->
              <div class="relative p-8 bg-white rounded-3xl shadow-2xl">
                <div class="absolute inset-0 bg-gradient-to-r from-purple-500 to-pink-500 rounded-3xl opacity-0 group-hover:opacity-100 transition-opacity blur-xl"></div>
                <img
                  v-if="qrCodeUrl"
                  :src="qrCodeUrl"
                  alt="Your Event Ticket QR Code"
                  class="relative w-64 h-64 md:w-80 md:h-80 object-contain"
                  @error="handleQRError"
                />
                <div v-else class="relative w-64 h-64 md:w-80 md:h-80 flex items-center justify-center bg-gray-200 rounded-2xl">
                  <font-awesome-icon :icon="['fas', 'qrcode']" class="text-6xl text-gray-400" />
                </div>
              </div>
              
              <!-- Scan Instruction -->
              <div class="absolute -bottom-6 left-1/2 transform -translate-x-1/2 whitespace-nowrap">
                <div class="px-6 py-2 bg-purple-500/90 rounded-full text-white text-sm font-semibold shadow-lg">
                  <font-awesome-icon :icon="['fas', 'mobile-alt']" class="mr-2" />
                  Show this QR at entry
                </div>
              </div>
            </div>
          </div>

          <!-- Pass ID -->
          <div class="text-center mt-12 mb-8">
            <p class="text-sm text-gray-400 mb-2">Pass ID</p>
            <div class="flex items-center justify-center gap-3">
              <code class="text-xl md:text-2xl font-mono font-bold text-purple-400 bg-purple-500/10 px-6 py-3 rounded-xl border border-purple-400/30">
                {{ passId }}
              </code>
              <button
                @click="copyPassId"
                class="p-3 bg-purple-500/20 hover:bg-purple-500/30 border border-purple-400/30 rounded-xl transition-all"
                title="Copy Pass ID"
              >
                <font-awesome-icon :icon="['fas', 'copy']" />
              </button>
            </div>
          </div>

          <!-- Event Details Grid -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4 pt-8 border-t border-purple-400/20">
            <div class="info-card">
              <font-awesome-icon :icon="['fas', 'calendar']" class="text-purple-400 text-xl mb-2" />
              <p class="text-sm text-gray-400">Date & Time</p>
              <p class="font-semibold">{{ formatDateTime(eventTimestamp) }}</p>
            </div>

            <div class="info-card">
              <font-awesome-icon :icon="['fas', 'map-marker-alt']" class="text-purple-400 text-xl mb-2" />
              <p class="text-sm text-gray-400">Venue</p>
              <p class="font-semibold">{{ eventLocation }}</p>
            </div>

            <div class="info-card" v-if="checkedInTime">
              <font-awesome-icon :icon="['fas', 'clock']" class="text-green-400 text-xl mb-2" />
              <p class="text-sm text-gray-400">Checked In At</p>
              <p class="font-semibold text-green-400">{{ formatDateTime(checkedInTime) }}</p>
            </div>

            <div class="info-card">
              <font-awesome-icon :icon="['fas', 'ticket-alt']" class="text-purple-400 text-xl mb-2" />
              <p class="text-sm text-gray-400">Registration Date</p>
              <p class="font-semibold">{{ formatDateTime(registrationDate) }}</p>
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="flex flex-col sm:flex-row gap-4 mt-8 pt-8 border-t border-purple-400/20">
            <button
              @click="downloadTicket"
              class="flex-1 flex items-center justify-center gap-2 px-6 py-4 bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600 rounded-xl transition-all duration-300 font-semibold shadow-lg hover:shadow-xl hover:scale-105"
            >
              <font-awesome-icon :icon="['fas', 'download']" />
              Download Ticket
            </button>
            
            <button
              @click="shareTicket"
              class="flex-1 flex items-center justify-center gap-2 px-6 py-4 bg-purple-500/20 hover:bg-purple-500/30 border border-purple-400/30 rounded-xl transition-all duration-300 font-semibold"
            >
              <font-awesome-icon :icon="['fas', 'share-alt']" />
              Share
            </button>
          </div>

          <!-- Important Notice -->
          <div class="mt-8 p-4 bg-yellow-500/10 border border-yellow-500/30 rounded-xl">
            <div class="flex gap-3">
              <font-awesome-icon :icon="['fas', 'info-circle']" class="text-yellow-400 mt-1 flex-shrink-0" />
              <div>
                <p class="font-semibold text-yellow-400 mb-1">Important</p>
                <p class="text-sm text-gray-300">
                  Please ensure your QR code is clearly visible at the entry point. 
                  Take a screenshot or download this ticket for offline access.
                  {{ checkedIn ? 'This ticket has already been used for check-in.' : 'This ticket can only be used once.' }}
                </p>
              </div>
            </div>
          </div>
        </div>

        <!-- Additional Event Info -->
        <div class="glass-card rounded-3xl p-8 mb-8">
          <h2 class="text-2xl font-bold mb-6 flex items-center gap-3">
            <font-awesome-icon :icon="['fas', 'info-circle']" class="text-purple-400" />
            Event Information
          </h2>
          
          <div class="space-y-4">
            <div v-if="eventDescription" class="p-4 bg-purple-500/10 rounded-xl border border-purple-400/20">
              <p class="text-gray-300">{{ eventDescription }}</p>
            </div>
            
            <button
              @click="viewEventDetails"
              class="w-full flex items-center justify-center gap-2 px-6 py-4 bg-purple-500/20 hover:bg-purple-500/30 border border-purple-400/30 rounded-xl transition-all duration-300 font-semibold"
            >
              <font-awesome-icon :icon="['fas', 'eye']" />
              View Full Event Details
            </button>
          </div>
        </div>
      </div>

      <div v-else class="text-center py-24">
        <font-awesome-icon :icon="['fas', 'exclamation-triangle']" class="text-6xl text-purple-400 mb-4 opacity-50" />
        <h2 class="text-2xl font-bold mb-2">Ticket Not Found</h2>
        <p class="text-gray-400 mb-6">Unable to load your ticket. Please try again.</p>
        <router-link
          to="/myevents"
          class="inline-flex items-center gap-2 px-6 py-3 bg-purple-500/20 hover:bg-purple-500/30 border border-purple-400/30 rounded-full transition-all duration-300"
        >
          <font-awesome-icon :icon="['fas', 'arrow-left']" />
          Back to My Events
        </router-link>
      </div>
    </main>

    <!-- Footer -->
    <footer class="py-6 text-center text-gray-400 border-t border-purple-400/20 bg-transparent relative z-20">
      © {{ new Date().getFullYear() }} Eventra — Your Digital Event Pass
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import api from '../services/api.js';
import { library } from "@fortawesome/fontawesome-svg-core";
import { fas } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import GlobalAlert from '../components/GlobalAlert.vue';
import { useAlert } from '../composables/useAlert';

library.add(fas);

const router = useRouter();
const route = useRoute();
const { alertState, showSuccess, showError, showWarning, showInfo } = useAlert();

const theme = ref(localStorage.getItem("theme") || "dark");
const loading = ref(true);

// Ticket data
const ticket = ref(null);
const passId = ref("");
const qrCodeUrl = ref("");
const checkedIn = ref(false);
const checkedInTime = ref(null);
const eventTitle = ref("");
const eventDescription = ref("");
const eventTimestamp = ref("");
const eventLocation = ref("");
const registrationDate = ref("");

const checkedInStatusClass = computed(() => {
  return checkedIn.value 
    ? 'bg-green-500/90 text-white animate-pulse' 
    : 'bg-orange-500/90 text-white';
});

const applyTheme = () => {
  document.documentElement.classList.toggle("dark", theme.value === "dark");
  localStorage.setItem("theme", theme.value);
};

const toggleTheme = () => {
  theme.value = theme.value === "dark" ? "light" : "dark";
  applyTheme();
};

const formatDateTime = (timestamp) => {
  if (!timestamp) return 'TBA';
  const date = new Date(timestamp);
  return date.toLocaleString('en-US', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const goBack = () => {
  router.push('/myevents?role=participant');
};

const viewEventDetails = () => {
  router.push(`/events/${route.params.eventId}`);
};

const handleQRError = () => {
  showError("Failed to load QR code");
};

const copyPassId = () => {
  navigator.clipboard.writeText(passId.value);
  showSuccess("Pass ID copied to clipboard!");
};

const downloadTicket = () => {
  if (!qrCodeUrl.value) {
    showError("QR code not available");
    return;
  }
  
  const link = document.createElement('a');
  link.href = qrCodeUrl.value;
  link.download = `${eventTitle.value}-ticket-${passId.value}.png`;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  showSuccess("Ticket downloaded!");
};

const shareTicket = async () => {
  const shareData = {
    title: `${eventTitle.value} - Event Ticket`,
    text: `My ticket for ${eventTitle.value}\nPass ID: ${passId.value}`,
    url: window.location.href
  };

  if (navigator.share) {
    try {
      await navigator.share(shareData);
      showSuccess("Ticket shared!");
    } catch (err) {
      if (err.name !== 'AbortError') {
        copyPassId();
      }
    }
  } else {
    copyPassId();
  }
};

const fetchTicket = async () => {
  const eventId = route.params.eventId;
  const userDetails = JSON.parse(localStorage.getItem("user") || "{}");
  const userId = userDetails.user_id;

  if (!eventId || !userId) {
    showError("Invalid ticket parameters");
    loading.value = false;
    return;
  }

  try {
    // Fetch participant ticket data
    const response = await api.get(`/api/event-participants/${eventId}/${userId}`);
    const data = response.data;
    console.log("Fetched ticket data:", data);
    ticket.value = data;
    passId.value = data.passId;
    qrCodeUrl.value = data.qrCodeUrl;
    checkedIn.value = data.checkedIn || false;
    checkedInTime.value = data.checkedInTime;
    registrationDate.value = data.participant.registeredAt;

    // Fetch event details
    const eventResponse = await api.get(`/api/events/${eventId}`);
    const eventData = eventResponse.data;
    
    eventTitle.value = eventData.title;
    eventDescription.value = eventData.description;
    eventTimestamp.value = eventData.eventTimestamp;
    eventLocation.value = eventData.location;

  } catch (err) {
    console.error("Error fetching ticket:", err);
    showError("Failed to load ticket. Please try again.");
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  applyTheme();
  
  if (!localStorage.getItem("token")) {
    showWarning("Please login to view your ticket");
    setTimeout(() => router.push("/auth"), 1500);
    return;
  }

  fetchTicket();
});
</script>

<style scoped>
/* Background animations */
.fog {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 60% 50%, rgba(255, 255, 255, 0.1), transparent 60%),
              radial-gradient(circle at 40% 50%, rgba(255, 255, 255, 0.05), transparent 50%);
  filter: blur(80px);
  animation: fogMove 20s ease-in-out infinite alternate;
  mix-blend-mode: screen;
}

.light {
  position: absolute;
  right: -10%;
  top: -10%;
  width: 80%;
  height: 120%;
  background: radial-gradient(circle at 60% 50%, rgba(255, 255, 255, 0.35), rgba(150, 0, 255, 0.15), transparent 80%);
  filter: blur(160px);
  animation: lightShift 18s ease-in-out infinite alternate;
  mix-blend-mode: screen;
}

.light-sweep {
  position: absolute;
  top: 0;
  right: -60%;
  width: 160%;
  height: 100%;
  background: linear-gradient(100deg, transparent 45%, rgba(255, 255, 255, 0.3) 50%, transparent 55%);
  filter: blur(60px);
  mix-blend-mode: screen;
  animation: sweep 12s ease-in-out infinite;
}

@keyframes sweep {
  0% { transform: translateX(80%); opacity: 0.05; }
  50% { transform: translateX(0%); opacity: 0.5; }
  100% { transform: translateX(-80%); opacity: 0.05; }
}

@keyframes fogMove { 
  0% { transform: translate(0, 0) scale(1); } 
  100% { transform: translate(-10%, 5%) scale(1.2); } 
}

@keyframes lightShift { 
  0% { transform: translate(0, 0) scale(1); } 
  100% { transform: translate(-10%, 10%) scale(1.1); } 
}

/* Glass card effect */
.glass-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.37);
}

.dark .glass-card {
  background: rgba(10, 10, 10, 0.6);
  border: 1px solid rgba(168, 85, 247, 0.2);
}

/* Info card */
.info-card {
  @apply p-4 rounded-xl bg-purple-500/10 border border-purple-400/20 transition-all duration-300;
}

.info-card:hover {
  @apply bg-purple-500/20 border-purple-400/40;
}

/* Navigation link */
.nav-link {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.6rem 1rem;
  border-radius: 9999px;
  border: 1px solid transparent;
  transition: all 0.3s;
  font-weight: 600;
}

.nav-link:hover {
  background-color: rgba(168, 85, 247, 0.2);
  border-color: rgba(168, 85, 247, 0.3);
}
</style>