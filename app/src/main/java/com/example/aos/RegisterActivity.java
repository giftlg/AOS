package com.example.aos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText register_name,register_email,register_phone,register_password;
   private   Button register;
   private   ProgressDialog loadingbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

         register_name=(EditText)findViewById(R.id.name);
         register_email=(EditText)findViewById(R.id.register_email);
         register_phone=(EditText)findViewById(R.id.phone);
         register_password=(EditText)findViewById(R.id.register_password);
        Button register_btn=(Button) findViewById(R.id.register_btn) ;
        loadingbar= new ProgressDialog(this);

        TextView Register_login=(TextView) findViewById(R.id.register_login);


        Register_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });



       register_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v)

           {
           CreateAccount();
           }
       });


    }

    private void CreateAccount()
               {
                   String name =  register_name.getText().toString();
                   String email = register_email.getEditableText().toString().replace('.',',');
                   String phone = register_phone.getText().toString();
                   String password = register_password.getText().toString();
                   

                   if (TextUtils.isEmpty(name))
                   {
                       Toast.makeText(this, "enter name", Toast.LENGTH_SHORT).show();

                   }

                   else if (TextUtils.isEmpty(email))
                   {
                       Toast.makeText(this, "enter email", Toast.LENGTH_SHORT).show();

                   }
                   else if (TextUtils.isEmpty(phone))
                   {
                       Toast.makeText(this, "enter phone number", Toast.LENGTH_SHORT).show();

                   }
                   else if (TextUtils.isEmpty(password))
                   {
                       Toast.makeText(this, "enter password", Toast.LENGTH_SHORT).show();

                   }
                   else if (password.length()<6)
                   {
                       Toast.makeText(this, "password must contain 6 or more characters", Toast.LENGTH_SHORT).show();
                   }

                   else
                       {
                           loadingbar.setTitle("Create account");
                           loadingbar.setMessage("Checking credentials");
                           loadingbar.setCanceledOnTouchOutside(false);
                           loadingbar.show();

                           ValidateEmail(name,email,phone,password);

                       }


               }

    private void ValidateEmail(String name, String email, String phone, String password)

    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot)

            {
                if (!(datasnapshot.child("Users").child(email).exists()))

                 {
                     HashMap<String,Object> userdataMap = new HashMap<>();
                     userdataMap.put("name",name);
                     userdataMap.put("email",email);
                     userdataMap.put("phone",phone);
                     userdataMap.put("password",password);

                     RootRef.child("Users").child(email).updateChildren(userdataMap)
                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task)
                                 {
                                     if (task.isSuccessful())

                                     {
                                         Toast.makeText(RegisterActivity.this, "Account created successfully ", Toast.LENGTH_SHORT).show();
                                         loadingbar.dismiss();
                                         Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                         startActivity(intent);


                                     }

                                     else
                                     {
                                         loadingbar.dismiss();
                                         Toast.makeText(RegisterActivity.this, "Check internet connection", Toast.LENGTH_SHORT).show();

                                     }

                                 }
                             });


                 }
                else
                 {
                     Toast.makeText(RegisterActivity.this, "This"+email+"Already exist", Toast.LENGTH_SHORT).show();
                     loadingbar.dismiss();
                     Intent intent= new Intent(RegisterActivity.this,MainActivity.class);
                     startActivity(intent);
                 }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}