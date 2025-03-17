package com.jzo2o.market.admin;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jzo2o.market.model.dto.request.ActivitySaveReqDTO;
import com.jzo2o.market.service.IActivityService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController("adminActivityController")
@RequestMapping("/operation/activity")
@Api(tags = "优惠券活动相关接口")
public class ActivityController {
    @Resource
    private IActivityService activityService;

    /**
     * 保存优惠券活动接口
     * @param activitySaveReqDTO
     */
    @ApiOperation("保存优惠券活动接口")
    @PostMapping("/save")
    public void saveActivity(@RequestBody ActivitySaveReqDTO activitySaveReqDTO) {
        activityService.saveActivity(activitySaveReqDTO);
    }
}
