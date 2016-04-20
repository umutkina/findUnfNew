package com.umutkina.findunfollowersapp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.umutkina.findunfollowersapp.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;


public class Utils {


    // public static final String BASE_IMAGE_URL =
    // "http://cardgusto.solidict.com/";
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }
    public  static Object getObject(String json,Type type){
//       Object obj=null;
        Gson gson = new Gson();
//       String json = mPrefs.getString("MyObject", "");
        return  gson.fromJson(json, type);
    }
    public  static String getJson(Object o){
        String s=null;

        Gson gson = new Gson();
        s = gson.toJson(o);
        return s;

    }
//
//    public static String getJsonObject(Object object) {
//        String json = null;
//        try {
//            json = Utils.OBJECT_MAPPER.writeValueAsString(object);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return json;
//    }
//
//    public static Object getObjectFromJson(String jsonObject, Class typeRef) {
//        Object object = null;
//        try {
//            object = Utils.OBJECT_MAPPER.readValue(jsonObject, typeRef);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return object;
//    }

    public static String separeteDigit(int number) {

        String numberStr = number + "";
        int length = numberStr.length();
        StringBuilder stringBuilder = new StringBuilder();
        int modValue = length % 3;

        String str = numberStr.substring(modValue);
        for (int i = str.length(); i >= 3; i--) {

            if (i % 3 == 0) {
                System.out.println("counter : " + i);
                stringBuilder.insert(0, "." + str.substring(i - 3, i));
            }

        }
        String s1 = stringBuilder.toString();
        if (modValue == 0) {
            System.out.println("seperated value : " + s1);
            System.out.println("seperated value : 2 " + s1.substring(1));
            return s1.substring(1);
        } else {
            stringBuilder.insert(0, numberStr.substring(0, modValue) + ".", 0, modValue);
            return stringBuilder.toString();
        }


    }


    public static int getAge(String birthDate) {
        if (birthDate != null && birthDate.length() != 0) {
            String[] split = birthDate.split("\\.");
            String year = split[2];
            Calendar now = Calendar.getInstance(); // This gets the current date and
            // time.
            int currentYear = now.get(Calendar.YEAR);
            int birthYear = Integer.parseInt(year);
            return currentYear - birthYear;
        } else {
            return 0;
        }
    }


    public static String convertToParameter(HashMap<String, String> parameters) {
        StringBuilder parameter = new StringBuilder();

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            parameter.append("&" + key + "=" + value);
        }
        if (parameters.size() > 0) {
            return parameter.toString().substring(1,
                    parameter.toString().length());
        } else {
            return null;
        }

    }


    public static boolean isAppropriateEmail(String email) {
        if (email.lastIndexOf(".") > email.indexOf("@")
                && email.indexOf("@") > 0) {
            return true;
        }
        return false;
    }

    public static void showInfoDialogWithEdittext(Context context, final Runnable onOk,
                                                  String title, String message, String hint) {

        final AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(title);
        alert.setMessage(message);
        final EditText input = new EditText(context);
        input.setHint(hint);
        alert.setView(input);
        if (onOk != null) {
            alert.setNegativeButton("İptal",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                        }
                    });
        }
        alert.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                if (onOk != null) {
                    onOk.run();
                }
            }
        });

        try {
            ((Activity) context).runOnUiThread(new Runnable() {
                public void run() {
                    alert.show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void showInfoDialogNotCancelable(final Context context, final Runnable onOk,
                                                   String title, String message) {

        final AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(title);
        alert.setMessage(message);
        alert.setCancelable(false);
        if (onOk != null) {
            alert.setNegativeButton(context.getString(R.string.logout),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (onOk != null) {
                                onOk.run();
                            }

                        }
                    });
        }
        alert.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

               Activity activity= (Activity) context;
                activity.finish();
            }
        });

        try {
            ((Activity) context).runOnUiThread(new Runnable() {
                public void run() {
                    alert.show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showInfoDialog(Context context, final Runnable onOk,
                                      String title, String message) {

        final AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(title);
        alert.setMessage(message);

        if (onOk != null) {
            alert.setNegativeButton(context.getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                        }
                    });
        }
        alert.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                if (onOk != null) {
                    onOk.run();
                }
            }
        });

        try {
            ((Activity) context).runOnUiThread(new Runnable() {
                public void run() {
                    alert.show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showInfoDialogWithRunnable(Context context, final Runnable onOk,
                                                  String title, String message) {

        final AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(title);
        alert.setMessage(message);

        alert.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                if (onOk != null) {
                    onOk.run();
                }
            }
        });

        try {
            ((Activity) context).runOnUiThread(new Runnable() {
                public void run() {
                    alert.show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Call(final String tell, final Context context) {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                String number = "tel:" + tell;
                Intent callIntent = new Intent(Intent.ACTION_CALL,
                        Uri.parse(number));
                try {
                    context.startActivity(callIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(context,
                            "there are no call clients installed",
                            Toast.LENGTH_SHORT).show();
                }

            }

        };
        showInfoDialog(context, runnable, "Bilgi",
                "Aramak istediğinize emin misiniz?");
    }
    public static int randInt(int max){
        Random rand = new Random();
        int randomNum = rand.nextInt(max  + 1);

        return randomNum;
    }

    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
    public static LinkedHashMap<String, String> readFile(
            Context context) {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        AssetManager am = null;
        am = context.getAssets();

        InputStream inputStream = null;
        try {
            inputStream = am.open("city_new.txt");

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream, "UTF-8"));

            // StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // out.append(UTF8Str); // add everything to StringBuilder
                // here you can have your logic of comparison.
                String[] lines = line.split("\t");
                int parseInt = Integer.parseInt(lines[0]);
                map.put(parseInt + "", lines[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;

    }


    public static int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static void sendSMS(String phoneNumber, String message, final Context context) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(
                SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
                new Intent(DELIVERED), 0);

        // ---when the SMS has been sent---
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        // ---when the SMS has been delivered---
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

//    public static void shareOnFacebook(String description, String url,
//                                       Activity activity) {
//        Bundle params;
//        params = new Bundle();
//        params.putString("name", "Tarif Dükkanım");
//        params.putString("description", description);
//        params.putString("caption", " ");
//        params.putString("picture", url);
//        params.putString("link", "tarifdukkani.solidict.com/indir.html");
//        shareOnFacebookWithDialog(params, activity);
//
//    }
//
//    public static void shareOnFacebookWithDialog(final Bundle params,
//                                                 final Activity activity) {
//
//        if ((Session.getActiveSession() == null)
//                || (!Session.getActiveSession().isOpened())) {
//            System.out.println("burdayim1");
//            Session.openActiveSession(activity, true,
//                    new Session.StatusCallback() {
//
//                        // callback when session changes state
//                        @Override
//                        public void call(final Session session,
//                                         SessionState state, Exception exception) {
//                            Session session2 = session;
//                            if (session.isOpened()) {
//                                Request request = Request.newMeRequest(session,
//                                        new Request.GraphUserCallback() {
//                                            @Override
//                                            public void onCompleted(
//                                                    GraphUser user,
//                                                    Response response) {
//                                                // If the response is successful
//                                                if (session == Session
//                                                        .getActiveSession()) {
//                                                    if (user != null) {
//                                                        String fbId = user
//                                                                .getId();// user
//                                                        // id
//                                                        String profileName = user
//                                                                .getName();// user's
//                                                        // profile
//                                                        // name
//
//
//                                                    }
//                                                }
//                                            }
//                                        });
//                                Request.executeBatchAsync(request);
//
//                                shareOnFacebookWithDialog(params, activity);
//
//                            }
//                        }
//                    });
//            return;
//        }
//        System.out.println("burdayim2");
//        WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(activity,
//                Session.getActiveSession(), params)).setOnCompleteListener(
//                new OnCompleteListener() {
//
//                    @Override
//                    public void onComplete(Bundle values,
//                                           FacebookException error) {
//                        if (error == null) {
//                            // When the story is posted, echo the success
//                            // and the post Id.
//                            final String postId = values.getString("post_id");
//                            if (postId != null) {
//                                // Toast.makeText(activity, "Posted story, id: "
//                                // + postId, Toast.LENGTH_SHORT).show();
//                            } else {
//                                // User clicked the Cancel button
//                                Toast.makeText(
//                                        activity.getApplicationContext(),
//                                        "Publish cancelled", Toast.LENGTH_SHORT)
//                                        .show();
//                            }
//                        } else if (error instanceof FacebookOperationCanceledException) {
//                            // User clicked the "x" button
//                            Toast.makeText(activity.getApplicationContext(),
//                                    "Publish cancelled", Toast.LENGTH_SHORT)
//                                    .show();
//                        } else {
//                            // Generic, ex: network error
//                            Toast.makeText(activity.getApplicationContext(),
//                                    "Error posting story", Toast.LENGTH_SHORT)
//                                    .show();
//                        }
//                    }
//
//                }).build();
//        feedDialog.show();
//        System.out.println("burdayim3");
//    }

    public static void showKeyboard(EditText mEtSearch, Context context) {
        mEtSearch.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public static void hideSoftKeyboard(EditText mEtSearch, Context context) {
        mEtSearch.clearFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEtSearch.getWindowToken(), 0);


    }

}
