package com.android.myapplication.todo.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.android.myapplication.todo.R
import com.android.myapplication.todo.util.ARG_DELETE_MESSAGE
import com.android.myapplication.todo.util.ARG_REMINDER_REPEAT_UNIT
import com.android.myapplication.todo.util.ARG_REMINDER_REPEAT_VALUE
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ReminderUnitDialog : DialogFragment() {
    interface Callbacks {
        fun onItemSelected(item:String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val items = arrayOf<String>("Month","Week","Day","Hour","Minute")
            val clickListener = DialogInterface.OnClickListener{ dialog, item->
                targetFragment?.let { fragment ->
                    val stringItem=when(item){
                        0->items.get(0)
                        1->items.get(1)
                        2->items.get(2)
                        3->items.get(3)
                        4->items.get(4)
                        else->items.get(0)
                    }
                    (fragment as ReminderUnitDialog.Callbacks).onItemSelected(stringItem)
                }
            }
            val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
                .setItems(items,clickListener)
            return alertDialogBuilder.create()
        }
}