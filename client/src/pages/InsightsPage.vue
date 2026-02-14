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
        <router-link to="/insights" class="nav-link" :class="{ active: $route.path === '/insights' }">
          <font-awesome-icon :icon="['fas', 'chart-line']" /> Insights
        </router-link>

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

        <button
          @click="toggleTheme"
          class="border border-purple-400/30 px-3 md:px-4 py-2 rounded-full transition-all duration-300 text-sm"
        >
          <font-awesome-icon :icon="['fas', theme === 'dark' ? 'sun' : 'moon']" />
        </button>
      </nav>
    </header>

    <!-- 📊 Insights Content -->
    <main class="pt-28 px-6 md:px-16 relative z-20 pb-20">
      <!-- 🔒 Login Required -->
      <template v-if="!loggedIn">
        <div class="flex flex-col items-center justify-center py-32">
          <font-awesome-icon :icon="['fas', 'lock']" class="text-6xl text-purple-400 mb-6 animate-pulse" />
          <h2 class="text-3xl font-bold mb-4">Login Required</h2>
          <p class="text-gray-400 mb-8 max-w-md text-center">
            You must be signed in to view your payment insights and analytics.
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
        <!-- Header with Back Button -->
        <div class="flex items-center justify-between mb-10">
          <div>
            <h1 class="text-4xl md:text-6xl font-bold mb-4">
              Payment <span class="text-purple-400">Insights</span>
            </h1>
            <p class="text-gray-400 max-w-2xl">
              Visualize your payment activities and event performance with interactive charts.
            </p>
          </div>
          <router-link
            to="/payments"
            class="flex items-center gap-2 px-6 py-3 rounded-full border border-purple-400/30 hover:bg-purple-500/20 transition-all"
          >
            <font-awesome-icon :icon="['fas', 'arrow-left']" />
            <span class="hidden md:inline">Back to Payments</span>
          </router-link>
        </div>

        <!-- Summary Cards -->
        <div class="grid md:grid-cols-3 gap-6 mb-12">
          <div class="backdrop-blur-lg bg-gradient-to-br from-green-500/20 to-emerald-500/20 border border-green-400/30 rounded-2xl shadow-lg p-8 flex flex-col items-center justify-center">
            <font-awesome-icon :icon="['fas', 'arrow-down']" class="text-4xl text-green-400 mb-4" />
            <h2 class="text-sm font-medium text-gray-400 mb-2">Total Received</h2>
            <p class="text-5xl font-bold text-green-400">₹{{ totalReceived.toFixed(2) }}</p>
          </div>
          
          <div class="backdrop-blur-lg bg-gradient-to-br from-red-500/20 to-pink-500/20 border border-red-400/30 rounded-2xl shadow-lg p-8 flex flex-col items-center justify-center">
            <font-awesome-icon :icon="['fas', 'arrow-up']" class="text-4xl text-red-400 mb-4" />
            <h2 class="text-sm font-medium text-gray-400 mb-2">Total Spent</h2>
            <p class="text-5xl font-bold text-red-400">₹{{ totalSpent.toFixed(2) }}</p>
          </div>
          
          <div class="backdrop-blur-lg bg-gradient-to-br from-blue-500/20 to-purple-500/20 border border-blue-400/30 rounded-2xl shadow-lg p-8 flex flex-col items-center justify-center">
            <font-awesome-icon :icon="['fas', 'balance-scale']" class="text-4xl text-blue-400 mb-4" />
            <h2 class="text-sm font-medium text-gray-400 mb-2">Net Balance</h2>
            <p class="text-5xl font-bold" :class="netBalance >= 0 ? 'text-green-400' : 'text-red-400'">
              ₹{{ netBalance.toFixed(2) }}
            </p>
          </div>
        </div>

        <!-- Charts Grid -->
        <div class="grid md:grid-cols-2 gap-8">
          <!-- Monthly Payments Received Chart -->
          <div class="backdrop-blur-lg bg-white/5 border border-purple-400/20 rounded-2xl shadow-lg p-6 hover:shadow-2xl transition-all">
            <div class="flex items-center gap-3 mb-4">
              <font-awesome-icon :icon="['fas', 'chart-bar']" class="text-purple-400 text-xl" />
              <h2 class="text-xl font-semibold text-purple-300">Monthly Payments Received</h2>
            </div>
            <canvas id="paymentsReceivedChart"></canvas>
          </div>

          <!-- Monthly Payments Spent Chart -->
          <div class="backdrop-blur-lg bg-white/5 border border-purple-400/20 rounded-2xl shadow-lg p-6 hover:shadow-2xl transition-all">
            <div class="flex items-center gap-3 mb-4">
              <font-awesome-icon :icon="['fas', 'chart-bar']" class="text-pink-400 text-xl" />
              <h2 class="text-xl font-semibold text-purple-300">Monthly Payments Spent</h2>
            </div>
            <canvas id="paymentsSpentChart"></canvas>
          </div>

          <!-- Payments Received by Event Chart -->
          <div class="backdrop-blur-lg bg-white/5 border border-purple-400/20 rounded-2xl shadow-lg p-6 hover:shadow-2xl transition-all">
            <div class="flex items-center gap-3 mb-4">
              <font-awesome-icon :icon="['fas', 'chart-pie']" class="text-green-400 text-xl" />
              <h2 class="text-xl font-semibold text-purple-300">Received by Event</h2>
            </div>
            <canvas id="receivedByEventChart"></canvas>
          </div>

          <!-- Payments Spent by Event Chart -->
          <div class="backdrop-blur-lg bg-white/5 border border-purple-400/20 rounded-2xl shadow-lg p-6 hover:shadow-2xl transition-all">
            <div class="flex items-center gap-3 mb-4">
              <font-awesome-icon :icon="['fas', 'chart-pie']" class="text-red-400 text-xl" />
              <h2 class="text-xl font-semibold text-purple-300">Spent by Event</h2>
            </div>
            <canvas id="spentByEventChart"></canvas>
          </div>
        </div>
      </template>
    </main>

    <!-- 🦶 Footer -->
    <footer class="py-6 text-center text-gray-400 border-t border-purple-400/20 bg-transparent">
      © {{ new Date().getFullYear() }} Eventra — Empower Your Events
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import Chart from 'chart.js/auto';
import { library } from '@fortawesome/fontawesome-svg-core';
import { fas } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import GlobalAlert from '../components/GlobalAlert.vue';
import { useAlert } from '../composables/useAlert';
import { getAuth, onAuthStateChanged } from "firebase/auth";

