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
import androidx.paging.ExperimentalPagingApi
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentCreatePostBinding
import ru.netology.nework.dto.Post
import ru.netology.nework.util.AndroidUtils
import ru.netology.nework.viewModel.PostViewModel


@ExperimentalPagingApi
class CreatePostFragment : Fragment() {

    private lateinit var binding: FragmentCreatePostBinding
    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        viewModel.editedPost.observe(viewLifecycleOwner) { editedPost ->
            editedPost?.let {

                (activity as MainActivity?)?.setActionBarTitle(getString(R.string.change_post_fragment_title))
                binding.eTPostContent.setText(editedPost.content)
                binding.eTPostContent.requestFocus(
                    binding.eTPostContent.text.lastIndex
                )
                AndroidUtils.showKeyboard(binding.eTPostContent)
            }
        }
        return binding.root
    }

    override fun onDestroy() {
        if (viewModel.editedPost.value != null) {
            viewModel.invalidateEditPost()
        }
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_create_edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                val content = binding.eTPostContent.text.toString()
                viewModel.editedPost.value?.let {
                    viewModel.savePost(it.copy(content = content))
                } ?: viewModel.savePost(Post(content = content))
                AndroidUtils.hideKeyboard(requireView())
                findNavController().popBackStack()
                true
            }
            else -> false
        }
    }
}