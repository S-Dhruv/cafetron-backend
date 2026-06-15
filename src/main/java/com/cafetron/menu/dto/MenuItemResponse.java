package com.cafetron.menu.dto;

//Shape of data the API SENDS BACK to the frontend.
//Includes the generated id, and flattens the vendor into id + name(so we never expose the whole Vendor object).

public record MenuItemResponse(
        Long id,
        String itemName,
        double price,
        int stock,
        String foodType,
        boolean isAvailable,
        Long vendorId,
        String vendorName
) {}
