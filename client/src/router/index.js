import { createRouter, createWebHistory } from "vue-router";

import LandingPage from "../pages/LandingPage.vue";
import HomePage from "../pages/HomePage.vue";
import AuthPage from "../pages/AuthPage.vue";
import EventsPage from "../pages/EventsPage.vue";
import MyEventsPage from "../pages/MyEventsPage.vue";
import PaymentsPage from "../pages/PaymentsPage.vue";
import CreateEventPage from "@/pages/CreateEventPage.vue";
import EventDetailsPage from "@/pages/EventDetailsPage.vue";
import InsightsPage from "@/pages/InsightsPage.vue";
import ParticipantTicketPage from "@/views/ParticipantTicketPage.vue";
import OrganizerScannerPage from "@/views/OrganizerScannerPage.vue";

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
    path: "/events/:id",
    name: "EventDetailsPage",
    component: EventDetailsPage,
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
  },
  {
    path: "/insights",
    name: "InsightsPage",
    component: InsightsPage,
  },
  // Participant ticket page
  {
    path: '/participant/ticket/:eventId',
    name: 'ParticipantTicket',
    component: ParticipantTicketPage,
    meta: { requiresAuth: true }
  },
  
  // Organizer scanner page
  {
    path: '/organizer/scanner/:eventId',
    name: 'OrganizerScanner',
    component: OrganizerScannerPage,
    meta: { requiresAuth: true }
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
