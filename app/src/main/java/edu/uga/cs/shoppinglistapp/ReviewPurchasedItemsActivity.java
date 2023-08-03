package edu.uga.cs.shoppinglistapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * This is an activity class for listing the current job leads.
 * The current job leads are listed as a RecyclerView.
 */
public class ReviewPurchasedItemsActivity
        extends AppCompatActivity
implements EditPurchaseItemDialogFragment.EditItemDialogListener {

    public static final String DEBUG_TAG = "ReviewPurchasedActivity";

    private RecyclerView recyclerView1;
    private RecyclerView recyclerView2;
    private PurchaseRecyclerAdapter recyclerAdapter;
    private double totalPrice = 0;
    private TextView price;
    private Button settle;


    private String buyer;
    private List<Purchase> purchaseList;
    private double numChildren;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(DEBUG_TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_purchased_items);

        recyclerView1 = findViewById(R.id.recyclerView);
        price = findViewById(R.id.totalPrice);
        settle = findViewById(R.id.settle);

        database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("users");
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                numChildren = snapshot.getChildrenCount();
                Log.d(DEBUG_TAG, "numChildren: " + numChildren);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("ValueEventListener: reading failed: " + databaseError.getMessage());
            }
        });


        // initialize the item list
        purchaseList = new ArrayList<Purchase>();

        // use a linear layout manager for the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView1.setLayoutManager(layoutManager);

        // the recycler adapter with job leads is empty at first; it will be updated later
        recyclerAdapter = new PurchaseRecyclerAdapter(purchaseList, ReviewPurchasedItemsActivity.this);
        recyclerView1.setAdapter(recyclerAdapter);


        // get a Firebase DB instance reference
        DatabaseReference myRef = database.getReference("purchasedlist");


        // Set up a listener (event handler) to receive a value for the database reference.
        // This type of listener is called by Firebase once by immediately executing its onDataChange method
        // and then each time the value at Firebase changes.
        //
        // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
        // to maintain job leads.
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Once we have a DataSnapshot object, we need to iterate over the elements and place them on our job lead list.
                purchaseList.clear(); // clear the current content; this is inefficient!
                totalPrice = 0;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Purchase purchase = postSnapshot.getValue(Purchase.class);
                    purchase.setKey(postSnapshot.getKey());
                    purchaseList.add(purchase);
                    Log.d(DEBUG_TAG, "ValueEventListener: added: " + purchase);
                    Log.d(DEBUG_TAG, "ValueEventListener: key: " + postSnapshot.getKey());
                }
                for (Purchase x : purchaseList) {
                   // totalPrice += x.getTotal();
                    totalPrice = 0;
                    for (Item i: x.getItems()) {
                        totalPrice += i.getPrice();
                    }
                }

                Log.d(DEBUG_TAG, "totalPrice: " + totalPrice);
                totalPrice = totalPrice / numChildren;
                totalPrice = Math.round(totalPrice * 100.0) / 100.0;
                Log.d(DEBUG_TAG, "totalPrice final: " + totalPrice);
                price.setText(Double.toString(totalPrice));


                Log.d(DEBUG_TAG, "ValueEventListener: notifying recyclerAdapter");
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("ValueEventListener: reading failed: " + databaseError.getMessage());
            }
        });


        settle.setOnClickListener(new settleButtonClickListener());

    }

    private class settleButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            int positionInt = 0;

            for (Purchase i : purchaseList) {
                // Remove item to cart
                //purchaseList.remove(i);
                recyclerAdapter.notifyItemRemoved(positionInt);

                DatabaseReference ref = database
                        .getReference()
                        .child("purchasedlist")
                        .child(i.getKey());
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(DEBUG_TAG, "deleted purchase at: " + "(" + i.getKey() + ")");
                                //Toast.makeText(getApplicationContext(), "Item deleted for " + i.getName(),Toast.LENGTH_SHORT).show();
                                price.setText(Double.toString(0.0));
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(DEBUG_TAG, "failed to delete item at: (" + i.getKey() + ")");
                        //Toast.makeText(getApplicationContext(), "Failed to delete " + i.getName(), Toast.LENGTH_SHORT).show();
                    }
                });

                positionInt++;
            }
                purchaseList.clear();
            }
        }

        // This is our own callback for a DialogFragment which edits an existing item.
        // The edit may be an update or a deletion of this item.
        // It is called from the EditItemDialogFragment.
        public void updateItem(int position, Item item, int action, String keyPurchase, int positionPurchase) {
            if (action == EditItemDialogFragment.SAVE) {
                Log.d(DEBUG_TAG, "Updating item at: " + position + "(" + item.getName() + ")");

                // Update the recycler view to show the changes in the updated job lead in that view
                recyclerAdapter.notifyItemChanged(positionPurchase);

                // Update this item in Firebase
                // Note that we are using a specific key (one child in the list)
                DatabaseReference ref = database
                        .getReference()
                        .child("purchasedlist")
                        .child(keyPurchase).child("items")
                        .child(Integer.toString(position));

                // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
                // to maintain job leads.
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(DEBUG_TAG, "updated item at: " + position + "(" + item.getName() + ")");
                                Toast.makeText(getApplicationContext(), "Item updated for " + item.getName(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(DEBUG_TAG, "failed to update item at: " + position + "(" + item.getName() + ")");
                        Toast.makeText(getApplicationContext(), "Failed to update " + item.getName(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (action == EditItemDialogFragment.DELETE) {
                Log.d(DEBUG_TAG, "Deleting item at: " + position + "(" + item.getName() + ") and " +
                        "returning it back to itemsneededlist");

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef1 = database.getReference("itemsneededlist");


                // Add item to items needed list
                myRef1.push().setValue(item)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Show a quick confirmation
                                Toast.makeText(getApplicationContext(), "Item created for " + item.getName(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Failed to create a item for " + item.getName(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });


                // remove the deleted purchase from the list (internal list in the App)
                //purchaseList.remove( position );

                // Update the recycler view to remove the deleted job lead from that view
                recyclerAdapter.notifyItemChanged(positionPurchase);

                // Delete this job lead in Firebase.
                // Note that we are using a specific key (one child in the list)
                DatabaseReference ref = database
                        .getReference()
                        .child("purchasedlist")
                        .child(keyPurchase).child("items")
                        .child(Integer.toString(position));

                // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
                // to maintain job leads.
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(DEBUG_TAG, "deleted item at: " + position + "(" + item.getName() + ")");
                                Toast.makeText(getApplicationContext(), "Item deleted for " + item.getName(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(DEBUG_TAG, "failed to delete item at: " + position + "(" + item.getName() + ")");
                        Toast.makeText(getApplicationContext(), "Failed to delete " + item.getName(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }


        }

}



