package com.example.recursosmultimedia

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.TimeUnit

class MusicActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
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
        setContentView(R.layout.activity_music)

        // Cambiar el color de la barra de notificaciones
        window.statusBarColor = getColor(R.color.spotify_green)

        // Referencias a los componentes
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

        // Inicializar MediaPlayer con el archivo de audio
        mediaPlayer = MediaPlayer.create(this, R.raw.audiofile)
        handler = Handler()

        // Establecer la duración total de la canción en el TextView
        val totalTime = mediaPlayer.duration
        tvTotalTime.text = "-${formatTime(totalTime)}"

        // Configurar el SeekBar
        seekBar.max = totalTime

        // Acción al presionar Play/Pause
        btnPlayPause.setOnClickListener {
            if (isPlaying) {
                pauseMusic()
            } else {
                playMusic()
            }
        }

        // Acción al presionar Rewind
        btnRewind.setOnClickListener {
            rewindMusic()
        }

        // Acción al presionar Forward
        btnForward.setOnClickListener {
            forwardMusic()
        }

        // Controlar el SeekBar manualmente
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                    tvCurrentTime.text = formatTime(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    // Método para reproducir la música
    private fun playMusic() {
        mediaPlayer.start()
        isPlaying = true
        btnPlayPause.setImageResource(R.drawable.ic_pause) // Cambia a icono de pausa
        updateSeekBar()
    }

    // Método para pausar la música
    private fun pauseMusic() {
        mediaPlayer.pause()
        isPlaying = false
        btnPlayPause.setImageResource(R.drawable.ic_play) // Cambia a icono de play
        handler.removeCallbacks(updateRunnable)
    }

    // Método para retroceder la música
    private fun rewindMusic() {
        val currentPosition = mediaPlayer.currentPosition
        val rewindPosition = (currentPosition - 10000).coerceAtLeast(0) // Retrocede 10 segundos
        mediaPlayer.seekTo(rewindPosition)
        seekBar.progress = rewindPosition
        tvCurrentTime.text = formatTime(rewindPosition)
    }

    // Método para adelantar la música
    private fun forwardMusic() {
        val currentPosition = mediaPlayer.currentPosition
        val forwardPosition =
            (currentPosition + 10000).coerceAtMost(mediaPlayer.duration) // Avanza 10 segundos
        mediaPlayer.seekTo(forwardPosition)
        seekBar.progress = forwardPosition
        tvCurrentTime.text = formatTime(forwardPosition)
    }

    // Actualizar el SeekBar y tiempo
    private fun updateSeekBar() {
        seekBar.progress = mediaPlayer.currentPosition
        tvCurrentTime.text = formatTime(mediaPlayer.currentPosition)

        // Actualizar el tiempo total restante
        val timeRemaining = mediaPlayer.duration - mediaPlayer.currentPosition
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
        mediaPlayer.release() // Liberar recursos del MediaPlayer al destruir la actividad
        handler.removeCallbacks(updateRunnable)
    }
}

