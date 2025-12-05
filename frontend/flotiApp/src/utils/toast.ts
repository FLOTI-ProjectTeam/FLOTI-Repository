import Toast from 'react-native-toast-message';

type ToastType = 'success' | 'error';

export const showToast = (message: string, type: ToastType = 'success') => {
  Toast.show({
    type: `custom_${type}`,
    props: { message }
  });
};