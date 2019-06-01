package com.example.phoneproperties

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.BatteryManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.acitivity_main.*


class MainActivity : AppCompatActivity() {

    private var batteryTemperature: Float = 0.0f

    private var mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            batteryTemperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0).toFloat() / 10
        }
    }

    private var notificationManager: NotificationManager? = null

    private var appContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_main)
        setNotificationChannelProperties()
        setSensorProperties()

        showMapBtn.setOnClickListener {
            temperatureTextView.setText("")
            var intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
        showTemperatureBtn.setOnClickListener {
            temperatureTextView.setText("Temperature of battery:\n" + batteryTemperature + " " + 0x00B0.toChar() + "C")
        }

    }

    override fun onPause(){
        super.onPause();
        backgroundWorkNotification()
    }

    fun setSensorProperties() {
        appContext = applicationContext
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        appContext?.registerReceiver(mBroadcastReceiver, intentFilter)
    }

    fun setNotificationChannelProperties() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val id = "com.example.PhoneProperties"
        val name = "Background work"
        val details = "Notifications displayed when app goes to the background"
        val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH)
        channel.description = details
        channel.enableLights(true)
        channel.lightColor = Color.RED
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager?.createNotificationChannel(channel)
    }

    private fun backgroundWorkNotification() {
        val notification = Notification.Builder(this@MainActivity, "com.example.PhoneProperties")
            .setChannelId("com.example.PhoneProperties")
            .setContentTitle("App went to the background")
            .setSmallIcon(android.R.drawable.ic_dialog_dialer)
            .setAutoCancel(true)
        val notificationIntent = Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        notification.setContentIntent(pendingIntent)
        notificationManager?.notify(123, notification.build())
    }

}
