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

      <nav class="flex items-center gap-3 md:gap-6">
        <router-link
          to="/home"
          class="nav-link"
          :class="{ active: $route.path === '/home' }"
        >
          <font-awesome-icon :icon="['fas', 'home']" /> Home
        </router-link>

        <router-link
          to="/events"
          class="nav-link"
          :class="{ active: $route.path === '/events' }"
        >
          <font-awesome-icon :icon="['fas', 'calendar-alt']" /> Events
        </router-link>

        <router-link
          to="/myevents"
          class="nav-link"
          :class="{ active: $route.path === '/myevents' }"
        >
          <font-awesome-icon :icon="['fas', 'star']" /> My Events
        </router-link>

        <router-link
          to="/payments"
          class="nav-link"
          :class="{ active: $route.path === '/payments' }"
        >
          <font-awesome-icon :icon="['fas', 'credit-card']" /> Payments
        </router-link>

        <!-- 👤 Auth Controls -->
        <div class="relative">
          <template v-if="loggedIn">
            <button
              @click="toggleDropdown"
              class="flex items-center gap-2 border border-purple-400/30 px-3 py-2 rounded-full transition-all duration-300 text-sm hover:bg-purple-600/20"
            >
              <font-awesome-icon :icon="['fas', 'user-circle']" class="text-lg" />
              <span v-if="userName">{{ userName }}</span> 
            </button>

            <div
              v-if="dropdownOpen"
              class="absolute right-0 mt-3 w-36 bg-white/10 backdrop-blur-lg border border-purple-400/30 rounded-xl shadow-lg text-sm z-50"
            >
              <button
                @click="logout"
                class="w-full flex items-center gap-2 px-4 py-2 hover:bg-purple-500/20 transition-all rounded-lg text-left"
              >
                <font-awesome-icon :icon="['fas', 'sign-out-alt']" />
                Logout
              </button>
            </div>
          </template>

          <template v-else>
            <router-link
              to="/auth"
              class="flex items-center gap-2 px-4 md:px-6 py-2 rounded-full text-sm font-semibold text-white bg-gradient-to-r from-purple-500 to-pink-500 shadow-md hover:shadow-xl hover:scale-105 transition-all duration-300"
            >
              Sign In <font-awesome-icon :icon="['fas', 'arrow-right']" />
            </router-link>
          </template>
        </div>

        <!-- 🌗 Theme Toggle -->
        <button
          @click="toggleTheme"
          class="border border-purple-400/30 px-3 md:px-4 py-2 rounded-full transition-all duration-300 text-sm"
        >
          <font-awesome-icon :icon="['fas', theme === 'dark' ? 'sun' : 'moon']" />
          <span class="hidden md:inline">{{ theme === 'dark' ? 'Light' : 'Dark' }}</span>
        </button>
      </nav>
    </header>

    <!-- 🏠 Home Content -->
    <main class="pt-28 px-6 md:px-16 relative z-20">
      <h1 class="text-4xl md:text-6xl font-bold mb-4">
        Welcome back to <span class="text-purple-400">Eventra</span>!
      </h1>
      <p class="text-gray-400 max-w-2xl mb-12">
        Discover, manage, and celebrate your events in one powerful dashboard.
      </p>

      <!-- Example Dashboard Cards -->
      <div class="grid md:grid-cols-3 gap-8">
        <div v-for="card in dashboardCards" :key="card.title" class="feature-card">
          <div class="icon">
            <font-awesome-icon :icon="card.icon" />
          </div>
          <h4>{{ card.title }}</h4>
          <p>{{ card.text }}</p>
        </div>
      </div>
    </main>

    <!-- 🦶 Footer -->
    <footer class="py-6 text-center text-gray-400 border-t border-purple-400/20 bg-transparent">
      © {{ new Date().getFullYear() }} Eventra — Empower Your Events
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { fas } from "@fortawesome/free-solid-svg-icons";

library.add(fas);

const theme = ref(localStorage.getItem("theme") || "dark");
const loggedIn = ref(!!localStorage.getItem("token"));
const dropdownOpen = ref(false);
const userName = ref(""); 

const applyTheme = () => {
  document.documentElement.classList.toggle("dark", theme.value === "dark");
  localStorage.setItem("theme", theme.value);
};
const toggleTheme = () => {
  theme.value = theme.value === "dark" ? "light" : "dark";
  applyTheme();
};

onMounted(() => {
  applyTheme();

  const userData = localStorage.getItem("user");
  if (userData) {
    try {
      const parsed = JSON.parse(userData);
      userName.value = parsed.name || "";
    } catch (err) {
      console.error("Failed to parse user data:", err);
    }
  }
});


const toggleDropdown = () => {
  dropdownOpen.value = !dropdownOpen.value;
};
const logout = () => {
  localStorage.removeItem("token");
  loggedIn.value = false;
  dropdownOpen.value = false;
  userName.value = ""; // Clear user name on logout
  window.location.href = "/auth";
};

const dashboardCards = [
  { icon: ['fas', 'calendar-check'], title: "Upcoming Events", text: "Stay updated with your upcoming schedules." },
  { icon: ['fas', 'users'], title: "Your Team", text: "Collaborate with your event partners and co-hosts." },
  { icon: ['fas', 'chart-line'], title: "Insights", text: "Track your event performance and analytics easily." },
];
</script>

<style scoped>
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
.nav-link.active {
  background-color: rgba(168, 85, 247, 0.3);
  border-color: rgba(168, 85, 247, 0.5);
}
</style>