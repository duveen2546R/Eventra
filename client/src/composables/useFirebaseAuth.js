// Firebase Authentication Composable
import { ref } from 'vue';
import { 
  signInWithPopup, 
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
  updateProfile,
  signOut,
  onAuthStateChanged
} from 'firebase/auth';
import { auth, googleProvider } from '../../firebase.config';

export function useFirebaseAuth() {
  const user = ref(null);
  const loading = ref(false);
  const error = ref(null);

  // Listen for auth state changes
  onAuthStateChanged(auth, (currentUser) => {
    user.value = currentUser;
  });

  // Sign in with Google
  const signInWithGoogle = async () => {
    loading.value = true;
    error.value = null;
    try {
      const result = await signInWithPopup(auth, googleProvider);
      const userData = {
        uid: result.user.uid,
        email: result.user.email,
        displayName: result.user.displayName,
        photoURL: result.user.photoURL,
        emailVerified: result.user.emailVerified
      };
      
      // Store user data in localStorage
      localStorage.setItem('firebaseUser', JSON.stringify(userData));
      
      return { success: true, user: userData };
    } catch (err) {
      error.value = err.message;
      console.error('Google Sign-In Error:', err);
      return { success: false, error: err.message };
    } finally {
      loading.value = false;
    }
  };

  // Email/Password Sign In
  const signInWithEmail = async (email, password) => {
    loading.value = true;
    error.value = null;
    try {
      const result = await signInWithEmailAndPassword(auth, email, password);
      const userData = {
        uid: result.user.uid,
        email: result.user.email,
        displayName: result.user.displayName,
        photoURL: result.user.photoURL,
        emailVerified: result.user.emailVerified
      };
      
      localStorage.setItem('firebaseUser', JSON.stringify(userData));
      
      return { success: true, user: userData };
    } catch (err) {
      error.value = err.message;
      let friendlyMessage = 'Invalid email or password.';
      
      if (err.code === 'auth/user-not-found') {
        friendlyMessage = 'No account found with this email.';
      } else if (err.code === 'auth/wrong-password') {
        friendlyMessage = 'Incorrect password.';
      } else if (err.code === 'auth/invalid-email') {
        friendlyMessage = 'Invalid email address.';
      } else if (err.code === 'auth/user-disabled') {
        friendlyMessage = 'This account has been disabled.';
      }
      
      return { success: false, error: friendlyMessage };
    } finally {
      loading.value = false;
    }
  };

  // Email/Password Registration
  const registerWithEmail = async (email, password, displayName) => {
    loading.value = true;
    error.value = null;
    try {
      const result = await createUserWithEmailAndPassword(auth, email, password);
      
      // Update profile with display name
      if (displayName) {
        await updateProfile(result.user, { displayName });
      }
      
      const userData = {
        uid: result.user.uid,
        email: result.user.email,
        displayName: displayName || result.user.displayName,
        photoURL: result.user.photoURL,
        emailVerified: result.user.emailVerified
      };
      
      localStorage.setItem('firebaseUser', JSON.stringify(userData));
      
      return { success: true, user: userData };
    } catch (err) {
      error.value = err.message;
      let friendlyMessage = 'Registration failed.';
      
      if (err.code === 'auth/email-already-in-use') {
        friendlyMessage = 'This email is already registered.';
      } else if (err.code === 'auth/invalid-email') {
        friendlyMessage = 'Invalid email address.';
      } else if (err.code === 'auth/weak-password') {
        friendlyMessage = 'Password should be at least 6 characters.';
      }
      
      return { success: false, error: friendlyMessage };
    } finally {
      loading.value = false;
    }
  };

  // Sign Out
  const logout = async () => {
    loading.value = true;
    error.value = null;
    try {
      await signOut(auth);
      localStorage.removeItem('firebaseUser');
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      return { success: true };
    } catch (err) {
      error.value = err.message;
      return { success: false, error: err.message };
    } finally {
      loading.value = false;
    }
  };

  return {
    user,
    loading,
    error,
    signInWithGoogle,
    signInWithEmail,
    registerWithEmail,
    logout
  };
}