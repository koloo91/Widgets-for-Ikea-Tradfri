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
import kotlinx.coroutines.experimental.Job
import thekolo.de.quicktilesforikeatradfri.Device
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.models.Group
import thekolo.de.quicktilesforikeatradfri.tradfri.TradfriService
import thekolo.de.quicktilesforikeatradfri.ui.adapter.SpinnerData
import thekolo.de.quicktilesforikeatradfri.ui.adapter.TilesAdapter
import thekolo.de.quicktilesforikeatradfri.utils.TileUtil


class TilesFragment : Fragment() {

    private lateinit var mainActivity: MainActivity

    private lateinit var layoutManager: GridLayoutManager

    private lateinit var service: TradfriService

    private var devices = emptyList<Device>()
    private var groups = emptyList<Group>()

    private lateinit var adapter: TilesAdapter

    private var currentJob: Job? = null
        set(value) {
            field?.let {
                if (!it.isCancelled && !it.isCompleted)
                    field?.cancel()
            }

            field = value
        }

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

        adapter = TilesAdapter(activity, tiles, listOf(SpinnerData(-1, "None", true)))
        recyclerView.adapter = adapter

        view.swipe_refresh_layout.setOnRefreshListener {
            currentJob = mainActivity.startLoadingProcess(this@TilesFragment::loadData)
        }

        currentJob = mainActivity.startLoadingProcess(this@TilesFragment::loadData)

        return view
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    override fun onPause() {
        super.onPause()

        Log.d("TilesFragment", "onPause")
        currentJob?.let {
            if (!it.isCancelled && !it.isCompleted)
                currentJob?.cancel()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        Log.d("TilesFragment", "onAttach")

        mainActivity = context as MainActivity
        service = TradfriService.instance(activity)
    }

    private fun loadData() {
        view?.swipe_refresh_layout?.isRefreshing = true

        currentJob = service.getDevices({ devices ->
            this.devices = devices
            updateAdapter()

            currentJob = service.getGroups({ groups ->
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
        Log.d("TilesFragment", "updateAdapter")

        val defaultEntry = listOf(SpinnerData(-1, "None", true)).toMutableList()
        val devicesData = devices.map { SpinnerData(it.id, it.name, true) }.toMutableList()
        val groupsData = groups.map { SpinnerData(it.id, "${it.name} (Group)", false) }.toMutableList()

        defaultEntry += devicesData
        defaultEntry += groupsData

        adapter.updateAdapter(defaultEntry)
    }

    companion object {
        fun newInstance(): TilesFragment {
            return TilesFragment()
        }
    }
}
