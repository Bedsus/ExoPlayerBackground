package ru.bedsus.exoplayerbackground

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import ru.bedsus.exoplayerbackground.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    companion object {
        val videoList = listOf(
            "http://view.eaglecdn.com/api/resources/MjAyMTAyMjYvNjYwNjdiNmMzMTg3NQ.m3u8?account=ramblernews&e=1614333313&record_id=1644837&st=NQJ776FcHaeYow99Yc8-1w",
            "http://view.eaglecdn.com/api/resources/MjAyMTAyMjYvYjg2Njk4ZjJjZTJhOA.m3u8?account=ramblernews&e=1614332234&record_id=1644856&st=GniOQixN8JqeHXe8KLVIFQ",
        )
    }

    private lateinit var binding: ActivityMainBinding

    private lateinit var  simpleExoPlayer: SimpleExoPlayer
    private lateinit var  mediaSourceFactory: HlsMediaSource.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        simpleExoPlayer = SimpleExoPlayer.Builder(applicationContext).build()
        mediaSourceFactory = createHlsMediaSourceFactory(applicationContext)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playerView.player = simpleExoPlayer

        with(simpleExoPlayer) {
            volume = 0f
            repeatMode = Player.REPEAT_MODE_OFF
            simpleExoPlayer.prepare()
            setMediaSource(getMediaSource(videoList[1]))
            playWhenReady = false
        }

        simpleExoPlayer.addListener(object : Player.EventListener {
            override fun onPlaybackStateChanged(stateInt: Int) {
                if (stateInt == ExoPlayer.STATE_READY) {
                    getFrameToVideo()
                }
            }
        })
    }

    private fun getFrameToVideo() {
        (binding.playerView.videoSurfaceView as TextureView).bitmap?.let { bitmap ->
            Palette.from(bitmap).generate { palette ->
                binding.videoContainer.setBackgroundColor(
                    palette?.mutedSwatch?.rgb ?:
                    ContextCompat.getColor(this, R.color.black)
                )
            }
        }
    }

    private fun getMediaSource(url: String) =
        mediaSourceFactory.createMediaSource(MediaItem.fromUri(Uri.parse(url)))

    private fun createHlsMediaSourceFactory(context: Context) = HlsMediaSource.Factory(
        DefaultHttpDataSourceFactory(Util.getUserAgent(context, getString(R.string.app_name)))
    ).setAllowChunklessPreparation(true)
}