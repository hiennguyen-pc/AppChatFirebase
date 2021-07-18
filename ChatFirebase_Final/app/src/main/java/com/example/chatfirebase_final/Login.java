package com.example.chatfirebase_final;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    EditText email,pass;
    FloatingActionButton btnDangnhap;
    FirebaseAuth auth;
    FirebaseFirestore fstone;
    TextView forgotPass;
    String strUsername;
    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth=FirebaseAuth.getInstance();
        fstone=FirebaseFirestore.getInstance();
        email=findViewById(R.id.emaill);
        pass=findViewById(R.id.passWordd);
        btnDangnhap=findViewById(R.id.btnDangNhap);
        forgotPass=findViewById(R.id.forgotPass);
//        if(checkLoginRemember()<0){
//            Toast.makeText(getApplicationContext(),"có",Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
//            finish();
//        }

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,RessetPass.class));
            }
        });
        btnDangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txt_Email=email.getText().toString().trim();
                String txt_pass=pass.getText().toString().trim();
                if(TextUtils.isEmpty(txt_Email)||TextUtils.isEmpty(txt_pass)){
                    Toast.makeText(Login.this,"Không được để trống",Toast.LENGTH_SHORT).show();
                }

                else {
                    auth.signInWithEmailAndPassword(txt_Email,txt_pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(Login.this,"Đăng nhập thành công",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            rememberUser(txt_Email,txt_pass,true);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Login.this,"Sai Email hoặc mật khẩu",Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
    }

    public void SignUp(View view) {


        startActivity(new Intent(getApplicationContext(), Registation.class));
    }
    protected void OnStart(){
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
    }
    public void rememberUser(String mail, String pass, boolean status){
        SharedPreferences sharedPreferences=getSharedPreferences("User_FILE",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        if(!status){

            editor.clear();
        }else {
            editor.putString("mail",mail);
            editor.putString("Pass",pass);
            editor.putBoolean("remember",status);
        }
        editor.commit();
    }
    public int checkLoginRemember(){
        SharedPreferences sharedPreferences=getSharedPreferences("User_FILE",MODE_PRIVATE);
        boolean chk=sharedPreferences.getBoolean("remember",false);
        if(chk){
            strUsername=sharedPreferences.getString("mail","");
            return 1;
        }
        return -1;
    }
}