package com.andreisemyonov.exchangerates.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.andreisemyonov.exchangerates.adapter.ViewPagerAdapter
import com.andreisemyonov.exchangerates.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val fragmentTitleArray = arrayOf("All currencies", "Favorites currencies")

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position -> tab.text = fragmentTitleArray[position]
        }.attach()
    }
}