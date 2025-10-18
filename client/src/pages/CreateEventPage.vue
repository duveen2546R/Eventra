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
        <!-- Auth Controls -->
        <div class="relative">
          <template v-if="loggedIn">
            <button @click="toggleDropdown" class="flex items-center gap-2 border border-purple-400/30 px-3 py-2 rounded-full transition-all duration-300 text-sm hover:bg-purple-600/20">
              <font-awesome-icon :icon="['fas', 'user-circle']" class="text-lg" />
              <span v-if="userDetails.name">{{ userDetails.name }}</span>
            </button>
            <div v-if="dropdownOpen" class="absolute right-0 mt-3 w-36 bg-white/10 backdrop-blur-lg border border-purple-400/30 rounded-xl shadow-lg text-sm z-50">
              <button @click="logout" class="w-full flex items-center gap-2 px-4 py-2 hover:bg-purple-500/20 transition-all rounded-lg text-left">
                <font-awesome-icon :icon="['fas', 'sign-out-alt']" /> Logout
              </button>
            </div>
          </template>
          <template v-else>
            <router-link to="/auth" class="flex items-center gap-2 px-4 md:px-6 py-2 rounded-full text-sm font-semibold text-white bg-gradient-to-r from-purple-500 to-pink-500 shadow-md hover:shadow-xl hover:scale-105 transition-all duration-300">
              Sign In <font-awesome-icon :icon="['fas', 'arrow-right']" />
            </router-link>
          </template>
        </div>
        <!-- Theme Toggle -->
        <button @click="toggleTheme" class="border border-purple-400/30 px-3 md:px-4 py-2 rounded-full transition-all duration-300 text-sm">
          <font-awesome-icon :icon="['fas', theme === 'dark' ? 'sun' : 'moon']" />
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

      <form @submit.prevent="createEvent" class="backdrop-blur-lg bg-white/5 border border-purple-400/20 rounded-2xl shadow-lg p-8 md:p-10 max-w-3xl mx-auto flex flex-col gap-6">
        
        <!-- Title & Description -->
        <div>
          <label class="text-sm text-gray-400">Title</label>
          <input v-model="event.title" type="text" placeholder="Enter event title" class="input-box" required />
        </div>
        <div>
          <label class="text-sm text-gray-400">Description</label>
          <textarea v-model="event.description" placeholder="Enter event description" rows="3" class="input-box resize-none" required></textarea>
        </div>
        
        <!-- MAP WITH SEARCH -->
        <div>
          <label class="text-sm text-gray-400">Set Event Location on Map</label>
          <p class="text-xs text-gray-500 mb-2">Use the search bar on the map or click directly to place a marker.</p>
          <div id="map-container" class="w-full h-80 rounded-xl border border-purple-400/30 mt-2 z-10"></div>
          <div class="grid grid-cols-2 gap-6 mt-4">
            <div>
              <label class="text-sm text-gray-400">Latitude</label>
              <input v-model="event.latitude" type="text" placeholder="Set by map" class="input-box bg-white/5 cursor-not-allowed" readonly />
            </div>
            <div>
              <label class="text-sm text-gray-400">Longitude</label>
              <input v-model="event.longitude" type="text" placeholder="Set by map" class="input-box bg-white/5 cursor-not-allowed" readonly />
            </div>
          </div>
        </div>
        
        <!-- Other Event Details -->
        <div class="grid md:grid-cols-2 gap-6">
          <div>
            <label class="text-sm text-gray-400">Location Name</label>
            <input v-model="event.location" type="text" placeholder="e.g., City Park Amphitheater" class="input-box" required />
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
            <input v-model.number="event.amount" type="number" step="0.01" placeholder="0 for free" class="input-box" required />
          </div>
        </div>
        <div class="grid md:grid-cols-2 gap-6">
          <div>
            <label class="text-sm text-gray-400">Capacity</label>
            <input v-model.number="event.capacity" type="number" placeholder="e.g. 100" class="input-box" required />
          </div>
          <div>
            <label class="text-sm text-gray-400">Remaining Capacity</label>
            <input v-model.number="event.remainingCapacity" type="number" placeholder="e.g. 100" class="input-box" required />
          </div>
        </div>
        <button type="submit" class="mt-4 py-3 rounded-full bg-gradient-to-r from-purple-500 to-pink-500 text-white font-semibold shadow-lg hover:scale-105 hover:shadow-xl transition-all">
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
import { ref, onMounted, nextTick } from "vue";
import { useRouter } from 'vue-router';
import L from 'leaflet';
import { GeoSearchControl, OpenStreetMapProvider } from 'leaflet-geosearch';
import axios from "axios";

// Font Awesome Setup
import { library } from "@fortawesome/fontawesome-svg-core";
import { fas } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
library.add(fas);

// --- FIX FOR LEAFLET ICON PATHS IN VITE ---
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon-2x.png',
  iconUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-shadow.png',
});

