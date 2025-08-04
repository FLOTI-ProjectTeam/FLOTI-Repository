import { SafeAreaView } from "react-native-safe-area-context";
import { View, Text } from "react-native";

export default function HomeScreen() {
  return (
    <SafeAreaView>
      <View className="flex justify-center items-center">
        <Text className="text-5xl font-bold">Hello World!</Text>
      </View>
    </SafeAreaView>
  );
}