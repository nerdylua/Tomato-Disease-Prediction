package com.example.sasya2.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sasya2.LeafScanActivity
import com.example.sasya2.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupUI()
        // Uncomment if needed to observe ViewModel data
        //observeViewModel(homeViewModel)

        return binding.root
    }

    // Set up the user interface and handle button clicks
    private fun setupUI() {
        binding.ScanButton.setOnClickListener {
            try {
                val intent = Intent(requireContext(), LeafScanActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Unable to start scan activity.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun observeViewModel(homeViewModel: HomeViewModel) {
        homeViewModel.text.observe(viewLifecycleOwner) { text ->
            binding.textView4.text = text
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}