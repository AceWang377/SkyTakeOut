package com.sky.service;

import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;

public interface ReportService {

    /**
     * turnover report
     */
    TurnoverReportVO getTurnoverReport(LocalDate begin, LocalDate end);

}
