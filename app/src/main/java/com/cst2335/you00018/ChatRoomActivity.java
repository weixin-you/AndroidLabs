package com.cst2335.you00018;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
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


import java.util.ArrayList;
import java.util.Arrays;

public class ChatRoomActivity extends AppCompatActivity {
    private static final String TAG = "ChatRoomActivity";

    MyOpenHelper myOpener;
    SQLiteDatabase database;

    public static class Messages {
        String inputMessage;
        public Boolean sendOrReceive;
        long id;

        private Messages(String inputMessage, Boolean sendOrReceive, long idIn) {
            this.inputMessage = inputMessage;
            this.sendOrReceive = sendOrReceive;
            this.id=idIn;
        }

        public long getId() {
            return id;
        }
    }
    ArrayList<Messages> messagesList = new ArrayList<>();

    MyListAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        myOpener = new MyOpenHelper( this );
        //open database
        database = myOpener.getWritableDatabase();
        Cursor results = database.rawQuery( "Select * from " + MyOpenHelper.TABLE_NAME + ";", null );
        int clmIndex = results.getColumnIndex( MyOpenHelper.COL_ID );
        int msgIndex = results.getColumnIndex( MyOpenHelper.COL_MESSAGE);
        int sOrRIndex = results.getColumnIndex( MyOpenHelper.COL_SEND_OR_RECEIVE);

        //returns false if no more data
        while( results.moveToNext() ) {
            int id = results.getInt(clmIndex);
            String message = results.getString( msgIndex );
            boolean sendOrReceive = results.getInt(sOrRIndex)==1;
            messagesList.add( new Messages( message,sendOrReceive, id ));
        }


        ListView myList = findViewById(R.id.listView);
        myList.setAdapter(myAdapter = new MyListAdapter());
        EditText editText = findViewById(R.id.type_hint);
        Button sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener( click -> {
            String inputText = editText.getText().toString();

            ContentValues newRow = new ContentValues();
            //Message column:
            newRow.put( MyOpenHelper.COL_MESSAGE , inputText );
            //Send or receive column:
            newRow.put( MyOpenHelper.COL_SEND_OR_RECEIVE, 1 );
            //now that columns are full, you insert:
            long id = database.insert( MyOpenHelper.TABLE_NAME, null, newRow );

            Log.i(TAG, "Add a row of sending message");

            messagesList.add(new Messages(inputText,true, id));
            editText.setText("");
            myAdapter.notifyDataSetChanged();
        });
        Button receiveButton = findViewById(R.id.receive_button);
        receiveButton.setOnClickListener( click -> {
            String inputText = editText.getText().toString();
            ContentValues newRow = new ContentValues();
            //Message column:
            newRow.put( MyOpenHelper.COL_MESSAGE , inputText );
            //Send or receive column:
            newRow.put(MyOpenHelper.COL_SEND_OR_RECEIVE, 0);
            //now that columns are full, you insert:
            long id = database.insert( MyOpenHelper.TABLE_NAME, null, newRow );
            Log.i(TAG, "Add a row of receiving message");
            messagesList.add(new Messages(inputText,false, id));
            editText.setText("");
            myAdapter.notifyDataSetChanged();
        });

        myList.setOnItemLongClickListener( (p, b, pos, id) -> {
            Messages whatWasClicked = messagesList.get(pos);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Do you want to delete this?")

                    //What is the message:
                    .setMessage("The selected row is:"+ pos+"\n"+"The database id is:"+id)
                    //what the Yes button does:
                    .setPositiveButton("Yes", (click, arg) -> {
                        messagesList.remove(pos);
                        myAdapter.notifyDataSetChanged();
                        database.delete(MyOpenHelper.TABLE_NAME,MyOpenHelper.COL_ID+"=?",new String[]{Long.toString(whatWasClicked.getId())});
                    })
                    //What the No button does:
                    .setNegativeButton("No", (click, arg) -> { })

                    //You can add extra layout elements:
                    .setView(getLayoutInflater().inflate(R.layout.receive, null) )

                    //Show the dialog
                    .create().show();

            return true;
        });

        printCursor(results,database.getVersion());

    }

    public void printCursor(Cursor c, int inVersion){
        int columnNumber = c.getColumnCount();
        String[] columnNames = c.getColumnNames();
        int rowNumber = c.getCount();

        c.moveToFirst();

        while (!c.isAfterLast()){
            int msgColIndex = c.getColumnIndex(MyOpenHelper.COL_MESSAGE);
            int sOrRColIndex = c.getColumnIndex(MyOpenHelper.COL_SEND_OR_RECEIVE);
            int idColIndex = c.getColumnIndex(MyOpenHelper.COL_ID);

            String message = c.getString(msgColIndex);
            int sendOrReceive = c.getInt(sOrRColIndex);
            long id = c.getLong(idColIndex);

            String row=String.format("Message="+message+",SendOrReceive="+sendOrReceive+",id="+id);
            Log.i("ROW VALUES", row);

            c.moveToNext();}


        Log.i("Database version", Integer.toString(inVersion));
        Log.i("Number of columns", Integer.toString(columnNumber));
        Log.i("Column names", Arrays.toString(columnNames));
        Log.i("Number of rows", Integer.toString(rowNumber));

    }


    public class MyListAdapter extends BaseAdapter {

        public int getCount() { return messagesList.size();}
        public Object getItem(int position) { return messagesList.get(position).inputMessage; }
        public long getItemId(int position) { return (long) position; }
        public View getView(int position, View old, ViewGroup parent)
        {
            LayoutInflater inflater = getLayoutInflater();
            if (messagesList.get(position).sendOrReceive){
                View view1 = inflater.inflate(R.layout.send, parent, false);
                EditText editText1 = view1.findViewById(R.id.text_send);
                editText1.setText( getItem(position).toString() );
                return view1;
            }
            else  {
                View view2 = inflater.inflate(R.layout.receive, parent, false);
                EditText editText2 = view2.findViewById(R.id.text_receive);
                editText2.setText(getItem(position).toString());
                return view2;
            }
        }
    }
}