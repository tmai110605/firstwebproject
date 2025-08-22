package murach.data;

import murach.business.User;
import java.util.ArrayList;
import java.util.List;

public class UserDB {
    private static final List<User> users = new ArrayList<>();
    public static void insert(User user) { users.add(user); }
    public static List<User> selectAll() { return new ArrayList<>(users); }
}
