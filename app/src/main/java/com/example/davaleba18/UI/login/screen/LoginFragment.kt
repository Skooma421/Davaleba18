package com.example.davaleba18.UI.login.screen

import android.content.Context
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.davaleba18.R
import com.example.davaleba18.UI.login.vm.LoginViewModel
import com.example.davaleba18.databinding.FragmentLoginBinding
import com.example.davaleba18.util.BaseFragment
import com.example.davaleba18.util.isValidEmail
import com.example.davaleba18.util.isValidPassword
import kotlinx.coroutines.launch
import okio.IOException
import kotlinx.coroutines.flow.collectLatest as collectLatest1

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val shouldLogout = arguments?.getBoolean("shouldLogout", false) ?: false
        if (shouldLogout) {
            try {
                val sharedPref = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
                sharedPref.edit().clear().apply()
                viewModel.clearLoginState()
                showMessage("Logged out successfully")
            } catch (e: IOException) {
                Log.e("LoginFragment", "SharedPreferences clear failed", e)
                showMessage("Error clearing session")
            }
        }
    }

    override fun bindViewActionListener() {
        binding.apply {
            setFragmentResultListener("register_result") { _, bundle ->
                val email = bundle.getString("email") ?: ""
                val password = bundle.getString("password") ?: ""
                emailField.setText(email)
                passwordField.setText(password)
            }

            emailField.addTextChangedListener { validateInputs() }
            passwordField.addTextChangedListener { validateInputs() }

            loginButton.setOnClickListener {
                val email = emailField.text.toString().trim()
                val password = passwordField.text.toString().trim()
                val rememberMe = rememberMeCheckBox.isChecked

                if (email.isEmpty() || password.isEmpty()) {
                    showMessage("Please fill in all the fields!")
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

                try {
                    val sharedPref =
                        requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
                    val storedEmail = sharedPref.getString("registered_email", null)
                    val storedPassword = sharedPref.getString("registered_password", null)

                    if (storedEmail == null || storedPassword == null) {
                        showMessage("No account found")
                        return@setOnClickListener
                    }

                    if (email != storedEmail) {
                        showMessage("Account not found")
                        return@setOnClickListener
                    }

                    if (password != storedPassword) {
                        showMessage("Invalid email or password")
                        return@setOnClickListener
                    }
                } catch (e: IOException) {
                    Log.e("LoginFragment", "SharedPreferences read failed", e)
                    showMessage("Error reading credentials")
                    return@setOnClickListener
                }


                loginButton.isEnabled = false
                viewModel.login("eve.holt@reqres.in", "cityslicka", rememberMe, email)
            }

            registerButton.setOnClickListener {
                findNavController().navigate(R.id.action_login_to_register)
            }

            eyeIcon.setOnClickListener {
                passwordField.transformationMethod =
                    if (passwordField.transformationMethod == null) {
                        PasswordTransformationMethod.getInstance()
                    } else {
                        null
                    }
                eyeIcon.setImageResource(
                    if (passwordField.transformationMethod == null) R.drawable.eye_closed_icon else R.drawable.eye_icon
                )
                passwordField.setSelection(passwordField.text?.length ?: 0)
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.loginResult.collectLatest1 { result ->
                        loginButton.isEnabled = true
                        result.onSuccess { token ->
                            if (token.isNotEmpty()) {
                                val email = emailField.text.toString().trim()
                                if (rememberMeCheckBox.isChecked) {
                                    try {
                                        val sharedPref = requireContext().getSharedPreferences(
                                            "auth",
                                            Context.MODE_PRIVATE
                                        )
                                        sharedPref.edit()
                                            .putString("email", email)
                                            .putString("token", token)
                                            .apply()
                                    } catch (e: IOException) {
                                        Log.e("LoginFragment", "SharedPreferences save failed", e)
                                        showMessage("Error saving session")
                                        return@collectLatest1
                                    }
                                }
                                showMessage("Login successful")
                                emailField.text?.clear()
                                passwordField.text?.clear()
                                val bundle = Bundle().apply {
                                    putString("email", email)
                                }
                                findNavController().navigate(R.id.action_login_to_home, bundle)
                            }
                        }.onFailure { exception ->
                            showMessage(exception.message ?: "Login failed")
                        }
                    }
                }
            }
        }
    }

    private fun validateInputs() {
        val email = binding.emailField.text.toString().trim()
        val password = binding.passwordField.text.toString().trim()
        binding.loginButton.isEnabled = email.isValidEmail() && password.isValidPassword()
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}