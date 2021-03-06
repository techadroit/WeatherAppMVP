package test.demo.weatherapp.data.usecases

import io.reactivex.Flowable
import test.demo.weatherapp.data.WeatherApiService
import test.demo.weatherapp.data.response.CurrentWeatherResponse


open class CurrentWeatherUsecase(var apiService: WeatherApiService) : Interactor<CurrentWeatherResponse> {
    lateinit var cityName : String
    override fun execute(): Flowable<CurrentWeatherResponse> {
        return apiService.getCurrentWeather(cityName = cityName)
    }
}
