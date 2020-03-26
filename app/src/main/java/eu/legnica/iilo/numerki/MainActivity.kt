package eu.legnica.iilo.numerki

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import androidx.preference.PreferenceManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.work.*
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    lateinit var refreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        refreshLayout = findViewById(R.id.swipe_refresh)
        refreshLayout.isRefreshing = true
        refreshLayout.setOnRefreshListener(this)

        TooltipCompat.setTooltipText(settings_button, getString(R.string.app_settings))
        TooltipCompat.setTooltipText(help_button, getString(R.string.help))

        loadData()
    }

    override fun onResume() {
        super.onResume()
        scheduleWork(this)
    }

    private fun loadData() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://2lo.legnica.eu")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(Api::class.java)
        val call = api.getNumbers()
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>,response: Response<ApiResponse>) {
                if (response.body() != null && response.body()!!.days.isNotEmpty()) {
                    val (date1, numbers) = response.body()!!.days[0]
                    val date = findViewById<TextView>(R.id.date)
                    val number1 = findViewById<TextView>(R.id.number1)
                    val number2 = findViewById<TextView>(R.id.number2)
                    date.text = date1
                    number1.text = numbers[0].toString()
                    number2.text = numbers[1].toString()
                } else if (response.body()!!.days.isEmpty()) { // weekend
                    val date = findViewById<TextView>(R.id.date)
                    date.text = getString(R.string.weekend)
                } else {
                    onFailure(call, null)
                }
                refreshLayout.isRefreshing = false
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable? ) {
                refreshLayout.isRefreshing = false
                Toast.makeText(this@MainActivity, getString(R.string.cannot_fetch_numbers), Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onRefresh() {
        loadData()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onSettingsClick(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("InflateParams")
    @Suppress("UNUSED_PARAMETER")
    fun onHelpClick(view: View) {
        val aboutView = layoutInflater.inflate(R.layout.help, null, false)
        val builder = AlertDialog.Builder(this)
        builder.setIcon(R.drawable.ic_help_24px)
        builder.setTitle(getString(R.string.help_title))
        builder.setView(aboutView)
        builder.setPositiveButton(getString(R.string.ok)) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        builder.create()
        builder.show()
    }

    companion object {
        fun scheduleWork(context: Context) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val notify = preferences.getBoolean(context.getString(R.string.notify_key), false)
            val seconds = preferences.getInt(context.getString(R.string.check_time_key), -1)
            Log.d("omg", notify.toString())
            if (notify && seconds >= 0) {
                val currentDate = Calendar.getInstance()
                val dueDate = Calendar.getInstance()
                dueDate[Calendar.HOUR_OF_DAY] = seconds / 60
                dueDate[Calendar.MINUTE] = seconds % 60
                dueDate[Calendar.SECOND] = 0

                // Muszę być co najmniej 30 sekund przed, aby powiadomienie zostało wysłane
                currentDate.add(Calendar.SECOND, 30)
                if (dueDate.before(currentDate)) {
                    dueDate.add(Calendar.HOUR_OF_DAY, 24)
                }
                currentDate.add(Calendar.SECOND, -30)

                val delay = dueDate.timeInMillis - currentDate.timeInMillis
                Log.d("omg", delay.toString())

                // Wymagane jest połączenie z Internetem
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
                val workRequest = OneTimeWorkRequest.Builder(NotifyNumberWorker::class.java)
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setConstraints(constraints)
                    .build()
                WorkManager.getInstance(context).enqueueUniqueWork(
                    "number_notification",
                    ExistingWorkPolicy.REPLACE,
                    workRequest
                )
            } else { // Wyłączenie powiadomień
                WorkManager.getInstance(context).cancelUniqueWork("number_notification")
            }
        }
    }

}
