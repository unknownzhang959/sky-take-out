package com.sky.task;

import com.sky.constant.MessageConstant;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;
    //  检查用户超时的待付款订单（下单超过十五分钟未付款则超时），每一分钟执行一次
    @Scheduled(cron = "0 0/1 * * * ?")
    public void checkOutTimeOrder() {
        log.info("检查用户超时的待付款订单（下单超过十五分钟未付款则超时）");
        //1.  查询状态为“待付款”的订单，且下单时间超过15分钟
        LocalDateTime time = LocalDateTime.now().minusMinutes(15);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);
        //2.  如果查询到结果，则修改订单状态为“已取消”
        if (ordersList != null && ordersList.size() > 0) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason(MessageConstant.ORDER_TIMEOUT_CANCELLED);
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }
    //  每天1点检查派送超过1h订单，修改订单状态为已完成
    @Scheduled(cron = "0 0 1 * * ?")
//    @Scheduled(cron = "0 39 23 * * ?")
    public void checkCompleteOrder() {
        log.info("检查派送中的订单，修改订单状态为已完成");
        //1.  查询状态为“派送中”的订单，且下单时间超过60分钟
        LocalDateTime time = LocalDateTime.now().minusMinutes(60);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);
        //2.  如果查询到结果，则修改订单状态为“已完成”
        if (ordersList != null && !ordersList.isEmpty()) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);
                orders.setDeliveryTime(time);
                orderMapper.update(orders);
            }
        }
    }
}
