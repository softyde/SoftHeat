package de.annee.softheat

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import de.annee.softheat.helpers.MqttHelper
import de.annee.softheat.model.SharedModel
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage


class MainActivity : AppCompatActivity() {

    var mqttHelper: MqttHelper? = null

    private val model: SharedModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        
        startMqtt()

        model.newTarget.observe(this, Observer { value ->
            Log.w( "Debug", "$value was set")
            mqttHelper?.publishNewTemp(value)
        })
    }


    private fun startMqtt() {
        mqttHelper = MqttHelper(applicationContext)
        mqttHelper!!.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(b: Boolean, s: String) {}
            override fun connectionLost(throwable: Throwable) {}

            @Throws(Exception::class)
            override fun messageArrived(topic: String, mqttMessage: MqttMessage) {

                if (topic == mqttHelper!!.topicSensorTargetTemperature) {
                    val targetTemperature = mqttMessage.toString().toInt()
                    model.setCurrentTarget(targetTemperature)
                } else if (topic == mqttHelper!!.topicSensorTemperature) {
                    model.setCurrentTemperature( "$mqttMessage°C")
                } else if (topic == mqttHelper!!.topicSensorHumidity) {
                    model.setCurrentHumidity( "Luftfeuchte $mqttMessage%")
                } else if (topic == mqttHelper!!.topicSensorBattery) {
                    model.setCurrentBattery( "Batterie $mqttMessage%")
                } else if (topic == mqttHelper!!.topicSensorMode) {

                    val mode = mqttMessage.toString()
                    if(mode == "heating") {
                        model.setCurrentMode("Heizung ist an")
                    } else if(mode == "cooling") {
                        model.setCurrentMode("Heizung ist aus")
                    }
                }

                Log.w("Debug", topic + "/" + mqttMessage.toString())
                //  dataReceived.setText(mqttMessage.toString())
            }

            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {}
        })


    }
}