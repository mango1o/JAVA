package com.example.todo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import data.Constant;
import data.DatabaseManager;
import data.DateHandler;
import data.Event;

import java.util.List;

public class ToDoAdapter extends BaseAdapter {
    private List<Event> eventList;
    private int resource;
    private LayoutInflater inflater;
    private DatabaseManager databaseManager;
    private Context context;
    private int labelId;
    private int sortBy;

    public ToDoAdapter(List<Event> eventList, int resource, int labelId, Context context, DatabaseManager databaseManager, int sortBy) {
        this.eventList = eventList;
        this.resource = resource;
        this.labelId = labelId;
        this.context = context;
        this.databaseManager = databaseManager;
        this.sortBy = sortBy;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int i) {
        return eventList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(resource, null);
        }
        final Event event = eventList.get(i);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(event.getEventTitle());
        TextView dateline = (TextView) view.findViewById(R.id.dateline);
        dateline.setText(DateHandler.parseDatetoString(event.getDateLine()));
        final ToggleButton isDone = (ToggleButton) view.findViewById(R.id.is_done);
        isDone.setChecked(event.isDone());
        isDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isDone.setChecked(b);
                event.setDone(!event.isDone());
                databaseManager.updateEventItem(event);
                ((MainActivity) context).startListFragment(labelId, sortBy);
            }
        });

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.todo_item);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constant.IS_DONE, event.isDone());
                bundle.putString(Constant.TITLE, event.getEventTitle());
                bundle.putString(Constant.PS, event.getEventPs());
                bundle.putInt(Constant.LABEL_ID, event.getLabelId());
                bundle.putString(Constant.DATE_LINE, DateHandler.parseDatetoString(event.getDateLine()));
                Intent intent = new Intent(view.getContext(), CheckItemActivity.class);
                intent.putExtras(bundle);
                view.getContext().startActivity(intent);
            }
        });
        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });
        return view;
    }
}
