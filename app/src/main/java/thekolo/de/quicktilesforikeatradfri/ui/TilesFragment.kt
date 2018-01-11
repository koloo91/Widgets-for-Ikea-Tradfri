package thekolo.de.quicktilesforikeatradfri.ui


import android.annotation.SuppressLint
import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_tiles.view.*
import thekolo.de.quicktilesforikeatradfri.Device
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.models.Group
import thekolo.de.quicktilesforikeatradfri.tradfri.TradfriService
import thekolo.de.quicktilesforikeatradfri.ui.adapter.SpinnerData
import thekolo.de.quicktilesforikeatradfri.ui.adapter.TilesAdapter
import thekolo.de.quicktilesforikeatradfri.utils.TileUtil


class TilesFragment : Fragment() {
    private lateinit var layoutManager: GridLayoutManager

    private val service: TradfriService
        get() = TradfriService.instance(activity)

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

        adapter = TilesAdapter(activity, tiles, listOf(SpinnerData(-1, "None", true)))
        recyclerView.adapter = adapter

        loadData()

        return view
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        service.getDevices({ devices ->
            this.devices = devices
            updateAdapter()
        }, {

        })

        service.getGroups({ groups ->
            this.groups = groups
            updateAdapter()
        }, {

        })
    }

    private fun updateAdapter() {
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
