package com.example.aos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.aos.Model.Users;
import com.example.aos.prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog loadinbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button login=(Button) findViewById(R.id.main_login_button);
        Button register =(Button) findViewById(R.id.main_register_button);
        loadinbar= new ProgressDialog(this);

        Paper.init(this);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)

            {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);

            }
        });

          String UserEmailKey = Paper.book().read(Prevalent.UserEmailKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        if (UserEmailKey !="" && UserPasswordKey !="")
        {
            if (!TextUtils.isEmpty(UserEmailKey) &&  !TextUtils.isEmpty(UserPasswordKey))
            {
                AlloweAccess(UserEmailKey,UserPasswordKey);

                loadinbar.setTitle("");
                loadinbar.setMessage("Please wait....");
                loadinbar.setCanceledOnTouchOutside(false);
                loadinbar.show();

            }
        }


    }

    private void AlloweAccess(final String login_email, final String login_password)

    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.child("Users").child(login_email).exists())

                {
                    Users usersData= snapshot.child("Users").child(login_email).getValue(Users.class);
                    if (usersData.getEmail().equals(login_email))
                    {
                        if (usersData.getPassword().equals(login_password))
                        {
                            Toast.makeText(MainActivity.this,"Login successful",Toast.LENGTH_SHORT).show();
                            loadinbar.dismiss();
                            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                            startActivity(intent);


                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                            loadinbar.dismiss();
                        }


                    }


                }
                else
                {
                    Toast.makeText(MainActivity.this,"Account"+login_email+"not registered",Toast.LENGTH_SHORT).show();
                    loadinbar.dismiss();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}