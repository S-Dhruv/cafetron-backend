package com.cafetron.menu.controller;

import com.cafetron.menu.dto.MenuItemRequest;
import com.cafetron.menu.dto.MenuItemResponse;
import com.cafetron.menu.service.MenuItemService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuItemController {
    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    // POST /menu -> create a new item.
    @PostMapping
    public MenuItemResponse create(@Valid @RequestBody MenuItemRequest request) {
        return menuItemService.create(request);
    }

    // GET /menu -> all items (staff/admin full inventory).
    @GetMapping
    public List<MenuItemResponse> getAll() {
        return menuItemService.getAll();
    }

    // GET /menu/{id} -> one item.
    @GetMapping("/today")
    public List<MenuItemResponse> getTodaysMenu() {

        try {
            return menuItemService.getTodaysMenu();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // PUT /menu/{id} -> replace/update an existing item.
    @PutMapping("/{id}")
    public MenuItemResponse update(@PathVariable Long id,
                                   @Valid @RequestBody MenuItemRequest request) {
        return menuItemService.update(id, request);
    }

    // DELETE /menu/{id} -> remove an item.
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        menuItemService.delete(id);
    }

    // --- Employee-facing reads ---

    // GET /menu/today -> available items from active vendors.
    @GetMapping("/search")
    public List<MenuItemResponse> search(@RequestParam String name) {
        try {
            return menuItemService.search(name);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // GET /menu/search?name=dosa -> search today's menu by name.
    @GetMapping("/filter")
    public List<MenuItemResponse> filterByFoodType(@RequestParam String type) {
        try {
            return menuItemService.filterByFoodType(type);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // GET /menu/filter?type=VEG -> filter today's menu by dietary type.
    @GetMapping("/{id}")
    public MenuItemResponse getById(@PathVariable Long id) {
        try {
            return menuItemService.getById(id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // --- Stock operations ---

    // PATCH /menu/{id}/stock?stock=30 -> set the stock level.
    // PATCH = update just part of an item (here, only the stock).
    @PatchMapping("/{id}/stock")
    public MenuItemResponse setStock(@PathVariable Long id, @RequestParam int stock) {
        return menuItemService.setStock(id, stock);
    }

    // PATCH /menu/{id}/availability?available=false -> manually show/hide.
    @PatchMapping("/{id}/availability")
    public MenuItemResponse setAvailability(@PathVariable Long id,
                                            @RequestParam boolean available) {
        return menuItemService.setAvailability(id, available);
    }
}

