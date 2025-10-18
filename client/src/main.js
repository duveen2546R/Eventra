import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import "./index.css"; // Tailwind entry


const app = createApp(App);
app.use(router);
app.mount("#app");


// --- ADD/VERIFY THESE TWO LINES ---
import 'leaflet/dist/leaflet.css';
import 'leaflet-geosearch/dist/geosearch.css';
