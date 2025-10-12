import { createRouter, createWebHistory } from "vue-router";

import LandingPage from "../pages/LandingPage.vue";
import HomePage from "../pages/HomePage.vue";
import AuthPage from "../pages/AuthPage.vue";
import EventsPage from "../pages/EventsPage.vue";
import MyEventsPage from "../pages/MyEventsPage.vue";
import PaymentsPage from "../pages/PaymentsPage.vue";
import CreateEventPage from "@/pages/CreateEventPage.vue";

const routes = [
  {
    path: "/",
    name: "LandingPage",
    component: LandingPage, // 👈 First page on load
  },
  {
    path: "/home",
    name: "HomePage",
    component: HomePage,
  },
  {
    path: "/events",
    name: "EventsPage",
    component: EventsPage,
  },
  {
    path: "/myevents",
    name: "MyEventsPage",
    component: MyEventsPage,
  },
  {
    path: "/payments",
    name: "PaymentsPage",
    component: PaymentsPage,
  },
  {
    path: "/auth",
    name: "AuthPage",
    component: AuthPage,
  },
  {
    path: "/create",
    name: "CreateEventPage",
    component: CreateEventPage,
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    // Smooth scroll to top when changing routes
    return { top: 0, behavior: "smooth" };
  },
});

export default router;
