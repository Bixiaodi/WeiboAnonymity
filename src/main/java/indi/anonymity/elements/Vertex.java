package indi.anonymity.elements;

/**
 * Created by emily on 17/2/14.
 */
public class Vertex extends BaseVertex {

    private String urlId;
    private String userName;
    private int gender;
    private String location;
    private String description;
    private String userTag;
    private String educationInformation;
    private String code64;
    private String code32;
    private String code16;
    private String code8;
    private String groupId;

    public static final int TOTAL = 169246;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void resetGroupId() {
        this.groupId = "";
    }

    public String getUrlId() {
        return urlId;
    }

    public void setUrlId(String urlId) {
        this.urlId = urlId == null ? "" : urlId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? "" : userName;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location == null ? "" : location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description;
    }

    public String getUserTag() {
        return userTag;
    }

    public void setUserTag(String userTag) {
        this.userTag = (userTag == null ? "" : userTag);
    }

    public String getEducationInformation() {
        return educationInformation;
    }

    public void setEducationInformation(String educationInformation) {
        this.educationInformation = educationInformation == null ? "" : educationInformation;
    }

    public String getCode64() {
        return code64;
    }

    public void setCode64(String code64) {
        this.code64 = code64;
    }

    public String getCode32() {
        return code32;
    }

    public void setCode32(String code32) {
        this.code32 = code32;
    }

    public String getCode16() {
        return code16;
    }

    public void setCode16(String code16) {
        this.code16 = code16;
    }

    public String getCode8() {
        return code8;
    }

    public void setCode8(String code8) {
        this.code8 = code8;
    }

    public String toString() {
        return id + " " + urlId + " " + gender + " " + location + " " + description + " " + userTag + " " + educationInformation;
    }
    public String[] toArray() {
        return new String[]{String.valueOf(id), urlId, String.valueOf(gender), location, description, userTag, educationInformation};
    }

    public boolean equals(Vertex vertex) {
        return this.getId() == vertex.getId();
    }
}
