package edu.uga.cs.shoppinglistapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * This is an adapter class for the RecyclerView to show all job leads.
 */
public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.ItemHolder> {

    public static final String DEBUG_TAG = "ItemRecyclerAdapter";

    public interface checkItemListener {
        void onItemCheck(Item item);
        void onItemUncheck(Item item);
    }

    private List<Item> itemList;
    private Context context;
    private checkItemListener checkListener;



    public ItemRecyclerAdapter(List<Item> List, Context context ) {
        this.itemList = List;
        this.context = context;
    }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    class ItemHolder extends RecyclerView.ViewHolder {

        TextView itemName ;
        TextView itemAmount;
        TextView itemPrice;
        CheckBox cbselect;

        public ItemHolder(View itemView ) {
            super(itemView);

            itemName = itemView.findViewById( R.id.itemName );
            itemAmount = itemView.findViewById( R.id.itemAmount);
            itemPrice = itemView.findViewById( R.id.itemPrice );
            cbselect = itemView.findViewById(R.id.checkBox3);
        }
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.item_list, parent, false );
        return new ItemHolder( view );
    }

    // This method fills in the values of the Views to show a JobLead
    @Override
    public void onBindViewHolder( ItemHolder holder, int position ) {
        Item item = itemList.get( position );

        Log.d( DEBUG_TAG, "onBindViewHolder: " + item );

        String key = item.getKey();
        String itemName = item.getName();
        int itemAmount = item.getAmount();
        double itemPrice = item.getPrice();


        holder.itemName.setText( item.getName());
        holder.itemAmount.setText( Integer.toString(item.getAmount()));
        holder.itemPrice.setText( Double.toString(item.getPrice()) );


        // Checkbox listening, If item is checked adds it to list.
        // If item is unchecked, removed from list
        holder.cbselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = holder.cbselect.isChecked();

                if(checked) {
                    // Adds item to item list in ReviewItemsActivity
                    checkListener.onItemCheck(item);
                } else {
                    // Removes item to item list in ReviewItemsActivity
                    checkListener.onItemUncheck(item);
                }

            }

        });

        // We can attach an OnClickListener to the itemView of the holder;
        // itemView is a public field in the Holder class.
        // It will be called when the user taps/clicks on the whole item, i.e., one of
        // the job leads shown.
        // This will indicate that the user wishes to edit (modify or delete) this item.
        // We create and show an EditJobLeadDialogFragment.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d( TAG, "onBindViewHolder: getItemId: " + holder.getItemId() );
                //Log.d( TAG, "onBindViewHolder: getAdapterPosition: " + holder.getAdapterPosition() );
                EditItemDialogFragment editItemFragment =
                        EditItemDialogFragment.newInstance( holder.getAdapterPosition(), key, itemName, itemAmount, itemPrice);
                editItemFragment.show( ((AppCompatActivity)context).getSupportFragmentManager(), null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
