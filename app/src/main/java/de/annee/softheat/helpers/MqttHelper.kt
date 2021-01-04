package de.annee.softheat.helpers

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.io.IOException
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.util.Properties
import kotlin.random.Random

@Throws(IOException::class)
fun getProperty(key: String?, context: Context): String? {
    val properties = Properties()
    val assetManager = context.assets
    val inputStream: InputStream = assetManager.open("mqtt.properties")
    properties.load(inputStream)
    return properties.getProperty(key)
}

class MqttHelper(private val context: Context) {

    var mqttAndroidClient: MqttAndroidClient

    val topicSensorTargetTemperature : String? by lazy { getProperty("mqtt.topic.sensor.targettemperature", context) }
    val topicSensorTemperature : String? by lazy { getProperty("mqtt.topic.sensor.temperature", context) }
    val topicSensorHumidity : String? by lazy { getProperty("mqtt.topic.sensor.humidity", context) }
    val topicSensorBattery : String? by lazy { getProperty("mqtt.topic.sensor.battery", context) }
    val topicSensorMode : String? by lazy { getProperty("mqtt.topic.sensor.mode", context) }
    val topicCommandTargetTemperature : String? by lazy { getProperty("mqtt.topic.command.targettemperature", context) }

    val subscriptionTopics = arrayOf(
        topicSensorTargetTemperature,
        topicSensorMode,
        topicSensorTemperature,
        topicSensorHumidity,
        topicSensorBattery
    )




    fun setCallback(callback: MqttCallbackExtended?) {
        mqttAndroidClient.setCallback(callback)
    }

    private fun connect() {

        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.isCleanSession = false

        // TODO wenn der MQTT-Server per Benutzername/Kennwort gesichert ist,
        //      muss das hier erweitert werden:
        //  mqttConnectOptions.setUserName(username);
        //  mqttConnectOptions.setPassword(password.toCharArray());
        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    val disconnectedBufferOptions = DisconnectedBufferOptions()
                    disconnectedBufferOptions.isBufferEnabled = true
                    disconnectedBufferOptions.bufferSize = 100
                    disconnectedBufferOptions.isPersistBuffer = false
                    disconnectedBufferOptions.isDeleteOldestMessages = false
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions)
                    subscribeToTopic()
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.w("mqtt", "Failed to connect to server: $exception")
                }
            })
        } catch (ex: MqttException) {
            ex.printStackTrace()
        }
    }

    fun publishNewTemp(value: Int) {
        try {
            mqttAndroidClient.publish(
                topicCommandTargetTemperature,
                MqttMessage(value.toString().toByteArray(charset("ASCII")))
            )
        } catch (e: MqttException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    private fun subscribeToTopic() {
        for (subscriptionTopic in subscriptionTopics) {
            try {
                mqttAndroidClient.subscribe(
                    subscriptionTopic,
                    0,
                    null,
                    object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken) {
                            Log.w("mqtt", "Subscribed!")
                        }

                        override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                            Log.w("mqtt", "Subscribed fail!")
                        }
                    })
            } catch (ex: MqttException) {
                System.err.println("Exception whilst subscribing")
                ex.printStackTrace()
            }
        }
    }

    init {

        val serverUri = getProperty("mqtt.server", context)
        val clientId = getProperty("mqtt.client", context) + Random.nextInt(0, 100000).toString()

        mqttAndroidClient = MqttAndroidClient(context, serverUri, clientId)
        mqttAndroidClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(b: Boolean, s: String) {
                Log.w("mqtt", s)
            }

            override fun connectionLost(throwable: Throwable) {}

            @Throws(Exception::class)
            override fun messageArrived(topic: String, mqttMessage: MqttMessage) {
                Log.w("mqtt", mqttMessage.toString())
            }

            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {}
        })
        connect()
    }
}