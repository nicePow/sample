package com.myprj.sample.sample.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.Format;

@RestController
@RequestMapping("/v1/sample1")
@RequiredArgsConstructor
@Tag(name = "Sample1Controller", description = "테스트1 컨크롤러")
@ApiResponse(responseCode = "200", description = "success")
@ApiResponse(responseCode = "400", description = "faile") // , content = @Content(schema = @Schema(implementation = ErrorResponse.class)
public class Sample1Controller {

    @GetMapping("/test/{id}")
    @Operation(summary = "테스트 조회", description = "테스트 입니다.")
    @Parameter(name = "id", description = "테스트할 id", example = "abc1234")
    public String test(@PathVariable String id){

        return  "test호출 ID: " + id;
    }
}
