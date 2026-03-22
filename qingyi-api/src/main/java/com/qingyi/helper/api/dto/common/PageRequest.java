package com.qingyi.helper.api.dto.common;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 分页请求
 */
@Data
public class PageRequest {

    @Min(value = 1, message = "当前页不能小于1")
    private Integer current = 1;

    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer size = 10;
}
