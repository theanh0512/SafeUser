package fit.ultimate.task1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    public static ArrayList<Task> arrayListTask;
    @BindView(R.id.recyclerview_task)
    RecyclerView recyclerViewTask;
    public static TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        taskAdapter = new TaskAdapter(this);
        recyclerViewTask.setAdapter(taskAdapter);
        recyclerViewTask.setHasFixedSize(true);
        recyclerViewTask.setLayoutManager(new LinearLayoutManager(this));
        arrayListTask = new ArrayList<>();
        GetDataTask dataTask = new GetDataTask(this);
        dataTask.execute();
    }
}
