package es.startupweekend.api;

import java.util.List;

import es.startupweekend.model.User;

public interface MeetmeApiInterface {

    public boolean registerUser(String userId, String name, String category, String imgRaw, String extraData);

    public List<String> getConnections(String userId);

    public List<User> getUsers();

    public User getUser(String userId);

    public boolean addConnection(String userId1, String userId2);

}
