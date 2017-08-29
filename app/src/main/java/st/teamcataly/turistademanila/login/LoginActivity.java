package st.teamcataly.turistademanila.login;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.androidhuman.rxfirebase2.auth.RxFirebaseAuth;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.fabric.sdk.android.Fabric;
import st.teamcataly.turistademanila.R;
import st.teamcataly.turistademanila.api.FirebaseApi;
import st.teamcataly.turistademanila.databinding.ActivityLoginBinding;
import st.teamcataly.turistademanila.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();

    public String email;
    public String password;
    private ActivityLoginBinding activityLoginBinding;
    private SweetAlertDialog pDialog;

    public static void start(Context context) {
        Intent starter = new Intent(context, LoginActivity.class);
        starter.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics(), new Answers());
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        activityLoginBinding.setVm(this);


        if (FirebaseApi.getUser() != null && FirebaseApi.getUser().isEmailVerified()) {
            finish();
            MainActivity.start(this);
        }

        activityLoginBinding.password.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == R.id.login || actionId == EditorInfo.IME_NULL) {
                validateAndSignIn(false);
                return true;
            }
            return false;
        });
    }

    public void showLoading() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Authentication")
                .setContentText("Please Wait...");
        pDialog.show();
    }

    public void hideLoading() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismissWithAnimation();
        }
    }

    public void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                pDialog.setContentText("We sent an verification link to your email address, please confirm.");
                pDialog.showCancelButton(false);
            }
        });
    }

    public void signIn() {
        showLoading();
        if (FirebaseApi.getUser() != null) {
            FirebaseAuth.getInstance().signOut();
        }
        RxFirebaseAuth.signInWithEmailAndPassword(FirebaseAuth.getInstance(), email, password)
                .subscribe(user -> {
                    logUser();
                    if (user.isEmailVerified()) {
                        hideLoading();
                        MainActivity.start(this);
                    } else {
                        pDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                        pDialog.setContentText("Your email is not yet verified, please check your email");
                        pDialog.setCancelText("Resend Email");
                        pDialog.setCancelClickListener(sweetAlertDialog -> {
                            sendVerificationEmail(user);
                        });
                        pDialog.setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);
                    }
                }, throwable -> {
                    pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    pDialog.setContentText(throwable.getMessage());
                    Log.e(TAG, "Login Error", throwable);
                });
    }

    public void register() {
        showLoading();
        RxFirebaseAuth.createUserWithEmailAndPassword(FirebaseAuth.getInstance(), email, password)
                .subscribe(user -> {
                    user.sendEmailVerification();
                    sendVerificationEmail(user);
                    logUser();
                }, throwable -> {
                    pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    pDialog.setContentText(throwable.getMessage());
                    Log.e(TAG, "Login Error", throwable);
                });
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void validateAndSignIn(boolean isRegister) {
        // Reset errors.
        activityLoginBinding.email.setError(null);
        activityLoginBinding.password.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            activityLoginBinding.password.setError(getString(R.string.error_invalid_password));
            focusView = activityLoginBinding.password;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            activityLoginBinding.email.setError(getString(R.string.error_field_required));
            focusView = activityLoginBinding.email;
            cancel = true;
        } else if (!isEmailValid(email)) {
            activityLoginBinding.email.setError(getString(R.string.error_invalid_email));
            focusView = activityLoginBinding.email;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            if (isRegister) {
                register();
            } else {
                signIn();
            }
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    Pattern uppercase = Pattern.compile("(?=.*[A-Z])");
    Pattern digit = Pattern.compile("[0-9]");
    Pattern special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");
    Pattern eight = Pattern.compile(".{8}");

    public final boolean isPasswordValid(String password) {
        Matcher hasLetter = uppercase.matcher(password);
        Matcher hasDigit = digit.matcher(password);
        Matcher hasSpecial = special.matcher(password);
//        return hasLetter.find() &&
//                hasDigit.find() &&
//                hasSpecial.find() &&
        return password.length() >= 8;
    }

    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        if (FirebaseApi.getUser() == null) return;
        Crashlytics.setUserIdentifier(FirebaseApi.getUser().getUid());
        Crashlytics.setUserEmail(FirebaseApi.getUser().getEmail());
        Crashlytics.setUserName(FirebaseApi.getUser().getDisplayName());
    }

}
