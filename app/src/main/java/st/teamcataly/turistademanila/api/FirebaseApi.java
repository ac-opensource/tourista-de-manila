package st.teamcataly.turistademanila.api;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

/**
 * @author A-Ar Andrew Concepcion
 * @createdOn 25/07/2017
 */
public class FirebaseApi {
    private static final FirebaseApi ourInstance = new FirebaseApi();
    private final FirebaseDatabase firebaseDatabase;

    public static FirebaseApi getInstance() {
        return ourInstance;
    }

    public static FirebaseUser getUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public FirebaseDatabase getDatabase() {
        return firebaseDatabase;
    }

    private FirebaseApi() {
        firebaseDatabase = FirebaseDatabase.getInstance();
    }
}
