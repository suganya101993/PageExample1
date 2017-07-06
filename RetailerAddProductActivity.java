package com.wish.registry.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wish.registry.R;
import com.wish.registry.utilclasses.AppController;
import com.wish.registry.utilclasses.Constants;
import com.wish.registry.utilclasses.NetworkConnectionManager;
import com.wish.registry.utilclasses.PrefManager;
import com.wish.registry.utilclasses.SmileyRemover;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


import mindmade.materiladesign.permission.AppPermissions;

public class RetailerAddProductActivity extends AppCompatActivity implements View.OnClickListener {

    EditText retailer_add_prodTitleET, retail_add_prodDescrET, retail_add_prodPriceET,retail_add_prodStoreET,retail_add_prodLocationET,retail_add_prodWebET;
    TextView retailer_ap_toolbar_title_TV,retailer_add_prodTitleErrorTV,retail_add_prodDescrErrorTV, retail_add_prodPriceErrorTV, retail_add_prodStoreErrorTV,retail_add_prodLocationErrorTV,retail_add_prodWebErrorTV;
    ImageView retailer_add_prod_IV;
    Button retailer_addProdConfirmBtn,retailer_addProdCancelBtn;
    Toolbar retailerAddProductToolbar;
    LinearLayout activity_retailAdd_product;
    ProgressBar retailer_apConfirmProgressbar,retailer_apCancelProgressbar,retailer_ap_imageProgressbar,retailer_loading_progressbar;
    SmileyRemover smileyRemover;
    LinearLayout linear;
    NetworkConnectionManager connectionManager;
    RequestQueue queue;
    Dialog dialog;
    private AppPermissions mRuntimePermission;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1,id;
    String userChoosenTask,product,key,editUrl,Url,responsestr;
    private String encodedImageString;
    private Uri fileUri;
    PrefManager sessinManager;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "Images";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_add_product);

        /*Tootlbar initialization
        *
        *   <<<<Start>>>>*/
        //Getting Product id
       //  productID = getIntent().getStringExtra("product_id");
         key=getIntent().getStringExtra("key");
      //   Log.d("success","productid"+productID);
         Log.d("success","key"+key);
        try {
            product = getIntent().getStringExtra("product_id");
            Log.d("SSSS1", "LLLL1" + product);

            id = Integer.parseInt(product);
            Log.d("SSSSid", "LLLL2" + id);
        } catch (Exception ex) {
            Log.d("Error", "" + ex);
        }

        retailerAddProductToolbar = (Toolbar) findViewById(R.id.retailer_add_product_toolbar);
        retailer_ap_toolbar_title_TV = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(retailerAddProductToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        retailer_ap_toolbar_title_TV.setText(getString(R.string.retialer_add_product));
        retailerAddProductToolbar.setNavigationIcon(R.drawable.ic_back_arrow);

        retailerAddProductToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

         /*<<<<END>>>*/

        retailer_add_prod_IV= (ImageView) findViewById(R.id.retailer_add_prod_IV);
        retailer_add_prodTitleET = (EditText) findViewById(R.id.retailer_add_prod_title_ET);
        retail_add_prodDescrET = (EditText) findViewById(R.id.retail_add_prod_descr_ET);
        retail_add_prodPriceET = (EditText) findViewById(R.id.retail_add_prod_price_ET);
        retail_add_prodStoreET = (EditText) findViewById(R.id.retail_add_prod_store_ET);
        retail_add_prodLocationET = (EditText) findViewById(R.id.retail_add_prod_location_ET);
        retail_add_prodWebET = (EditText) findViewById(R.id.retail_add_prod_web_ET);

        linear=(LinearLayout)findViewById(R.id.linear);
        retailer_add_prodTitleErrorTV = (TextView) findViewById(R.id.retailer_add_prod_title_error_TV);
        retail_add_prodDescrErrorTV = (TextView) findViewById(R.id.retail_add_prod_descr_error_TV);
        retail_add_prodPriceErrorTV = (TextView) findViewById(R.id.retail_add_prod_price_error_TV);
        retail_add_prodStoreErrorTV = (TextView) findViewById(R.id.retail_add_prod_store_error_TV);
        retail_add_prodLocationErrorTV = (TextView) findViewById(R.id.retail_add_prod_location_error_TV);
        retail_add_prodWebErrorTV = (TextView) findViewById(R.id.retail_add_prod_web_error_TV);
        activity_retailAdd_product = (LinearLayout) findViewById(R.id.activity_retail_add_product);
        retailer_apConfirmProgressbar = (ProgressBar) findViewById(R.id.retailer_ap_confirm_loading_progressbar);
        retailer_apCancelProgressbar = (ProgressBar) findViewById(R.id.retailer_ap_cancel_loading_progressbar);
        retailer_ap_imageProgressbar = (ProgressBar) findViewById(R.id.retailer_ap_image_loading_progressbar);
        retailer_loading_progressbar =(ProgressBar)findViewById(R.id.retailer_loading_progressbar);

        queue = AppController.getInstance().getRequestQueue();
        smileyRemover = new SmileyRemover();
        connectionManager = new NetworkConnectionManager(this);
        mRuntimePermission = new AppPermissions(this);
        sessinManager = new PrefManager(this);

        retailer_add_prodTitleET.setFilters(new InputFilter[]{smileyRemover,new InputFilter.LengthFilter(30)});
        retail_add_prodDescrET.setFilters(new InputFilter[]{smileyRemover,new InputFilter.LengthFilter(50)});
        retail_add_prodPriceET.setFilters(new InputFilter[]{smileyRemover,new InputFilter.LengthFilter(10)});
        retail_add_prodStoreET.setFilters(new InputFilter[]{smileyRemover,new InputFilter.LengthFilter(50)});
        retail_add_prodLocationET.setFilters(new InputFilter[]{smileyRemover,new InputFilter.LengthFilter(30)});
        retail_add_prodWebET.setFilters(new InputFilter[]{smileyRemover,new InputFilter.LengthFilter(200)});

        AddTextChangeClass(retailer_add_prodTitleET);
        AddTextChangeClass(retail_add_prodDescrET);
        AddTextChangeClass(retail_add_prodPriceET);
        AddTextChangeClass(retail_add_prodStoreET);
        AddTextChangeClass(retail_add_prodLocationET);
        AddTextChangeClass(retail_add_prodWebET);

    try {
        if (key.equals("edit")) {

            editUrl = Constants.LIVE_URL + Constants.RETAILER_EDIT + Constants.RETAILER_ID_PARAM + new PrefManager(getApplicationContext()).getSessionIntValue(Constants.SESSION_USERID) + "&" +
                    Constants.PRODUCT_ID_PARAM + product + "&" +
                    Constants.RETAILER_ACTION;
          // setDisable();
            retailer_loading_progressbar.setVisibility(View.VISIBLE);
            retailer_ap_imageProgressbar.setVisibility(View.VISIBLE);
            linear.setVisibility(View.GONE);
            Log.d("success", "editurl" + editUrl);
            loadwishListDataFromApi(editUrl);
        }
    }
    catch (Exception e){
        Log.d("Error",""+e);
    }

        retailer_addProdConfirmBtn = (Button)findViewById(R.id.confirm_btn);
        retailer_addProdCancelBtn = (Button)findViewById(R.id.cancel_btn);

        retailer_addProdConfirmBtn.setOnClickListener(this);
        retailer_addProdCancelBtn.setOnClickListener(this);
        retailer_add_prod_IV.setOnClickListener(this);
        retail_add_prodLocationET.setOnClickListener(this);

    }

    private void loadwishListDataFromApi(String url) {
        if (connectionManager.isConnectingToInternet()) {
            Log.d("Success", "Edit URL:::: " + url);
            JsonObjectRequest wishlistJsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                   //setEnable();
                    retailer_loading_progressbar.setVisibility(View.GONE);
                    retailer_ap_imageProgressbar.setVisibility(View.GONE);
                    linear.setVisibility(View.VISIBLE);
                    Log.d("Success", "Edit_response:::: " + response);
                    try {
                        if (response.getInt(Constants.STATUS_KEY) == 1) {
                            JSONArray jsonarray=response.getJSONArray("Retailer_product_list");
                            for (int i = 0; i < jsonarray.length(); i++) {

                                JSONObject object = jsonarray.getJSONObject(i);
                                retailer_add_prodTitleET.setText(object.getString("name"));
                                retail_add_prodDescrET.setText(object.getString("description"));
                                retail_add_prodPriceET.setText(object.getString("price"));
                                retail_add_prodStoreET.setText(object.getString("store"));
                                retail_add_prodLocationET.setText(object.getString("location"));
                                retail_add_prodWebET.setText(object.getString("website_url"));
                                String image=object.getString("image");
                                Glide.with(getApplicationContext()).load(image)
                                        .thumbnail(0.5f)
                                        .crossFade()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .placeholder(R.drawable.ic_take_photo)
                                        .into(retailer_add_prod_IV);
                            }
                        }else
                        {
                            Toast.makeText(getApplicationContext(),response.getString("message"),Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),"success"+e,Toast.LENGTH_SHORT).show();
                        Log.e("Exception", "" + e);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    retailer_ap_imageProgressbar.setVisibility(View.GONE);
                    retailer_loading_progressbar.setVisibility(View.GONE);
                    linear.setVisibility(View.VISIBLE);
                  // setEnable();
                    VolleyLog.d("Error: " + error.getMessage());
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Toast.makeText(getApplicationContext(), R.string.timeoutError, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(getApplicationContext(), R.string.authenticationError, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(getApplicationContext(), R.string.serverError, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(getApplicationContext(), R.string.networkError, Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(getApplicationContext(), R.string.parseError, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            int socketTimeout = 40000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            wishlistJsonObjectRequest.setRetryPolicy(policy);
            AppController.getInstance().addToRequestQueue(wishlistJsonObjectRequest);
        } else {
            retailer_loading_progressbar.setVisibility(View.GONE);
            retailer_ap_imageProgressbar.setVisibility(View.GONE);
            linear.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(),"success No network",Toast.LENGTH_SHORT).show();
        }
    }

    public void AddTextChangeClass(EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    retailer_add_prodTitleET.setBackground(ContextCompat.getDrawable(RetailerAddProductActivity.this, R.drawable.registry_edit_text_bg));
                    retail_add_prodDescrET.setBackground(ContextCompat.getDrawable(RetailerAddProductActivity.this, R.drawable.registry_edit_text_bg));
                    retail_add_prodPriceET.setBackground(ContextCompat.getDrawable(RetailerAddProductActivity.this, R.drawable.registry_edit_text_bg));
                    retail_add_prodStoreET.setBackground(ContextCompat.getDrawable(RetailerAddProductActivity.this, R.drawable.registry_edit_text_bg));
                    retail_add_prodLocationET.setBackground(ContextCompat.getDrawable(RetailerAddProductActivity.this, R.drawable.registry_edit_text_bg));
                    retail_add_prodWebET.setBackground(ContextCompat.getDrawable(RetailerAddProductActivity.this, R.drawable.registry_edit_text_bg));

                    retailer_add_prodTitleErrorTV.setVisibility(View.INVISIBLE);
                    retail_add_prodDescrErrorTV.setVisibility(View.INVISIBLE);
                    retail_add_prodPriceErrorTV.setVisibility(View.INVISIBLE);
                    retail_add_prodStoreErrorTV.setVisibility(View.INVISIBLE);
                    retail_add_prodLocationErrorTV.setVisibility(View.INVISIBLE);
                    retail_add_prodWebErrorTV.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == retailer_addProdConfirmBtn) {
            if (retailer_add_prodTitleET.getText().toString().trim().length() == 0 || retail_add_prodDescrET.getText().toString().trim().length() == 0
                    || retail_add_prodPriceET.getText().toString().trim().length() == 0 || retail_add_prodStoreET.getText().toString().trim().length() == 0
                    || retail_add_prodLocationET.getText().toString().trim().length() == 0 || retail_add_prodWebET.getText().toString().trim().length() == 0) {
                if (retailer_add_prodTitleET.getText().toString().trim().length() == 0 && retail_add_prodDescrET.getText().toString().trim().length() == 0
                        && retail_add_prodPriceET.getText().toString().trim().length() == 0 && retail_add_prodStoreET.getText().toString().trim().length() == 0
                        && retail_add_prodLocationET.getText().toString().trim().length() == 0 && retail_add_prodWebET.getText().toString().trim().length() == 0) {
                    Log.e("Error", "Please enter email id and password");
                    retailer_add_prodTitleET.setBackground(ContextCompat.getDrawable(this, R.drawable.seterror_change_bg));
                    retail_add_prodDescrET.setBackground(ContextCompat.getDrawable(this, R.drawable.seterror_change_bg));
                    retail_add_prodPriceET.setBackground(ContextCompat.getDrawable(this, R.drawable.seterror_change_bg));
                    retail_add_prodStoreET.setBackground(ContextCompat.getDrawable(this, R.drawable.seterror_change_bg));
                    retail_add_prodLocationET.setBackground(ContextCompat.getDrawable(this, R.drawable.seterror_change_bg));
                    retail_add_prodWebET.setBackground(ContextCompat.getDrawable(this, R.drawable.seterror_change_bg));

                    retailer_add_prodTitleErrorTV.setVisibility(View.VISIBLE);
                    retailer_add_prodTitleErrorTV.setText(getString(R.string.title_required));
                    retail_add_prodDescrErrorTV.setVisibility(View.VISIBLE);
                    retail_add_prodDescrErrorTV.setText(getString(R.string.desc_required));
                    retail_add_prodPriceErrorTV.setVisibility(View.VISIBLE);
                    retail_add_prodPriceErrorTV.setText(getString(R.string.price_required));
                    retail_add_prodStoreErrorTV.setVisibility(View.VISIBLE);
                    retail_add_prodStoreErrorTV.setText(getString(R.string.store_required));
                    retail_add_prodLocationErrorTV.setVisibility(View.VISIBLE);
                    retail_add_prodLocationErrorTV.setText(getString(R.string.location_required));
                    retail_add_prodWebErrorTV.setVisibility(View.VISIBLE);
                    retail_add_prodWebErrorTV.setText(getString(R.string.web_required));

                } else if (retailer_add_prodTitleET.getText().toString().trim().length() == 0) {
                    Log.e("Error", "Please enter email id");
                    retailer_add_prodTitleET.setBackground(ContextCompat.getDrawable(this, R.drawable.seterror_change_bg));
                    retailer_add_prodTitleErrorTV.setVisibility(View.VISIBLE);
                    retailer_add_prodTitleErrorTV.setText(getString(R.string.title_required));
                } else if (retail_add_prodDescrET.getText().toString().trim().length() == 0) {
                    Log.e("Error", "Please enter password");
                    retail_add_prodDescrET.setBackground(ContextCompat.getDrawable(this, R.drawable.seterror_change_bg));
                    retail_add_prodDescrErrorTV.setVisibility(View.VISIBLE);
                    retail_add_prodDescrErrorTV.setText(getString(R.string.desc_required));
                } else if (retail_add_prodPriceET.getText().toString().trim().length() == 0) {
                    Log.e("Error", "Please enter password");
                    retail_add_prodPriceET.setBackground(ContextCompat.getDrawable(this, R.drawable.seterror_change_bg));
                    retail_add_prodPriceErrorTV.setVisibility(View.VISIBLE);
                    retail_add_prodPriceErrorTV.setText(getString(R.string.price_required));
                } else if (retail_add_prodStoreET.getText().toString().trim().length() == 0) {
                    Log.e("Error", "Please enter password");
                    retail_add_prodStoreET.setBackground(ContextCompat.getDrawable(this, R.drawable.seterror_change_bg));
                    retail_add_prodStoreErrorTV.setVisibility(View.VISIBLE);
                    retail_add_prodStoreErrorTV.setText(getString(R.string.store_required));
                } else if (v == retail_add_prodLocationET) {
                    Log.e("Error", "Please enter password");
                    retail_add_prodLocationET.setBackground(ContextCompat.getDrawable(this, R.drawable.seterror_change_bg));
                    retail_add_prodLocationErrorTV.setVisibility(View.VISIBLE);
                    retail_add_prodLocationErrorTV.setText(getString(R.string.location_required));
                } else if (retail_add_prodWebET.getText().toString().trim().length() == 0) {
                    Log.e("Error", "Please enter password");
                    retail_add_prodWebET.setBackground(ContextCompat.getDrawable(this, R.drawable.seterror_change_bg));
                    retail_add_prodWebErrorTV.setVisibility(View.VISIBLE);
                    retail_add_prodWebErrorTV.setText(getString(R.string.web_required));
                }

            }
            else {

                if (!Patterns.WEB_URL.matcher(retail_add_prodWebET.getText().toString().trim()).matches()) {
                    Log.e("Error", "Not valid email id");
                    retail_add_prodWebET.setBackground(ContextCompat.getDrawable(this, R.drawable.seterror_change_bg));
                    retail_add_prodWebErrorTV.setVisibility(View.VISIBLE);
                    retail_add_prodWebErrorTV.setText(getString(R.string.valid_web_url));
                }
//                else if (!retail_add_prodWebET.getText().toString().trim().matches(Constants.WEBURL_REGULAR_EX)) {
//                    retail_add_prodWebET.setBackground(ContextCompat.getDrawable(this, R.drawable.seterror_change_bg));
//                    retail_add_prodWebErrorTV.setVisibility(View.VISIBLE);
//                    retail_add_prodWebErrorTV.setText(R.string.Weburl_valid_str);
//                }
                else {
                    Log.d("Success", "Value get successfully");
                    try {
                        if (key.equals("nav")) {
                            Url = Constants.LIVE_URL + Constants.RETAILER_ADD_PROD_URL;
                            setDisable();
                            Log.d("success", "" + Url);
                            addRetailerProduct(Url);
                        } else {
                            Url = Constants.LIVE_URL + Constants.RETAILER_EDIT;
                            setDisable();
                            Log.d("success", "" + Url);
                            updateRetailerProduct(Url);
                        }
                    } catch (Exception e) {
                        Log.d("Error", "" + e);
                    }
                }
            }

           } else if (v == retailer_add_prod_IV) {
            selectImage();
        }
        else if (v == retail_add_prodLocationET) {
                Intent mapIntent = new Intent(RetailerAddProductActivity.this, MapActivity.class);
                startActivity(mapIntent);
            }
        else {
            finish();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.STORAGE_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
              //      mRuntimePermission.requestPermission(Constants.WRITE_STORAGE_PERMISSIONS, Constants.STORAGE_REQUEST_CODE);
                } else {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                }
                break;
            case Constants.READ_STORAGE_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
             //         mRuntimePermission.requestPermission(Constants.READ_STORAGE_PERMISSIONS, Constants.READ_STORAGE_REQUEST_CODE);
                } else {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                }
                break;
            case Constants.CAMERA_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
              //      mRuntimePermission.requestPermission(Constants.CAMERA_PERMISSIONS, Constants.CAMERA_REQUEST_CODE);
                } else {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                }
                break;
        }
    }
    /*Image pick section
     * 1. Pick from camera
     * 2.Pick from Galley*/
                                        /*START*/
    private void selectImage() {
        final String[] items = Constants.SELECT_IMAGE_OPTIONS;

        AlertDialog.Builder builder = new AlertDialog.Builder(RetailerAddProductActivity.this);
        builder.setTitle(Constants.ADD_PHOTO_TITLE);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(items[0])) {
                    userChoosenTask = items[0];
                    cameraIntent();
                } else if (items[item].equals(items[1])) {
                    userChoosenTask = items[1];
                    if (mRuntimePermission.hasPermission(Constants.READ_STORAGE_PERMISSIONS)) {
                        if (mRuntimePermission.hasPermission(Constants.WRITE_STORAGE_PERMISSIONS)) {
                            galleryIntent();
                        } else {
                            Toast.makeText(RetailerAddProductActivity.this, "Plz allow gift registry to access storage permission", Toast.LENGTH_SHORT).show();
                            mRuntimePermission.requestPermission(Constants.WRITE_STORAGE_PERMISSIONS, Constants.STORAGE_REQUEST_CODE);
                        }
                    } else {
                        Toast.makeText(RetailerAddProductActivity.this, "Plz allow gift registry to access storage permission", Toast.LENGTH_SHORT).show();
                        mRuntimePermission.requestPermission(Constants.READ_STORAGE_PERMISSIONS, Constants.READ_STORAGE_REQUEST_CODE);
                    }
                } else if (items[item].equals(items[2])) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        if (mRuntimePermission.hasPermission(Constants.READ_STORAGE_PERMISSIONS)) {
            if (mRuntimePermission.hasPermission(Constants.WRITE_STORAGE_PERMISSIONS)) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_FILE);
            } else {
                Toast.makeText(RetailerAddProductActivity.this, "Plz allow gift registry to access storage permission", Toast.LENGTH_SHORT).show();
                mRuntimePermission.requestPermission(Constants.WRITE_STORAGE_PERMISSIONS, Constants.STORAGE_REQUEST_CODE);

            }
        } else {
            Toast.makeText(RetailerAddProductActivity.this, "Plz allow gift registry to access storage permission", Toast.LENGTH_SHORT).show();
            mRuntimePermission.requestPermission(Constants.READ_STORAGE_PERMISSIONS, Constants.READ_STORAGE_REQUEST_CODE);
        }

    }

    private void cameraIntent() {
        if (mRuntimePermission.hasPermission(Constants.CAMERA_PERMISSIONS)) {
//            if (mRuntimePermission.hasPermission(Constants.READ_STORAGE_PERMISSIONS)) {
//                if (mRuntimePermission.hasPermission(Constants.WRITE_STORAGE_PERMISSIONS)) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(intent, REQUEST_CAMERA);
//                } else {
//                    mRuntimePermission.requestPermission(Constants.WRITE_STORAGE_PERMISSIONS, Constants.STORAGE_REQUEST_CODE);
//                }
//            } else {
//                mRuntimePermission.requestPermission(Constants.READ_STORAGE_PERMISSIONS, Constants.READ_STORAGE_REQUEST_CODE);
//            }
        }

        else {
            Toast.makeText(RetailerAddProductActivity.this, "Plz allow gift registry to access storage permission", Toast.LENGTH_SHORT).show();
            mRuntimePermission.requestPermission(Constants.CAMERA_PERMISSIONS, Constants.CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }


    private void onCaptureImageResult(Intent data) {

        try {
            String imageUri = fileUri.getPath();
            imageUri = compressImage(imageUri);
            encodedImageString = getBase64(imageUri);
            Glide.with(this)
                    .load(compressImage(imageUri))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.ic_take_photo)
                    .into(retailer_add_prod_IV);
        } catch (Exception e) {
            Log.e("Error onCapture", "" + e);
        }

    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        try {
            Uri selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String imageUri = c.getString(columnIndex);
            c.close();
            imageUri = compressImage(imageUri);
            encodedImageString = getBase64(imageUri);
            Glide.with(this)
                    .load(compressImage(imageUri))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.ic_take_photo)
                    .into(retailer_add_prod_IV);
        } catch (Exception e) {
            Log.e("Errror onSelect", "" + e);
        }


    }

    private String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;


        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);

            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filename;
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    private String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(),  Constants.APP_NAME + "/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;
    }
    private String getBase64(String imagepathUplaod) {
        //  Bitmap bitmap = ((BitmapDrawable) createRegProfileIV.getDrawable()).getBitmap();
        Bitmap bm = BitmapFactory.decodeFile(imagepathUplaod);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public Uri getOutputMediaFileUri(int type) {

        File fileToReturn = getOutputMediaFile(type);
        return fileToReturn != null ? Uri.fromFile(fileToReturn) :
                null;

    }

    private File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    /*END*/
    private void callNextIntent(Class intentclass) {
        Intent nextIntent = new Intent(this, intentclass);
        startActivity(nextIntent);
        finishAffinity();
    }

    public String urlencoder(String data) {
        try {
            String returnData = URLEncoder.encode(data, "utf-8");
            return returnData;
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }

    public void addRetailerProduct(String url) {
        if (connectionManager.isConnectingToInternet()) {
            clearQueue();
            Log.d("Success", "retaileraddproduct:::: " + url);

            JsonObjectRequest registerJsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url, new JSONObject(getParams()), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    setEnable();
                    try {
                        Log.d("Success", "Response:::: " + response);
                        if (response.getInt(Constants.STATUS_KEY) == 1) {
                            deleteDirectory();
                            // Log.d("Success", "" + response.getInt(Constants.STATUS_KEY));
                            sessinManager.storeLocationnValue(Constants.LOCATION_SESSION_NAME, Constants.ADDRESS_KEY, "Click to change location");
                            sessinManager.storeLocationnValue(Constants.LOCATION_SESSION_NAME, Constants.LATITUDE_KEY, "");
                            sessinManager.storeLocationnValue(Constants.LOCATION_SESSION_NAME, Constants.LONGITUDE_KEY, "");

                            retailer_add_prodTitleET.setText("");
                            retail_add_prodDescrET.setText("");
                            retail_add_prodPriceET.setText("");
                            retail_add_prodStoreET.setText("");
                            retail_add_prodLocationET.setText("");
                            retail_add_prodWebET.setText("");
                            //Toast.makeText(RetailerAddProductActivity.this, ""+response.getString(Constants.MESSAGE_KEY), Toast.LENGTH_SHORT).show();
                            responsestr=response.getString(Constants.MESSAGE_KEY);
                            displaySuccessDialog();

                            //callNextIntent(RetailerProductsActivity.class);
                        }

                        else if (response.getInt(Constants.STATUS_KEY) == 2) {
                            // Log.d("Success", "" + response.getInt(Constants.STATUS_KEY));
                            retailer_add_prodTitleET.setBackground(ContextCompat.getDrawable(RetailerAddProductActivity.this, R.drawable.seterror_change_bg));
                            retailer_add_prodTitleErrorTV.setVisibility(View.VISIBLE);
                            retailer_add_prodTitleErrorTV.setText(response.getString(Constants.MESSAGE_KEY));
                        }
                        else if (response.getInt(Constants.STATUS_KEY) == 0) {
                            Toast.makeText(RetailerAddProductActivity.this, ""+response.getString(Constants.MESSAGE_KEY), Toast.LENGTH_SHORT).show();
                            Log.d("Success", "" + response.getInt(Constants.MESSAGE_KEY));
                        }
                        else {
                            Log.e("Failure", "" + response.getString(Constants.MESSAGE_KEY));
                        }
                    } catch (Exception ex) {
                        Log.e("Exception", "" + ex.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    setEnable();
                    VolleyLog.d("Error: ","" + error);
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Log.e("Error", "Time out" + getString(R.string.timeoutError));
                    } else if (error instanceof AuthFailureError) {
                        Log.e("Error", "Authentication" + getString(R.string.authenticationError));
                    } else if (error instanceof ServerError) {
                        Log.e("Error", "serverError" + getString(R.string.serverError));
                    } else if (error instanceof NetworkError) {
                        Log.e("Error", "networkError" + getString(R.string.networkError));
                    } else if (error instanceof ParseError) {
                        Log.e("Error", "parseError" + getString(R.string.parseError));
                    }
                }
            });
            int socketTimeout = 40000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            registerJsonObjectRequest.setRetryPolicy(policy);
            AppController.getInstance().addToRequestQueue(registerJsonObjectRequest);
        } else {
            setEnable();
            Log.e("Error", "" + getString(R.string.netUnavailable));
        }
    }




    public void updateRetailerProduct(String url) {
        if (connectionManager.isConnectingToInternet()) {
            clearQueue();
            Log.d("Success", "retaileraddproduct:::: " + url);

            JsonObjectRequest updateJsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url, new JSONObject(getparam()), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    setEnable();
                    try {
                        Log.d("Success", "Response:::: " + response);
                        if (response.getInt(Constants.STATUS_KEY) == 1) {

                            sessinManager.storeLocationnValue(Constants.LOCATION_SESSION_NAME, Constants.ADDRESS_KEY, "Click to change location");
                            sessinManager.storeLocationnValue(Constants.LOCATION_SESSION_NAME, Constants.LATITUDE_KEY, "");
                            sessinManager.storeLocationnValue(Constants.LOCATION_SESSION_NAME, Constants.LONGITUDE_KEY, "");
                            retailer_add_prodTitleET.setText("");
                            retail_add_prodDescrET.setText("");
                            retail_add_prodPriceET.setText("");
                            retail_add_prodStoreET.setText("");
                            retail_add_prodLocationET.setText("");
                            retail_add_prodWebET.setText("");

                            //Toast.makeText(RetailerAddProductActivity.this, ""+response.getString(Constants.MESSAGE_KEY), Toast.LENGTH_SHORT).show();
                            responsestr=response.getString(Constants.MESSAGE_KEY);
                            displaySuccessDialog();
                            deleteDirectory();
                            //callNextIntent(RetailerProductsActivity.class);
                        }

                        else if (response.getInt(Constants.STATUS_KEY) == 2) {
                            // Log.d("Success", "" + response.getInt(Constants.STATUS_KEY));
                            retailer_add_prodTitleET.setBackground(ContextCompat.getDrawable(RetailerAddProductActivity.this, R.drawable.seterror_change_bg));
                            retailer_add_prodTitleErrorTV.setVisibility(View.VISIBLE);
                            retailer_add_prodTitleErrorTV.setText(response.getString(Constants.MESSAGE_KEY));
                        }
                        else if (response.getInt(Constants.STATUS_KEY) == 0) {
                            Toast.makeText(RetailerAddProductActivity.this, ""+response.getString(Constants.MESSAGE_KEY), Toast.LENGTH_SHORT).show();
                            Log.d("Success", "" + response.getInt(Constants.MESSAGE_KEY));
                        }
                        else {
                            Log.e("Failure", "" + response.getString(Constants.MESSAGE_KEY));
                        }
                    } catch (Exception ex) {
                        Log.e("Exception", "" + ex.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    setEnable();
                    VolleyLog.d("Error: ","" + error);
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Log.e("Error", "Time out" + getString(R.string.timeoutError));
                    } else if (error instanceof AuthFailureError) {
                        Log.e("Error", "Authentication" + getString(R.string.authenticationError));
                    } else if (error instanceof ServerError) {
                        Log.e("Error", "serverError" + getString(R.string.serverError));
                    } else if (error instanceof NetworkError) {
                        Log.e("Error", "networkError" + getString(R.string.networkError));
                    } else if (error instanceof ParseError) {
                        Log.e("Error", "parseError" + getString(R.string.parseError));
                    }
                }
            });
            int socketTimeout = 40000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            updateJsonObjectRequest.setRetryPolicy(policy);
            AppController.getInstance().addToRequestQueue(updateJsonObjectRequest);

        } else {
            setEnable();
            Log.e("Error", "" + getString(R.string.netUnavailable));
        }
    }
    public void setEnable() {
        retailer_apConfirmProgressbar.setVisibility(View.GONE);
        retailer_addProdConfirmBtn.setVisibility(View.VISIBLE);
        retailer_add_prodTitleET.setEnabled(true);
        retail_add_prodDescrET.setEnabled(true);
        retail_add_prodPriceET.setEnabled(true);
        retail_add_prodStoreET.setEnabled(true);
        retail_add_prodPriceET.setEnabled(true);
        retail_add_prodLocationET.setEnabled(true);
        retail_add_prodWebET.setEnabled(true);
        retailer_add_prod_IV.setClickable(true);
    }

    public void setDisable() {
        retailer_add_prod_IV.setClickable(false);
        retailer_apConfirmProgressbar.setVisibility(View.VISIBLE);
        retailer_addProdConfirmBtn.setVisibility(View.GONE);
        retailer_add_prodTitleET.setEnabled(false);
        retail_add_prodDescrET.setEnabled(false);
        retail_add_prodPriceET.setEnabled(false);
        retail_add_prodStoreET.setEnabled(false);
        retail_add_prodPriceET.setEnabled(false);
        retail_add_prodLocationET.setEnabled(false);
        retail_add_prodWebET.setEnabled(false);


    }
    public void displaySuccessDialog(){
        dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_addproduct_confirm);
        dialog.show();
        TextView textConfirm = (TextView) dialog.findViewById(R.id.textConfirm);
        textConfirm.setText(responsestr);
        Button confirmBtn = (Button) dialog.findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // callNextIntent(RetailerHome.class);
                dialog.dismiss();
                Intent retailer_home=new Intent(RetailerAddProductActivity.this,RetailerHome.class);
                startActivity(retailer_home);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w("Succcess", "onResume");
        try {
            retail_add_prodLocationET.setText(sessinManager.getLocationnValue(Constants.LOCATION_SESSION_NAME, Constants.ADDRESS_KEY));
        } catch (Exception e) {
            Log.e("Exception", "" + e);
        }
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.w("Succcess", "onPostResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w("Succcess", "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w("Succcess", "onStop");
    }



