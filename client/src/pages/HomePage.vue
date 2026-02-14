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
          v-if="loggedIn"
          to="/myevents"
          class="nav-link"
          :class="{ active: $route.path === '/myevents' }"
        >
          <font-awesome-icon :icon="['fas', 'star']" /> My Events
        </router-link>

        <router-link
          v-if="loggedIn"
          to="/payments"
          class="nav-link"
          :class="{ active: $route.path === '/payments' }"
        >
          <font-awesome-icon :icon="['fas', 'credit-card']" /> Payments
        </router-link>

        <router-link to="/insights" class="nav-link" :class="{ active: $route.path === '/insights' }">
          <font-awesome-icon :icon="['fas', 'chart-line']" /> Insights
        </router-link>

        <!-- 👤 Auth Controls -->
        <div class="relative">
          <template v-if="loggedIn">
            <button
              @click="toggleDropdown"
              class="flex items-center gap-2 border border-purple-400/30 px-3 py-2 rounded-full transition-all duration-300 text-sm hover:bg-purple-600/20"
            >
              <!-- Profile Picture or Icon -->
              <img 
                v-if="userProfilePic" 
                :src="userProfilePic" 
                alt="Profile" 
                class="profile-pic-small"
                @error="handleImageError"
              />
              <font-awesome-icon v-else :icon="['fas', 'user-circle']" class="text-lg" />
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
    <main class="pt-28 px-6 md:px-16 relative z-20 pb-20">
      <div class="text-center mb-16">
        <h1 class="text-5xl md:text-7xl font-bold mb-6">
          Welcome <span v-if="loggedIn && userName">{{ userName }}</span> to <br/>
          <span class="text-transparent bg-clip-text bg-gradient-to-r from-purple-400 to-pink-400">Eventra</span>
        </h1>
        <p class="text-gray-400 text-lg max-w-3xl mx-auto mb-8">
          Discover, manage, and celebrate your events in one powerful dashboard. 
          <span v-if="!loggedIn" class="text-purple-400">Sign in to get started!</span>
        </p>
        
        <div v-if="!loggedIn" class="flex justify-center gap-4 mt-8">
          <router-link
            to="/auth"
            class="px-8 py-4 rounded-full bg-gradient-to-r from-purple-500 to-pink-500 text-white font-semibold shadow-lg hover:scale-105 hover:shadow-xl transition-all text-lg"
          >
            Get Started
          </router-link>
          <router-link
            to="/events"
            class="px-8 py-4 rounded-full border-2 border-purple-400/50 text-purple-300 font-semibold hover:bg-purple-500/20 transition-all text-lg"
          >
            Browse Events
          </router-link>
        </div>
      </div>

      <!-- Quick Stats (Only for logged in users) -->
      <div v-if="loggedIn" class="grid md:grid-cols-4 gap-6 mb-16">
        <div class="backdrop-blur-lg bg-gradient-to-br from-purple-500/20 to-pink-500/20 border border-purple-400/30 rounded-2xl p-6 shadow-lg hover:scale-105 transition-all">
          <div class="flex items-center justify-between mb-2">
            <div class="w-12 h-12 bg-purple-500/20 rounded-full flex items-center justify-center">
              <font-awesome-icon :icon="['fas', 'calendar-check']" class="text-purple-400 text-xl" />
            </div>
            <span class="text-3xl font-bold text-purple-300">{{ stats.upcomingEvents }}</span>
          </div>
          <p class="text-sm text-gray-400">Upcoming Events</p>
        </div>

        <div class="backdrop-blur-lg bg-gradient-to-br from-green-500/20 to-emerald-500/20 border border-green-400/30 rounded-2xl p-6 shadow-lg hover:scale-105 transition-all">
          <div class="flex items-center justify-between mb-2">
            <div class="w-12 h-12 bg-green-500/20 rounded-full flex items-center justify-center">
              <font-awesome-icon :icon="['fas', 'users']" class="text-green-400 text-xl" />
            </div>
            <span class="text-3xl font-bold text-green-300">{{ stats.organizing }}</span>
          </div>
          <p class="text-sm text-gray-400">Organizing</p>
        </div>

        <div class="backdrop-blur-lg bg-gradient-to-br from-blue-500/20 to-cyan-500/20 border border-blue-400/30 rounded-2xl p-6 shadow-lg hover:scale-105 transition-all">
          <div class="flex items-center justify-between mb-2">
            <div class="w-12 h-12 bg-blue-500/20 rounded-full flex items-center justify-center">
              <font-awesome-icon :icon="['fas', 'ticket-alt']" class="text-blue-400 text-xl" />
            </div>
            <span class="text-3xl font-bold text-blue-300">{{ stats.registered }}</span>
          </div>
          <p class="text-sm text-gray-400">Registered</p>
        </div>

        <div class="backdrop-blur-lg bg-gradient-to-br from-pink-500/20 to-red-500/20 border border-pink-400/30 rounded-2xl p-6 shadow-lg hover:scale-105 transition-all">
          <div class="flex items-center justify-between mb-2">
            <div class="w-12 h-12 bg-pink-500/20 rounded-full flex items-center justify-center">
              <font-awesome-icon :icon="['fas', 'check-circle']" class="text-pink-400 text-xl" />
            </div>
            <span class="text-3xl font-bold text-pink-300">{{ stats.completed }}</span>
          </div>
          <p class="text-sm text-gray-400">Completed</p>
        </div>
      </div>

      <!-- Feature Cards -->
      <h2 class="text-3xl font-bold mb-8 text-center">
        Explore <span class="text-purple-400">Features</span>
      </h2>
      
      <div class="grid md:grid-cols-3 gap-8 mb-12">
        <router-link 
          v-for="card in featureCards" 
          :key="card.title"
          :to="card.link" 
          class="feature-card group"
          @click="handleCardClick(card.title)"
        >
          <div class="icon-wrapper">
            <div class="icon">
              <font-awesome-icon :icon="card.icon" />
            </div>
          </div>
          <h4>{{ card.title }}</h4>
          <p>{{ card.description }}</p>
          <div class="learn-more">
            <span>Explore</span>
            <font-awesome-icon :icon="['fas', 'arrow-right']" class="ml-2 group-hover:translate-x-1 transition-transform" />
          </div>
        </router-link>
      </div>

      <!-- CTA Section -->
      <div class="backdrop-blur-lg bg-gradient-to-r from-purple-500/20 to-pink-500/20 border border-purple-400/30 rounded-3xl p-12 text-center">
        <h2 class="text-4xl font-bold mb-4">
          Ready to Create Your Event?
        </h2>
        <p class="text-gray-400 text-lg mb-8 max-w-2xl mx-auto">
          Start organizing amazing events today. Our platform makes it easy to manage everything from registration to payments.
        </p>
        <router-link
          v-if="loggedIn"
          to="/create"
          class="inline-flex items-center gap-2 px-8 py-4 rounded-full bg-gradient-to-r from-purple-500 to-pink-500 text-white font-semibold shadow-lg hover:scale-105 hover:shadow-xl transition-all text-lg"
        >
          <font-awesome-icon :icon="['fas', 'plus']" />
          Create Event
        </router-link>
        <router-link
          v-else
          to="/auth"
          class="inline-flex items-center gap-2 px-8 py-4 rounded-full bg-gradient-to-r from-purple-500 to-pink-500 text-white font-semibold shadow-lg hover:scale-105 hover:shadow-xl transition-all text-lg"
        >
          Sign In to Create
        </router-link>
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
import { useRouter } from "vue-router";
import axios from "axios";
import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { fas } from "@fortawesome/free-solid-svg-icons";
import GlobalAlert from '../components/GlobalAlert.vue';
import { useAlert } from '../composables/useAlert';
import { getAuth, onAuthStateChanged } from "firebase/auth";

