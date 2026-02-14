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

    <!-- 💳 Main Content -->
    <main class="pt-28 px-6 md:px-16 relative z-20 pb-20">
      <!-- 🔒 Login Required -->
      <template v-if="!loggedIn">
        <div class="flex flex-col items-center justify-center py-32">
          <font-awesome-icon :icon="['fas', 'lock']" class="text-6xl text-purple-400 mb-6 animate-pulse" />
          <h2 class="text-3xl font-bold mb-4">Login Required</h2>
          <p class="text-gray-400 mb-8 max-w-md text-center">
            You must be signed in to access your payment history and manage transactions.
          </p>
          <router-link
            to="/auth"
            class="px-6 py-3 rounded-full bg-gradient-to-r from-purple-500 to-pink-500 text-white font-semibold shadow-lg hover:scale-105 hover:shadow-xl transition-all"
          >
            Go to Login
          </router-link>
        </div>
      </template>

      <!-- Logged In Content -->
      <template v-else>
        <div class="mb-10">
          <div class="flex items-center justify-between mb-4">
            <div>
              <h1 class="text-4xl md:text-6xl font-bold mb-4">
                Your <span class="text-purple-400">Payments</span>
              </h1>
              <p class="text-gray-400 max-w-2xl">
                Track your event payments and transaction history.
              </p>
            </div>
            
            <!-- Insights Button -->
            <router-link
              to="/insights"
              class="flex items-center gap-2 px-6 py-3 rounded-full bg-gradient-to-r from-blue-500 to-purple-500 text-white font-semibold shadow-lg hover:scale-105 hover:shadow-xl transition-all"
            >
              <font-awesome-icon :icon="['fas', 'chart-line']" />
              <span class="hidden md:inline">View Insights</span>
            </router-link>
          </div>
          
          <!-- Summary Cards -->
          <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mt-8">
            <div class="backdrop-blur-lg bg-gradient-to-br from-purple-500/20 to-pink-500/20 border border-purple-400/30 rounded-2xl p-6 shadow-lg">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm text-gray-400 mb-1">Total Received</p>
                  <p class="text-3xl font-bold text-purple-300">₹{{ totalReceived.toFixed(2) }}</p>
                </div>
                <div class="w-12 h-12 bg-green-500/20 rounded-full flex items-center justify-center">
                  <font-awesome-icon :icon="['fas', 'arrow-down']" class="text-green-400 text-xl" />
                </div>
              </div>
            </div>
            
            <div class="backdrop-blur-lg bg-gradient-to-br from-pink-500/20 to-purple-500/20 border border-purple-400/30 rounded-2xl p-6 shadow-lg">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm text-gray-400 mb-1">Total Spent</p>
                  <p class="text-3xl font-bold text-pink-300">₹{{ totalSpent.toFixed(2) }}</p>
                </div>
                <div class="w-12 h-12 bg-red-500/20 rounded-full flex items-center justify-center">
                  <font-awesome-icon :icon="['fas', 'arrow-up']" class="text-red-400 text-xl" />
                </div>
              </div>
            </div>
            
            <div class="backdrop-blur-lg bg-gradient-to-br from-blue-500/20 to-purple-500/20 border border-purple-400/30 rounded-2xl p-6 shadow-lg">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm text-gray-400 mb-1">Total Transactions</p>
                  <p class="text-3xl font-bold text-blue-300">{{ totalTransactions }}</p>
                </div>
                <div class="w-12 h-12 bg-blue-500/20 rounded-full flex items-center justify-center">
                  <font-awesome-icon :icon="['fas', 'list']" class="text-blue-400 text-xl" />
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Payments Received -->
        <section class="mb-12">
          <div class="flex items-center gap-3 mb-6">
            <h2 class="text-3xl font-bold text-green-400">Payments Received</h2>
            <span class="text-sm text-gray-400">({{ payments.received.length }})</span>
          </div>
          
          <div v-if="payments.received.length > 0" class="grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
            <div
              v-for="payment in payments.received"
              :key="payment.paymentId"
              class="backdrop-blur-lg bg-white/5 border border-purple-400/20 rounded-2xl p-6 shadow-xl hover:scale-105 transition-all duration-300 hover:shadow-2xl"
            >
              <div class="flex items-center justify-between mb-4">
                <h3 class="text-lg font-semibold text-purple-300 flex items-center gap-2">
                  <font-awesome-icon :icon="['fas', 'calendar-check']" />
                  {{ payment.event.title }}
                </h3>
                <span
                  class="px-3 py-1 rounded-full text-xs font-medium"
                  :class="{
                    'bg-green-500/20 text-green-400': payment.status === 'SUCCESS',
                    'bg-yellow-500/20 text-yellow-400': payment.status === 'PENDING',
                    'bg-red-500/20 text-red-400': payment.status === 'FAILED'
                  }"
                >
                  {{ payment.status }}
                </span>
              </div>

              <div class="text-sm text-gray-400 space-y-2">
                <p class="flex items-center gap-2">
                  <font-awesome-icon :icon="['fas', 'money-bill-wave']" class="text-green-400" />
                  <span class="text-white font-semibold text-lg">₹{{ payment.amount.toFixed(2) }}</span>
                </p>
                <p class="flex items-center gap-2">
                  <font-awesome-icon :icon="['fas', 'clock']" class="text-purple-400" />
                  <span class="text-white">{{ formatDate(payment.paidAt) }}</span>
                </p>
                <p class="flex items-center gap-2">
                  <font-awesome-icon :icon="['fas', 'id-card']" class="text-purple-400" />
                  <span class="text-gray-300 text-xs">{{ payment.paymentId }}</span>
                </p>
              </div>
            </div>
          </div>
          
          <div v-else class="text-center text-gray-400 py-10 backdrop-blur-lg bg-white/5 border border-purple-400/20 rounded-2xl">
            <font-awesome-icon :icon="['fas', 'inbox']" class="text-4xl mb-4 text-purple-400 animate-pulse" />
            <p class="text-lg">No payments received yet.</p>
            <p class="text-sm mt-2">Payments from event registrations will appear here.</p>
          </div>
        </section>

        <!-- Payments Made -->
        <section>
          <div class="flex items-center gap-3 mb-6">
            <h2 class="text-3xl font-bold text-pink-400">Payments Made</h2>
            <span class="text-sm text-gray-400">({{ payments.made.length }})</span>
          </div>
          
          <div v-if="payments.made.length > 0" class="grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
            <div
              v-for="payment in payments.made"
              :key="payment.paymentId"
              class="backdrop-blur-lg bg-white/5 border border-purple-400/20 rounded-2xl p-6 shadow-xl hover:scale-105 transition-all duration-300 hover:shadow-2xl"
            >
              <div class="flex items-center justify-between mb-4">
                <h3 class="text-lg font-semibold text-purple-300 flex items-center gap-2">
                  <font-awesome-icon :icon="['fas', 'calendar-check']" />
                  {{ payment.event.title }}
                </h3>
                <span
                  class="px-3 py-1 rounded-full text-xs font-medium"
                  :class="{
                    'bg-green-500/20 text-green-400': payment.status === 'SUCCESS',
                    'bg-yellow-500/20 text-yellow-400': payment.status === 'PENDING',
                    'bg-red-500/20 text-red-400': payment.status === 'FAILED'
                  }"
                >
                  {{ payment.status }}
                </span>
              </div>

              <div class="text-sm text-gray-400 space-y-2">
                <p class="flex items-center gap-2">
                  <font-awesome-icon :icon="['fas', 'money-bill-wave']" class="text-pink-400" />
                  <span class="text-white font-semibold text-lg">₹{{ payment.amount.toFixed(2) }}</span>
                </p>
                <p class="flex items-center gap-2">
                  <font-awesome-icon :icon="['fas', 'clock']" class="text-purple-400" />
                  <span class="text-white">{{ formatDate(payment.paidAt) }}</span>
                </p>
                <p class="flex items-center gap-2">
                  <font-awesome-icon :icon="['fas', 'id-card']" class="text-purple-400" />
                  <span class="text-gray-300 text-xs">{{ payment.paymentId }}</span>
                </p>
              </div>
            </div>
          </div>
          
          <div v-else class="text-center text-gray-400 py-10 backdrop-blur-lg bg-white/5 border border-purple-400/20 rounded-2xl">
            <font-awesome-icon :icon="['fas', 'inbox']" class="text-4xl mb-4 text-purple-400 animate-pulse" />
            <p class="text-lg">No payments made yet.</p>
            <p class="text-sm mt-2">Register for paid events to see your payment history here.</p>
          </div>
        </section>
      </template>
    </main>

    <!-- 🦶 Footer -->
    <footer class="py-6 text-center text-gray-400 border-t border-purple-400/20 bg-transparent">
      © {{ new Date().getFullYear() }} Eventra — Empower Your Events
    </footer>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";
