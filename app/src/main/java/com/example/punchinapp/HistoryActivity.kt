package com.example.punchinapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.punchinapp.data.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat

class HistoryActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var table : TableLayout
    private var auth : FirebaseUser? = null
    private lateinit var deleteButton : ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Status Bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.buttonEnabled)

        table = findViewById(R.id.historytableLayout)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        auth = FirebaseAuth.getInstance().currentUser
        deleteButton = findViewById(R.id.button_delete)


        //Reading the entries in the database
        userViewModel.readEntries(auth?.displayName.toString()).observe(this){
            while (table.childCount > 1) {
                table.removeView(table.getChildAt(table.childCount - 1))
            }

            for(i in it){

                val tr = TableRow(this)
                val tv0 = TextView(this)
                tv0.text = i.name
                tv0.setPadding(10,10,10,10)
                tr.addView(tv0)

                val tv1 = TextView(this)
                tv1.text = i.date
                tv1.setPadding(10,10,10,10)
                tr.addView(tv1)

                val tv2 = TextView(this)
                tv2.text = i.punchInTime
                tv2.setPadding(10,10,10,10)
                tr.addView(tv2)

                val tv3 = TextView(this)
                tv3.text = i.punchInLoc
                tv3.setPadding(10,10,10,10)
                tr.addView(tv3)

                val tv4 = TextView(this)
                tv4.text = i.punchOutTime
                tv4.setPadding(10,10,10,10)
                tr.addView(tv4)

                val tv5 = TextView(this)
                tv5.text = i.punchOutLoc
                tv5.setPadding(10,10,10,10)
                tr.addView(tv5)

                if(i.duration.toString()!="-") {

                    var duration = Integer.valueOf(i.duration.toString())

                    val seconds = duration % 60
                    duration /= 60

                    val minutes = duration
                    duration /= 60

                    val hours = duration

                    val tv6 = TextView(this)
                    tv6.text = "${hours} hours ${minutes} minutes ${seconds} seconds"
                    tv6.setPadding(10, 10, 10, 10)
                    tr.addView(tv6)
                }
                else{
                    val tv6 = TextView(this)
                    tv6.text = "-"
                    tv6.setPadding(10, 10, 10, 10)
                    tr.addView(tv6)
                }

                tr.setPadding(10,10,10,10)
                table.addView(tr)
            }


        }

        deleteButton.setOnClickListener{
            userViewModel.deleteEntries()
        }

    }
}