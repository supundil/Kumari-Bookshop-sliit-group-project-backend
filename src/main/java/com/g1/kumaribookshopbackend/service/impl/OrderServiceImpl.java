package com.g1.kumaribookshopbackend.service.impl;

import com.g1.kumaribookshopbackend.dto.CustomerOrderDto;
import com.g1.kumaribookshopbackend.dto.CustomerOrderWrapperDto;
import com.g1.kumaribookshopbackend.dto.OrderDetailDto;
import com.g1.kumaribookshopbackend.entity.Customer;
import com.g1.kumaribookshopbackend.entity.CustomerOrder;
import com.g1.kumaribookshopbackend.entity.OrderDetail;
import com.g1.kumaribookshopbackend.entity.Product;
import com.g1.kumaribookshopbackend.enums.OrderStatus;
import com.g1.kumaribookshopbackend.exception.BadRequestException;
import com.g1.kumaribookshopbackend.exception.InternalServerException;
import com.g1.kumaribookshopbackend.exception.ValidateException;
import com.g1.kumaribookshopbackend.repository.CustomerOrderRepository;
import com.g1.kumaribookshopbackend.repository.CustomerRepository;
import com.g1.kumaribookshopbackend.repository.OrderDetailRepository;
import com.g1.kumaribookshopbackend.repository.ProductRepository;
import com.g1.kumaribookshopbackend.service.OrderService;
import com.g1.kumaribookshopbackend.util.MessageConstant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CustomerOrderRepository customerOrderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Override
    public Boolean addToCart(CustomerOrderDto customerOrderDto) {
        try {

            if (Objects.nonNull(customerOrderDto) && Objects.nonNull(customerOrderDto.getUsername()) && Objects.nonNull(customerOrderDto.getProductId())) {

                Customer customer = customerRepository.findByUserName(customerOrderDto.getUsername()).orElseThrow(() -> {
                    throw new InternalServerException(MessageConstant.USER_NOT_FOUND);
                });

                Optional<CustomerOrder> order = customerOrderRepository.findFirstByOrderStatusAndCustomer(OrderStatus.PENDING, customer);

                Product product = productRepository.findById(customerOrderDto.getProductId()).orElseThrow(() -> {
                    throw new InternalServerException(MessageConstant.PRODUCT_NOT_FOUND);
                });

                if (order.isPresent()) {
                    CustomerOrder customerOrder = order.get();
                    for (OrderDetail detail : customerOrder.getOrderDetailSet()) {
                        if (detail.getProduct().equals(product)) {
                            OrderDetail orderDetail = detail;
                            validateQuantity(product, orderDetail.getProductQnt() + customerOrderDto.getQuantity());
                            orderDetail.setProductQnt(orderDetail.getProductQnt() + customerOrderDto.getQuantity());
                            orderDetail.setProductTotalPrice(product.getSellingPrice().multiply(BigDecimal.valueOf(orderDetail.getProductQnt())));
                            customerOrder.getOrderDetailSet().remove(detail);
                            customerOrder.getOrderDetailSet().add(orderDetail);
                            break;
                        } else {
                            validateQuantity(product, customerOrderDto.getQuantity());
                            customerOrder.getOrderDetailSet().add(getOrderDetail(product, customerOrder, customerOrderDto.getQuantity()));
                            break;
                        }
                    }
                    customerOrderRepository.save(customerOrder);
                } else {
                    CustomerOrder customerOrder = CustomerOrder.builder()
                            .customer(customer)
                            .orderStatus(OrderStatus.PENDING)
                            .build();
                    CustomerOrder save = customerOrderRepository.save(customerOrder);
                    orderDetailRepository.save(getOrderDetail(product, save, customerOrderDto.getQuantity()));
                }
                return true;


            } else {
                throw new BadRequestException(MessageConstant.BAS_REQUEST);
            }

        } catch (ValidateException e) {
            throw new ValidateException(e.getMessage());
        } catch (Exception e) {
            log.error("addToCart failed : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public CustomerOrderWrapperDto getCart(String username) {
        try {

            CustomerOrderWrapperDto customerOrderWrapperDto = new CustomerOrderWrapperDto();
            List<OrderDetailDto> orderDetailDtoList = new ArrayList<>();
            OrderDetailDto orderDetailDto = new OrderDetailDto();

            if (Objects.nonNull(username)) {

                Customer customer = customerRepository.findByUserName(username).orElseThrow(() -> {
                    throw new InternalServerException(MessageConstant.USER_NOT_FOUND);
                });

                Optional<CustomerOrder> order = customerOrderRepository.findFirstByOrderStatusAndCustomer(OrderStatus.PENDING, customer);
                if (order.isPresent()) {

                    CustomerOrder orderDetail = order.get();
                    customerOrderWrapperDto.setOderId(orderDetail.getOderId());
                    customerOrderWrapperDto.setUsername(username);
                    customerOrderWrapperDto.setOrderStatus(orderDetail.getOrderStatus());
                    customerOrderWrapperDto.setProductCount(orderDetail.getOrderDetailSet().size());

                    if (!CollectionUtils.isEmpty(orderDetail.getOrderDetailSet())) {
                        orderDetail.getOrderDetailSet().forEach(o -> {
                           orderDetailDtoList.add(o.toDto());
                        });
                    }

                    customerOrderWrapperDto.setOrderDetailDtoList(orderDetailDtoList);
                    return customerOrderWrapperDto;

                } else {
                    return customerOrderWrapperDto;
                }

            } else {
                throw new InternalServerException(MessageConstant.USER_NOT_FOUND);
            }

        } catch (Exception e) {
            log.error("getCart failed : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Boolean increaseProductQuantity(Long detailId) {
        try {

            Optional<OrderDetail> orderDetail = orderDetailRepository.findById(detailId);
            if (orderDetail.isPresent()) {
                OrderDetail detail = orderDetail.get();
                detail.setProductQnt(detail.getProductQnt()+1);
                validateQuantity(detail.getProduct(),detail.getProductQnt());
                orderDetailRepository.save(detail);
                return true;
            } else {
                throw new InternalServerException(MessageConstant.ORDER_DETAIL_NOT_FOUND);
            }

        } catch (ValidateException e) {
            throw new ValidateException(e.getMessage());
        } catch (Exception e) {
            log.error("increaseProductQuantity failed : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Boolean decreaseProductQuantity(Long detailId) {
        try {

            Optional<OrderDetail> orderDetail = orderDetailRepository.findById(detailId);
            if (orderDetail.isPresent()) {
                OrderDetail detail = orderDetail.get();
                detail.setProductQnt(detail.getProductQnt()-1);
                orderDetailRepository.save(detail);
                return true;
            } else {
                throw new InternalServerException(MessageConstant.ORDER_DETAIL_NOT_FOUND);
            }

        } catch (Exception e) {
            log.error("increaseProductQuantity failed : ");
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }

    private OrderDetail getOrderDetail(Product product, CustomerOrder customerOrder, Integer quantity) {
        return OrderDetail.builder()
                .productQnt(quantity)
                .productTotalPrice(product.getSellingPrice().multiply(BigDecimal.valueOf(quantity)))
                .customerOrder(customerOrder)
                .product(product)
                .build();
    }

    private void validateQuantity(Product product, Integer quantity) {
        if (product.getQuantity() < quantity)
            throw new ValidateException(MessageConstant.INSUFFICIENT_QUANTITY);
    }
}
