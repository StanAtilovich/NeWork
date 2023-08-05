package ru.netology.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentProfileBinding
import ru.netology.nework.viewModel.ProfileViewModel

class ProfileFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentProfileBinding.inflate(inflater, container, false)
        val viewModel : ProfileViewModel by viewModels(
            ownerProducer = ::requireParentFragment
        )

        binding.btSignOut.setOnClickListener {
            viewModel.onSignOut()
            findNavController().popBackStack()
        }

        return binding.root
    }

}