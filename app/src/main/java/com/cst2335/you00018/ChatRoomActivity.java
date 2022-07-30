package com.cst2335.you00018;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Arrays;

public class ChatRoomActivity extends AppCompatActivity {

    public static final String ITEM_SELECTED = "ITEM";
    public static final String ITEM_POSITION = "POSITION";
    public static final String ITEM_ID = "ID";
    public static final Boolean IS_SENT = false;
    DetailsFragment dFragment = new DetailsFragment();

    ArrayList<Messages> myMessages = new ArrayList<>();
    private static final String TAG = "ChatRoomActivity";
    MyListAdapter myAdapter;
    SQLiteDatabase db;
    MyOpenHelper dbOpener;


    public class Messages {
        String message;
        Boolean isSend;
        long id;

        /*Constructor*/
        public Messages(String message, Boolean isSend, long id) {
            this.message = message;
            this.isSend = isSend;
            this.id = id;
        }

        public String getText() {
            return message;
        }

        public long getId() {
            return id;
        }

        public String getMessage() {
            return message;
        }

        public Boolean getSorR() {
            return isSend;
        }
    }


    //public static FragmentManager fragmentManager;
    //DetailsFragment tFragment = new DetailsFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        boolean isTablet = findViewById(R.id.fragmentItem) != null;

        loadDataFromDatabase();
        myAdapter = new MyListAdapter();
        ListView myList = findViewById(R.id.listView);
        myList.setAdapter(myAdapter);
        Log.i("before", "before listener");

        myList.setOnItemClickListener((list, view, position, id) -> {
            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putString(ITEM_SELECTED, myMessages.get(position).getText());
            dataToPass.putInt(ITEM_POSITION, position);
            dataToPass.putLong(ITEM_ID, id);
            dataToPass.putBoolean(String.valueOf(IS_SENT), myMessages.get(position).getSorR());
            Log.i("inside", "inside listener");

            if (isTablet) {
//                DetailsFragment dFragment = new DetailsFragment();
                Log.i("test", "get if there");
                dFragment.setArguments(dataToPass); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentItem, dFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
            } else
            {
                Log.i("test", "get else there");
                Intent nextActivity = new Intent(ChatRoomActivity.this, EmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }
        });
        Button sendButton = findViewById(R.id.button_send);
        Button receiveButton = findViewById(R.id.button_receive);

        sendButton.setOnClickListener(click ->
        {
            EditText chatMsg = findViewById(R.id.editText);
            String inputString = chatMsg.getText().toString();

            ContentValues newRowValues = new ContentValues();

            //put 1 or 0 in the sender column:
            newRowValues.put(MyOpenHelper.COL_SEND_OR_RECEIVE, 1);
            newRowValues.put(MyOpenHelper.COL_MESSAGE, inputString);

            //Now insert in the database:
            long newId = db.insert(MyOpenHelper.TABLE_NAME, null, newRowValues);


            myMessages.add(new Messages(inputString, true, newId));
            myAdapter.notifyDataSetChanged();
            chatMsg.setText("");
            myList.setSelection(myAdapter.getCount() - 1);
        });
        receiveButton.setOnClickListener(click ->
        {
            EditText chatMsg = findViewById(R.id.editText);
            String inputString = chatMsg.getText().toString();

            ContentValues newRowValues = new ContentValues();

            //put 1 or 0 in the sender column:
            newRowValues.put(MyOpenHelper.COL_SEND_OR_RECEIVE, 0);
            newRowValues.put(MyOpenHelper.COL_MESSAGE, inputString);

            //Now insert in the database:
            long newId = db.insert(MyOpenHelper.TABLE_NAME, null, newRowValues);


            myMessages.add(new Messages(inputString, false, newId));
            myAdapter.notifyDataSetChanged();
            chatMsg.setText("");
            myList.setSelection(myAdapter.getCount() - 1);
        });

        myList.setOnItemLongClickListener((p, b, pos, id) -> {
            Messages selectedMessage = myMessages.get(pos);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Want to delete?")

                    //What is the message:
                    .setMessage("Do you want to delete this row?")

                    //what the Yes button does:
                    .setPositiveButton("Yes", (click, arg) -> {
                        db.delete(MyOpenHelper.TABLE_NAME, MyOpenHelper.COL_ID+ "= ?", new String[]{Long.toString(selectedMessage.getId())});
                        myMessages.remove(pos); //remove the contact from contact list
                        myAdapter.notifyDataSetChanged();
                    })
                    //What the No button does:
                    .setNegativeButton("No", (click, arg) -> {
                    })


                    //Show the dialog
                    .create().show();
            return true;
        });

    }

