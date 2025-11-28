import { Href, useRouter } from "expo-router";

export const useNavigation = () => {
    const router = useRouter();

    // 화면 이동
    const navigateTo = (path: string, replace = false) => {
        if (replace) router.replace(path as Href);
        else router.push(path as Href);
    };

    // 파라미터 포함 화면 이동
    const navigateWithParams = (path: string, params: Record<string, string | number>, replace = false) => {
        if (replace) router.replace({ pathname: path as any, params });
        else router.push({ pathname: path as any, params });
    };

    // 뒤로가기 안전 처리
    const goBackSafely = (path: string = '/') => {
        if (router.canGoBack()) router.back();
        else router.replace(path as Href);
    };

    return { navigateTo, navigateWithParams, goBackSafely };
};