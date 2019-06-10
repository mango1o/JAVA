package com.example.todo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import data.Constant;
import data.DatabaseManager;
import data.DateHandler;
import data.Event;

import java.util.Calendar;
import java.util.Date;

import jp.wasabeef.richeditor.RichEditor;

public class NewItemActivity extends AppCompatActivity {
    private ImageButton datelineSetButton;
    private TextView datelineTextView;
    private TextView labelTextView;
    private ImageButton labelSelectButton;
    private EditText titleEditText;
    private RichEditor psRichEditor;

    private int labelId;
    private Date date = new Date();
    private DatabaseManager databaseManager;
    private boolean newOrEdit = Constant.NEW;
    private int editId;
    private boolean editIsDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
        init();

        datelineSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c1 = Calendar.getInstance();
                new TimePickerDialog(NewItemActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                date.setHours(hourOfDay);
                                date.setMinutes(minute);
                                datelineTextView.setText(DateHandler.parseDatetoString(date));
                            }
                        }, c1.get(Calendar.HOUR_OF_DAY), c1.get(Calendar.MINUTE), true).show();

                Calendar c = Calendar.getInstance();
                new DatePickerDialog(NewItemActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                date.setYear(year - 1900);
                                date.setMonth(monthOfYear);
                                date.setDate(dayOfMonth);
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        labelSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                labelSelectButton.showContextMenu();
            }
        });
    }

    public void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_check);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        databaseManager = new DatabaseManager(this);
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHARED_SETTING, Context.MODE_APPEND);
        labelId = sharedPreferences.getInt(Constant.DEFAULT_LABEL_ID_SETTING, Constant.DEFAULT_LABEL_ID);
        datelineSetButton = (ImageButton) findViewById(R.id.dateline_set_button);
        datelineTextView = (TextView) findViewById(R.id.dateline_text_view);
        labelTextView = (TextView) findViewById(R.id.label_text_view);
        labelSelectButton = (ImageButton) findViewById(R.id.label_select_button);
        titleEditText = (EditText) findViewById(R.id.title_edit);
        psRichEditor = (RichEditor) findViewById(R.id.ps_edit);
        initRichEditor();

        initLabelTextView(sharedPreferences.getInt(Constant.DEFAULT_LABEL_ID_SETTING, Constant.DEFAULT_LABEL_ID));
        editSetting();
        registerForContextMenu(labelSelectButton);
    }

    public void editSetting() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        newOrEdit = bundle.getBoolean(Constant.NEW_OR_EDIT, Constant.NEW);
        if (!newOrEdit) {
            setTitle("Edit");
            editId = bundle.getInt(Constant.ID);
            editIsDone = bundle.getBoolean(Constant.IS_DONE);
            initLabelTextView(bundle.getInt(Constant.LABEL_ID));
            labelId = bundle.getInt(Constant.LABEL_ID);
            date = DateHandler.parseStringtoDate(bundle.getString(Constant.DATE_LINE));
            datelineTextView.setText(bundle.getString(Constant.DATE_LINE));
            titleEditText.setText(bundle.getString(Constant.TITLE));
            psRichEditor.setHtml(bundle.getString(Constant.PS));
        }
    }

    public void initLabelTextView(int labelId) {
        switch (labelId) {
            case Constant.DEFAULT_LABEL_ID:
                labelTextView.setText("Default set");
                break;
            case Constant.LABEL_ID_1:
                labelTextView.setText("Set 1");
                break;
            case Constant.LABEL_ID_2:
                labelTextView.setText("Set 2");
        }
    }

    public void initRichEditor() {
        psRichEditor.setEditorHeight(200);
        psRichEditor.setEditorFontSize(16);
        psRichEditor.setEditorFontColor(Color.BLACK);
        psRichEditor.setPadding(10, 10, 10, 10);
        psRichEditor.setPlaceholder("More...");

        findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                psRichEditor.undo();
            }
        });

        findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                psRichEditor.redo();
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                psRichEditor.setBold();
            }
        });

        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                psRichEditor.setItalic();
            }
        });

        findViewById(R.id.action_subscript).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                psRichEditor.setSubscript();
            }
        });

        findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                psRichEditor.setSuperscript();
            }
        });

        findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                psRichEditor.setStrikeThrough();
            }
        });

        findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                psRichEditor.setUnderline();
            }
        });

        findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                psRichEditor.setHeading(1);
            }
        });

        findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                psRichEditor.setHeading(2);
            }
        });

        findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                psRichEditor.setHeading(3);
            }
        });

        findViewById(R.id.action_heading4).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                psRichEditor.setHeading(4);
            }
        });

        findViewById(R.id.action_heading5).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                psRichEditor.setHeading(5);
            }
        });

        findViewById(R.id.action_heading6).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                psRichEditor.setHeading(6);
            }
        });

        findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;
            @Override public void onClick(View v) {
                psRichEditor.setTextColor(isChanged ? Color.BLACK : Color.BLUE);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;
            @Override public void onClick(View v) {
                psRichEditor.setTextBackgroundColor(isChanged ? Color.WHITE : Color.CYAN);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                psRichEditor.setIndent();
            }
        });

        findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                psRichEditor.setOutdent();
            }
        });

        findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                psRichEditor.setAlignLeft();
            }
        });

        findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                psRichEditor.setAlignCenter();
            }
        });

        findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                psRichEditor.setAlignRight();
            }
        });

        findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                psRichEditor.setBlockquote();
            }
        });

        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                psRichEditor.insertImage("http://www.1honeywan.com/dachshund/image/7.21/7.21_3_thumb.JPG", "dachshund");
            }
        });

        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                psRichEditor.insertLink("https://github.com/wasabeef", "wasabeef");
            }
        });
        findViewById(R.id.action_insert_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                psRichEditor.insertTodo();
            }
        });
    }

    public void eventCommit(boolean newOrEdit) {
        if (newOrEdit) {
            Event event = new Event(0, false, titleEditText.getText().toString(), psRichEditor.getHtml().toString(),
                    date, labelId);
            databaseManager.insertEventItem(event);
        } else {
            Event event = new Event(editId, editIsDone, titleEditText.getText().toString(),
                    psRichEditor.getHtml().toString(), date, labelId);
            databaseManager.updateEventItem(event);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_item_activity_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.new_item_done:
                if (TextUtils.isEmpty(titleEditText.getText())) {
                    Toast.makeText(this, "No empty", Toast.LENGTH_LONG).show();
                } else {
                    eventCommit(newOrEdit);
                    finish();
                }
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Choose set");
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.label_select, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.label_id:
                labelId = Constant.DEFAULT_LABEL_ID;
                break;
            case R.id.label_id1:
                labelId = Constant.LABEL_ID_1;
                break;
            case R.id.label_id2:
                labelId = Constant.LABEL_ID_2;
                break;
            default:
                labelId = Constant.DEFAULT_LABEL_ID;
                break;
        }
        labelTextView.setText(item.getTitle().toString());
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseManager.closeDatabase();
    }
}