library.add(fas);

const router = useRouter();
const { alertState, showSuccess, showError, showInfo, showWarning } = useAlert();

const theme = ref(localStorage.getItem("theme") || "dark");
const loggedIn = ref(!!localStorage.getItem("token"));
const dropdownOpen = ref(false);
const userName = ref("");
const userProfilePic = ref(null);

const stats = ref({
  upcomingEvents: 0,
  organizing: 0,
  registered: 0,
  completed: 0
});

const featureCards = [
  { 
    icon: ['fas', 'calendar-check'], 
    title: "Upcoming Events", 
    description: "Stay updated with your upcoming schedules and never miss an event.",
    link: "/myevents?role=participant&tab=upcoming"
  },
  { 
    icon: ['fas', 'users'], 
    title: "Organize Events", 
    description: "Create and manage your events with powerful organizational tools.",
    link: "/myevents?role=organizer"
  },
  { 
    icon: ['fas', 'chart-line'], 
    title: "Insights & Analytics", 
    description: "Track your event performance and payment analytics with beautiful charts.",
    link: "/insights"
  },
  { 
    icon: ['fas', 'credit-card'], 
    title: "Payment History", 
    description: "View all your transactions and manage event payments seamlessly.",
    link: "/payments"
  },
  { 
    icon: ['fas', 'search'], 
    title: "Discover Events", 
    description: "Browse and register for exciting events happening around you.",
    link: "/events"
  },
  { 
    icon: ['fas', 'map-marker-alt'], 
    title: "Event Locations", 
    description: "Find events near you with interactive maps and location search.",
    link: "/events"
  }
];

const applyTheme = () => {
  document.documentElement.classList.toggle("dark", theme.value === "dark");
  localStorage.setItem("theme", theme.value);
};

const toggleTheme = () => {
  theme.value = theme.value === "dark" ? "light" : "dark";
  applyTheme();
};

// Check for user profile picture
const checkUserProfile = () => {
  const auth = getAuth();
  
  onAuthStateChanged(auth, (user) => {
    if (user && user.photoURL) {
      userProfilePic.value = user.photoURL;
    } else {
      // Check localStorage for saved user data
      const savedUser = localStorage.getItem('user');
      if (savedUser) {
        try {
          const userData = JSON.parse(savedUser);
          if (userData.profile_pic) {
            userProfilePic.value = userData.profile_pic;
          }
        } catch (e) {
          console.error('Error parsing user data:', e);
        }
      } else {
        userProfilePic.value = null;
      }
    }
  });
};

