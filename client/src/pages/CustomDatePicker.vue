<template>
  <div class="custom-date-picker" ref="datePickerRef">
    <div class="relative">
      <input
        type="text"
        :value="formattedDate"
        @click="toggleCalendar"
        :placeholder="placeholder"
        readonly
        class="auth-input"
        :class="{ 'input-error': hasError }"
      />
      <button
        type="button"
        @click="toggleCalendar"
        class="calendar-icon-button"
        aria-label="Open calendar"
      >
        <font-awesome-icon :icon="['fas', 'calendar-days']" />
      </button>
    </div>

    <!-- Custom Calendar Dropdown -->
    <transition name="calendar-fade">
      <div v-if="isOpen" class="calendar-dropdown">
        <!-- Calendar Header -->
        <div class="calendar-header">
          <button type="button" @click="previousMonth" class="calendar-nav-btn">
            <font-awesome-icon :icon="['fas', 'chevron-left']" />
          </button>
          
          <div class="calendar-month-year">
            <select v-model="currentMonth" class="calendar-select">
              <option v-for="(month, index) in months" :key="index" :value="index">
                {{ month }}
              </option>
            </select>
            <select v-model="currentYear" class="calendar-select">
              <option v-for="year in yearRange" :key="year" :value="year">
                {{ year }}
              </option>
            </select>
          </div>

          <button type="button" @click="nextMonth" class="calendar-nav-btn">
            <font-awesome-icon :icon="['fas', 'chevron-right']" />
          </button>
        </div>

        <!-- Weekday Headers -->
        <div class="calendar-weekdays">
          <div v-for="day in weekdays" :key="day" class="calendar-weekday">
            {{ day }}
          </div>
        </div>

        <!-- Calendar Days Grid -->
        <div class="calendar-days">
          <button
            type="button"
            v-for="day in calendarDays"
            :key="`${day.date}-${day.month}`"
            @click="selectDate(day)"
            :class="[
              'calendar-day',
              {
                'other-month': !day.isCurrentMonth,
                'selected': day.isSelected,
                'today': day.isToday
              }
            ]"
            :disabled="!day.isCurrentMonth"
          >
            {{ day.day }}
          </button>
        </div>

        <!-- Calendar Footer -->
        <div class="calendar-footer">
          <button type="button" @click="clearDate" class="calendar-footer-btn clear-btn">
            Clear
          </button>
          <button type="button" @click="selectToday" class="calendar-footer-btn today-btn">
            Today
          </button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue';
import { library } from '@fortawesome/fontawesome-svg-core';
import { fas } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';

library.add(fas);

const props = defineProps({
  modelValue: String,
  hasError: Boolean,
  placeholder: {
    type: String,
    default: 'Select Date'
  }
});

const emit = defineEmits(['update:modelValue']);

const isOpen = ref(false);
const datePickerRef = ref(null);
const selectedDate = ref(props.modelValue || null);

const currentMonth = ref(new Date().getMonth());
const currentYear = ref(new Date().getFullYear());

const months = [
  'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December'
];

const weekdays = ['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'];

// Generate year range (100 years back from current year)
const yearRange = computed(() => {
  const currentYear = new Date().getFullYear();
  const years = [];
  for (let year = currentYear; year >= currentYear - 100; year--) {
    years.push(year);
  }
  return years;
});

// Format date for display
const formattedDate = computed(() => {
  if (!selectedDate.value) return '';
  const date = new Date(selectedDate.value);
  return date.toLocaleDateString('en-US', { 
    month: '2-digit', 
    day: '2-digit', 
    year: 'numeric' 
  });
});

