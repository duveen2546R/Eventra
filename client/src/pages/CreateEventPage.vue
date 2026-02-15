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
        <!-- Auth Controls -->
        <div class="relative">
          <template v-if="loggedIn">
            <button @click="toggleDropdown" class="flex items-center gap-2 border border-purple-400/30 px-3 py-2 rounded-full transition-all duration-300 text-sm hover:bg-purple-600/20">
              <!-- Profile Picture or Icon -->
              <img 
                v-if="userProfilePic" 
                :src="userProfilePic" 
                alt="Profile" 
                class="profile-pic-small"
                @error="handleImageError"
              />
              <font-awesome-icon v-else :icon="['fas', 'user-circle']" class="text-lg" />
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

    <!-- ✏️ Create/Edit Event Form -->
    <main class="pt-28 px-6 md:px-16 relative z-20">
      <h1 class="text-4xl md:text-6xl font-bold mb-4">
        {{ isEditMode ? 'Edit' : 'Create' }} <span class="text-purple-400">Event</span>
      </h1>
      <p class="text-gray-400 max-w-2xl mb-10">
        {{ isEditMode ? 'Update the event details below.' : 'Fill in the event details below to add a new event to Eventra.' }}
      </p>

      <form @submit.prevent="submitEvent" class="mb-10 backdrop-blur-lg bg-white/5 border border-purple-400/20 rounded-2xl shadow-lg p-8 md:p-10 max-w-3xl mx-auto flex flex-col gap-6">
        
        <!-- Title & Description -->
        <div>
          <label class="text-sm text-gray-400">Title</label>
          <input v-model="event.title" type="text" placeholder="Enter event title" class="input-box" required />
        </div>
        <div>
          <label class="text-sm text-gray-400">Description</label>
          <textarea v-model="event.description" placeholder="Enter event description" rows="3" class="input-box resize-none" required></textarea>
        </div>
        
        <!-- Contact Information Section -->
        <div class="border-t border-purple-400/20 pt-6 mt-4">
          <h3 class="text-lg font-semibold text-purple-400 mb-4">
            <font-awesome-icon :icon="['fas', 'address-book']" class="mr-2" />
            Contact Information (Optional)
          </h3>
          <p class="text-xs text-gray-500 mb-4">Add phone numbers and email addresses for attendees to reach you with queries.</p>
          
          <!-- Phone Numbers -->
          <div class="mb-6">
            <label class="text-sm text-gray-400">Contact Phone Numbers</label>
            <div class="flex gap-2 mt-2">
              <input 
                v-model="newPhone" 
                type="tel" 
                placeholder="e.g., +91 98765 43210" 
                class="input-box flex-1"
                @keypress.enter.prevent="addPhone"
              />
              <button 
                type="button"
                @click="addPhone"
                class="px-4 py-2 bg-purple-500/20 hover:bg-purple-500/30 rounded-xl transition-all whitespace-nowrap"
              >
                <font-awesome-icon :icon="['fas', 'plus']" class="mr-1" /> Add
              </button>
            </div>
            
            <!-- Phone List -->
            <div v-if="event.contactPhones.length > 0" class="mt-3 space-y-2">
              <div 
                v-for="(phone, index) in event.contactPhones" 
                :key="index"
                class="flex items-center justify-between p-3 bg-white/5 border border-purple-400/20 rounded-lg"
              >
                <div class="flex items-center gap-2">
                  <font-awesome-icon :icon="['fas', 'phone']" class="text-purple-400" />
                  <span class="text-sm">{{ phone }}</span>
                </div>
                <button 
                  type="button"
                  @click="removePhone(index)"
                  class="text-xs px-3 py-1 bg-red-500/20 hover:bg-red-500/30 rounded-full transition-all"
                >
                  <font-awesome-icon :icon="['fas', 'times']" /> Remove
                </button>
              </div>
            </div>
            <p v-else class="text-xs text-gray-500 mt-2 italic">No phone numbers added yet</p>
          </div>

          <!-- Email Addresses -->
          <div>
            <label class="text-sm text-gray-400">Contact Email Addresses</label>
            <div class="flex gap-2 mt-2">
              <input 
                v-model="newEmail" 
                type="email" 
                placeholder="e.g., contact@example.com" 
                class="input-box flex-1"
                @keypress.enter.prevent="addEmail"
              />
              <button 
                type="button"
                @click="addEmail"
                class="px-4 py-2 bg-purple-500/20 hover:bg-purple-500/30 rounded-xl transition-all whitespace-nowrap"
              >
                <font-awesome-icon :icon="['fas', 'plus']" class="mr-1" /> Add
              </button>
            </div>
            
            <!-- Email List -->
            <div v-if="event.contactEmails.length > 0" class="mt-3 space-y-2">
              <div 
                v-for="(email, index) in event.contactEmails" 
                :key="index"
                class="flex items-center justify-between p-3 bg-white/5 border border-purple-400/20 rounded-lg"
              >
                <div class="flex items-center gap-2">
                  <font-awesome-icon :icon="['fas', 'envelope']" class="text-purple-400" />
                  <span class="text-sm">{{ email }}</span>
                </div>
                <button 
                  type="button"
                  @click="removeEmail(index)"
                  class="text-xs px-3 py-1 bg-red-500/20 hover:bg-red-500/30 rounded-full transition-all"
                >
                  <font-awesome-icon :icon="['fas', 'times']" /> Remove
                </button>
              </div>
            </div>
            <p v-else class="text-xs text-gray-500 mt-2 italic">No email addresses added yet</p>
          </div>
        </div>
        
        <!-- Event Brochure Upload -->
        <div>
          <label class="text-sm text-gray-400">Event Brochure (Optional)</label>
          <p class="text-xs text-gray-500 mb-2">Upload a PDF or image brochure for your event</p>
          
          <div class="relative">
            <input 
              ref="brochureInput"
              type="file" 
              @change="handleBrochureUpload" 
              accept="image/*,application/pdf"
              class="hidden" 
              id="brochure-upload"
            />
            <label 
              for="brochure-upload" 
              class="flex items-center justify-center gap-2 w-full px-4 py-3 border-2 border-dashed border-purple-400/30 rounded-xl cursor-pointer hover:border-purple-400/60 hover:bg-purple-400/5 transition-all"
              :class="{ 'border-purple-400/60 bg-purple-400/10': uploadingBrochure }"
            >
              <font-awesome-icon 
                :icon="['fas', uploadingBrochure ? 'spinner' : 'cloud-upload-alt']" 
                :class="{ 'animate-spin': uploadingBrochure }"
                class="text-lg text-purple-400"
              />
              <span v-if="uploadingBrochure" class="text-sm">Uploading brochure...</span>
              <span v-else-if="event.brochureUrl" class="text-sm text-green-400">
                <font-awesome-icon :icon="['fas', 'check-circle']" /> Brochure uploaded
              </span>
              <span v-else class="text-sm">Click to upload brochure</span>
            </label>
          </div>

          <!-- Brochure Preview/Info -->
          <div v-if="event.brochureUrl" class="mt-3 p-3 bg-white/5 border border-purple-400/20 rounded-lg flex items-center justify-between">
            <div class="flex items-center gap-2">
              <font-awesome-icon :icon="['fas', brochureFileType === 'pdf' ? 'file-pdf' : 'file-image']" class="text-purple-400" />
              <span class="text-sm text-gray-300">{{ brochureFileName || 'Brochure uploaded' }}</span>
            </div>
            <div class="flex gap-2">
              <a 
                :href="event.brochureUrl" 
                target="_blank" 
                class="text-xs px-3 py-1 bg-purple-500/20 hover:bg-purple-500/30 rounded-full transition-all"
              >
                <font-awesome-icon :icon="['fas', 'eye']" /> View
              </a>
              <button 
                type="button"
                @click="removeBrochure" 
                class="text-xs px-3 py-1 bg-red-500/20 hover:bg-red-500/30 rounded-full transition-all"
              >
                <font-awesome-icon :icon="['fas', 'trash']" /> Remove
              </button>
            </div>
          </div>
        </div>

        <!-- QR Code Generator -->
        <div>
          <label class="text-sm text-gray-400">Event QR Code (Optional)</label>
          <p class="text-xs text-gray-500 mb-2">Generate a QR code from a website link (e.g., registration page, event website)</p>
          
          <!-- QR Code URL Input -->
          <div class="flex gap-3">
            <input 
              v-model="qrCodeUrl" 
              type="url" 
              placeholder="Enter website URL (e.g., https://example.com/event)" 
              class="input-box flex-1"
            />
            <button 
              type="button"
              @click="generateQRCode"
              :disabled="generatingQR || !qrCodeUrl"
              class="px-4 py-2 bg-gradient-to-r from-purple-500 to-pink-500 text-white rounded-xl font-semibold hover:scale-105 transition-all disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100 whitespace-nowrap"
            >
              <font-awesome-icon 
                :icon="['fas', generatingQR ? 'spinner' : 'qrcode']" 
                :class="{ 'animate-spin': generatingQR }"
                class="mr-2"
              />
              {{ generatingQR ? 'Generating...' : 'Generate QR' }}
            </button>
          </div>

          <!-- QR Code Preview -->
          <div v-if="qrCodePreview" class="mt-4 p-4 bg-white/5 border border-purple-400/20 rounded-lg">
            <div class="flex flex-col md:flex-row gap-4 items-center">
              <!-- QR Code Image -->
              <div class="flex-shrink-0">
                <img :src="qrCodePreview" alt="QR Code Preview" class="w-32 h-32 rounded-lg border border-purple-400/30" />
              </div>
              
              <!-- QR Code Info and Actions -->
              <div class="flex-1 flex flex-col gap-2">
                <div class="flex items-center gap-2">
                  <font-awesome-icon :icon="['fas', 'qrcode']" class="text-purple-400" />
                  <span class="text-sm text-gray-300">QR Code generated for: {{ qrCodeUrl }}</span>
                </div>
                
                <div class="flex items-center gap-2 text-xs text-green-400">
                  <font-awesome-icon :icon="['fas', 'check-circle']" />
                  <span>Stored as base64 - Ready to save</span>
                </div>
                
                <div class="flex gap-2 flex-wrap mt-2">
                  <button 
                    type="button"
                    @click="downloadQR"
                    class="text-xs px-3 py-1 bg-blue-500/20 hover:bg-blue-500/30 rounded-full transition-all"
                  >
                    <font-awesome-icon :icon="['fas', 'download']" /> Download
                  </button>
                  
                  <button 
                    type="button"
                    @click="removeQRCode"
                    class="text-xs px-3 py-1 bg-red-500/20 hover:bg-red-500/30 rounded-full transition-all"
                  >
                    <font-awesome-icon :icon="['fas', 'trash']" /> Remove
                  </button>
                </div>
              </div>
            </div>
          </div>
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
          <!-- Custom Date Picker -->
          <div>
            <label class="text-sm text-gray-400">Date</label>
            <CustomDatePicker 
              v-model="event.eventDate" 
              :hasError="false"
              placeholder="Event Date"
            />
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
        <button 
          type="submit" 
          :disabled="uploadingBrochure"
          class="mt-4 py-3 rounded-full bg-gradient-to-r from-purple-500 to-pink-500 text-white font-semibold shadow-lg hover:scale-105 hover:shadow-xl transition-all disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100"
        >
          <font-awesome-icon :icon="['fas', isEditMode ? 'save' : 'plus']" class="mr-2" /> 
          {{ uploadingBrochure ? 'Uploading...' : (isEditMode ? 'Update Event' : 'Create Event') }}
        </button>
      </form>
    </main>

    <!-- 🦶 Footer -->
    <footer class="py-6 mt-10 text-center text-gray-400 border-t border-purple-400/20 bg-transparent">
      © {{ new Date().getFullYear() }} Eventra — Empower Your Events
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from "vue";
import { useRouter, useRoute } from 'vue-router';
import L from 'leaflet';
import { GeoSearchControl, OpenStreetMapProvider } from 'leaflet-geosearch';
import api from "../services/api.js";
import CustomDatePicker from '../components/CustomDatePicker.vue';
import GlobalAlert from '../components/GlobalAlert.vue';
import { useAlert } from '../composables/useAlert';
import { getAuth, onAuthStateChanged } from "firebase/auth";
import { uploadToCloudinary } from '../services/cloudinary';
import { generateQRCodeDataURL, downloadQRCode as downloadQRCodeHelper } from '../services/qrcode';

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
const route = useRoute();
const { alertState, showSuccess, showError, showWarning, showInfo } = useAlert();

