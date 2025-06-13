package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Service
public class ReportServicelmpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        //1.构造dateList
        List<LocalDate> dateList = getDateList(begin, end);
        log.info("开始构造日期集合：{}", dateList);
        //2.构造turnoverList
        List<Double> turnoverList = new ArrayList<>();
        dateList.forEach(date -> {
            Map map = new HashMap();
            map.put("status", Orders.COMPLETED);
            map.put("beginTime", LocalDateTime.of(date, LocalTime.MIN));
            map.put("endTime",LocalDateTime.of(date, LocalTime.MAX));
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        });
        //3.封装数据并返回
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    @Override
    public OrderReportVO orderStatistics(LocalDate begin, LocalDate end) {
        //1.构造dateList
        List<LocalDate> dateList = getDateList(begin, end);
        //2.构造orderCountList和totalOrderCountList
        List<Integer> orderCountList = new ArrayList<>();
        Integer totalOrderCount = 0;
        List<Integer> validOrderCountList = new ArrayList<>();
        Integer validOrderCount = 0;
        for (LocalDate date : dateList) {
            Map map = new HashMap();
            map.put("beginTime",  LocalDateTime.of(date, LocalTime.MIN));
            map.put("endTime", LocalDateTime.of(date, LocalTime.MAX));
            Integer orderCount = orderMapper.countByMap(map);
//            orderCount = orderCount == null ? 0 : orderCount;
            orderCountList.add(orderCount);
            totalOrderCount += orderCount;
        //3.构造validOrderCountList and validOrderCount
            map.put("status", Orders.COMPLETED);
            Integer OrderCount = orderMapper.countByMap(map);
//            OrderCount = OrderCount == null ? 0 : OrderCount;
            validOrderCountList.add(OrderCount);
            validOrderCount += OrderCount;
        }
        Double CompletionRate=  0.0;
        if (totalOrderCount != 0){
            CompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }
        //4.封装数据并返回
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .validOrderCount(validOrderCount)
                .orderCompletionRate(CompletionRate)
                .build();
    }

    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        //2.构造nameList and numberList,封装数据
        List<String> nameList = new ArrayList<>();
        List<Object> numberList = new ArrayList<>();
        Map map = new HashMap();
        map.put("beginTime", LocalDateTime.of(begin, LocalTime.MIN));
        map.put("endTime", LocalDateTime.of(end, LocalTime.MAX));
        List<Map<String, Object>> resultList = orderMapper.getSalesTop10(map);
        if (resultList != null && !resultList.isEmpty()) {
            resultList.forEach(result -> {
                nameList.add((String) result.get("name"));
                numberList.add(result.get("number"));
            });
//            for (Map<List<String>, List<Integer>> nameAndNumber : resultList) {
//                nameAndNumber.forEach((name, number) -> {
//                    if (!name.isEmpty() && !number.isEmpty()) {
//                        nameList.add(name.get(0));
//                        numberList.add(number.get(0));
//                    }
//                });
//            }
        }

        //3.返回
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numberList, ","))
                .build();

    }

    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        //1.构造dateList
        List<LocalDate> dateList = getDateList(begin, end);
        //2.构造newUserList and totalUserList
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        Integer totalUserCount = 0;
        Integer newUserCount = 0;
        for (LocalDate date : dateList) {
            Map map = new HashMap();
            map.put("endTime", LocalDateTime.of(date, LocalTime.MAX));
            totalUserCount  = userMapper.countUserByMap(map);
            map.put("beginTime", LocalDateTime.of(date, LocalTime.MIN));
            newUserCount = userMapper.countUserByMap(map);
            totalUserList.add(totalUserCount);
            newUserList.add(newUserCount);
        }
        //3.封装数据并返回
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    @Override
    public void export(HttpServletResponse response){
        //1.从数据库获取数据
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));
        try {
            //2.把数据填到表格中
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet sheet = workbook.getSheet("Sheet1");
            sheet.getRow(1).getCell(1).setCellValue(begin+"至"+end);
            sheet.getRow(3).getCell(2).setCellValue(businessData.getTurnover());
            sheet.getRow(4).getCell(2).setCellValue(businessData.getValidOrderCount());
            sheet.getRow(3).getCell(4).setCellValue(businessData.getOrderCompletionRate());
            sheet.getRow(3).getCell(6).setCellValue(businessData.getNewUsers());
            sheet.getRow(4).getCell(4).setCellValue(businessData.getUnitPrice());
            for (int i = 0; i < 30; i++) {
                LocalDate date = begin.plusDays(i);
                XSSFRow row = sheet.getRow(i+7);
                businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }
            //3.输出到浏览器
            ServletOutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            out.close();
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取指定时间区间内的日期集合
    public List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        while (!begin.isAfter(end)) {
            dateList.add(begin);
            begin = begin.plusDays(1);
        }
        return dateList;
    }
}
