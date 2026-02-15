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

      <div v-else-if="event" class="max-w-6xl mx-auto">
        <!-- Event Header -->
        <div class="glass-card rounded-3xl p-8 mb-8">
          <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-6">
            <div>
              <h1 class="text-3xl md:text-4xl font-bold mb-2 bg-gradient-to-r from-purple-400 to-pink-400 bg-clip-text text-transparent">
                {{ event.title }}
              </h1>
              <p class="text-gray-400">{{ formatDateTime(event.eventTimestamp) }}</p>
            </div>
            
            <div class="flex gap-4">
              <div class="text-center px-6 py-4 bg-purple-500/20 rounded-xl border border-purple-400/30">
                <p class="text-sm text-gray-400">Total Registered</p>
                <p class="text-3xl font-bold text-purple-400">{{ stats.totalRegistrations }}</p>
              </div>
              <div class="text-center px-6 py-4 bg-green-500/20 rounded-xl border border-green-500/30">
                <p class="text-sm text-gray-400">Checked In</p>
                <p class="text-3xl font-bold text-green-400">{{ stats.checkedInCount }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Scanner Mode Tabs -->
        <div class="flex gap-4 mb-8">
          <button
            @click="scanMode = 'camera'"
            class="flex-1 px-6 py-4 rounded-xl font-semibold transition-all"
            :class="scanMode === 'camera'
              ? 'bg-gradient-to-r from-purple-500 to-pink-500 text-white shadow-lg'
              : 'bg-white/5 border border-purple-400/20 text-gray-300 hover:bg-purple-500/10'"
          >
            <font-awesome-icon :icon="['fas', 'camera']" class="mr-2" />
            Camera Scanner
          </button>
          <button
            @click="scanMode = 'manual'"
            class="flex-1 px-6 py-4 rounded-xl font-semibold transition-all"
            :class="scanMode === 'manual'
              ? 'bg-gradient-to-r from-purple-500 to-pink-500 text-white shadow-lg'
              : 'bg-white/5 border border-purple-400/20 text-gray-300 hover:bg-purple-500/10'"
          >
            <font-awesome-icon :icon="['fas', 'keyboard']" class="mr-2" />
            Manual Entry
          </button>
        </div>

        <!-- Camera Scanner Section -->
        <div v-if="scanMode === 'camera'" class="glass-card rounded-3xl p-8 mb-8">
          <h2 class="text-2xl font-bold mb-6 flex items-center gap-3">
            <font-awesome-icon :icon="['fas', 'qrcode']" class="text-purple-400" />
            QR Code Scanner
          </h2>

          <div class="space-y-6">
            <!-- Scanner Container -->
            <div class="relative">
              <div 
                id="qr-reader" 
                class="rounded-2xl overflow-hidden shadow-2xl border-4 border-purple-400/30"
                :class="{ 'hidden': !scannerActive }"
              ></div>
              
              <div 
                v-if="!scannerActive"
                class="flex flex-col items-center justify-center bg-gray-900 rounded-2xl p-16 border-4 border-dashed border-purple-400/30"
              >
                <font-awesome-icon :icon="['fas', 'camera']" class="text-6xl text-purple-400 mb-4 opacity-50" />
                <p class="text-gray-400 mb-6">Camera scanner is ready</p>
                <button
                  @click="startScanner"
                  class="px-8 py-4 bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600 rounded-xl font-semibold shadow-lg hover:shadow-xl hover:scale-105 transition-all"
                >
                  <font-awesome-icon :icon="['fas', 'camera']" class="mr-2" />
                  Start Camera
                </button>
              </div>
            </div>

            <!-- Scanner Controls -->
            <div v-if="scannerActive" class="flex gap-4">
              <button
                @click="stopScanner"
                class="flex-1 px-6 py-3 bg-red-500/20 hover:bg-red-500/30 border border-red-500/30 rounded-xl transition-all font-semibold text-red-400"
              >
                <font-awesome-icon :icon="['fas', 'stop']" class="mr-2" />
                Stop Scanner
              </button>
            </div>

            <!-- Last Scanned Pass -->
            <div v-if="lastScannedPass" class="p-4 bg-purple-500/10 rounded-xl border border-purple-400/20">
              <p class="text-sm text-gray-400 mb-1">Last Scanned</p>
              <code class="text-lg font-mono font-bold text-purple-400">{{ lastScannedPass }}</code>
            </div>
          </div>
        </div>

        <!-- Manual Entry Section -->
        <div v-if="scanMode === 'manual'" class="glass-card rounded-3xl p-8 mb-8">
          <h2 class="text-2xl font-bold mb-6 flex items-center gap-3">
            <font-awesome-icon :icon="['fas', 'keyboard']" class="text-purple-400" />
            Manual Pass Entry
          </h2>

          <div class="space-y-6">
            <div>
              <label class="block text-sm font-semibold mb-2 text-gray-300">Enter Pass ID</label>
              <div class="flex gap-4">
                <input
                  v-model="manualPassId"
                  @keyup.enter="checkInManual"
                  type="text"
                  placeholder="EVT-XXXX-USR-XXXX-XXXX"
                  class="flex-1 px-6 py-4 bg-white/5 border border-purple-400/30 rounded-xl focus:border-purple-400/60 focus:ring-2 focus:ring-purple-400/20 outline-none transition-all font-mono text-lg"
                />
                <button
                  @click="checkInManual"
                  :disabled="!manualPassId || processing"
                  class="px-8 py-4 bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600 disabled:from-gray-500 disabled:to-gray-600 disabled:cursor-not-allowed rounded-xl font-semibold shadow-lg hover:shadow-xl hover:scale-105 disabled:hover:scale-100 transition-all"
                >
                  <font-awesome-icon 
                    :icon="['fas', processing ? 'spinner' : 'check']" 
                    :class="{ 'animate-spin': processing }"
                    class="mr-2"
                  />
                  {{ processing ? 'Checking...' : 'Check In' }}
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Check-in Result -->
        <transition name="slide-up">
          <div v-if="checkInResult" class="glass-card rounded-3xl p-8 mb-8">
            <div 
              class="flex items-start gap-6"
              :class="{
                'text-green-400': checkInResult.success,
                'text-red-400': !checkInResult.success && checkInResult.type === 'error',
                'text-orange-400': !checkInResult.success && checkInResult.type === 'warning'
              }"
            >
              <!-- Icon -->
              <div class="flex-shrink-0">
                <div 
                  class="w-20 h-20 rounded-full flex items-center justify-center"
                  :class="{
                    'bg-green-500/20 border-2 border-green-500/50': checkInResult.success,
                    'bg-red-500/20 border-2 border-red-500/50': !checkInResult.success && checkInResult.type === 'error',
                    'bg-orange-500/20 border-2 border-orange-500/50': !checkInResult.success && checkInResult.type === 'warning'
                  }"
                >
                  <font-awesome-icon 
                    :icon="['fas', checkInResult.success ? 'check-circle' : 'exclamation-circle']" 
                    class="text-4xl"
                  />
                </div>
              </div>

              <!-- Content -->
              <div class="flex-1">
                <h3 class="text-2xl font-bold mb-2">{{ checkInResult.message }}</h3>
                
                <!-- Participant Info (on success) -->
                <div v-if="checkInResult.success && checkInResult.participant" class="mt-4 space-y-3">
                  <div class="p-4 bg-white/5 rounded-xl border border-purple-400/20">
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <p class="text-sm text-gray-400">Participant Name</p>
                        <p class="font-semibold text-white text-lg">{{ checkInResult.participant.name }}</p>
                      </div>
                      <div>
                        <p class="text-sm text-gray-400">Email</p>
                        <p class="font-semibold text-white">{{ checkInResult.participant.email }}</p>
                      </div>
                      <div>
                        <p class="text-sm text-gray-400">Phone</p>
                        <p class="font-semibold text-white">{{ checkInResult.participant.phone || 'N/A' }}</p>
                      </div>
                      <div>
                        <p class="text-sm text-gray-400">Pass ID</p>
                        <code class="font-semibold text-purple-400 font-mono">{{ checkInResult.passId }}</code>
                      </div>
                      <div>
                        <p class="text-sm text-gray-400">Check-in Time</p>
                        <p class="font-semibold text-green-400">{{ formatDateTime(new Date()) }}</p>
                      </div>
                    </div>
                  </div>

                  <!-- Undo Button -->
                  <button
                    @click="undoCheckIn(checkInResult.passId)"
                    class="px-6 py-3 bg-orange-500/20 hover:bg-orange-500/30 border border-orange-500/30 rounded-xl transition-all font-semibold text-orange-400"
                  >
                    <font-awesome-icon :icon="['fas', 'undo']" class="mr-2" />
                    Undo Check-in
                  </button>
                </div>

                <!-- Error Details -->
                <div v-if="!checkInResult.success" class="mt-4">
                  <p class="text-gray-300">{{ checkInResult.details }}</p>
                </div>
              </div>

              <!-- Close Button -->
              <button
                @click="clearResult"
                class="flex-shrink-0 w-10 h-10 rounded-full hover:bg-white/10 transition-all flex items-center justify-center"
              >
                <font-awesome-icon :icon="['fas', 'times']" class="text-gray-400" />
              </button>
            </div>
          </div>
        </transition>

        <!-- Recent Check-ins -->
        <div class="glass-card rounded-3xl p-8">
          <div class="flex justify-between items-center mb-6">
            <h2 class="text-2xl font-bold flex items-center gap-3">
              <font-awesome-icon :icon="['fas', 'history']" class="text-purple-400" />
              Recent Check-ins
            </h2>
            <button
              @click="refreshScannerPage"
              :disabled="refreshing"
              class="px-4 py-2 bg-purple-500/20 hover:bg-purple-500/30 border border-purple-400/30 rounded-xl transition-all text-sm font-semibold"
            >
              <font-awesome-icon :icon="['fas', 'sync']" class="mr-2" :class="{ 'animate-spin': refreshing }" />
              {{ refreshing ? 'Refreshing...' : 'Refresh' }}
            </button>
          </div>

          <div v-if="recentCheckIns.length > 0" class="space-y-3">
            <div 
              v-for="(checkIn, index) in recentCheckIns" 
              :key="index"
              class="p-4 bg-purple-500/10 rounded-xl border border-purple-400/20 hover:bg-purple-500/20 transition-all"
            >
              <div class="flex justify-between items-center">
                <div class="flex-1">
                  <p class="font-semibold text-white mb-1">{{ checkIn.name }}</p>
                  <div class="flex gap-4 text-sm text-gray-400">
                    <span>
                      <font-awesome-icon :icon="['fas', 'ticket-alt']" class="mr-1" />
                      {{ checkIn.passId }}
                    </span>
                    <span>
                      <font-awesome-icon :icon="['fas', 'clock']" class="mr-1" />
                      {{ formatRelativeTime(checkIn.checkedInTime) }}
                    </span>
                  </div>
                </div>
                <div class="text-right">
                  <span class="px-3 py-1 bg-green-500/20 text-green-400 rounded-full text-sm font-semibold">
                    <font-awesome-icon :icon="['fas', 'check']" class="mr-1" />
                    Checked In
                  </span>
                </div>
              </div>
            </div>
          </div>

          <div v-else class="text-center py-12 text-gray-400">
            <font-awesome-icon :icon="['fas', 'inbox']" class="text-4xl mb-3 opacity-50" />
            <p>No check-ins yet</p>
          </div>
        </div>
      </div>

      <div v-else class="text-center py-24">
        <font-awesome-icon :icon="['fas', 'exclamation-triangle']" class="text-6xl text-purple-400 mb-4 opacity-50" />
        <h2 class="text-2xl font-bold mb-2">Event Not Found</h2>
        <p class="text-gray-400 mb-6">Unable to load event details.</p>
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
      © {{ new Date().getFullYear() }} Eventra — Organizer Check-in Portal
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from "vue";
import { useRouter, useRoute } from "vue-router";
import api from '../services/api.js';
import { library } from "@fortawesome/fontawesome-svg-core";
import { fas } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import GlobalAlert from '../components/GlobalAlert.vue';
import { useAlert } from '../composables/useAlert';
import { Html5Qrcode } from "html5-qrcode";

