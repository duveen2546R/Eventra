import { initializeApp } from "firebase/app";
import { getAuth, GoogleAuthProvider } from "firebase/auth";
import { getAnalytics } from "firebase/analytics";

const firebaseConfig = {
    apiKey: "AIzaSyDvxhzJnxhfj0gZr69W2WGIZr3IMp_8gBU",
    authDomain: "eventra-c3703.firebaseapp.com",
    projectId: "eventra-c3703",
    storageBucket: "eventra-c3703.firebasestorage.app",
    messagingSenderId: "599432520576",
    appId: "1:599432520576:web:896a35d7649a1e2f085765",
    measurementId: "G-TY7LP8S6G2"
};

const app = initializeApp(firebaseConfig);

export const auth = getAuth(app);
export const googleProvider = new GoogleAuthProvider();
// Optional: Configure Google provider settings
googleProvider.setCustomParameters({
  prompt: 'select_account' // Forces account selection even if one account is available
});

export default app;

