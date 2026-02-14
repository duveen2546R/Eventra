import { ref } from 'vue';

// Global state for alerts
const alertState = ref({
  show: false,
  message: '',
  type: 'info',
  duration: 3000
});

export function useAlert() {
  const showAlert = (message, type = 'info', duration = 3000) => {
    alertState.value = {
      show: true,
      message,
      type,
      duration
    };
  };

  const showSuccess = (message, duration = 3000) => {
    showAlert(message, 'success', duration);
  };

  const showError = (message, duration = 3000) => {
    showAlert(message, 'error', duration);
  };

  const showInfo = (message, duration = 3000) => {
    showAlert(message, 'info', duration);
  };

  const showWarning = (message, duration = 3000) => {
    showAlert(message, 'warning', duration);
  };

  const hideAlert = () => {
    alertState.value.show = false;
  };

  return {
    alertState,
    showAlert,
    showSuccess,
    showError,
    showInfo,
    showWarning,
    hideAlert
  };
}