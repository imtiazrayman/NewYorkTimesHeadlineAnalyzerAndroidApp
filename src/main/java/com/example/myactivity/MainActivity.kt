package com.example.myactivity

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.URI
import java.net.URL


var article = 0

//Imtiaz Rayman
class MainActivity : AppCompatActivity() {

    var sortquery = ""
    
    inner class myClass : AsyncTask<Void, Void, String>() {
        var query =
            "" // this variable will hold our query that the user enters and then append that query to our url which will then search the entire json of nyt
        var sort = ""
        var startday = ""
        var endday = ""

        override fun doInBackground(vararg params: Void?): String {
            var toReturn = ""
            sort = sortquery //sort works with newest, oldest, relevance

                val buildUri1 = Uri.parse("https://api.nytimes.com/svc/search/v2/articlesearch.json")
                .buildUpon()
                .appendQueryParameter("api-key", "Kiuz98UcSpY5h7gKLTnvwXXtA6pPkOQW")
                .appendQueryParameter("begin_date", "20200220")// begin date 2/20/2020
                .appendQueryParameter("end_date", "20200301") // end date 03/01/2020
                .appendQueryParameter("q", query)
                .appendQueryParameter("sort", sort)
                .build()

           // val buildUri1 = Uri.parse("https://www.reddit.com/r/funny/new.json?limit=25.json")


            //   toReturn = URL("https://api.nytimes.com/svc/search/v2/articlesearch.json?begin_date=20200220&end_date=20200229&q=application%2Fjson%2Fresponse%2Fdocs&api-key=Kiuz98UcSpY5h7gKLTnvwXXtA6pPkOQW").readText()
            // .appendPath("application/json/response/docs")
            // val buildUri = Uri.parse(toReturn).buildUpon().appendPath("data.response.docs").build()
            //var toReturn= ""

            toReturn = URL(buildUri1.toString()).readText()
            return toReturn
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            // this is the searching through the json to get what we needed which were the headlines
            // the way it worked was status/response/docs(which is an array)/headline/main
            // status/response/docs/headline/main is the path of the json.
            var myJson = JSONObject(result)

           // var response = myJson.getJSONObject("response")
            var response = myJson.getJSONObject("data")
            var docs = response.getJSONArray("docs")
            //status/response/docs/headline/main

            // maybe a while loop can be here

            if (article > 9) { // the json only gives 10 at a time so if i go over scope it may crash the app so reset to 0,
                // but another way to overcome this may be to recall the api again if we exceed the index to get new articles.
                article = 0
                var headline = docs.getJSONObject(article).getJSONObject("headline")
                    .getString("main") // this took a long time to figure out.
                 textView.text = headline.toString()
            } else {
                var headline = docs.getJSONObject(article).getJSONObject("headline")
                    .getString("main")
                textView.text = headline.toString()
                article++
            }

        }
    }

    //sentiment api async task
    inner class myClass2 : AsyncTask<Void, Void, String>() {
        var text = ""
        override fun doInBackground(vararg params: Void?): String {
            val buildUri1 = Uri.parse("https://api.meaningcloud.com/sentiment-2.1").buildUpon()
                .appendQueryParameter("key", "323e6aea1e4aa5d2c2cafa70ab037aa3")
                .appendQueryParameter("lang", "en")
                .appendQueryParameter("txt", text)
                .build()
            var toReturn = ""
            toReturn = URL(buildUri1.toString()).readText()
            return toReturn
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            findViewById<ProgressBar>(R.id.progressBar1).visibility = View.GONE
            var myJson = JSONObject(result)
            var score_tag = myJson.getString("score_tag")
            var irony: String = myJson.getString("irony")
            var confidence: String = myJson.getString("confidence")

            scoreTag.text = score_tag.toString()

            // textView.text.toString()
            if (scoreTag.text == "P+") {
                textView2.text =
                    "Strong Positive Statement" // additonal functionality different statements can correspond to different font colors.
                textView2.setTextColor(Color.BLUE)
            }
            if (scoreTag.text == "P") {
                textView2.text = "Positive Statement"
                textView2.setTextColor(Color.BLUE) //blues for positive
            }
            if (scoreTag.text == "NEU") {
                textView2.text = "Neutral Statement"
                textView2.setTextColor(Color.MAGENTA)
            }
            if (scoreTag.text == "N+") {
                textView2.text = "Strong Negative Statement"
                textView2.setTextColor(Color.RED) // reds for negative
            }
            if (scoreTag.text == "N") {
                textView2.text = "Negative Statement"
                textView2.setTextColor(Color.RED)
            }
            if (scoreTag.text == "NONE") {
                textView2.text = "Without Sentiment"
                textView2.setTextColor(Color.GREEN)
            }
            /*
               What the ratings mean:
               P+: strong positive
               P: positive
               NEU: neutral
               N: negative
               N+: strong negative
               NONE: without sentiment
             */
            ironyText.text = irony
            confidenceText.text = confidence
        }
    }

    fun buttonclicked(view: View) // this is the primary button which calls the two async functions, it also reads in the query written by the user.
    {
        var j = myClass()
        if (startDay.text.isNotEmpty()) {
            j.startday = startDay.text.toString()
        }
        if (endDay.text.isNotEmpty()) {
            j.endday = endDay.text.toString()
        }

        if (editText.text.isNotEmpty()) {
            //Search query entered
            j.query = editText.text.toString()
            findViewById<ProgressBar>(R.id.progressBar1).visibility = View.VISIBLE
            j.execute()
        } else {
            //no search query entered blank text field
            findViewById<ProgressBar>(R.id.progressBar1).visibility = View.VISIBLE
            j.execute()
        }
        //myClass().execute() this will work without any user input which will sentiment the nyt headlines which come from the docs array in the json provided.

        if (textView.text.isNotEmpty()) { // the text view hold the article that has been ran, which means that our first async has ran, so the second async can run meaning the textview is not empty.
             var i = myClass2()
             i.text = textView.text.toString()
             findViewById<ProgressBar>(R.id.progressBar1).visibility = View.VISIBLE
             i.execute()
         }
    }

    fun sortClicked(view: View) { // this on click takes care of the radio button selection on how the information is to be sorted
        if (rdoNewest.isChecked) {
            sortquery = "newest"
        } else if (rdoOldest.isChecked) {
            sortquery = "oldest"
        } else if (rdoRelevance.isChecked) {
            sortquery = "relevance"
        } else {
            sortquery = ""
        }
    }


}
