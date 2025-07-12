package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "B端订单接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 订单搜索
     *
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("订单搜索：condition search");
        PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    };

    @GetMapping("/statistics")
    @ApiOperation("订单统计")
    public Result<OrderStatisticsVO> statistics() {
        log.info("订单统计：statistics");
        return Result.success(orderService.statistics());
    }

    @GetMapping("/details/{id}")
    @ApiOperation("订单详情")
    public Result<OrderVO> detail(@PathVariable(value = "id") Long id) {
        log.info("订单详情：detail");
        return Result.success(orderService.details(id));
    }

    @PutMapping("/confirm")
    @ApiOperation("确认订单")
    public Result confirm(@RequestBody OrdersConfirmDTO orderConfirmDTO) {
        log.info("确认订单：confirm");
        orderService.confirm(orderConfirmDTO);
        return Result.success();
    }

    @PutMapping("/rejection")
    @ApiOperation("Reject order")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        log.info("Reject order：rejection");
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * cancel order
     * @param ordersCancelDTO
     * @return
     * @throws Exception
     */
    @PutMapping("/cancel")
    @ApiOperation("Cancel order")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) throws Exception {
        log.info("Cancel order：cancel");
        orderService.cancelOrders(ordersCancelDTO);
        return Result.success();
    }

    /**
     * Accept order
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("Accept order")
    public Result delivery(@PathVariable(value = "id") Long id) {
        log.info("Accept order：delivery");
        orderService.delivery(id);
        return Result.success();
    }







}
