package com.jzo2o.market.handler;

import java.time.LocalDateTime;
import java.util.List;

import com.jzo2o.market.enums.ActivityStatusEnum;
import com.jzo2o.market.model.domain.Activity;
import com.jzo2o.market.service.IActivityService;
import com.jzo2o.market.service.ICouponService;
import com.jzo2o.redis.annotations.Lock;
import com.jzo2o.redis.constants.RedisSyncQueueConstants;
import com.jzo2o.redis.sync.SyncManager;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static com.jzo2o.market.constants.RedisConstants.Formatter.*;
import static com.jzo2o.market.constants.RedisConstants.RedisKey.COUPON_SEIZE_SYNC_QUEUE_NAME;

@Component
public class XxlJobHandler {

    @Resource
    private SyncManager syncManager;

    @Resource
    private IActivityService activityService;

    @Resource
    private ICouponService couponService;

    /**
     * 活动状态修改，
     * 1.活动进行中状态修改
     * 2.活动已失效状态修改
     * 1分钟一次
     */
    @XxlJob("updateActivityStatus")
    public void updateActivitySatus(){
        //待生效的活动
        List<Activity> notStartActivities = activityService.queryWithStatus(ActivityStatusEnum.NO_DISTRIBUTE);
        //进行中的活动
        List<Activity> distributingActivities = activityService.queryWithStatus(ActivityStatusEnum.DISTRIBUTING);

        updateActivity(notStartActivities, distributingActivities);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateActivity(List<Activity> notStartActivities, List<Activity> distributingActivities) {
        LocalDateTime now = LocalDateTime.now();
        //对于待生效的活动：到达发放开始时间状态改为“进行中”。
        for (Activity activity : notStartActivities) {
            if (activity.getDistributeStartTime().isBefore(now)) {
                activity.setStatus(ActivityStatusEnum.DISTRIBUTING.getStatus());
            }
            if(activity.getDistributeEndTime().isBefore(now)){
                activity.setStatus(ActivityStatusEnum.LOSE_EFFICACY.getStatus());
            }
        }
        //对于待生效及进行中的活动：到达发放结束时间状态改为“已失效”
        for (Activity activity : distributingActivities) {
            if (activity.getDistributeEndTime().isBefore(now)) {
                activity.setStatus(ActivityStatusEnum.LOSE_EFFICACY.getStatus());
            }
        }
        activityService.updateBatchById(notStartActivities);
        activityService.updateBatchById(distributingActivities);
    }


    /**
     * 已领取优惠券自动过期任务
     */
    @XxlJob("processExpireCoupon")
    public void processExpireCoupon() {

    }


}
