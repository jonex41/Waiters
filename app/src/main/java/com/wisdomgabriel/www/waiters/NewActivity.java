package com.wisdomgabriel.www.waiters;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class NewActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<ShopModel, ShopViewholder> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groupfragment);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(NewActivity.this));
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


    public void setUpAdapter(){

        Query query = FirebaseFirestore.getInstance().collection("orders");


        FirestoreRecyclerOptions<ShopModel> options = new FirestoreRecyclerOptions.Builder<ShopModel>().setQuery(query, ShopModel.class).build();

            mAdapter = new FirestoreRecyclerAdapter<ShopModel, ShopViewholder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull ShopViewholder holder, int position, @NonNull ShopModel model) {

                    holder.setFoodName(model.getFoodName());
                    holder.setFoodPrice("#"+model.getFoodPrice());
                    Glide.with(getApplicationContext())
                            .load(model.getImageUrl())

                            .into(holder.foodIamge);
                }

                @NonNull
                @Override
                public ShopViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    return new ShopViewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.food_card, parent, false));

                }
            };




    }

     class ShopViewholder extends RecyclerView.ViewHolder{


         public View mView;
         public ImageView foodIamge;
         public TextView title, foodprice;
         public LinearLayout foodclick;



         public ShopViewholder(View itemView) {
             super(itemView);
             mView = itemView;

             title = (TextView) mView.findViewById(R.id.foodname);
             foodprice = (TextView) mView.findViewById(R.id.foodPrice);
             foodIamge = (ImageView) mView.findViewById(R.id.foodImage);

             foodclick = (LinearLayout) mView.findViewById(R.id.click_shop);


         }


         public void setFoodName(String foodName) {
                    title.setText(foodName);
         }

         public void setFoodPrice(String s) {
                foodprice.setText(s);
         }
     }

}