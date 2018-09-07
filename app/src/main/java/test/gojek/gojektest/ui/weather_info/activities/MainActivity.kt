package test.gojek.gojektest.ui.weather_info.activities


import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import test.gojek.gojektest.R
import test.gojek.gojektest.data.response.WeatherInfo
import test.gojek.gojektest.di.DaggerMainComponent
import test.gojek.gojektest.di.MainComponent
import test.gojek.gojektest.location.FetchCurrentCity
import test.gojek.gojektest.ui.base.BaseActivity
import test.gojek.gojektest.ui.base.Response
import test.gojek.gojektest.ui.weather_info.fragment.ErrorFragment
import test.gojek.gojektest.ui.weather_info.fragment.WeatherForecastFragment
import test.gojek.gojektest.ui.weather_info.presenter.MainPresenter
import test.gojek.gojektest.util.addErrorAnimation
import test.gojek.gojektest.util.addWeatherScreenAnimation
import test.gojek.gojektest.util.getRotateAnimation
import javax.inject.Inject


class MainActivity : BaseActivity<MainPresenter>(), ErrorFragment.OnRetryListener {

    lateinit var mainComponent: MainComponent
    @Inject
    lateinit var fetchCurrentCity: FetchCurrentCity
    val COARSE_LOCATION = 1;

    override fun getLayout(): Int {
        return R.layout.activity_main
    }

    override fun initPresenter(): MainPresenter {
        return MainPresenter()
    }

    override fun init() {
        mainComponent = DaggerMainComponent.create();
        mainComponent.inject(this)
        mainComponent.inject(presenter)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showLoading(true)
        presenter.observeForWeatherInfo().observe(this@MainActivity, Observer { response -> response?.let { processResponse(response) } })
        checkForPermissions()
    }

    fun showLoading(showLoading: Boolean) {
        if (showLoading) {
            startLoading()
        } else {
            imvLoading.clearAnimation()
            imvLoading.visibility = View.GONE
        }
    }

    fun processResponse(response: Response) {

        when (response) {

            is Response.OnLoading -> {
                showLoading(response.showLoading)
            }
            is Response.SuccessResponse -> {
                var fragment = WeatherForecastFragment()
                var bundle = Bundle()
                bundle.putParcelable("weather_info", response.s as WeatherInfo)
                fragment.arguments = bundle
                addWeatherScreenAnimation(fragment)
                supportFragmentManager.beginTransaction().replace(R.id.flContainer, fragment, "success").commit()
            }
            is Response.ErrorResponse -> {
                showError(response.s )
            }
        }
    }

    fun showError(errorString : String?){
        var fragment = ErrorFragment()
        addErrorAnimation(fragment)
        var bundle = Bundle()
        bundle.putString("error_msg",errorString)
        supportFragmentManager.beginTransaction().replace(R.id.flContainer, fragment, "error").commit()
    }

    override fun onRetryClick() {
        for (fragment in supportFragmentManager.fragments) {
            fragment?.let {
                supportFragmentManager.beginTransaction().remove(fragment).commit()
            }
        }
        fetchWeatherInfo()
    }

    @SuppressLint("MissingPermission")
    fun fetchWeatherInfo() {
        fetchCurrentCity.observe(this@MainActivity, Observer {
            it?.let {
               processCityResponse(it)
            }
        })
    }

    fun processCityResponse(response: Response){

        when(response){
            is Response.ErrorResponse -> {
             showError(response.s)
            }
            is Response.SuccessResponse -> {
                presenter.loadData(response.s as String)
            }
        }

    }

    fun startLoading() {
        imvLoading.visibility = View.VISIBLE
        imvLoading.startAnimation(getRotateAnimation())
    }

    fun checkForPermissions() {

        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), COARSE_LOCATION)
        } else {
            fetchWeatherInfo()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            COARSE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    fetchWeatherInfo()
                } else {
                    Toast.makeText(this@MainActivity, "Need permission to fetch weather information", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }


}
