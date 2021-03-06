package com.example.todos;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.todos.data.TodosContract;
import com.example.todos.data.TodosQueryHandler;
import com.example.todos.databinding.ActivityTodoBinding;
import com.example.todos.model.Category;
import com.example.todos.model.CategoryList;
import com.example.todos.model.Todo;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.todos.data.TodosContract.TodosEntry.COLUMN_DONE;

public class TodoActivity extends AppCompatActivity {
    Todo todo;
    TodosQueryHandler handler;
    Spinner spinner;
    CategoryList list;
    CategoryListAdapter adapter;
    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int position=0;
        handler =  new TodosQueryHandler(getContentResolver());
        ActivityTodoBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_todo);
        Intent intent = getIntent();
        todo = (Todo)intent.getSerializableExtra("todo");
        list = (CategoryList) intent.getSerializableExtra("categories");

        Category c = (Category) intent.getSerializableExtra("current_select_category");


        adapter = new CategoryListAdapter(list.ItemList);
        spinner=(Spinner) findViewById(R.id.spCategories);
        spinner.setAdapter(adapter);
        //set the bindings
        binding.setTodo(todo);
        //spinner, selected right
        if (Integer.valueOf(todo.category.get()) == 0&&c!=null) {
            for (Category cat : list.ItemList) {
                if (Integer.valueOf(cat.catId.get()) == Integer.valueOf(c.catId.get())) {
                    break;
                }
                position++;
            }
            spinner.setSelection(position);

        }
        else {
            for (Category cat : list.ItemList) {
                if (Integer.valueOf(cat.catId.get()) == Integer.valueOf(todo.category.get())) {
                    break;
                }
                position++;
            }
            spinner.setSelection(position);
        }


        findViewById(R.id.save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saveOrUpdateTODO()){
                    Intent goBack = new Intent(TodoActivity.this,TodoListActivity.class);
                    startActivity(goBack);
                }else{
                    new AlertDialog.Builder(TodoActivity.this)
                            .setTitle(getString(R.string.delete_todo_dialog_title))
                            .setMessage("Cannot create TODO for \"All Category\". Please select a category")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    }).show();
                }
            }
        });

        final EditText expiredDate = (EditText) findViewById(R.id.editExpiryDate);
        expiredDate.setInputType(InputType.TYPE_NULL);
        expiredDate.requestFocus();


        expiredDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                Calendar newCalendar = Calendar.getInstance();
                DatePickerDialog fromDatePickerDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        expiredDate.setText(dateFormatter.format(newDate.getTime()));
                    }

                },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                fromDatePickerDialog.show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete_todo) {
            //confirm?
            new AlertDialog.Builder(TodoActivity.this)
                    .setTitle(getString(R.string.delete_todo_dialog_title))
                    .setMessage(getString(R.string.delete_todo_dialog))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            //delete
                            Uri uri =  Uri.withAppendedPath(TodosContract.TodosEntry.CONTENT_URI, String.valueOf(todo.Id.get()));

                            String selection = TodosContract.TodosEntry._ID + "=?";
                            String[] arguments = new String[1];
                            arguments[0] = String.valueOf(todo.Id.get());

                            handler.startDelete(1, null, uri
                                    , selection, arguments);
                            Intent intent = new Intent(TodoActivity.this, TodoListActivity.class);
                            startActivity(intent);

                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }



        return super.onOptionsItemSelected(item);
    }

    //I use the onpause method to save data to the db
    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
    }


    private boolean saveOrUpdateTODO(){
        String [] args = new String[1];
        TodosQueryHandler handler =  new TodosQueryHandler(getContentResolver());
        //save data(existing todo)
        //read the current category
        Category cat = (Category)spinner.getSelectedItem();
        int catId = cat.catId.get();

        if(catId==-1){
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(TodosContract.TodosEntry.COLUMN_TEXT, todo.text.get());
        values.put(TodosContract.TodosEntry.COLUMN_CATEGORY, catId);
        values.put(TodosContract.TodosEntry.COLUMN_DONE, todo.done.get());
        values.put(TodosContract.TodosEntry.COLUMN_EXPIRED, todo.expired.get());
        if(todo != null && todo.Id.get() != 0) {
            args[0] = String.valueOf(todo.Id.get());
            handler.startUpdate(1,null,TodosContract.TodosEntry.CONTENT_URI, values,
                    TodosContract.TodosEntry._ID + "=?", args);
        }
        else if(todo != null && todo.Id.get() == 0) {
            handler.startInsert(1,null,TodosContract.TodosEntry.CONTENT_URI, values);

        }
        return true;
    }
}