const theme = ref(localStorage.getItem("theme") || "dark");
const loggedIn = ref(!!localStorage.getItem("token"));
const userDetails = ref(JSON.parse(localStorage.getItem("user") || "{}"));
const userProfilePic = ref(null);
const dropdownOpen = ref(false);
let map = null;
let marker = null;
const isEditMode = ref(false);
const editEventId = ref(null);

// Brochure upload state
const uploadingBrochure = ref(false);
const brochureInput = ref(null);
const brochureFileName = ref('');
const brochureFileType = ref('');

// QR Code state
const qrCodeUrl = ref('');
const qrCodePreview = ref('');
const generatingQR = ref(false);

// Contact input state
const newPhone = ref('');
const newEmail = ref('');

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
  brochureUrl: "",
  qrCodeUrl: "", // Stores the base64 data URL
  contactPhones: [], // Array of phone numbers
  contactEmails: [], // Array of email addresses
});

// --- CONTACT MANAGEMENT FUNCTIONS ---
const addPhone = () => {
  if (!newPhone.value.trim()) {
    showWarning('Please enter a phone number');
    return;
  }
  
  // Basic phone validation (adjust regex as needed)
  const phoneRegex = /^[\d\s\-\+\(\)]+$/;
  if (!phoneRegex.test(newPhone.value.trim())) {
    showWarning('Please enter a valid phone number');
    return;
  }
  
  if (event.value.contactPhones.includes(newPhone.value.trim())) {
    showWarning('This phone number is already added');
    return;
  }
  
  event.value.contactPhones.push(newPhone.value.trim());
  newPhone.value = '';
  showSuccess('Phone number added');
};

