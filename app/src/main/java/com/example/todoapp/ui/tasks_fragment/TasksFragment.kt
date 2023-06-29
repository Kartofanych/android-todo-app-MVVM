package com.example.todoapp.ui.tasks_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.todoapp.R
import com.example.todoapp.data_source.room.TodoItem
import com.example.todoapp.databinding.FragmentTasksBinding
import com.example.todoapp.ui.MainViewModel
import com.example.todoapp.ui.tasks_fragment.adapter.DealsAdapter
import com.example.todoapp.ui.tasks_fragment.adapter.OnItemListener
import com.example.todoapp.ui.tasks_fragment.adapter.SwipeCallbackInterface
import com.example.todoapp.ui.tasks_fragment.adapter.SwipeHelper
import com.example.todoapp.utils.LoadingState
import com.example.todoapp.utils.factory
import com.example.todoapp.utils.internet_connection.ConnectivityObserver
import com.example.todoapp.utils.internet_connection.ConnectivityObserver.Status.Available
import com.example.todoapp.utils.internet_connection.ConnectivityObserver.Status.Losing
import com.example.todoapp.utils.internet_connection.ConnectivityObserver.Status.Lost
import com.example.todoapp.utils.internet_connection.ConnectivityObserver.Status.Unavailable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class TasksFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels { factory() }


    private var binding: FragmentTasksBinding? = null
    private val adapter: DealsAdapter? get() = views { recycler.adapter as DealsAdapter }

    private var internetState = Unavailable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentTasksBinding.inflate(LayoutInflater.from(context)).also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadData()
        internetState = viewModel.status.value

        views {

            floatingNewTask.setOnClickListener {
                val action = TasksFragmentDirections.actionManageTask(null)
                findNavController().navigate(action)
            }

            recycler.adapter = DealsAdapter(object : OnItemListener {
                override fun onItemClick(id: String) {
                    val action = TasksFragmentDirections.actionManageTask(id = id)
                    findNavController().navigate(action)
                }

                override fun onCheckClick(todoItem: TodoItem) {
                    if (internetState == Available) {
                        viewModel.updateNetworkItem(todoItem)
                    } else {
                        Toast.makeText(
                            context,
                            "No internet connection, will upload with later. Continue offline.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    viewModel.changeItemDone(todoItem)
                }

            })
            val helper = SwipeHelper(object : SwipeCallbackInterface {
                override fun onDelete(todoItem: TodoItem) {
                    if (internetState == Available) {
                        viewModel.deleteNetworkItem(todoItem.id)
                    } else {
                        Toast.makeText(
                            context,
                            "No internet connection, will upload later. Continue offline.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    viewModel.deleteItem(todoItem)
                }

                override fun onChangeDone(todoItem: TodoItem) {
                    if (internetState == Available) {
                        viewModel.updateNetworkItem(todoItem)
                    } else {
                        Toast.makeText(
                            context,
                            "No internet connection, will upload later. Continue offline.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    viewModel.changeItemDone(todoItem)
                }

            }, requireContext())
            helper.attachToRecyclerView(recycler)


            visible.setOnClickListener {
                viewModel.changeMode()
                when (viewModel.modeAll) {
                    true -> {
                        visible.setImageResource(R.drawable.ic_invisible)
                    }

                    false -> {
                        visible.setImageResource(R.drawable.ic_visible)
                    }
                }
            }

            refresher.setOnRefreshListener {
                if (internetState == Available) {
                    viewModel.loadNetworkList()
                } else {
                    Toast.makeText(
                        context,
                        "No internet connection, retry later(",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                refresher.isRefreshing = false
            }

        }

        lifecycleScope.launch {
            viewModel.status.collectLatest {
                updateStatusUI(it)
            }
        }

        lifecycleScope.launch {
            viewModel.data.collectLatest {
                updateRecyclerData(it)
            }
        }
        lifecycleScope.launch {
            viewModel.countComplete.collectLatest {
                updateCounter(it)
            }
        }

        lifecycleScope.launch {
            viewModel.loading.collectLatest {
                updateLoadingUI(it)
            }
        }

    }

    private fun updateLoadingUI(loadingState: LoadingState<Any>) {
        when (loadingState) {
            is LoadingState.Loading -> {
                views {
                    recycler.visibility = View.GONE
                    loading.visibility = View.VISIBLE
                }
            }

            is LoadingState.Success -> {
                views {
                    recycler.visibility = View.VISIBLE
                    loading.visibility = View.GONE
                }
            }

            is LoadingState.Error -> {
                Toast.makeText(
                    context,
                    "Error occurred, loaded local files",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateStatusUI(status: ConnectivityObserver.Status) {
        views {
            when (status) {
                Available -> {
                    connection.imageTintList =
                        AppCompatResources.getColorStateList(requireContext(), R.color.green)
                    if (internetState != status) {
                        Toast.makeText(context, "Connected! Merging data...", Toast.LENGTH_SHORT)
                            .show()
                        viewModel.loadNetworkList()
                    }

                }

                Unavailable -> {
                    connection.imageTintList =
                        AppCompatResources.getColorStateList(requireContext(), R.color.red)
                    if (internetState != status) {
                        Toast.makeText(
                            context,
                            "Internet unavailable! Work offline",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.loadNetworkList()
                    }
                }

                Losing -> {
                    connection.imageTintList =
                        AppCompatResources.getColorStateList(requireContext(), R.color.orange)

                    if (internetState != status) {
                        Toast.makeText(context, "Losing Internet!", Toast.LENGTH_SHORT).show()
                    }
                }

                Lost -> {
                    connection.imageTintList =
                        AppCompatResources.getColorStateList(requireContext(), R.color.red)

                    if (internetState != status) {
                        Toast.makeText(context, "Internet Lost!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        internetState = status
    }

    private fun updateCounter(count: Int) {
        views {
            numberDone.text = "Выполнено - $count"
        }
    }

    private fun updateRecyclerData(list: List<TodoItem>) {
        if (viewModel.modeAll) {
            adapter?.submitList(list)
        } else {
            adapter?.submitList(list.filter { !it.done })
        }
    }

    private fun <T : Any> views(block: FragmentTasksBinding.() -> T): T? = binding?.block()


}
