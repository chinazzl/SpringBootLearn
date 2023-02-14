package org.basic.entity;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2022/12/4 17:08
 * @Description: 返回值构造
 */
public interface ResultBuilder {

    default ResponseEntity<Result<?>> success(Object data) {
        return ResponseEntity.ok(Result.builder()
                .status(HttpStatus.OK.value())
                .data(data)
                .build());
    }

    default ResponseEntity<Result<?>> badRequest(String errMsg) {
        return ResponseEntity.badRequest().body(Result.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(errMsg)
                .build());
    }

    /**
     * 404的构造
     *
     * @param errorMsg 错误信息
     * @return Result
     */
    default ResponseEntity<Result<?>> notFound(String errorMsg) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND.value())
                .body(Result.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .message(errorMsg)
                        .build());
    }

    /**
     * 500的构造
     *
     * @param errorMsg 错误信息
     * @return Result
     */
    default ResponseEntity<Result<?>> internalServerError(String errorMsg) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(Result.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message(errorMsg)
                        .build());
    }

}
