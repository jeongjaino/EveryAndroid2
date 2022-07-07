package com.example.melon

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
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

class PlayerFragment : Fragment() {

    private val binding by lazy{ FragmentPlayerBinding.inflate(layoutInflater)}
    private var isPlayList = true
    private var player: SimpleExoPlayer? = null
    private lateinit var playListAdapter: PlayListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        initPlayListButton()
        initPlayerView()
        initPlayControlButton()
        initRecyclerView()

        getVideoList()
        return binding.root
    }

    private fun initPlayListButton(){
        binding.playListImageView.setOnClickListener{

            //todo 만약 서버에서 데이터가 다 불러오지 않았을 경우
            binding.playerViewGroup.isVisible = isPlayList
            binding.playlistViewGroup.isVisible = isPlayList.not()

            isPlayList = !isPlayList
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

        }
        binding.skipPreviousImageView.setOnClickListener{

        }
    }

    private fun initRecyclerView(){

        playListAdapter = PlayListAdapter{

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

            player.addListener(object: Player.EventListener{

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)

                    if(isPlaying){
                        binding.playControlImageView.setImageResource(R.drawable.exo_controls_pause)
                    }else{
                        binding.playControlImageView.setImageResource(R.drawable.ic_baseline_play_arrow_48)
                    }

                }

            })

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
                            response.body()?.let{
                                val modelList = it.musics.mapIndexed { index, musicEntity ->
                                    musicEntity.mapper(index.toLong())
                                }
                                setMusicList(modelList)
                                playListAdapter.submitList(modelList)
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

    companion object{
        fun newInstance(): PlayerFragment{
            return PlayerFragment()
        }
    }
}