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

import com.example.aos.Model.Users;
import com.example.aos.prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText input_email,input_password;
    private Button login_btn;
    private ProgressDialog loadinbar;
    private String parentDbName = "Users";
    private CheckBox CHKBoxRememberMe;
    private TextView admin_link,not_admin_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        input_email=(EditText)findViewById(R.id.login_email);
        input_password=(EditText)findViewById(R.id.login_password);
        login_btn=(Button) findViewById(R.id.login_button);
        admin_link=(TextView)findViewById(R.id.admin_link);
        not_admin_link=(TextView)findViewById(R.id.not_admin_link);
        loadinbar=new ProgressDialog(this);

        CHKBoxRememberMe = (CheckBox) findViewById(R.id.remember_me_chkbx);
        Paper.init(this);

        TextView Login_Register=(TextView) findViewById(R.id.log_register);


        Login_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)

            {
                LoginUser();
                
            }
        });


        admin_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                login_btn.setText("Login Admin");
                admin_link.setVisibility(View.INVISIBLE);
                not_admin_link.setVisibility(View.VISIBLE);
                parentDbName="Admins";

            }
        });

        not_admin_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                login_btn.setText("Login");
                admin_link.setVisibility(View.VISIBLE);
                not_admin_link.setVisibility(View.INVISIBLE);
                parentDbName="Users";

            }
        });


    }

    private void LoginUser()
    {
        String login_email = input_email.getText().toString().replace('.',',') ;
        String login_password = input_password.getText().toString();

        if (TextUtils.isEmpty(login_email))
        {
            Toast.makeText(LoginActivity.this, "Please enter email address", Toast.LENGTH_SHORT).show();

        }

       else if (TextUtils.isEmpty(login_password))
        {
            Toast.makeText(LoginActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();

        }
       else

           {
               loadinbar.setTitle("Login");
               loadinbar.setMessage("Logging into your account");
               loadinbar.setCanceledOnTouchOutside(false);
               loadinbar.show();

               AllowAccessToAccount(login_email,login_password);


        }


    }

    private void AllowAccessToAccount(final String login_email, final String login_password)

    {
        if (CHKBoxRememberMe.isChecked())
        {
         Paper.book().write(Prevalent.UserEmailKey,login_email);
         Paper.book().write(Prevalent.UserPasswordKey,login_password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.child(parentDbName).child(login_email).exists())

                {
                    Users usersData= snapshot.child(parentDbName).child(login_email).getValue(Users.class);

                    if (usersData.getEmail().equals(login_email))
                    {
                        if (usersData.getPassword().equals(login_password))
                        {
                           if (parentDbName.equals("Admins"))
                           {
                               Toast.makeText(LoginActivity.this,"Welcome Admin",Toast.LENGTH_SHORT).show();
                               loadinbar.dismiss();
                               Intent intent = new Intent(LoginActivity.this,AdminActivity.class);
                               startActivity(intent);

                           }
                           else if (parentDbName.equals("Users"))
                           {
                               Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                               startActivity(intent);
                               Toast.makeText(LoginActivity.this,"Login successful",Toast.LENGTH_SHORT).show();
                               loadinbar.dismiss();


                           }

                        }
                        else
                            {
                                Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                                loadinbar.dismiss();
                            }
                    }

                }
                else
                    {
                        Toast.makeText(LoginActivity.this,"Account "+login_email+" not registered",Toast.LENGTH_SHORT).show();
                        loadinbar.dismiss();

                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}