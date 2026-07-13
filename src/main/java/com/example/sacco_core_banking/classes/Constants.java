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
    String WORKFLOWS_PATH = API_BASE_PATH + "/workflows";
    String WORKFLOW_STAGES_PATH = API_BASE_PATH + "/workflow-stages";
    String WORKFLOW_TRANSITIONS_PATH = API_BASE_PATH + "/workflow-transitions";
    String WORKFLOW_MAPPINGS_PATH = API_BASE_PATH + "/workflow-mappings";
    String WORKFLOW_INSTANCES_PATH = API_BASE_PATH + "/workflow-instances";
    String WORKFLOW_INSTANCE_HISTORY_PATH = API_BASE_PATH + "/workflow-instance-history";
    String WORKFLOW_STATUSES_PATH = API_BASE_PATH + "/workflow-statuses";
    String WORKFLOW_STATES_PATH = API_BASE_PATH + "/workflow-states";
    String WORKFLOW_STAGE_ACTIONS_PATH = API_BASE_PATH + "/workflow-stage-actions";
    String LOANS_PATH = API_BASE_PATH + "/loans";
    String LOAN_PRODUCTS_PATH = API_BASE_PATH + "/loan-products";
    String CHECKLISTS_PATH = API_BASE_PATH + "/checklists";
    String SAVINGS_PATH = API_BASE_PATH + "/savings";
    String SAVINGS_PRODUCTS_PATH = API_BASE_PATH + "/savings-products";

    String TEXT_FIELD_REQUIRED = "Input is required for this field!";
    String TEXT_FIELD_UNIQUE = "Input should be unique for this field!";
}
