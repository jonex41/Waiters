package com.wisdomgabriel.www.waiters;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class OrdersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<ShopModel, ShopViewHolder> mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orders_activity);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        }


        setUpAdapter();
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }



    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }


    private void setUpAdapter() {

        Query query = FirebaseFirestore.getInstance().collection("orders");


        FirestoreRecyclerOptions<ShopModel> options = new FirestoreRecyclerOptions.Builder<ShopModel>().setQuery(query, ShopModel.class).build();


        mAdapter = new FirestoreRecyclerAdapter<ShopModel, ShopViewHolder>(options) {
            @Override
            protected void onBindViewHolder(final ShopViewHolder holder, int position, final ShopModel model) {

                holder.setFoodName(model.getFoodName());

                holder.setFoodPrice("#"+model.getFoodPrice());

                Glide.with(getApplicationContext()).load(model.getImageUrl()).into(holder.foodIamge);


                holder.foodclick.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        final AlertDialog.Builder builder1 = new AlertDialog.Builder(new ContextThemeWrapper(OrdersActivity.this, R.style.myDialog));
                        builder1.setMessage("Are you sure you want to delete this food...");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "Delete",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                                        String id2 = snapshot.getId();

                                        FirebaseFirestore.getInstance().collection("orders").document(id2).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(OrdersActivity.this, "Pdf has been deleted", Toast.LENGTH_SHORT).show();
                                                }else {

                                                    Toast.makeText(OrdersActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                                    }
                                });

                        builder1.setNegativeButton(
                                "Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();


                        return false;
                    }
                });



            }

            @NonNull
            @Override
            public ShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ShopViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layoutsingle, parent, false));

            }
        };



    }



    class ShopViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        public ImageView foodIamge;
        public TextView title, costs_of_product, sit, platenumber;
        public LinearLayout foodclick;



        public ShopViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            title = (TextView) mView.findViewById(R.id.foodname);
            costs_of_product = (TextView) mView.findViewById(R.id.foodPrice);
            foodIamge = (ImageView) mView.findViewById(R.id.foodImage);





        }


        public void setFoodName(String foodName) {
            title.setText(foodName);
        }

        public void setFoodPrice(String foodPrice) {
            costs_of_product.setText("Price : "+ foodPrice);
        }


    }
}
