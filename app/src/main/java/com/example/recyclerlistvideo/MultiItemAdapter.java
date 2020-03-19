package com.example.recyclerlistvideo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

/**
 * @author sunwei
 * email：tianmu19@gmail.com
 * date：2020/3/19 10:35
 * package：com.example.recyclerlistvideo
 * version：1.0
 * <p>description：              </p>
 */
public class MultiItemAdapter extends BaseMultiItemQuickAdapter<MultiItem, BaseViewHolder> {
    public MultiItemAdapter(List<MultiItem> data) {
        super(data);
        addItemType(MultiItem.GRID, R.layout.item_grid);
        addItemType(MultiItem.TWOPIC, R.layout.item_twopic);
        addItemType(MultiItem.VIDEO, R.layout.item_video);
    }

    private final String imgUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1584597567415&di=2668e572b2513f59ffcadfbd263310a6&imgtype=0&src=http%3A%2F%2Fb2-q.mafengwo.net%2Fs5%2FM00%2F91%2F06%2FwKgB3FH_RVuATULaAAH7UzpKp6043.jpeg";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void convert(BaseViewHolder holder, MultiItem multiItem) {
        switch (multiItem.getItemType()) {
            case MultiItem.VIDEO:
                bindVideo(holder, multiItem);

                break;
            case MultiItem.GRID:
                ImageView imageView = holder.getView(R.id.iv_grid);
                Glide.with(imageView.getContext()).load(imgUrl)
                        .apply(new RequestOptions().override(700, 700).diskCacheStrategy(DiskCacheStrategy.RESOURCE)).into(imageView);
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void bindVideo(BaseViewHolder holder, MultiItem item) {
        final int seekPosition = item.getSeekPosition();
        Log.e(TAG, "onBindVideo:  " + seekPosition);
        TextureView textureView = holder.getView(R.id.textureView);
        Context context = holder.itemView.getContext();
        final ImageView iv = holder.getView(R.id.frameImage);
        MediaPlayer mediaPlayer = (MediaPlayer) holder.itemView.getTag(R.integer.key_mediaplayer);
        if (null == mediaPlayer) {
            mediaPlayer = new MediaPlayer();
            holder.itemView.setTag(R.integer.key_mediaplayer, mediaPlayer);
            final MediaPlayer finalMediaPlayer = mediaPlayer;
            iv.setVisibility(View.VISIBLE);
            iv.setImageBitmap(getBitmap(context, textureView));
            prepareAndStart(context, iv, finalMediaPlayer, textureView, seekPosition);
        } else {
            Log.e(TAG, "bindVideo:直接开始 ");
            mediaPlayer.seekTo(seekPosition, MediaPlayer.SEEK_CLOSEST);
            mediaPlayer.start();
            mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    if(what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START ){
                        iv.setVisibility(View.GONE);
                    }
                    return true;
                }
            });
        }

    }

    private Bitmap getBitmap(Context context, TextureView textureView) {
        Resources resources = context.getApplicationContext().getResources();
        return textureView == null ? BitmapFactory.decodeResource(resources, R.drawable.video_home) :
                (textureView.getBitmap() == null ? BitmapFactory.decodeResource(resources, R.drawable.video_home) : textureView.getBitmap());
    }

    private void prepareAndStart(Context context, final ImageView iv, final MediaPlayer finalMediaPlayer, TextureView textureView, final int seekPosition) {
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                try {
                    Surface surface1 = new Surface(surface);
                    finalMediaPlayer.setSurface(surface1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
        //uri
        String packageName = context.getApplicationContext().getPackageName();
        Uri videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.video_home);
        //prepare play
        finalMediaPlayer.pause();
        finalMediaPlayer.reset();
        try {
            finalMediaPlayer.setDataSource(context, videoUri);
            finalMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (seekPosition > 0) {
                        finalMediaPlayer.seekTo(seekPosition);
                    }
                    finalMediaPlayer.start();
                    iv.setVisibility(View.GONE);
                    Log.e(TAG, "onPrepared: ");
                }
            });
            finalMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.e(TAG, "onCompletion: ");
                    finalMediaPlayer.seekTo(7000);
                    finalMediaPlayer.start();
                }
            });
            finalMediaPlayer.prepare();
            Log.e(TAG, "prepare: ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String TAG = "adapter>> ";

    @Override
    public void onViewDetachedFromWindow(@NonNull BaseViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        //此时执行动画的暂停，获取seek位置
        MediaPlayer mediaPlayer = (MediaPlayer) holder.itemView.getTag(R.integer.key_mediaplayer);
        if (null != mediaPlayer && mediaPlayer.isPlaying()) {
            Context context = holder.itemView.getContext();
            TextureView textureView = holder.getView(R.id.textureView);
            mediaPlayer.pause();
            int currentPosition = mediaPlayer.getCurrentPosition();
            MultiItem item = getData().get(holder.getAdapterPosition());
            item.setSeekPosition(currentPosition);
            //暂停显示图片
            final ImageView iv = holder.getView(R.id.frameImage);
            iv.setVisibility(View.VISIBLE);
            iv.setImageBitmap(getBitmap(context, textureView));
            if (0 == holder.getAdapterPosition())
                Log.e(TAG, "onDetached: " + " currentPosition: " + currentPosition);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        //此处开启视频，seek到指定位置
        MultiItem item = getData().get(holder.getAdapterPosition());
        int seekPosition = item.getSeekPosition();
        MediaPlayer mediaPlayer = (MediaPlayer) holder.itemView.getTag(R.integer.key_mediaplayer);
        if (null != mediaPlayer && !mediaPlayer.isPlaying()) {
            TextureView textureView = holder.getView(R.id.textureView);
            Context context = holder.itemView.getContext();
            final ImageView iv = holder.getView(R.id.frameImage);

            mediaPlayer.seekTo(seekPosition, MediaPlayer.SEEK_CLOSEST);
            mediaPlayer.start();
            mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    if(what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START )iv.setVisibility(View.GONE);
                    return true;
                }
            });
            Log.e(TAG, "onAttached: " + " seekPosition: " + seekPosition);
        }

    }

    public void pauseVideo() {
        List<MultiItem> datas = getData();
        for (int i = 0; i < datas.size(); i++) {
            MultiItem item = datas.get(i);
            if (item.getItemType() == MultiItem.VIDEO) {
                notifyItemChanged(i);
            }
        }
    }
}