const removePhone = (index) => {
  event.value.contactPhones.splice(index, 1);
  showInfo('Phone number removed');
};

const addEmail = () => {
  if (!newEmail.value.trim()) {
    showWarning('Please enter an email address');
    return;
  }
  
  // Email validation
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(newEmail.value.trim())) {
    showWarning('Please enter a valid email address');
    return;
  }
  
  if (event.value.contactEmails.includes(newEmail.value.trim().toLowerCase())) {
    showWarning('This email is already added');
    return;
  }
  
  event.value.contactEmails.push(newEmail.value.trim().toLowerCase());
  newEmail.value = '';
  showSuccess('Email address added');
};

const removeEmail = (index) => {
  event.value.contactEmails.splice(index, 1);
  showInfo('Email address removed');
};

// --- BROCHURE UPLOAD HANDLER ---
const handleBrochureUpload = async (e) => {
  const file = e.target.files[0];
  if (!file) return;

  // Validate file type
  const validTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp', 'application/pdf'];
  if (!validTypes.includes(file.type)) {
    showError('Please upload a valid image (JPG, PNG, WEBP) or PDF file');
    return;
  }

  // Validate file size (max 10MB)
  const maxSize = 10 * 1024 * 1024; // 10MB
  if (file.size > maxSize) {
    showError('File size must be less than 10MB');
    return;
  }

  try {
    uploadingBrochure.value = true;
    showInfo('Uploading brochure...', 1000);

    // Upload to Cloudinary
    const url = await uploadToCloudinary(file);
    
    event.value.brochureUrl = url;
    brochureFileName.value = file.name;
    brochureFileType.value = file.type === 'application/pdf' ? 'pdf' : 'image';

    showSuccess('Brochure uploaded successfully!');
  } catch (error) {
    console.error('Error uploading brochure:', error);
    showError('Failed to upload brochure. Please try again.');
  } finally {
    uploadingBrochure.value = false;
  }
};

