package it.scoppelletti.spaceship.sample

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.scoppelletti.spaceship.app.showExceptionDialog
import it.scoppelletti.spaceship.inject.Injectable
import it.scoppelletti.spaceship.sample.lifecycle.ListState
import it.scoppelletti.spaceship.sample.lifecycle.ListViewModel
import it.scoppelletti.spaceship.sample.widget.ItemListAdapter
import kotlinx.android.synthetic.main.list_fragment.*
import javax.inject.Inject

class ListFragment : Fragment(),
        DrawerFragment,
        Injectable,
        OnAddClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: ListViewModel
    private lateinit var adapter: ItemListAdapter

    override val titleId: Int
        get() = R.string.cmd_section1

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val listLayout : LinearLayoutManager
        val itemDeco: RecyclerView.ItemDecoration

        super.onViewCreated(view, savedInstanceState)

        refreshLayout.isEnabled = false
        refreshLayout.setColorSchemeResources(R.color.secondaryColor)
        listLayout = LinearLayoutManager(context)
        listView.layoutManager = listLayout
        itemDeco =  DividerItemDecoration(context, listLayout.orientation)
        listView.addItemDecoration(itemDeco)

        adapter = ItemListAdapter(::onItemClick)
        listView.adapter = adapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ListViewModel::class.java)
        viewModel.state.observe(this, Observer<ListState> { state ->
            if (state != null) {
                if (state.waiting) {
                    refreshLayout.isRefreshing = true
                }

                adapter.submitList(state.items)

                if (!state.waiting) {
                    refreshLayout.isRefreshing = false
                }

                state.error?.poll()?.let { err ->
                    requireActivity().showExceptionDialog(err)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.list()
    }

    override fun onAddClick() {
        onItemClick(0)
    }

    private fun onItemClick(itemId: Int) {
        val intent: Intent

        intent = Intent(requireActivity(), TabbedActivity::class.java)
        intent.putExtra(MainApp.PROP_ITEMID, itemId)

        startActivity(intent)
    }

    companion object {
        fun newInstance(): ListFragment = ListFragment()
    }
}