package com.ns.news.presentation.activity.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ns.news.databinding.FragmentSettingBinding
import com.ns.news.presentation.activity.NewsSharedViewModel
import com.ns.news.presentation.activity.NewsSharedViewModelFactory
import com.ns.news.presentation.activity.ui.settings.SettingsAdapter
import com.ns.news.presentation.activity.ui.settings.SettingsModel

class SettingFragment: Fragment() {


    private val newsShareViewModel: NewsSharedViewModel by activityViewModels { NewsSharedViewModelFactory }
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = ArrayList<SettingsModel>()
        data.add(SettingsModel("","Allow Notification",true))
        data.add(SettingsModel("","Customize Sections",false))
        data.add(SettingsModel("","Rate App",false))
        data.add(SettingsModel("","Share App",false))
        data.add(SettingsModel("","Privacy Policy",false))
        data.add(SettingsModel("","Terms of Service",false))
        data.add(SettingsModel("","1.11 Android",false))
        showSettingsList(data)
    }

    private fun showSettingsList(it: List<SettingsModel>) {
        binding.recyclerSettings.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerSettings.adapter = SettingsAdapter(requireActivity(), it)
        binding.recyclerSettings.addItemDecoration(
            DividerItemDecoration(
                requireActivity(),
                DividerItemDecoration.VERTICAL
            )
        )
    }

    override fun onResume() {
        super.onResume()
        newsShareViewModel.disableDrawer()
    }

    override fun onPause() {
        super.onPause()
        newsShareViewModel.enableDrawer()
    }
}