export const clamp = (v: number, min: number, max: number) =>
    Math.min(Math.max(v, min), max);
  
export const computePopupPosition = (
    pageX: number, pageY: number,
    screenWidth: number, screenHeight: number
) => {
    const POPUP_SIZE = 60;
    const MARGIN = 16;

    const baseX = pageX - POPUP_SIZE / 2;
    const baseY = pageY - POPUP_SIZE / 2;

    const x = clamp(baseX, MARGIN, screenWidth - POPUP_SIZE - MARGIN);
    const y = clamp(baseY, MARGIN, screenHeight - POPUP_SIZE - MARGIN);

    return { x, y };
};