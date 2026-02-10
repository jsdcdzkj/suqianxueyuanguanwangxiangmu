package com.jsdc.iotpt.dto.chintcloud;

import lombok.Data;

/**
 * 泰杰赛智慧照明系统 Token
 */

@Data
public class TokenDto {

    /**
     * 请求Token
     */
    private TokenContent accessToken;

    /**
     * 刷新Token
     */
    private TokenContent refreshToken;


    @Data
    public static class TokenContent {

        /**
         * token内容
         */
        private String tokenContent;

        /**
         * 过期时间 yyyy-MM-dd HH:mm:ss
         */
        private String expires;

    }

}
