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

public class EditPurchaseItemDialogFragment extends DialogFragment {

    // indicate the type of an edit
    public static final int SAVE = 1;   // update an existing item
    public static final int DELETE = 2; // delete an existing item
    public static final int MOVE = 3;   // move an existing item to cart

    private EditText itemNameView;
    private EditText itemAmountView;
    private EditText itemPriceView;

    int position;
    int positionPurchase;
    String key;
    String keyPurchase;
    String itemName;
    int amount;
    double price;

    // A callback listener interface to finish up the editing of a Item.
    // ReviewShoppingListActivity implements this listener interface, as it will
    // need to update the list of Items and also update the RecyclerAdapter to reflect the
    // changes.
    public interface EditItemDialogListener {
        void updateItem(int position, Item item, int action, String keyPurchase,  int positionPurchase);
    }


    public static EditPurchaseItemDialogFragment newInstance(int position, String key, String itemName, int amount, double price, String keyPurchase,  int positionPurchase) {
        EditPurchaseItemDialogFragment dialog = new EditPurchaseItemDialogFragment();

        // Supply job lead values as an argument.
        Bundle args = new Bundle();
        args.putString("key", key);
        args.putString("keyPurchase", keyPurchase);
        args.putInt("position", position);
        args.putInt("positionPurchase", positionPurchase);
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
        keyPurchase = getArguments().getString("keyPurchase");
        position = getArguments().getInt("position");
        positionPurchase = getArguments().getInt("positionPurchase");
        itemName = getArguments().getString("name");
        amount = getArguments().getInt("amount");
        price = getArguments().getDouble("price");

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.item_dialogue, getActivity().findViewById(R.id.root));

        itemNameView = layout.findViewById(R.id.editText1);
        itemAmountView = layout.findViewById(R.id.editText2);
        itemPriceView = layout.findViewById(R.id.editText3);

        // Pre-fill the edit texts with the current values for this Item.
        // The user will be able to modify them.
        itemNameView.setText(itemName);
        itemAmountView.setText(Integer.toString(amount));
        itemPriceView.setText(Double.toString(price));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(layout);

        // Set the title of the AlertDialog
        builder.setTitle("Item");

        // The Cancel button handler
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // close the dialog
                dialog.dismiss();
            }
        });

        // The Save button handler
        builder.setPositiveButton("SAVE", new SaveButtonClickListener());

        // The Delete button handler
        builder.setNeutralButton("DELETE", new DeleteButtonClickListener());

        // Create the AlertDialog and show it
        return builder.create();

    }

    private class SaveButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String itemName1 =  itemNameView.getText().toString();
            int amount1 = Integer.parseInt(itemAmountView.getText().toString());
            double price1 = Double.parseDouble(itemPriceView.getText().toString());
            Item item = new Item(itemName1, amount1, price1);
            item.setKey(key);

            // get the Activity's listener to add the new item
            EditItemDialogListener listener = (EditPurchaseItemDialogFragment.EditItemDialogListener) getActivity();
            // add the new item
            listener.updateItem(position, item, SAVE, keyPurchase, positionPurchase);

            // close the dialog
            dismiss();
        }
    }

    private class DeleteButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            Item item = new Item(itemName, amount, price);
            item.setKey(key);

            // get the Activity's listener to add the new job lead
            EditPurchaseItemDialogFragment.EditItemDialogListener listener = (EditPurchaseItemDialogFragment.EditItemDialogListener) getActivity();            // add the new job lead
            listener.updateItem(position, item, DELETE, keyPurchase, positionPurchase);
            // close the dialog
            dismiss();
        }
    }
}