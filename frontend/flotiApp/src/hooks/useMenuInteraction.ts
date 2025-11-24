import { useState } from 'react';

/**
 * T란?
 * - 대상 데이터의 타입
 * - 기본값을 지정할 수 있음 (예: T = number)
 */

export function useMenuInteraction<T = number>() {
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