package com.example.orders.api;

import com.example.orders.domain.service.OrderService;
import com.example.orders.dto.OrderRequest;
import com.example.orders.dto.OrderResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody OrderRequest req){
        return service.create(req);
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable String id){
        return service.get(id);
    }

    @GetMapping
    public List<OrderResponse> list(){
        return service.list();
    }
}
