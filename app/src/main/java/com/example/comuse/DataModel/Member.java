package com.example.comuse.DataModel;

public class Member {

    private String name;            // 멤버의 이름
    private String uid;             // 멤버의 uid, FirebaseUser.Uid를 저장, 고유데이터
    private Boolean inoutStatus;          // 멤버의 inout
    private String position;        // 멤버의 포지션, Setting에서 edit 가

    //MARK: -Constructor, getter, setter
    public Member() {
    }
    public Member(String name, String uid, Boolean inoutStatus, String position) {
        this.name = name;
        this.uid = uid;
        this.inoutStatus = inoutStatus;
        this.position = position;
    }
    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public Boolean getInoutStatus() {
        return inoutStatus;
    }

    public void setInoutStatus(Boolean inoutStatus) {
        this.inoutStatus = inoutStatus;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}