// --- COMPONENT STATE ---
const router = useRouter();
const theme = ref(localStorage.getItem("theme") || "dark");
const loggedIn = ref(!!localStorage.getItem("token"));
const userDetails = ref(JSON.parse(localStorage.getItem("user") || "{}"));
const dropdownOpen = ref(false);
let map = null;
let marker = null;
const event = ref({
  title: "",
  description: "",
  location: "",
  latitude: "",
  longitude: "",
  amount: 0,
  capacity: 100,
  status: "ACTIVE",
  eventDate: "",
  eventTime: "",
  remainingCapacity: 100,
});

// --- LIFECYCLE HOOK ---
onMounted(() => {
  applyTheme();
  nextTick(() => { // Wait for the DOM to be ready
    initMap();
  });
});

// --- MAP LOGIC ---
const initMap = () => {
  if (map) return;
  map = L.map('map-container').setView([20.5937, 78.9629], 5);
  updateMapTiles();

  const provider = new OpenStreetMapProvider();
  const searchControl = new GeoSearchControl({
    provider: provider,
    style: 'bar',
    showMarker: false,
    autoClose: true,
  });
  map.addControl(searchControl);

  map.on('click', onMapClick);
  map.on('geosearch/showlocation', onSearchResult);

  setTimeout(() => map.invalidateSize(), 100);
};

const updateMapTiles = () => {
    if(!map) return;
    map.eachLayer(layer => {
        if (layer instanceof L.TileLayer) {
            map.removeLayer(layer);
        }
    });
    const tileUrl = theme.value === 'dark'
        ? 'https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png'
        : 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
    L.tileLayer(tileUrl, { maxZoom: 19, attribution: '© OpenStreetMap & © CARTO' }).addTo(map);
}

const onMapClick = (e) => updateLocation(e.latlng);
const onSearchResult = (e) => updateLocation({ lat: e.location.y, lng: e.location.x });

const updateLocation = (latLng) => {
  const { lat, lng } = latLng;
  event.value.latitude = lat.toFixed(6);
  event.value.longitude = lng.toFixed(6);
  if (marker) {
    marker.setLatLng(latLng);
  } else {
    marker = L.marker(latLng).addTo(map);
  }
  map.setView(latLng, 13);
};

// --- API & FORM LOGIC ---
const createEvent = async () => {
  try {
    const token = localStorage.getItem("token");
    if (!token) {
      alert("You must be logged in to create an event.");
      router.push('/auth');
      return;
    }
    const eventTimestamp = `${event.value.eventDate}T${event.value.eventTime}`;

    // 2. Build the exact payload that the Spring Boot backend @RequestBody expects.
    // This payload matches the fields in your Event.java entity.
    const payload = {
      title: event.value.title,
      description: event.value.description,
      location: event.value.location,
      latitude: event.value.latitude,
      longitude: event.value.longitude,
      amount: event.value.amount,
      capacity: event.value.capacity, 
      status: event.value.status,
      eventTimestamp: eventTimestamp, // The new combined timestamp field
    };
    // ------------------------------------

    // 3. Send the request to the correct endpoint
    await axios.post("/api/events", payload, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    alert("🎉 Event created successfully!");
    router.push("/myevents"); // Redirect to a relevant page after creation

  } catch (err) {
    console.error("Error creating event:", err.response?.data || err.message);
    alert("Failed to create event. Please check the form and try again. See console for details.");
  }
};

// --- THEME & NAVBAR LOGIC ---
const applyTheme = () => {
  document.documentElement.classList.toggle("dark", theme.value === "dark");
  localStorage.setItem("theme", theme.value);
};

const toggleTheme = () => {
  theme.value = theme.value === "dark" ? "light" : "dark";
  applyTheme();
  updateMapTiles(); // Update map tiles when theme changes
};

const toggleDropdown = () => dropdownOpen.value = !dropdownOpen.value;

const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  loggedIn.value = false;
  router.push("/auth");
};
</script>

<style>
/* --- Styles for Leaflet Search Bar --- */
.leaflet-control-geosearch {
  position: absolute !important;
  top: 12px !important;
  left: 50% !important;
  transform: translateX(-50%) !important;
  z-index: 9999 !important;
}

.geosearch form {
  background: rgba(255, 255, 255, 0.9) !important;
  border-radius: 8px !important;
  border: 1px solid rgba(168, 85, 247, 0.4) !important;
  padding: 4px 8px !important;
}

.geosearch input {
  background: transparent !important;
  color: #111 !important;
  font-size: 14px !important;
  width: 200px !important;
}

.dark .geosearch form {
  background: rgba(30, 30, 30, 0.9) !important;
  border: 1px solid rgba(168, 85, 247, 0.4) !important;
}

.dark .geosearch input {
  color: #f5f5f5 !important;
}

.geosearch .results {
  background: rgba(30, 30, 30, 0.95) !important;
  color: #fff !important;
  border-radius: 6px !important;
  border: 1px solid rgba(168, 85, 247, 0.3) !important;
}
.light .geosearch .results {
  background: #fff !important;
  color: #111 !important;
}
</style>

<style scoped>
/* All your beautiful, existing component-specific styles go here */
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