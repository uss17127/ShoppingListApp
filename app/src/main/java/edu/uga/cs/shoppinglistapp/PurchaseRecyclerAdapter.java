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
public class PurchaseRecyclerAdapter extends RecyclerView.Adapter<PurchaseRecyclerAdapter.ItemHolder> {

    public static final String DEBUG_TAG = "ItemRecyclerAdapter";
    private List<Purchase> purchaseList;
    private Context context;





    public PurchaseRecyclerAdapter(List<Purchase> List, Context context) {
        this.purchaseList = List;
        this.context = context;
    }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    class ItemHolder extends RecyclerView.ViewHolder {

        TextView buyer;
        RecyclerView itemsBought;
        TextView totalPrice;

        public ItemHolder(View itemView ) {
            super(itemView);

            buyer = itemView.findViewById( R.id.nameOfItem );
            itemsBought = itemView.findViewById( R.id.itemsBought);
            totalPrice = itemView.findViewById( R.id.total );

        }
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.purchase_list, parent, false );
        return new ItemHolder( view );
    }

    // This method fills in the values of the Views to show a JobLead
    @Override
    public void onBindViewHolder( ItemHolder holder, int position ) {
        Purchase purchase =  purchaseList.get( position );


        Log.d( DEBUG_TAG, "onBindViewHolder: " + purchase );

        String key =  purchase.getKey();
        String buyerName =  purchase.getBuyer();
        List<Item> bought = purchase.getItems();
        double total = 0;
        for (Item i: bought) {
            total += i.getPrice();
        }

        PurchaseItemRecyclerAdapter purchaseItemRecyclerAdapter = new PurchaseItemRecyclerAdapter(bought, holder.itemView.getContext());
        holder.itemsBought.setAdapter(purchaseItemRecyclerAdapter);

        holder.buyer.setText(buyerName);
        holder.totalPrice.setText( Double.toString(total) );


    }

    @Override
    public int getItemCount() {
        return purchaseList.size();
    }
}
