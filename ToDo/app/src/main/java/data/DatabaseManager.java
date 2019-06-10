package data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager implements Serializable {

    private SQLiteOpenHelper sqLiteOpenHelper;
    private SQLiteDatabase database;

    public DatabaseManager(Context context) {
        sqLiteOpenHelper = new MyDatabaseHelper(context, "ToDoList.db", null, 1);
        database = sqLiteOpenHelper.getWritableDatabase();
    }

    public void insertEventItem(Event event) {
        ContentValues values = new ContentValues();
        values.put(Constant.IS_DONE, event.isDone() ? 1 : 0);
        values.put(Constant.TITLE, event.getEventTitle());
        values.put(Constant.PS, event.getEventPs());
        values.put(Constant.DATE_LINE, DateHandler.parseDatetoString(event.getDateLine()));
        values.put(Constant.LABEL_ID, event.getLabelId());
        database.insert(Constant.TODO_LIST, null, values);
    }

    public void deleteEventItem(Event event) {
        database.delete(Constant.TODO_LIST, "id = ?", new String[]{"" + event.getEventId()});
    }

    public void updateEventItem(Event event) {
        ContentValues values = new ContentValues();
        values.put(Constant.IS_DONE, event.isDone() ? 1 : 0);
        values.put(Constant.TITLE, event.getEventTitle());
        values.put(Constant.PS, event.getEventPs());
        values.put(Constant.DATE_LINE, DateHandler.parseDatetoString(event.getDateLine()));
        values.put(Constant.LABEL_ID, event.getLabelId());
        database.update(Constant.TODO_LIST, values, "id = ?", new String[]{"" + event.getEventId()});
    }

    public List<Event> queryLabelItems(int labelId) {
        List<Event> eventList = new ArrayList<Event>();
        Cursor cursor = database.query(Constant.TODO_LIST, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Event event;
            if ((cursor.getInt(cursor.getColumnIndex(Constant.LABEL_ID)) == labelId &&
                    cursor.getInt(cursor.getColumnIndex(Constant.IS_DONE)) == 0)) {
                event = new Event(cursor.getInt(cursor.getColumnIndex(Constant.ID)),
                        cursor.getInt(cursor.getColumnIndex(Constant.IS_DONE)) == 1,
                        cursor.getString(cursor.getColumnIndex(Constant.TITLE)),
                        cursor.getString(cursor.getColumnIndex(Constant.PS)),
                        DateHandler.parseStringtoDate(cursor.getString(cursor.getColumnIndex(Constant.DATE_LINE))),
                        cursor.getInt(cursor.getColumnIndex(Constant.LABEL_ID)));
                eventList.add(event);
            }
        }
        cursor.close();
        return eventList;
    }

    public List<Event> queryDoneItems() {
        List<Event> eventList = new ArrayList<Event>();
        Cursor cursor = database.query(Constant.TODO_LIST, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Event event;
            if (cursor.getInt(cursor.getColumnIndex(Constant.IS_DONE)) == 1) {
                event = new Event(cursor.getInt(cursor.getColumnIndex(Constant.ID)),
                        cursor.getInt(cursor.getColumnIndex(Constant.IS_DONE)) == 1,
                        cursor.getString(cursor.getColumnIndex(Constant.TITLE)),
                        cursor.getString(cursor.getColumnIndex(Constant.PS)),
                        DateHandler.parseStringtoDate(cursor.getString(cursor.getColumnIndex(Constant.DATE_LINE))),
                        cursor.getInt(cursor.getColumnIndex(Constant.LABEL_ID)));
                eventList.add(event);
            }
        }
        cursor.close();
        return eventList;
    }
    public void closeDatabase() {
        database.close();
    }
}
