package com.alexander.sharedpreferences;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private CheckBox checkBoxRememberMe;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        checkBoxRememberMe = findViewById(R.id.checkBoxRememberMe);

        // Obtener la instancia de SharedPreferences
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Comprobar si se guardaron los datos del usuario previamente
        boolean rememberMe = sharedPreferences.getBoolean("rememberMe", false);
        if (rememberMe) {
            String username = sharedPreferences.getString("username", "");
            String password = sharedPreferences.getString("password", "");

            editTextUsername.setText(username);
            editTextPassword.setText(password);
            checkBoxRememberMe.setChecked(true);
        }

        checkBoxRememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    editor.clear();
                    editor.apply();
                }
            }
        });

        Button buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override

            public void  onClick(View v){
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();
                boolean rememberMe = checkBoxRememberMe.isChecked();

                if (rememberMe) {
                    editor.putString("username", username);
                    editor.putString("password", password);
                    editor.putBoolean("rememberMe", true);
                    editor.apply();
                } else {
                    editor.clear();
                    editor.apply();
                }

                Cursor cursor = database.query(
                        DatabaseHelper.TABLE_NAME,
                        null,
                        DatabaseHelper.COLUMN_USERNAME + " = ? AND " + DatabaseHelper.COLUMN_PASSWORD + " = ?",
                        new String[]{username, password},
                        null,
                        null,
                        null
                );

                if (cursor.getCount() > 0) {
                    if (rememberMe) {
                        editor.putString("username", username);
                        editor.putString("password", password);
                        editor.putBoolean("rememberMe", true);
                        editor.apply();
                    } else {
                        editor.clear();
                        editor.apply();
                    }
                    Toast.makeText(MainActivity.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }

        });

        Button buttonConsult = findViewById(R.id.buttonConsult);
        buttonConsult.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                ContentValues values = new ContentValues();
                values.put("username", username);
                values.put("password", password);
                long newRowId = database.insert("user", null, values);

                Toast.makeText(MainActivity.this, "Creado", Toast.LENGTH_SHORT).show();


            }
        });

    }

}