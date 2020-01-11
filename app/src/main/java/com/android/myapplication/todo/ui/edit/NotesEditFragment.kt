package com.android.myapplication.todo.ui.edit


import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.android.myapplication.todo.R
import com.android.myapplication.todo.databinding.FragmentNotesEditBinding
import com.android.myapplication.todo.ui.dialogs.DatePickerFragment
import com.android.myapplication.todo.ui.dialogs.DeleteDialogFragment
import com.android.myapplication.todo.util.*
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * A simple [Fragment] subclass.
 */
class NotesEditFragment : Fragment(), DatePickerFragment.Callbacks,DeleteDialogFragment.Callbacks {
    private val args by navArgs<NotesEditFragmentArgs>()
    private lateinit var binding: FragmentNotesEditBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfig: AppBarConfiguration
    private val viewModel: NotesEditViewModel by viewModel {
        parametersOf(args.noteIdentifier)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = findNavController()
        binding = FragmentNotesEditBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setuptoolbar()
        setHasOptionsMenu(true)
        return binding.root
    }

    fun setuptoolbar() {
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.editToolbar)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            appBarConfig = AppBarConfiguration(navController.graph)
            setupActionBarWithNavController(navController, appBarConfig)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_optionmenu, menu)
        menu.findItem(R.id.edit_item_delete).isVisible = args.noteIdentifier != null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.snackBarEvent.observe(viewLifecycleOwner, EventObserver { message ->
            if (!TextUtils.isEmpty(message)) {
                showSnackBar(message)
            }
        })
        viewModel.navigationEvent.observe(viewLifecycleOwner, EventObserver {destination->
            when(destination){
                Destination.UP->{
                    navController.navigateUp()
                }
                Destination.VIEWPAGERFRAGMENT->{
                    navController.popBackStack(R.id.homeViewPagerFragment,false)
                }
            }
        })

        viewModel.showDatePickerEvent.observe(viewLifecycleOwner, EventObserver { date ->
            DatePickerFragment.newInstance(date).apply {
                setTargetFragment(this@NotesEditFragment, REQUEST_DATE)
                show(this@NotesEditFragment.requireFragmentManager(), DIALOG_DATE)
            }
        })
        viewModel.showDeleteDialogEvent.observe(viewLifecycleOwner, EventObserver {
            DeleteDialogFragment().apply {
                setTargetFragment(this@NotesEditFragment, REQUEST_DELETE_ANSWER)
                show(this@NotesEditFragment.requireFragmentManager(), DIALOG_DELETE)
            }
        })
    }

    fun showSnackBar(message: String) {
        Snackbar.make(
            requireActivity().findViewById<CoordinatorLayout>(R.id.edit_coordinatorlayout),
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_item_save -> {
                viewModel.saveNote()
                true
            }
            R.id.edit_item_date -> {
                viewModel.showDatePicker()
                true
            }
            R.id.edit_item_delete -> {
                viewModel.showDeleteDialog()
                true
            }
            android.R.id.home -> {
                viewModel.navigateUp()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onDateSelected(date: String) {
        viewModel.updateDateTextView(date)
    }

    override fun onPositiveButtonClick() {
        viewModel.deleteAndNavigateToList()
    }


}
