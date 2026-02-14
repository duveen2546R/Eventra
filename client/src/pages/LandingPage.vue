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

    <!-- 🌫 Moving Energy Light Background -->
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

      <div class="flex items-center gap-3 md:gap-6">
        <router-link
          to="/events"
          class="hidden md:flex items-center gap-2 px-4 md:px-6 py-2 rounded-full text-sm font-semibold hover:bg-purple-600/30 border border-purple-400/40 transition-all duration-300"
        >
          <font-awesome-icon :icon="['fas', 'calendar-alt']" /> Events
        </router-link>

        <!-- Show Sign In button only when NOT signed in -->
        <router-link
          v-if="!isSignedIn"
          to="/auth"
          class="flex items-center gap-2 px-4 md:px-6 py-2 rounded-full text-sm font-semibold text-white bg-gradient-to-r from-purple-500 to-pink-500 shadow-md hover:shadow-xl hover:scale-105 transition-all duration-300"
        >
          Sign In <font-awesome-icon :icon="['fas', 'arrow-right']" />
        </router-link>

        <!-- Show user menu when signed in -->
        <div v-else class="flex items-center gap-2 px-4 md:px-6 py-2 rounded-full text-sm font-semibold border border-purple-400/40">
          <font-awesome-icon :icon="['fas', 'user-circle']" />
          <span class="hidden md:inline">Profile</span>
        </div>

        <!-- Theme Toggle -->
        <button
          @click="toggleTheme"
          class="border border-purple-400/30 px-3 md:px-4 py-2 rounded-full transition-all duration-300 text-sm"
          aria-label="Toggle Theme"
        >
          <font-awesome-icon :icon="['fas', theme === 'dark' ? 'sun' : 'moon']" />
          <span class="hidden md:inline">{{ theme === 'dark' ? 'Light' : 'Dark' }}</span>
        </button>
      </div>
    </header>

    <!-- 🌠 Hero Section -->
    <main class="flex flex-col items-center justify-center text-center relative z-20 min-h-[85vh] px-4">
      <Motion :initial="{ opacity: 0, y: 40 }" :animate="{ opacity: 1, y: 0 }" :transition="{ duration: 1.2 }">
        <h1
          class="text-5xl md:text-7xl lg:text-[6rem] font-extrabold leading-tight text-transparent bg-clip-text bg-gradient-to-r from-purple-400 via-pink-500 to-fuchsia-400 drop-shadow-2xl animate-glow"
        >
          Manage. Host. Celebrate.
        </h1>
        <p class="mt-6 text-gray-300 max-w-2xl text-base md:text-lg">
          Empower your events with Eventra — a sleek platform to manage, organize, and elevate your
          gatherings effortlessly.
        </p>
      </Motion>

      <Motion
        :initial="{ opacity: 0 }"
        :animate="{ opacity: 1 }"
        :transition="{ delay: 1.2, duration: 1.2 }"
        class="mt-10 flex flex-col sm:flex-row gap-4 sm:gap-6"
      >
        <router-link
          to="/events"
          @click="handleExploreEvents"
          class="px-6 md:px-8 py-3 rounded-full text-base md:text-lg font-semibold bg-gradient-to-r from-purple-500 to-pink-500 hover:scale-105 hover:shadow-xl transition-all duration-300"
        >
          <font-awesome-icon :icon="['fas', 'compass']" /> Explore Events
        </router-link>

        <router-link
          v-if="!isSignedIn"
          to="/auth"
          @click="handleGetStarted"
          class="px-6 md:px-8 py-3 rounded-full text-base md:text-lg font-semibold border border-purple-400/50 bg-white/10 hover:bg-purple-500/20 transition-all duration-300"
        >
          <font-awesome-icon :icon="['fas', 'sign-in-alt']" /> Get Started
        </router-link>
      </Motion>

      <!-- Features Section -->
      <Motion
        :initial="{ opacity: 0, y: 60 }"
        :animate="{ opacity: 1, y: 0 }"
        :transition="{ delay: 1.8, duration: 1.2 }"
        class="mt-24 w-full max-w-7xl"
      >
        <h2 class="text-3xl md:text-4xl font-bold mb-12 text-transparent bg-clip-text bg-gradient-to-r from-purple-400 to-pink-400">
          Why Choose Eventra?
        </h2>
        
        <div class="features-grid">
          <div class="feature-card">
            <div class="icon">
              <font-awesome-icon :icon="['fas', 'calendar-check']" />
            </div>
            <h4>Easy Event Management</h4>
            <p>Create, organize, and manage events with our intuitive interface</p>
          </div>

          <div class="feature-card">
            <div class="icon">
              <font-awesome-icon :icon="['fas', 'money-bill-wave']" />
            </div>
            <h4>Seamless Payments</h4>
            <p>Integrated payment system for hassle-free event registrations</p>
          </div>

          <div class="feature-card">
            <div class="icon">
              <font-awesome-icon :icon="['fas', 'chart-line']" />
            </div>
            <h4>Detailed Insights</h4>
            <p>Track your events and payments with beautiful analytics</p>
          </div>

          <div class="feature-card">
            <div class="icon">
              <font-awesome-icon :icon="['fas', 'map-marker-alt']" />
            </div>
            <h4>Location Mapping</h4>
            <p>Interactive maps to help attendees find your events easily</p>
          </div>

          <div class="feature-card">
            <div class="icon">
              <font-awesome-icon :icon="['fas', 'users']" />
            </div>
            <h4>Participant Management</h4>
            <p>Keep track of registrations and manage capacity effortlessly</p>
          </div>

          <div class="feature-card">
            <div class="icon">
              <font-awesome-icon :icon="['fas', 'mobile-alt']" />
            </div>
            <h4>Mobile Friendly</h4>
            <p>Access Eventra anywhere, anytime on any device</p>
          </div>
        </div>
      </Motion>
    </main>

    <!-- 🦶 Footer -->
    <footer class="py-16 mt-10 text-center text-gray-400 border-t border-purple-400/20 bg-transparent relative z-20">
      © {{ new Date().getFullYear() }} Eventra — Empower Your Events
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { Motion } from "@motionone/vue";
import { library } from '@fortawesome/fontawesome-svg-core';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import { fas } from '@fortawesome/free-solid-svg-icons';
import GlobalAlert from '../components/GlobalAlert.vue';
import { useAlert } from '../composables/useAlert';

