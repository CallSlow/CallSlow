package com.example.callslow.objects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.callslow.R;

import java.util.List;

public class MessageAdaptater extends BaseAdapter {
    private Context mContext;
    private List<Message> mMessageList;

    public MessageAdaptater(Context mContext, List<Message> mMessageList) {
        this.mContext = mContext;
        this.mMessageList = mMessageList;
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
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.item_conversation_sender_messages, parent, false);
        }

        TextView messageContentView = view.findViewById(R.id.text_message_sender);
        TextView senderNameView = view.findViewById(R.id.text_user_sender);

        Message message = mMessageList.get(position);
        Contact sender = message.getSender();

        messageContentView.setText(message.getContent());
        senderNameView.setText(sender.getName());

        return view;
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
