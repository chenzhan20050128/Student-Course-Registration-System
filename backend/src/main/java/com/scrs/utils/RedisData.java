package com.scrs.utils;/*
 * @date 11/28 21:21
 */

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RedisData {
    private LocalDateTime expireTime;

    private Object data;
}
