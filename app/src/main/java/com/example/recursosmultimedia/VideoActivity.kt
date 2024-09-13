package com.example.recursosmultimedia

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.TimeUnit

class VideoActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var seekBar: SeekBar
    private lateinit var tvCurrentTime: TextView
    private lateinit var tvTotalTime: TextView
    private lateinit var btnPlayPause: ImageButton
    private lateinit var btnRewind: ImageButton
    private lateinit var btnForward: ImageButton
    private lateinit var handler: Handler
    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        // Cambiar el color de la barra de notificaciones
        window.statusBarColor = getColor(R.color.youtube_red)

        // Referencias a los componentes
        videoView = findViewById(R.id.videoView)
        seekBar = findViewById(R.id.seekBar)
        tvCurrentTime = findViewById(R.id.tvCurrentTime)
        tvTotalTime = findViewById(R.id.tvTotalTime)
        btnPlayPause = findViewById(R.id.btnPlayPause)
        btnRewind = findViewById(R.id.btnRewind)
        btnForward = findViewById(R.id.btnForward)

        // Botón para regresar a la actividad principal
        val btnBack: ImageButton = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Inicializar el VideoView con el archivo de video
        val videoUri = Uri.parse("android.resource://$packageName/${R.raw.videofile}")
        videoView.setVideoURI(videoUri)
        handler = Handler()

        // Establecer la duración total del video cuando esté listo
        videoView.setOnPreparedListener {
            val totalTime = videoView.duration
            tvTotalTime.text = "-${formatTime(totalTime)}"
            seekBar.max = totalTime
        }

        // Acción al presionar Play/Pause
        btnPlayPause.setOnClickListener {
            if (isPlaying) {
                pauseVideo()
            } else {
                playVideo()
            }
        }

        // Acción al presionar Rewind
        btnRewind.setOnClickListener {
            rewindVideo()
        }

        // Acción al presionar Forward
        btnForward.setOnClickListener {
            forwardVideo()
        }

        // Controlar el SeekBar manualmente
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    videoView.seekTo(progress)
                    tvCurrentTime.text = formatTime(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    // Método para reproducir el video
    private fun playVideo() {
        videoView.start()
        isPlaying = true
        btnPlayPause.setImageResource(R.drawable.ic_pause) // Cambia a icono de pausa
        updateSeekBar()
    }

    // Método para pausar el video
    private fun pauseVideo() {
        videoView.pause()
        isPlaying = false
        btnPlayPause.setImageResource(R.drawable.ic_play) // Cambia a icono de play
        handler.removeCallbacks(updateRunnable)
    }

    // Método para retroceder el video
    private fun rewindVideo() {
        val currentPosition = videoView.currentPosition
        val rewindPosition = (currentPosition - 10000).coerceAtLeast(0) // Retrocede 10 segundos
        videoView.seekTo(rewindPosition)
        seekBar.progress = rewindPosition
        tvCurrentTime.text = formatTime(rewindPosition)
    }

    // Método para adelantar el video
    private fun forwardVideo() {
        val currentPosition = videoView.currentPosition
        val forwardPosition =
            (currentPosition + 10000).coerceAtMost(videoView.duration) // Avanza 10 segundos
        videoView.seekTo(forwardPosition)
        seekBar.progress = forwardPosition
        tvCurrentTime.text = formatTime(forwardPosition)
    }

    // Actualizar el SeekBar y tiempo
    private fun updateSeekBar() {
        seekBar.progress = videoView.currentPosition
        tvCurrentTime.text = formatTime(videoView.currentPosition)

        // Actualizar el tiempo total restante
        val timeRemaining = videoView.duration - videoView.currentPosition
        tvTotalTime.text = "-${formatTime(timeRemaining)}"

        handler.postDelayed(updateRunnable, 1000) // Actualizar cada segundo
    }

    // Runnable para actualizar el SeekBar
    private val updateRunnable = Runnable {
        updateSeekBar()
    }

    // Formatear el tiempo en minutos y segundos
    private fun formatTime(millis: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis.toLong())
        val seconds =
            TimeUnit.MILLISECONDS.toSeconds(millis.toLong()) - TimeUnit.MINUTES.toSeconds(minutes)
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.stopPlayback() // Liberar recursos del VideoView al destruir la actividad
        handler.removeCallbacks(updateRunnable)
    }
}
