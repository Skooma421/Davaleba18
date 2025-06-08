package com.example.davaleba18.UI.home.screen

import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.davaleba18.R
import com.example.davaleba18.databinding.FragmentHomeBinding
import com.example.davaleba18.util.BaseFragment

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    override fun bindViewActionListener() {
        binding.logoutButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_home_to_login,
                null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.homeFragment, true)
                    .build()
            )
        }
    }
}