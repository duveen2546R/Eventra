<template>
  <div
    class="relative min-h-screen w-screen flex flex-col transition-all duration-700"
    :class="theme === 'dark' ? 'bg-[#0a0a0a] text-white' : 'bg-gray-100 text-gray-900'"
  >
    <!-- 🌫 Background Fog & Light -->
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

    <!-- 🧩 Auth Section -->
    <main
      class="relative flex-1 grid md:grid-cols-2 items-center justify-center px-6 md:px-12 z-10"
    >
      <!-- 🎥 Video Side -->
      <div
        class="hidden md:flex items-center justify-center transition-all duration-700"
        :class="isRegister ? 'order-2' : 'order-1'"
      >
        <video autoplay loop muted playsinline class="w-full h-auto max-h-[80vh] object-contain rounded-3xl">
          <source src="@/assets/auth.webm" type="video/webm" />
        </video>
      </div>

      <!-- 🧾 Form Side -->
      <div
        class="flex flex-col justify-center items-center transition-all duration-700"
        :class="isRegister ? 'order-1' : 'order-2'"
      >
        <div
          class="relative bg-white/10 dark:bg-[#141414]/60 backdrop-blur-xl p-8 md:p-10 rounded-3xl shadow-2xl border border-purple-400/20 w-full max-w-md transition-all duration-700"
        >
          <Motion
            :initial="{ opacity: 0, y: 30 }"
            :animate="{ opacity: 1, y: 0 }"
            :transition="{ duration: 0.8 }"
          >
            <h2 class="text-3xl font-bold mb-6 text-center text-transparent bg-clip-text bg-gradient-to-r from-purple-400 to-pink-400">
              {{ isRegister ? 'Create Account' : 'Welcome Back' }}
            </h2>

            <!-- LOGIN FORM -->
            <form
              v-if="!isRegister"
              @submit.prevent="handleLogin"
              class="flex flex-col gap-4"
            >
              <div class="relative">
                <input
                  type="email"
                  v-model="loginForm.email"
                  placeholder="Email Address"
                  class="auth-input"
                  :class="{ 'input-error': loginErrors.email }"
                  required
                />
                <p v-if="loginErrors.email" class="error-message">{{ loginErrors.email }}</p>
              </div>
              <div class="relative">
                <input
                  :type="showLoginPassword ? 'text' : 'password'"
                  v-model="loginForm.password"
                  placeholder="Password"
                  class="auth-input pr-10"
                  :class="{ 'input-error': loginErrors.password }"
                  required
                />
                <button
                  type="button"
                  @click="toggleLoginPasswordVisibility"
                  class="password-toggle-button"
                  aria-label="Toggle password visibility"
                >
                  <font-awesome-icon :icon="['fas', showLoginPassword ? 'eye-slash' : 'eye']" />
                </button>
                <p v-if="loginErrors.password" class="error-message">{{ loginErrors.password }}</p>
              </div>

              <button type="submit" class="auth-button">
                <font-awesome-icon :icon="['fas', 'sign-in-alt']" /> Sign In
              </button>
            </form>

            <!-- REGISTER FORM -->
            <form
              v-else
              @submit.prevent="handleRegister"
              class="flex flex-col gap-4"
            >
              <div class="relative">
                <input type="text" v-model="registerForm.name" placeholder="Full Name" class="auth-input" :class="{ 'input-error': registerErrors.name }" required />
                <p v-if="registerErrors.name" class="error-message">{{ registerErrors.name }}</p>
              </div>
              <div class="relative">
                <input type="email" v-model="registerForm.email" placeholder="Email Address" class="auth-input" :class="{ 'input-error': registerErrors.email }" required />
                <p v-if="registerErrors.email" class="error-message">{{ registerErrors.email }}</p>
              </div>
              
              <!-- Custom Date Picker -->
              <div class="relative">
                <CustomDatePicker 
                  v-model="registerForm.dob" 
                  :hasError="!!registerErrors.dob"
                />
                <p v-if="registerErrors.dob" class="error-message">{{ registerErrors.dob }}</p>
              </div>

              <div class="relative">
                <div class="gender-selection">
                  <label class="gender-option" :class="{ 'selected-gender': registerForm.gender === 'Male' }">
                    <input type="radio" v-model="registerForm.gender" value="Male" class="hidden" required />
                    <font-awesome-icon :icon="['fas', 'male']" /> Male
                  </label>
                  <label class="gender-option" :class="{ 'selected-gender': registerForm.gender === 'Female' }">
                    <input type="radio" v-model="registerForm.gender" value="Female" class="hidden" required />
                    <font-awesome-icon :icon="['fas', 'female']" /> Female
                  </label>
                  <label class="gender-option" :class="{ 'selected-gender': registerForm.gender === 'Other' }">
                    <input type="radio" v-model="registerForm.gender" value="Other" class="hidden" required />
                    <font-awesome-icon :icon="['fas', 'genderless']" /> Other
                  </label>
                </div>
                <p v-if="registerErrors.gender" class="error-message">{{ registerErrors.gender }}</p>
              </div>
              <div class="relative">
                <input type="tel" v-model="registerForm.phone" placeholder="Phone Number" class="auth-input" :class="{ 'input-error': registerErrors.phone }" required />
                <p v-if="registerErrors.phone" class="error-message">{{ registerErrors.phone }}</p>
              </div>
              <div class="relative">
                <input
                  :type="showRegisterPassword ? 'text' : 'password'"
                  v-model="registerForm.password"
                  placeholder="Password"
                  class="auth-input pr-10"
                  :class="{ 'input-error': registerErrors.password }"
                  required
                />
                <button
                  type="button"
                  @click="toggleRegisterPasswordVisibility"
                  class="password-toggle-button"
                  aria-label="Toggle password visibility"
                >
                  <font-awesome-icon :icon="['fas', showRegisterPassword ? 'eye-slash' : 'eye']" />
                </button>
                <p v-if="registerErrors.password" class="error-message">{{ registerErrors.password }}</p>
              </div>

              <button type="submit" class="auth-button">
                <font-awesome-icon :icon="['fas', 'user-plus']" /> Register
              </button>
            </form>

            <!-- 🔄 Toggle Button -->
            <div class="mt-6 text-center">
              <button
                @click="toggleAuthMode"
                class="transition-all duration-500 font-semibold text-purple-400 hover:text-pink-400"
              >
                {{ isRegister ? 'Already have an account? Sign In' : "Don't have an account? Register" }}
              </button>
            </div>
          </Motion>
        </div>
      </div>
    </main>

    <!-- Global Alert/Notification System -->
    <div v-if="alert.message" :class="['alert-container', alert.type]">
      <div class="alert-content">
        <font-awesome-icon :icon="['fas', alert.icon]" class="alert-icon" />
        <p class="alert-message">{{ alert.message }}</p>
        <button @click="clearAlert" class="alert-close">
          <font-awesome-icon :icon="['fas', 'times']" />
        </button>
      </div>
    </div>

    <!-- 🦶 Footer -->
    <footer class="py-6 text-center text-gray-400 border-t border-purple-400/20 bg-transparent z-10">
      © {{ new Date().getFullYear() }} Eventra — Empower Your Events
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { Motion } from "@motionone/vue";
import { library } from "@fortawesome/fontawesome-svg-core";
import { fas } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { useRouter } from 'vue-router';
import axios from "axios";
import CustomDatePicker from './CustomDatePicker.vue';

