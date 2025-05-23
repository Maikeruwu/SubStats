package com.maikeruwu.substats.ui.statistics.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.maikeruwu.substats.R
import com.maikeruwu.substats.databinding.FragmentStatisticsListBinding
import com.maikeruwu.substats.model.exception.SubsonicException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import retrofit2.HttpException
import java.net.SocketTimeoutException

abstract class AbstractListFragment<V : AbstractListViewModel>(
    private val viewModelClass: Class<V>
) : Fragment() {
    private var _binding: FragmentStatisticsListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    protected val binding get() = _binding!!

    protected val viewModel: V by lazy {
        ViewModelProvider(this)[viewModelClass]
    }

    protected open fun getHandler(
        viewModel: AbstractListViewModel,
        dataIsEmpty: Boolean,
        jobs: MutableList<Job>? = null
    ): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, exception ->
            var message =
                if (!dataIsEmpty && exception is SocketTimeoutException) null
                else when (exception) {
                    is HttpException -> {
                        getString(
                            R.string.response_error_code,
                            exception.code()
                        ) + if (exception.message().isNotEmpty()) getString(
                            R.string.response_error_message,
                            exception.message()
                        ) else ""
                    }

                    is SubsonicException -> {
                        getString(
                            R.string.response_error_code,
                            exception.code
                        ) + if (exception.message.isNotEmpty()) getString(
                            R.string.response_error_message,
                            exception.message
                        ) else ""
                    }

                    else -> {
                        getString(R.string.response_failed)
                    }
                }
            if (message != null) {
                jobs?.forEach {
                    it.cancel(exception.message.orEmpty(), exception)
                    it.cancelChildren()
                }
                viewModel.setErrorText(
                    message
                )
            }
        }
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
}