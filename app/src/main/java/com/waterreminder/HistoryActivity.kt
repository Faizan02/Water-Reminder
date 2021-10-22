package com.waterreminder

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.waterreminder.room.History
import com.waterreminder.room.WaterDatabase
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_history.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class HistoryActivity : AppCompatActivity() {

    var array = ArrayList<Double>()
    var data = ArrayList<History>()
    val currentDate = SimpleDateFormat("MMM/yyyy").format(Date())
    var month = ""

    @Inject
    lateinit var database: WaterDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        btnBack.setOnClickListener {
            this.onBackPressed()
        }
        monthYearText.text = currentDate
        month = monthYearText.text.split("/")[0]
        database.historyDao().getAllHistory().observe(this, { entities ->
            data.clear()
            entities.forEach {
                data.add(it)
            }
            showMonthData()
        })
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        monthYearText.setOnClickListener {


            val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener
            { view, year, monthOfYear, dayOfMonth ->
                val calendar1 = Calendar.getInstance();
                calendar1.set(year, monthOfYear, dayOfMonth)
                monthYearText.text = SimpleDateFormat("MMM/yyyy").format(calendar1.time)
                this.month = monthYearText.text.split("/")[0]
                showMonthData()
            }, year, month, day
            )
            datePickerDialog.datePicker.maxDate = Date().time
            datePickerDialog.show()
        }
    }

    private fun showMonthData() {
        array.clear()
        data.forEach {
            val format1 = SimpleDateFormat("dd/MMM/yyyy").format(it.date)
            val day = SimpleDateFormat("dd").format(it.date)
            Log.d("2345ot", "${array.size}  $day")
            if (format1.contains("${monthYearText.text}")) {
                Log.d("2345st", "${array.size}  $day")
                if (array.size == day.toInt() - 1) {
                    array.add(it.totalDrank)
                } else {
                    if (array.size == 0) {
                        Log.d("2345zr", "${array.size}  $day")
                        for (i in 0 until day.toInt() - 1) {
                            array.add(0.0)
                            Log.d("2345zrin", "${array.size}  $day  ${array[array.size - 1]}")
                        }
                        array.add(it.totalDrank)
                        Log.d("2345zr", "${array.size}  $day")
                    } else {
                        Log.d("2345ne", "${array.size}  $day")
                        for (i in array.size until day.toInt() - 1) {
                            array.add(0.0)
                        }
                        array.add(it.totalDrank)
                        Log.d("2345ne", "${array.size}  $day")
                    }
                }
            }
        }
        //chk if current month and size is not equal to current date then add 0.0 to equal the size
        if (SimpleDateFormat("MMM").format(Date()) == month) {
            if (array.size < SimpleDateFormat("dd").format(Date()).toInt()) {
                for (i in array.size until SimpleDateFormat("dd").format(Date()).toInt()) {
                    array.add(0.0)
                }
            }
        } else if (array.size < 31) {
            //if not current month previous month then add size to 31 if size is not 31
            for (i in array.size until 31) {
                array.add(0.0)
            }
        }
        val aaChartModel: AAChartModel = AAChartModel()
            .chartType(AAChartType.Area)
            .title("")
//            .titleStyle(AAStyle().color("#ffffff"))
            .subtitle("")
//            .subtitleStyle(AAStyle().color("#ffffff"))
            .colorsTheme(arrayOf("#03BBFF", "#ffffff", "#ffffff", "#ffffff"))
            .backgroundColor(Color.TRANSPARENT)
//            .axesTextColor("#ffffff")
            .dataLabelsEnabled(true)
            .tooltipValueSuffix("FL OZ")
            .categories(
                arrayOf(
                    "1",
                    "2",
                    "3",
                    "4",
                    "5",
                    "6",
                    "7",
                    "8",
                    "9",
                    "10",
                    "11",
                    "12",
                    "13",
                    "14",
                    "15",
                    "16",
                    "17",
                    "18",
                    "19",
                    "20",
                    "21",
                    "22",
                    "23",
                    "24",
                    "25",
                    "26",
                    "27",
                    "28",
                    "29",
                    "30",
                    "31"
                )
            )
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("fl oz")
                        .data(array.toTypedArray())
                )
            )
        aa_chart_view.aa_drawChartWithChartModel(aaChartModel)
    }
}