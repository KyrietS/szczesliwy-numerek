package eu.legnica.iilo.numerki

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NotifyNumberWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)

    override fun doWork(): Result {
        val number1: Int
        val number2: Int
        val date: String
        val retrofit = Retrofit.Builder()
            .baseUrl("https://2lo.legnica.eu")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(Api::class.java)
        val call = api.getNumbers()
        try {
            val response = call.execute()
            if (response.body() != null && response.body()!!.days.isNotEmpty()) {
                val (date1, numbers) = response.body()!!.days[0]
                number1 = numbers[0]
                number2 = numbers[1]
                date = date1
            } else if (response.body()!!.days.isEmpty()) { // Prawdopodobnie jest weekend
                Log.d("omg", "Weekend")
                MainActivity.scheduleWork(applicationContext)
                return Result.success()
            } else {
                throw RuntimeException("Incorrect response from the server")
            }
        } catch (e: Exception) {
            Log.d("omg", "Worker error")
            return Result.retry()
        }
        sendNotificationIfExpected(number1, number2, date)
        // Ustaw od nowa worker na przyszły dzień
        MainActivity.scheduleWork(applicationContext)
        return Result.success()
    }

    private fun sendNotificationIfExpected(num1: Int, num2: Int, date: String) {
        val context = applicationContext
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val userType = preferences.getString(context.getString(R.string.user_type_key), "unknown")
        val numbers = preferences.getStringSet( context.getString(R.string.notify_numbers_key),emptySet()) ?: emptySet()
        if (userType == "teacher") { // Nauczyciel dostaje powiadomienie zawsze
            sendNotification(num1, num2, date, false)
        } else if(numbers.contains(num1.toString()) || numbers.contains(num2.toString())) {
            sendNotification(num1, num2, date, true)
        }
    }

    private fun sendNotification(num1: Int, num2: Int, date: String, userStudent: Boolean) {
        val title = if (userStudent) applicationContext.getString(R.string.your_number_has_drawn)
            else applicationContext.getString(R.string.numbers_have_drawn)
        val message = applicationContext.getString(R.string.notification_summary, num1, num2, date)
        val color = ContextCompat.getColor(applicationContext, R.color.colorLogo)

        val openAppIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            Intent(applicationContext, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(applicationContext, App.NUMBER_CHANNEL_PUSH_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setColor(color)
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(openAppIntent)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1, notification)
    }
}