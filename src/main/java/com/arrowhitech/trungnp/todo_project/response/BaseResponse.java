package com.arrowhitech.trungnp.todo_project.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BaseResponse<T> {
    private int status;
    private String message;
    private T data;
}
