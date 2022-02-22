package com.vmware.accessmanagement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.vmware.accessmanagement.dto.*;
import com.vmware.accessmanagement.validator.FieldValueExists;

import java.util.List;

public interface UserService extends FieldValueExists {
    UserViewDto getUserWithGroups(String userName);
    List<UserViewDto> getUsers();
    ApiResponseDto createUser(UserDetailDto user);
    UserViewDto updateUser(String userName, UserUpdateDto userDto);
    UserViewDto partiallyUpdateUser(String userName, JsonPatch userDetailPatch) throws JsonPatchException, JsonProcessingException;
    UserViewDto addGroupsToUser(String userName, List<GroupDetailDto> userDto);
    UserViewDto deleteGroupsFromUser(String userName, List<GroupDetailDto> userDto);
    ApiResponseDto deleteUser(String userName);
}
