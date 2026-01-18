package org.tcskart.order.dto;

import lombok.Data;

import java.util.List;


@Data
public class Statistics {

    int totalOrders;
    int totalSales ;
    List<TopProduct> topProducts;
    int avrageUsers;
    double userEngagement;



}
