package com.zgreenmatting.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.seu.magicfilter.MagicEngine;
import com.seu.magicfilter.filter.advanced.MagicAAFilter;
import com.seu.magicfilter.filter.base.gpuimage.GPUImageFilter;
import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.seu.magicfilter.widget.MagicCameraView;
import com.seu.magicfilter.widget.base.MagicBaseView;
import com.zgreenmatting.BaseActivity;
import com.zgreenmatting.R;
import com.zgreenmatting.adapter.FilterAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;

public class CameraActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, FilterAdapter.onFilterChangeListener{
    @BindView(R.id.new_layout_filter)
    LinearLayout mFilterLayout;
    @BindView(R.id.filter_listView)
    RecyclerView mFilterListView;
    @BindView(R.id.new_glsurfaceview_camera)
    MagicCameraView cameraView;
    @BindView(R.id.new_btn_camera_mode)
    ImageView btn_mode;
    @BindView(R.id.new_btn_camera_beauty)
    SeekBar new_btn_camera_beauty;
    @BindView(R.id.new_btn_camera_shutter)
    ImageView new_btn_camera_shutter;
    @BindView(R.id.btn_camera_closefilter)
    ImageView btn_camera_closefilter;
    @BindView(R.id.new_btn_camera_switch)
    ImageView new_btn_camera_switch;
    @BindView(R.id.new_btn_camera_album)
    TextView new_btn_camera_album;
    @BindView(R.id.new_btn_camera_filter)
    TextView new_btn_camera_filter;

    @BindView(R.id.up_down)
    LinearLayout up_down;
    @BindView(R.id.max_min)
    LinearLayout max_min;
    @BindView(R.id.left_rigit)
    LinearLayout left_rigit;
    @BindView(R.id.updown_seek)
    SeekBar updown_seek;
    @BindView(R.id.max_seek)
    SeekBar max_seek;
    @BindView(R.id.left_seek)
    SeekBar left_seek;
    @BindView(R.id.beauty_seek)
    SeekBar beauty_seek;
    @BindView(R.id.setting)
    ImageView setting;

    private FilterAdapter mAdapter;
    private MagicEngine magicEngine;
    private final int MODE_PIC = 1;
    private final int MODE_VIDEO = 2;
    private int mode = MODE_PIC;
    private ObjectAnimator animator;
    private SoundPool soundPool;
    private Map<Integer, Integer> soundMap;
    private static final int DISPLAY = 0;
    private static final int HIDE = 1;
    private static int STATE = HIDE;

    private float mLastX = 0.5f;
    private float mLastY = 0.5f;

    private float mLastMoveX = 0.f;
    private float mLastMoveY = 0.f;

    private final MagicFilterType[] types = new MagicFilterType[]{
            MagicFilterType.NONE,
            MagicFilterType.AA1,
            MagicFilterType.AA2,
            MagicFilterType.AA3,
            MagicFilterType.AA4,
            MagicFilterType.AA5,
            MagicFilterType.AA6,
            MagicFilterType.AA7,
            MagicFilterType.AA8,
            MagicFilterType.AA9,
            MagicFilterType.AA10,
            MagicFilterType.AA11,
            MagicFilterType.AA12,
            MagicFilterType.AA13,
            MagicFilterType.AA14,
            MagicFilterType.AA15,
            MagicFilterType.AA16,
            MagicFilterType.AA17,
            MagicFilterType.AA18,
            MagicFilterType.AA19,
            MagicFilterType.AA20,
            MagicFilterType.AA21,
            MagicFilterType.AA22,
            MagicFilterType.AA23,
            MagicFilterType.AA24,
            MagicFilterType.AA25,
            MagicFilterType.AA26,

    };

    @Override
    protected int getContentLayout() {
        return R.layout.activity_camera;
    }

