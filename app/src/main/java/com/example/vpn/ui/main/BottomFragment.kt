package com.example.vpn.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vpn.R
import com.example.vpn.data.source.local.SharedPreferencesDataSource
import com.example.vpn.databinding.BottomSheetLayoutBinding
import com.example.vpn.domain.model.Country
import com.example.vpn.ui.main.adapter.BottomSheetAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val COLLAPSED_HEIGHT = 112

@AndroidEntryPoint
class BottomFragment : BottomSheetDialogFragment() {

    lateinit var prefs : SharedPreferencesDataSource

    private lateinit var binding: BottomSheetLayoutBinding
    private lateinit var behavior: BottomSheetBehavior<FrameLayout>

    private val viewModel by activityViewModels<MainFragmentViewModel>()
    private val adapter by lazy { BottomSheetAdapter(::chooseCountry) }

    private val countryList: MutableList<Country> = mutableListOf()


    override fun getTheme() = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = BottomSheetLayoutBinding.bind(inflater.inflate(R.layout.bottom_sheet_layout, container))
        prefs = SharedPreferencesDataSource(requireContext())
        viewModel.getListFromApi(prefs.getCountryName())
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setupBottomBehavior()
        observeViewModel()
        setupRecyclerView()
    }

    private fun setupBottomBehavior() {
        val density = requireContext().resources.displayMetrics.density

        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        dialog?.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)

        dialog?.let {
//            it.setCancelable(false)
            val bottomSheet = it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            behavior = BottomSheetBehavior.from(bottomSheet)

            behavior.peekHeight = (COLLAPSED_HEIGHT * density).toInt()
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            behavior.isHideable = false

            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

                override fun onStateChanged(bottomSheet: View, newState: Int) {}

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    with(binding) {
                        if (slideOffset > 0) {
                            layoutCollapsed.alpha = 1 - 2 * slideOffset
                            layoutExpanded.alpha = slideOffset * slideOffset

                            if (slideOffset > 0.5) {
                                layoutCollapsed.visibility = View.GONE
                                layoutExpanded.visibility = View.VISIBLE
                            }

                            if (slideOffset < 0.5 && binding.layoutExpanded.visibility == View.VISIBLE) {
                                layoutCollapsed.visibility = View.VISIBLE
                                layoutExpanded.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
            })

            binding.imgClose.setOnClickListener {
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvCountries.adapter = adapter
        binding.rvCountries.layoutManager = LinearLayoutManager(requireContext())

        adapter.submitList(countryList)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.countryList.collect { list ->
                countryList.clear()
                countryList.addAll(list)
                adapter.notifyDataSetChanged()

                if (!list.isNullOrEmpty())
                    setCountry(list, prefs.getCountryName())
            }
        }
    }

    private fun setCountry(countryList: List<Country>,countryName: String) {
        val country = countryList.find { it.shortName == countryName }

        setSingleItem(country)
    }

    private fun chooseCountry(itemPosition: Int) {
        val country = adapter.currentList[itemPosition]
        prefs.setCountryName(country.shortName)
        viewModel.setNewCountryList(country)
        setSingleItem(country)
        if (this::behavior.isInitialized)
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun setSingleItem(country: Country?) {
        binding.singleItem.visibility = View.VISIBLE
        binding.progressBar.visibility = View.INVISIBLE

        if (country != null){
            binding.include.ivFlag.setImageResource(country.flag)
            binding.include.tvCountry.text = country.fullName

            if (country.shortName == "US")
                binding.include.tvBestChoice.visibility = View.VISIBLE
            else
                binding.include.tvBestChoice.visibility = View.GONE
        } else{
            binding.include.ivFlag.setImageResource(R.drawable.ic_placeholder)
            binding.include.tvCountry.text ="Unknown country"
        }
    }
}