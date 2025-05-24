package com.maikeruwu.substats.ui.statistics.details

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.maikeruwu.substats.R
import com.maikeruwu.substats.databinding.FragmentDetailsBinding
import com.maikeruwu.substats.service.isEmptyCardValue
import com.maikeruwu.substats.service.loadCoverArt

abstract class AbstractDetailsFragment<V : AbstractDetailsViewModel>(
    private val viewModelClass: Class<V>
) : Fragment() {
    private var _binding: FragmentDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    protected val binding get() = _binding!!

    private var count = 0

    protected val viewModel: V by lazy {
        ViewModelProvider(this)[viewModelClass]
    }

    protected fun init(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        viewModel.errorText.observe(viewLifecycleOwner) {
            binding.errorText.text = it
            binding.errorText.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
            binding.details.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun setTopCard(name: String, coverArt: String?) {
        binding.name.text = name
        setCardLongClickListener(binding.topCard, getString(R.string.name), name)

        if (coverArt.isNullOrEmpty()) {
            return
        }
        binding.coverArt.loadCoverArt(coverArt)
    }

    protected fun addDetailCard(
        label: String?,
        value: String?,
    ) {
        if (label.isNullOrEmpty() || value.isEmptyCardValue()) {
            return
        }
        // On count = even we need to add a new TableRow
        if (count % 2 == 0) {
            val tableRow = layoutInflater.inflate(
                R.layout.table_row_details, binding.detailItems, false
            )
            binding.detailItems.addView(tableRow)
        }
        // Create a new CardView
        val cardView = layoutInflater.inflate(
            R.layout.card_detail_item, binding.detailItems, false
        )
        // Set the label and value
        cardView.findViewById<TextView>(R.id.detailLabel).apply {
            this.text = label
        }
        cardView.findViewById<TextView>(R.id.detailValue).apply {
            this.text = value
        }
        // Set the layout parameters
        cardView.layoutParams = TableRow.LayoutParams(
            0,
            resources.getDimensionPixelSize(R.dimen.detail_card_height),
            1f
        ).apply {
            setMargins(
                resources.getDimensionPixelSize(
                    R.dimen.activity_vertical_margin
                )
            )
        }
        // Set the long click listener to copy the value to clipboard
        setCardLongClickListener(cardView, label, value)
        // Add the CardView to the newest TableRow
        val tableRow =
            binding.detailItems.getChildAt(binding.detailItems.childCount - 1) as ViewGroup
        tableRow.addView(cardView)
        // Increment the count
        count++
    }

    private fun setCardLongClickListener(cardView: View?, label: String, value: String?) {
        cardView?.setOnLongClickListener {
            val labelWithoutColon = label.substring(0, label.length - 1)

            // Show Toast on long press
            Toast.makeText(
                it.context,
                getString(R.string.copied_text, labelWithoutColon),
                Toast.LENGTH_SHORT
            ).show()

            // Copy to clipboard
            val clipboard =
                it.context.getSystemService(Activity.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(labelWithoutColon, value)
            clipboard.setPrimaryClip(clip)

            true
        }
    }
}