// Note: CustomDatePicker uses .auth-input class from parent component
// Make sure the parent has these styles defined

library.add(fas);

const router = useRouter();

const theme = ref(localStorage.getItem("theme") || "dark");
const isRegister = ref(false);

// Password visibility states
const showLoginPassword = ref(false);
const showRegisterPassword = ref(false);

const toggleLoginPasswordVisibility = () => {
  showLoginPassword.value = !showLoginPassword.value;
};

const toggleRegisterPasswordVisibility = () => {
  showRegisterPassword.value = !showRegisterPassword.value;
};

// Alert system
const alert = ref({
  message: '',
  type: '', // 'success', 'error', 'info'
  icon: '',
  timeout: null,
});

const showAlert = (message, type, duration = 3000) => {
  clearTimeout(alert.value.timeout);
  alert.value.message = message;
  alert.value.type = type;
  alert.value.icon = type === 'success' ? 'check-circle' : (type === 'error' ? 'exclamation-circle' : 'info-circle');
  alert.value.timeout = setTimeout(() => {
    clearAlert();
  }, duration);
};

const clearAlert = () => {
  alert.value.message = '';
  alert.value.type = '';
  alert.value.icon = '';
};


// 🧾 Login form
const loginForm = ref({
  email: "",
  password: "",
});
const loginErrors = ref({});

