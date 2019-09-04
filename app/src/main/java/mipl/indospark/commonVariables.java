package mipl.indospark;

import android.app.ProgressDialog;
import android.content.Context;

public class commonVariables {

    public static String imagePath = "https://shop.indospark.com/pub/media/catalog/product";

    public static ProgressDialog showProgressDialog(Context context, String message){
        ProgressDialog m_Dialog = new ProgressDialog(context);
        m_Dialog.setMessage(message);
        m_Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_Dialog.setCancelable(false);
        m_Dialog.show();
        return m_Dialog;
    }

    public static final String mypreference = "login";
    public static final String token = "token";
    public static final String Name = "name";
    public static final String Email = "email";
    public static final String customer_id = "customer_id";
}