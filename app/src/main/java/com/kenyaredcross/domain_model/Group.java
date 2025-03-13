package com.kenyaredcross.domain_model;

import java.util.List;

public class Group {
    private String groupId;
    private String groupName;
    private List<Member> members;

    public Group() {
        // Default constructor required for Firebase
    }

    public Group(String groupId, String groupName, List<Member> members) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.members = members;
    }

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

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }
}