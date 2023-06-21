package com.joshcummings.codeplay.terracotta.security;

public enum Policy {

    DIRECTORY_BROWSING("0"),
    CROSS_SITE_SCRIPTING("40012,40014,40016,40017"),
    SQL_INJECTION("40018"),
    PATH_TRAVERSAL("6"),
    REMOTE_FILE_INCLUSION("7"),
    SERVER_SIDE_INCLUDE("40009"),
    SERVER_SIDE_CODE_INJECTION("90019"),
    REMOTE_OS_COMMAND_INSPECTION("90020"),
    EXTERNAL_REDIRECT("20019"),
    CRLF_INJECTION("40003")
    ;
    //SOURCE_CODE_DISCLOSURE("42,10045,20017"),
    //SHELL_SHOCK( "10048"),
    //REMOTE_CODE_EXECUTION("20018"),
    //LDAP_INJECTION("40015"),
    //XPATH_INJECTION("90021"),
    //XML_EXTERNAL_ENTITY("90023"),
    //PADDING_ORACLE("90024"),
    //EL_INJECTION("90025"),
    //INSECURE_HTTP_METHODS("90028"),
    //PARAMETER_POLLUTION("20014")

    private final String scannerIds;

    Policy(String scannerIds) {
        this.scannerIds = scannerIds;
    }

    public String getScannerIds() {
        return scannerIds;
    }
}
