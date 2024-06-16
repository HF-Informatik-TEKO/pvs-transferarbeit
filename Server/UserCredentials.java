package Server;

public class UserCredentials {
    private final String userName;
    private final String password;
    
    public UserCredentials(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    @Override
    /** Automatically generated hasCode method */
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((userName == null) ? 0 : userName.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        var typed = (UserCredentials) obj;

        if (userName == null) {
            if (typed.userName != null) {
                return false;
            }
        } else if (!userName.equals(typed.userName)) {
            return false;
        }

        if (password == null) {
            if (typed.password != null) {
                return false;
            }
        } else if (!password.equals(typed.password)) {
            return false;
        }

        return true;
    }
}
