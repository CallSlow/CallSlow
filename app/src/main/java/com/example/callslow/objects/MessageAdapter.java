package com.example.callslow.objects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.callslow.R;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends BaseAdapter {
    private Context mContext;
    private List<Message> mMessageList;
    private ArrayList<String> settingslist;

    public MessageAdapter(Context mContext, List<Message> mMessageList) {
        this.mContext = mContext;
        this.mMessageList = mMessageList;
    }

    public void add(Message message)
    {
        List<Message> messageList = this.getmMessageList();
        messageList.add(message);
        this.setmMessageList(messageList);
    }

    @Override
    public int getCount() {
        return mMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = mMessageList.get(position);

        settingslist = Settings.getInstance().getSettings();

        LayoutInflater inflater = LayoutInflater.from(mContext);

        if (message.getSenderMac().equals(settingslist.get(0))) {

            convertView = inflater.inflate(R.layout.item_conversation_receiver_messages, parent, false);


            TextView messageContentView = convertView.findViewById(R.id.text_message_receiver);
            //TextView senderNameView = view.findViewById(R.id.text_user_sender);
            //Contact sender = message.getSender();
            messageContentView.setText(message.getContent());
            //senderNameView.setText(message.getSenderMac());
        } else {

            convertView = inflater.inflate(R.layout.item_conversation_sender_messages, parent, false);

            TextView messageContentView = convertView.findViewById(R.id.text_message_sender);
            TextView senderNameView = convertView.findViewById(R.id.text_user_sender);

            //Contact sender = message.getSender();

            messageContentView.setText(message.getContent());
            senderNameView.setText(message.getSenderMac());
        }
        return convertView;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public List<Message> getmMessageList() {
        return mMessageList;
    }

    public void setmMessageList(List<Message> mMessageList) {
        this.mMessageList = mMessageList;
    }
}
