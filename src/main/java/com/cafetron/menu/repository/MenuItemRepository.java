package com.cafetron.menu.repository;

import com.cafetron.menu.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

//Inherits basic CRUD from JpaRepository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByVendorId(Long vendorId); // All items for one vendor

    @Query("SELECT m FROM MenuItem m WHERE m.isAvailable = true AND m.vendor.isActive = true")
    List<MenuItem> findTodaysMenu(); // Today's menu: available items from active vendors.

    // Today's menu, matching a name search.
    // CONCAT adds % wildcards; LOWER makes it case-insensitive.
    @Query("SELECT m FROM MenuItem m WHERE m.isAvailable = true AND m.vendor.isActive = true " +
            "AND LOWER(m.itemName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<MenuItem> searchTodaysMenu(@Param("name") String name);

    // Today's menu filtered by dietary type.
    @Query("SELECT m FROM MenuItem m WHERE m.isAvailable = true AND m.vendor.isActive = true " +
            "AND m.foodType = :foodType")
    List<MenuItem> filterByFoodType(@Param("foodType") String foodType);
}