// 🧾 Register form
const registerForm = ref({
  name: "",
  email: "",
  dob: "",
  gender: "",
  phone: "",
  password: "",
});
const registerErrors = ref({});


const applyTheme = () => {
  document.documentElement.classList.toggle("dark", theme.value === "dark");
  localStorage.setItem("theme", theme.value);
};
const toggleTheme = () => {
  theme.value = theme.value === "dark" ? "light" : "dark";
  applyTheme();
};
onMounted(applyTheme);

// 🧠 Logic
const toggleAuthMode = () => {
  isRegister.value = !isRegister.value;
  // Clear errors and alerts when switching modes
  loginErrors.value = {};
  registerErrors.value = {};
  clearAlert();
};

const validateLoginForm = () => {
  loginErrors.value = {};
  if (!loginForm.value.email) loginErrors.value.email = "Email is required.";
  else if (!/\S+@\S+\.\S+/.test(loginForm.value.email)) loginErrors.value.email = "Email is invalid.";
  if (!loginForm.value.password) loginErrors.value.password = "Password is required.";
  return Object.keys(loginErrors.value).length === 0;
};

const validateRegisterForm = () => {
  registerErrors.value = {};
  if (!registerForm.value.name) registerErrors.value.name = "Full Name is required.";
  if (!registerForm.value.email) registerErrors.value.email = "Email is required.";
  else if (!/\S+@\S+\.\S+/.test(registerForm.value.email)) registerErrors.value.email = "Email is invalid.";
  if (!registerForm.value.dob) registerErrors.value.dob = "Date of Birth is required.";
  if (!registerForm.value.gender) registerErrors.value.gender = "Gender is required.";
  if (!registerForm.value.phone) registerErrors.value.phone = "Phone Number is required.";
  if (!registerForm.value.password) registerErrors.value.password = "Password is required.";
  else if (registerForm.value.password.length < 6) registerErrors.value.password = "Password must be at least 6 characters.";
  return Object.keys(registerErrors.value).length === 0;
};

const handleLogin = async () => {
  if (validateLoginForm()) {
    try {
      const response = await axios.post("/api/auth/login", {
        email: loginForm.value.email,
        password: loginForm.value.password,
      });

      const data = response.data;

      // Token might be named `jwt` in backend response
      const token = data.jwt || data.token;
      const message = data.message || "Login successful!";

      if (token) {
        // Store token
        localStorage.setItem("token", token);

        // Optionally store user details
        if (data.userData) {
          localStorage.setItem("user", JSON.stringify(data.userData));
        }

        showAlert(message, "success");
        router.push("/home");
      } else {
        showAlert("Login succeeded but no token received.", "warning");
      }
    } catch (error) {
      console.error("Login error:", error);

      const message =
        error.response?.data?.message ||
        error.response?.data ||
        "Invalid email or password.";

      showAlert(message, "error");
    }
  }
};

const handleRegister = async () => {
  if (validateRegisterForm()) {
    try {
      const payload = {
        name: registerForm.value.name,
        email: registerForm.value.email,
        dob: registerForm.value.dob,
        gender: registerForm.value.gender,
        phoneNo: registerForm.value.phone,
        password: registerForm.value.password,
      };

      await axios.post("/api/auth/register", payload);

      showAlert("Registration successful! Please sign in to continue.", "success");

      isRegister.value = false;

      loginForm.value.email = registerForm.value.email;
      loginForm.value.password = ""; // Clear the password field for security


    } catch (error) {
      console.error("Registration error:", error);
      const message =
        error.response?.data?.message ||
        error.response?.data ||
        "Registration failed. The email may already be in use.";
      showAlert(message, "error");
    }
  }
};

</script>

<style scoped>
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
  background: radial-gradient(circle at 60% 50%, rgba(255, 255, 255, 0.35), rgba(150, 0, 255, 0.15), transparent 80%);
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

