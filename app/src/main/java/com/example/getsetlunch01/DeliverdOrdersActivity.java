package com.example.getsetlunch01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.getsetlunch01.Admin.AdminNewOrdersActivity;
import com.example.getsetlunch01.Model.DeliverdOrders;
import com.example.getsetlunch01.Prevalent.Prevalent;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeliverdOrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private String childDateTime,order_at;
    DatabaseReference shippedorderRef;
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliverd_orders);

        recyclerView=findViewById(R.id.rl7);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

          shippedorderRef= FirebaseDatabase.getInstance().getReference().child("Shipped Orders")
                .child(Prevalent.currentOnlineUser.getPhone());


        sp=getSharedPreferences("f1",MODE_PRIVATE);
            order_at =sp.getString("order_at","");


      //  getReferenceId();
        getShippedOrders(order_at);
        //Toast.makeText(this, ""+order_at, Toast.LENGTH_SHORT).show();

    }


  /*  public  void getReferenceId()
    {

        shippedorderRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    //childDateTime=dataSnapshot.getRef().getKey();
                    childDateTime=dataSnapshot.getKey().toString();
                    String datetime=dataSnapshot.child(childDateTime).child("orded_at").getValue().toString();
                    Toast.makeText(DeliverdOrdersActivity.this, ""+childDateTime, Toast.LENGTH_SHORT).show();


                    getShippedOrders(datetime);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });


    }*/

    private void getShippedOrders(String order_at)
    {



        DatabaseReference FinalShippedRef=FirebaseDatabase.getInstance().getReference().child("Shipped Orders")
                .child(Prevalent.currentOnlineUser.getPhone())
        ;

        FirebaseRecyclerOptions <DeliverdOrders>options=
                new FirebaseRecyclerOptions.Builder<DeliverdOrders>()
                .setQuery(FinalShippedRef,DeliverdOrders.class)
                .build();


        FirebaseRecyclerAdapter<DeliverdOrders,DeliverdOrdersViewHolder> adapter
                =new FirebaseRecyclerAdapter<DeliverdOrders, DeliverdOrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DeliverdOrdersViewHolder holder, int i, @NonNull DeliverdOrders model)
            {
                holder.txtProductName.setText("Product Name :"+model.getPname());
                holder.txtProductOrderate.setText("Order At :"+model.getOrder_at());
            }

            @NonNull
            @Override
            public DeliverdOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.deliverd_item_list,parent,false);

                return  new DeliverdOrdersViewHolder(view);

            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    public static class DeliverdOrdersViewHolder extends RecyclerView.ViewHolder
    {
        TextView txtProductName,txtProductOrderate;

        public DeliverdOrdersViewHolder(@NonNull View itemView)
        {
            super(itemView);

            txtProductName=itemView.findViewById(R.id.txtpname);
            txtProductOrderate=itemView.findViewById(R.id.txtOrderdate);

        }
    }
}
