package com.jzo2o.market.service.impl;

import cn.hutool.db.DbRuntimeException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.api.market.dto.request.CouponUseBackReqDTO;
import com.jzo2o.api.market.dto.request.CouponUseReqDTO;
import com.jzo2o.api.market.dto.response.AvailableCouponsResDTO;
import com.jzo2o.api.market.dto.response.CouponUseResDTO;
import com.jzo2o.common.expcetions.BadRequestException;
import com.jzo2o.common.expcetions.CommonException;
import com.jzo2o.common.expcetions.DBException;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.common.utils.*;
import com.jzo2o.market.enums.ActivityStatusEnum;
import com.jzo2o.market.enums.CouponStatusEnum;
import com.jzo2o.market.mapper.CouponMapper;
import com.jzo2o.market.model.domain.Activity;
import com.jzo2o.market.model.domain.Coupon;
import com.jzo2o.market.model.domain.CouponWriteOff;
import com.jzo2o.market.model.dto.request.CouponOperationPageQueryReqDTO;
import com.jzo2o.market.model.dto.request.MyCouponReqDTO;
import com.jzo2o.market.model.dto.request.SeizeCouponReqDTO;
import com.jzo2o.market.model.dto.response.ActivityInfoResDTO;
import com.jzo2o.market.model.dto.response.CouponInfoResDTO;
import com.jzo2o.market.service.IActivityService;
import com.jzo2o.market.service.ICouponService;
import com.jzo2o.market.service.ICouponUseBackService;
import com.jzo2o.market.service.ICouponWriteOffService;
import com.jzo2o.market.utils.CouponUtils;
import com.jzo2o.mvc.utils.UserContext;
import com.jzo2o.mysql.utils.PageUtils;
import com.jzo2o.redis.utils.RedisSyncQueueUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.jzo2o.common.constants.ErrorInfo.Code.SEIZE_COUPON_FAILD;
import static com.jzo2o.market.constants.RedisConstants.RedisKey.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author itcast
 * @since 2023-09-16
 */
@Service
@Slf4j
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements ICouponService {

    @Resource(name = "seizeCouponScript")
    private DefaultRedisScript<String> seizeCouponScript;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private IActivityService activityService;

    @Resource
    private ICouponUseBackService couponUseBackService;

    @Resource
    private ICouponWriteOffService couponWriteOffService;


    /**
     * 查询领取记录
     *
     * @param couponOperationPageQueryReqDTO
     * @return
     */
    @Override
    public PageResult<CouponInfoResDTO> pageQueryCoupon(CouponOperationPageQueryReqDTO couponOperationPageQueryReqDTO) {
        Page<Coupon> page = new Page<>(couponOperationPageQueryReqDTO.getPageNo(), couponOperationPageQueryReqDTO.getPageSize());
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<Coupon>()
            .eq(BeanUtils.isNotEmpty(couponOperationPageQueryReqDTO.getActivityId()), Coupon::getActivityId, couponOperationPageQueryReqDTO.getActivityId());
        this.page(page, wrapper);
        List<CouponInfoResDTO> collect = page.getRecords().stream().map(coupon -> BeanUtils.toBean(coupon, CouponInfoResDTO.class)).collect(Collectors.toList());
        PageResult<CouponInfoResDTO> pageResult = new PageResult<>();
        pageResult.setList(collect);
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    /**
     * 领取优惠券
     *
     * @param myCouponReqDTO
     */
    @Override
    public CouponInfoResDTO myCoupon(MyCouponReqDTO myCouponReqDTO) {
        Long UserId= UserContext.currentUserId();
        //查询用户领取的优惠券
        Coupon coupon = this.getOne(new LambdaQueryWrapper<Coupon>()
            .eq(Coupon::getUserId, UserId)
            .eq(Coupon::getStatus, myCouponReqDTO.getStatus()));
        return BeanUtils.toBean(coupon, CouponInfoResDTO.class);
    }
}