// --- REMOVE BROCHURE ---
const removeBrochure = () => {
  event.value.brochureUrl = '';
  brochureFileName.value = '';
  brochureFileType.value = '';
  if (brochureInput.value) {
    brochureInput.value.value = '';
  }
  showInfo('Brochure removed');
};

// --- QR CODE HANDLERS ---
const generateQRCode = async () => {
  if (!qrCodeUrl.value) {
    showWarning('Please enter a URL to generate QR code');
    return;
  }

  // Validate URL format
  try {
    new URL(qrCodeUrl.value);
  } catch (e) {
    showError('Please enter a valid URL (must include http:// or https://)');
    return;
  }

  try {
    generatingQR.value = true;
    showInfo('Generating QR code...', 1000);

    // Generate QR code as data URL (base64)
    const dataUrl = await generateQRCodeDataURL(qrCodeUrl.value, {
      width: 512,
      margin: 2,
      errorCorrectionLevel: 'H'
    });

    qrCodePreview.value = dataUrl;
    // Store the base64 directly in the event object
    event.value.qrCodeUrl = dataUrl;
    
    showSuccess('QR code generated successfully!');
  } catch (error) {
    console.error('Error generating QR code:', error);
    showError('Failed to generate QR code. Please try again.');
  } finally {
    generatingQR.value = false;
  }
};

