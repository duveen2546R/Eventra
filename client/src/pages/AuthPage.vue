<template>
  <div
    class="relative min-h-screen w-screen flex flex-col transition-all duration-700"
    :class="theme === 'dark' ? 'bg-[#0a0a0a] text-white' : 'bg-gray-100 text-gray-900'"
  >
    <!-- Global Alert Component -->
    <GlobalAlert 
      v-model="alertState.show"
      :message="alertState.message"
      :type="alertState.type"
      :duration="alertState.duration"
    />

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

            <!-- Google Sign-In Button (Common for both login and register) -->
            <button 
              @click="handleGoogleSignIn"
              :disabled="firebaseLoading"
              class="google-auth-button mb-6"
            >
              <svg class="w-5 h-5" viewBox="0 0 24 24">
                <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
              </svg>
              <span v-if="!firebaseLoading">Continue with Google</span>
              <span v-else>Signing in...</span>
            </button>

            <div class="divider-container mb-6">
              <div class="divider-line"></div>
              <span class="divider-text">or</span>
              <div class="divider-line"></div>
            </div>

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
                  autocomplete="email"
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
                  autocomplete="current-password"
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

              <button type="submit" class="auth-button" :disabled="firebaseLoading">
                <font-awesome-icon :icon="['fas', 'sign-in-alt']" /> 
                <span v-if="!firebaseLoading">Sign In</span>
                <span v-else>Signing in...</span>
              </button>
            </form>

            <!-- REGISTER FORM -->
            <form
              v-else
              @submit.prevent="handleRegister"
              class="flex flex-col gap-4"
            >
              <div class="relative">
                <input type="text" v-model="registerForm.name" placeholder="Full Name" class="auth-input" :class="{ 'input-error': registerErrors.name }" autocomplete="name" required />
                <p v-if="registerErrors.name" class="error-message">{{ registerErrors.name }}</p>
              </div>
              <div class="relative">
                <input type="email" v-model="registerForm.email" placeholder="Email Address" class="auth-input" :class="{ 'input-error': registerErrors.email }" autocomplete="email" required />
                <p v-if="registerErrors.email" class="error-message">{{ registerErrors.email }}</p>
              </div>
              
              <!-- Custom Date Picker -->
              <div class="relative">
                <CustomDatePicker 
                  v-model="registerForm.dob" 
                  :hasError="!!registerErrors.dob"
                  placeholder="Date of Birth"
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
                <input type="tel" v-model="registerForm.phone" placeholder="Phone Number" class="auth-input" :class="{ 'input-error': registerErrors.phone }" autocomplete="tel" required />
                <p v-if="registerErrors.phone" class="error-message">{{ registerErrors.phone }}</p>
              </div>
              <div class="relative">
                <input
                  :type="showRegisterPassword ? 'text' : 'password'"
                  v-model="registerForm.password"
                  placeholder="Password"
                  class="auth-input pr-10"
                  :class="{ 'input-error': registerErrors.password }"
                  autocomplete="new-password"
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

              <button type="submit" class="auth-button" :disabled="firebaseLoading">
                <font-awesome-icon :icon="['fas', 'user-plus']" /> 
                <span v-if="!firebaseLoading">Register</span>
                <span v-else>Registering...</span>
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
import CustomDatePicker from '../components/CustomDatePicker.vue';
import GlobalAlert from '../components/GlobalAlert.vue';
import { useAlert } from '../composables/useAlert.js';
import { useFirebaseAuth } from '../composables/useFirebaseAuth.js';

library.add(fas);

const router = useRouter();
const { alertState, showSuccess, showError, showWarning } = useAlert();
const { 
  signInWithGoogle, 
  signInWithEmail, 
  registerWithEmail, 
  loading: firebaseLoading 
} = useFirebaseAuth();

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
  loginErrors.value = {};
  registerErrors.value = {};
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

import { getAuth } from "firebase/auth";

const handleGoogleSignIn = async () => {
    const result = await signInWithGoogle();

    if (!result.success) {
        showError(result.error || "Google sign-in failed");
        return;
    }

    showSuccess("Successfully signed in with Google!");

    try {
        const auth = getAuth();

        if (!auth.currentUser) {
            showError("Firebase user not found. Please try again.");
            return;
        }

        const idToken = await auth.currentUser.getIdToken(true);

        if (!idToken) {
            showError("Failed to fetch Firebase ID token.");
            return;
        }

        const response = await axios.post("/api/auth/firebase-auth", {
            idToken: idToken
        });

        // Backend may return jwt or token
        const token = response.data.jwt || response.data.token;

        if (!token) {
            showError("Backend did not return JWT token.");
            return;
        }

        localStorage.setItem("token", token);

        if (response.data.userData) {
            localStorage.setItem("user", JSON.stringify(response.data.userData));
        } else {
            showError("Backend did not return user data.");
            return;
        }

        setTimeout(() => router.push("/home"), 1000);

    } catch (error) {
        console.error("Backend sync failed:", error);
        showError("Backend authentication failed!");
    }
};

