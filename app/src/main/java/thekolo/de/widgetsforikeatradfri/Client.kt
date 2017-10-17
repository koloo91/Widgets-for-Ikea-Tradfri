package thekolo.de.widgetsforikeatradfri

object Client {
    private val ip = "192.168.178.56"
    private val securityId = "vBPnZjwbl07N8rex"

    private var client: TradfriClient? = null

    fun getInstance(): TradfriClient {
        if (client == null)
            client = TradfriClient(ip, securityId)

        return client!!
    }
}