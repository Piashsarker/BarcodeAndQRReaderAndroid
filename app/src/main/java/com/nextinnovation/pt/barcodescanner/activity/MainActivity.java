package com.nextinnovation.pt.barcodescanner.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.nextinnovation.pt.barcodescanner.R;
import com.nextinnovation.pt.barcodescanner.database.DatabaseHelper;
import com.nextinnovation.pt.barcodescanner.fragment.BarcodeFragment;
import com.nextinnovation.pt.barcodescanner.fragment.LicenseFragment;
import com.nextinnovation.pt.barcodescanner.fragment.ProductListFragment;
import com.nextinnovation.pt.barcodescanner.model.Product;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements BarcodeFragment.ScanRequest {

    public static final String BARCODE_KEY = "BARCODE";
    private final String TAG = MainActivity.class.getSimpleName();
    private final int MY_PERMISSION_REQUEST_CAMERA = 1001;
    private Context context;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private Barcode barcodeResult;

    private final ActivityResultLauncher<Intent> scannerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() != RESULT_OK) {
                            return;
                        }

                        Intent data = result.getData();

                        if (data == null) {
                            return;
                        }

                        String value = data.getStringExtra(
                                ScannerActivity.EXTRA_SCAN_VALUE
                        );

                        if (value != null && !value.isEmpty()) {
                            showDialog(value, getScanTime(), getScanDate());
                        }
                    }
            );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BarcodeFragment(), "Barcode Scanner");
        adapter.addFragment(new ProductListFragment(), "Scan Item");
        viewPager.setAdapter(adapter);
    }

    public String getScanTime() {
        DateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return timeFormat.format(new Date());
    }

    public String getScanDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    @Override
    public void scanBarcode() {
        Intent intent = new Intent(this, ScannerActivity.class);
        scannerLauncher.launch(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.item_share:
                openShare();
                break;
            case R.id.item_rate_app:
                openRate();
                break;
            case R.id.item_submit_bug:
                openSubmitBug();
                break;
            case R.id.item_license:
                openLisence();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openSubmitBug() {
        String to = "sarkerpt@gmail.com";
        String subject = "Barcode Reader For Android - Bug Report";

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    private void openRate() {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    private void openShare() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        String appLink = "https://play.google.com/store/apps/details?id=" + context.getPackageName();
        sharingIntent.setType("text/plain");
        String shareBodyText = "Check Out The Cool Barcode Reader App \n Link: " + appLink + " \n" +
                " #Barcode #Android";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Barcode Reader Android App");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
        startActivity(Intent.createChooser(sharingIntent, "Share"));
    }

    private void openLisence() {
        LicenseFragment licensesFragment = new LicenseFragment();
        licensesFragment.show(getSupportFragmentManager().beginTransaction(), "dialog_licenses");
    }

    private void showDialog(final String scanContent, final String currentTime, final String currentDate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage(scanContent)
                .setTitle(R.string.dialog_title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DatabaseHelper databaseHelper = new DatabaseHelper(context);
                databaseHelper.addProduct(new Product(scanContent, currentTime, currentDate));
                Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                viewPager.setCurrentItem(1);


            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(MainActivity.this, "Not Saved", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are You Sure? ")
                .setTitle(R.string.exit_title);
        builder.setPositiveButton(R.string.ok_title, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                MainActivity.this.finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }


        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }


        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
