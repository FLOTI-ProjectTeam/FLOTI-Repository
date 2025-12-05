import { useState } from 'react';

export function useMenuInteraction<T = number /* 기본값 지정 가능 */>() {
  const [openMenuId, setOpenMenuId] = useState<number | null>(null);
  const [target, setTarget] = useState<T | null>(null);

  const selectTarget = (item: T) => {
    setTarget(item);
    setOpenMenuId(null);
  };

  const clearTarget = () => {
    setTarget(null);
    setOpenMenuId(null);
  };

  return { openMenuId, target, setOpenMenuId, selectTarget, clearTarget };
}