const downloadQR = () => {
  if (!qrCodePreview.value) {
    showWarning('No QR code to download');
    return;
  }

  downloadQRCodeHelper(qrCodePreview.value, `event-qr-${Date.now()}.png`);
  showSuccess('QR code downloaded!');
};

const removeQRCode = () => {
  qrCodePreview.value = '';
  qrCodeUrl.value = '';
  event.value.qrCodeUrl = '';
  showInfo('QR code removed');
};


// --- LOAD EDIT DATA ---
const loadEditData = () => {
  // Check URL parameters
  const urlEdit = route.query.edit === 'true';
  const urlEventId = route.query.eventId;
  
  if (urlEdit && urlEventId) {
    isEditMode.value = true;
    editEventId.value = urlEventId;
    
    // Try to load from sessionStorage
    const storedEvent = sessionStorage.getItem('editEvent');
    if (storedEvent) {
      try {
        const editData = JSON.parse(storedEvent);
        
        // Parse the eventTimestamp to get date and time
        const eventDateTime = new Date(editData.eventTimestamp);
        const dateStr = eventDateTime.toISOString().split('T')[0]; // YYYY-MM-DD
        const timeStr = eventDateTime.toTimeString().slice(0, 5); // HH:MM
        
        // Parse phone numbers and emails from database format
        let contactPhones = [];
        let contactEmails = [];
        
        // If phone_no exists and is a string, split it
        if (editData.phone_no && typeof editData.phone_no === 'string') {
          contactPhones = editData.phone_no.split(',').map(p => p.trim()).filter(Boolean);
        } else if (Array.isArray(editData.contactPhones)) {
          contactPhones = editData.contactPhones;
        }
        
        // If email exists and is a string, split it
        if (editData.email && typeof editData.email === 'string') {
          contactEmails = editData.email.split(',').map(e => e.trim()).filter(Boolean);
        } else if (Array.isArray(editData.contactEmails)) {
          contactEmails = editData.contactEmails;
        }
        
        // Populate form fields
        event.value = {
          title: editData.title || "",
          description: editData.description || "",
          location: editData.location || "",
          latitude: editData.latitude || "",
          longitude: editData.longitude || "",
          amount: editData.amount || 0,
          capacity: editData.capacity || 100,
          status: editData.status || "ACTIVE",
          eventDate: dateStr,
          eventTime: timeStr,
          remainingCapacity: editData.remainingCapacity || editData.capacity || 100,
          brochureUrl: editData.brochureUrl || "",
          qrCodeUrl: editData.qrCodeUrl || "",
          contactPhones: contactPhones,
          contactEmails: contactEmails,
        };

        // Set brochure file info if exists
        if (editData.brochureUrl) {
          brochureFileName.value = 'Existing brochure';
          // Try to detect file type from URL
          if (editData.brochureUrl.toLowerCase().includes('.pdf')) {
            brochureFileType.value = 'pdf';
          } else {
            brochureFileType.value = 'image';
          }
        }

        // Set QR code preview if exists (base64 data URL)
        if (editData.qrCodeUrl) {
          qrCodePreview.value = editData.qrCodeUrl;
        }
        
        showInfo('Editing event: ' + editData.title, 2000);
        
        // Clear sessionStorage after loading
        sessionStorage.removeItem('editEvent');
      } catch (err) {
        console.error('Error loading edit data:', err);
        showError('Failed to load event data');
      }
    }
  }
};