/* Background movement */
@keyframes fogMove { 0% { transform: translate(0, 0) scale(1); } 100% { transform: translate(-10%, 5%) scale(1.2); } }
@keyframes lightShift { 0% { transform: translate(0, 0) scale(1); } 100% { transform: translate(-10%, 10%) scale(1.1); } }

/* ✨ Auth Form Styles */
.auth-input {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(150, 0, 255, 0.2);
  color: inherit;
  padding: 0.75rem 1rem;
  border-radius: 0.75rem;
  outline: none;
  transition: all 0.3s ease;
  width: 100%; /* Ensure inputs take full width */
}
.auth-input:focus {
  border-color: rgba(200, 0, 255, 0.6);
  background: rgba(255, 255, 255, 0.15);
}

/* Password Toggle Button */
.password-toggle-button {
  position: absolute;
  right: 1rem;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: rgba(150, 0, 255, 0.6);
  cursor: pointer;
  padding: 0.25rem;
  transition: color 0.3s ease;
}

.password-toggle-button:hover {
  color: rgba(200, 0, 255, 0.8);
}
.dark .password-toggle-button {
  color: rgba(255, 255, 255, 0.6);
}
.dark .password-toggle-button:hover {
  color: rgba(255, 255, 255, 0.8);
}


/* Error State for Inputs */
.input-error {
  border-color: #ef4444 !important; /* Tailwind's red-500 */
  box-shadow: 0 0 0 1px #ef4444;
}
.error-message {
  color: #ef4444; /* Tailwind's red-500 */
  font-size: 0.75rem; /* text-xs */
  margin-top: 0.25rem; /* mt-1 */
  position: absolute;
  bottom: -1.25rem; /* Adjust as needed */
  left: 0;
}

/* Gender Selection with Icons */
.gender-selection {
  display: flex;
  gap: 0.5rem;
  width: 100%;
  justify-content: center;
  flex-wrap: wrap; /* Allow wrapping on smaller screens */
}
.gender-option {
  flex: 1; /* Distribute space evenly */
  min-width: 90px; /* Minimum width for each button */
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(150, 0, 255, 0.2);
  padding: 0.75rem 1rem;
  border-radius: 0.75rem;
  cursor: pointer;
  transition: all 0.3s ease;
}
.gender-option:hover {
  border-color: rgba(200, 0, 255, 0.6);
  background: rgba(255, 255, 255, 0.15);
}
.gender-option.selected-gender {
  background: linear-gradient(to right, #a855f7, #ec4899);
  border-color: #a855f7;
  color: white;
  box-shadow: 0 0 15px rgba(236, 72, 153, 0.3);
}

.auth-button {
  background: linear-gradient(to right, #a855f7, #ec4899);
  color: white;
  padding: 0.9rem 1.5rem;
  border-radius: 0.75rem;
  font-weight: 600;
  transition: all 0.3s ease;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
}
.auth-button:hover {
  transform: scale(1.05);
  box-shadow: 0 0 25px rgba(236, 72, 153, 0.4);
}

/* Alert Container Styles */
.alert-container {
  position: fixed;
  top: 2rem;
  left: 50%;
  transform: translateX(-50%);
  z-index: 100;
  padding: 0.75rem 1.5rem;
  border-radius: 0.75rem;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  box-shadow: 0 10px 15px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease-in-out;
  animation: fadeInDown 0.5s ease-out;
}

.alert-container.success {
  background-color: #10b981; /* Tailwind green-500 */
  color: white;
}

.alert-container.error {
  background-color: #ef4444; /* Tailwind red-500 */
  color: white;
}

.alert-container.info {
  background-color: #3b82f6; /* Tailwind blue-500 */
  color: white;
}

.alert-content {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.alert-icon {
  font-size: 1.25rem;
}

.alert-message {
  font-weight: 500;
}

.alert-close {
  background: none;
  border: none;
  color: inherit;
  font-size: 1rem;
  cursor: pointer;
  margin-left: 0.5rem;
  opacity: 0.8;
}

.alert-close:hover {
  opacity: 1;
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translate(-50%, -20px);
  }
  to {
    opacity: 1;
    transform: translate(-50%, 0);
  }
}
</style>