library.add(fas);

const { alertState, showSuccess, showInfo } = useAlert();

const theme = ref(localStorage.getItem("theme") || "dark");

// Check if user is signed in (you can replace this with your actual auth check)
const isSignedIn = computed(() => {
  // Replace with your actual authentication check
  // For example: return !!localStorage.getItem('authToken');
  // or use a Vuex/Pinia store
  return !!localStorage.getItem('authToken');
});

const applyTheme = () => {
  document.documentElement.classList.toggle("dark", theme.value === "dark");
  localStorage.setItem("theme", theme.value);
};

const toggleTheme = () => {
  theme.value = theme.value === "dark" ? "light" : "dark";
  applyTheme();
  showInfo(`Switched to ${theme.value} mode`, 1500);
};

const handleExploreEvents = () => {
  showInfo("Loading events...", 1000);
};

const handleGetStarted = () => {
  showSuccess("Welcome to Eventra!", 1500);
};

onMounted(() => {
  applyTheme();
  // Welcome message on page load
  setTimeout(() => {
    showInfo("Welcome to Eventra! 🎉", 2000);
  }, 500);
});
</script>

<style>
/* Import Inter font from Google Fonts */
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800;900&display=swap');

/* Base Styles */
body {
  font-family: 'Inter', sans-serif;
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

html, body {
  height: 100%;
}

/* ✨ Fog background */
.fog {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 60% 50%, rgba(255, 255, 255, 0.1), transparent 60%),
              radial-gradient(circle at 40% 50%, rgba(255, 255, 255, 0.05), transparent 50%);
  filter: blur(80px);
  animation: fogMove 20s ease-in-out infinite alternate;
  mix-blend-mode: screen;
}

