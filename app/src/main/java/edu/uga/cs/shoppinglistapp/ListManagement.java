package edu.uga.cs.shoppinglistapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ListManagement extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_management);

        Button addItem = findViewById( R.id.button4);

        addItem.setOnClickListener(new addItemButtonClickListener());
    }

    private class addItemButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // start the user registration activity
            Intent intent = new Intent(view.getContext(), AddItem.class);
            view.getContext().startActivity(intent);
        }
    }

}