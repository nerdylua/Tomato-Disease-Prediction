package com.example.sasya2.ui.remedies

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.cardview.widget.CardView
import com.example.sasya2.*
import com.example.sasya2.databinding.FragmentRemediesBinding

class RemediesFragment : Fragment() {
    private var _binding: FragmentRemediesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRemediesBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        with(binding) {
            tomatoCard.setOnClickListener { navigateToCropDetail(TomatoActivity::class.java) }
            potatoCard.setOnClickListener { navigateToCropDetail(PotatoActivity::class.java) }
            cherryCard.setOnClickListener { navigateToCropDetail(CherryActivity::class.java) }
            pepperCard.setOnClickListener { navigateToCropDetail(PepperActivity::class.java) }
            grapeCard.setOnClickListener { navigateToCropDetail(GrapeActivity::class.java) }
            appleCard.setOnClickListener { navigateToCropDetail(AppleActivity::class.java) }
        }
    }

    private fun navigateToCropDetail(activityClass: Class<*>) {
        startActivity(Intent(requireContext(), activityClass))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}