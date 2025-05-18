package com.maikeruwu.substats.ui.statistics.list

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.maikeruwu.substats.databinding.FragmentStatisticsListBinding

fun FragmentStatisticsListBinding.showProgressOverlay(
    showProgress: Boolean,
    showList: Boolean = !showProgress
) {
    progressBar.visibility = if (showProgress) View.VISIBLE else View.GONE
    recyclerView.visibility = if (showList) View.VISIBLE else View.GONE
}

fun FragmentStatisticsListBinding.observeText(
    viewLifecycleOwner: LifecycleOwner,
    viewModel: ListViewModel,
) {
    viewModel.errorText.observe(viewLifecycleOwner) {
        errorText.text = it
        errorText.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
        showProgressOverlay(false, false)
    }
}