package com.mobileslots.ui.games.slots

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.mobileslots.MobileSlotsApplication
import com.mobileslots.R
import com.mobileslots.data.repository.GameHistoryRepository
import com.mobileslots.data.repository.UserRepository
import com.mobileslots.utils.NotificationHelper
import com.mobileslots.utils.ShakeDetector
import com.mobileslots.utils.VibrationHelper
import kotlinx.coroutines.launch

class SlotMachineFragment : Fragment() {

    private lateinit var viewModel: SlotMachineViewModel
    private lateinit var shakeDetector: ShakeDetector

    private lateinit var reel1: ImageView
    private lateinit var reel2: ImageView
    private lateinit var reel3: ImageView
    private lateinit var balanceTextView: MaterialTextView
    private lateinit var betTextView: MaterialTextView
    private lateinit var resultTextView: MaterialTextView
    private lateinit var betSlider: Slider
    private lateinit var spinButton: MaterialButton

    private val slotSymbols = listOf(
        R.drawable.slot_cherry,
        R.drawable.slot_lemon,
        R.drawable.slot_bell,
        R.drawable.slot_seven,
        R.drawable.slot_diamond
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_slot_machine, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupViews(view)
        setupShakeDetector()
        observeViewModel()
    }

    private fun setupViewModel() {
        val database = (requireActivity().application as MobileSlotsApplication).database
        val userRepository = UserRepository(database.userDao(), database.userSettingsDao())
        val gameHistoryRepository = GameHistoryRepository(database.gameHistoryDao())

        val factory = SlotMachineViewModelFactory(userRepository, gameHistoryRepository)
        viewModel = ViewModelProvider(this, factory)[SlotMachineViewModel::class.java]
    }

    private fun setupViews(view: View) {
        reel1 = view.findViewById(R.id.reel1)
        reel2 = view.findViewById(R.id.reel2)
        reel3 = view.findViewById(R.id.reel3)
        balanceTextView = view.findViewById(R.id.balance_text)
        betTextView = view.findViewById(R.id.bet_text)
        resultTextView = view.findViewById(R.id.result_text)
        betSlider = view.findViewById(R.id.bet_slider)
        spinButton = view.findViewById(R.id.spin_button)

        betSlider.addOnChangeListener { _, value, _ ->
            viewModel.setBetAmount(value.toInt())
        }

        spinButton.setOnClickListener {
            spin()
        }
    }

    private fun setupShakeDetector() {
        shakeDetector = ShakeDetector {
            lifecycleScope.launch {
                viewModel.userSettings.value?.let { settings ->
                    if (settings.shakeToSpinEnabled && !viewModel.isSpinning.value) {
                        spin()
                    }
                }
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.currentUser.collect { user ->
                user?.let {
                    balanceTextView.text = getString(R.string.profile_balance, it.balance)
                    betSlider.valueTo = minOf(it.balance.toFloat(), 1000f)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.betAmount.collect { amount ->
                betTextView.text = getString(R.string.slot_bet_amount, amount)
            }
        }

        lifecycleScope.launch {
            viewModel.spinResult.collect { result ->
                result?.let {
                    displayResult(it)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.error.collect { error ->
                error?.let {
                    Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun spin() {
        if (viewModel.isSpinning.value) return

        viewModel.spin { symbols ->
            animateReels(symbols)
        }
    }

    private fun animateReels(symbols: List<Int>) {
        val rotateAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_spin)

        // Animate each reel
        reel1.startAnimation(rotateAnimation)
        reel2.startAnimation(rotateAnimation)
        reel3.startAnimation(rotateAnimation)

        // Use ValueAnimator to change symbols during animation
        ValueAnimator.ofInt(0, 20).apply {
            duration = 2000
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                if (value % 2 == 0) {
                    reel1.setImageResource(slotSymbols.random())
                    reel2.setImageResource(slotSymbols.random())
                    reel3.setImageResource(slotSymbols.random())
                }

                // Set final symbols at the end
                if (value == 20) {
                    reel1.setImageResource(slotSymbols[symbols[0]])
                    reel2.setImageResource(slotSymbols[symbols[1]])
                    reel3.setImageResource(slotSymbols[symbols[2]])
                }
            }
            start()
        }
    }

    private fun displayResult(result: SlotMachineResult) {
        if (result.isWin) {
            resultTextView.text = getString(R.string.slot_win_message, result.winAmount)
            resultTextView.setTextColor(resources.getColor(R.color.success, null))

            // Vibrate on win
            viewModel.userSettings.value?.let { settings ->
                if (settings.vibrationEnabled) {
                    VibrationHelper.vibrateMedium(requireContext())
                }
            }

            // Show notification for big wins
            if (result.winAmount >= 500) {
                NotificationHelper.showWinNotification(requireContext(), result.winAmount)
            }
        } else {
            resultTextView.text = getString(R.string.slot_lose_message)
            resultTextView.setTextColor(resources.getColor(R.color.danger, null))
        }

        val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_scale)
        resultTextView.startAnimation(fadeIn)
    }

    override fun onResume() {
        super.onResume()
        shakeDetector.start(requireContext())
    }

    override fun onPause() {
        super.onPause()
        shakeDetector.stop()
    }
}
