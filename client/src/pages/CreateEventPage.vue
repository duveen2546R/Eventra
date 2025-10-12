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
        <router-link to="/home" class="nav-link" :class="{ active: $route.path === '/home' }">
          <font-awesome-icon :icon="['fas', 'home']" /> Home
        </router-link>
        <router-link to="/events" class="nav-link" :class="{ active: $route.path === '/events' }">
          <font-awesome-icon :icon="['fas', 'calendar-alt']" /> Events
        </router-link>
        <router-link to="/myevents" class="nav-link" :class="{ active: $route.path === '/myevents' }">
          <font-awesome-icon :icon="['fas', 'star']" /> My Events
        </router-link>
        <router-link to="/payments" class="nav-link" :class="{ active: $route.path === '/payments' }">
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
              <span v-if="userDetails.name">{{ userDetails.name }}</span>
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

    <!-- ✏️ Create Event Form -->
    <main class="pt-28 px-6 md:px-16 relative z-20">
      <h1 class="text-4xl md:text-6xl font-bold mb-4">
        Create <span class="text-purple-400">Event</span>
      </h1>
      <p class="text-gray-400 max-w-2xl mb-10">
        Fill in the event details below to add a new event to Eventra.
      </p>

      <form
        @submit.prevent="createEvent"
        class="backdrop-blur-lg bg-white/5 border border-purple-400/20 rounded-2xl shadow-lg p-8 md:p-10 max-w-3xl mx-auto flex flex-col gap-6"
      >
        <div>
          <label class="text-sm text-gray-400">Title</label>
          <input
            v-model="event.title"
            type="text"
            placeholder="Enter event title"
            class="input-box"
            required
          />
        </div>

        <div>
          <label class="text-sm text-gray-400">Description</label>
          <textarea
            v-model="event.description"
            placeholder="Enter event description"
            rows="3"
            class="input-box resize-none"
            required
          ></textarea>
        </div>

        <div class="grid md:grid-cols-2 gap-6">
          <div>
            <label class="text-sm text-gray-400">Location</label>
            <input
              v-model="event.location"
              type="text"
              placeholder="Enter event location"
              class="input-box"
              required
            />
          </div>
          <div>
            <label class="text-sm text-gray-400">Status</label>
            <select v-model="event.status" class="input-box" required>
              <option value="ACTIVE">Active</option>
              <option value="CANCELLED">Cancelled</option>
              <option value="COMPLETED">Completed</option>
            </select>
          </div>
        </div>

        <div class="grid md:grid-cols-2 gap-6">
          <div>
            <label class="text-sm text-gray-400">Latitude</label>
            <input v-model="event.latitude" type="text" placeholder="e.g. 12.9716" class="input-box" />
          </div>
          <div>
            <label class="text-sm text-gray-400">Longitude</label>
            <input v-model="event.longitude" type="text" placeholder="e.g. 77.5946" class="input-box" />
          </div>
        </div>

        <div class="grid md:grid-cols-3 gap-6">
          <div>
            <label class="text-sm text-gray-400">Date</label>
            <input v-model="event.eventDate" type="date" class="input-box" required />
          </div>
          <div>
            <label class="text-sm text-gray-400">Time</label>
            <input v-model="event.eventTime" type="time" class="input-box" required />
          </div>
          <div>
            <label class="text-sm text-gray-400">Amount (₹)</label>
            <input
              v-model.number="event.amount"
              type="number"
              step="0.01"
              placeholder="0 for free"
              class="input-box"
              required
            />
          </div>
        </div>

        <div class="grid md:grid-cols-2 gap-6">
          <div>
            <label class="text-sm text-gray-400">Capacity</label>
            <input v-model.number="event.capacity" type="number" class="input-box" required />
          </div>
          <div>
            <label class="text-sm text-gray-400">Remaining Capacity</label>
            <input v-model.number="event.remainingCapacity" type="number" class="input-box" required />
          </div>
        </div>

        <!-- ✅ Submit Button -->
        <button
          type="submit"
          class="mt-4 py-3 rounded-full bg-gradient-to-r from-purple-500 to-pink-500 text-white font-semibold shadow-lg hover:scale-105 hover:shadow-xl transition-all"
        >
          <font-awesome-icon :icon="['fas', 'plus']" class="mr-2" /> Create Event
        </button>
      </form>
    </main>

    <!-- 🦶 Footer -->
    <footer class="py-6 text-center text-gray-400 border-t border-purple-400/20 bg-transparent">
      © {{ new Date().getFullYear() }} Eventra — Empower Your Events
    </footer>
  </div>
</template>

<script setup>
import { ref } from "vue";
import axios from "axios";
import { library } from "@fortawesome/fontawesome-svg-core";
import { fas } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
library.add(fas);

const theme = ref(localStorage.getItem("theme") || "dark");
const loggedIn = ref(!!localStorage.getItem("token"));
const dropdownOpen = ref(false);

const userDetails = ref(JSON.parse(localStorage.getItem("user") || "{}"));
const event = ref({
  title: "",
  description: "",
  location: "",
  latitude: "",
  longitude: "",
  amount: 0,
  capacity: 0,
  status: "ACTIVE",
  eventDate: "",
  eventTime: "",
  remainingCapacity: 0,
});

const applyTheme = () => {
  document.documentElement.classList.toggle("dark", theme.value === "dark");
  localStorage.setItem("theme", theme.value);
};
const toggleTheme = () => {
  theme.value = theme.value === "dark" ? "light" : "dark";
  applyTheme();
};
applyTheme();

const createEvent = async () => {
  try {
    const token = localStorage.getItem("token");

    await axios.post("http://localhost:8080/api/events/create", event.value, {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`, // ✅ include the token
      },
    });

    alert("🎉 Event created successfully!");
    window.location.href = "/events";
  } catch (err) {
    console.error("Error creating event:", err);
    alert("Failed to create event.");
  }
};


const toggleDropdown = () => (dropdownOpen.value = !dropdownOpen.value);
const logout = () => {
  localStorage.removeItem("token");
  loggedIn.value = false;
  dropdownOpen.value = false;
  userDetails.value = {};
  window.location.href = "/auth";
};
</script>

<style scoped>
.input-box {
  width: 100%;
  background-color: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(168, 85, 247, 0.3);
  border-radius: 12px;
  padding: 0.75rem 1rem;
  margin-top: 0.4rem;
  color: inherit;
  font-size: 0.95rem;
  outline: none;
  transition: all 0.3s;
}
.input-box:focus {
  border-color: rgba(168, 85, 247, 0.6);
  box-shadow: 0 0 0 3px rgba(168, 85, 247, 0.25);
}
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
