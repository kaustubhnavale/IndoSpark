package mipl.indospark;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewPages extends AppCompatActivity {

    WebView wvPages;
    ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_pages);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.themecolor)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        wvPages = (WebView) findViewById(R.id.wvPages);

        wvPages.getSettings().setJavaScriptEnabled(true);
        wvPages.getSettings().setDomStorageEnabled(true);
        wvPages.getSettings().setDatabaseEnabled(true);
//        wvPages.getSettings().setDatabasePath(dbpath); //check the documentation for info about dbpath
        wvPages.getSettings().setMinimumFontSize(1);
        wvPages.getSettings().setMinimumLogicalFontSize(1);

        String page = getIntent().getStringExtra("WebView");
        setTitle(page);

        if (page.equals("Enquiry")) {
            wvPages.loadUrl("https://shop.indospark.com/index.php/enquiry.html");
        } else if (page.equals("About Us")) {
            wvPages.loadUrl("https://shop.indospark.com/index.php/about-us.html");
        } else if (page.equals("Contact Us")) {
            wvPages.loadUrl("https://shop.indospark.com/index.php/contact-us.html");
        } else if (page.equals("Terms and Condition")) {
            wvPages.loadUrl("https://shop.indospark.com/terms-and-condition");
        } else if (page.equals("Privacy Policy")) {
            wvPages.loadUrl("https://shop.indospark.com/privacy-policy");
        }

        progressBar = new ProgressDialog(WebViewPages.this);
        progressBar.setMessage("Please wait...");
        progressBar.setCancelable(false);

        wvPages.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (!progressBar.isShowing()) {
                    progressBar.show();
                }
            }

            public void onPageFinished(WebView view, String url) {
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}