package com.g1.kumaribookshopbackend.controller;

import com.g1.kumaribookshopbackend.dto.CustomerOrderDto;
import com.g1.kumaribookshopbackend.dto.CustomerOrderWrapperDto;
import com.g1.kumaribookshopbackend.service.OrderService;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.repo.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@AllArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/v1/order-service")
public class OrderController {

    private final OrderService orderService;


    @PostMapping("/add-to-cart")
    public ResponseEntity<Boolean> addToCart(@RequestBody CustomerOrderDto customerOrderDto) {
        return new ResponseEntity<>(orderService.addToCart(customerOrderDto), HttpStatus.OK);
    }

    @GetMapping("/get-cart/{username}")
    public ResponseEntity<CustomerOrderWrapperDto> getCart(@PathVariable String username) {
        return new ResponseEntity<>(orderService.getCart(username), HttpStatus.OK);
    }

    @PostMapping("/increase-product-quantity/{detailId}")
    public ResponseEntity<Boolean> increaseProductQuantity(@PathVariable Long detailId) {
        return new ResponseEntity<>(orderService.increaseProductQuantity(detailId), HttpStatus.OK);
    }

    @PostMapping("/decrease-product-quantity/{detailId}")
    public ResponseEntity<Boolean> decreaseProductQuantity(@PathVariable Long detailId) {
        return new ResponseEntity<>(orderService.decreaseProductQuantity(detailId), HttpStatus.OK);
    }

    @PostMapping("/place-order/{username}")
    public ResponseEntity<Boolean> placeOrder(@PathVariable String username) {
        return new ResponseEntity<>(orderService.placeOrder(username), HttpStatus.OK);
    }

    @GetMapping("/get-all-orders/{username}")
    public ResponseEntity<List<CustomerOrderWrapperDto>> getAllOrders(@PathVariable String username) {
        return new ResponseEntity<>(orderService.getAllOrders(username), HttpStatus.OK);
    }

    @GetMapping("/get-all-customer-submitted-orders")
    public ResponseEntity<List<CustomerOrderWrapperDto>> getAllCustomerSubmittedOrders() {
        return new ResponseEntity<>(orderService.getAllCustomerSubmittedOrders(), HttpStatus.OK);
    }

    @PostMapping("/confirm-customer-order/{orderId}")
    public ResponseEntity<Boolean> confirmCustomerOrder(@PathVariable Long orderId) {
        return new ResponseEntity<>(orderService.confirmCustomerOrder(orderId), HttpStatus.OK);
    }

    @PostMapping("/reject-customer-order/{orderId}")
    public ResponseEntity<Boolean> rejectCustomerOrder(@PathVariable Long orderId) {
        return new ResponseEntity<>(orderService.rejectCustomerOrder(orderId), HttpStatus.OK);
    }

    @GetMapping("/get-all-confirmed-orders")
    public ResponseEntity<List<CustomerOrderWrapperDto>> getAllConfirmedOrders() {
        return new ResponseEntity<>(orderService.getAllConfirmedOrders(), HttpStatus.OK);
    }

    @GetMapping("/get-all-paid-orders")
    public ResponseEntity<List<CustomerOrderWrapperDto>> getAllPaidOrders() {
        return new ResponseEntity<>(orderService.getAllPaidOrders(), HttpStatus.OK);
    }

    @GetMapping("/get-all-rejected-orders")
    public ResponseEntity<List<CustomerOrderWrapperDto>> getAllRejectedOrders() {
        return new ResponseEntity<>(orderService.getAllRejectedOrders(), HttpStatus.OK);
    }

    @PostMapping("/paid-customer-order/{orderId}")
    public ResponseEntity<Boolean> paidCustomerOrder(@PathVariable Long orderId) {
        return new ResponseEntity<>(orderService.closeCustomerOrder(orderId), HttpStatus.OK);
    }
    @PostMapping("/get-bill/{orderId}")
    public ResponseEntity<byte[]> getBill(@PathVariable Long orderId) {

        try {

            ByteArrayInputStream pdfStream = orderService.getBill(orderId);
            if(null == pdfStream) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=Invoice_" + orderId + ".pdf");
            InputStreamResource resource = new InputStreamResource();
            resource.setInputStream(pdfStream);
            return new ResponseEntity<>(pdfStream.readAllBytes(), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
