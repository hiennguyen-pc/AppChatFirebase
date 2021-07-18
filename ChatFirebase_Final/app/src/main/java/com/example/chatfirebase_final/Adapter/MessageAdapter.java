package com.example.chatfirebase_final.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatfirebase_final.ChatMain;
import com.example.chatfirebase_final.Model.Chat;
import com.example.chatfirebase_final.Model.ChatList;
import com.example.chatfirebase_final.Model.User;
import com.example.chatfirebase_final.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context context;

    public static int MSG_TYPE_LEFT=0;
    public static int MSG_TYPE_RIGHT=1;
    FirebaseUser firebaseUser;

    private List<Chat> mChat;
    private String imageurl;
    public MessageAdapter(Context context, List<Chat> mChat, String imageurl) {
        this.context = context;
        this.mChat = mChat;
        this.imageurl=imageurl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else {

            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Chat chat=mChat.get(position);
        holder.show_message.setText(chat.getMessage());
//        if(imageurl.equals("default")){
//            holder.img_left.setImageResource(R.mipmap.ic_launcher);
//
//        }else {
//            Glide.with(context).load(imageurl).into(holder.img_left);
//        }
        String type= mChat.get(position).getType();
        String mess=mChat.get(position).getMessage();
        if(type.equals("text")){
            holder.show_message.setVisibility(View.VISIBLE);
            holder.messageIv.setVisibility(View.GONE);
            holder.show_message.setText(mess);
        }
        else {
            holder.show_message.setVisibility(View.GONE);
            holder.messageIv.setVisibility(View.VISIBLE);
            Glide.with(context).load(mess).into(holder.messageIv);
        }
        if(position==mChat.size()-1){
            if(chat.isIsseen()){
                holder.txt_seen.setText("Đã xem");
            }else {
                holder.txt_seen.setText("Đã gửi");
            }
        }
        else {
            holder.txt_seen.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;
        public ImageView profile_Image,messageIv;
        public TextView txt_seen;
        public CircleImageView img_left;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message=itemView.findViewById(R.id.showMessage);
            profile_Image=itemView.findViewById(R.id.profile_image);
            txt_seen=itemView.findViewById(R.id.txt_seen);
            messageIv=itemView.findViewById(R.id.messageIv);
            img_left=itemView.findViewById(R.id.profile_image_right);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;

        }
        else {
            return MSG_TYPE_LEFT;

        }
    }
}