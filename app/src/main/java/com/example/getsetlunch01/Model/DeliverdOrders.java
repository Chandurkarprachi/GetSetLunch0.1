package com.example.getsetlunch01.Model;

public class DeliverdOrders
{
    private  String pname,order_at,status;

    public DeliverdOrders(String pname, String order_at, String status) {
        this.pname = pname;
        this.order_at = order_at;
        this.status = status;
    }

    public DeliverdOrders()
    {

    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getOrder_at() {
        return order_at;
    }

    public void setOrder_at(String order_at) {
        this.order_at = order_at;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
