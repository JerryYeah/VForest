package com.vforest.yeah.vforest.activity;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vforest.yeah.vforest.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Target;

public class CreateNewsActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int TAKE_PHOTO=1, LOCAL_PICTURE=2;
    EditText description;
    ImageView[] pics = new ImageView[4];
    Uri imageUri;
    private int addPicCount=1, addTakePicCount=1;
    private FrameLayout edit_photo_fullscreen_layout;
    private RelativeLayout edit_photo_outer_layout;
    private Animation get_photo_layout_out_from_up,get_photo_layout_in_from_down;
    private TextView take_picture,select_local_picture,cancel,upload;

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
    public void onClick(View v) {
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
                    try {
                        addTakePicCount++;
                        //将拍摄的照片依次显示
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        pics[addPicCount++].setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
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
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            pics[addPicCount++].setImageBitmap(bitmap);
        } else{
            Toast.makeText(this, "获取照片失败", Toast.LENGTH_SHORT).show();
        }
    }

}

