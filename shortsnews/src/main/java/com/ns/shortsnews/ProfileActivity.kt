package com.ns.shortsnews

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.ns.shortsnews.databinding.ActivityProfileBinding
import com.ns.shortsnews.user.data.network.NetService
import com.ns.shortsnews.user.data.repository.UserDataRepositoryImpl
import com.ns.shortsnews.user.domain.usecase.UserUseCases
import com.ns.shortsnews.user.ui.fragment.LoginFragment
import com.ns.shortsnews.user.ui.viewmodel.UserViewModel
import com.ns.shortsnews.user.ui.viewmodel.UserViewModelFactory

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        window.statusBarColor = Color.parseColor("#1E1E1E")
        window.navigationBarColor = Color.parseColor("#1E1E1E")
        val netService = NetService()
        val userDataRepository = UserDataRepositoryImpl(netService.createRetrofit())
        val userDataUseCases =UserUseCases(userDataRepository)
        val userViewModelFactory = UserViewModelFactory()
        userViewModelFactory.inject(userDataUseCases)
        val userViewModel:UserViewModel by viewModels { userViewModelFactory }

        val loginFragment = LoginFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_containerProfile, loginFragment).commit()
    }
}