// Generate calendar days
const calendarDays = computed(() => {
  const year = currentYear.value;
  const month = currentMonth.value;
  
  const firstDay = new Date(year, month, 1);
  const lastDay = new Date(year, month + 1, 0);
  const prevLastDay = new Date(year, month, 0);
  
  const firstDayOfWeek = firstDay.getDay();
  const lastDate = lastDay.getDate();
  const prevLastDate = prevLastDay.getDate();
  
  const days = [];
  
  // Previous month days
  for (let i = firstDayOfWeek - 1; i >= 0; i--) {
    days.push({
      day: prevLastDate - i,
      month: month - 1,
      year: month === 0 ? year - 1 : year,
      isCurrentMonth: false,
      isSelected: false,
      isToday: false,
      date: new Date(month === 0 ? year - 1 : year, month - 1, prevLastDate - i)
    });
  }
  
  // Current month days
  for (let day = 1; day <= lastDate; day++) {
    const date = new Date(year, month, day);
    const dateString = date.toISOString().split('T')[0];
    const today = new Date();
    const isToday = date.toDateString() === today.toDateString();
    
    days.push({
      day,
      month,
      year,
      isCurrentMonth: true,
      isSelected: selectedDate.value === dateString,
      isToday,
      date
    });
  }
  
  // Next month days to complete the grid
  const remainingDays = 42 - days.length;
  for (let day = 1; day <= remainingDays; day++) {
    days.push({
      day,
      month: month + 1,
      year: month === 11 ? year + 1 : year,
      isCurrentMonth: false,
      isSelected: false,
      isToday: false,
      date: new Date(month === 11 ? year + 1 : year, month + 1, day)
    });
  }
  
  return days;
});

const toggleCalendar = () => {
  isOpen.value = !isOpen.value;
};

const selectDate = (day) => {
  if (!day.isCurrentMonth) return;
  
  const dateString = day.date.toISOString().split('T')[0];
  selectedDate.value = dateString;
  emit('update:modelValue', dateString);
  isOpen.value = false;
};

const clearDate = () => {
  selectedDate.value = null;
  emit('update:modelValue', '');
  isOpen.value = false;
};

const selectToday = () => {
  const today = new Date();
  const dateString = today.toISOString().split('T')[0];
  selectedDate.value = dateString;
  emit('update:modelValue', dateString);
  currentMonth.value = today.getMonth();
  currentYear.value = today.getFullYear();
};

const previousMonth = () => {
  if (currentMonth.value === 0) {
    currentMonth.value = 11;
    currentYear.value--;
  } else {
    currentMonth.value--;
  }
};

const nextMonth = () => {
  if (currentMonth.value === 11) {
    currentMonth.value = 0;
    currentYear.value++;
  } else {
    currentMonth.value++;
  }
};

// Close calendar when clicking outside
const handleClickOutside = (event) => {
  if (datePickerRef.value && !datePickerRef.value.contains(event.target)) {
    isOpen.value = false;
  }
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
  
  // Set current month/year based on selected date if exists
  if (selectedDate.value) {
    const date = new Date(selectedDate.value);
    currentMonth.value = date.getMonth();
    currentYear.value = date.getFullYear();
  }
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});

watch(() => props.modelValue, (newValue) => {
  selectedDate.value = newValue;
});
</script>

<style scoped>
/* Auth input class - matches your form fields */
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

/* Custom Date Picker Container */
.custom-date-picker {
  position: relative;
  width: 100%;
}

/* Calendar Icon Button */
.calendar-icon-button {
  position: absolute;
  right: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: rgba(168, 85, 247, 0.7);
  cursor: pointer;
  padding: 0.25rem;
  transition: all 0.3s ease;
  font-size: 1rem;
  z-index: 2;
}

.calendar-icon-button:hover {
  color: rgba(236, 72, 153, 0.9);
  transform: translateY(-50%) scale(1.1);
}

/* Calendar Dropdown - COMPACT SIZE */
.calendar-dropdown {
  position: absolute;
  top: calc(100% + 0.5rem);
  left: 0;
  right: 0;
  background: linear-gradient(135deg, rgba(20, 20, 20, 0.98) 0%, rgba(30, 20, 40, 0.98) 100%);
  border: 1px solid rgba(168, 85, 247, 0.3);
  border-radius: 0.75rem;
  padding: 0.75rem;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5), 0 0 40px rgba(168, 85, 247, 0.2);
  z-index: 1000;
  backdrop-filter: blur(20px);
  min-width: 280px;
  max-width: 320px;
}

/* Light mode dropdown */
:global(.bg-gray-100) .calendar-dropdown {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.98) 0%, rgba(250, 245, 255, 0.98) 100%);
  border-color: rgba(168, 85, 247, 0.2);
}

/* Calendar Header - COMPACT */
.calendar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 0.75rem;
  gap: 0.5rem;
}

