package thekolo.de.widgetsforikeatradfri

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //65540 Stehlampe
        val ip = "192.168.178.56"
        val securityId = "vBPnZjwbl07N8rex"
        val stehlampe = "65540"

        val client = TradfriClient(ip, securityId)
        val device = client.getDevice(stehlampe)
        println(device)
        //val turnOffResponse = client.turnDeviceOn(stehlampe)
        val response = client.getDevices()
        println(response)

    }
}
