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

    <!-- 🎟️ Event List -->
    <main class="pt-28 px-6 md:px-16 relative z-20 pb-20">
      <h1 class="text-4xl md:text-6xl font-bold mb-4">
        Explore <span class="text-purple-400">Events</span>
      </h1>
      <p class="text-gray-400 max-w-2xl mb-10">
        Discover upcoming events and register easily.
        <span v-if="!loggedIn" class="text-purple-400 font-semibold">Sign in to register for events!</span>
      </p>

      <!-- 🗓️ Event Groups -->
      <div v-if="events.length > 0" class="flex flex-col gap-12">
        
        <!-- 🔴 Ongoing Events -->
        <section v-if="ongoingEvents.length > 0">
          <div class="flex items-center gap-3 mb-6">
            <div class="w-3 h-3 bg-red-500 rounded-full animate-pulse"></div>
            <h2 class="text-2xl font-bold text-red-400">Ongoing Events</h2>
            <span class="text-sm text-gray-400">({{ ongoingEvents.length }})</span>
          </div>
          <div class="flex flex-col gap-6">
            <EventCard 
              v-for="event in ongoingEvents" 
              :key="event.eventId"
              :event="event"
              :loggedIn="loggedIn"
              status="ongoing"
              @register="registerEvent"
            />
          </div>
        </section>

        <!-- 🟢 Upcoming Events -->
        <section v-if="upcomingEvents.length > 0">
          <div class="flex items-center gap-3 mb-6">
            <div class="w-3 h-3 bg-green-500 rounded-full animate-pulse"></div>
            <h2 class="text-2xl font-bold text-green-400">Upcoming Events</h2>
            <span class="text-sm text-gray-400">({{ upcomingEvents.length }})</span>
          </div>
          <div class="flex flex-col gap-6">
            <EventCard 
              v-for="event in upcomingEvents" 
              :key="event.eventId"
              :event="event"
              :loggedIn="loggedIn"
              status="upcoming"
              @register="registerEvent"
            />
          </div>
        </section>

        <!-- ⚫ Past Events -->
        <section v-if="pastEvents.length > 0">
          <div class="flex items-center gap-3 mb-6">
            <div class="w-3 h-3 bg-gray-500 rounded-full"></div>
            <h2 class="text-2xl font-bold text-gray-400">Past Events</h2>
            <span class="text-sm text-gray-400">({{ pastEvents.length }})</span>
          </div>
          <div class="flex flex-col gap-6">
            <EventCard 
              v-for="event in pastEvents" 
              :key="event.eventId"
              :event="event"
              :loggedIn="loggedIn"
              status="past"
              @register="registerEvent"
            />
          </div>
        </section>

      </div>

      <div v-else class="text-center text-gray-400 py-24">
        <font-awesome-icon :icon="['fas', 'info-circle']" class="text-4xl mb-4 text-purple-400 animate-pulse" />
        <p class="text-lg">No events available right now.</p>
      </div>
    </main>

    <!-- ➕ Floating Create Event Button -->
    <router-link
      v-if="loggedIn"
      to="/create"
      class="fixed bottom-8 right-8 flex items-center justify-center w-14 h-14 rounded-full bg-gradient-to-r from-purple-500 to-pink-500 text-white shadow-lg hover:scale-110 hover:shadow-2xl transition-all duration-300 z-50"
    >
      <font-awesome-icon :icon="['fas', 'plus']" class="text-2xl" />
    </router-link>

    <!-- 🦶 Footer -->
    <footer class="py-6 text-center text-gray-400 border-t border-purple-400/20 bg-transparent">
      © {{ new Date().getFullYear() }} Eventra — Empower Your Events
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";
import { library } from "@fortawesome/fontawesome-svg-core";
import { fas } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import GlobalAlert from '../components/GlobalAlert.vue';
import { useAlert } from '../composables/useAlert';
import EventCard from '../components/EventCard.vue';

library.add(fas);

const router = useRouter();
const { alertState, showSuccess, showError, showWarning, showInfo } = useAlert();

const theme = ref(localStorage.getItem("theme") || "dark");
const loggedIn = ref(!!localStorage.getItem("token"));
const dropdownOpen = ref(false);
const events = ref([]);
const userDetails = ref({
  user_id: "",
  name: "",
  email: "",
  phone_no: "",
  gender: "",
  dob: "",
});

// Group events by status
const ongoingEvents = computed(() => {
  const now = new Date();
  return events.value.filter(event => {
    const eventDate = new Date(event.eventTimestamp);
    const eventEndDate = new Date(eventDate.getTime() + 4 * 60 * 60 * 1000); // Assume 4 hour duration
    return eventDate <= now && now <= eventEndDate;
  });
});

