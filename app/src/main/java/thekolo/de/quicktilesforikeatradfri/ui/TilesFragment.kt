package thekolo.de.quicktilesforikeatradfri.ui


import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_tiles.view.*
import kotlinx.coroutines.experimental.CoroutineExceptionHandler
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import thekolo.de.quicktilesforikeatradfri.Device
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.models.Group
import thekolo.de.quicktilesforikeatradfri.room.Database
import thekolo.de.quicktilesforikeatradfri.room.DeviceData
import thekolo.de.quicktilesforikeatradfri.room.DeviceDataDao
import thekolo.de.quicktilesforikeatradfri.services.QueueService
import thekolo.de.quicktilesforikeatradfri.tradfri.TradfriService
import thekolo.de.quicktilesforikeatradfri.ui.adapter.SpinnerData
import thekolo.de.quicktilesforikeatradfri.ui.adapter.TilesAdapter
import thekolo.de.quicktilesforikeatradfri.utils.TileUtil


class TilesFragment : Fragment(), TilesAdapter.TilesAdapterActions {

    private lateinit var mainActivity: MainActivity

    private lateinit var layoutManager: GridLayoutManager

    private lateinit var service: TradfriService
    private lateinit var deviceDataDao: DeviceDataDao

    private var devices = emptyList<Device>()
    private var groups = emptyList<Group>()

    private lateinit var adapter: TilesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tiles, container, false)

        val recyclerView = view.recycler_view
        recyclerView.setHasFixedSize(true)

        layoutManager = GridLayoutManager(activity, 2)
        recyclerView.layoutManager = layoutManager

        val tiles = listOf(
                Pair("Tile 1", TileUtil.nameForIndex(1)),
                Pair("Tile 2", TileUtil.nameForIndex(2)),
                Pair("Tile 3", TileUtil.nameForIndex(3)),
                Pair("Tile 4", TileUtil.nameForIndex(4)),
                Pair("Tile 5", TileUtil.nameForIndex(5))
        )

        adapter = TilesAdapter(activity, tiles, listOf(SpinnerData(-1, "None", true)), emptyList(), this)
        recyclerView.adapter = adapter

        view.swipe_refresh_layout.setOnRefreshListener {
            mainActivity.startLoadingProcess(this@TilesFragment::loadData)
        }

        mainActivity.startLoadingProcess(this@TilesFragment::loadData)
        loadAndRefreshDeviceData()

        return view
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    override fun onPause() {
        super.onPause()

        Log.d("TilesFragment", "onPause")
        QueueService.instance().clearQueue()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        Log.d("TilesFragment", "onAttach")

        mainActivity = context as MainActivity
        service = TradfriService.instance(activity)
        deviceDataDao = Database.get(context).deviceDataDao()
    }

    private fun loadData() {
        view?.swipe_refresh_layout?.isRefreshing = true

        service.getDevices({ devices ->
            this.devices = devices

            service.getGroups({ groups ->
                this.groups = groups
                updateAdapter()

                view?.swipe_refresh_layout?.isRefreshing = false
            }, {
                view?.swipe_refresh_layout?.isRefreshing = false
            })
        }, {
            view?.swipe_refresh_layout?.isRefreshing = false
        })
    }

    private fun updateAdapter() {
        Log.d("TilesFragment", "updateSpinnerItems")

        val defaultEntry = listOf(SpinnerData(-1, "None", true)).toMutableList()
        val devicesData = devices.map { SpinnerData(it.id, it.name, true) }.toMutableList()
        val groupsData = groups.map { SpinnerData(it.id, "${it.name} (Group)", false) }.toMutableList()

        defaultEntry += devicesData
        defaultEntry += groupsData

        adapter.updateSpinnerItems(defaultEntry)
    }

    override fun onStateSwitchCheckedChanged(spinnerItem: SpinnerData, tile: String) {
        launch {
            deviceDataDao.deleteByTile(tile)

            if (spinnerItem.id > 0)
                deviceDataDao.insert(DeviceData(spinnerItem.id, spinnerItem.name, tile, spinnerItem.isDevice))

            loadAndRefreshDeviceData()
        }
    }

    private fun loadAndRefreshDeviceData() {
        launch {
            val allStoredData = deviceDataDao.getAll()

            launch(UI) {
                adapter.updateStoredDeviceData(allStoredData)
            }
        }
    }

    companion object {
        fun newInstance(): TilesFragment {
            return TilesFragment()
        }
    }
}