/* 🔥 Light blob */
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

/* 🌊 Sweeping light beam */
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

/* 🌀 Glow effect */
@keyframes glow {
  0%, 100% { text-shadow: 0 0 30px rgba(150, 0, 255, 0.3), 0 0 60px rgba(255, 0, 200, 0.2); }
  50% { text-shadow: 0 0 50px rgba(180, 0, 255, 0.6), 0 0 80px rgba(255, 0, 220, 0.3); }
}

.animate-glow { 
  animation: glow 4s ease-in-out infinite; 
}

/* Background movement */
@keyframes fogMove { 
  0% { transform: translate(0, 0) scale(1); } 
  100% { transform: translate(-10%, 5%) scale(1.2); } 
}

@keyframes lightShift { 
  0% { transform: translate(0, 0) scale(1); } 
  100% { transform: translate(-10%, 10%) scale(1.1); } 
}

/* 🌟 Enhanced Feature Card Styles - 3 Columns Grid */
.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 2.5rem;
  margin-top: 2rem;
}

.feature-card {
  position: relative;
  overflow: hidden;
  padding: 2.5rem;
  border-radius: 1.5rem;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.05), rgba(255, 255, 255, 0.02));
  border: 1px solid rgba(150, 0, 255, 0.2);
  transition: all 0.6s cubic-bezier(0.23, 1, 0.32, 1);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.1);
  text-align: center;
  cursor: pointer;
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at 70% 0%, rgba(200, 0, 255, 0.1), transparent 70%);
  opacity: 0;
  transition: opacity 0.6s cubic-bezier(0.23, 1, 0.32, 1);
  mix-blend-mode: screen;
  z-index: 0;
}

.feature-card::after {
  content: '';
  position: absolute;
  bottom: 0;
  right: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at 30% 100%, rgba(255, 0, 200, 0.1), transparent 70%);
  opacity: 0;
  transition: opacity 0.6s cubic-bezier(0.23, 1, 0.32, 1);
  mix-blend-mode: screen;
  z-index: 0;
}

.feature-card:hover {
  transform: translateY(-10px) scale(1.03);
  border-color: rgba(200, 0, 255, 0.6);
  box-shadow: 0 15px 50px rgba(150, 0, 255, 0.4), 0 0 80px rgba(255, 0, 200, 0.2);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05));
}

.feature-card:hover::before,
.feature-card:hover::after {
  opacity: 1;
}

.feature-card .icon {
  font-size: 4rem;
  margin-bottom: 1.5rem;
  color: #c084fc;
  text-shadow: 0 0 20px rgba(150, 0, 255, 0.6);
  transition: color 0.6s, text-shadow 0.6s, transform 0.6s;
  position: relative;
  z-index: 1;
}

.feature-card:hover .icon {
  color: #f472b6;
  text-shadow: 0 0 30px rgba(255, 0, 200, 0.8), 0 0 50px rgba(200, 0, 255, 0.6);
  transform: scale(1.1) rotate(5deg);
}

.feature-card h4 {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 0.75rem;
  background: linear-gradient(to right, #c084fc, #f472b6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  position: relative;
  z-index: 1;
}

.feature-card p {
  font-size: 1rem;
  line-height: 1.8;
  color: #9ca3af;
  position: relative;
  z-index: 1;
}

/* Dark theme adjustments */
.dark .feature-card {
  background: linear-gradient(135deg, rgba(22, 27, 34, 0.8), rgba(22, 27, 34, 0.5));
  border-color: rgba(150, 0, 255, 0.2);
}

.dark .feature-card:hover {
  border-color: rgba(200, 0, 255, 0.6);
  background: linear-gradient(135deg, rgba(22, 27, 34, 0.9), rgba(22, 27, 34, 0.6));
}

/* Responsive adjustments */
@media (max-width: 1024px) {
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 2rem;
  }
}

@media (max-width: 768px) {
  h1 {
    font-size: 3rem !important;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
  
  .feature-card {
    padding: 2rem;
  }
  
  .feature-card .icon {
    font-size: 3rem;
  }
}
</style>