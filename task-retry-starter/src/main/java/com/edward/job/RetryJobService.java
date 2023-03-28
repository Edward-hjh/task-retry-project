package com.edward.job;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: hejh
 * @Date: 2023/3/28 15:41
 */
@Slf4j
public class RetryJobService {

    private static final String BLANK = "blank";

    @Resource
    TaskBaseService taskBaseService;

    /**
     * 重发定时任务
     *
     * @param ids 记录ID,为空即查询需要推送的、最近三天的数据
     * @return ReturnT<String>
     */
    @XxlJob(value = "taskRetryJob")
    public ReturnT<String> retryJob(String ids) {
        List<Long> idList = null;
        if (StringUtils.isNotBlank(ids) && !BLANK.equals(ids)) {
            idList = Arrays.stream(ids.split(",")).map(e -> Long.parseLong(e.trim())).collect(Collectors.toList());
        }
        try {
            taskBaseService.retryJob(idList);
        } catch (Exception e) {
            return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 删除表前 KEEP_DAY 天历史数据
     *
     * @return ReturnT<String>
     */
    @XxlJob(value = "removeHistory")
    public ReturnT<String> removeHistory() {
        try {
            taskBaseService.removeHistory();
        } catch (Exception e) {
            return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
        }
        return ReturnT.SUCCESS;
    }
}
