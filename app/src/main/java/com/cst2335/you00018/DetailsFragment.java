package com.cst2335.you00018;

import static com.cst2335.you00018.ChatRoomActivity.IS_SENT;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsFragment#} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment {
    private AppCompatActivity parentActivity;
    private Bundle dataFromActivity;
    private long id;
    private boolean isSent;
    int position = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisRow;

        dataFromActivity = getArguments();
        id = dataFromActivity.getLong(ChatRoomActivity.ITEM_ID );
        // Inflate the layout for this fragment
        View result =  inflater.inflate(R.layout.fragment_details, container, false);

        //show the message
        TextView message = (TextView)result.findViewById(R.id.textView);
        message.setText(dataFromActivity.getString(ChatRoomActivity.ITEM_SELECTED));
        //show the id:
        TextView idView = (TextView)result.findViewById(R.id.textView2);
        idView.setText("ID=" + id);

        isSent= dataFromActivity.getBoolean(String.valueOf(IS_SENT));
        CheckBox isSentCheck = (CheckBox)result.findViewById(R.id.checkBox);
        if (isSent){
            isSentCheck.setChecked(true);
        } else {
            isSentCheck.setChecked(false);
        }

        // get the delete button, and add a click listener:
        Button finishButton = (Button)result.findViewById(R.id.button3);
        finishButton.setOnClickListener( click -> {

            //Tell the parent activity to remove
            parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
        });

        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be ChatroomActivity for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity)context;
    }
}
