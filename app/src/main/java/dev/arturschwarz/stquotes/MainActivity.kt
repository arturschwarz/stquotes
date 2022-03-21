package dev.arturschwarz.stquotes

import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.io.InputStreamReader
import java.net.URL


class MainActivity : AppCompatActivity() {

    private lateinit var rvQuotes: RecyclerView
    private val quotesAdapter = QuotesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvQuotes = findViewById(R.id.rvQuotes)

        rvQuotes.adapter = quotesAdapter
        rvQuotes.layoutManager = LinearLayoutManager(this)

        GetQuotesTask(quotesAdapter).execute(URL("https://strangerthings-quotes.vercel.app/api/quotes/10"))
    }

    class GetQuotesTask(private val quotesAdapter: QuotesAdapter) : AsyncTask<URL, Int, List<Quote>>() {
        override fun doInBackground(vararg url: URL?): List<Quote> {
            val myConnection = url.first()?.openConnection()
            val inputStream = myConnection?.getInputStream()
            val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
            val jsonReader = JsonReader(inputStreamReader)

            return Gson().fromJson<Array<Quote>>(jsonReader, Array<Quote>::class.java).toList()
        }

        override fun onPostExecute(result: List<Quote>?) {
            quotesAdapter.submitList(result)
            super.onPostExecute(result)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_refresh -> {
            GetQuotesTask(quotesAdapter).execute(URL("https://strangerthings-quotes.vercel.app/api/quotes/10"))
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

}

class QuotesAdapter : ListAdapter<Quote, QuoteViewHolder>(QuoteItemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(
            viewType, parent, false
        )
        return QuoteViewHolder(root)
    }

    override fun getItemViewType(position: Int) = R.layout.view_quote_item

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object QuoteItemCallback : DiffUtil.ItemCallback<Quote>() {
        override fun areItemsTheSame(oldItem: Quote, newItem: Quote) =
            oldItem.toString() == newItem.toString()

        override fun areContentsTheSame(oldItem: Quote, newItem: Quote) = oldItem == newItem
    }

}

data class Quote(val quote: String, val author: String)

class QuoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(quote: Quote) {
        itemView.findViewById<TextView>(R.id.tvQuote).text = "\"${quote.quote}\""
        itemView.findViewById<TextView>(R.id.tvAuthor).text = "- ${quote.author}"
    }

}