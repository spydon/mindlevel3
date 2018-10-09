package net.mindlevel.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.mindlevel.R;
import net.mindlevel.api.ControllerCallback;
import net.mindlevel.api.IntegrationController;
import net.mindlevel.impl.Glassbar;
import net.mindlevel.model.Integration;
import net.mindlevel.util.CoordinatorUtil;
import net.mindlevel.util.PreferencesUtil;

public class IntegrationActivity extends AppCompatActivity {

    private View view;
    private IntegrationController integrationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integration);
        view = findViewById(R.id.scroll);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        integrationController = new IntegrationController(view.getContext());
        final EditText password = findViewById(R.id.password);

        Button finishedButton = findViewById(R.id.integration_finished);
        finishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                integrationController.get(password.getText().toString(), integrationCallback);
            }
        });

        Button defaultButton = findViewById(R.id.integration_default);
        defaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                integrationController.get(getString(R.string.integration_default), integrationCallback);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private ControllerCallback<Integration> integrationCallback = new ControllerCallback<Integration>() {

        @Override
        public void onPostExecute(final Boolean success, final Integration integration) {
            String integrationText;
            Context context = getBaseContext();
            if (success) {
                PreferencesUtil.setIntegration(context, integration.db);
                if (!integration.pass.equals("default")) {
                    integrationText = context.getString(R.string.integration_successful, integration.pass);
                } else {
                    integrationText = context.getString(R.string.integration_normal);
                }
                Intent data = new Intent();
                data.setData(Uri.parse(integrationText));
                setResult(RESULT_OK, data);
                finish();
            } else {
                integrationText = context.getString(R.string.integration_failed);
                Glassbar.make(view, integrationText, Snackbar.LENGTH_LONG).show();
            }
        }
    };
}
