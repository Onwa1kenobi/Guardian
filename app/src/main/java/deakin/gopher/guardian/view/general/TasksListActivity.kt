package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.TaskListAdapter
import deakin.gopher.guardian.model.Task
import deakin.gopher.guardian.services.NavigationService


class TasksListActivity : AppCompatActivity() {
    private var taskListAdapter: TaskListAdapter? = null
    private var query: Query? = null
    private var overviewCardview: CardView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_list);
        var navigationView : NavigationView  = findViewById(R.id.nav_view);
        var taskListMenuBtn : ImageView = findViewById(R.id.task_list_manu_button);
        var drawerLayout : DrawerLayout = findViewById(R.id.drawer_layout);

        navigationView.setItemIconTintList(null);
        taskListMenuBtn.setOnClickListener({
            drawerLayout.openDrawer(GravityCompat.START);
            navigationView.setNavigationItemSelectedListener { menuItem: MenuItem ->
                val id = menuItem.itemId
                if (R.id.nav_home === id) {
                    startActivity(
                        Intent(
                            this@TasksListActivity,
                            Homepage4caretaker::class.java
                        )
                    )
                } else if (R.id.nav_admin === id) {
                    startActivity(
                        Intent(
                            this@TasksListActivity,
                            Homepage4caretaker::class.java
                        )
                    )
                } else if (R.id.nav_settings === id) {
                    startActivity(
                        Intent(
                            this@TasksListActivity,
                            Setting::class.java
                        )
                    )
                } else if (R.id.add_task === id) {
                    startActivity(
                        Intent(
                            this@TasksListActivity,
                            TaskAddActivity::class.java
                        )
                    )
                }else if (R.id.nav_signout === id) {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this@TasksListActivity, LoginActivity::class.java))
                    finish()
                }
                true
            }
        });
        val taskListRecyclerView: RecyclerView = findViewById(R.id.task_list_recycleView)
        overviewCardview = findViewById(R.id.task_list_task_overview)
        val taskSearchView: SearchView = findViewById(R.id.task_list_searchView)

        val addTaskButton: Button = findViewById(R.id.add_task_button)
        addTaskButton.setOnClickListener {
            NavigationService(this).onLaunchTaskCreator()
        }

        query = FirebaseDatabase.getInstance().reference.child("tasks")

        taskListAdapter = TaskListAdapter(getTestData())
        taskListRecyclerView.layoutManager = GridLayoutManager(this@TasksListActivity, 1)
        taskListRecyclerView.adapter = taskListAdapter

        taskSearchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(s: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(s: String?): Boolean {
                    query =
                        if (s.isNullOrEmpty()) {
                            FirebaseDatabase.getInstance().reference.child("tasks")
                        } else {
                            FirebaseDatabase.getInstance()
                                .reference
                                .child("tasks")
                                .orderByChild("description")
                                .startAt(s)
                                .endAt(s + "\uf8ff")
                                .limitToFirst(10)
                        }

                    return true
                }
            },
        )
    }

    private fun getTestData(): List<Task> {
        return listOf(
            Task("1", "Task 1 Description", "Patient 1"),
            Task("2", "Task 2 Description", "Patient 2"),
        )
    }
}
