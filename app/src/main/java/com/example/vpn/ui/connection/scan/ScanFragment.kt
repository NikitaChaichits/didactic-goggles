package com.example.vpn.ui.connection.scan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.vpn.R
import com.example.vpn.common.base.BaseFragment
import com.example.vpn.databinding.FragmentScanBinding
import com.example.vpn.ui.connection.AdActivityViewModel
import com.example.vpn.ui.connection.State.*
import com.natasa.progressviews.CircleProgressBar
import kotlinx.coroutines.delay

private const val SEARCH_DELAY = 60L

class ScanFragment : BaseFragment(R.layout.fragment_scan) {

    override val viewModel by activityViewModels<AdActivityViewModel>()

    private lateinit var binding : FragmentScanBinding
    private lateinit var circleProgressBar: CircleProgressBar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScanBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        circleProgressBar = binding.pbScanning
        circleProgressBar.setStartPositionInDegrees(-90)
        circleProgressBar.setRoundEdgeProgress(true)

        observeViewModels()
    }

    private fun observeViewModels() {
        lifecycleScope.launchWhenResumed {
            viewModel.state.collect { state ->
                when (state) {
                    SCANNING.name -> {
                        binding.tvTitleBold.setText(R.string.fr_scan_scanning_for)
                        circleProgressBar.setProgress(0f)
                        for (i in 0..100){
                            delay(SEARCH_DELAY)
                            circleProgressBar.setProgress(i.toFloat())
                            binding.tvProgress.text = "$i %"
                        }

                        viewModel.setState(FOUNDED.name)
                    }
                    REMOVING.name -> {
                        binding.tvTitleBold.setText(R.string.fr_scan_removing)

                        circleProgressBar.setProgress(0f)
                        for (i in 0..100){
                            delay(SEARCH_DELAY)
                            circleProgressBar.setProgress(i.toFloat())
                            binding.tvProgress.text = "$i %"
                        }

                        viewModel.setState(RESULT.name)
                    }
                    FOUNDED.name -> {
                        navigate(R.id.action_scan_fragment_to_alert_fragment)
                    }
                    RESULT.name -> {
                        if (findNavController().currentDestination?.id == R.id.scan_fragment)
                            navigate(R.id.action_scan_fragment_to_alert_fragment)
                    }
                }
            }
        }
    }
}