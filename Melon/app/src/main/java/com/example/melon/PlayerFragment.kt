package com.example.melon

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.melon.databinding.FragmentPlayerBinding
import com.example.melon.service.MusicDto
import com.example.melon.service.MusicService
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class PlayerFragment : Fragment() {

    private val binding by lazy{ FragmentPlayerBinding.inflate(layoutInflater)}

    private var model: PlayerModel = PlayerModel()
    private var player: SimpleExoPlayer? = null
    private lateinit var playListAdapter: PlayListAdapter
    private val updateSeekRunnable = Runnable{
        updateSeek()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        initPlayListButton()
        initPlayerView()
        initPlayControlButton()
        initSeekBar()
        initRecyclerView()

        getVideoList()
        return binding.root
    }

    private fun initPlayListButton(){
        binding.playListImageView.setOnClickListener{

            if(model.currentPosition == - 1) return@setOnClickListener //처음 앱을 킬 때

            binding.playerViewGroup.isVisible = model.isWatchingPlayListView
            binding.playlistViewGroup.isVisible = model.isWatchingPlayListView.not()

            model.isWatchingPlayListView = !model.isWatchingPlayListView
        }
    }

    private fun initPlayControlButton(){
        binding.playControlImageView.setOnClickListener{
            val player = this.player ?: return@setOnClickListener

            if(player.isPlaying){
                player.pause()
            }else{
                player.play()
            }
        }
        binding.skipNextImageView.setOnClickListener{
            val nextMusic = model.nextMusic() ?: return@setOnClickListener
            playMusic(nextMusic)
        }
        binding.skipPreviousImageView.setOnClickListener{
            val previousMusic = model.previousMusic() ?: return@setOnClickListener
            playMusic(previousMusic)
        }
    }

    private fun initRecyclerView(){

        playListAdapter = PlayListAdapter{
            playMusic(it)
        }

        binding.playListRecyclerView.apply{
            adapter = playListAdapter
            layoutManager = LinearLayoutManager(context)
        }

    }

    private fun initPlayerView(){

        context?.let{
            player = SimpleExoPlayer.Builder(it).build()
        }
        binding.playerView.player = player

        binding?.let{ binding ->

            player?.addListener(object: Player.EventListener{

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)

                    if(isPlaying){
                        binding.playControlImageView.setImageResource(R.drawable.exo_controls_pause)
                    }else{
                        binding.playControlImageView.setImageResource(R.drawable.ic_baseline_play_arrow_48)
                    }
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)

                    val newIndex = mediaItem?.mediaId ?: return
                    model.currentPosition  = newIndex.toInt()
                    updatePlayerView(model.currentMusicModel())
                    playListAdapter.submitList(model.getAdapterModels())
                }

                override fun onPlaybackStateChanged(state: Int) {
                    super.onPlaybackStateChanged(state)

                    updateSeek()
                }
            })
        }
    }

    private fun updateSeek(){
        val player = this.player ?: return
        val duration = if(player.duration >= 0) player.duration else 0
        val position = player.currentPosition

        updateSeekUi(duration, position)

        val state = player.playbackState
        view?.removeCallbacks(updateSeekRunnable) //중첩 방지
        if(state != Player.STATE_IDLE && state != Player.STATE_ENDED){
            view?.postDelayed(updateSeekRunnable, 1000)
        }
    }

    private fun updateSeekUi(duration: Long, position: Long){

        binding.playListSeekBar.max = (duration / 1000).toInt()
        binding.playListSeekBar.progress = (position / 1000).toInt()

        binding.playerSeekBar.max = (duration / 1000).toInt()
        binding.playerSeekBar.progress= (position / 1000).toInt()

        binding.playTimeTextView.text = String.format("%02d:%02d",
            TimeUnit.MINUTES.convert(position, TimeUnit.MILLISECONDS),
            (position / 1000) % 60)

        binding.totalTimeTextView.text = String.format("%02d:%02d",
            TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS),
            (position / 1000) % 60)

    }

    private fun initSeekBar(){
        binding.playerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, p2: Boolean) {
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                player?.seekTo((seekBar.progress * 1000.toLong()))
            }
        })
        binding.playListSeekBar.setOnTouchListener{v, event ->
            false
        }
    }

    private fun updatePlayerView(currentMusicModel:MusicModel?){
        currentMusicModel ?: return

        binding.let{
            binding.trackTextView.text = currentMusicModel.track
            binding.artistTextView.text = currentMusicModel.artist
            Glide.with(binding.coverImageView.context)
                .load(currentMusicModel.coverUrl)
                .into(binding.coverImageView)
        }
    }

    private fun getVideoList(){
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(MusicService::class.java)
            .also{
                it.listMusics()
                    .enqueue(object : Callback<MusicDto>{
                        override fun onResponse(
                            call: Call<MusicDto>,
                            response: Response<MusicDto>,
                        ) {
                            response.body()?.let{ musicDto ->

                                model = musicDto.mapper()
                                setMusicList(model.getAdapterModels())
                                playListAdapter.submitList(model.getAdapterModels())
                            }
                        }
                        override fun onFailure(call: Call<MusicDto>, t: Throwable) {
                            Log.d("tag", t.message.toString())
                        }
                    })
            }
    }

    private fun setMusicList(modelList: List<MusicModel>){
        context?.let{
            player?.addMediaItems(modelList.map { musicModel ->
                MediaItem.Builder()
                    .setMediaId(musicModel.id.toString())
                    .setUri(musicModel.streamUrl)
                    .build()
            })
            player?.prepare()
        }
    }

    private fun playMusic(musicModel: MusicModel){
        model.updateCurrentPosition(musicModel)
        player?.seekTo(model.currentPosition, 0)
        player?.play()
    }

    companion object{
        fun newInstance(): PlayerFragment{
            return PlayerFragment()
        }
    }

    override fun onStop(){
        super.onStop()

        player?.pause()
        view?.removeCallbacks(updateSeekRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()

        player?.release()
    }

}