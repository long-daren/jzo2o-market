package com.jzo2o.market.service;

import com.jzo2o.common.model.PageResult;
import com.jzo2o.market.model.domain.Activity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.market.model.dto.request.ActivityQueryForPageReqDTO;
import com.jzo2o.market.model.dto.request.ActivitySaveReqDTO;
import com.jzo2o.market.model.dto.response.ActivityInfoResDTO;
import com.jzo2o.market.model.dto.response.SeizeCouponInfoResDTO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author itcast
 * @since 2023-09-16
 */
public interface IActivityService extends IService<Activity> {

    void saveActivity(ActivitySaveReqDTO activitySaveReqDTO);
    PageResult<ActivityInfoResDTO> pageQueryActivity(ActivityQueryForPageReqDTO activityQueryForPageReqDTO);
    /**
     * 查询优惠券活动详情
     * @param id
     * @return
     */
    ActivityInfoResDTO getActivityDetail(Long id);
    /**
     * 撤销优惠券活动
     * @param id
     */
    void revokeActivity(Long id);


}
