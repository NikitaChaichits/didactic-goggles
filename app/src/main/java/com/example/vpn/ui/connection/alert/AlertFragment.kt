package com.example.vpn.ui.connection.alert

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vpn.R
import com.example.vpn.common.base.BaseFragment
import com.example.vpn.databinding.FragmentAlertBinding
import com.example.vpn.ui.MainActivity
import com.example.vpn.ui.connection.AdActivityViewModel
import com.example.vpn.ui.connection.State.*
import com.example.vpn.ui.connection.alert.adapter.IssuesResultAdapter
import com.example.vpn.util.time.handleWithDelay
import kotlinx.coroutines.launch

class AlertFragment : BaseFragment(R.layout.fragment_alert) {

    override val viewModel by activityViewModels<AdActivityViewModel>()
    private val adapter by lazy { IssuesResultAdapter() }

    private var issuesList: List<String> = listOf()

    private lateinit var binding: FragmentAlertBinding

    private var state: String = INITIAL.name
    // «X issues were found», где X - третий параметр пастборда (1&2&X)
    private var issues: Int = 3

    //TODO Delete
    var isSubscribed = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlertBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.clInitial.visibility = View.VISIBLE
        binding.clDetected.visibility = View.INVISIBLE

        setListeners()
        observeViewModels()
    }

    private fun setListeners() {
        binding.btnStartScan.setOnClickListener{
            when (state) {
                DETECTED.name -> {
                    viewModel.setState(SCANNING.name)
                    navigate(R.id.action_alert_fragment_to_scan_fragment)
                }
                FOUNDED.name -> {
                    // Кнопка «Fix Connection Issues» - недельная подписка.
                   // if ("subscription is successfull" == "") {
                    if (isSubscribed) {
                        viewModel.setState(REMOVING.name)
                        navigate(R.id.action_alert_fragment_to_scan_fragment)
                    } else {
                        isSubscribed = true
                        dialogBuilder(R.string.fr_alert_error).show()
                    }
                }
                RESULT.name -> {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun observeViewModels() {
        lifecycleScope.launch {
            viewModel.state.collect { newState ->
                state = newState
                when (newState) {
                    INITIAL.name -> {
                        handleWithDelay(3000L) {
                            binding.clInitial.visibility = View.GONE
                            binding.clDetected.visibility = View.VISIBLE
                            viewModel.setState(DETECTED.name)
                        }
                    }
                    DETECTED.name -> {
                        binding.clInitial.visibility = View.GONE
                        binding.clDetected.visibility = View.VISIBLE

                        binding.clMessageDetected.visibility = View.VISIBLE
                        binding.clMessageFounded.visibility = View.GONE
                        binding.rvMessageResult.visibility = View.GONE

                        binding.ivMain.setImageResource(R.drawable.ic_attention)
                        binding.tvTitle.setText(R.string.fr_alert_detected)
                        binding.tvTitle2.setText(R.string.fr_alert_connection_issues)
                        binding.btnStartScan.setText(R.string.fr_alert_start_scanning)
                        binding.ivDots.setImageResource(R.drawable.ic_three_dots_1)
                    }
                    FOUNDED.name -> {
                        binding.clInitial.visibility = View.GONE
                        binding.clDetected.visibility = View.VISIBLE

                        binding.clMessageDetected.visibility = View.GONE
                        binding.clMessageFounded.visibility = View.VISIBLE
                        binding.rvMessageResult.visibility = View.GONE

                        binding.ivMain.setImageResource(R.drawable.ic_bug)
                        binding.tvTitle.text = String.format(getString(
                            R.string.fr_alert_issues), issues)
                        binding.tvTitle2.setText(R.string.fr_alert_were_founded)
                        binding.btnStartScan.setText(R.string.fr_alert_fix_issues)
                        binding.ivDots.setImageResource(R.drawable.ic_three_dots_2)
                    }
                    RESULT.name -> {
                        setupRecyclerView()

                        binding.clInitial.visibility = View.GONE
                        binding.clDetected.visibility = View.VISIBLE

                        binding.clMessageDetected.visibility = View.GONE
                        binding.clMessageFounded.visibility = View.GONE
                        binding.rvMessageResult.visibility = View.VISIBLE

                        binding.ivMain.setImageResource(R.drawable.ic_verify)
                        binding.tvTitle.text = String.format(getString(
                            R.string.fr_alert_issues_are_fixed), issues)
                        binding.tvTitle2.setText(R.string.fr_alert_connection_safe)
                        binding.btnStartScan.setText(R.string.fr_alert_done)
                        binding.ivDots.setImageResource(R.drawable.ic_three_dots_3)
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvMessageResult.adapter = adapter
        binding.rvMessageResult.layoutManager = LinearLayoutManager(requireContext())

        issuesList = listOf(getString(R.string.fr_alert_first_item),
        getString(R.string.fr_alert_second_item))

        adapter.submitList(issuesList)
    }

}