// Handle image load errors
const handleImageError = () => {
  userProfilePic.value = null;
};

const fetchStats = async () => {
  if (!loggedIn.value) return;
  
  try {
    const userId = JSON.parse(localStorage.getItem('user')).user_id;
    const token = localStorage.getItem('token');
    
    // Fetch events as organizer
    const organizerResponse = await axios.get("/api/events/mine", {
      params: { userId, role: 'organizer' },
      headers: { Authorization: `Bearer ${token}` }
    });
    
    // Fetch events as participant
    const participantResponse = await axios.get("/api/events/mine", {
      params: { userId, role: 'participant' },
      headers: { Authorization: `Bearer ${token}` }
    });
    
    const now = new Date();
    
    // Count organizing events
    stats.value.organizing = organizerResponse.data.length;
    
    // Count registered events
    stats.value.registered = participantResponse.data.length;
    
    // Count upcoming events (both organizer and participant)
    const allEvents = [...organizerResponse.data, ...participantResponse.data];
    stats.value.upcomingEvents = allEvents.filter(e => new Date(e.eventTimestamp) > now).length;
    
    // Count completed events
    stats.value.completed = allEvents.filter(e => {
      const eventDate = new Date(e.eventTimestamp);
      const eventEndDate = new Date(eventDate.getTime() + 4 * 60 * 60 * 1000);
      return eventEndDate < now;
    }).length;
    
  } catch (err) {
    console.error('Error fetching stats:', err);
  }
};

const handleCardClick = (title) => {
  if (loggedIn.value) {
    showInfo(`Opening ${title}...`, 1000);
  } else {
    showWarning("Sign in to access this feature");
  }
};

onMounted(async () => {
  applyTheme();
  checkUserProfile();

  const userData = localStorage.getItem("user");
  if (userData) {
    try {
      const parsed = JSON.parse(userData);
      userName.value = parsed.name || "";
      
      // Set profile picture from user data if available
      if (parsed.profile_pic) {
        userProfilePic.value = parsed.profile_pic;
      }
      
      showSuccess(`Welcome back, ${userName.value}!`, 2000);
    } catch (err) {
      console.error("Failed to parse user data:", err);
    }
  }
  
  await fetchStats();
});

const toggleDropdown = () => {
  dropdownOpen.value = !dropdownOpen.value;
};

const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  loggedIn.value = false;
  dropdownOpen.value = false;
  userName.value = "";
  userProfilePic.value = null;
  stats.value = { upcomingEvents: 0, organizing: 0, registered: 0, completed: 0 };
  showInfo("Logged out successfully");
  setTimeout(() => {
    router.push("/auth");
  }, 1000);
};
</script>

<style>
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
</style>

<style scoped>
/* Profile Picture Styles */
.profile-pic-small {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(168, 85, 247, 0.4);
  transition: all 0.3s ease;
}

.profile-pic-small:hover {
  border-color: rgba(168, 85, 247, 0.8);
  transform: scale(1.1);
}

.dark .profile-pic-small {
  border-color: rgba(255, 255, 255, 0.3);
}

.dark .profile-pic-small:hover {
  border-color: rgba(255, 255, 255, 0.6);
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

/* Feature Cards */
.feature-card {
  backdrop-filter: blur(12px);
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(168, 85, 247, 0.2);
  border-radius: 1.5rem;
  padding: 2rem;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  cursor: pointer;
}

.feature-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 20px 60px rgba(168, 85, 247, 0.3);
  border-color: rgba(236, 72, 153, 0.4);
  background: rgba(168, 85, 247, 0.1);
}

.icon-wrapper {
  margin-bottom: 1.5rem;
}

.icon {
  width: 4rem;
  height: 4rem;
  background: linear-gradient(135deg, rgba(168, 85, 247, 0.2), rgba(236, 72, 153, 0.2));
  border: 2px solid rgba(168, 85, 247, 0.3);
  border-radius: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  color: #a855f7;
  transition: all 0.3s ease;
}

.feature-card:hover .icon {
  transform: scale(1.1) rotate(5deg);
  background: linear-gradient(135deg, rgba(168, 85, 247, 0.3), rgba(236, 72, 153, 0.3));
  border-color: rgba(236, 72, 153, 0.5);
  box-shadow: 0 0 20px rgba(168, 85, 247, 0.4);
}

.feature-card h4 {
  font-size: 1.25rem;
  font-weight: 700;
  margin-bottom: 0.75rem;
  background: linear-gradient(to right, #a855f7, #ec4899);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.feature-card p {
  color: #9ca3af;
  font-size: 0.875rem;
  line-height: 1.5;
  margin-bottom: 1rem;
}

.learn-more {
  display: flex;
  align-items: center;
  color: #a855f7;
  font-weight: 600;
  font-size: 0.875rem;
  margin-top: auto;
}

.feature-card:hover .learn-more {
  color: #ec4899;
}
</style>