package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class GridRVAdapter(private val letterList: Array<GridViewModal>,
                    private val context: Context
) : BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var letterTV: TextView

    override fun getCount(): Int {
        return letterList.size
    }

    override fun getItem(p0: Int): Any? {
        return null
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var grid = convertView
        // on blow line we are checking if layout inflater
        // is null, if it is null we are initializing it.
        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        // on the below line we are checking if convert view is null.
        // If it is null we are initializing it.
        if (convertView == null) {
            // on below line we are passing the layout file
            // which we have to inflate for each item of grid view.
            grid = layoutInflater!!.inflate(R.layout.gridview_item, null)
        }
        // on below line we are initializing our course image view
        // and course text view with their ids.
        letterTV = grid!!.findViewById(R.id.textView)
        // on below line we are setting image for our course image view.
        // on below line we are setting text in our course text view.
        letterTV.text = letterList[position].letter

        if(letterList[position].status == Status.CORRECT){
            grid.setBackgroundResource(R.color.green)
        } else if(letterList[position].status == Status.WRONGPOSITION) {
            grid.setBackgroundResource(R.color.yellow)
        } else if(letterList[position].status == Status.INCORRECT) {
            grid.setBackgroundResource(R.color.dark_gray)
        } else {
            grid.setBackgroundResource(R.color.white)
        }
        // at last we are returning our convert view.
        return grid
    }
}