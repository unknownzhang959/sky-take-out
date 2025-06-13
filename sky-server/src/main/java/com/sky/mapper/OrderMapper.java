package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrderMapper {
    /**
     * 插入订单数据
     * @param orders
     */
    void insert(Orders orders);
    /**
     * 根据订单号和用户id查询订单
     * @param orderNumber
     * @param userId
     */
    @Select("select * from orders where number = #{orderNumber} and user_id= #{userId}")
    Orders getByNumberAndUserId(String orderNumber, Long userId);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);
    /**
     * 分页查询订单
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);
    /**
     * 获取订单详情
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getWithId(Long id);
    /**
     * 条件搜索订单
     * @param ordersPageQueryDTO
     * @return
     */
    Page<OrderVO> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);
    /**
     * 各个状态的订单数量统计
     * @return
     */
    OrderStatisticsVO statistics();
    @Update("update orders set status = 3 where id = #{orderId}")
    void confirm(Integer orderId);
    @Select("select * from orders where status = #{status} and order_time < #{time}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime time);
    /**
     * 根据Map条件查询总记录数
     * @param map
     * @return
     */
    Double sumByMap(Map map);
    /**
     * 根据Map条件查询总记录数
     * @param map
     * @return
     */
    Integer countByMap(Map map);
    /**
     * 查询指定时间区间内的营业额数据
     * @param map
     * @return
     */
    List<Map<String, Object>>  getSalesTop10(Map map);





    //订单明细表操作
    /**
     * 插入订单明细数据
     * @param orderDetail
     */
    void insertdetail(OrderDetail orderDetail);
    /**
     * 根据订单id查询订单明细
     * @param orderId
     * @return
     */
    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);

}