    /**
     * onCreate
     */
    @Override
    protected void preInitView() {
        MagicEngine.Builder builder = new MagicEngine.Builder();
        magicEngine = builder.build(cameraView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mFilterListView.setLayoutManager(linearLayoutManager);

        mAdapter = new FilterAdapter(this, types);
        mAdapter.setOnFilterChangeListener(this);
        mFilterListView.setAdapter(mAdapter);
//        mAdapter.setOnFilterChangeListener(onFilterChangeListener);

        animator = ObjectAnimator.ofFloat(new_btn_camera_shutter,"rotation",0,360);
        animator.setDuration(500);
        animator.setRepeatCount(ValueAnimator.INFINITE);
//        Point screenSize = new Point();
//        getWindowManager().getDefaultDisplay().getSize(screenSize);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cameraView.getLayoutParams();
//        params.width = screenSize.x;
//        params.height = screenSize.x * 4 / 3;
//        cameraView.setLayoutParams(params);

        new_btn_camera_filter.setOnClickListener(this);
        btn_camera_closefilter.setOnClickListener(this);
        new_btn_camera_shutter.setOnClickListener(this);
        new_btn_camera_switch.setOnClickListener(this);
        btn_mode.setOnClickListener(this);
        new_btn_camera_album.setOnClickListener(this);
        setting.setOnClickListener(this);
        new_btn_camera_beauty.setOnSeekBarChangeListener(this);
        updown_seek.setOnSeekBarChangeListener(this);
        max_seek.setOnSeekBarChangeListener(this);
        left_seek.setOnSeekBarChangeListener(this);
        beauty_seek.setOnSeekBarChangeListener(this);
    }

    private void setting(int state){
        switch (state){
            case DISPLAY:
                STATE = HIDE;
                up_down.setVisibility(View.VISIBLE);
                max_min.setVisibility(View.VISIBLE);
                left_rigit.setVisibility(View.VISIBLE);
                break;
            case HIDE:
                STATE = DISPLAY;
                up_down.setVisibility(View.GONE);
                max_min.setVisibility(View.GONE);
                left_rigit.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * onResume
     */
    @Override
    protected void preInitData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.new_btn_camera_mode:
                switchMode();
                break;
            case R.id.new_btn_camera_shutter:

                if (PermissionChecker.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(CameraActivity.this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
                } else {
                    takePhoto();
                }
                break;
            case R.id.new_btn_camera_filter:
                showFilters();
                break;
            case R.id.new_btn_camera_switch:
                magicEngine.switchCamera();
                break;
            case R.id.btn_camera_closefilter:
                hideFilters();
                break;
            case R.id.new_btn_camera_album:
                startActivity(new Intent(CameraActivity.this,GalleryActivity.class));
                break;
            case R.id.setting:
                setting(STATE);
                break;
        }
    }

    private void initSP() throws Exception{
        //创建一个SoundPool对象，该对象可以容纳5个音频流
        AudioManager systemService = (AudioManager) CameraActivity.this.getSystemService(Context.AUDIO_SERVICE);
        //初始化soundPool 对象,第一个参数是允许有多少个声音流同时播放,第2个参数是声音类型,第三个参数是声音的品质
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        soundMap=new HashMap<>();
        soundMap.put(1, soundPool.load(CameraActivity.this, R.raw.tackphoto, 1));
        int streamVolume = systemService.getStreamVolume(AudioManager.STREAM_MUSIC);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        soundPool.play(soundMap.get(1), streamVolume, streamVolume, 1, 0, 1f);
    }

    private void switchMode(){
        if(mode == MODE_PIC){
            mode = MODE_VIDEO;
            btn_mode.setImageResource(R.mipmap.icon_camera);
        }else{
            mode = MODE_PIC;
            btn_mode.setImageResource(R.mipmap.icon_video);
        }
    }

    private void takePhoto(){
        try {
            initSP();
        } catch (Exception e) {
            e.printStackTrace();
        }
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "ZGreenMatting");
        Log.e("test","file : "+getOutputMediaFile());
        Log.e("test","file : "+mediaStorageDir.getPath() + File.separator);
        magicEngine.savePicture(getOutputMediaFile(),null);
    }
    public File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "ZGreenMatting");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }
    private void showFilters(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY", mFilterLayout.getHeight(), 0);
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                new_btn_camera_shutter.setClickable(false);
                mFilterLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        animator.start();
    }

    private void hideFilters(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY", 0 ,  mFilterLayout.getHeight());
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mFilterLayout.setVisibility(View.INVISIBLE);
                new_btn_camera_shutter.setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mFilterLayout.setVisibility(View.INVISIBLE);
                new_btn_camera_shutter.setClickable(true);
            }
        });
        animator.start();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == new_btn_camera_beauty){
            int params = new_btn_camera_beauty.getProgress();
            Log.e("params",params+"");
            MagicAAFilter filter = (MagicAAFilter)cameraView.getFilter();
            if(filter != null){
                float value = params/10f;
                Log.e("透明度value",value+"");
                filter.setParams(value);
            }
        }
        //上下
        if (seekBar == updown_seek){
            int params = updown_seek.getProgress();
            float value = params/10f;
            Log.e("上下value",value+"");
            mLastMoveY = (value - mLastY) * 2;
            move(mLastMoveX, mLastMoveY, 0);
        }
        //大小
        if (seekBar == max_seek){
            int params = max_seek.getProgress();
            float value = params/10.f;
            Log.e("大小value",value+"");
            setSize(value);
        }
        //左右
        if (seekBar == left_seek){
            int params = left_seek.getProgress();
            float value = params/10f;
            Log.e("左右value",value+"");
            mLastMoveX = (value - mLastX) * 2;
            move(mLastMoveX, mLastMoveY, 1);
        }
        //美颜
        if (seekBar == beauty_seek){
            int params = beauty_seek.getProgress();
            float value = params/10f;
            Log.e("美颜value",value+"");

            int beautyLevel = getBeautyLevel(params);
            magicEngine.setBeautyLevel(beautyLevel);

        }

    }

    private int getBeautyLevel(int param) {
        int level = 0;
        if (param == 0) {
            level = 0;
        } else if (param <= 25) {
            level = 1;
        } else if (param <= 50) {
            level = 2;
        } else if (param <= 75) {
            level = 3;
        } else if (param <= 90) {
            level = 4;
        } else {
            level = 5;
        }
        return level;
    }

    private void setSize(float param) {
        cameraView.scale(param);
    }

    private void move(float x, float y, int leftRight) {
        cameraView.move(x, y, leftRight);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onFilterChanged(MagicFilterType filterType) {
        magicEngine.setFilter(filterType);
    }

    @Override
    public void onChangePostion(final int position) {
        MagicAAFilter filter = (MagicAAFilter)cameraView.getFilter();
        if(filter != null) {
            if(position!=0){
                magicEngine.setFilterListener(new MagicBaseView.OnFilterChangedListener() {
                    @Override
                    public void filterChange(GPUImageFilter filter) {
                        if(filter instanceof MagicAAFilter){
                            MagicAAFilter aafilter =(MagicAAFilter) filter;
                            aafilter.setAsset(position+".jpg");
                        }
                    }
                });
                Log.e("photo:",position+".jpg");
            }
        }
        hideFilters();
    }
}
