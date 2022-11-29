package edu.uga.cs.shoppinglistapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class EditItemDialogFragment extends DialogFragment {

    // indicate the type of an edit
    public static final int SAVE = 1;   // update an existing item
    public static final int DELETE = 2; // delete an existing item
    public static final int MOVE = 3;   // move an existing item to cart

    private EditText itemNameView;
    private EditText itemAmountView;
    private EditText itemPriceView;

    int position;
    String key;
    String itemName;
    int amount;
    double price;

    // A callback listener interface to finish up the editing of a Item.
    // ReviewShoppingListActivity implements this listener interface, as it will
    // need to update the list of Items and also update the RecyclerAdapter to reflect the
    // changes.
    public interface EditItemDialogListener {
        void updateItem(int position, Item item, int action);
    }


    public static EditItemDialogFragment newInstance(int position, String key, String itemName, int amount, double price) {
        EditItemDialogFragment dialog = new EditItemDialogFragment();

        // Supply job lead values as an argument.
        Bundle args = new Bundle();
        args.putString( "key", key );
        args.putInt( "position", position );
        args.putString("name", itemName);
        args.putInt("amount", amount);
        args.putDouble("price", price);
        dialog.setArguments(args);

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {
        key = getArguments().getString("key");
        position = getArguments().getInt("position");
        itemName = getArguments().getString("name");
        amount = getArguments().getInt("amount");
        price = getArguments().getDouble("price");

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate( R.layout.item_dialogue, getActivity().findViewById( R.id.root ) );

        itemNameView =  layout.findViewById(R.id.editText1);
        itemAmountView = layout.findViewById(R.id.editText2);
        itemPriceView = layout.findViewById(R.id.editText3);

        // Pre-fill the edit texts with the current values for this Item.
        // The user will be able to modify them.
        itemNameView.setText(itemName);
        itemAmountView.setText(amount);
        itemPriceView.setText(Double.toString(price));

        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity());
        builder.setView(layout);

        // Set the title of the AlertDialog
        builder.setTitle( "Item" );

        // The Cancel button handler
        builder.setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // close the dialog
                dialog.dismiss();
            }
        });

        // The Save button handler
        builder.setPositiveButton( "SAVE", new SaveButtonClickListener() );

        // The Delete button handler
        builder.setNeutralButton( "DELETE", new DeleteButtonClickListener() );

        // Create the AlertDialog and show it
        return builder.create();

    }

    private class SaveButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String itemName = getArguments().getString("name");
            int amount = getArguments().getInt("amount");
            double price = getArguments().getDouble("price");
            Item item = new Item(itemName, amount, price);
            item.setKey(key);

            // get the Activity's listener to add the new job lead
            EditItemDialogListener listener = (EditItemDialogFragment.EditItemDialogListener) getActivity();
            // add the new job lead
            listener.updateItem( position, item, SAVE );

            // close the dialog
            dismiss();
        }
    }

    private class DeleteButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick( DialogInterface dialog, int which ) {

            Item item = new Item(itemName, amount, price);
            item.setKey( key );

            // get the Activity's listener to add the new job lead
            EditItemDialogFragment.EditItemDialogListener listener = (EditItemDialogFragment.EditItemDialogListener) getActivity();            // add the new job lead
            listener.updateItem( position, item, DELETE );
            // close the dialog
            dismiss();
        }

}