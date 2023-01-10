package com.ns.view.navigation

import androidx.navigation.NavHostController
import androidx.navigation.fragment.NavHostFragment

class NewsNavHostFragment: NavHostFragment() {
    override fun createFragmentNavigator() =
        NewsFragmentNavigator(requireContext(), childFragmentManager, id)


    override fun onCreateNavHostController(navHostController: NavHostController) {
        super.onCreateNavHostController(navHostController)
        NewsFragmentNavigator(requireContext(), childFragmentManager, id)
    }
}