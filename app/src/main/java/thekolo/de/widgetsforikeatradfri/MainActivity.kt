package thekolo.de.widgetsforikeatradfri

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.experimental.runBlocking

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //65540 Stehlampe
        val stehlampe = "65540"

        /*val client = Client.getInstance()
        val device = client.getDevice(stehlampe)
        println(device)
        //val turnOffResponse = client.turnDeviceOn(stehlampe)
        val response = client.getDevices()
        println(response)*/

        val devices = runBlocking {
            Client.getInstance().getDevices().await().filter { it != null }.map { it!! }
        }

        println(devices)


    }
}
