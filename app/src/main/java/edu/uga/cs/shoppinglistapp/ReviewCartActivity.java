package edu.uga.cs.shoppinglistapp;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
public class ReviewCartActivity
        extends AppCompatActivity
        implements EditItemDialogFragment.EditItemDialogListener, ItemRecyclerAdapter.checkItemListener  {

    public static final String DEBUG_TAG = "ReviewCartActivity";

    private RecyclerView recyclerView;
    private ItemRecyclerAdapter recyclerAdapter;
    private Button returnToList;

    private List<Item> itemsList;

    private FirebaseDatabase database;

    //Item list for checked items
    private List<Item> checkedItem = new ArrayList<>();
    private  List<Integer> positionsList = new ArrayList<>();

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        Log.d( DEBUG_TAG, "onCreate()" );

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_review_cart );

        recyclerView = findViewById( R.id.recyclerView );
        returnToList = findViewById(R.id.returnToList);


        // initialize the item list
        itemsList = new ArrayList<Item>();

        // use a linear layout manager for the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // the recycler adapter with items is empty at first; it will be updated later
        recyclerAdapter = new ItemRecyclerAdapter( itemsList, ReviewCartActivity.this);
        recyclerView.setAdapter( recyclerAdapter );

        //Listener for moving items to purchased list
        returnToList.setOnClickListener(new returnToListButtonListener());

        // get a Firebase DB instance reference
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("cartlist");

        // Set up a listener (event handler) to receive a value for the database reference.
        // This type of listener is called by Firebase once by immediately executing its onDataChange method
        // and then each time the value at Firebase changes.
        //
        // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
        // to maintain job leads.
        myRef.addValueEventListener( new ValueEventListener() {

            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                // Once we have a DataSnapshot object, we need to iterate over the elements and place them on our job lead list.
                itemsList.clear(); // clear the current content; this is inefficient!
                for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                    Item item = postSnapshot.getValue(Item.class);
                    item.setKey( postSnapshot.getKey() );
                    itemsList.add( item );
                    Log.d( DEBUG_TAG, "ValueEventListener: added: " + item );
                    Log.d( DEBUG_TAG, "ValueEventListener: key: " + postSnapshot.getKey() );
                }

                Log.d( DEBUG_TAG, "ValueEventListener: notifying recyclerAdapter" );
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled( @NonNull DatabaseError databaseError ) {
                System.out.println( "ValueEventListener: reading failed: " + databaseError.getMessage() );
            }
        } );
    }


    // This is our own callback for a DialogFragment which edits an existing item.
    // The edit may be an update or a deletion of this item.
    // It is called from the EditItemDialogFragment.
    public void updateItem( int position, Item item, int action ) {
        if( action == EditItemDialogFragment.SAVE ) {
            Log.d( DEBUG_TAG, "Updating item at: " + position + "(" + item.getName() + ")" );

            // Update the recycler view to show the changes in the updated job lead in that view
            recyclerAdapter.notifyItemChanged( position );

            // Update this item in Firebase
            // Note that we are using a specific key (one child in the list)
            DatabaseReference ref = database
                    .getReference()
                    .child( "cartlist" )
                    .child( item.getKey() );

            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
            // to maintain job leads.
            ref.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                    dataSnapshot.getRef().setValue( item ).addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d( DEBUG_TAG, "updated item at: " + position + "(" + item.getName() + ")" );
                            Toast.makeText(getApplicationContext(), "Item updated for " + item.getName(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled( @NonNull DatabaseError databaseError ) {
                    Log.d( DEBUG_TAG, "failed to update item at: " + position + "(" + item.getName() + ")" );
                    Toast.makeText(getApplicationContext(), "Failed to update " + item.getName(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if( action == EditItemDialogFragment.DELETE ) {
            Log.d( DEBUG_TAG, "Deleting item at: " + position + "(" + item.getName() + ")" );

            // remove the deleted job lead from the list (internal list in the App)
            itemsList.remove( position );

            // Update the recycler view to remove the deleted job lead from that view
            recyclerAdapter.notifyItemRemoved( position );

            // Delete this job lead in Firebase.
            // Note that we are using a specific key (one child in the list)
            DatabaseReference ref = database
                    .getReference()
                    .child( "cartlist" )
                    .child( item.getKey() );

            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
            // to maintain job leads.
            ref.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                    dataSnapshot.getRef().removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d( DEBUG_TAG, "deleted item at: " + position + "(" + item.getName() + ")" );
                            Toast.makeText(getApplicationContext(), "Item deleted for " + item.getName(),
                                    Toast.LENGTH_SHORT).show();                        }
                    });
                }

                @Override
                public void onCancelled( @NonNull DatabaseError databaseError ) {
                    Log.d( DEBUG_TAG, "failed to delete item at: " + position + "(" + item.getName() + ")" );
                    Toast.makeText(getApplicationContext(), "Failed to delete " + item.getName(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private class returnToListButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef1 = database.getReference("itemsneededlist");
            int positionInt = 0;
            for (Item i : checkedItem) {

                // Add item to cart
                myRef1.push().setValue( i )
                        .addOnSuccessListener( new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Show a quick confirmation
                                Toast.makeText(getApplicationContext(), "Item created for " + i.getName(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener( new OnFailureListener() {
                            @Override
                            public void onFailure( @NonNull Exception e ) {
                                Toast.makeText( getApplicationContext(), "Failed to create a item for " + i.getName(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                // Remove item to cart
                itemsList.remove(i);
                recyclerAdapter.notifyItemRemoved(positionsList.get(positionInt));

                DatabaseReference ref = database
                        .getReference()
                        .child( "cartlist" )
                        .child( i.getKey() );
                ref.addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                        dataSnapshot.getRef().removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d( DEBUG_TAG, "deleted item at: " + "(" + i.getName() + ")" );
                                //Toast.makeText(getApplicationContext(), "Item deleted for " + i.getName(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled( @NonNull DatabaseError databaseError ) {
                        Log.d( DEBUG_TAG, "failed to delete item at: (" + i.getName() + ")" );
                        //Toast.makeText(getApplicationContext(), "Failed to delete " + i.getName(), Toast.LENGTH_SHORT).show();
                    }
                });

                positionInt++;
            }
            checkedItem.clear();

        }
    }

    public void onItemCheck(Item item, int position) {
        checkedItem.add(item);
        positionsList.add(position);
    }
    public void onItemUncheck(Item item, int position) {
        checkedItem.remove(item);
        positionsList.remove(new Integer(position));
    }
}