library.add(fas);

const router = useRouter();
const { alertState, showSuccess, showError, showWarning, showInfo } = useAlert();

const theme = ref(localStorage.getItem('theme') || 'dark');
const loggedIn = ref(!!localStorage.getItem('token'));
const dropdownOpen = ref(false);
const userName = ref('');
const userProfilePic = ref(null);

const paymentsReceivedChart = ref(null);
const paymentsSpentChart = ref(null);
const receivedByEventChart = ref(null);
const spentByEventChart = ref(null);

const totalReceived = ref(0);
const totalSpent = ref(0);

const netBalance = computed(() => totalReceived.value - totalSpent.value);

const applyTheme = () => {
  document.documentElement.classList.toggle('dark', theme.value === 'dark');
  localStorage.setItem('theme', theme.value);
};

const toggleTheme = () => {
  theme.value = theme.value === 'dark' ? 'light' : 'dark';
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

const fetchInsights = async () => {
  try {
    showInfo('Loading insights...', 1000);
    
    const userId = JSON.parse(localStorage.getItem('user')).user_id;
    const token = localStorage.getItem('token');
    
    const response = await axios.get(`/api/payments/insights/${userId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    
    const { monthlyReceived, monthlySpent, totalReceived: received, totalSpent: spent, receivedByEvent, spentByEvent } = response.data;

    renderTotalAmounts(received, spent);
    renderMonthlyCharts(monthlyReceived, monthlySpent);
    renderEventCharts(receivedByEvent, spentByEvent);
    
    showSuccess('Insights loaded successfully!', 2000);
  } catch (err) {
    console.error('Error fetching insights:', err);
    showError('Failed to load insights. Please try again.');
  }
};

const renderTotalAmounts = (received, spent) => {
  totalReceived.value = received;
  totalSpent.value = spent;
};

const renderMonthlyCharts = (monthlyReceivedData, monthlySpentData) => {
  // Monthly Received Chart
  const receivedCtx = document.getElementById('paymentsReceivedChart').getContext('2d');
  if (paymentsReceivedChart.value) paymentsReceivedChart.value.destroy();
  paymentsReceivedChart.value = new Chart(receivedCtx, {
    type: 'bar',
    data: {
      labels: monthlyReceivedData.map(data => data.month),
      datasets: [{
        label: 'Amount Received (₹)',
        data: monthlyReceivedData.map(data => data.amount),
        backgroundColor: 'rgba(34, 197, 94, 0.6)',
        borderColor: 'rgba(34, 197, 94, 1)',
        borderWidth: 2,
        borderRadius: 8
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: true,
      scales: {
        y: {
          beginAtZero: true,
          grid: {
            color: 'rgba(168, 85, 247, 0.1)'
          },
          ticks: {
            color: '#9ca3af'
          }
        },
        x: {
          grid: {
            display: false
          },
          ticks: {
            color: '#9ca3af'
          }
        }
      },
      plugins: {
        legend: {
          labels: {
            color: '#9ca3af'
          }
        }
      }
    }
  });

  // Monthly Spent Chart
  const spentCtx = document.getElementById('paymentsSpentChart').getContext('2d');
  if (paymentsSpentChart.value) paymentsSpentChart.value.destroy();
  paymentsSpentChart.value = new Chart(spentCtx, {
    type: 'bar',
    data: {
      labels: monthlySpentData.map(data => data.month),
      datasets: [{
        label: 'Amount Spent (₹)',
        data: monthlySpentData.map(data => data.amount),
        backgroundColor: 'rgba(236, 72, 153, 0.6)',
        borderColor: 'rgba(236, 72, 153, 1)',
        borderWidth: 2,
        borderRadius: 8
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: true,
      scales: {
        y: {
          beginAtZero: true,
          grid: {
            color: 'rgba(168, 85, 247, 0.1)'
          },
          ticks: {
            color: '#9ca3af'
          }
        },
        x: {
          grid: {
            display: false
          },
          ticks: {
            color: '#9ca3af'
          }
        }
      },
      plugins: {
        legend: {
          labels: {
            color: '#9ca3af'
          }
        }
      }
    }
  });
};

const renderEventCharts = (receivedByEventData, spentByEventData) => {
  // Received by Event Pie Chart
  const receivedByEventCtx = document.getElementById('receivedByEventChart').getContext('2d');
  if (receivedByEventChart.value) receivedByEventChart.value.destroy();
  receivedByEventChart.value = new Chart(receivedByEventCtx, {
    type: 'doughnut',
    data: {
      labels: receivedByEventData.map(data => data.eventTitle),
      datasets: [{
        data: receivedByEventData.map(data => data.amount),
        backgroundColor: [
          'rgba(124, 58, 237, 0.8)',
          'rgba(236, 72, 153, 0.8)',
          'rgba(34, 197, 94, 0.8)',
          'rgba(59, 130, 246, 0.8)',
          'rgba(249, 115, 22, 0.8)',
          'rgba(168, 85, 247, 0.8)'
        ],
        borderWidth: 2,
        borderColor: '#1a1a1a'
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: true,
      plugins: {
        legend: {
          position: 'bottom',
          labels: {
            color: '#9ca3af',
            padding: 15,
            font: {
              size: 11
            }
          }
        }
      }
    }
  });

  // Spent by Event Pie Chart
  const spentByEventCtx = document.getElementById('spentByEventChart').getContext('2d');
  if (spentByEventChart.value) spentByEventChart.value.destroy();
  spentByEventChart.value = new Chart(spentByEventCtx, {
    type: 'doughnut',
    data: {
      labels: spentByEventData.map(data => data.eventTitle),
      datasets: [{
        data: spentByEventData.map(data => data.amount),
        backgroundColor: [
          'rgba(236, 72, 153, 0.8)',
          'rgba(124, 58, 237, 0.8)',
          'rgba(239, 68, 68, 0.8)',
          'rgba(251, 191, 36, 0.8)',
          'rgba(59, 130, 246, 0.8)',
          'rgba(168, 85, 247, 0.8)'
        ],
        borderWidth: 2,
        borderColor: '#1a1a1a'
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: true,
      plugins: {
        legend: {
          position: 'bottom',
          labels: {
            color: '#9ca3af',
            padding: 15,
            font: {
              size: 11
            }
          }
        }
      }
    }
  });
};

const toggleDropdown = () => {
  dropdownOpen.value = !dropdownOpen.value;
};

const logout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  loggedIn.value = false;
  dropdownOpen.value = false;
  userName.value = '';
  userProfilePic.value = null;
  showInfo('Logged out successfully');
  setTimeout(() => {
    router.push('/auth');
  }, 1000);
};

onMounted(() => {
  applyTheme();
  checkUserProfile();
  
  const userData = localStorage.getItem('user');
  if (userData) {
    try {
      const parsed = JSON.parse(userData);
      userName.value = parsed.name || '';
      
      // Set profile picture from user data if available
      if (parsed.profile_pic) {
        userProfilePic.value = parsed.profile_pic;
      }
    } catch (err) {
      console.error('Failed to parse user data:', err);
    }
  }
  
  if (!loggedIn.value) {
    showWarning('Please login to view payment insights');
    return;
  }
  
  fetchInsights();
});
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