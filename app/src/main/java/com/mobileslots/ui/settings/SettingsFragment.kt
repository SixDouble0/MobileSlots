package com.mobileslots.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.mobileslots.MobileSlotsApplication
import com.mobileslots.R
import com.mobileslots.data.repository.UserRepository
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = (requireActivity().application as MobileSlotsApplication).database
        userRepository = UserRepository(database.userDao(), database.userSettingsDao())

        setupViews(view)
    }

    private fun setupViews(view: View) {
        val soundSwitch: SwitchMaterial = view.findViewById(R.id.sound_switch)
        val vibrationSwitch: SwitchMaterial = view.findViewById(R.id.vibration_switch)
        val shakeSwitch: SwitchMaterial = view.findViewById(R.id.shake_switch)

        lifecycleScope.launch {
            userRepository.getCurrentUser().collect { user ->
                user?.let {
                    userRepository.getUserSettings(it.userId).collect { settings ->
                        settings?.let { s ->
                            soundSwitch.isChecked = s.soundEnabled
                            vibrationSwitch.isChecked = s.vibrationEnabled
                            shakeSwitch.isChecked = s.shakeToSpinEnabled
                        }
                    }
                }
            }
        }

        soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            updateSettings { it.copy(soundEnabled = isChecked) }
        }

        vibrationSwitch.setOnCheckedChangeListener { _, isChecked ->
            updateSettings { it.copy(vibrationEnabled = isChecked) }
        }

        shakeSwitch.setOnCheckedChangeListener { _, isChecked ->
            updateSettings { it.copy(shakeToSpinEnabled = isChecked) }
        }

        view.findViewById<View>(R.id.reset_button).setOnClickListener {
            showResetDialog()
        }
    }

    private fun updateSettings(update: (com.mobileslots.domain.model.UserSettings) -> com.mobileslots.domain.model.UserSettings) {
        lifecycleScope.launch {
            userRepository.getCurrentUser().value?.let { user ->
                userRepository.getUserSettings(user.userId).value?.let { settings ->
                    userRepository.updateSettings(update(settings))
                }
            }
        }
    }

    private fun showResetDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.settings_reset_balance)
            .setMessage(R.string.settings_reset_confirmation)
            .setPositiveButton(R.string.yes) { _, _ ->
                resetBalance()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun resetBalance() {
        lifecycleScope.launch {
            userRepository.getCurrentUser().value?.let { user ->
                userRepository.updateBalance(user.userId, 1000)
            }
        }
    }
}
