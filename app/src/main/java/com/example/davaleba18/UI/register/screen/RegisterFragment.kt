package com.example.davaleba18.UI.register.screen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.davaleba18.R
import com.example.davaleba18.UI.register.vm.RegisterViewModel
import com.example.davaleba18.databinding.FragmentRegisterBinding
import com.example.davaleba18.util.BaseFragment
import com.example.davaleba18.util.isValidEmail
import com.example.davaleba18.util.isValidPassword
import java.io.IOException

class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {

    private val viewModel: RegisterViewModel by viewModels()

    override fun bindViewActionListener() {
        binding.apply {
            emailField.addTextChangedListener { validateFields() }
            passwordField.addTextChangedListener { validateFields() }
            passwordRepeatField.addTextChangedListener { validateFields() }

            registerButton.setOnClickListener {
                val email = emailField.text.toString()?.trim() ?: ""
                val password = passwordField.text.toString()?.trim() ?: ""
                val passwordRepeat = passwordRepeatField.text.toString()?.trim() ?: ""

                if (email.isEmpty() || password.isEmpty() || passwordRepeat.isEmpty()) {
                    showMessage("Please fill in all fields")
                    return@setOnClickListener
                }
                if (!email.isValidEmail()) {
                    showMessage("Invalid email format")
                    return@setOnClickListener
                }
                if (!password.isValidPassword()) {
                    showMessage("Password must be at least 6 characters")
                    return@setOnClickListener
                }
                if (password != passwordRepeat) {
                    showMessage("Passwords do not match")
                    return@setOnClickListener
                }

                try {
                    val sharedPref =
                        requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
                    val existingEmail = sharedPref.getString("register_email", null)

                    if (existingEmail == email) {
                        showMessage("Account with this email already exists")
                        return@setOnClickListener
                    }
                    sharedPref.edit()
                        .putString("registered_email", email)
                        .putString("registered_password", password)
                        .apply()
                } catch (e: IOException) {
                    Log.e(TAG, "SharedPreferences save failed", e)
                    showMessage("Error saving credentials")
                    return@setOnClickListener
                }

                registerButton.isEnabled = false
                viewModel.register(email, password)
            }

            binding.arrow.setOnClickListener {
                findNavController().navigate(R.id.action_register_to_login)
            }


            viewModel.registerResponse.observe(viewLifecycleOwner) { response ->
                registerButton.isEnabled = true
                if (response?.token?.isNotEmpty() == true) {
                    showMessage("Registration successful")
                    val bundle = Bundle().apply {
                        putString("email", emailField.text?.toString())
                        putString("password", passwordField.text?.toString())
                    }
                    setFragmentResult("register_result", bundle)
                    findNavController().navigate(R.id.action_register_to_login)
                }
            }

            viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
                if (!error.isNullOrEmpty()) {
                    registerButton.isEnabled = true
                    showMessage(error)
                }
            }
        }
    }

    private fun validateFields() {
        val email = binding.emailField.text?.toString()?.trim() ?: ""
        val password = binding.passwordField.text?.toString()?.trim() ?: ""
        val passwordRepeat = binding.passwordRepeatField.text?.toString()?.trim() ?: ""
        binding.registerButton.isEnabled =
            email.isValidEmail() && password.isValidPassword() && password == passwordRepeat
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "RegisterFragment"
    }
}