package com.example.sacco_core_banking.classes;

public interface Constants {
    String API_BASE_PATH = "/api";
    String AUTH_PATH = API_BASE_PATH + "/auth";
    String MEMBERS_PATH = API_BASE_PATH + "/members";
    String ADMIN_PATH = API_BASE_PATH + "/admin";
    String USERS_PATH = API_BASE_PATH + "/users";
    String ROLES_PATH = API_BASE_PATH + "/roles";
    String USER_GROUPS_PATH = API_BASE_PATH + "/user-groups";
    String PERMISSIONS_PATH = API_BASE_PATH + "/permissions";
    String USER_ROLES_PATH = API_BASE_PATH + "/user-roles";
    String ROLE_PERMISSIONS_PATH = API_BASE_PATH + "/role-permissions";

    String TEXT_FIELD_REQUIRED = "Input is required for this field!";
    String TEXT_FIELD_UNIQUE = "Input should be unique for this field!";
}