// --- LIFECYCLE HOOK ---
onMounted(() => {
  applyTheme();
  checkUserProfile();
  loadEditData();
  nextTick(() => {
    initMap();
    // If editing and we have coordinates, set marker on map
    if (isEditMode.value && event.value.latitude && event.value.longitude) {
      const lat = parseFloat(event.value.latitude);
      const lng = parseFloat(event.value.longitude);
      if (!isNaN(lat) && !isNaN(lng)) {
        setTimeout(() => {
          updateLocation({ lat, lng });
        }, 500);
      }
    }
  });
});

// --- PROFILE PICTURE LOGIC ---
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
  if (!isEditMode.value) {
    showInfo('Location set successfully!', 2000);
  }
};

// --- API & FORM LOGIC ---
const submitEvent = async () => {
  if (uploadingBrochure.value) {
    showWarning('Please wait for brochure upload to complete');
    return;
  }

  if (isEditMode.value) {
    await updateEvent();
  } else {
    await createEvent();
  }
};

const createEvent = async () => {
  // Validation
  if (!event.value.latitude || !event.value.longitude) {
    showWarning('Please select a location on the map');
    return;
  }

  if (!event.value.eventDate) {
    showWarning('Please select an event date');
    return;
  }

  if (!event.value.eventTime) {
    showWarning('Please select an event time');
    return;
  }

  try {
    const token = localStorage.getItem("token");
    if (!token) {
      showError("You must be logged in to create an event");
      setTimeout(() => router.push('/auth'), 1500);
      return;
    }

    showInfo('Creating event...', 1000);

    const eventTimestamp = `${event.value.eventDate}T${event.value.eventTime}`;

    const payload = {
      title: event.value.title,
      description: event.value.description,
      location: event.value.location,
      latitude: event.value.latitude,
      longitude: event.value.longitude,
      amount: event.value.amount,
      capacity: event.value.capacity, 
      status: event.value.status,
      eventTimestamp: eventTimestamp,
      brochureUrl: event.value.brochureUrl || null,
      qrCodeUrl: event.value.qrCodeUrl || null,
      // Send as comma-separated strings to match database schema
      phoneNo: event.value.contactPhones.length > 0 ? event.value.contactPhones : [],
      email: event.value.contactEmails.length > 0 ? event.value.contactEmails : [],
    };

    await api.post("/api/events", payload, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    showSuccess("🎉 Event created successfully!");
    
    setTimeout(() => {
      router.push("/myevents");
    }, 1500);

  } catch (err) {
    console.error("Error creating event:", err.response?.data || err.message);
    
    const errorMessage = err.response?.data?.message 
      || err.response?.data 
      || "Failed to create event. Please try again.";
    
    showError(errorMessage);
  }
};

const updateEvent = async () => {
  // Validation
  if (!event.value.latitude || !event.value.longitude) {
    showWarning('Please select a location on the map');
    return;
  }

  if (!event.value.eventDate) {
    showWarning('Please select an event date');
    return;
  }

  if (!event.value.eventTime) {
    showWarning('Please select an event time');
    return;
  }

  try {
    const token = localStorage.getItem("token");
    if (!token) {
      showError("You must be logged in to update an event");
      setTimeout(() => router.push('/auth'), 1500);
      return;
    }

    showInfo('Updating event...', 1000);

    const eventTimestamp = `${event.value.eventDate}T${event.value.eventTime}`;

    const payload = {
      title: event.value.title,
      description: event.value.description,
      location: event.value.location,
      latitude: event.value.latitude,
      longitude: event.value.longitude,
      amount: event.value.amount,
      capacity: event.value.capacity,
      remainingCapacity: event.value.remainingCapacity,
      status: event.value.status,
      eventTimestamp: eventTimestamp,
      brochureUrl: event.value.brochureUrl || null,
      qrCodeUrl: event.value.qrCodeUrl || null,
      // Send as comma-separated strings to match database schema
      phoneNo: event.value.contactPhones.length > 0 ? event.value.contactPhones : [],
      email: event.value.contactEmails.length > 0 ? event.value.contactEmails : [],
    };

    await api.put(`/api/events/${editEventId.value}`, payload, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    showSuccess("✅ Event updated successfully!");
    
    setTimeout(() => {
      router.push("/myevents");
    }, 1500);

  } catch (err) {
    console.error("Error updating event:", err.response?.data || err.message);
    
    const errorMessage = err.response?.data?.message 
      || err.response?.data 
      || "Failed to update event. Please try again.";
    
    showError(errorMessage);
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
  updateMapTiles();
};

const toggleDropdown = () => dropdownOpen.value = !dropdownOpen.value;

const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  loggedIn.value = false;
  userProfilePic.value = null;
  showInfo('Logged out successfully');
  setTimeout(() => {
    router.push("/auth");
  }, 1000);
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

/* Input styles matching auth form */
.input-box {
  width: 100%;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(150, 0, 255, 0.2);
  color: inherit;
  padding: 0.75rem 1rem;
  border-radius: 0.75rem;
  outline: none;
  transition: all 0.3s ease;
  margin-top: 0.4rem;
}

.input-box:focus {
  border-color: rgba(200, 0, 255, 0.6);
  background: rgba(255, 255, 255, 0.15);
}

/* Navigation links */
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

/* Auth input class for CustomDatePicker compatibility */
.auth-input {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(150, 0, 255, 0.2);
  color: inherit;
  padding: 0.75rem 1rem;
  border-radius: 0.75rem;
  outline: none;
  transition: all 0.3s ease;
  width: 100%;
  cursor: pointer;
}

.auth-input:focus {
  border-color: rgba(200, 0, 255, 0.6);
  background: rgba(255, 255, 255, 0.15);
}

.input-error {
  border-color: #ef4444 !important;
  box-shadow: 0 0 0 1px #ef4444;
}

/* Spinner animation for upload */
@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.animate-spin {
  animation: spin 1s linear infinite;
}
</style>