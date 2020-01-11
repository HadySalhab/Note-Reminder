package com.android.myapplication.todo.ui.edit

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.android.myapplication.todo.data.Notes
import com.android.myapplication.todo.repositories.NotesRepository
import com.android.myapplication.todo.util.Event
import kotlinx.coroutines.launch
import com.android.myapplication.todo.R
import com.android.myapplication.todo.util.Destination

class NotesEditViewModel(
    private val notesRepository: NotesRepository,
    private val noteIdentifier: String?,
    private val app: Application
) : AndroidViewModel(app) {


    companion object {
        private const val TAG = "NotesEditViewModel"
    }

    private lateinit var editableNote: Notes

    init {
        initializeNote()
    }

    private val _snackBarEvent = MutableLiveData<Event<String>>()
    val snackBarEvent: LiveData<Event<String>>
        get() = _snackBarEvent

    private val _navigationEvent = MutableLiveData<Event<Destination>>()
    val navigationEvent: LiveData<Event<Destination>>
        get() = _navigationEvent

    private val _showDatePickerEvent = MutableLiveData<Event<String>>()
    val showDatePickerEvent: LiveData<Event<String>>
        get() = _showDatePickerEvent

    private val _showDeletDialogEvent = MutableLiveData<Event<Unit>>()
    val showDeleteDialogEvent: LiveData<Event<Unit>>
        get() = _showDeletDialogEvent


    val _titleEditText = MutableLiveData<String>()

    val _descriptionEditText = MutableLiveData<String>()

    val _favoriteCheckBox = MutableLiveData<Boolean>()

    val _dateTextView = MutableLiveData<String>()


    fun initializeNote() {
        viewModelScope.launch {
            if (noteIdentifier != null) {
                editableNote = notesRepository.getNoteById(noteIdentifier) ?: Notes()
            } else {
                editableNote = Notes()
            }
            updateUI()
        }
    }

    fun updateUI() {
        _titleEditText.value = editableNote.title
        _descriptionEditText.value = editableNote.description
        _favoriteCheckBox.value = editableNote.isFavorite
        _dateTextView.value = editableNote.date
    }

    override fun onCleared() {
        super.onCleared()
        showlog()
    }

    fun showlog() {
        Log.d(TAG, "showlog: title is ${_titleEditText.value}")
        Log.d(TAG, "showlog: desc is ${_descriptionEditText.value}")
        Log.d(TAG, "showlog: isfav ${_favoriteCheckBox.value}")
        Log.d(TAG, "showlog: date is ${_dateTextView.value}")
    }


    fun deleteNote() {
        viewModelScope.launch {
            notesRepository.delete(editableNote)
        }

    }

    fun saveNote() {
        if (_titleEditText.value.isNullOrEmpty() || _descriptionEditText.value.isNullOrEmpty()) {
            _snackBarEvent.value = Event(app.getString(R.string.snackbartext_emptynote))
        } else {
            updateNote()
            viewModelScope.launch {
                if (noteIdentifier == null) {
                    notesRepository.insert(editableNote)
                } else {
                    notesRepository.update(editableNote)
                }
            }
            _navigationEvent.value = Event(Destination.UP)
        }
    }

    fun updateNote() {
        editableNote.apply {
            title = _titleEditText.value!!
            description = _descriptionEditText.value!!
            isFavorite = _favoriteCheckBox.value!!
            date = _dateTextView.value!!
        }

    }

    fun navigateUp() {
        _navigationEvent.value = Event(Destination.UP)
    }

    fun showDatePicker() {
        _showDatePickerEvent.value = Event(editableNote.date)
    }

    fun showDeleteDialog() {
        _showDeletDialogEvent.value = Event(Unit)
    }

    fun updateDateTextView(date: String) {
        _dateTextView.value = date
    }

    fun deleteAndNavigateToList(){
        deleteNote()
        _navigationEvent.value = Event(Destination.VIEWPAGERFRAGMENT)
    }

}