package st.teamcataly.turistademanila.data;

/**
 * @author A-Ar Andrew Concepcion
 * @createdOn 03/07/2017
 */
public class UserProfile {

    public UserProfile() {
    }

    public String profileUri;
    public String name;
    public String contactNumber;
    public String aboutMe;

    private UserProfile(Builder builder) {
        profileUri = builder.profileUri;
        name = builder.name;
        contactNumber = builder.contactNumber;
        aboutMe = builder.aboutMe;
    }

    public static final class Builder {
        private String profileUri;
        private String name;
        private String contactNumber;
        private String aboutMe;

        public Builder() {
        }

        public Builder(UserProfile copy) {
            this.profileUri = copy.profileUri;
            this.name = copy.name;
            this.contactNumber = copy.contactNumber;
            this.aboutMe = copy.aboutMe;
        }

        public Builder profileUri(String val) {
            profileUri = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder contactNumber(String val) {
            contactNumber = val;
            return this;
        }

        public Builder aboutMe(String val) {
            aboutMe = val;
            return this;
        }

        public UserProfile build() {
            return new UserProfile(this);
        }
    }
}
