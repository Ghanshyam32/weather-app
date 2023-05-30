package com.ghanshyam.weatherapp;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherData {

    private String temp, icon, city, weatherType;
    private int condition;

    public static WeatherData fromJson(JSONObject jsonObject) {
        try {
            WeatherData weatherData = new WeatherData();
            weatherData.city = jsonObject.getString("name");
            weatherData.condition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            weatherData.weatherType = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
            weatherData.icon = updateWeather(weatherData.condition);

            double tempResult = jsonObject.getJSONObject("main").getDouble("temp") - 273.15;
            int roundedValue = (int) Math.rint(tempResult);
            weatherData.temp = Integer.toString(roundedValue);

            return weatherData;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    private static String updateWeather(int condition) {
        if (condition >= 0 && condition <= 300) {
            return "scattered_thunderstorms";
        } else if (condition >= 300 && condition <= 500) {
            return "light_rain";
        } else if (condition >= 500 && condition <= 600) {
            return "shower";
        } else if (condition >= 600 && condition <= 700) {
            return "snow";
        } else if (condition >= 701 && condition <= 771) {
            return "fog";
        } else if (condition >= 772 && condition <= 800) {
            return "overcast";
        } else if (condition == 800) {
            return "sun";
        } else if (condition >= 801 && condition <= 804) {
            return "cloudy_day";
        } else if (condition >= 900 && condition <= 902) {
            return "scattered_thunderstorms";
        }
        if (condition == 903) {
            return "snow";
        }
        if (condition == 904) {
            return "sun";
        }
        if (condition >= 905 && condition <= 1000) {
            return "scattered_thunderstorms";
        }
        return "dunno";
    }


    public String getTemp() {
        return temp+"°C";
    }

    public String getIcon() {
        return icon;
    }

    public String getCity() {
        return city;
    }

    public String getWeatherType() {
        return weatherType;
    }
}
