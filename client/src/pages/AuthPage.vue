<template>
  <div
    class="relative min-h-screen w-screen flex flex-col transition-all duration-700 font-inter"
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
          to="/events"
          class="hidden md:flex items-center gap-2 px-4 md:px-6 py-2 rounded-full text-sm font-semibold hover:bg-purple-600/30 border border-purple-400/40 transition-all duration-300"
        >
          <font-awesome-icon :icon="['fas', 'calendar-alt']" /> Events
        </router-link>

        <router-link
          to="/auth"
          class="flex items-center gap-2 px-4 md:px-6 py-2 rounded-full text-sm font-semibold text-white bg-gradient-to-r from-purple-500 to-pink-500 shadow-md hover:shadow-xl hover:scale-105 transition-all duration-300"
        >
          Sign In <font-awesome-icon :icon="['fas', 'arrow-right']" />
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
        <!-- No extra styling/dimensions here for the video itself -->
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
              {{ isRegister ? (otpSent ? 'Verify OTP' : 'Create Account') : 'Welcome Back' }}
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
                <Transition name="fade">
                  <p v-if="loginErrors.email" class="error-message">
                    <font-awesome-icon :icon="['fas', 'exclamation-circle']" class="mr-1" />
                    {{ loginErrors.email }}
                  </p>
                </Transition>
              </div>
              <div class="relative">
                <input
                  type="password"
                  v-model="loginForm.password"
                  placeholder="Password"
                  class="auth-input"
                  :class="{ 'input-error': loginErrors.password }"
                  required
                />
                <Transition name="fade">
                  <p v-if="loginErrors.password" class="error-message">
                    <font-awesome-icon :icon="['fas', 'excla