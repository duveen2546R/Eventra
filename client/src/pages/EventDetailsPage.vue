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

    <!-- Background -->
    <div class="absolute inset-0 overflow-hidden">
      <div class="fog"></div>
      <div class="light"></div>
      <div class="light-sweep"></div>
    </div>

    <!-- Navbar -->
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
        <router-link to="/home" class="nav-link">
          <font-awesome-icon :icon="['fas', 'home']" /> Home
        </router-link>
        <router-link to="/events" class="nav-link">
          <font-awesome-icon :icon="['fas', 'calendar-alt']" /> Events
        </router-link>
        <router-link to="/myevents" class="nav-link">
          <font-awesome-icon :icon="['fas', 'star']" /> My Events
        </router-link>

        <button
          @click="toggleTheme"
          class="border border-purple-400/30 px-3 md:px-4 py-2 rounded-full transition-all duration-300 text-sm"
        >
          <font-awesome-icon :icon="['fas', theme === 'dark' ? 'sun' : 'moon']" />
        </button>
      </nav>
    </header>

    <!-- Main Content -->
    <main class="pt-20 px-6 md:px-16 lg:px-24 relative z-20 pb-20">
      <!-- Back Button -->
      <button
        @click="goBack"
        class="mb-6 flex items-center gap-2 text-purple-400 hover:text-purple-300 transition-all duration-300"
      >
        <font-awesome-icon :icon="['fas', 'arrow-left']" />
        <span>Back to Events</span>
      </button>

      <div v-if="loading" class="flex justify-center items-center py-24">
        <div class="animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-purple-500"></div>
      </div>

      <div v-else-if="event" class="max-w-6xl mx-auto">
        <!-- Event Header -->
        <div class="glass-card rounded-3xl p-8 md:p-12 mb-8">
          <div class="flex flex-col lg:flex-row gap-8">
            <!-- Left: Event Image/Brochure -->
            <div class="lg:w-1/2">
              <div class="relative overflow-hidden rounded-2xl shadow-2xl group">
                <!-- PDF Brochure -->
                <div v-if="isPDF" class="w-full bg-gray-900 rounded-2xl overflow-hidden">
                  <iframe
                    :src="getBrochureUrl"
                    class="w-full h-[500px] border-0"
                    title="Event Brochure"
                  ></iframe>
                </div>
                
                <!-- Image Brochure with enhanced display -->
                <div v-else-if="event.brochure || event.brochureUrl" class="relative">
                  <img
                    :src="getBrochureUrl"
                    :alt="event.title"
                    class="w-full h-auto max-h-[500px] object-contain bg-gray-900 transition-transform duration-500 group-hover:scale-105"
                    @error="handleImageError"
                  />
                  <div class="absolute inset-0 bg-gradient-to-t from-black/50 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
                </div>
                
                <!-- Fallback -->
                <div
                  v-else
                  class="w-full h-96 bg-gradient-to-br from-purple-500/20 to-pink-500/20 flex items-center justify-center rounded-2xl"
                >
                  <font-awesome-icon :icon="['fas', 'image']" class="text-6xl text-purple-400 opacity-50" />
                </div>
                
                <!-- Event Status Badge -->
                <div class="absolute top-4 right-4">
                  <span
                    class="px-4 py-2 rounded-full text-sm font-bold backdrop-blur-md shadow-lg"
                    :class="eventStatusClass"
                  >
                    {{ eventStatus }}
                  </span>
                </div>
              </div>

              <!-- Download Brochure Button -->
              <button
                v-if="event.brochure || event.brochureUrl"
                @click="downloadBrochure"
                class="mt-4 w-full flex items-center justify-center gap-2 px-6 py-3 bg-purple-500/20 hover:bg-purple-500/30 border border-purple-400/30 rounded-xl transition-all duration-300 hover:scale-105"
              >
                <font-awesome-icon :icon="['fas', 'download']" />
                <span>Download Brochure</span>
              </button>
            </div>

            <!-- Right: Event Details -->
            <div class="lg:w-1/2 flex flex-col justify-between">
              <div>
                <h1 class="text-4xl md:text-5xl font-bold mb-4 bg-gradient-to-r from-purple-400 to-pink-400 bg-clip-text text-transparent">
                  {{ event.title }}
                </h1>
                
                <p class="text-gray-400 text-lg mb-6 leading-relaxed">
                  {{ event.description }}
                </p>

                <!-- Event Info Grid -->
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
                  <!-- Date & Time -->
                  <div class="info-card">
                    <font-awesome-icon :icon="['fas', 'calendar']" class="text-purple-400 text-xl mb-2" />
                    <p class="text-sm text-gray-400">Date & Time</p>
                    <p class="font-semibold">{{ formatDateTime(event.eventTimestamp) }}</p>
                  </div>

                  <!-- Venue -->
                  <div class="info-card">
                    <font-awesome-icon :icon="['fas', 'map-marker-alt']" class="text-purple-400 text-xl mb-2" />
                    <p class="text-sm text-gray-400">Venue</p>
                    <p class="font-semibold">{{ event.location || event.venue }}</p>
                  </div>

                  <!-- Price -->
                  <div class="info-card">
                    <font-awesome-icon :icon="['fas', 'tag']" class="text-purple-400 text-xl mb-2" />
                    <p class="text-sm text-gray-400">Entry Fee</p>
                    <p class="font-semibold text-2xl">
                      <span v-if="event.amount <= 0" class="text-green-400">FREE</span>
                      <span v-else class="text-purple-400">₹{{ event.amount }}</span>
                    </p>
                  </div>

                  <!-- Capacity -->
                  <div class="info-card" v-if="event.capacity !== undefined">
                    <font-awesome-icon :icon="['fas', 'users']" class="text-purple-400 text-xl mb-2" />
                    <p class="text-sm text-gray-400">Capacity</p>
                    <p class="font-semibold text-xl">
                      <span :class="event.remainingCapacity > 10 ? 'text-green-400' : 'text-orange-400'">
                        {{ event.remainingCapacity || 0 }}
                      </span>
                      <span class="text-gray-500 text-sm"> / {{ event.capacity }}</span>
                    </p>
                  </div>

                  <!-- Total Registrations -->
                  <div class="info-card" v-if="event.totalRegistrations !== undefined">
                    <font-awesome-icon :icon="['fas', 'user-check']" class="text-purple-400 text-xl mb-2" />
                    <p class="text-sm text-gray-400">Registered</p>
                    <p class="font-semibold text-xl text-purple-400">{{ event.totalRegistrations }}</p>
                  </div>

                  <!-- Checked In -->
                  <div class="info-card" v-if="event.checkedInCount !== undefined">
                    <font-awesome-icon :icon="['fas', 'clipboard-check']" class="text-purple-400 text-xl mb-2" />
                    <p class="text-sm text-gray-400">Checked In</p>
                    <p class="font-semibold text-xl text-green-400">{{ event.checkedInCount }}</p>
                  </div>

                  <!-- Status -->
                  <div class="info-card" v-if="event.status">
                    <font-awesome-icon :icon="['fas', 'info-circle']" class="text-purple-400 text-xl mb-2" />
                    <p class="text-sm text-gray-400">Status</p>
                    <p class="font-semibold">
                      <span :class="event.status === 'ACTIVE' ? 'text-green-400' : 'text-gray-400'">
                        {{ event.status }}
                      </span>
                    </p>
                  </div>

                  <!-- Category -->
                  <div class="info-card" v-if="event.category">
                    <font-awesome-icon :icon="['fas', 'bookmark']" class="text-purple-400 text-xl mb-2" />
                    <p class="text-sm text-gray-400">Category</p>
                    <p class="font-semibold">{{ event.category }}</p>
                  </div>
                </div>

                <!-- Additional Details -->
                <div v-if="event.additional_details" class="mt-6 p-4 bg-purple-500/10 border border-purple-400/20 rounded-xl">
                  <h3 class="text-lg font-semibold mb-2 flex items-center gap-2">
                    <font-awesome-icon :icon="['fas', 'info-circle']" class="text-purple-400" />
                    Additional Information
                  </h3>
                  <p class="text-gray-300 leading-relaxed">{{ event.additional_details }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Location Map Section -->
        <div v-if="hasLocation" class="glass-card rounded-3xl p-8 mb-8">
          <h2 class="text-2xl font-bold mb-6 flex items-center gap-3">
            <font-awesome-icon :icon="['fas', 'map-marked-alt']" class="text-purple-400" />
            Event Location
          </h2>
          
          <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <!-- Map -->
            <div class="lg:col-span-2">
              <div class="rounded-2xl overflow-hidden shadow-2xl h-96 bg-gray-800">
                <iframe
                  :src="getMapUrl"
                  width="100%"
                  height="100%"
                  style="border:0;"
                  allowfullscreen=""
                  loading="lazy"
                  referrerpolicy="no-referrer-when-downgrade"
                  class="w-full h-full"
                ></iframe>
              </div>
            </div>
            
            <!-- Location Details -->
            <div class="flex flex-col gap-4">
              <div class="p-4 bg-purple-500/10 border border-purple-400/20 rounded-xl">
                <font-awesome-icon :icon="['fas', 'map-marker-alt']" class="text-purple-400 text-2xl mb-3" />
                <h3 class="font-semibold mb-2">Address</h3>
                <p class="text-gray-400">{{ event.location || event.venue }}</p>
              </div>
              
              <div v-if="event.latitude && event.longitude" class="p-4 bg-purple-500/10 border border-purple-400/20 rounded-xl">
                <font-awesome-icon :icon="['fas', 'compass']" class="text-purple-400 text-2xl mb-3" />
                <h3 class="font-semibold mb-2">Coordinates</h3>
                <p class="text-gray-400 text-sm">
                  Lat: {{ event.latitude }}<br/>
                  Long: {{ event.longitude }}
                </p>
              </div>
              
              <a
                :href="getDirectionsUrl"
                target="_blank"
                class="flex items-center justify-center gap-2 px-6 py-3 bg-purple-500/20 hover:bg-purple-500/30 border border-purple-400/30 rounded-xl transition-all duration-300 text-center"
              >
                <font-awesome-icon :icon="['fas', 'directions']" />
                <span>Get Directions</span>
              </a>
            </div>
          </div>
        </div>

        <!-- QR Code Section -->
        <div v-if="hasQRCode" class="glass-card rounded-3xl p-8 mb-8">
          <h2 class="text-2xl font-bold mb-6 flex items-center gap-3">
            <font-awesome-icon :icon="['fas', 'qrcode']" class="text-purple-400" />
            Event QR Code
          </h2>
          
          <div class="flex flex-col md:flex-row items-center justify-center gap-8">
            <div class="flex-shrink-0">
              <div class="bg-white p-8 rounded-2xl shadow-2xl">
                <img
                  :src="getQRCodeUrl"
                  alt="Event QR Code"
                  class="w-72 h-72 object-contain"
                  @error="handleQRError"
                />
              </div>
            </div>
            
            <div class="flex-1 max-w-md">
              <h3 class="text-2xl font-semibold mb-4 text-purple-400">Scan to Access Event</h3>
              <p class="text-gray-400 mb-6 leading-relaxed">
                Use your mobile device to scan this QR code for quick access to event registration and details.
                You can save this QR code to your device for future reference.
              </p>
              <button
                @click="downloadQR"
                class="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600 rounded-xl transition-all duration-300 shadow-lg hover:shadow-xl hover:scale-105"
              >
                <font-awesome-icon :icon="['fas', 'download']" />
                <span class="font-semibold">Download QR Code</span>
              </button>
            </div>
          </div>
        </div>

        <!-- Contact Information -->
        <div v-if="hasContactInfo" class="glass-card rounded-3xl p-8 mb-8">
          <h2 class="text-2xl font-bold mb-6 flex items-center gap-3">
            <font-awesome-icon :icon="['fas', 'address-book']" class="text-purple-400" />
            Contact Information
          </h2>
          
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <!-- Email Contacts -->
            <div v-if="emailContacts.length > 0" class="flex flex-col gap-3">
              <div 
                v-for="(email, index) in emailContacts" 
                :key="`email-${index}`"
                class="flex items-center gap-3 p-4 bg-purple-500/10 rounded-xl hover:bg-purple-500/20 transition-all"
              >
                <font-awesome-icon :icon="['fas', 'envelope']" class="text-purple-400 text-xl flex-shrink-0" />
                <div class="flex-1 min-w-0">
                  <p class="text-sm text-gray-400">Email {{ emailContacts.length > 1 ? `#${index + 1}` : '' }}</p>
                  <a 
                    :href="`mailto:${email}`" 
                    class="font-semibold hover:text-purple-400 transition-colors truncate block"
                  >
                    {{ email }}
                  </a>
                </div>
              </div>
            </div>
            
            <!-- Phone Contacts -->
            <div v-if="phoneContacts.length > 0" class="flex flex-col gap-3">
              <div 
                v-for="(phone, index) in phoneContacts" 
                :key="`phone-${index}`"
                class="flex items-center gap-3 p-4 bg-purple-500/10 rounded-xl hover:bg-purple-500/20 transition-all"
              >
                <font-awesome-icon :icon="['fas', 'phone']" class="text-purple-400 text-xl flex-shrink-0" />
                <div class="flex-1">
                  <p class="text-sm text-gray-400">Phone {{ phoneContacts.length > 1 ? `#${index + 1}` : '' }}</p>
                  <a 
                    :href="`tel:${phone}`" 
                    class="font-semibold hover:text-purple-400 transition-colors"
                  >
                    {{ phone }}
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Registration Section -->
        <div class="glass-card rounded-3xl p-8 text-center">
          <div v-if="isRegistered" class="py-8">
            <font-awesome-icon :icon="['fas', 'check-circle']" class="text-6xl text-green-400 mb-4" />
            <h3 class="text-2xl font-bold mb-2">You're Registered!</h3>
            <p class="text-gray-400 mb-6">You have successfully registered for this event.</p>
            <router-link
              to="/myevents"
              class="inline-flex items-center gap-2 px-8 py-4 bg-purple-500/20 hover:bg-purple-500/30 border border-purple-400/30 rounded-full transition-all duration-300 text-lg font-semibold hover:scale-105"
            >
              <font-awesome-icon :icon="['fas', 'star']" />
              View My Events
            </router-link>
          </div>

          <div v-else-if="!loggedIn" class="py-8">
            <font-awesome-icon :icon="['fas', 'user-lock']" class="text-6xl text-purple-400 mb-4 opacity-50" />
            <h3 class="text-2xl font-bold mb-2">Sign In Required</h3>
            <p class="text-gray-400 mb-6">Please sign in to register for this event.</p>
            <router-link
              to="/auth"
              class="inline-flex items-center gap-2 px-8 py-4 bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600 rounded-full transition-all duration-300 text-lg font-semibold shadow-lg hover:shadow-xl hover:scale-105"
            >
              Sign In Now
              <font-awesome-icon :icon="['fas', 'arrow-right']" />
            </router-link>
          </div>

          <div v-else-if="isPastEvent" class="py-8">
            <font-awesome-icon :icon="['fas', 'clock']" class="text-6xl text-gray-400 mb-4 opacity-50" />
            <h3 class="text-2xl font-bold mb-2">Event Has Ended</h3>
            <p class="text-gray-400">This event has already concluded.</p>
          </div>

          <div v-else-if="isFullyBooked" class="py-8">
            <font-awesome-icon :icon="['fas', 'exclamation-circle']" class="text-6xl text-orange-400 mb-4" />
            <h3 class="text-2xl font-bold mb-2">Event Fully Booked</h3>
            <p class="text-gray-400">Unfortunately, all seats for this event have been filled.</p>
          </div>

          <div v-else class="py-8">
            <h3 class="text-3xl font-bold mb-4">Ready to Join?</h3>
            <p class="text-gray-400 mb-8 max-w-2xl mx-auto">
              Register now to secure your spot at this amazing event. 
              {{ event.amount > 0 ? `Entry fee: ₹${event.amount}` : "It's completely free!" }}
            </p>
            <button
              @click="registerForEvent"
              :disabled="registering"
              class="inline-flex items-center gap-3 px-12 py-5 bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600 disabled:from-gray-500 disabled:to-gray-600 disabled:cursor-not-allowed rounded-full transition-all duration-300 text-xl font-bold shadow-lg hover:shadow-2xl hover:scale-105 disabled:hover:scale-100"
            >
              <font-awesome-icon :icon="['fas', registering ? 'spinner' : 'ticket-alt']" :class="{ 'animate-spin': registering }" />
              {{ registering ? 'Processing...' : event.amount > 0 ? `Register for ₹${event.amount}` : 'Register for Free' }}
            </button>
            
            <p class="text-sm text-gray-500 mt-4">
              By registering, you agree to our terms and conditions.
            </p>
          </div>
        </div>
      </div>

      <div v-else class="text-center py-24">
        <font-awesome-icon :icon="['fas', 'exclamation-triangle']" class="text-6xl text-purple-400 mb-4 opacity-50" />
        <h2 class="text-2xl font-bold mb-2">Event Not Found</h2>
        <p class="text-gray-400 mb-6">The event you're looking for doesn't exist or has been removed.</p>
        <router-link
          to="/events"
          class="inline-flex items-center gap-2 px-6 py-3 bg-purple-500/20 hover:bg-purple-500/30 border border-purple-400/30 rounded-full transition-all duration-300"
        >
          <font-awesome-icon :icon="['fas', 'arrow-left']" />
          Back to Events
        </router-link>
      </div>
    </main>

    <!-- Footer -->
    <footer class="py-6 text-center text-gray-400 border-t border-purple-400/20 bg-transparent relative z-20">
      © {{ new Date().getFullYear() }} Eventra — Empower Your Events
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import axios from "axios";
import { library } from "@fortawesome/fontawesome-svg-core";
import { fas } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import GlobalAlert from '../components/GlobalAlert.vue';
import { useAlert } from '../composables/useAlert';

library.add(fas);

const router = useRouter();
const route = useRoute();
const { alertState, showSuccess, showError, showWarning, showInfo } = useAlert();

const theme = ref(localStorage.getItem("theme") || "dark");
const loggedIn = ref(!!localStorage.getItem("token"));
const event = ref(null);
const loading = ref(true);
const registering = ref(false);
const isRegistered = ref(false);

const userDetails = ref({
  user_id: "",
  name: "",
  email: "",
  phone_no: "",
});

// Computed properties
const eventStatus = computed(() => {
  if (!event.value) return '';
  const now = new Date();
  const eventDate = new Date(event.value.eventTimestamp);
  const eventEndDate = new Date(eventDate.getTime() + 4 * 60 * 60 * 1000);
  
  if (eventDate <= now && now <= eventEndDate) return 'Ongoing';
  if (eventDate > now) return 'Upcoming';
  return 'Completed';
});

const eventStatusClass = computed(() => {
  const status = eventStatus.value;
  if (status === 'Ongoing') return 'bg-red-500/90 text-white';
  if (status === 'Upcoming') return 'bg-green-500/90 text-white';
  return 'bg-gray-500/90 text-white';
});

const isPastEvent = computed(() => {
  if (!event.value) return false;
  const now = new Date();
  const eventDate = new Date(event.value.eventTimestamp);
  const eventEndDate = new Date(eventDate.getTime() + 4 * 60 * 60 * 1000);
  return eventEndDate < now;
});

const isFullyBooked = computed(() => {
  return event.value && event.value.remainingCapacity !== undefined && event.value.remainingCapacity <= 0;
});

// Handle base64 QR code
const getQRCodeUrl = computed(() => {
  if (!event.value) return '';
  const qrCode = event.value.qrCode || event.value.qrCodeUrl;
  if (!qrCode) return '';
  
  // Check if it's already a data URL or regular URL
  if (qrCode.startsWith('data:') || qrCode.startsWith('http://') || qrCode.startsWith('https://')) {
    return qrCode;
  }
  
  // If it's base64 without prefix, add the data URL prefix
  return `data:image/png;base64,${qrCode}`;
});

const hasQRCode = computed(() => {
  return event.value && (event.value.qrCode || event.value.qrCodeUrl);
});

// Handle brochure URL (supports both base64 and regular URLs)
const getBrochureUrl = computed(() => {
  if (!event.value) return '';
  const brochure = event.value.brochure || event.value.brochureUrl;
  if (!brochure) return '';
  
  // Check if it's already a data URL or regular URL
  if (brochure.startsWith('data:') || brochure.startsWith('http://') || brochure.startsWith('https://')) {
    return brochure;
  }
  
  // If it's base64 without prefix, try to detect type and add prefix
  // Default to PDF if we can't determine
  if (brochure.length > 100 && !brochure.includes('.')) {
    return `data:application/pdf;base64,${brochure}`;
  }
  
  return brochure;
});

// Check if brochure is a PDF
const isPDF = computed(() => {
  if (!event.value) return false;
  const brochure = event.value.brochure || event.value.brochureUrl;
  if (!brochure) return false;
  
  // Check by data URL type
  if (brochure.startsWith('data:application/pdf')) return true;
  
  // Check by file extension
  const lowerBrochure = brochure.toLowerCase();
  return lowerBrochure.includes('.pdf') || lowerBrochure.endsWith('.pdf');
});

// Location and Map
const hasLocation = computed(() => {
  return event.value && event.value.latitude && event.value.longitude;
});

const getMapUrl = computed(() => {
  if (!hasLocation.value) return '';
  const lat = event.value.latitude;
  const lng = event.value.longitude;
  return `https://www.google.com/maps/embed/v1/place?key=AIzaSyBFw0Qbyq9zTFTd-tUY6dZWTgaQzuU17R8&q=${lat},${lng}&zoom=15`;
});

const getDirectionsUrl = computed(() => {
  if (!hasLocation.value) return '';
  const lat = event.value.latitude;
  const lng = event.value.longitude;
  return `https://www.google.com/maps/dir/?api=1&destination=${lat},${lng}`;
});

// Contact Information
const emailContacts = computed(() => {
  if (!event.value) return [];
  
  // Handle both single email and array of emails
  if (event.value.email) {
    if (Array.isArray(event.value.email)) {
      return event.value.email.filter(e => e);
    }
    return [event.value.email];
  }
  
  if (event.value.contact_email) {
    if (Array.isArray(event.value.contact_email)) {
      return event.value.contact_email.filter(e => e);
    }
    return [event.value.contact_email];
  }
  
  return [];
});

const phoneContacts = computed(() => {
  if (!event.value) return [];
  
  // Handle both phoneNo and phone_no, single and array
  const phoneField = event.value.phoneNo || event.value.phone_no || event.value.contact_phone;
  
  if (phoneField) {
    if (Array.isArray(phoneField)) {
      return phoneField.filter(p => p);
    }
    return [phoneField];
  }
  
  return [];
});

const hasContactInfo = computed(() => {
  return emailContacts.value.length > 0 || phoneContacts.value.length > 0;
});

const applyTheme = () => {
  document.documentElement.classList.toggle("dark", theme.value === "dark");
  localStorage.setItem("theme", theme.value);
};

const toggleTheme = () => {
  theme.value = theme.value === "dark" ? "light" : "dark";
  applyTheme();
};

const formatDateTime = (timestamp) => {
  const date = new Date(timestamp);
  return date.toLocaleString('en-US', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const goBack = () => {
  router.push('/events');
};

const handleImageError = () => {
  console.log('Error loading event image');
};

const handleQRError = () => {
  console.log('Error loading QR code');
};

const downloadBrochure = () => {
  const brochureUrl = getBrochureUrl.value;
  if (brochureUrl) {
    const link = document.createElement('a');
    link.href = brochureUrl;
    
    // Determine file extension
    const isPdf = isPDF.value;
    const extension = isPdf ? 'pdf' : 'jpg';
    link.download = `${event.value.title}-brochure.${extension}`;
    
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    showSuccess('Brochure download started!');
  }
};

const downloadQR = () => {
  const qrCodeUrl = getQRCodeUrl.value;
  if (qrCodeUrl) {
    const link = document.createElement('a');
    link.href = qrCodeUrl;
    link.download = `${event.value.title}-qr-code.png`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    showSuccess('QR Code download started!');
  }
};

const checkRegistrationStatus = async () => {
  if (!loggedIn.value || !event.value) return;
  
  try {
    const response = await axios.get(
      `/api/event-participants/is-registered/${event.value.eventId}/${userDetails.value.user_id}`
    );
    isRegistered.value = response.data;
  } catch (err) {
    console.error("Error checking registration:", err);
  }
};

const registerForEvent = async () => {
  if (!loggedIn.value) {
    showWarning("Please sign in to register");
    setTimeout(() => router.push("/auth"), 1500);
    return;
  }

  if (isRegistered.value) {
    showInfo("You are already registered for this event!");
    return;
  }

  if (isPastEvent.value) {
    showError("Cannot register for a past event");
    return;
  }

  if (isFullyBooked.value) {
    showError("This event is fully booked");
    return;
  }

  registering.value = true;

  const user = userDetails.value;

  if (event.value.amount <= 0) {
    // Free event
    try {
      await axios.post(`/api/events/${event.value.eventId}/register-free`, {
        userId: user.user_id,
      });
      showSuccess("Successfully registered for the event!");
      isRegistered.value = true;
      
      setTimeout(() => {
        router.push("/myevents");
      }, 2000);
    } catch (err) {
      console.error("Error registering free event:", err);
      showError("Failed to register. Please try again.");
    } finally {
      registering.value = false;
    }
    return;
  }

  // Paid event - Razorpay
  try {
    showInfo("Preparing payment...", 1000);
    
    const token = localStorage.getItem("token");
    const response = await axios.post(
      `/api/events/${event.value.eventId}/create-order`,
      { userId: user.user_id },
      { headers: { Authorization: `Bearer ${token}` } }
    );
    const orderData = response.data;

    const options = {
      key: orderData.key,
      amount: orderData.amount,
      currency: "INR",
      name: "Eventra",
      description: event.value.title,
      order_id: orderData.order_id,
      handler: async function (paymentResponse) {
        const token = localStorage.getItem("token");
        try {
          await axios.post("/api/events/verify-payment", {
            userId: user.user_id,
            eventId: event.value.eventId,
            razorpay_order_id: paymentResponse.razorpay_order_id,
            razorpay_payment_id: paymentResponse.razorpay_payment_id,
            razorpay_signature: paymentResponse.razorpay_signature,
          }, {
            headers: { Authorization: `Bearer ${token}` }
          });
          showSuccess("Payment successful! You are registered.");
          isRegistered.value = true;
          setTimeout(() => {
            router.push("/myevents");
          }, 2000);
        } catch (err) {
          console.error("Payment verification failed:", err);
          showError("Payment verification failed. Please contact support.");
        } finally {
          registering.value = false;
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
          registering.value = false;
        }
      }
    };

    const razor = new window.Razorpay(options);
    razor.open();
  } catch (err) {
    console.error("Error creating order:", err);
    showError("Failed to initiate payment. Please try again.");
    registering.value = false;
  }
};

onMounted(async () => {
  applyTheme();
  
  // Get user details from localStorage
  const userData = localStorage.getItem("user");
  if (userData) {
    try {
      userDetails.value = JSON.parse(userData);
    } catch (err) {
      console.error("Failed to parse user data:", err);
    }
  }

  // Fetch event details
  const eventId = route.params.id;
  if (!eventId) {
    showError("Invalid event ID");
    loading.value = false;
    return;
  }

  try {
    const response = await axios.get(`/api/events/${eventId}`);
    event.value = response.data;
    
    // Check if user is already registered
    await checkRegistrationStatus();
  } catch (err) {
    console.error("Error fetching event:", err);
    showError("Failed to load event details");
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
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

/* Glass card effect */
.glass-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.37);
}

.dark .glass-card {
  background: rgba(10, 10, 10, 0.6);
  border: 1px solid rgba(168, 85, 247, 0.2);
}

/* Info card */
.info-card {
  @apply p-4 rounded-xl bg-purple-500/10 border border-purple-400/20 transition-all duration-300;
}

.info-card:hover {
  @apply bg-purple-500/20 border-purple-400/40 transform scale-105;
}

/* Navigation link */
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
</style>