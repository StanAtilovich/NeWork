package ru.netology.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentCreateEditPostMenuBinding
import ru.netology.nework.dto.Post
import ru.netology.nework.viewModel.PostViewModel

class CreatePostFragment : Fragment() {

    private lateinit var binding: FragmentCreateEditPostMenuBinding
    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateEditPostMenuBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)


        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_post_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_add_post -> {
                val content = binding.eTPostContent.text.toString()
                viewModel.savePost(Post(content = content))
                findNavController().popBackStack()
                true
            }
            else -> false
        }
    }
}
