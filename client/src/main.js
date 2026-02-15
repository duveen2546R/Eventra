import { createApp } from "vue";
import axios from "axios";
import App from "./App.vue";
import router from "./router";
import "./index.css"; // Tailwind entry

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL;
if (apiBaseUrl) {
  axios.defaults.baseURL = apiBaseUrl.replace(/\/$/, "");
}

const app = createApp(App);
app.use(router);
app.mount("#app");


// --- ADD/VERIFY THESE TWO LINES ---
import 'leaflet/dist/leaflet.css';
import 'leaflet-geosearch/dist/geosearch.css';
