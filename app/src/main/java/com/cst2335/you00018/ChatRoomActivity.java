package com.cst2335.you00018;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class ChatRoomActivity extends AppCompatActivity {
    private static final String TAG = "ChatRoomActivity";
    public static class Messages {
        String inputMessage;
        public Boolean sendOrReceive;

        private Messages(String inputMessage, Boolean sendOrReceive) {
            this.inputMessage = inputMessage;
            this.sendOrReceive = sendOrReceive;
        }
    }
    ArrayList<Messages> myMessages = new ArrayList<>();

    MyListAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        ListView myList = findViewById(R.id.listView);
        myList.setAdapter(myAdapter = new MyListAdapter());
        EditText editText = findViewById(R.id.type_hint);
        Button sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener( click -> {
            String inputText = editText.getText().toString();
            Log.i(TAG, "Add a row of sending message");
            myMessages.add(new Messages(inputText,true));
            editText.setText("");
            myAdapter.notifyDataSetChanged();
        });
        Button receiveButton = findViewById(R.id.receive_button);
        receiveButton.setOnClickListener( click -> {
            String inputText = editText.getText().toString();
            Log.i(TAG, "Add a row of receiving message");
            myMessages.add(new Messages(inputText,false));
            editText.setText("");
            myAdapter.notifyDataSetChanged();
        });

        myList.setOnItemLongClickListener( (p, b, pos, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Do you want to delete this?")
                    .setIcon(0)
                    .setMessage("The selected row is:"+ (id+1)+"\n"+"The database id is:"+id)
                    .setPositiveButton("Yes", (click, arg) -> {
                        myMessages.remove(pos);
                        myAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("No", (click, arg) -> { })
                    .setView(getLayoutInflater().inflate(R.layout.send, null) )
                    .create().show();

            return true;
        });

    }


    public class MyListAdapter extends BaseAdapter {

        public int getCount() { return myMessages.size();}
        public Object getItem(int position) { return myMessages.get(position).inputMessage; }
        public long getItemId(int position) { return (long) position; }
        public View getView(int position, View old, ViewGroup parent)
        {
            LayoutInflater inflater = getLayoutInflater();
            if (myMessages.get(position).sendOrReceive){
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