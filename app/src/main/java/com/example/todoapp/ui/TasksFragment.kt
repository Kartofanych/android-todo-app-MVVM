package com.example.todoapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.todoapp.adapter.DealsAdapter
import com.example.todoapp.adapter.OnItemListener
import com.example.todoapp.R
import com.example.todoapp.adapter.SwipeCallbackInterface
import com.example.todoapp.adapter.SwipeHelper
import com.example.todoapp.databinding.FragmentTasksBinding
import com.example.todoapp.factory
import com.example.todoapp.room.TodoItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class TasksFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels { factory() }
    private var binding: FragmentTasksBinding? = null
    private val adapter: DealsAdapter? get() = views { recycler.adapter as DealsAdapter }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentTasksBinding.inflate(LayoutInflater.from(context)).also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.loadData()

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

                override fun onCheckClick(id: String, done: Boolean) {
                    viewModel.changeItemDone(id, done)
                }

            })
            val helper = SwipeHelper(object : SwipeCallbackInterface {
                override fun onDelete(todoItem: TodoItem) {
                    viewModel.deleteItem(todoItem)
                }

                override fun onChangeDone(todoItem: TodoItem) {
                    viewModel.changeItemDone(todoItem.id, !todoItem.done)
                }

            }, requireContext())
            helper.attachToRecyclerView(recycler)


            visible.setOnClickListener {
                viewModel.changeDone()
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
                viewModel.loadNetworkList()
                refresher.isRefreshing = false
            }


        }

        lifecycleScope.launch {
            viewModel.data.collectLatest {
                updateUI(it)
            }
        }
        lifecycleScope.launch {
            viewModel.countComplete.collectLatest {
                updateCounter(it)
            }
        }

    }

    private fun updateCounter(count: Int) {
        views {
            numberDone.text = "Выполнено - $count"
        }
    }

    private fun updateUI(list: List<TodoItem>) {
        if(viewModel.modeAll) {
            adapter?.submitList(list)
        }else{
            adapter?.submitList(list.filter { !it.done })
        }
    }

    private fun <T : Any> views(block: FragmentTasksBinding.() -> T): T? = binding?.block()


}

