package pl.kcworks.simplegymlog.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import pl.kcworks.simplegymlog.BuildConfig;
import pl.kcworks.simplegymlog.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_settings);
        setTheme(R.style.PreferenceScreen);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_frame_layout, new SettingsFragment())
                .commit();

    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            buttonSetUp();
        }

        private void buttonSetUp() {
            Preference aboutAppButton = findPreference(getString(R.string.ABOUT_APP_BUTTON));
            aboutAppButton.setOnPreferenceClickListener(this);

            Preference openSourceInfoButton = findPreference(getString(R.string.OPEN_SOURCE_LICENSES_BUTTON));
            openSourceInfoButton.setOnPreferenceClickListener(this);

        }

        private void showAboutAppDialog() {
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_about_app, null);

            TextView versionNameTextView = dialogView.findViewById(R.id.tv_version_name);
            versionNameTextView.setText(BuildConfig.VERSION_NAME);

            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
            dialogBuilder.setView(dialogView)
                    .setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        }

        private void showOpenLicensesInfoDialog() {
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_open_licenses_info, null);

            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
            dialogBuilder.setView(dialogView)
                    .setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            switch (preference.getKey()) {
                case "ABOUT_APP_BUTTON":
                    showAboutAppDialog();
                    break;
                case "OPEN_SOURCE_LICENSES_BUTTON":
                    showOpenLicensesInfoDialog();
                    break;
            }
            return false;
        }
    }

}