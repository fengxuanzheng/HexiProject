package com.EnergyProject.server;

import com.EnergyProject.pojo.ApplyReport;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 魔法少女
 * @since 2021-07-14
 */
public interface APPLYREPORTervice extends IService<ApplyReport> {
    public Integer insterApplyReport(ApplyReport applyReport);

    public Map<String, Object> selectStatus(Integer currentPage, Integer pageSize);

    public Integer updataApplyReportDataStatus(Integer[] selectId, String selectMode);
    public Integer singleApplyReportDataStatus(ApplyReport applyReport);

    IPage<ApplyReport> getAllApplyReport(Integer page, Integer size);

    IPage<ApplyReport> selectOfApplyUsername(Map<String,Map<String,Object>> accurateSelect,Integer page, Integer size);


    List<String> getAllUsername();

    Integer deleteApplyReport(Integer id);

    Integer deleteAllApplyReport(List<Integer> ids);

    ApplyReport updataApplyReport(Integer id);

    Integer updataApplyReportFinall(ApplyReport applyReport);

    IPage<ApplyReport> getAllApplyReportForReject(Integer page, Integer size);

    Integer sendRejectText(Map<String,Object> rejectText, Integer id);

    Integer agreeRejectApplyReportForComment(Integer id);

    List<ApplyReport> getApplyReportOfAmountData(LocalDateTime startTime, LocalDateTime endTime);

    List<ApplyReport> getFirstThreeDaysOfApplyReportOfUnReviewed();

    Page<ApplyReport> getRejectModeData(Integer page, Integer size);
}
