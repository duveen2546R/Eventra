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

        <button
          @click="toggleTheme"
          class="border border-purple-400/30 px-3 md:px-4 py-2 rounded-full transition-all duration-300 text-sm"
        >
          <font-awesome-icon :icon="['fas', theme === 'dark' ? 'sun' : 'moon']" />
          <span class="hidden md:inline">{{ theme === 'dark' ? 'Light' : 'Dark' }}</span>
        </button>
      </nav>
    </header>

    <!-- 🧩 My Events -->
    <main class="pt-28 px-6 md:px-16 relative z-20">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-6 mb-10">
        <!-- 🎭 Role Dropdown -->
        <div class="flex items-center gap-3">
          <div
            class="flex items-center gap-3 px-5 py-2 rounded-full border border-purple-400/30 backdrop-blur-md bg-white/5 shadow-lg"
          >
            <span class="text-gray-300 text-sm font-medium">View as:</span>
            <button
              class="rounded-full px-4 py-2 text-sm font-semibold transition-all"
              :class="viewRole === 'organizer' ? 'bg-gradient-to-r from-purple-500 to-pink-500 text-white' : 'text-gray-400 hover:text-white'"
              @click="setRole('organizer')"
            >
              Organizer
            </button>
            <button
              class="rounded-full px-4 py-2 text-sm font-semibold transition-all"
              :class="viewRole === 'participant' ? 'bg-gradient-to-r from-purple-500 to-pink-500 text-white' : 'text-gray-400 hover:text-white'"
              @click="setRole('participant')"
            >
              Participant
            </button>
          </div>
        </div>

        <div class="relative w-72">
        <font-awesome-icon
            :icon="['fas', 'search']"
            class="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 text-base pointer-events-none"
        />
        <input
            v-model="searchQuery"
            type="text"
            placeholder="Search events..."
            class="input-box pl-14 pr-10 text-center w-full text-sm placeholder-gray-400"
        />
        <button
            v-if="searchQuery"
            @click="searchQuery = ''"
            type="button"
            class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-purple-400 text-sm"
        >
            <font-awesome-icon :icon="['fas', 'times-circle']" />
        </button>
        </div>

      </div>

      <!-- 🗓️ Event Type Tabs -->
      <div class="flex overflow-x-auto gap-4 mb-10 pb-2 hide-scrollbar">
        <button
          v-for="tab in tabs"
          :key="tab.key"
          @click="currentTab = tab.key"
          class="px-5 py-2 rounded-full font-semibold transition-all whitespace-nowrap"
          :class="currentTab === tab.key
            ? 'bg-gradient-to-r from-purple-500 to-pink-500 text-white shadow-lg scale-105'
            : 'bg-white/5 border border-purple-400/20 text-gray-300 hover:bg-purple-500/10'"
        >
          {{ tab.label }}
        </button>
      </div>

      <!-- 🎟️ Event Lists -->
      <div v-if="filteredEvents[currentTab].length > 0" class="flex flex-col gap-5">
        <div
          v-for="event in filteredEvents[currentTab]"
          :key="event.eventId"
          class="backdrop-blur-lg bg-white/5 border border-purple-400/20 rounded-2xl p-6 shadow-lg hover:shadow-2xl transition-all duration-300 flex justify-between items-center"
        >
          <div class="flex flex-col gap-2">
            <h3 class="text-lg font-semibold text-purple-300">{{ event.title }}</h3>
            <p class="text-gray-400 text-sm max-w-md line-clamp-2">{{ event.description }}</p>
            <div class="flex gap-6 text-sm mt-2">
              <p><font-awesome-icon :icon="['fas', 'map-marker-alt']" class="text-purple-400" /> {{ event.location }}</p>
              <p><font-awesome-icon :icon="['fas', 'calendar']" class="text-purple-400" /> {{ formatDate(event.eventDate) }}</p>
              <p><font-awesome-icon :icon="['fas', 'clock']" class="text-purple-400" /> {{ event.eventTime || 'TBA' }}</p>
            </div>
          </div>

          <div class="text-right">
            <p class="text-sm text-purple-400 font-medium mb-2">{{ displayAmount(event.amount) }}</p>
            <div class="flex gap-2 justify-end">
              <button
                v-if="viewRole === 'organizer'"
                @click="editEvent(event)"
                class="btn-outline"
              >
                Edit
              </button>
              <button
                v-if="viewRole === 'organizer'"
                @click="deleteEvent(event)"
                class="btn-danger"
              >
                Delete
              </button>
              <router-link
                v-if="viewRole === 'participant'"
                :to="`/events/${event.eventId}`"
                class="btn-primary"
              >
                View
              </router-link>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="text-center text-gray-400 py-20">
        <font-awesome-icon :icon="['fas', 'info-circle']" class="text-4xl mb-4 text-purple-400 animate-pulse" />
        <p class="text-lg">No {{ currentTab }} events found.</p>
      </div>
    </main>

    <!-- 🦶 Footer -->
    <footer class="py-6 text-center text-gray-400 border-t border-purple-400/20 bg-transparent">
      © {{ new Date().getFullYear() }} Eventra — Empower Your Events
    </footer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import axios from "axios";
