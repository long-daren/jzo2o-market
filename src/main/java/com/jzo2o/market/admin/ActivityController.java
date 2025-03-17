package com.jzo2o.market.admin;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jzo2o.common.model.PageResult;
import com.jzo2o.market.model.dto.request.ActivityQueryForPageReqDTO;
import com.jzo2o.market.model.dto.request.ActivitySaveReqDTO;
import com.jzo2o.market.model.dto.response.ActivityInfoResDTO;
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
    /**
     * 分页查询优惠券活动接口
     *
     * @param activityQueryForPageReqDTO
     */
    @ApiOperation("分页查询优惠券活动接口")
    @GetMapping("/page")
    public PageResult<ActivityInfoResDTO> pageQueryActivity(ActivityQueryForPageReqDTO activityQueryForPageReqDTO) {
        return activityService.pageQueryActivity(activityQueryForPageReqDTO);
    }

    /**
     * 查询优惠券活动详情接口
     * @param id
     * @return
     */
    @ApiOperation("查询优惠券活动详情接口")
    @GetMapping("/{id}")
    public ActivityInfoResDTO getActivityDetail(@PathVariable Long id) {
        return activityService.getActivityDetail(id);
    }

    @ApiOperation("撤销优惠券活动接口")
    @PostMapping("/revoke/{id}")
    public void revokeActivity(@PathVariable Long id) {
        activityService.revokeActivity(id);
    }



}
