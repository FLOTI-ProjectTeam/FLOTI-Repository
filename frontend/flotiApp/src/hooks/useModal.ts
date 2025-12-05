import { useState } from 'react';

export type ModalType = string;

export function useModal<T extends ModalType>() {
  const [modalVisible, setModalVisible] = useState(false);
  const [type, setType] = useState<T | null>(null);

  const openModal = (modalType: T | null = null) => {
    setType(modalType);
    setModalVisible(true);
  };

  const closeModal = () => {
    setModalVisible(false);
    setType(null);
  };

  return { modalVisible, type, openModal, closeModal, setType };
}