import { library } from "@fortawesome/fontawesome-svg-core";
import { fas } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import GlobalAlert from '../components/GlobalAlert.vue';
import { useAlert } from '../composables/useAlert';
import { getAuth, onAuthStateChanged } from "firebase/auth";

library.add(fas);

const router = useRouter();
const { alertState, showSuccess, showError, showWarning, showInfo } = useAlert();

const theme = ref(localStorage.getItem("theme") || "dark");
const loggedIn = ref(!!localStorage.getItem("token"));
const dropdownOpen = ref(false);
const userName = ref("");
const userProfilePic = ref(null);
const payments = reactive({ made: [], received: [] });

// Computed totals
const totalReceived = computed(() => {
  return payments.received.reduce((sum, payment) => {
    return payment.status === 'SUCCESS' ? sum + payment.amount : sum;
  }, 0);
});

const totalSpent = computed(() => {
  return payments.made.reduce((sum, payment) => {
    return payment.status === 'SUCCESS' ? sum + payment.amount : sum;
  }, 0);
});

const totalTransactions = computed(() => {
  return payments.made.length + payments.received.length;
});

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

const formatDate = (dateStr) => {
  if (!dateStr) return "N/A";
  const date = new Date(dateStr);
  return date.toLocaleDateString("en-IN", { 
    year: "numeric", 
    month: "short", 
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit"
  });
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
    } catch (err) {
      console.error("Failed to parse user data:", err);
      showError("Failed to load user data");
    }
  }

  if (!loggedIn.value) {
    showWarning("Please login to view your payment history");
    return;
  }

  try {
    showInfo("Loading payment history...", 1000);
    
    const token = localStorage.getItem("token");
    const userId = JSON.parse(localStorage.getItem("user")).user_id;
    
    const response = await axios.get("/api/payments", {
      params: { userId },
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    
    payments.made = response.data.madePayments || [];
    payments.received = response.data.receivedPayments || [];
    
    const total = payments.made.length + payments.received.length;
    if (total > 0) {
      showSuccess(`Loaded ${total} transaction${total !== 1 ? 's' : ''}`, 2000);
    } else {
      showInfo("No payment history found", 2000);
    }
  } catch (err) {
    console.error("Error fetching payments:", err);
    const errorMessage = err.response?.data?.message || "Failed to load payment history";
    showError(errorMessage);
  }
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
</style>