library.add(fas);

const router = useRouter();
const route = useRoute();
const { alertState, showSuccess, showError, showWarning, showInfo } = useAlert();

const theme = ref(localStorage.getItem("theme") || "dark");
const loading = ref(true);
const processing = ref(false);
const refreshing = ref(false);

// Event and stats
const event = ref(null);
const stats = ref({
  totalRegistrations: 0,
  checkedInCount: 0
});

// Scanner
const scanMode = ref("camera");
const scannerActive = ref(false);
const html5QrCode = ref(null);
const lastScannedPass = ref("");

// Manual entry
const manualPassId = ref("");

// Check-in result
const checkInResult = ref(null);

// Recent check-ins
const recentCheckIns = ref([]);

const applyTheme = () => {
  document.documentElement.classList.toggle("dark", theme.value === "dark");
  localStorage.setItem("theme", theme.value);
};

const toggleTheme = () => {
  theme.value = theme.value === "dark" ? "light" : "dark";
  applyTheme();
};

const formatDateTime = (timestamp) => {
  if (!timestamp) return 'N/A';
  const date = new Date(timestamp);
  return date.toLocaleString('en-US', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const formatRelativeTime = (timestamp) => {
  if (!timestamp) return 'Just now';
  const now = new Date();
  const then = new Date(timestamp);
  const diffMs = now - then;
  const diffMins = Math.floor(diffMs / 60000);
  
  if (diffMins < 1) return 'Just now';
  if (diffMins < 60) return `${diffMins}m ago`;
  const diffHours = Math.floor(diffMins / 60);
  if (diffHours < 24) return `${diffHours}h ago`;
  const diffDays = Math.floor(diffHours / 24);
  return `${diffDays}d ago`;
};

const goBack = () => {
  router.push('/myevents?role=organizer');
};

const clearResult = () => {
  checkInResult.value = null;
};

// QR Scanner functions
const startScanner = async () => {
  try {
    // Reader element must be visible before starting camera, otherwise
    // some devices initialize with zero-size frames and fail to decode.
    scannerActive.value = true;
    await nextTick();

    html5QrCode.value = new Html5Qrcode("qr-reader");
    
    await html5QrCode.value.start(
      { facingMode: "environment" },
      {
        fps: 10,
        qrbox: { width: 250, height: 250 }
      },
      onScanSuccess,
      onScanFailure
    );
    
    showInfo("Scanner started. Point camera at QR code.");
  } catch (err) {
    console.error("Error starting scanner:", err);
    scannerActive.value = false;
    showError("Failed to start camera. Please check permissions.");
  }
};

const stopScanner = async () => {
  if (html5QrCode.value) {
    try {
      await html5QrCode.value.stop();
      scannerActive.value = false;
      showInfo("Scanner stopped");
    } catch (err) {
      console.error("Error stopping scanner:", err);
    }
  }
};

const onScanSuccess = (decodedText) => {
  lastScannedPass.value = decodedText;
  performCheckIn(decodedText);
  
  // Stop scanner after successful scan
  setTimeout(() => {
    stopScanner();
  }, 500);
};

const onScanFailure = (error) => {
  // Silent fail - camera is just scanning
};

// Check-in functions
const performCheckIn = async (passId) => {
  if (processing.value) return;
  
  processing.value = true;
  clearResult();
  
  try {
    const eventId = route.params.eventId;
    const response = await api.post(`/api/event-organizers/checkin/${eventId}/${passId}`);
    
    const data = response.data;
    
    checkInResult.value = {
      success: true,
      message: "Check-in Successful!",
      passId: data.passId,
      participant: data.user,
      type: 'success'
    };
    
    // Refresh data from backend to avoid stale UI counters
    await refreshStatsAndRecent(true);
    
    showSuccess("Participant checked in successfully!");
    
    // Play success sound (optional)
    playSuccessSound();
    
  } catch (err) {
    console.error("Check-in error:", err);
    
    const errorMessage = err.response?.data?.message || err.response?.data || "Check-in failed";
    
    checkInResult.value = {
      success: false,
      message: "Check-in Failed",
      details: errorMessage,
      type: errorMessage.includes("already") ? 'warning' : 'error'
    };
    
    showError(errorMessage);
  } finally {
    processing.value = false;
  }
};

const checkInManual = () => {
  if (!manualPassId.value.trim()) {
    showWarning("Please enter a Pass ID");
    return;
  }
  
  performCheckIn(manualPassId.value.trim());
  manualPassId.value = "";
};

const undoCheckIn = async (passId) => {
  try {
    const eventId = route.params.eventId;
    await api.post(`/api/event-organizers/undo-checkin/${eventId}/${passId}`);
    
    showSuccess("Check-in undone successfully!");
    
    // Clear result
    clearResult();
    
    // Refresh data from backend to avoid stale UI counters
    await refreshStatsAndRecent(true);
    
  } catch (err) {
    console.error("Undo check-in error:", err);
    showError("Failed to undo check-in");
  }
};

const playSuccessSound = () => {
  // Optional: Play a success sound
  const audio = new Audio('data:audio/wav;base64,UklGRnoGAABXQVZFZm10IBAAAAABAAEAQB8AAEAfAAABAAgAZGF0YQoGAACBhYqFbF1fdJivrJBhNjVgodDbq2EcBj+a2/LDciUFLIHO8tiJNwgZaLvt559NEAxQp+PwtmMcBjiR1/LMeSwFJHfH8N2QQAoUXrTp66hVFApGn+DyvmwhBSuJzvDijz0KF2S36+OdTgoQU6zj7rdmHAU+lNLvy3opBSl+zPLaizsKGGS56+mjUBEQU6vn77RnGwU8ktL0zHooByl8yO/bjD4LHGe76+mjTxEPUqrl7rdnHAU+ltL0y3ooByh7yO7ckD4KHGe66+mjTxEPUqvm7rdnHAU+ltL0y3ooByh7yO7ckD4KHGe66+mjTxEPU6vm7rdnHAU+ltL0y3ooByh7yO7ckD4KHGe66+mjTxEPU6vm7rdnHAU+ltL0y3ooByh7yO7ckD4KHGe66+mjTxEPU6vm7rdnHAU+ltL0y3ooByh7yO7ckD4KHGe66+mjTxEPU6vm7rdnHAU+ltL0y3ooByh7yO7ckD4KHGe66+mjTxEPU6vm7rdnHAU+ltL0y3ooByh7yO7ckD4KHGe66+mjTxEPU6vm7rdnHAU+ltL0y3ooByh7yO7ckD4KHGe66+mjTxEPU6vm7rdnHAU+ltL0y3ooByh7yO7ckD4KHGe66+mjTxEPU6vm7rdnHAU+ltL0y3ooByh7yO7ckD4KHGe66+mjTxEPU6vm7rdnHAU+ltL0y3ooByh7yO7ckD4KHGe66+mjTxEPU6vm7rdnHAU+ltL0y3ooByh7yO7ckD4KHGe66+mjTxEPU6vm7rdnHAU+ltL0y3ooByh7yO7ckD4KHGe66+mjTxEPU6vm7rdnHAU+ltL0y3ooByh7yO7ckD4KHGe66+mjTxEPU6vm7rdnHAU+ltL0y3ooByh7yO7ckD4KHGe66+mjTxEPU6vm7rdnHAU+ltL0y3ooByh7yO7ckD4KHGe66+mjTxEPU6vm7rdnHAU+ltL0y3ooByh7yO7ckD4KHGe66+mjTxEPU6vm7rdnHAU+ltL0y3ooByh7yO7ckD4KHGe66+mjTxEPU6vm7rdnHAU+ltL0y3ooByh7yO7ckD4KHGe66+mjTxEPU6vm7rdnHAU+ltL0y3ooByh7yO7ckD4KHGe66+mjTxEPU6vm7rdnHAU+ltL0y3ooByh7yO7ckD4KHGe66+mjTxEPU6vm7rdnHAU+ltL0y3ooByh7yO7ckD4KHGe66+mjTxEPU6vm7rdnHAU+ltL0y3ooByh7yO7ckD4KHGe66+mjTxEPU6vm7rdnHAU+ltL0y3ooByh7yO7ckD4KHGe66+mjTxEPU6vm7rdnHAU+ltL0y3ooByh7yO7ckD4K');
  audio.play().catch(() => {});
};

const fetchEventDetails = async (forceRefresh = false) => {
  const eventId = route.params.eventId;
  
  if (!eventId) {
    showError("Invalid event ID");
    loading.value = false;
    return;
  }

  try {
<<<<<<< HEAD
    const response = await api.get(`/api/events/${eventId}`);
=======
    const response = await axios.get(`/api/events/${eventId}`, {
      params: forceRefresh ? { _ts: Date.now() } : {}
    });
>>>>>>> 27166d7 (Fixes Done)
    event.value = response.data;
  } catch (err) {
    console.error("Error fetching event:", err);
    showError("Failed to load event details");
  } finally {
    loading.value = false;
  }
};

const fetchEventStats = async (forceRefresh = false) => {
  const eventId = route.params.eventId;

  try {
    const response = await axios.get(`/api/events/${eventId}/stats`, {
      params: forceRefresh ? { _ts: Date.now() } : {}
    });
    stats.value = {
      totalRegistrations: response.data.totalRegistrations || 0,
      checkedInCount: response.data.checkedInCount || 0
    };
  } catch (err) {
    console.error("Error fetching event stats:", err);
    // Fallback: infer total registrations from participants list when stats endpoint fails
    try {
      const participantsRes = await axios.get(`/api/event-organizers/participants/${eventId}`, {
        params: forceRefresh ? { _ts: Date.now() } : {}
      });
      const total = Array.isArray(participantsRes.data) ? participantsRes.data.length : 0;
      stats.value = {
        totalRegistrations: total,
        checkedInCount: stats.value.checkedInCount || 0
      };
    } catch (fallbackErr) {
      console.error("Error fetching participant fallback stats:", fallbackErr);
    }
  }
};

const fetchRecentCheckIns = async (forceRefresh = false) => {
  const eventId = route.params.eventId;
  
  try {
<<<<<<< HEAD
    const response = await api.get(`/api/event-organizers/recent-checkins/${eventId}?limit=10`);
=======
    const params = { limit: 10 };
    if (forceRefresh) params._ts = Date.now();

    const response = await axios.get(`/api/event-organizers/recent-checkins/${eventId}`, {
      params
    });
>>>>>>> 27166d7 (Fixes Done)
    recentCheckIns.value = response.data;
  } catch (err) {
    console.error("Error fetching recent check-ins:", err);
  }
};

const refreshStatsAndRecent = async (forceRefresh = false) => {
  await Promise.all([
    fetchEventStats(forceRefresh),
    fetchRecentCheckIns(forceRefresh)
  ]);
};

const refreshScannerPage = async () => {
  if (refreshing.value) return;
  refreshing.value = true;

  try {
    await fetchEventDetails(true);
    await refreshStatsAndRecent(true);
    showInfo("Scanner data refreshed");
  } finally {
    refreshing.value = false;
  }
};

onMounted(async () => {
  applyTheme();
  
  if (!localStorage.getItem("token")) {
    showWarning("Please login to access this page");
    setTimeout(() => router.push("/auth"), 1500);
    return;
  }

  await fetchEventDetails();
  await refreshStatsAndRecent();
});

onBeforeUnmount(() => {
  if (scannerActive.value && html5QrCode.value) {
    stopScanner();
  }
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

/* Slide up transition */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease;
}

.slide-up-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.slide-up-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}
</style>
