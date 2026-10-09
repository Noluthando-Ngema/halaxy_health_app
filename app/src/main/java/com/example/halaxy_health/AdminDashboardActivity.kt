package com.example.halaxy_health

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class AdminDashboardActivity : AppCompatActivity() {

    data class ApprovalItem(
        val id: Int,
        val name: String,
        val email: String,
        val details: String,
        val isDoctor: Boolean
    )

    private val db by lazy { AppDatabase.getDatabase(this) }
    private var showingDoctors = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val btnTabDoctors = findViewById<Button>(R.id.btnTabDoctors)
        val btnTabPatients = findViewById<Button>(R.id.btnTabPatients)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        btnTabDoctors.setOnClickListener {
            showingDoctors = true
            btnTabDoctors.setBackgroundResource(R.drawable.bg_tab_selected)
            btnTabPatients.setBackgroundResource(R.drawable.bg_tab_unselected)
            loadData()
        }

        btnTabPatients.setOnClickListener {
            showingDoctors = false
            btnTabPatients.setBackgroundResource(R.drawable.bg_tab_selected)
            btnTabDoctors.setBackgroundResource(R.drawable.bg_tab_unselected)
            loadData()
        }

        btnLogout.setOnClickListener {
            getSharedPreferences("halaxy_session", MODE_PRIVATE).edit().clear().apply()
            startActivity(Intent(this, AdminLoginActivity::class.java))
            finish()
        }

        loadData()
    }

    private fun loadData() {
        val recycler = findViewById<RecyclerView>(R.id.recAdminList)
        val txtEmpty = findViewById<TextView>(R.id.txtEmpty)
        recycler.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val items = mutableListOf<ApprovalItem>()
            if (showingDoctors) {
                val pendingDocs = db.doctorDao().getPendingDoctors()
                for (doc in pendingDocs) {
                    items.add(
                        ApprovalItem(
                            id = doc.id,
                            name = if (doc.fullName.isNotEmpty()) doc.fullName else "Dr. ${doc.name} ${doc.surname}",
                            email = doc.email,
                            details = "Specialization: ${if (doc.specialization.isNotEmpty()) doc.specialization else doc.specialty}",
                            isDoctor = true
                        )
                    )
                }
            } else {
                val pendingUsers = db.userDao().getPendingUsers()
                for (u in pendingUsers) {
                    items.add(
                        ApprovalItem(
                            id = u.id,
                            name = "${u.name} ${u.surname}",
                            email = u.email,
                            details = "Patient Account Request",
                            isDoctor = false
                        )
                    )
                }
            }

            if (items.isEmpty()) {
                txtEmpty.visibility = View.VISIBLE
                recycler.visibility = View.GONE
            } else {
                txtEmpty.visibility = View.GONE
                recycler.visibility = View.VISIBLE
                recycler.adapter = AdminAdapter(items,
                    onApprove = { item ->
                        lifecycleScope.launch {
                            if (item.isDoctor) {
                                db.doctorDao().updateApproval(item.id, true, "APPROVED")
                            } else {
                                db.userDao().updateApproval(item.id, true, "APPROVED")
                            }
                            Toast.makeText(this@AdminDashboardActivity, "Approved ${item.name}", Toast.LENGTH_SHORT).show()
                            loadData()
                        }
                    },
                    onDecline = { item ->
                        lifecycleScope.launch {
                            if (item.isDoctor) {
                                db.doctorDao().updateApproval(item.id, false, "DECLINED")
                            } else {
                                db.userDao().updateApproval(item.id, false, "DECLINED")
                            }
                            Toast.makeText(this@AdminDashboardActivity, "Declined ${item.name}", Toast.LENGTH_SHORT).show()
                            loadData()
                        }
                    }
                )
            }
        }
    }

    private class AdminAdapter(
        private val list: List<ApprovalItem>,
        private val onApprove: (ApprovalItem) -> Unit,
        private val onDecline: (ApprovalItem) -> Unit
    ) : RecyclerView.Adapter<AdminAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val txtName: TextView = view.findViewById(R.id.txtName)
            val txtEmail: TextView = view.findViewById(R.id.txtEmail)
            val txtDetails: TextView = view.findViewById(R.id.txtDetails)
            val btnApprove: Button = view.findViewById(R.id.btnApprove)
            val btnDecline: Button = view.findViewById(R.id.btnDecline)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_admin_approval, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list[position]
            holder.txtName.text = item.name
            holder.txtEmail.text = item.email
            holder.txtDetails.text = item.details

            holder.btnApprove.setOnClickListener { onApprove(item) }
            holder.btnDecline.setOnClickListener { onDecline(item) }
        }
    }
}
