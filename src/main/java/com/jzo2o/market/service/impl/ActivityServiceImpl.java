package com.jzo2o.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.common.expcetions.BadRequestException;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.common.utils.*;
import com.jzo2o.market.constants.TabTypeConstants;
import com.jzo2o.market.enums.ActivityStatusEnum;
import com.jzo2o.market.mapper.ActivityMapper;
import com.jzo2o.market.model.domain.Activity;
import com.jzo2o.market.model.dto.request.ActivityQueryForPageReqDTO;
import com.jzo2o.market.model.dto.request.ActivitySaveReqDTO;
import com.jzo2o.market.model.dto.response.ActivityInfoResDTO;
import com.jzo2o.market.model.dto.response.SeizeCouponInfoResDTO;
import com.jzo2o.market.service.IActivityService;
import com.jzo2o.market.service.ICouponService;
import com.jzo2o.market.service.ICouponWriteOffService;
import com.jzo2o.mysql.utils.PageUtils;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;

import static com.jzo2o.market.constants.RedisConstants.RedisKey.*;
import static com.jzo2o.market.enums.ActivityStatusEnum.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author itcast
 * @since 2023-09-16
 */
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements IActivityService {
    private static final int MILLION = 1000000;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private ICouponService couponService;

    @Resource
    private ICouponWriteOffService couponWriteOffService;

    @Resource
    private ActivityServiceImpl owner;

    @Override
    public void saveActivity(ActivitySaveReqDTO activitySaveReqDTO) {
        Activity activity = new Activity();
        // 1. 校验参数
        if (activitySaveReqDTO.getId() != null) {
            activity = getById(activitySaveReqDTO.getId());
            if (activity == null) {
                throw new BadRequestException("活动不存在");
            }
        }
        Integer type = activitySaveReqDTO.getType();
        if (type.equals(1)) {
            if (activitySaveReqDTO.getDiscountAmount() == null) {
                throw new BadRequestException("满减活动优惠金额不能为空");
            } else {
                if (activitySaveReqDTO.getDiscountAmount().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new BadRequestException("满减活动优惠金额必须是大于0的整数");
                }
            }
        } else if (type.equals(2)) {
            if (activitySaveReqDTO.getDiscountRate() == null) {
                throw new BadRequestException("折扣活动折扣率不能为空");
            } else {
                //折扣活动折扣率必须是大于0小于100的整数
                if (activitySaveReqDTO.getDiscountRate() <= 0 || activitySaveReqDTO.getDiscountRate() >= 100) {
                    throw new BadRequestException("折扣活动折扣率必须是大于0小于100的整数");
                }
            }
        } else {
            throw new BadRequestException("优惠券类型错误");
        }
        //1.2 发放时间
        LocalDateTime nowTime = LocalDateTime.now();
        LocalDateTime distributeStartTime = activitySaveReqDTO.getDistributeStartTime();
        LocalDateTime distributeEndTime = activitySaveReqDTO.getDistributeEndTime();
        //发放开始时间不能早于当前时间
        if (distributeStartTime.isBefore(nowTime)) {
            throw new BadRequestException("发放开始时间不能早于当前时间");
        }
        //发放结束时间不能早于当前时间
        if (distributeEndTime.isBefore(nowTime)) {
            throw new BadRequestException("发放结束时间不能早于当前时间");
        }
        //1.3 新增修改后的状态为：待生效;
        if (activitySaveReqDTO.getId() == null) {
            activity.setStatus(ActivityStatusEnum.NO_DISTRIBUTE.getStatus());
        }else {
            //到达发放开始时间状态改为“进行中”
            if (nowTime.isAfter(distributeStartTime)) {
                activity.setStatus(ActivityStatusEnum.DISTRIBUTING.getStatus());
            }
            //到达发放结束时间状态改为“已失效”
            if (nowTime.isAfter(distributeEndTime)) {
                activity.setStatus(ActivityStatusEnum.LOSE_EFFICACY.getStatus());
            }
            //撤销后状态为“作废”
            if(activity.getIsDeleted().equals(1)){
                activity.setStatus(ActivityStatusEnum.VOIDED.getStatus());
            }
        }
        // 2. 保存
        BeanUtil.copyProperties(activitySaveReqDTO, activity);
        owner.saveOrUpdate(activity);

    }
}
