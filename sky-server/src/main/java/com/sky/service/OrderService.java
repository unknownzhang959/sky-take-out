package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.*;

import java.time.LocalDate;

public interface OrderService {
    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);
    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);
    /**
     * 历史订单查询
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult page(Integer page, Integer pageSize, Integer status);
    /**
     * 根据id查询订单详情
     * @param id
     * @return
     */
    OrderVO getOrderDetail(Long id);
    /**
     * 取消订单
     * @param ordersCancelDTO
     * @return
     */
    void cancel(OrdersCancelDTO ordersCancelDTO);
    /**
     * 再来一单
     * @param id
     * @return
     */
    void repetition(Long id);
    /**
     * 条件搜索订单
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);
    /**
     * 统计订单数据
     * @return
     */
    OrderStatisticsVO statistics();
    /**
     * 确认订单
     * @param ordersConfirmDTO
     * @return
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);
    /**
     * 拒绝订单
     * @param ordersRejectionDTO
     * @return
     */
    void rejection(OrdersRejectionDTO ordersRejectionDTO);
    /**
     * 派送订单
     * @param id
     */
    void delivery(Long id);
    /**
     * 完成订单
     * @param id
     */
    void complete(Long id);
    /**
     * 催单
     * @param id
     */
    void reminder(Long id);

}
