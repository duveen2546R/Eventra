<template>
  <div
    class="min-h-screen flex flex-col transition-all duration-700"
    :class="theme === 'dark'
      ? 'bg-[#050505] text-white'
      : 'bg-gray-100 text-gray-900'"
  >
    <router-view />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";

// 🌙 Theme management
const theme = ref(localStorage.getItem("theme") || "dark");

const applyTheme = () => {
  const html = document.documentElement;
  theme.value === "dark"
    ? html.classList.add("dark")
    : html.classList.remove("dark");
  localStorage.setItem("theme", theme.value);
};

onMounted(applyTheme);
</script>

<style>
html,
body {
  font-family: 'Inter', sans-serif;
  margin: 0;
  padding: 0;
  scroll-behavior: smooth;
}
</style>
