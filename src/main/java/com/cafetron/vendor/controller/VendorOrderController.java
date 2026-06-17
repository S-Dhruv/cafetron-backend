package com.cafetron.vendor.controller;

import com.cafetron.security.UserPrincipal;
import com.cafetron.vendor.dto.VendorOrderDecisionRequest;
import com.cafetron.vendor.dto.VendorOrderResponse;
import com.cafetron.vendor.service.VendorOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendor/orders")
public class VendorOrderController {
    private final VendorOrderService vendorOrderService;

    public VendorOrderController(VendorOrderService vendorOrderService) {
        this.vendorOrderService = vendorOrderService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<VendorOrderResponse> getMyVendorOrders(@AuthenticationPrincipal UserPrincipal principal) {
        return vendorOrderService.getMyVendorOrders(principal);
    }

    @PostMapping("/{statusId}/accept")
    @ResponseStatus(HttpStatus.OK)
    public VendorOrderResponse accept(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long statusId
    ) {
        return vendorOrderService.accept(principal, statusId);
    }

    @PostMapping("/{statusId}/decline")
    @ResponseStatus(HttpStatus.OK)
    public VendorOrderResponse decline(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long statusId,
            @RequestBody(required = false) VendorOrderDecisionRequest request
    ) {
        return vendorOrderService.decline(principal, statusId, request == null ? null : request.reason());
    }
}
