package com.example.vpn.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vpn.R
import com.example.vpn.data.list.CountryList
import com.example.vpn.data.model.Country
import com.example.vpn.data.source.local.SharedPreferencesDataSource
import com.example.vpn.databinding.BottomSheetLayoutBinding
import com.example.vpn.ui.main.adapter.BottomSheetAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject

private const val COLLAPSED_HEIGHT = 130

class BottomFragment : BottomSheetDialogFragment() {

    @Inject
    lateinit var prefs : SharedPreferencesDataSource

    private lateinit var binding: BottomSheetLayoutBinding
    private lateinit var behavior: BottomSheetBehavior<FrameLayout>

    private val viewModel by viewModels<MainFragmentViewModel>()
    private val adapter by lazy { BottomSheetAdapter(::chooseCountry) }

    private val countryList: MutableList<Country> = mutableListOf()


    override fun getTheme() = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = BottomSheetLayoutBinding.bind(inflater.inflate(R.layout.bottom_sheet_layout, container))
        prefs = SharedPreferencesDataSource(requireContext())
        setCountry(prefs.getCountryIndex())
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

        dialog?.let {
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

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        viewModel.countryList.observe(viewLifecycleOwner) { list ->
            countryList.clear()
            countryList.addAll(list)
            adapter.notifyDataSetChanged()
        }
    }

    private fun setupRecyclerView() {
        binding.rvCountries.adapter = adapter
        binding.rvCountries.layoutManager = LinearLayoutManager(requireContext())

        adapter.submitList(countryList)
    }

    private fun setCountry(itemPosition: Int) {
        val country = CountryList.list[itemPosition]
        viewModel.setNewCountryList(country)
        setSingleItem(country)
    }

    private fun chooseCountry(itemPosition: Int) {
        prefs.setCountryIndex(itemPosition)
        val country = adapter.currentList[itemPosition]
        viewModel.setNewCountryList(country)
        setSingleItem(country)
        if (this::behavior.isInitialized)
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        Log.d("BottomFragment", "country = $country")
    }

    private fun setSingleItem(country: Country) {
        binding.include.ivFlag.setImageResource(country.flag)
        binding.include.tvCountry.text = country.name

        if (country.name == CountryList.list[0].name)
            binding.include.tvBestChoice.visibility = View.VISIBLE
        else
            binding.include.tvBestChoice.visibility = View.GONE
    }
}