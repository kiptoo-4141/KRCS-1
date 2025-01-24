package com.kenyaredcross.domain_model;

public class GroupsCheckModel {

    GroupsCheckModel(){

    }

    String groupId, groupName, email, username;

    public GroupsCheckModel(String groupId, String username, String email, String groupName) {
        this.groupId = groupId;
        this.username = username;
        this.email = email;
        this.groupName = groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
