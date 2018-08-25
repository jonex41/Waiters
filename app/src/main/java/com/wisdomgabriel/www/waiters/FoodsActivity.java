package com.wisdomgabriel.www.waiters;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

public class FoodsActivity extends AppCompatActivity{


    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<ShopModel, ShopViewHolder> mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groupstuffs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Foods");
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                intent.putExtra("string", "foods");
                startActivity(intent);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        Query query = FirebaseFirestore.getInstance().collection("foods");


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
                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                        String id2 = snapshot.getId();

                        FirebaseFirestore.getInstance().collection("orders").document(id2).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(FoodsActivity.this, "Pdf has been deleted", Toast.LENGTH_SHORT).show();
                                }else {

                                    Toast.makeText(FoodsActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

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
            foodclick = mView.findViewById(R.id.click_shop);





        }


        public void setFoodName(String foodName) {
            title.setText(foodName);
        }

        public void setFoodPrice(String foodPrice) {
            costs_of_product.setText("price : "+foodPrice);
        }


    }
}
