package cyber.bletarget.ui.home;

import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import cyber.bletarget.BeaconManager;
import cyber.bletarget.R;

public class HomeFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.i("TAG1", "HomeFragment.onCreateView()");

        View v = inflater.inflate(R.layout.fragment_home, container, false);


        final WebView mWebView = (WebView) v.findViewById(R.id.webview_home);


        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser

        mWebView.setWebViewClient(new MyWebViewClient(mWebView));
//        mWebView.loadUrl("https://google.com");
                mWebView.loadUrl("https://c.jako.pro/cyber/circles/alessio/circle_loop_realtime.html");

        return v;
    }


    class MyWebViewClient extends WebViewClient {
        private WebView webView;

        public MyWebViewClient(WebView webView) {

            this.webView = webView;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            webView.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
            handler.proceed();
        }
    }
}
