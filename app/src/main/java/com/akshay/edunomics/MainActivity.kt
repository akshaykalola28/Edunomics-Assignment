package com.akshay.edunomics

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_main.*
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.round
import kotlin.math.sqrt


class MainActivity : AppCompatActivity() {

    private var height: Double = 10.0
    private var restitution: Double = 0.0
    private var timePoint: Double = 0.0

    private var g: Double = 10.0 //gravity

    private var bounce: Int = 0

    private val df = DecimalFormat("#.###")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        df.roundingMode = RoundingMode.CEILING


        submit_button.setOnClickListener {
            if (restitution == 0.0 || height_edit_text.text.isEmpty()) {
                Toast.makeText(this, "Invalid Data", Toast.LENGTH_SHORT).show()
            } else {
                height = height_edit_text.text.toString().toDouble()
                setGraphView()
            }
        }

        restitution_seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                restitution = progress / 100.0
                Toast.makeText(this@MainActivity, restitution.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setGraphView() {
        val series = LineGraphSeries<DataPoint>()

        series.appendData(DataPoint(0.0, height), true, 100)

        val list = arrayListOf<DataPoint>()
        list.add(DataPoint(0.0, height))

        while (height > 0.0) {

            //Fall to point
            timePoint =
                round(timePoint * 100.0) / 100.0 + timeFromDistance(height, velocity(height))
            series.appendData(
                DataPoint(timePoint, 0.0),
                true,
                100
            )
            list.add(DataPoint(timePoint, 0.0))
            Log.d("Fall to X,Y", "${timePoint} , ${0.0}")

            if (height <= 0.0) break

            bounce++

            //Rise to point
            val u = restitution * velocity(height)
            height = heightFromVelocity(u)

            timePoint = round(timePoint * 100.0) / 100.0 + timeFromDistance(height, u)
            series.appendData(
                DataPoint(
                    timePoint,
                    height
                ),
                true,
                100
            )
            list.add(DataPoint(timePoint, height))
            Log.d("Rise to X,Y", "$timePoint , $height")
        }

        val array = arrayOfNulls<DataPoint>(list.size)
        list.toArray(array)

        //Setup Graph view
        graph_view.title = "Graph View"
        graph_view.viewport.isScalable = true
        graph_view.viewport.isScrollable = true
        graph_view.addSeries(LineGraphSeries(array))

        bounce_text.text = "Total Bounce: $bounce"
    }

    private fun velocity(height: Double): Double {
        return Math.round((sqrt(2 * g * height)) * 100.0) / 100.0
    }

    private fun heightFromVelocity(velocity: Double): Double {
        return Math.round((velocity * velocity) / (2 * g) * 100.0) / 100.0
    }

    private fun timeFromDistance(distance: Double, velocity: Double): Double {
        return Math.round((distance / velocity) * 100.0) / 100.0
    }
}