import { library } from "@fortawesome/fontawesome-svg-core";
import { fas } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
library.add(fas);

const theme = ref(localStorage.getItem("theme") || "dark");
const loggedIn = ref(!!localStorage.getItem("token"));
const dropdownOpen = ref(false);
const userDetails = ref(JSON.parse(localStorage.getItem("user") || "{}"));

const viewRole = ref("organizer");
const searchQuery = ref("");
const currentTab = ref("ongoing");

const tabs = [
  { key: "ongoing", label: "Ongoing" },
  { key: "upcoming", label: "Upcoming" },
  { key: "past", label: "Past" },
];

const events = reactive({
  ongoing: [],
  upcoming: [],
  past: [],
});

const filteredEvents = reactive({
  ongoing: [],
  upcoming: [],
  past: [],
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

const setRole = (role) => {
  viewRole.value = role;
  fetchEvents();
};

const fetchEvents = async () => {
  try {
    const userId = userDetails.value?.user_id;
    const res = await axios.get(
      `http://localhost:8080/api/events/mine?userId=${userId}&role=${viewRole.value}`
    );
    categorize(res.data);
  } catch (err) {
    console.error("Error fetching events:", err);
  }
};

const categorize = (allEvents) => {
  const now = new Date();
  const ongoing = [];
  const upcoming = [];
  const past = [];

  allEvents.forEach((e) => {
    const eventDate = new Date(e.eventDate + "T" + (e.eventTime || "00:00"));
    if (eventDate.toDateString() === now.toDateString()) ongoing.push(e);
    else if (eventDate > now) upcoming.push(e);
    else past.push(e);
  });

  events.ongoing = ongoing;
  events.upcoming = upcoming;
  events.past = past;

  applyFilters();
};

const applyFilters = () => {
  const query = searchQuery.value.toLowerCase();
  ["ongoing", "upcoming", "past"].forEach((key) => {
    filteredEvents[key] = events[key].filter(
      (e) =>
        e.title.toLowerCase().includes(query) ||
        e.description.toLowerCase().includes(query)
    );
  });
};

onMounted(() => {
  if (loggedIn.value) fetchEvents();
});

const formatDate = (date) =>
  new Date(date).toLocaleDateString("en-IN", { year: "numeric", month: "short", day: "numeric" });

const displayAmount = (amt) => (amt > 0 ? `₹${amt}` : "Free");

const editEvent = (e) => (window.location.href = `/create?eventId=${e.eventId}`);
const deleteEvent = async (e) => {
  if (!confirm("Are you sure you want to delete this event?")) return;
  await axios.delete(`http://localhost:8080/api/events/${e.eventId}`);
  fetchEvents();
};

const toggleDropdown = () => (dropdownOpen.value = !dropdownOpen.value);
const logout = () => {
  localStorage.removeItem("token");
  loggedIn.value = false;
  window.location.href = "/auth";
};
</script>

<style scoped>
.input-box {
  background-color: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(168, 85, 247, 0.25);
  border-radius: 9999px;
  padding: 0.6rem 1rem;
  color: inherit;
  outline: none;
  transition: all 0.3s;
}
.input-box:focus {
  border-color: rgba(168, 85, 247, 0.6);
  box-shadow: 0 0 0 3px rgba(168, 85, 247, 0.25);
}
.hide-scrollbar::-webkit-scrollbar {
  display: none;
}
.btn-primary {
  background: linear-gradient(90deg, #7c3aed, #ec4899);
  color: white;
  padding: 0.4rem 1rem;
  border-radius: 9999px;
  font-weight: 600;
  transition: all 0.2s;
}
.btn-outline {
  border: 1px solid rgba(168, 85, 247, 0.3);
  padding: 0.4rem 1rem;
  border-radius: 9999px;
  font-weight: 600;
  transition: all 0.2s;
}
.btn-outline:hover {
  background: rgba(168, 85, 247, 0.15);
}
.btn-danger {
  background: rgba(239, 68, 68, 0.15);
  color: #ef4444;
  padding: 0.4rem 1rem;
  border-radius: 9999px;
  font-weight: 600;
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