const upcomingEvents = computed(() => {
  const now = new Date();
  return events.value.filter(event => {
    const eventDate = new Date(event.eventTimestamp);
    return eventDate > now;
  }).sort((a, b) => new Date(a.eventTimestamp) - new Date(b.eventTimestamp));
});

const pastEvents = computed(() => {
  const now = new Date();
  return events.value.filter(event => {
    const eventDate = new Date(event.eventTimestamp);
    const eventEndDate = new Date(eventDate.getTime() + 4 * 60 * 60 * 1000);
    return eventEndDate < now;
  }).sort((a, b) => new Date(b.eventTimestamp) - new Date(a.eventTimestamp));
});

const applyTheme = () => {
  document.documentElement.classList.toggle("dark", theme.value === "dark");
  localStorage.setItem("theme", theme.value);
};

const toggleTheme = () => {
  theme.value = theme.value === "dark" ? "light" : "dark";
  applyTheme();
};

onMounted(async () => {
  applyTheme();

  // Get user details from localStorage
  const userData = localStorage.getItem("user");
  if (userData) {
    try {
      const parsed = JSON.parse(userData);
      userDetails.value = parsed;
    } catch (err) {
      console.error("Failed to parse user data:", err);
    }
  }

  // Fetch events regardless of login status
  try {
    const response = await axios.get("/api/events");
    events.value = response.data || [];
    
    if (events.value.length > 0) {
      showInfo(`Found ${events.value.length} events`, 2000);
    }
  } catch (err) {
    console.error("Error fetching events:", err);
    showError("Failed to load events. Please try again.");
  }
});

const registerEvent = async (event) => {
  // Check if user is logged in
  if (!loggedIn.value) {
    showWarning("Please sign in to register for events");
    setTimeout(() => {
      router.push("/auth");
    }, 1500);
    return;
  }

  const user = userDetails.value;

  // Check if user is already registered
  try {
    const isRegisteredResponse = await axios.get(`/api/event-participants/is-registered/${event.eventId}/${user.user_id}`);
    if (isRegisteredResponse.data) {
      showInfo("You are already registered for this event!");
      return;
    }
  } catch (err) {
    console.error("Error checking registration status:", err);
    showError("Failed to check registration status. Please try again.");
    return;
  }

  if (event.amount <= 0) {
    // Free event
    try {
      await axios.post(`/api/events/${event.eventId}/register-free`, {
        userId: user.user_id,
      });
      showSuccess("Successfully registered for the event!");
      
      setTimeout(() => {
        router.push("/myevents");
      }, 1500);
    } catch (err) {
      console.error("Error registering free event:", err);
      showError("Failed to register for the event. Please try again.");
    }
    return;
  }

  // Paid event - Razorpay
  try {
    showInfo("Preparing payment...", 1000);
    
    const token = localStorage.getItem("token");
    const response = await axios.post(`/api/events/${event.eventId}/create-order`, {
      userId: user.user_id,
    }, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    const orderData = response.data;
    console.log("Order data:", orderData);

    const options = {
      key: orderData.key,
      amount: orderData.amount,
      currency: "INR",
      name: "Eventra",
      description: event.title,
      order_id: orderData.order_id,
      handler: async function (paymentResponse) {
        const token = localStorage.getItem("token");
        try {
          await axios.post("/api/events/verify-payment", {
            userId: user.user_id,
            eventId: event.eventId,
            razorpay_order_id: paymentResponse.razorpay_order_id,
            razorpay_payment_id: paymentResponse.razorpay_payment_id,
            razorpay_signature: paymentResponse.razorpay_signature,
          }, {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
          showSuccess("Payment successful! You are registered.");
          setTimeout(() => {
            router.push("/myevents");
          }, 1500);
        } catch (err) {
          console.error("Payment verification failed:", err);
          showError("Payment verification failed. Please contact support.");
        }
      },
      prefill: { 
        name: user.name, 
        email: user.email, 
        contact: user.phone_no 
      },
      theme: { color: "#9b5de5" },
      modal: {
        ondismiss: function() {
          showWarning("Payment cancelled");
        }
      }
    };

    console.log("Razorpay options:", options);

    const razor = new window.Razorpay(options);
    razor.open();
  } catch (err) {
    console.error("Error creating order:", err);
    showError("Failed to initiate payment. Please try again.");
  }
};

const toggleDropdown = () => (dropdownOpen.value = !dropdownOpen.value);

const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  loggedIn.value = false;
  dropdownOpen.value = false;
  userDetails.value = {};
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