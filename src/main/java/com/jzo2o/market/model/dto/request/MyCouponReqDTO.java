package com.jzo2o.market.model.dto.request;

import javax.validation.constraints.Null;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("我的优惠券查询模型")
public class MyCouponReqDTO {
    @ApiModelProperty(value = "优惠券状态",required = true)
    @Null(message = "请先选择优惠券状态")
    private Integer status;

    @ApiModelProperty(value = "上一次查询最后一张优惠券id",required = false)
    private Long id;
}
