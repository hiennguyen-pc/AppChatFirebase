package com.example.chatfirebase_final;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.chatfirebase_final.Adapter.MessageAdapter;
import com.example.chatfirebase_final.Fragment.APIService;
import com.example.chatfirebase_final.Model.Chat;
import com.example.chatfirebase_final.Model.User;
import com.example.chatfirebase_final.Notifications.Client;
import com.example.chatfirebase_final.Notifications.Data;
import com.example.chatfirebase_final.Notifications.MyResponse;
import com.example.chatfirebase_final.Notifications.Sender;
import com.example.chatfirebase_final.Notifications.Token;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatMain extends AppCompatActivity {

    CircleImageView circleImageView;
    TextView username1;
    String userName2;
    Intent intent;
    FirebaseUser firebaseUser;
    Toolbar toolbar;
    ImageButton btn_send;
    ImageView imageIv;
    CircleImageView chat_left;
    ImageButton btn_pick;
    EditText txt_text;
    private Uri imageURI;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    private StorageTask uploadTask;
    DatabaseReference reference;
    private int IMAGE_REQUEST=1;

    private static int RESULT_LOAD_IMAGE = 1;
    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=200;
    private static final int IMAGE_PICK_CAMERA_CODE=300;
    private static final int IMAGE_PICK_GALLERY_CODE=400;
    String[] cameraPermission;
    String[] storagePermission;
    Uri imgae_rui=null;


    MessageAdapter messageAdapter;
    List<Chat> mchat;
    RecyclerView recyclerView;
    ValueEventListener seenlistener;

    String img;

    APIService apiService;
    boolean notify=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);
        circleImageView=findViewById(R.id.profile_imageChat);
        username1=findViewById(R.id.userNameMainChat);
        toolbar=findViewById(R.id.toolbarChat);
        btn_send=findViewById(R.id.btn_send);
        btn_pick=findViewById(R.id.pick);



        cameraPermission=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission=new  String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        storageReference= FirebaseStorage.getInstance().getReference("Uploads");

        txt_text=findViewById(R.id.textMess);
        recyclerView=findViewById(R.id.RecyclerviewChat);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ChatMain.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        intent=getIntent();
        userName2=intent.getStringExtra("UserName");

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notify=true;
                firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
                String msg=txt_text.getText().toString();
                if(!msg.equals("")){
                    sendMessage(firebaseUser.getUid(),userName2,msg);


                }else {
                    Toast.makeText(getApplicationContext(),"Không gửi tin nhắn rỗng",Toast.LENGTH_SHORT).show();
                }

                txt_text.setText("");
            }
        });

        btn_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showImagePickDialog();
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,IMAGE_REQUEST);
            }
        });

        databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(userName2);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                User user=snapshot.getValue(User.class);
                username1.setText(user.getUser());
                //circleImageView.setImageResource(R.mipmap.ic_launcher);
                if(user.getImageInfo().equals("default")){
                    circleImageView.setImageResource(R.mipmap.ic_launcher);

                }
                else {
                    Glide.with(getApplicationContext()).load(user.getImageInfo()).into(circleImageView);
                }


                readMessage(firebaseUser.getUid(),userName2,img);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
        seenMessage(userName2);

    }
    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        HashMap<String , Object> hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("isseen",false);
        hashMap.put("type","text");
        reference.child("Chats").push().setValue(hashMap);
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference chatRef=FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid()).child(userName2);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                if(!snapshot.exists()){
                    chatRef.child("id").setValue(userName2);
//                    sendNotifiaction(receiver, user.getUser(), message);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        String msg=message;
        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                if(notify) {

                    sendNotifiaction(receiver, user.getUser(), msg);

                }
                    notify = false;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendNotifiaction(String receiver,final String username,final String message){
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query =tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Token token=dataSnapshot.getValue(Token.class);
                    Data data=new Data(firebaseUser.getUid(),R.mipmap.ic_launcher,username+":"+message,"Tin nhắn mới",userName2);
                    Sender sender=new Sender(data,token.getToken());
                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {

                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                            if(response.code()==200){

                                if(response.body().success != 1){
                                    Toast.makeText(ChatMain.this,"Failed",Toast.LENGTH_SHORT).show();
                                }

                            }


                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void seenMessage(String userid){
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Chats");
        seenlistener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chat chat=dataSnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid())&&chat.getSender().equals(userid)){
                        HashMap<String,Object>hashMap=new HashMap<>();
                        hashMap.put("isseen",true);
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void readMessage(String myid, String username, String ImageURL){
        mchat=new ArrayList<>();
        databaseReference=FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mchat.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    Chat chat=snapshot1.getValue(Chat.class);
                    if(chat.getReceiver().equals(myid)&&chat.getSender().equals(username)||chat.getReceiver().equals(username)&&chat.getSender().equals(myid)){
                        mchat.add(chat);
                    }
                    messageAdapter=new MessageAdapter(ChatMain.this,mchat,img);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void status(String status){
        firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
        databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        Map<String, Object> hashMap=new HashMap<>();
        hashMap.put("status",status);

        databaseReference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.removeEventListener(seenlistener);
        status("offline");
    }
    private void showImagePickDialog(){
        String[] option={"Camera","Gallery"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Chọn ảnh");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else {
                        pickFromCamera();
                    }
                }
                if(which==1){
                    if(!checkStoragepermission()){
                        requestStoragePermission();
                    }
                    else {
                        pickFromeGallery();
                    }

                }
            }
        });
        builder.create().show();

    }
    private boolean checkStoragepermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }
    private void pickFromeGallery(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }
    private void pickFromCamera(){
        ContentValues cv=new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE,"Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp Descr");
        imgae_rui=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv);
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imgae_rui);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);

    }
    private boolean checkCameraPermission(){
        boolean result=ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result1=ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return  result && result1;
    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=this.getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getMimeTypeFromExtension(contentResolver.getType(uri));
    }
    private void   uploadImage(){
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pd.show();
        if(imageURI!=null){
            StorageReference fileReferences=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageURI));
            uploadTask=fileReferences.putFile(imageURI);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReferences.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri=task.getResult();
                        String mUri=downloadUri.toString();
                        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
                        HashMap<String,Object>hashMap=new HashMap<>();
                        hashMap.put("sender",firebaseUser.getUid());
                        hashMap.put("receiver",userName2);
                        hashMap.put("message",mUri);
                        hashMap.put("isseen",false);
                        hashMap.put("type","image");
                        databaseReference.child("Chats").push().setValue(hashMap);

                        DatabaseReference chatRef=FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid()).child(userName2);
                        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user=snapshot.getValue(User.class);
                                if(!snapshot.exists()){
                                    chatRef.child("id").setValue(userName2);
                                    sendNotifiaction(firebaseUser.getUid(),userName2,"Đã gửi 1 ảnh");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        pd.dismiss();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else {
            Toast.makeText(getApplicationContext(),"No image selected",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if(requestCode==RESULT_OK){
//            if(requestCode==IMAGE_PICK_GALLERY_CODE){
//                imgae_rui=data.getData();
//                try {
//                    SendImageMessage(imgae_rui);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//            else if(requestCode==IMAGE_PICK_CAMERA_CODE){
//                try {
//                    SendImageMessage(imgae_rui);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_REQUEST&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
            imageURI=data.getData();
            if(uploadTask!=null&&uploadTask.isInProgress()){
                Toast.makeText(getApplicationContext(),"Uplaod in preogress",Toast.LENGTH_SHORT).show();
            }
            else {
                uploadImage();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted&&storageAccepted){
                        pickFromCamera();
                    }else {
                        Toast.makeText(this, "Camera & Storage",Toast.LENGTH_SHORT).show();
                    }
                }else {

                }
            }break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean storageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        pickFromeGallery();
                    }else {
                        Toast.makeText(this, "Storage",Toast.LENGTH_SHORT).show();
                    }
                }
            }break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void SendImageMessage(Uri imgae_rui) throws IOException {
        notify=true;
        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Đang gửi...");
        progressDialog.show();
        String timeStamp=""+System.currentTimeMillis();
        String fileNamePath="ChatImages/"+"post_"+timeStamp;
        Bitmap bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),imgae_rui);
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] data=baos.toByteArray();
        StorageReference ref= FirebaseStorage.getInstance().getReference().child(fileNamePath);
        ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                String downloatUri=uriTask.getResult().toString();
                if(uriTask.isSuccessful()){
                    DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
                    HashMap<String,Object>hashMap=new HashMap<>();
                    hashMap.put("sender",firebaseUser.getUid());
                    hashMap.put("receiver",userName2);
                    hashMap.put("message",downloatUri);
                    hashMap.put("isseen",false);
                    hashMap.put("type","image");
                    databaseReference.child("Chats").push().setValue(hashMap);
                    DatabaseReference database=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                    database.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user=snapshot.getValue(User.class);
                            if(notify){
                                sendNotifiaction(userName2, user.getUser(), "Sent you a photo...");
                            }
                            notify=false;
                            sendNotifiaction(userName2, user.getUser(), "Bạn nhận được tin nhắn");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    DatabaseReference chatRef=FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid()).child(userName2);
                    chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user=snapshot.getValue(User.class);
                            if(!snapshot.exists()){
                                chatRef.child("id").setValue(userName2);
                                //sendNotifiaction(receiver, user.getUser(), message);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    DatabaseReference chatRef1=FirebaseDatabase.getInstance().getReference("Chatlist").child(userName2).child(firebaseUser.getUid());
                    chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user=snapshot.getValue(User.class);
                            if(!snapshot.exists()){
                                chatRef.child("id").setValue(firebaseUser.getUid());
                                //sendNotifiaction(receiver, user.getUser(), message);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });

    }

}