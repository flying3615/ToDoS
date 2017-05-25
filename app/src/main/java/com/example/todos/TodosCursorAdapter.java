package com.example.todos;

import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.todos.data.TodosContract;
import com.squareup.picasso.Picasso;


public class TodosCursorAdapter extends CursorAdapter {
    public TodosCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
                R.layout.todo_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView todoTextView = (TextView) view.findViewById(R.id.tvText);
        int textColumn = cursor.getColumnIndex(TodosContract.TodosEntry.COLUMN_TEXT);
        String text = cursor.getString(textColumn);

        int dateColumn = cursor.getColumnIndex(TodosContract.TodosEntry.COLUMN_EXPIRED);
        String expiredDate = cursor.getString(dateColumn);

        todoTextView.setText(text+"("+expiredDate+")");


        ImageView imageView = (ImageView) view.findViewById(R.id.doneIcon);
        int ifDone = cursor.getInt(cursor.getColumnIndex(TodosContract.TodosEntry.COLUMN_DONE));
        imageView.setImageDrawable(null);
        if(ifDone==1){
            Picasso.with(context).load(R.drawable.correct).into(imageView);
        }

    }
}