//    else {
//        Url = Constants.LIVE_URL + Constants.RETAILER_EDIT +

        private Map<String, String> getParams() {
            Map<String, String> retaileraddparams = new HashMap<String, String>();
            retaileraddparams.put(Constants.POST_RETAILER_ID_PARAM ,String.valueOf(sessinManager.getSessionIntValue(Constants.SESSION_USERID)));
            retaileraddparams.put(Constants.POST_RETAILER_ADD_PROD_NAME_PARAM ,retailer_add_prodTitleET.getText().toString().trim());
            retaileraddparams.put(Constants.POST_RETAILER_ADD_PROD_DESC_PARAM ,retail_add_prodDescrET.getText().toString().trim());
            retaileraddparams.put(Constants.POST_RETAILER_ADD_PROD_PRICE_PARAM ,retail_add_prodPriceET.getText().toString().trim());
            retaileraddparams.put(Constants.POST_RETAILER_ADD_PROD_STORE_PARAM ,retail_add_prodStoreET.getText().toString().trim());
            retaileraddparams.put(Constants.POST_RETAILER_ADD_PROD_LOC_PARAM, sessinManager.getLocationnValue(Constants.LOCATION_SESSION_NAME, Constants.ADDRESS_KEY));
            retaileraddparams.put(Constants.POST_RETAILER_ADD_PROD_WEB_PARAM  ,retail_add_prodWebET.getText().toString().trim());
            retaileraddparams.put(Constants.POST_RETAILER_ADD_PROD_IMAGE_PARAM ,encodedImageString);
            retaileraddparams.put(Constants.LATITUDE_KEY, sessinManager.getLocationnValue(Constants.LOCATION_SESSION_NAME, Constants.LATITUDE_KEY));
            retaileraddparams.put(Constants.LONGITUDE_KEY, sessinManager.getLocationnValue(Constants.LOCATION_SESSION_NAME, Constants.LONGITUDE_KEY));
            return retaileraddparams;
           }

    private Map<String,String> getparam(){
        Map<String, String> retailerupdateparams = new HashMap<String, String>();
        retailerupdateparams.put(Constants.POST_RETAILER_ID_PARAM ,String.valueOf(sessinManager.getSessionIntValue(Constants.SESSION_USERID)));
        retailerupdateparams.put(Constants.POST_PRODUCT_ID_PARAM , product);
        retailerupdateparams.put(Constants.POST_RETAILER_ACTION , Constants.RETAILER_UPDATE );
        retailerupdateparams.put(Constants.POST_RETAILER_ADD_PROD_NAME_PARAM , retailer_add_prodTitleET.getText().toString().trim());
        retailerupdateparams.put(Constants.POST_RETAILER_ADD_PROD_DESC_PARAM ,retail_add_prodDescrET.getText().toString().trim());
        retailerupdateparams.put(Constants.POST_RETAILER_ADD_PROD_PRICE_PARAM ,retail_add_prodPriceET.getText().toString().trim());
        retailerupdateparams.put(Constants.POST_RETAILER_ADD_PROD_STORE_PARAM , retail_add_prodStoreET.getText().toString().trim());
        retailerupdateparams.put(Constants.POST_RETAILER_ADD_PROD_LOC_PARAM ,sessinManager.getLocationnValue(Constants.LOCATION_SESSION_NAME, Constants.ADDRESS_KEY));
        retailerupdateparams.put(Constants.POST_RETAILER_ADD_PROD_WEB_PARAM  ,retail_add_prodWebET.getText().toString().trim());
        retailerupdateparams.put(Constants.POST_RETAILER_ADD_PROD_IMAGE_PARAM ,encodedImageString);
        retailerupdateparams.put(Constants.LATITUDE_KEY, sessinManager.getLocationnValue(Constants.LOCATION_SESSION_NAME, Constants.LATITUDE_KEY));
        retailerupdateparams.put(Constants.LONGITUDE_KEY, sessinManager.getLocationnValue(Constants.LOCATION_SESSION_NAME, Constants.LONGITUDE_KEY));
        return retailerupdateparams;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sessinManager.storeLocationnValue(Constants.LOCATION_SESSION_NAME, Constants.ADDRESS_KEY, Constants.LOCATION_HINT);
        sessinManager.storeLocationnValue(Constants.LOCATION_SESSION_NAME, Constants.LATITUDE_KEY, "");
        sessinManager.storeLocationnValue(Constants.LOCATION_SESSION_NAME, Constants.LONGITUDE_KEY, "");
        clearQueue();
        deleteDirectory();
        finish();
    }
    private void clearQueue() {
        if (AppController.getInstance().getRequestQueue() != null) {
            AppController.getInstance().getRequestQueue().cancelAll(R.string.retialer_add_product);
        }
    }


    private void deleteDirectory() {
        if (mRuntimePermission.hasPermission(Constants.READ_STORAGE_PERMISSIONS)) {
               if (mRuntimePermission.hasPermission(Constants.WRITE_STORAGE_PERMISSIONS)) {
        // File dir = new File(Environment.getExternalStorageDirectory()+"Dir_name_here");
        File dir = new File(Environment.getExternalStorageDirectory().getPath(), Constants.APP_NAME + "/Images");
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }}}
    }

}