// 🔥 Firebase Email/Password Login
const handleLogin = async () => {
  if (validateLoginForm()) {
    // Try Firebase authentication first
    const firebaseResult = await signInWithEmail(loginForm.value.email, loginForm.value.password);
    
    if (firebaseResult.success) {
      showSuccess('Login successful!');
      setTimeout(() => router.push("/home"), 1000);
      return;
    }
    
    // Fallback to original backend authentication
    try {
      const response = await axios.post("/api/auth/login", {
        email: loginForm.value.email,
        password: loginForm.value.password,
      });

      const data = response.data;
      const token = data.jwt || data.token;
      const message = data.message || "Login successful!";

      if (token) {
        localStorage.setItem("token", token);

        if (data.userData) {
          localStorage.setItem("user", JSON.stringify(data.userData));
        }

        showSuccess(message);
        setTimeout(() => router.push("/home"), 1000);
      } else {
        showWarning("Login succeeded but no token received.");
      }
    } catch (error) {
      console.error("Login error:", error);
      const message = error.response?.data?.message || error.response?.data || "Invalid email or password.";
      showError(message);
    }
  }
};

// 🔥 Firebase Email/Password Registration
const handleRegister = async () => {
  if (validateRegisterForm()) {
    // Try Firebase registration first
    const firebaseResult = await registerWithEmail(
      registerForm.value.email, 
      registerForm.value.password,
      registerForm.value.name
    );
    
    if (firebaseResult.success) {
      // Also register additional data with your backend
      try {
        const payload = {
          uid: firebaseResult.user.uid,
          name: registerForm.value.name,
          email: registerForm.value.email,
          dob: registerForm.value.dob,
          gender: registerForm.value.gender,
          phoneNo: registerForm.value.phone,
        };

        await axios.post("/api/auth/firebase-register", payload);
      } catch (error) {
        console.log('Backend registration optional - continuing with Firebase auth');
      }
      
      showSuccess("Registration successful! You're now signed in.");
      setTimeout(() => router.push("/home"), 1500);
      return;
    }
    
    // Fallback to original backend registration
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

      showSuccess("Registration successful! Please sign in to continue.");
      
      setTimeout(() => {
        isRegister.value = false;
        loginForm.value.email = registerForm.value.email;
        loginForm.value.password = "";
      }, 1500);

    } catch (error) {
      console.error("Registration error:", error);
      const message = error.response?.data?.message || error.response?.data || firebaseResult.error || "Registration failed.";
      showError(message);
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

/* 🔥 Google Sign-In Button */
.google-auth-button {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  background: white;
  color: #3c4043;
  padding: 0.875rem 1.5rem;
  border-radius: 0.75rem;
  font-weight: 600;
  font-size: 0.95rem;
  transition: all 0.3s ease;
  border: 1px solid #dadce0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.google-auth-button:hover:not(:disabled) {
  background: #f8f9fa;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
  transform: translateY(-1px);
}

.google-auth-button:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.google-auth-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.dark .google-auth-button {
  background: white;
  color: #3c4043;
}

/* Divider */
.divider-container {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.divider-line {
  flex: 1;
  height: 1px;
  background: rgba(150, 0, 255, 0.2);
}

.divider-text {
  color: rgba(150, 0, 255, 0.6);
  font-size: 0.875rem;
  font-weight: 500;
}

.dark .divider-text {
  color: rgba(255, 255, 255, 0.5);
}

/* ✨ Auth Form Styles */
.auth-input {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(150, 0, 255, 0.2);
  color: inherit;
  padding: 0.75rem 1rem;
  border-radius: 0.75rem;
  outline: none;
  transition: all 0.3s ease;
  width: 100%;
}

.auth-input:focus {
  border-color: rgba(200, 0, 255, 0.6);
  background: rgba(255, 255, 255, 0.15);
}

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

.input-error {
  border-color: #ef4444 !important;
  box-shadow: 0 0 0 1px #ef4444;
}

.error-message {
  color: #ef4444;
  font-size: 0.75rem;
  margin-top: 0.25rem;
  position: absolute;
  bottom: -1.25rem;
  left: 0;
}

.gender-selection {
  display: flex;
  gap: 0.5rem;
  width: 100%;
  justify-content: center;
  flex-wrap: wrap;
}

.gender-option {
  flex: 1;
  min-width: 90px;
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

.auth-button:hover:not(:disabled) {
  transform: scale(1.05);
  box-shadow: 0 0 25px rgba(236, 72, 153, 0.4);
}

.auth-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: scale(1);
}
</style>