<template>
  <v-mini-weather>
    <template #default="{ weather, icon }">
     <div class="weather-content">
         <!--插入图标-->
      <v-mini-weather-icon :icon="icon" style="width: 28px;height: 28px;"></v-mini-weather-icon>
      <!--DIY内容-->
      <span
      style="color: #fff;margin-left: 5px;"
        >{{weather.temp}}℃ {{weather.weather}} {{ weather.WD }}{{weather.WS}}</span
      >
     </div>
    </template>
  </v-mini-weather>
</template>

<script>
import { vMiniWeather,vMiniWeatherIcon } from "vue-mini-weather";
export default {
  components: {
    vMiniWeather,vMiniWeatherIcon
  },
};
//   "nameen": "quanshan",
//   "cityname": "泉山",
//   "city": "101190811",
//   "temp": "32",
//   "tempf": "89",
//   "WD": "西风",
//   "wde": "W",
//   "WS": "3级",
//   "wse": "13km/h",
//   "SD": "61%",
//   "sd": "61%",
//   "qy": "993",
//   "njd": "16km",
//   "time": "14:00",
//   "rain": "0",
//   "rain24h": "0",
//   "aqi": "55",
//   "aqi_pm25": "55",
//   "weather": "多云",
//   "weathere": "Cloudy",
//   "weathercode": "01",
//   "limitnumber": "",
//   "date": "07月14日(星期五)"
</script>


<style lang="scss" scoped>
.weather-content {
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>

