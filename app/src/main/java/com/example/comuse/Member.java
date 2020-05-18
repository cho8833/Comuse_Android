package com.example.comuse;

public class Member {

    private String name;            // 멤버의 이름, FirebaseUser.name 저장

    /*
        Member class의 고유데이터
        FireStore Database 의 User 의 데이터를 저장할 때 document name 을 uid 로 저장한다.
        Schedule 을 생성할 때 작성자 구분을 위해 professorName property 에 uid 를 저장한다.
     */
    private String uid;

    private Boolean inoutStatus;    // 멤버의 inout 상터태
    private String position;        // 멤버의 포지션, Setting에서 edit 가능

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