    public class Message {

        private String msgText;
        private boolean isSend;
        private long id;

        public Message(String msgText, boolean isSend, long id) {
            this.msgText = msgText;
            this.isSend = isSend;
            this.id = id;
        }

        public String getText() {
            return msgText;
        }

        public boolean isSend() {
            return isSend;
        }

        public long getID() {
            return id;
        }
    }


    class MyListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return myMessages.size();
        }

        @Override
        public Object getItem(int position) {
            return myMessages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            View thisRow;


            if (myMessages.get(position).getSorR()) {
                thisRow = inflater.inflate(R.layout.send, null);
                TextView sendText = thisRow.findViewById(R.id.text_send);
                sendText.setText(myMessages.get(position).getText());
            } else {
                thisRow = inflater.inflate(R.layout.receive, null);
                TextView sendText = thisRow.findViewById(R.id.text_receive);
                sendText.setText(myMessages.get(position).getText());
            }


            return thisRow;
        }
    }

    private void loadDataFromDatabase() {
        //get a database connection:
        dbOpener = new MyOpenHelper(this);
        db = dbOpener.getWritableDatabase(); //This calls onCreate() if you've never built the table before, or onUpgrade if the version here is newer


        // We want to get all of the columns. Look at MyOpener.java for the definitions:
        String[] columns = {MyOpenHelper.COL_ID, MyOpenHelper.COL_SEND_OR_RECEIVE, MyOpenHelper.COL_MESSAGE};
        //query all the results from the database:
        Cursor results = db.query(false, MyOpenHelper.TABLE_NAME, columns, null, null, null, null, null, null);

        //Now the results object has rows of results that match the query.
        //find the column indices:
        int senderColumnIndex = results.getColumnIndex(MyOpenHelper.COL_SEND_OR_RECEIVE);
        int msgColIndex = results.getColumnIndex(MyOpenHelper.COL_MESSAGE);
        int idColIndex = results.getColumnIndex(MyOpenHelper.COL_ID);

        //iterate over the results, return true if there is a next item:
        while (results.moveToNext()) {
            int isSendInt = results.getInt(senderColumnIndex);
            boolean isSend;
            isSend = isSendInt == 1;
            String message = results.getString(msgColIndex);
            long id = results.getLong(idColIndex);

            myMessages.add(new Messages(message, isSend, id));
        }

        printCursor(results, db.getVersion());
    }

    public void printCursor(Cursor c, int version) {
        ArrayList<Messages> rowValue = new ArrayList<>();

        int idIndex = c.getColumnIndex(MyOpenHelper.COL_ID);
        int senderIndex = c.getColumnIndex(MyOpenHelper.COL_SEND_OR_RECEIVE);
        int messageIndex = c.getColumnIndex(MyOpenHelper.COL_MESSAGE);


        c.moveToFirst();
        while (!c.isAfterLast()) {
            String msg = c.getString(messageIndex);
            boolean sender = (c.getInt(senderIndex) != 0);
            long id = c.getInt(idIndex);
            rowValue.add(new Messages(msg, sender, id));
            c.moveToNext();
        }

        Log.e("DATABASE VERSION", db.getVersion() + "");
        Log.e("NUMBER OF COLUMNS", c.getColumnCount() + "");
        Log.e("COLUMN NAMES", Arrays.toString(c.getColumnNames()));
        Log.e("NUMBER OF ROWS", c.getCount() + "");
        Log.e("EACH ROW OF RESULTS", rowValue.toString());

    }
}

