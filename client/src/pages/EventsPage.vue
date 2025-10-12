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

    <!-- 🎟️ Event List -->
    <main class="pt-28 px-6 md:px-16 relative z-20">
      <h1 class="text-4xl md:text-6xl font-bold mb-4">
        Explore <span class="text-purple-400">Events</span>
      </h1>
      <p class="text-gray-400 max-w-2xl mb-10">
        Discover upcoming events and register easily.
      </p>

      <!-- 🔒 Login check -->
      <template v-if="!loggedIn">
        <div class="flex flex-col items-center justify-center py-32">
          <font-awesome-icon :icon="['fas', 'lock']" class="text-6xl text-purple-400 mb-6 animate-pulse" />
          <h2 class="text-3xl font-bold mb-4">Login to View Events</h2>
          <p class="text-gray-400 mb-8 max-w-md text-center">
            You must be signed in to explore and register for upcoming events.
          </p>
          <router-link
            to="/auth"
            class="px-6 py-3 rounded-full bg-gradient-to-r from-purple-500 to-pink-500 text-white font-semibold shadow-lg hover:scale-105 hover:shadow-xl transition-all"
          >
            Go to Login
          </router-link>
        </div>
      </template>

      <!-- 🗓️ Event List Cards -->
      <template v-else>
        <div v-if="events.length > 0" class="flex flex-col gap-6">
          <div
            v-for="event in events"
            :key="event.eventId"
            class="backdrop-blur-lg bg-white/5 border border-purple-400/20 rounded-2xl p-6 shadow-lg hover:shadow-2xl transition-all duration-300 flex justify-between items-center"
          >
            <div class="flex flex-col gap-2">
              <h3 class="text-xl font-semibold text-purple-300">{{ event.title }}</h3>
              <p class="text-gray-400 text-sm max-w-md">{{ event.description }}</p>
              <div class="flex gap-8 text-sm mt-2">
                <p><font-awesome-icon :icon="['fas', 'calendar']" class="text-purple-400" /> {{ formatDate(event.date) }}</p>
                <p>
                  <font-awesome-icon :icon="['fas', 'tag']" class="text-purple-400" />
                  <span class="text-purple-400 font-semibold">
                    {{ event.amount && event.amount > 0 ? '₹' + event.amount : 'Free' }}
                  </span>
                </p>
              </div>
            </div>

            <button
              @click="registerEvent(event)"
              class="px-5 py-2 rounded-full font-semibold text-white bg-gradient-to-r from-purple-500 to-pink-500 hover:scale-105 hover:shadow-xl transition-all"
            >
              {{ event.amount && event.amount > 0 ? 'Register' : 'Join' }}
            </button>
          </div>
        </div>

        <div v-else class="text-center text-gray-400 py-24">
          <font-awesome-icon :icon="['fas', 'info-circle']" class="text-4xl mb-4 text-purple-400 animate-pulse" />
          <p class="text-lg">No events available right now.</p>
        </div>
      </template>
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
import { ref, onMounted } from "vue";
import axios from "axios";
import { library } from "@fortawesome/fontawesome-svg-core";
import { fas } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
library.add(fas);

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

  // ✅ Get user details from localStorage
  const userData = localStorage.getItem("user");
  if (userData) {
    try {
      const parsed = JSON.parse(userData);
      userDetails.value = parsed;
    } catch (err) {
      console.error("Failed to parse user data:", err);
    }
  }

  if (loggedIn.value) {
    try {
      const response = await axios.get("http://localhost:8080/api/events");
      events.value = response.data || [];
    } catch (err) {
      console.error("Error fetching events:", err);
    }
  }
});

const formatDate = (dateStr) => {
  if (!dateStr) return "TBA";
  const d = new Date(dateStr);
  return d.toLocaleDateString("en-IN", { year: "numeric", month: "short", day: "numeric" });
};

const registerEvent = async (event) => {
  if (!loggedIn.value) {
    alert("Please log in to register for this event.");
    window.location.href = "/auth";
    return;
  }

  const user = userDetails.value;

  if (event.amount <= 0) {
    // Free event
    try {
      await axios.post(`http://localhost:8080/api/events/${event.eventId}/register-free`, {
        userId: user.user_id,
      });
      alert("Successfully registered for free event!");
    } catch (err) {
      console.error("Error registering free event:", err);
      alert("Failed to register for free event.");
    }
    return;
  }

  // Paid event - Razorpay
  try {
    const response = await axios.post(`http://localhost:8080/api/events/${event.eventId}/create-order`, {
      userId: user.user_id,
    });
    const orderData = response.data;

    const options = {
      key: "rzp_test_XXXXXXXXXXXXXX", // replace with your Razorpay key
      amount: orderData.amount,
      currency: "INR",
      name: "Eventra",
      description: event.title,
      order_id: orderData.order_id,
      handler: async function (paymentResponse) {
        await axios.post("http://localhost:8080/api/events/verify-payment", {
          userId: user.user_id,
          eventId: event.eventId,
          razorpay_order_id: paymentResponse.razorpay_order_id,
          razorpay_payment_id: paymentResponse.razorpay_payment_id,
          razorpay_signature: paymentResponse.razorpay_signature,
        });
        alert("Payment successful! You are registered.");
      },
      prefill: { name: user.name, email: user.email, contact: user.phone_no },
      theme: { color: "#9b5de5" },
    };

    const razor = new window.Razorpay(options);
    razor.open();
  } catch (err) {
    console.error("Error creating order:", err);
    alert("Failed to initiate payment.");
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
