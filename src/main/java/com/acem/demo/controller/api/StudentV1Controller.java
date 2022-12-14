package com.acem.demo.controller.api;

import com.acem.demo.builder.ResponseBuilder;
import com.acem.demo.constant.RegexConstant;
import com.acem.demo.controller.Controller;
import com.acem.demo.exception.ExceptionHandler;
import com.acem.demo.request.StudentSaveRequest;
import com.acem.demo.request.StudentUpdateRequest;
import com.acem.demo.request.mapper.StudentMapperUtil;
import com.acem.demo.response.Response;
import com.acem.demo.service.StudentService;
import com.acem.demo.utils.InputStreamMapperUtil;
import com.acem.demo.utils.ValidationUtil;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class StudentV1Controller extends Controller {

    @Inject
    private StudentService studentService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        ExceptionHandler.handleWithFallBack(
                () -> {
                    String url = request.getRequestURL().toString();
                    String[] urlTokenized = url.split("/");
                    String id = urlTokenized[urlTokenized.length - 1];
                    Response responseBody = null;
                    if (id.matches(RegexConstant.isNumber)) {
                        responseBody = studentService.getById(Long.parseLong(id));
                    } else {
                        responseBody = studentService.getAll();
                    }
                    buildResponse(response, responseBody);
                },
                () -> buildResponse(response, ResponseBuilder.serverError())
        );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ExceptionHandler.handleWithFallBack(
                () -> {
                    StudentSaveRequest studentSaveRequest = InputStreamMapperUtil
                            .mapToObject(request.getInputStream(), StudentSaveRequest.class);

                    Optional<List<String>> violations = ValidationUtil.validate(studentSaveRequest);

                    Response responseBody = null;
                    if (violations.isPresent()) {
                        responseBody = ResponseBuilder.validationFailed(violations.get());
                    } else {
                        responseBody = studentService.save(StudentMapperUtil.mapStudent(studentSaveRequest));
                    }
                    buildResponse(response, responseBody);
                },
                () -> buildResponse(response, ResponseBuilder.serverError())
        );
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ExceptionHandler.handleWithFallBack(
                () -> {
                    StudentUpdateRequest studentUpdateRequest = InputStreamMapperUtil
                            .mapToObject(request.getInputStream(), StudentUpdateRequest.class);

                    Optional<List<String>> violations = ValidationUtil.validate(studentUpdateRequest);

                    Response responseBody = null;
                    if (violations.isPresent()) {
                        responseBody = ResponseBuilder.validationFailed(violations.get());
                    } else {
                        responseBody = studentService.update(StudentMapperUtil.mapStudent(studentUpdateRequest));
                    }
                    buildResponse(response, responseBody);
                },
                () -> buildResponse(response, ResponseBuilder.serverError())
        );
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ExceptionHandler.handleWithFallBack(
                () -> {
                    String url = request.getRequestURL().toString();
                    String[] urlTokenized = url.split("/");
                    String id = urlTokenized[urlTokenized.length - 1];
                    Response responseBody = null;
                    if (id.matches(RegexConstant.isNumber)) {
                        responseBody = studentService.delete(Long.parseLong(id));
                    } else {
                        responseBody = ResponseBuilder.invalidPathParameter();
                    }
                    buildResponse(response, responseBody);
                },
                () -> buildResponse(response, ResponseBuilder.serverError())
        );
    }
}
