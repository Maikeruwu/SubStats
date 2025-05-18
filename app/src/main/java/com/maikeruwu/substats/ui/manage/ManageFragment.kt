package com.maikeruwu.substats.ui.manage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.maikeruwu.substats.databinding.FragmentManageBinding

class ManageFragment : Fragment() {

    private var _binding: FragmentManageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val manageViewModel =
            ViewModelProvider(this)[ManageViewModel::class.java]

        _binding = FragmentManageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textManage
        manageViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}