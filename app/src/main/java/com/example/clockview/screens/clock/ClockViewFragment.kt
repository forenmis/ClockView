package com.example.clockview.screens.clock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.clockview.databinding.FragmentClockViewBinding
import com.example.clockview.entity.Weather

class ClockViewFragment : Fragment() {
    private lateinit var binding: FragmentClockViewBinding
    private lateinit var viewModel: ClockViewFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ClockViewFragmentViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClockViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.timeLD.observe(viewLifecycleOwner) { binding.clockView.updTime(it) }
        with(binding) {
            ivCloud.setOnClickListener { clockView.updWeather(Weather.Cloud(18)) }
            ivSnow.setOnClickListener { clockView.updWeather(Weather.Snow(0)) }
            ivWind.setOnClickListener { clockView.updWeather(Weather.Wind(15)) }
            ivSunny.setOnClickListener { clockView.updWeather(Weather.Sunny(20)) }
            btPlus.setOnClickListener { clockView.updHeartBeat(1) }
            btRemove.setOnClickListener { clockView.updHeartBeat(-1) }
        }
    }
}