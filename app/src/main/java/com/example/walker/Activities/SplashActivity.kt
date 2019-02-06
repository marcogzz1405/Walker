package com.example.walker.Activities

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.walker.R
import com.example.walker.Utilities.Conts
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import java.lang.IllegalArgumentException
import java.util.jar.Manifest
import kotlin.concurrent.thread

class SplashActivity : AppCompatActivity() {

    private val TAG = SplashActivity::class.simpleName

    val arrayPermissions = arrayOf(android.Manifest.permission.READ_PHONE_STATE,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)

    private val welcomeScreenDisplay = 3000
    private val PLAY_SERVICES_RESOLUTION_REQUEST = 9000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val splashTime = 0
        val splashThread = Thread(){
            var wait = 0
            splashTime.run {
                Looper.prepare()
                try {
                    while (wait < welcomeScreenDisplay){
                        Thread.sleep(100)
                        wait += 100
                    }
                } catch (e: Exception){
                    Log.e(TAG, "Exception= " + e)
                } finally {
                    doAfterSplashWork()
                }
                Looper.loop()
            }
        }
        splashThread.start()
    }

    fun checkPlayServices(activity: Activity): Boolean {
        googleApiAvailability
        val resultCode = googleConnectionStatus
        if (resultCode != ConnectionResult.SUCCESS){
            if (googleApiAvailability.isUserResolvableError(resultCode)){
                googleApiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show()
            } else {
                Toast.makeText(activity, getString(R.string.play_unavailable), Toast.LENGTH_LONG).show()
                activity.finish()
            }
            return false
        }
        return true
    }

    private val googleApiAvailability by lazy {
        GoogleApiAvailability.getInstance()
    }

    private val googleConnectionStatus by lazy {
        googleApiAvailability.isGooglePlayServicesAvailable(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(this, MainActivity::class.java)
            //getLocationInfo()
            startActivityForResult(intent, 0)
            finish()
        } else {
            Log.e(TAG, "Denied Permissions")
            alertCalling()
        }

    }

    // getLocationInfo()

    fun setCountryCode(countryCode: String){
        //Conts.COUNTRY_CODE = countryCode
    }

    fun doAfterSplashWork(){
        if (checkPlayServices(this)){
            if (Build.VERSION.SDK_INT >= 23){
                if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    val intent = Intent(this, MainActivity::class.java)
                    startActivityForResult(intent, 0)
                    //getLocationInfo()
                    finish()
                } else {
                    alertCalling()
                }
            } else {
                val intent = Intent(this, MainActivity::class.java)
                //getLocationInfo()
                startActivityForResult(intent, 0)
                finish()
            }
        }
    }

    private fun alertCalling(){
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Permission").
                setCancelable(false).
                setMessage("Location and Phone State are mandatory permission please allow to access.\n" +
                        " Are you sure you want to continue?").
                setPositiveButton("Continue", DialogInterface.OnClickListener { dialog, which ->
                    ActivityCompat.requestPermissions(this, arrayPermissions, 1)
                }).
                setNegativeButton("Exit", DialogInterface.OnClickListener { dialog, which ->
                    finish()
                }).setIcon(R.mipmap.ic_dialog_alert).create()
        if (!isFinishing)
            dialogBuilder.show()
    }

}