.calendar-nav-btn {
  background: rgba(168, 85, 247, 0.15);
  border: 1px solid rgba(168, 85, 247, 0.3);
  color: rgba(236, 72, 153, 0.9);
  padding: 0.4rem 0.6rem;
  border-radius: 0.4rem;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.875rem;
}

.calendar-nav-btn:hover {
  background: linear-gradient(to right, rgba(168, 85, 247, 0.3), rgba(236, 72, 153, 0.3));
  border-color: rgba(236, 72, 153, 0.5);
  transform: scale(1.05);
}

/* Month/Year Selectors - COMPACT */
.calendar-month-year {
  display: flex;
  gap: 0.4rem;
  flex: 1;
  justify-content: center;
}

.calendar-select {
  background: rgba(168, 85, 247, 0.1);
  border: 1px solid rgba(168, 85, 247, 0.3);
  color: inherit;
  padding: 0.4rem 0.3rem;
  border-radius: 0.4rem;
  cursor: pointer;
  font-size: 0.8rem;
  transition: all 0.3s ease;
  outline: none;
}

.calendar-select:hover {
  border-color: rgba(236, 72, 153, 0.5);
  background: rgba(168, 85, 247, 0.15);
}

.calendar-select:focus {
  border-color: rgba(236, 72, 153, 0.8);
  box-shadow: 0 0 0 2px rgba(236, 72, 153, 0.2);
}

/* Weekday Headers - COMPACT */
.calendar-weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 0.15rem;
  margin-bottom: 0.4rem;
}

.calendar-weekday {
  text-align: center;
  font-size: 0.7rem;
  font-weight: 600;
  color: rgba(168, 85, 247, 0.8);
  padding: 0.3rem 0;
}

/* Calendar Days Grid - COMPACT */
.calendar-days {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 0.15rem;
}

.calendar-day {
  aspect-ratio: 1;
  border: 1px solid rgba(168, 85, 247, 0.2);
  background: rgba(255, 255, 255, 0.05);
  color: inherit;
  border-radius: 0.4rem;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.8rem;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.calendar-day:hover:not(:disabled) {
  background: linear-gradient(135deg, rgba(168, 85, 247, 0.3), rgba(236, 72, 153, 0.3));
  border-color: rgba(236, 72, 153, 0.5);
  transform: scale(1.1);
  z-index: 10;
}

.calendar-day.other-month {
  opacity: 0.3;
  pointer-events: none;
}

.calendar-day.today {
  border-color: rgba(168, 85, 247, 0.8);
  background: rgba(168, 85, 247, 0.15);
  font-weight: 700;
}

.calendar-day.selected {
  background: linear-gradient(to right, #a855f7, #ec4899);
  border-color: #ec4899;
  color: white;
  font-weight: 700;
  box-shadow: 0 0 12px rgba(236, 72, 153, 0.5);
  transform: scale(1.05);
}

.calendar-day.selected:hover {
  transform: scale(1.15);
}

/* Calendar Footer - COMPACT */
.calendar-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 0.75rem;
  padding-top: 0.75rem;
  border-top: 1px solid rgba(168, 85, 247, 0.2);
  gap: 0.4rem;
}

.calendar-footer-btn {
  flex: 1;
  padding: 0.4rem 0.75rem;
  border-radius: 0.4rem;
  cursor: pointer;
  font-size: 0.8rem;
  font-weight: 600;
  transition: all 0.3s ease;
}

.clear-btn {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(168, 85, 247, 0.3);
  color: rgba(236, 72, 153, 0.8);
}

.clear-btn:hover {
  background: rgba(168, 85, 247, 0.15);
  border-color: rgba(236, 72, 153, 0.5);
  transform: scale(1.05);
}

.today-btn {
  background: linear-gradient(to right, rgba(168, 85, 247, 0.3), rgba(236, 72, 153, 0.3));
  border: 1px solid rgba(236, 72, 153, 0.5);
  color: white;
}

.today-btn:hover {
  background: linear-gradient(to right, #a855f7, #ec4899);
  box-shadow: 0 0 12px rgba(236, 72, 153, 0.3);
  transform: scale(1.05);
}

/* Calendar Fade Transition */
.calendar-fade-enter-active,
.calendar-fade-leave-active {
  transition: all 0.3s ease;
}

.calendar-fade-enter-from {
  opacity: 0;
  transform: translateY(-10px) scale(0.95);
}

.calendar-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px) scale(0.95);
}
</style>