package com.vforest.yeah.vforest.activity;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.vforest.yeah.vforest.R;
import com.vforest.yeah.vforest.util.CommonUrl;
import com.vforest.yeah.vforest.util.HttpUtil;
import com.vforest.yeah.vforest.util.JsonResponse;
import com.vforest.yeah.vforest.util.Wetland;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Target;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CreateNewsActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int TAKE_PHOTO=1, LOCAL_PICTURE=2;
    EditText description;
    ImageView[] pics = new ImageView[4];
    Uri imageUri;
    private int addPicCount=1, addTakePicCount=1;
    private FrameLayout edit_photo_fullscreen_layout;
    private RelativeLayout edit_photo_outer_layout;
    private Animation get_photo_layout_in_from_down;
    private TextView take_picture,select_local_picture,cancel,upload;
    private Bitmap[] bitmaps = new Bitmap[4];
    private ArrayList<String> imgNameList, picUrl=new ArrayList<>();
    private ProgressDialog uploadNewsPrgDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_news);
        initView();

    }

    private void initView(){
        description = (EditText) findViewById(R.id.desc_edit);
        pics[0] = (ImageView) findViewById(R.id.add_pic) ;
        pics[1] = (ImageView) findViewById(R.id.add_img_1) ;
        pics[2] = (ImageView) findViewById(R.id.add_img_2) ;
        pics[3] = (ImageView) findViewById(R.id.add_img_3) ;
        edit_photo_fullscreen_layout=(FrameLayout)findViewById(R.id.edit_photo_fullscreen_layout);
        edit_photo_outer_layout=(RelativeLayout)findViewById(R.id.edit_photo_outer_layout);

        take_picture = (TextView)findViewById(R.id.take_picture);
        select_local_picture = (TextView)findViewById(R.id.select_local_picture);
        cancel = (TextView)edit_photo_outer_layout.findViewById(R.id.cancel_upload);

        upload = (TextView)findViewById(R.id.upload);


        pics[0].setOnClickListener(this);
        take_picture.setOnClickListener(this);
        select_local_picture.setOnClickListener(this);
        cancel.setOnClickListener(this);
        upload.setOnClickListener(this);
        for(int i=1;i<4;i++)
            pics[i].setOnClickListener(this);
    }
    @Override
    public void onClick(final View v) {
        switch (v.getId()){
            case R.id.add_pic:
                if (addPicCount == 4){
                    Toast.makeText(CreateNewsActivity.this, "最多只能添加三张照片", Toast.LENGTH_SHORT).show();
                } else {
                    //点击添加图片
                    edit_photo_fullscreen_layout.setVisibility(View.VISIBLE);
                    get_photo_layout_in_from_down = AnimationUtils.loadAnimation(CreateNewsActivity.this, R.anim.search_layout_in_from_down);
                    edit_photo_outer_layout.startAnimation(get_photo_layout_in_from_down);
                }
                break;
            case R.id.take_picture:           //拍照
                edit_photo_fullscreen_layout.setVisibility(View.GONE);
                File file = new File(getExternalCacheDir(), "pic_take_0"+ addTakePicCount +".jpg");
                try{
                    if (file.exists()){
                        file.delete();
                    }
                    file.createNewFile();
                } catch (IOException e){
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24) {
                    imageUri = FileProvider.getUriForFile(CreateNewsActivity.this,
                            "com.yeah.vforest.fileprovider", file);
                }else
                    imageUri = Uri.fromFile(file);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);
                break;
            case R.id.select_local_picture:    //选取本地照片
                edit_photo_fullscreen_layout.setVisibility(View.GONE);
                if(ContextCompat.checkSelfPermission(CreateNewsActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(CreateNewsActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }else
                    openAlbum();
                break;
            case R.id.cancel_upload:         //上传动态
                edit_photo_fullscreen_layout.setVisibility(View.GONE);
                break;
            case R.id.add_img_1:
            case R.id.add_img_2:
            case R.id.add_img_3:
                InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(description.getWindowToken(), 0) ;
                final View view = findViewById(R.id.display_big_image_layout);
                final ImageButton back, delete;
                back = (ImageButton) view.findViewById(R.id.btn_back);
                delete = (ImageButton) view.findViewById(R.id.btn_delete);
                ImageView pic = (ImageView) view.findViewById(R.id.big_photo);
                TextView page = (TextView) view.findViewById(R.id.edit_pages);
                pic.setImageBitmap(((BitmapDrawable) ((ImageView) v).getDrawable()).getBitmap());
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.setVisibility(View.GONE);
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v1) {
                        addPicCount--;
                        switch (v.getId()){
                            case R.id.add_img_1:
                                picUrl.remove(0);
                                if( bitmaps[2]!=null){
                                    bitmaps[1]=bitmaps[2];
                                    if(bitmaps[3]!=null){
                                        bitmaps[2]=bitmaps[3];
                                        bitmaps[3]=null;
                                    }
                                    else  bitmaps[2]=null;
                                }
                                else
                                    bitmaps[1]=null;
                                break;
                            case R.id.add_img_2:
                                picUrl.remove(1);
                                if(bitmaps[3]!=null){
                                    bitmaps[2]=bitmaps[3];
                                    bitmaps[3]=null;
                                }
                                else  bitmaps[2]=null;
                                break;
                            case R.id.add_img_3:
                                picUrl.remove(2);
                                bitmaps[3]=null;
                                break;
                        }
                        // 清空，重新排序
                        for(int i=1;i<4;i++)
                        {
                            Bitmap previousbitmap = pics[i].getDrawingCache();
                            if(previousbitmap != null && !previousbitmap.isRecycled())
                                previousbitmap.recycle();
                            pics[i].setImageBitmap(null);
                        }
                        for(int i=1;bitmaps[i]!=null;i++)
                            pics[i].setImageBitmap(bitmaps[i]);
                        view.setVisibility(View.GONE);
                    }
                });
                view.setVisibility(View.VISIBLE);
                break;
            case R.id.upload:
                imgNameList = new ArrayList<>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                int count = getPicAmount();
                if(count==0)
                    Toast.makeText(this, "请至少添加一张照片！", Toast.LENGTH_SHORT).show();
                else{
                    showProgressDialog();
                    for(int i =0;i<count;i++){
                        String key = "pic" + sdf.format(new Date()) + i;
                        imgNameList.add(key);
                    }
                    final Map<String, String> map=new HashMap<String, String>();
                    map.put("step","1");
                    map.put("username",LoginActivity.getName());
                    map.put("images",listToString(imgNameList));
                    //Log.d("!!!!!!!!", "onClick: "+imgNameList.size());
                    Log.d("!!!!!!!!!!!!", "onClick: "+listToString(imgNameList));
                    new Thread(){
                        @Override
                        public void run() {
                            HttpUtil.sendOkHttpRequest(CommonUrl.uploadNews, map, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.d("!!!", "onFailure: 失败啦！！！！！");
                                    indicate("获取上传凭证失败");
                                }
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    final String responseText = response.body().string();
                                    try {
                                        JSONObject res = new JSONObject(responseText);
                                        JSONArray keys = res.getJSONArray("contents");
                                        for(int i=0;i<keys.length();i++){
                                            UploadManager uploadManager = new UploadManager();
                                            uploadManager.put(picUrl.get(i), imgNameList.get(i), keys.getString(i), new UpCompletionHandler() {
                                                @Override
                                                public void complete(String key, ResponseInfo info, JSONObject response) {
                                                    Log.d("!!!!!!!!", "complete: "+"成功啦！");
                                                }
                                            },null);
                                        }
                                        map.put("step","2");      //上传成功后发第二次请求
                                        map.put("description",description.getText().toString());
                                        HttpUtil.sendOkHttpRequest(CommonUrl.uploadNews, map, new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                Log.d("!!!", "onFailure: 失败啦！！！！！");
                                                indicate("动态上传失败");
                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {
                                                Log.d("!!!!!!!!!", "onResponse: 成功啦！！！！！");
                                                String responseText = response.body().string();
                                                //Log.d("!!!!!!!!!", "回复:"+responseText);
                                                String code = JsonResponse.getRspCode(responseText);
                                                Log.d("!!!!!!!!!", "回复:"+code);
                                                if(code.equals("12003")) {     //发布成功
                                                    {
                                                        indicate("动态发布成功");
                                                        finish();
                                                    }
                                                }else
                                                    indicate("动态发布失败");
                                            }
                                        });

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }.start();
                }


            default:
                break;
            }
        }
    private  void openAlbum(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, LOCAL_PICTURE);  //打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                } else{
                    Toast.makeText(this, "你拒绝了该请求", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    addTakePicCount++;
                    //将拍摄的照片依次显示
                    String path =getRealFilePath(this,imageUri);
                    Bitmap bitmap = trasformToZoomBitmapAndLessMemory(path);
                    picUrl.add(path);
                    bitmaps[addPicCount]=bitmap;
                    pics[addPicCount].setImageBitmap(bitmap);
                    addPicCount++;
                    //Log.d("!!!!!!!!", "onActivityResult: "+picUrl.toString());
                }
                break;
            case LOCAL_PICTURE:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else
                        //4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                }
                break;
            default:
                break;
        }
    }
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            //如果是document类型的Uri,则通过document id处理
            String docId=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];//解析出数字格式的id
                String selection=MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content类型的URI，则使用普通方式处理
            imagePath=getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的Uri,直接获取图片路径即可
            imagePath=uri.getPath();
        }
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data){
        Uri uri=data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private  void displayImage(String imagePath){
        if(imagePath != null){
            Bitmap bitmap = trasformToZoomPhotoAndLessMemory(imagePath);
            bitmaps[addPicCount]=bitmap;
            pics[addPicCount].setImageBitmap(bitmap);
            addPicCount++;
            picUrl.add(imagePath);
            //Log.d("!!!!!!!!", "onActivityResult: "+picUrl.toString());
        } else{
            Toast.makeText(this, "获取照片失败", Toast.LENGTH_SHORT).show();
        }
    }

    //注意显示本地图片时一定要压缩质量，不然容易出现OOM
    public Bitmap trasformToZoomPhotoAndLessMemory(String url) {
        File file = new File(url);

        BitmapFactory.Options options = new BitmapFactory.Options();
        // 通过这个bitmap获取图片的宽和高
        options.inJustDecodeBounds = true;
        int inSampleSize = 1;
        if (file.length() < 256 * 1024) {
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        } else if (file.length() < 512 * 1024) {
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = 2;
            inSampleSize = 2;
        } else if (file.length() < 1024 * 1024) {
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = 4;
            inSampleSize = 4;
        } else {
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = 6;
            inSampleSize = 6;
        }
        //options.inPurgeable = true;
        // options.inInputShareable = true;
        // 注意这次要把options.inJustDecodeBounds 设为 false,这次图片是要读取出来的
        options.inJustDecodeBounds = false;
        int degree = readPictureDegree(file.getAbsolutePath());
        InputStream is = null;
        try {
            is = new FileInputStream(url);

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }
        Bitmap cameraBitmap = BitmapFactory.decodeStream(is, null, options);
        // Bitmap cameraBitmap = BitmapFactory.decodeFile(url, options);
        Bitmap photo = rotaingImageView(degree, cameraBitmap);
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        return photo;
    }

    public Bitmap trasformToZoomBitmapAndLessMemory(String url) {
        File file = new File(url);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        int inSampleSize = 1;

        if (file.length() < 256 * 1024) {
            options.inPreferredConfig = Bitmap.Config.RGB_565;
        } else if (file.length() < 512 * 1024) {
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = 2;
            inSampleSize = 2;
        } else if (file.length() < 1024 * 1024) {
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = 4;
            inSampleSize = 4;
        } else {
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = 6;
            inSampleSize = 6;
        }
        //options.inPurgeable = true;
        //options.inInputShareable = true;
        options.inJustDecodeBounds = false;
        int degree = readPictureDegree(file.getAbsolutePath());
        InputStream is = null;
        try {
            is = new FileInputStream(url);

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }
        Bitmap cameraBitmap;
        Bitmap tmp = BitmapFactory.decodeStream(is, null, options);
        if (tmp!=null)
            cameraBitmap=tmp;
        else
            cameraBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        Bitmap photo = rotaingImageView(degree, cameraBitmap);
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        return photo;
    }

    /* 如果有些手机拍照反转，读取角度*/
    public int readPictureDegree(String path){
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /* 如果有些手机拍照反转，那么把图片调正 */
    public Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    private int getPicAmount(){
        for(int i=1;i<4;i++){
            if(bitmaps[i]==null)
                return i-1;
        }
        return 3;
    }

    private void showProgressDialog(){
        if(uploadNewsPrgDlg == null) {
            uploadNewsPrgDlg = new ProgressDialog(CreateNewsActivity.this);
            uploadNewsPrgDlg.setTitle("VF");
            uploadNewsPrgDlg.setMessage("上传图片中，请稍等");
            uploadNewsPrgDlg.setCancelable(false);
        }
        uploadNewsPrgDlg.show();
    }
    private void closeProgressDialog(){
        if(uploadNewsPrgDlg != null)
            uploadNewsPrgDlg.dismiss();
    }

    private void indicate(final String hint){
        CreateNewsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                closeProgressDialog();
                Toast.makeText(CreateNewsActivity.this, hint,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static String getRealFilePath(final Context context, final Uri uri ) {       //Uri转文件路径
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
    private String listToString(ArrayList<String> list){
        StringBuffer sb = new StringBuffer("[");
        for(int i=0;i<list.size()-1;i++)
            sb.append("\"" + list.get(i) + "\", ");
        sb.append("\"" + list.get(list.size()-1) + "\"]");
        return sb.toString();
    }
}

