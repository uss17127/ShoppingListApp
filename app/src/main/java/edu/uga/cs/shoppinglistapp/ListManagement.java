package edu.uga.cs.shoppinglistapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// This is the main menu of the app.
// Here users can choose to logout, add item to list, view list,view cart list, or view purchased list.
public class ListManagement extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_management);


        Button addItem = findViewById( R.id.button4);
        Button viewList = findViewById(R.id.button5);
        Button cartList = findViewById(R.id.button6);
        Button purchasedList = findViewById(R.id.button7);
        Button signOut = findViewById(R.id.signOut);
        // set email
        TextView email = findViewById(R.id.email);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String splitEmail[] = user.getEmail().split("@");
        email.setText(splitEmail[0]);

        addItem.setOnClickListener(new addItemButtonClickListener());
        viewList.setOnClickListener(new viewListButtonClickListener());
        cartList.setOnClickListener(new viewCartClickListener() );
        purchasedList.setOnClickListener(new viewPurchasedClickListener());
        signOut.setOnClickListener(new signOutButtonClickListener());
    }

    private class signOutButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // Go back to main screen
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            view.getContext().startActivity(intent);
        }
    }

    private class addItemButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // start the user registration activity
            Intent intent = new Intent(view.getContext(), AddItem.class);
            view.getContext().startActivity(intent);
        }
    }

    private class viewListButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // start the user registration activity
            Intent intent = new Intent(view.getContext(), ReviewItemsActivity.class);
            view.getContext().startActivity(intent);
        }
    }

    private class viewCartClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // start the user registration activity
            Intent intent = new Intent(view.getContext(), ReviewCartActivity.class);
            view.getContext().startActivity(intent);
        }
    }

    private class viewPurchasedClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // start the user registration activity
            Intent intent = new Intent(view.getContext(), ReviewPurchasedItemsActivity.class);
            view.getContext().startActivity(intent);
        }
    }

}