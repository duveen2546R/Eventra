<template>
  <div
    class="relative min-h-screen w-screen transition-all duration-700"
    :class="theme === 'dark' ? 'bg-[#0a0a0a] text-white' : 'bg-gray-100 text-gray-900'"
  >
    <!-- 🌫 Background -->
    <div class="absolute inset-0 overflow-hidden">
      <div class="fog"></div>
      <div class="light"></div>
      <div class="light-sweep"></div>
    </div>

    <!-- 🧭 Navbar -->
    <header
      class="flex justify-between items-center px-6 md:px-10 py-6 z-20 relative text-sm tracking-wide bg-transparent"
    >
      <div class="flex items-center gap-3">
        <img src="@/assets/app-icon.png" alt="Eventra Logo" class="w-10 h-10 drop-shadow-md rounded-full" />
        <h1 class="text-2xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-purple-400 to-pink-400">
          Eventra
        </h1>
      </div>
    </header>

    <!-- 🎟️ Event Details -->
    <main class="pt-28 px-6 md:px-16 relative z-20">
      <div v-if="event" class="max-w-4xl mx-auto">
        <div class="backdrop-blur-lg bg-white/5 border border-purple-400/20 rounded-2xl shadow-lg p-8 md:p-10">
          <div class="flex flex-col md:flex-row gap-8">
            <!-- Left Side: QR Code -->
            <div class="flex-shrink-0">
              <div class="w-64 h-64 bg-white rounded-lg p-4 flex items-center justify-center">
                <img :src="qrCodeUrl" alt="QR Code" class="w-full h-full" />
              </div>
            </div>

            <!-- Right Side: Event Info -->
            <div class="flex-grow">
              <h1 class="text-3xl font-bold text-purple-300 mb-2">{{ event.title }}</h1>
              <p class="text-gray-400 mb-6">{{ event.description }}</p>

              <div class="grid grid-cols-2 gap-6 text-sm">
                <div>
                  <p class="text-purple-400 font-semibold">
                    <font-awesome-icon :icon="['fas', 'map-marker-alt']" /> Location
                  </p>
                  <p>{{ event.location }}</p>
                </div>
                <div>
                  <p class="text-purple-400 font-semibold">
                    <font-awesome-icon :icon="['fas', 'calendar']" /> Date
                  </p>
                  <p>{{ formatDate(event.eventTimestamp) }}</p>
                </div>
                <div>
                  <p class="text-purple-400 font-semibold">
                    <font-awesome-icon :icon="['fas', 'clock']" /> Time
                  </p>
                  <p>{{ formatTime(event.eventTimestamp) }}</p>
                </div>
                <div>
                  <p class="text-purple-400 font-semibold">
                    <font-awesome-icon :icon="['fas', 'money-bill-wave']" /> Amount
                  </p>
                  <p>{{ displayAmount(event.amount) }}</p>
                </div>
              </div>

              <div class="mt-8">
                <button @click="checkIn" class="btn-primary w-full">
                  <font-awesome-icon :icon="['fas', 'check-circle']" /> Check In
                </button>
              </div>
            </div>
          </div>

          <!-- Participant Info -->
          <div v-if="participant" class="mt-8 pt-8 border-t border-purple-400/20">
            <h2 class="text-2xl font-bold text-purple-300 mb-4">Participant Details</h2>
            <div class="grid grid-cols-2 gap-6 text-sm">
              <div>
                <p class="text-purple-400 font-semibold">
                  <font-awesome-icon :icon="['fas', 'user']" /> Name
                </p>
                <p>{{ participant.user.name }}</p>
              </div>
              <div>
                <p class="text-purple-400 font-semibold">
                  <font-awesome-icon :icon="['fas', 'envelope']" /> Email
                </p>
                <p>{{ participant.user.email }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="text-center text-gray-400 py-20">
        <font-awesome-icon :icon="['fas', 'info-circle']" class="text-4xl mb-4 text-purple-400 animate-pulse" />
        <p class="text-lg">Loading event details...</p>
      </div>
    </main>

    <!-- 🦶 Footer -->
    <footer class="py-6 text-center text-gray-400 border-t border-purple-400/20 bg-transparent">
      © {{ new Date().getFullYear() }} Eventra — Empower Your Events
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';
import { library } from '@fortawesome/fontawesome-svg-core';
import { fas } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
library.add(fas);

const theme = ref(localStorage.getItem('theme') || 'dark');
const route = useRoute();
const event = ref(null);
const participant = ref(null);
const qrCodeUrl = ref('');

const applyTheme = () => {
  document.documentElement.classList.toggle('dark', theme.value === 'dark');
  localStorage.setItem('theme', theme.value);
};

const fetchEventDetails = async () => {
  try {
    const eventId = route.params.id;
    const userId = JSON.parse(localStorage.getItem('user')).user_id;
    const response = await axios.get(`/api/event-participants/${eventId}/${userId}`);
    event.value = response.data.event;
    participant.value = response.data.participant;
    qrCodeUrl.value = response.data.qrCodeUrl;
  } catch (err) {
    console.error('Error fetching event details:', err);
  }
};

const formatDate = (date) =>
  new Date(date).toLocaleDateString('en-IN', { year: 'numeric', month: 'short', day: 'numeric' });

const formatTime = (date) =>
  new Date(date).toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit' });

const displayAmount = (amt) => (amt > 0 ? `₹${amt}` : 'Free');

const checkIn = async () => {
  try {
    const eventId = route.params.id;
    const userId = JSON.parse(localStorage.getItem('user')).user_id;
    await axios.post(`/api/checkin/${eventId}/${userId}`);
    alert('Checked in successfully!');
  } catch (err) {
    console.error('Error checking in:', err);
    alert('Failed to check in. Please try again.');
  }
};

onMounted(() => {
  applyTheme();
  fetchEventDetails();
});
</script>

<style scoped>
.btn-primary {
  background: linear-gradient(90deg, #7c3aed, #ec4899);
  color: white;
  padding: 0.75rem 1.5rem;
  border-radius: 9999px;
  font-weight: 600;
  transition: all 0.2s;
}
</style>