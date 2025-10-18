<template>
  <div
    class="relative min-h-screen w-screen transition-all duration-700"
    :class="theme === 'dark' ? 'bg-[#0a0a0a] text-white' : 'bg-gray-100 text-gray-900'"
  >
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
          to="/home"
          class="hidden md:flex items-center gap-2 px-4 md:px-6 py-2 rounded-full text-sm font-semibold hover:bg-purple-600/30 border border-purple-400/40 transition-all duration-300"
        >
          <font-awesome-icon :icon="['fas', 'calendar-alt']" /> Events
        </router-link>

        <router-link
          to="/auth"
          class="flex items-center gap-2 px-4 md:px-6 py-2 rounded-full text-sm font-semibold text-white bg-gradient-to-r from-purple-500 to-pink-500 shadow-md hover:shadow-xl hover:scale-105 transition-all duration-300"
        >
          Sign In <font-awesome-icon :icon="['fas', 'arrow-right']" />
        </router-link>

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
          class="px-6 md:px-8 py-3 rounded-full text-base md:text-lg font-semibold bg-gradient-to-r from-purple-500 to-pink-500 hover:scale-105 hover:shadow-xl transition-all duration-300"
        >
          <font-awesome-icon :icon="['fas', 'compass']" /> Explore Events
        </router-link>

        <router-link
          to="/auth"
          class="px-6 md:px-8 py-3 rounded-full text-base md:text-lg font-semibold border border-purple-400/50 bg-white/10 hover:bg-purple-500/20 transition-all duration-300"
        >
          <font-awesome-icon :icon="['fas', 'sign-in-alt']" /> Get Started
        </router-link>
      </Motion>
    </main>

    <!-- 🦶 Footer -->
    <footer class="py-6 text-center text-gray-400 border-t border-purple-400/20 bg-transparent">
      © {{ new Date().getFullYear() }} Eventra — Empower Your Events
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { Motion } from "@motionone/vue";
import { library } from '@fortawesome/fontawesome-svg-core';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import { fas } from '@fortawesome/free-solid-svg-icons';
// Assuming you have 'vue-router' installed and configured for router-link
// If not, replace <router-link> with <a> tags or install vue-router.

// Add all solid icons to the library
library.add(fas);

const theme = ref(localStorage.getItem("theme") || "dark");

const applyTheme = () => {
  document.documentElement.classList.toggle("dark", theme.value === "dark");
  localStorage.setItem("theme", theme.value);
};
const toggleTheme = () => {
  theme.value = theme.value === "dark" ? "light" : "dark";
  applyTheme();
};
onMounted(applyTheme);

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

/* Ensure full height for HTML and body */
html, body {
  height: 100%;
}

.text-accent {
  color: #00c896; /* This accent color is from the first template, keeping it for consistency if needed */
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
  background: radial-gradient(circle at 60% 50%, rgba(255, 255, 255, 0.35), rgba(150, 0, 255, 0.15), transparent 80%); /* Adjusted color for purple theme */
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
  0%, 100% { text-shadow: 0 0 30px rgba(150, 0, 255, 0.3), 0 0 60px rgba(255, 0, 200, 0.2); } /* Adjusted for purple/pink glow */
  50% { text-shadow: 0 0 50px rgba(180, 0, 255, 0.6), 0 0 80px rgba(255, 0, 220, 0.3); } /* Adjusted for purple/pink glow */
}
.animate-glow { animation: glow 4s ease-in-out infinite; }

/* 🎞 Parallax fog for feature section */
.parallax-fog {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 60% 50%, rgba(255, 255, 255, 0.05), transparent 60%),
              radial-gradient(circle at 40% 60%, rgba(150, 0, 255, 0.04), transparent 70%); /* Adjusted color */
  filter: blur(80px);
  animation: parallaxFogMove 25s ease-in-out infinite alternate;
}
@keyframes parallaxFogMove {
  0% { transform: translate(0%, 0%) scale(1); }
  100% { transform: translate(-10%, 8%) scale(1.2); }
}

/* Background movement */
@keyframes fogMove { 0% { transform: translate(0, 0) scale(1); } 100% { transform: translate(-10%, 5%) scale(1.2); } }
@keyframes lightShift { 0% { transform: translate(0, 0) scale(1); } 100% { transform: translate(-10%, 10%) scale(1.1); } }

/* 🌟 Enhanced Feature Card Styles */
.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2.5rem; /* Increased gap for better spacing */
  margin-top: 4rem; /* Adjusted top margin */
}

.feature-card {
  position: relative;
  overflow: hidden;
  padding: 2.5rem; /* Slightly more padding */
  border-radius: 1.5rem; /* More rounded corners */
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.05), rgba(255, 255, 255, 0.02)); /* Subtle translucent background */
  border: 1px solid rgba(150, 0, 255, 0.2); /* Soft purple border */
  transition: all 0.6s cubic-bezier(0.23, 1, 0.32, 1); /* Smoother, more pronounced transition */
  backdrop-filter: blur(10px); /* Add blur for a frosted glass effect */
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.1);
  text-align: center; /* Ensure content is centered */
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at 70% 0%, rgba(200, 0, 255, 0.1), transparent 70%); /* Top-right light glow */
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
  background: radial-gradient(circle at 30% 100%, rgba(255, 0, 200, 0.1), transparent 70%); /* Bottom-left light glow */
  opacity: 0;
  transition: opacity 0.6s cubic-bezier(0.23, 1, 0.32, 1);
  mix-blend-mode: screen;
  z-index: 0;
}

.feature-card:hover {
  transform: translateY(-10px) scale(1.03); /* More lift and slight scale */
  border-color: rgba(200, 0, 255, 0.6); /* More prominent border on hover */
  box-shadow: 0 15px 50px rgba(150, 0, 255, 0.4), 0 0 80px rgba(255, 0, 200, 0.2); /* Enhanced glow effect */
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05)); /* Slightly brighter background */
}

.feature-card:hover::before,
.feature-card:hover::after {
  opacity: 1; /* Make glows visible on hover */
}

.feature-card .icon {
  font-size: 4.5rem; /* Larger icons */
  margin-bottom: 1.5rem;
  color: #c084fc; /* A vibrant purple for icons */
  text-shadow: 0 0 20px rgba(150, 0, 255, 0.6); /* Icon glow */
  transition: color 0.6s, text-shadow 0.6s;
  position: relative;
  z-index: 1;
}

.feature-card:hover .icon {
  color: #f472b6; /* Pinker on hover */
  text-shadow: 0 0 30px rgba(255, 0, 200, 0.8), 0 0 50px rgba(200, 0, 255, 0.6);
}

.feature-card h4 {
  font-size: 2.2rem; /* Larger title */
  font-weight: 700;
  margin-bottom: 0.75rem;
  color: darkorchid;
  position: relative;
  z-index: 1;
}

.feature-card p {
  font-size: 1.1rem; /* Slightly larger text */
  line-height: 1.8;
  color: #ccc; /* Lighter gray for text */
  position: relative;
  z-index: 1;
}

/* Adjustments for dark theme specific styles if needed, otherwise these generally work for dark */
.dark .feature-card {
  background: linear-gradient(135deg, rgba(22, 27, 34, 0.8), rgba(22, 27, 34, 0.5));
  border-color: rgba(150, 0, 255, 0.2);
}

.dark .feature-card:hover {
  border-color: rgba(200, 0, 255, 0.6);
  background: linear-gradient(135deg, rgba(22, 27, 34, 0.9), rgba(22, 27, 34, 0.6));
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .hero-section h1 {
    font-size: 3rem; /* Smaller heading on mobile */
  }
}
</style>