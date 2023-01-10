package com.ns.view.navigation

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import com.ns.news.R

@Navigator.Name("fragment") // `keep_state_fragment` is used in navigation xml
class NewsFragmentNavigator(
    private val context: Context,
    private val fragmentManager: FragmentManager, // Should pass childFragmentManager.
    private val containerId: Int
) : FragmentNavigator(context, fragmentManager, containerId) {

    private val savedIds = mutableSetOf<String>()


    override fun navigate(
        entries: List<NavBackStackEntry>,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ) {
        for (entry in entries) {
            navigate(entry, navOptions, navigatorExtras)
        }
    }

    private fun navigate(
        entry: NavBackStackEntry,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ) {
        val initialNavigation = state.backStack.value.isEmpty()
        val restoreState = (
                navOptions != null && !initialNavigation &&
                        navOptions.shouldRestoreState() &&
                        savedIds.remove(entry.id)
                )
        if (restoreState) {
            // Restore back stack does all the work to restore the entry
            fragmentManager.restoreBackStack(entry.id)
            state.push(entry)
            return
        }
        val ft = createFragmentTransaction(entry, navOptions)

        if (!initialNavigation) {
            ft.addToBackStack(entry.id)
        }

        if (navigatorExtras is Extras) {
            for ((key, value) in navigatorExtras.sharedElements) {
                ft.addSharedElement(key, value)
            }
        }
        ft.commit()
        // The commit succeeded, update our view of the world
        state.push(entry)
    }

    private fun createFragmentTransaction(
        entry: NavBackStackEntry,
        navOptions: NavOptions?
    ): FragmentTransaction {
        val destination = entry.destination as Destination
        val args = entry.arguments
        var className = destination.className
        if (className[0] == '.') {
            className = context.packageName + className
        }
        val frag = fragmentManager.fragmentFactory.instantiate(context.classLoader, className)
        frag.arguments = args
        val ft = fragmentManager.beginTransaction()
        var enterAnim = navOptions?.enterAnim ?: -1
        var exitAnim = navOptions?.exitAnim ?: -1
        var popEnterAnim = navOptions?.popEnterAnim ?: -1
        var popExitAnim = navOptions?.popExitAnim ?: -1
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = if (enterAnim != -1) enterAnim else 0
            exitAnim = if (exitAnim != -1) exitAnim else 0
            popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
            popExitAnim = if (popExitAnim != -1) popExitAnim else 0
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
        }
//        ft.replace(containerId, frag)
        ft.add(containerId, frag)
        ft.setPrimaryNavigationFragment(frag)
        ft.setReorderingAllowed(true)
        return ft
    }


    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ): NavDestination? {
        val tag = destination.id.toString()
        val transaction = fragmentManager.beginTransaction()

        var initialNavigate = false
        val currentFragment = fragmentManager.primaryNavigationFragment
        if (currentFragment != null) {
            transaction.detach(currentFragment)
        } else {
            initialNavigate = true
        }

        var fragment = fragmentManager.findFragmentByTag(tag)
        if (fragment == null) {
            val className = destination.className
            fragment = fragmentManager.fragmentFactory.instantiate(context.classLoader, className)
            transaction.add(containerId, fragment, tag)
        } else {
            transaction.attach(fragment)
        }

        transaction.setPrimaryNavigationFragment(fragment)
        transaction.setReorderingAllowed(true)
        transaction.commitNow()

        return if (initialNavigate) {
            destination
        } else {
            null
        }
    }
}

@Navigator.Name("keep_state_fragment")
class MyFragmentNavigator(
    context: Context,
    fm: FragmentManager,
    containerId: Int
) : FragmentNavigator(context, fm, containerId) {
    override fun navigate(destination: Destination,
                          args: Bundle?,
                          navOptions: NavOptions?,
                          navigatorExtras: Navigator.Extras?): NavDestination? {
        val shouldSkip = navOptions?.run {
//            popUpTo == destination.id && !isPopUpToInclusive()
            R.id.navigation_detailPager == destination.id && !isPopUpToInclusive()
        }  ?: false

        return if (shouldSkip) null
        else super.navigate(destination, args, navOptions, navigatorExtras)
    }
}