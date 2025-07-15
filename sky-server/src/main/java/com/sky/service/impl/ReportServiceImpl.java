package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;

    /**
     * turnover report
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverReport(LocalDate begin, LocalDate end) {
        // store date in arrayList
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        // add all days between begin and end
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Double> turnoverList = new ArrayList<>();
        // sql
        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }


        return TurnoverReportVO.builder()
                .dateList(org.apache.commons.lang3.StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    @Override
    public UserReportVO getUserReport(LocalDate begin, LocalDate end) {
        // DateList
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        // UserList
        List<Integer> userList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("end", endTime);

            // count all user by date
            Integer totalUser = userMapper.countByMap(map);

            map.put("begin", beginTime);

            Integer newUser = userMapper.countByMap(map);

            userList.add(newUser);
            totalUserList.add(totalUser);
        }


        return UserReportVO
                .builder()
                .dateList(org.apache.commons.lang3.StringUtils.join(dateList, ","))
                .totalUserList(org.apache.commons.lang3.StringUtils.join(totalUserList, ","))
                .newUserList(org.apache.commons.lang3.StringUtils.join(userList, ","))
                .build();
    }

    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        // DateList
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> orderList = new ArrayList<>();
        List<Integer> validOrderList = new ArrayList<>();

        // loop the dateList to get the data
        for (LocalDate localDate : dateList) {
            // get the data of everyday orders
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);

            Integer orderCount = getOrderCount(beginTime, endTime, null);
            orderList.add(orderCount);

            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);
            validOrderList.add(validOrderCount);
        }
        // count total
        Integer totalCount = orderList.stream().reduce(Integer::sum).get();
        // count total valid
        Integer validCount = validOrderList.stream().reduce(Integer::sum).get();
        // calculate order completion rate
        Double orderCompletionRate = 0.0;
        if (totalCount != 0) {
            orderCompletionRate = validCount.doubleValue() / totalCount;
        }

        return OrderReportVO.builder()
                .orderCompletionRate(orderCompletionRate)
                .validOrderCount(validCount)
                .dateList(StringUtils.join(dateList, ","))
                .validOrderCountList(StringUtils.join(validOrderList, ","))
                .orderCountList(StringUtils.join(orderList, ","))
                .totalOrderCount(totalCount)
                .build();
    }

    @Override
    public SalesTop10ReportVO getSalesTop10Report(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getSalesTop(beginTime, endTime, 5);

        List<String> names = goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameLists = StringUtils.join(names, ",");

        List<Integer> numbers = goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberLists = StringUtils.join(numbers, ",");
        return SalesTop10ReportVO.builder()
                .nameList(nameLists)
                .numberList(numberLists)
                .build();
    }

    /**
     * export business data to excel
     * @param response
     */
    @Override
    public void exportBusiness(HttpServletResponse response) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=BusinessReport.xlsx");

        // query database
        LocalDate beginDay = LocalDate.now().minusDays(30);
        LocalDate endDay = LocalDate.now().minusDays(1);

        BusinessDataVO businessDataVO = workspaceService.getBusinessData(LocalDateTime.of(beginDay, LocalTime.MIN), LocalDateTime.of(endDay, LocalTime.MAX));

        // write to excel via POI
        InputStream inputSteam = this.getClass().getClassLoader().getResourceAsStream("template/DataReportTemplate.xlsx");
        try {
            // create excel based on the input stream
            XSSFWorkbook excel = new XSSFWorkbook(inputSteam);
            // fill up the cells
            XSSFSheet sheet1 = excel.getSheet("Sheet1");
            sheet1.getRow(1).getCell(1).setCellValue("Date From: " + beginDay + " To: " + endDay);

            // turnover
            sheet1.getRow(3).getCell(2).setCellValue(businessDataVO.getTurnover());
            // valid order count
            sheet1.getRow(4).getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            // user order complete rate
            sheet1.getRow(3).getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            // new user count
            sheet1.getRow(3).getCell(6).setCellValue(businessDataVO.getNewUsers());
            // average unit price
            sheet1.getRow(4).getCell(4).setCellValue(businessDataVO.getUnitPrice());

            // fill up details date
            for (int i = 0; i < 30; i ++) {
                LocalDate date = beginDay.plusDays(i);
                // query daily business data
                BusinessDataVO businessDataVO1 = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                sheet1.getRow(i + 7).getCell(1).setCellValue(date.toString());
                sheet1.getRow(i + 7).getCell(2).setCellValue(businessDataVO1.getTurnover());
                sheet1.getRow(i + 7).getCell(3).setCellValue(businessDataVO1.getValidOrderCount());
                sheet1.getRow(i + 7).getCell(4).setCellValue(businessDataVO1.getOrderCompletionRate());
                sheet1.getRow(i + 7).getCell(5).setCellValue(businessDataVO1.getUnitPrice());
                sheet1.getRow(i + 7).getCell(6).setCellValue(businessDataVO1.getNewUsers());
            }


            // download excel via outputStream
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);

            // excel close
            outputStream.flush();
            outputStream.close();
            excel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);
        return orderMapper.countByMap(map);

    }


}
