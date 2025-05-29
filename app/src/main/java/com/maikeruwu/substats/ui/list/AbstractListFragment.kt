package com.maikeruwu.substats.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.maikeruwu.substats.databinding.FragmentStatisticsListBinding

abstract class AbstractListFragment<V : AbstractListViewModel>(
    private val viewModelClass: Class<V>
) : Fragment() {
    private var _binding: FragmentStatisticsListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    protected val binding get() = _binding!!

    val viewModel: V by lazy {
        ViewModelProvider(this)[viewModelClass]
    }

    protected fun init(
        inflater: LayoutInflater,
        container: ViewGroup?,
        viewModel: AbstractListViewModel
    ): View {
        _binding = FragmentStatisticsListBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        viewModel.errorText.observe(viewLifecycleOwner) {
            binding.errorText.text = it
            binding.errorText.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
            showProgressOverlay(false, false)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun showProgressOverlay(
        showProgress: Boolean,
        showList: Boolean = !showProgress
    ) {
        binding.progressBar.visibility = if (showProgress) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (showList) View.VISIBLE else View.GONE
    }

    protected abstract fun onItemClick(position: Int)
}