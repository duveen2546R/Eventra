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
    </header>

    <!-- 📊 Insights Content -->
    <main class="pt-28 px-6 md:px-16 relative z-20">
      <h1 class="text-4xl md:text-6xl font-bold mb-4">
        Your <span class="text-purple-400">Insights</span>
      </h1>
      <p class="text-gray-400 max-w-2xl mb-10">
        Visualize your payment activities and event performance.
      </p>

      <div class="grid md:grid-cols-2 gap-8">
        <!-- Total Amounts -->
        <div class="backdrop-blur-lg bg-white/5 border border-purple-400/20 rounded-2xl shadow-lg p-6 flex flex-col items-center justify-center">
          <h2 class="text-xl font-semibold text-purple-300 mb-4">Total Received</h2>
          <p class="text-5xl font-bold text-green-400">₹{{ totalReceived.toFixed(2) }}</p>
        </div>
        <div class="backdrop-blur-lg bg-white/5 border border-purple-400/20 rounded-2xl shadow-lg p-6 flex flex-col items-center justify-center">
          <h2 class="text-xl font-semibold text-purple-300 mb-4">Total Spent</h2>
          <p class="text-5xl font-bold text-red-400">₹{{ totalSpent.toFixed(2) }}</p>
        </div>

        <!-- Monthly Payments Received Chart -->
        <div class="backdrop-blur-lg bg-white/5 border border-purple-400/20 rounded-2xl shadow-lg p-6">
          <h2 class="text-xl font-semibold text-purple-300 mb-4">Monthly Payments Received</h2>
          <canvas id="paymentsReceivedChart"></canvas>
        </div>

        <!-- Monthly Payments Spent Chart -->
        <div class="backdrop-blur-lg bg-white/5 border border-purple-400/20 rounded-2xl shadow-lg p-6">
          <h2 class="text-xl font-semibold text-purple-300 mb-4">Monthly Payments Spent</h2>
          <canvas id="paymentsSpentChart"></canvas>
        </div>

        <!-- Payments Received by Event Chart -->
        <div class="backdrop-blur-lg bg-white/5 border border-purple-400/20 rounded-2xl shadow-lg p-6">
          <h2 class="text-xl font-semibold text-purple-300 mb-4">Payments Received by Event</h2>
          <canvas id="receivedByEventChart"></canvas>
        </div>

        <!-- Payments Spent by Event Chart -->
        <div class="backdrop-blur-lg bg-white/5 border border-purple-400/20 rounded-2xl shadow-lg p-6">
          <h2 class="text-xl font-semibold text-purple-300 mb-4">Payments Spent by Event</h2>
          <canvas id="spentByEventChart"></canvas>
        </div>
      </div>
    </main>

    <!-- 🦶 Footer -->
    <footer class="py-6 text-center text-gray-400 border-t border-purple-400/20 bg-transparent">
      © {{ new Date().getFullYear() }} Eventra — Empower Your Events
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import Chart from 'chart.js/auto';

const theme = ref(localStorage.getItem('theme') || 'dark');
const paymentsReceivedChart = ref(null);
const paymentsSpentChart = ref(null);

const applyTheme = () => {
  document.documentElement.classList.toggle('dark', theme.value === 'dark');
  localStorage.setItem('theme', theme.value);
};

const fetchInsights = async () => {
  try {
    const userId = JSON.parse(localStorage.getItem('user')).user_id;
    const token = localStorage.getItem('token');
    const response = await axios.get(`/api/payments/insights/${userId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    const { monthlyReceived, monthlySpent, totalReceived, totalSpent, receivedByEvent, spentByEvent } = response.data;

    renderMonthlyCharts(monthlyReceived, monthlySpent);
    renderTotalAmounts(totalReceived, totalSpent);
    renderEventCharts(receivedByEvent, spentByEvent);
  } catch (err) {
    console.error('Error fetching insights:', err);
  }
};

const renderMonthlyCharts = (monthlyReceivedData, monthlySpentData) => {
  const receivedCtx = document.getElementById('paymentsReceivedChart').getContext('2d');
  if (paymentsReceivedChart.value) paymentsReceivedChart.value.destroy();
  paymentsReceivedChart.value = new Chart(receivedCtx, {
    type: 'bar',
    data: {
      labels: monthlyReceivedData.map(data => data.month),
      datasets: [{
        label: 'Amount Received',
        data: monthlyReceivedData.map(data => data.amount),
        backgroundColor: 'rgba(124, 58, 237, 0.6)',
        borderColor: 'rgba(124, 58, 237, 1)',
        borderWidth: 1
      }]
    },
    options: {
      responsive: true,
      scales: {
        y: {
          beginAtZero: true
        }
      }
    }
  });

  const spentCtx = document.getElementById('paymentsSpentChart').getContext('2d');
  if (paymentsSpentChart.value) paymentsSpentChart.value.destroy();
  paymentsSpentChart.value = new Chart(spentCtx, {
    type: 'bar',
    data: {
      labels: monthlySpentData.map(data => data.month),
      datasets: [{
        label: 'Amount Spent',
        data: monthlySpentData.map(data => data.amount),
        backgroundColor: 'rgba(236, 72, 153, 0.6)',
        borderColor: 'rgba(236, 72, 153, 1)',
        borderWidth: 1
      }]
    },
    options: {
      responsive: true,
      scales: {
        y: {
          beginAtZero: true
        }
      }
    }
  });
};

const totalReceived = ref(0);
const totalSpent = ref(0);
const receivedByEventChart = ref(null);
const spentByEventChart = ref(null);

const renderTotalAmounts = (received, spent) => {
  totalReceived.value = received;
  totalSpent.value = spent;
};

const renderEventCharts = (receivedByEventData, spentByEventData) => {
  const receivedByEventCtx = document.getElementById('receivedByEventChart').getContext('2d');
  if (receivedByEventChart.value) receivedByEventChart.value.destroy();
  receivedByEventChart.value = new Chart(receivedByEventCtx, {
    type: 'pie',
    data: {
      labels: receivedByEventData.map(data => data.eventTitle),
      datasets: [{
        data: receivedByEventData.map(data => data.amount),
        backgroundColor: receivedByEventData.map((_, i) => `hsl(${i * 60}, 70%, 60%)`),
      }]
    },
    options: {
      responsive: true,
      plugins: {
        legend: {
          position: 'top',
        },
        title: {
          display: true,
          text: 'Received by Event'
        }
      }
    }
  });

  const spentByEventCtx = document.getElementById('spentByEventChart').getContext('2d');
  if (spentByEventChart.value) spentByEventChart.value.destroy();
  spentByEventChart.value = new Chart(spentByEventCtx, {
    type: 'pie',
    data: {
      labels: spentByEventData.map(data => data.eventTitle),
      datasets: [{
        data: spentByEventData.map(data => data.amount),
        backgroundColor: spentByEventData.map((_, i) => `hsl(${i * 60 + 30}, 70%, 60%)`),
      }]
    },
    options: {
      responsive: true,
      plugins: {
        legend: {
          position: 'top',
        },
        title: {
          display: true,
          text: 'Spent by Event'
        }
      }
    }
  });
};

onMounted(() => {
  applyTheme();
  fetchInsights();
});
</script>

<style scoped>
/* Add any component-specific styles here */
</style>
