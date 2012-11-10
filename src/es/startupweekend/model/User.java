package es.startupweekend.model;

import java.util.List;

public class User {

    public static final int PIGGY_TYPE_SAVING = 0;
    public static final int PIGGY_TYPE_OBJETIVE = 1;
    public static final int PIGGY_TYPE_GIFT = 2;
    public static final int PIGGY_TYPE_SHARED = 3;
    public static final int PIGGY_TYPE_CAIXA = 4;
    public static final int PIGGY_TYPE_OTHER = 5;

    public String getUserId() {
        return userId;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getCategory() {
        return category;
    }


    public void setCategory(String category) {
        this.category = category;
    }


    public String getImageRaw() {
        return imageRaw;
    }


    public void setImageRaw(String imageRaw) {
        this.imageRaw = imageRaw;
    }


    public String getExtraData() {
        return extraData;
    }


    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }


    public List<String> getConnections() {
        return connections;
    }


    public void setConnections(List<String> connections) {
        this.connections = connections;
    }


    private String userId;
    public User(String userId, String name, String category, String imageRaw, String extraData, List<String> connections) {
        super();
        this.userId = userId;
        this.name = name;
        this.category = category;
        this.imageRaw = imageRaw;
        this.extraData = extraData;
        this.connections = connections;
    }


    private String name;
    private String category;
    private String imageRaw;
    private String extraData ;
    private List<String> connections;
    

    
}
