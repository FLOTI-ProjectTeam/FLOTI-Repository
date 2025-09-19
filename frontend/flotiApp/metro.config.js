const { getDefaultConfig } = require("expo/metro-config");
const { withNativeWind } = require('nativewind/metro');
 
const config = getDefaultConfig(__dirname)

// NativeWind 적용
module.exports = withNativeWind(config, { input: "./app/global.css" });