package com.example.getsetlunch01.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.getsetlunch01.Model.AdminOrders;
import com.example.getsetlunch01.Prevalent.Prevalent;
import com.example.getsetlunch01.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AdminNewOrdersActivity extends AppCompatActivity {
    private RecyclerView orderList;
    private DatabaseReference orderRef;
    private String name,phone,totalprice,shippingadress,status,city,pname,datetime;
    private String uId,pid;
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);


        orderRef= FirebaseDatabase.getInstance().getReference().child("Orders");

        orderList=findViewById(R.id.orders_list);
        orderList.setLayoutManager(new LinearLayoutManager(this));

        sp=getSharedPreferences("f1",  MODE_PRIVATE);
        String name=sp.getString("order_at","");



    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders> options=
        new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(orderRef,AdminOrders.class)
                .build();


        FirebaseRecyclerAdapter<AdminOrders,AdminOrdersViewHolder> adapter=
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, final int i, @NonNull final AdminOrders model) {

                         name=model.getName().toString();
                         phone=model.getPhone().toString();
                         totalprice=model.getTotalAmount().toString();
                         shippingadress=model.getAddress().toString();
                         city=model.getCity().toString();
                        pname=model.getPname().toString();
                        datetime=model.getDate()+" "+model.getTime();

                        holder.userName.setText("Name :"+model.getName());
                        holder.userPhoneNumber.setText("Phone No : :"+model.getPhone());
                        holder.userPname.setText("Pname :"+model.getPname());
                        holder.userTotalPrice.setText("Total Price :"+model.getTotalAmount());
                        holder.userDateTime.setText("Order At :"+model.getDate()+" "+model.getTime());
                        holder.userShippingAddress.setText("Address :"+model.getAddress()+" "+model.getCity());
                        holder.ShowOrdersBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                orderRef.addValueEventListener(new ValueEventListener()
                                {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        if (dataSnapshot.exists())
                                        {
                                             uId=getRef(i).getKey();

                                            Intent i= new Intent(AdminNewOrdersActivity.this, AdminUserProductsActivity.class);
                                            i.putExtra("uid",uId);
                                            startActivity(i);

                                        }
                                        else
                                        {
                                            Toast.makeText(AdminNewOrdersActivity.this, "No New Orders", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view) {

                                CharSequence options[]= new CharSequence[]
                                        {
                                                "Yes",
                                                "No"
                                        };
                                AlertDialog.Builder builder=new AlertDialog.Builder(AdminNewOrdersActivity.this);
                                builder.setTitle("Have you shipped this product ?");

                                builder.setItems(options, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {
                                        if(i==0)
                                        {
                                            String uID=getRef(i).getKey();

                                            RemoveOrder(uID);
                                            ShippedOrder();


                                        }else
                                        {

                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,parent,false);

                        return  new AdminOrdersViewHolder(view);

                    }
                };
        orderList.setAdapter(adapter);
        adapter.startListening();

    }


    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder
    {
        public TextView userName,userPhoneNumber,userTotalPrice,userDateTime,userShippingAddress,userPname;
        public Button ShowOrdersBtn ;

        public AdminOrdersViewHolder(@NonNull View itemView)
        {
            super(itemView);

            userName=itemView.findViewById(R.id.order_user_name);
           userPname=itemView.findViewById(R.id.order_pname);
            userPhoneNumber=itemView.findViewById(R.id.order_phone_number);
            userTotalPrice=itemView.findViewById(R.id.order_total_price);
            userShippingAddress=itemView.findViewById(R.id.order_address_city);
            userDateTime=itemView.findViewById(R.id.order_date_time);
            ShowOrdersBtn=itemView.findViewById(R.id.show_all_product_btn);

        }
    }

    private void RemoveOrder(String uID)
    {
       orderRef.child(uID).removeValue();
    }



    private void ShippedOrder()
    {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("order_at",datetime);
        editor.commit();

        DatabaseReference shippedOrderRef=FirebaseDatabase.getInstance().getReference().child("Shipped Orders")
                .child(Prevalent.currentOnlineUser.getPhone())
                .child(datetime)
                ;

        HashMap<String,Object> orderMap=new HashMap<>();
        orderMap.put("name",name);
        orderMap.put("pname",pname);
        orderMap.put("totalAmount",totalprice);
        orderMap.put("phone",phone);
        orderMap.put("address",shippingadress);
        orderMap.put("city",city);
        orderMap.put("status","Shipped");

        orderMap.put("order_at",datetime);

        shippedOrderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(AdminNewOrdersActivity.this, "Shipped", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

}

