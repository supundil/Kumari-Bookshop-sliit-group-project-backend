package com.g1.kumaribookshopbackend.service.impl;

import com.g1.kumaribookshopbackend.dbConnection.DbConnection;
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
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.webjars.NotFoundException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
                List<OrderDetail> orderDetails = new ArrayList<>();
                Product product = productRepository.findById(customerOrderDto.getProductId()).orElseThrow(() -> {
                    throw new InternalServerException(MessageConstant.PRODUCT_NOT_FOUND);
                });


                if (order.isPresent()) {
                    Optional<OrderDetail> orderDetail = orderDetailRepository.findByCustomerOrderAndProduct(order.get(), product);
                    if (orderDetail.isPresent()) {
                        OrderDetail save = orderDetail.get();
                        validateQuantity(product, save.getProductQnt() + customerOrderDto.getQuantity());
                        save.setProductQnt(save.getProductQnt() + customerOrderDto.getQuantity());
                        save.setProductTotalPrice(product.getSellingPrice().multiply(BigDecimal.valueOf(save.getProductQnt())));
                        OrderDetail save1 = orderDetailRepository.save(save);

                    } else {
                        validateQuantity(product, customerOrderDto.getQuantity());
                        OrderDetail save = getOrderDetail(product, order.get(), customerOrderDto.getQuantity());
                        orderDetailRepository.save(save);
                    }
                } else {
                    CustomerOrder customerOrder = CustomerOrder.builder().customer(customer).totalCost(BigDecimal.ZERO).orderStatus(OrderStatus.PENDING).build();
                    CustomerOrder save = customerOrderRepository.save(customerOrder);
                    OrderDetail save1 = orderDetailRepository.save(getOrderDetail(product, save, customerOrderDto.getQuantity()));
                }
                BigDecimal totalCost = product.getSellingPrice().multiply(BigDecimal.valueOf(customerOrderDto.getQuantity()));
                Optional<CustomerOrder> updatedOrder = customerOrderRepository.findFirstByOrderStatusAndCustomer(OrderStatus.PENDING, customer);
                if (updatedOrder.isPresent()) {
                    CustomerOrder customerOrder = updatedOrder.get();
                    customerOrder.setTotalCost(customerOrder.getTotalCost().add(totalCost));
                    customerOrderRepository.save(customerOrder);
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
                    customerOrderWrapperDto.setTotalCost(orderDetail.getTotalCost());
                    customerOrderWrapperDto.setOrderStatus(orderDetail.getOrderStatus());
                    customerOrderWrapperDto.setProductCount(orderDetail.getOrderDetailSet().size());
                    customerOrderWrapperDto.setCreatedDate(orderDetail.getCreatedDate());

                    if (!CollectionUtils.isEmpty(orderDetail.getOrderDetailSet())) {
                        orderDetail.getOrderDetailSet().forEach(o -> {
                            orderDetailDtoList.add(o.toDto());
                        });
                    }

                    customerOrderWrapperDto.setOrderDetailDtoList(orderDetailDtoList.stream().sorted(Comparator.comparing(OrderDetailDto::getCreatedDate)).toList());
                    return customerOrderWrapperDto;

                } else {
                    return null;
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
                detail.setProductQnt(detail.getProductQnt() + 1);
                validateQuantity(detail.getProduct(), detail.getProductQnt());
                detail.setProductTotalPrice(detail.getProductTotalPrice().add(detail.getProduct().getSellingPrice()));
                orderDetailRepository.save(detail);
                CustomerOrder customerOrder = orderDetail.get().getCustomerOrder();
                customerOrder.setTotalCost(customerOrder.getTotalCost().add(detail.getProduct().getSellingPrice()));
                customerOrderRepository.save(customerOrder);
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
                detail.setProductQnt(detail.getProductQnt() - 1);
                detail.setProductTotalPrice(detail.getProductTotalPrice().add(detail.getProduct().getSellingPrice().negate()));
                orderDetailRepository.save(detail);
                CustomerOrder customerOrder = orderDetail.get().getCustomerOrder();
                customerOrder.setTotalCost(customerOrder.getTotalCost().add(detail.getProduct().getSellingPrice().negate()));
                customerOrderRepository.save(customerOrder);
                return true;
            } else {
                throw new InternalServerException(MessageConstant.ORDER_DETAIL_NOT_FOUND);
            }

        } catch (Exception e) {
            log.error("increaseProductQuantity failed : ");
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Boolean placeOrder(String username) {
        try {

            Customer customer = customerRepository.findByUserName(username).orElseThrow(() -> {
                throw new InternalServerException(MessageConstant.USER_NOT_FOUND);
            });

            Optional<CustomerOrder> order = customerOrderRepository.findFirstByOrderStatusAndCustomer(OrderStatus.PENDING, customer);
            if (order.isPresent()) {
                CustomerOrder customerOrder = order.get();
                customerOrder.setOderPlacedDate(LocalDateTime.now());
                customerOrder.setOrderStatus(OrderStatus.SUBMITTED);
                customerOrderRepository.save(customerOrder);
                return true;
            } else {
                throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            log.error("placeOrder failed : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<CustomerOrderWrapperDto> getAllOrders(String username) {
        try {
            List<CustomerOrderWrapperDto> orderWrapperDtoList = new ArrayList<>();

            OrderDetailDto orderDetailDto = new OrderDetailDto();

            if (Objects.nonNull(username)) {

                Customer customer = customerRepository.findByUserName(username).orElseThrow(() -> {
                    throw new InternalServerException(MessageConstant.USER_NOT_FOUND);
                });

                List<CustomerOrder> allOrders = customerOrderRepository.findAllByCustomer(customer);
                if (!CollectionUtils.isEmpty(allOrders)) {

                    allOrders = allOrders.stream().filter(customerOrder -> !customerOrder.getOrderStatus().equals(OrderStatus.PENDING)).toList();

                    allOrders.forEach(customerOrder -> {
                        CustomerOrderWrapperDto customerOrderWrapperDto = new CustomerOrderWrapperDto();
                        List<OrderDetailDto> orderDetailDtoList = new ArrayList<>();

                        CustomerOrder orderDetail = customerOrder;
                        customerOrderWrapperDto.setOderId(orderDetail.getOderId());
                        customerOrderWrapperDto.setUsername(username);
                        customerOrderWrapperDto.setTotalCost(orderDetail.getTotalCost());
                        customerOrderWrapperDto.setOrderStatus(orderDetail.getOrderStatus());
                        customerOrderWrapperDto.setProductCount(orderDetail.getOrderDetailSet().size());
                        customerOrderWrapperDto.setCreatedDate(orderDetail.getCreatedDate());

                        if (!CollectionUtils.isEmpty(orderDetail.getOrderDetailSet())) {
                            orderDetail.getOrderDetailSet().forEach(o -> {
                                orderDetailDtoList.add(o.toDto());
                            });
                            customerOrderWrapperDto.setOrderDetailDtoList(orderDetailDtoList.stream().sorted(Comparator.comparing(OrderDetailDto::getCreatedDate)).toList());
                        }
                        orderWrapperDtoList.add(customerOrderWrapperDto);
                    });

                    return orderWrapperDtoList.stream().sorted(Comparator.comparing(CustomerOrderWrapperDto::getCreatedDate).reversed()).toList();
                } else {
                    return orderWrapperDtoList;
                }


            } else {
                throw new InternalServerException(MessageConstant.USER_NOT_FOUND);
            }

        } catch (Exception e) {
            log.error("getAllOrder failed : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }


    public ByteArrayInputStream printBill(Long OrderId, String customerName, String total) {
        try {
            JasperDesign jasperDesign = JRXmlLoader.load("src/main/resources/Invoice.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("Parameter1", OrderId);
            parameters.put("main_total", total);
            parameters.put("cus_name", customerName);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, DbConnection.getInstance().getConnection());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);
//          Uncomment and set path if you want to download from backend

//          String pdfFilePath = "D:\\SLIIT\\projects\\group\\Invoice.pdf";
//          JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFilePath);
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            log.error("order bill generate failed : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<CustomerOrderWrapperDto> getAllCustomerSubmittedOrders() {
        try {
            List<CustomerOrderWrapperDto> orderWrapperDtoList = new ArrayList<>();

            OrderDetailDto orderDetailDto = new OrderDetailDto();


            List<CustomerOrder> allOrders = customerOrderRepository.findAllByOrderStatus(OrderStatus.SUBMITTED);
            if (!CollectionUtils.isEmpty(allOrders)) {


                allOrders.forEach(customerOrder -> {
                    CustomerOrderWrapperDto customerOrderWrapperDto = new CustomerOrderWrapperDto();
                    List<OrderDetailDto> orderDetailDtoList = new ArrayList<>();

                    CustomerOrder orderDetail = customerOrder;
                    customerOrderWrapperDto.setOderId(orderDetail.getOderId());
                    customerOrderWrapperDto.setUsername(orderDetail.getCustomer().getName());
                    customerOrderWrapperDto.setTotalCost(orderDetail.getTotalCost());
                    customerOrderWrapperDto.setOrderStatus(orderDetail.getOrderStatus());
                    customerOrderWrapperDto.setProductCount(orderDetail.getOrderDetailSet().size());
                    customerOrderWrapperDto.setCreatedDate(orderDetail.getCreatedDate());

                    if (!CollectionUtils.isEmpty(orderDetail.getOrderDetailSet())) {
                        orderDetail.getOrderDetailSet().forEach(o -> {
                            orderDetailDtoList.add(o.toDto());
                        });
                        customerOrderWrapperDto.setOrderDetailDtoList(orderDetailDtoList.stream().sorted(Comparator.comparing(OrderDetailDto::getCreatedDate)).toList());
                    }
                    orderWrapperDtoList.add(customerOrderWrapperDto);
                });

                return orderWrapperDtoList.stream().sorted(Comparator.comparing(CustomerOrderWrapperDto::getCreatedDate).reversed()).toList();
            } else {
                return orderWrapperDtoList;
            }


        } catch (Exception e) {
            log.error("getAllCustomerSubmittedOrders failed : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<CustomerOrderWrapperDto> getAllConfirmedOrders() {
        try {
            List<CustomerOrderWrapperDto> orderWrapperDtoList = new ArrayList<>();

            OrderDetailDto orderDetailDto = new OrderDetailDto();


            List<CustomerOrder> allOrders = customerOrderRepository.findAllByOrderStatus(OrderStatus.CONFIRMED);
            if (!CollectionUtils.isEmpty(allOrders)) {


                allOrders.forEach(customerOrder -> {
                    CustomerOrderWrapperDto customerOrderWrapperDto = new CustomerOrderWrapperDto();
                    List<OrderDetailDto> orderDetailDtoList = new ArrayList<>();

                    CustomerOrder orderDetail = customerOrder;
                    customerOrderWrapperDto.setOderId(orderDetail.getOderId());
                    customerOrderWrapperDto.setUsername(orderDetail.getCustomer().getName());
                    customerOrderWrapperDto.setTotalCost(orderDetail.getTotalCost());
                    customerOrderWrapperDto.setOrderStatus(orderDetail.getOrderStatus());
                    customerOrderWrapperDto.setProductCount(orderDetail.getOrderDetailSet().size());
                    customerOrderWrapperDto.setCreatedDate(orderDetail.getCreatedDate());

                    if (!CollectionUtils.isEmpty(orderDetail.getOrderDetailSet())) {
                        orderDetail.getOrderDetailSet().forEach(o -> {
                            orderDetailDtoList.add(o.toDto());
                        });
                        customerOrderWrapperDto.setOrderDetailDtoList(orderDetailDtoList.stream().sorted(Comparator.comparing(OrderDetailDto::getCreatedDate)).toList());
                    }
                    orderWrapperDtoList.add(customerOrderWrapperDto);
                });

                return orderWrapperDtoList.stream().sorted(Comparator.comparing(CustomerOrderWrapperDto::getCreatedDate).reversed()).toList();
            } else {
                return orderWrapperDtoList;
            }


        } catch (Exception e) {
            log.error("getAllConfirmedOrders failed : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<CustomerOrderWrapperDto> getAllPaidOrders() {
        try {
            List<CustomerOrderWrapperDto> orderWrapperDtoList = new ArrayList<>();

            OrderDetailDto orderDetailDto = new OrderDetailDto();


            List<CustomerOrder> allOrders = customerOrderRepository.findAllByOrderStatus(OrderStatus.PAID);
            if (!CollectionUtils.isEmpty(allOrders)) {


                allOrders.forEach(customerOrder -> {
                    CustomerOrderWrapperDto customerOrderWrapperDto = new CustomerOrderWrapperDto();
                    List<OrderDetailDto> orderDetailDtoList = new ArrayList<>();

                    CustomerOrder orderDetail = customerOrder;
                    customerOrderWrapperDto.setOderId(orderDetail.getOderId());
                    customerOrderWrapperDto.setUsername(orderDetail.getCustomer().getName());
                    customerOrderWrapperDto.setTotalCost(orderDetail.getTotalCost());
                    customerOrderWrapperDto.setOrderStatus(orderDetail.getOrderStatus());
                    customerOrderWrapperDto.setProductCount(orderDetail.getOrderDetailSet().size());
                    customerOrderWrapperDto.setCreatedDate(orderDetail.getCreatedDate());

                    if (!CollectionUtils.isEmpty(orderDetail.getOrderDetailSet())) {
                        orderDetail.getOrderDetailSet().forEach(o -> {
                            orderDetailDtoList.add(o.toDto());
                        });
                        customerOrderWrapperDto.setOrderDetailDtoList(orderDetailDtoList.stream().sorted(Comparator.comparing(OrderDetailDto::getCreatedDate)).toList());
                    }
                    orderWrapperDtoList.add(customerOrderWrapperDto);
                });

                return orderWrapperDtoList.stream().sorted(Comparator.comparing(CustomerOrderWrapperDto::getCreatedDate).reversed()).toList();
            } else {
                return orderWrapperDtoList;
            }


        } catch (Exception e) {
            log.error("getAllPaidOrders failed : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<CustomerOrderWrapperDto> getAllRejectedOrders() {
        try {
            List<CustomerOrderWrapperDto> orderWrapperDtoList = new ArrayList<>();

            OrderDetailDto orderDetailDto = new OrderDetailDto();


            List<CustomerOrder> allOrders = customerOrderRepository.findAllByOrderStatus(OrderStatus.REJECTED);
            if (!CollectionUtils.isEmpty(allOrders)) {


                allOrders.forEach(customerOrder -> {
                    CustomerOrderWrapperDto customerOrderWrapperDto = new CustomerOrderWrapperDto();
                    List<OrderDetailDto> orderDetailDtoList = new ArrayList<>();

                    CustomerOrder orderDetail = customerOrder;
                    customerOrderWrapperDto.setOderId(orderDetail.getOderId());
                    customerOrderWrapperDto.setUsername(orderDetail.getCustomer().getName());
                    customerOrderWrapperDto.setTotalCost(orderDetail.getTotalCost());
                    customerOrderWrapperDto.setOrderStatus(orderDetail.getOrderStatus());
                    customerOrderWrapperDto.setProductCount(orderDetail.getOrderDetailSet().size());
                    customerOrderWrapperDto.setCreatedDate(orderDetail.getCreatedDate());

                    if (!CollectionUtils.isEmpty(orderDetail.getOrderDetailSet())) {
                        orderDetail.getOrderDetailSet().forEach(o -> {
                            orderDetailDtoList.add(o.toDto());
                        });
                        customerOrderWrapperDto.setOrderDetailDtoList(orderDetailDtoList.stream().sorted(Comparator.comparing(OrderDetailDto::getCreatedDate)).toList());
                    }
                    orderWrapperDtoList.add(customerOrderWrapperDto);
                });

                return orderWrapperDtoList.stream().sorted(Comparator.comparing(CustomerOrderWrapperDto::getCreatedDate).reversed()).toList();
            } else {
                return orderWrapperDtoList;
            }


        } catch (Exception e) {
            log.error("getAllRejectedOrders failed : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Boolean confirmCustomerOrder(Long orderId) {
        try {

            if (Objects.nonNull(orderId)) {

                Optional<CustomerOrder> customerOrder = customerOrderRepository.findById(orderId);
                if (customerOrder.isPresent()) {

                    CustomerOrder order = customerOrder.get();
                    order.setOrderStatus(OrderStatus.CONFIRMED);
                    customerOrderRepository.save(order);
                    return true;
                } else {
                    throw new NotFoundException(MessageConstant.ORDER_DETAIL_NOT_FOUND);
                }

            } else {
                throw new BadRequestException(MessageConstant.BAS_REQUEST);
            }

        } catch (Exception e) {
            log.error("confirmCustomerOrder failed : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Boolean rejectCustomerOrder(Long orderId) {
        try {

            if (Objects.nonNull(orderId)) {

                Optional<CustomerOrder> customerOrder = customerOrderRepository.findById(orderId);
                if (customerOrder.isPresent()) {

                    CustomerOrder order = customerOrder.get();
                    order.setOrderStatus(OrderStatus.REJECTED);
                    customerOrderRepository.save(order);
                    return true;
                } else {
                    throw new NotFoundException(MessageConstant.ORDER_DETAIL_NOT_FOUND);
                }

            } else {
                throw new BadRequestException(MessageConstant.BAS_REQUEST);
            }

        } catch (Exception e) {
            log.error("rejectCustomerOrder failed : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Boolean closeCustomerOrder(Long orderId) {
        try {

            if (Objects.nonNull(orderId)) {

                Optional<CustomerOrder> customerOrder = customerOrderRepository.findById(orderId);
                if (customerOrder.isPresent()) {

                    CustomerOrder order = customerOrder.get();
                    order.setOrderStatus(OrderStatus.PAID);
                    customerOrderRepository.save(order);
                    return true;
                } else {
                    throw new NotFoundException(MessageConstant.ORDER_DETAIL_NOT_FOUND);
                }

            } else {
                throw new BadRequestException(MessageConstant.BAS_REQUEST);
            }

        } catch (Exception e) {
            log.error("getAllOrder failed : " + e.getMessage());
            throw new InternalServerException(MessageConstant.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ByteArrayInputStream getBill(String username) {
        BigDecimal total = new BigDecimal(0);
        Customer customer = customerRepository.findByUserName(username).orElseThrow(() -> {
            throw new InternalServerException(MessageConstant.USER_NOT_FOUND);
        });

        Optional<CustomerOrder> order = customerOrderRepository.findFirstByOrderStatusAndCustomer(OrderStatus.CONFIRMED, customer);
        if (order.isPresent()) {
            for (OrderDetail orderDetail : order.get().getOrderDetailSet()) {
                total = total.add(orderDetail.getProductTotalPrice());
            }
            return printBill(order.get().getOderId(), customer.getName(), total.toString());
        } else {
            return null;
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
