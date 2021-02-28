package tsturm18.pos.todo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Note> notes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadNotes();

        ListView listView = findViewById(R.id.taskList);
        registerForContextMenu(listView);
    }

    private void loadNotes(){
        File file = new File("notes.txt");
        if (!file.exists()){
            Toast.makeText(getApplicationContext(),"Noch keine Notizen!",Toast.LENGTH_LONG).show();
        }
        try {
            InputStream inputStream = openFileInput(file.getName());
            notes.clear();
            try(BufferedReader bufferedInputStream = new BufferedReader(new InputStreamReader(inputStream))){
                String s;
                while((s=bufferedInputStream.readLine())!=null){
                    String[] splits = s.split(";");
                    notes.add(new Note(splits[0],splits[1],splits[2]));
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ListView noteView = findViewById(R.id.taskList);
        noteView.setAdapter(new NoteAdapter(this,R.layout.note_layout,notes));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.saveAction:
                saveNotes();
                break;
            case R.id.newNote:
                addNewNote();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    private void addNewNote(){

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText text = new EditText(getApplicationContext());
        text.setText("Description");
        layout.addView(text);

        EditText date = new EditText(getApplicationContext());
        date.setText(LocalDate.now().format(dateFormatter));
        layout.addView(date);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Picker picker = new Picker();
                picker.pickDate(date,MainActivity.this);
            }
        });

        EditText time = new EditText(getApplicationContext());
        time.setText(LocalTime.now().format(timeFormatter));
        layout.addView(time);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Picker picker = new Picker();
                picker.pickTime(time,MainActivity.this);
            }
        });

        EditText details = new EditText(getApplicationContext());
        details.setText("Detail");
        layout.addView(details);

        AlertDialog.Builder message = new AlertDialog.Builder(MainActivity.this);
        message.setTitle("Add new Task")
                .setView(layout)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                            notes.add(new Note(text.getText().toString(),date.getText().toString() + " " + time.getText().toString(),details.getText().toString()));
                        ListView listView = findViewById(R.id.taskList);
                        listView.invalidateViews();
                    }
                }).setNegativeButton("Cancel",null);
        message.show();

    }

    private void saveNotes(){
        File file = new File("notes.txt");
        try {
            OutputStream outputStream = openFileOutput(file.getName(), Context.MODE_PRIVATE);
            PrintWriter outPrintWriter = new PrintWriter(new OutputStreamWriter(outputStream));
            for (Note note:notes) {
                outPrintWriter.println(note.getNote() + ";" + note.getDateTime() + ";" + note.getDetails());
            }
            outPrintWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        int viewId = v.getId();
        if (viewId == R.id.taskList) {
            getMenuInflater().inflate(R.menu.context_tasks, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.delete:
                deleteItem(info.position);
                break;
            case R.id.details:
                showDetails(info.position);
                break;
            case R.id.edit:
                editItem(info.position);
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void deleteItem(int position){
        notes.remove(position);
        ListView listView = findViewById(R.id.taskList);
        listView.invalidateViews();
    }

    public void showDetails(int position){
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView description = new TextView(getApplicationContext());
        //TODO Padding
        description.setText(notes.get(position).getDetails());
        layout.addView(description);

        AlertDialog.Builder message = new AlertDialog.Builder(MainActivity.this);
        message.setTitle("Details")
                .setView(layout)
                .setNegativeButton("Close",null);
        message.show();
    }

    public void editItem(int position){

        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText text = new EditText(getApplicationContext());
        text.setText(notes.get(position).getNote());
        layout.addView(text);

        EditText date = new EditText(getApplicationContext());
        date.setText(notes.get(position).getDateTime().split(" ")[0]);
        layout.addView(date);

        EditText time = new EditText(getApplicationContext());
        time.setText(notes.get(position).getDateTime().split(" ")[1]);
        layout.addView(time);

        EditText details = new EditText(getApplicationContext());
        details.setText(notes.get(position).getDetails());
        layout.addView(details);

        AlertDialog.Builder message = new AlertDialog.Builder(MainActivity.this);
        message.setTitle("Edit Task")
                .setView(layout)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        notes.get(position).setNote(text.getText().toString());
                        notes.get(position).setDateTime(date.getText().toString() + " " + time.getText().toString());
                        notes.get(position).setDetails(details.getText().toString());
                    }
                }).setNegativeButton("Cancel",null);
        message.show();

        ListView listView = findViewById(R.id.taskList);
        listView.invalidateViews();
    }
}
