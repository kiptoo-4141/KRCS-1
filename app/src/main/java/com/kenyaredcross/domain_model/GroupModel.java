package com.kenyaredcross.domain_model;

import java.util.HashMap;
import java.util.Map;

public class GroupModel {
    private String groupId;
    private String groupName;
    private Map<String, VolunteerModel> members;

    public GroupModel() {
        // Default constructor required for Firebase
    }

    public GroupModel(String groupId, String groupName, Map<String, VolunteerModel> members) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.members = members;
    }

    // Getters and Setters
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Map<String, VolunteerModel> getMembers() {
        return members;
    }

    public void setMembers(Map<String, VolunteerModel> members) {
        this.members = members;
    }
}