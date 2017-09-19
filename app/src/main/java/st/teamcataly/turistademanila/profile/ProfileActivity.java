package st.teamcataly.turistademanila.profile;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.androidhuman.rxfirebase2.database.RxFirebaseDatabase;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import st.teamcataly.turistademanila.R;
import st.teamcataly.turistademanila.api.FirebaseApi;
import st.teamcataly.turistademanila.data.UserProfile;
import st.teamcataly.turistademanila.databinding.ProfileActivityBinding;

public class ProfileActivity extends AppCompatActivity {

    ProfileActivityBinding profileActivityBinding;
    private DatabaseReference myProfile;
    private UserProfile userProfile;
    private String downloadUrl;
    private ImageView mIvProfileImage;

    public static void start(Context context) {
        Intent starter = new Intent(context, ProfileActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileActivityBinding = DataBindingUtil.setContentView(this, R.layout.profile_activity);
        profileActivityBinding.setVm(this);
        mIvProfileImage = profileActivityBinding.profileImage;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myProfile = FirebaseApi.getInstance().getDatabase().getReference("user_profile").child(FirebaseApi.getUser().getUid());
        Glide.with(this).load(R.drawable.busy).centerCrop().into(profileActivityBinding.banner);
        loadProfile();
    }

    public void loadProfile() {
        RxFirebaseDatabase.dataOf(myProfile, UserProfile.class).toObservable()
                .doOnError(throwable -> createProfile())
                .map(userProfile -> {
                    if (TextUtils.isEmpty(userProfile.profileUri)) {
                        return new UserProfile.Builder(userProfile)
                                .profileUri("http://industribune.net/wp-content/uploads/2015/02/industribune-default-no-profile-pic.jpg")
                                .build();
                    }
                    return userProfile;
                })
                .subscribe(userProfile -> {
                    this.userProfile = userProfile;
                    profileActivityBinding.setUserProfile(userProfile);
                }, throwable -> {
                    Log.e("WTF", "WTF", throwable);
                });
    }

    private void createProfile() {
        RxFirebaseDatabase.setValue(myProfile,
                new UserProfile.Builder()
                        .name("John Doe")
                        .aboutMe("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt. Duis leo. Sed fringilla mauris sit amet nibh. Donec sodales sagittis magna. Sed consequat, leo eget bibendum sodales, augue velit cursus nunc,")
                        .profileUri("http://industribune.net/wp-content/uploads/2015/02/industribune-default-no-profile-pic.jpg")
                        .build())
                .subscribe(this::loadProfile, throwable -> {
                    Log.e("WTF", "WTF", throwable);
                });
    }

    public void editName() {
        Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.edit_text_dialog);
        dialog.setCancelable(true);
        Button updateButton = (Button) dialog.findViewById(R.id.confirm_button);
        EditText value = (EditText) dialog.findViewById(R.id.value);
        updateButton.setOnClickListener(v -> {
            dialog.dismiss();
            RxFirebaseDatabase.setValue(myProfile,
                    userProfile = new UserProfile.Builder(userProfile)
                            .name(value.getText().toString())
                            .build())
                    .subscribe(this::loadProfile, throwable -> {
                        Log.e("WTF", "WTF", throwable);
                    });
        });
        //now that the dialog is set up, it's time to show it
        dialog.show();
    }

    public void editContact() {
        Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.edit_text_dialog);
        dialog.setCancelable(true);
        Button updateButton = (Button) dialog.findViewById(R.id.confirm_button);
        EditText value = (EditText) dialog.findViewById(R.id.value);
        updateButton.setOnClickListener(v -> {
            dialog.dismiss();
            RxFirebaseDatabase.setValue(myProfile,
                    userProfile = new UserProfile.Builder(userProfile)
                            .contactNumber(value.getText().toString())
                            .build())
                    .subscribe(this::loadProfile, throwable -> {
                        Log.e("WTF", "WTF", throwable);
                    });
        });
        //now that the dialog is set up, it's time to show it
        dialog.show();
    }

    public void editAboutMe() {
        Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.edit_text_dialog);
        dialog.setCancelable(true);
        Button updateButton = (Button) dialog.findViewById(R.id.confirm_button);
        EditText value = (EditText) dialog.findViewById(R.id.value);
        updateButton.setOnClickListener(v -> {
            dialog.dismiss();
            RxFirebaseDatabase.setValue(myProfile,
                    userProfile = new UserProfile.Builder(userProfile)
                            .aboutMe(value.getText().toString())
                            .build())
                    .subscribe(this::loadProfile, throwable -> {
                        Log.e("WTF", "WTF", throwable);
                    });
        });
        //now that the dialog is set up, it's time to show it
        dialog.show();
    }

    public void onProfileImageClicked() {
        EasyImage.openChooserWithGallery(this, "Upload profile picture", 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagePicked(File file, EasyImage.ImageSource imageSource, int type) {
                Uri uri = Uri.fromFile(file);
                StorageReference riversRef = FirebaseStorage
                        .getInstance()
                        .getReference()
                        .child("images/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/profile.jpg");
                riversRef.putFile(uri)
                        .addOnSuccessListener(taskSnapshot -> {
                            // Get a URL to the uploaded content
                            downloadUrl = taskSnapshot.getDownloadUrl().toString();
                            myProfile.child("profileUri").setValue(downloadUrl);
                            Glide.with(ProfileActivity.this).load(downloadUrl).into(mIvProfileImage);
                        })
                        .addOnFailureListener(exception -> {

                        });
            }
        });
    }
}
