package tsturm18.pos.todo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class NoteAdapter extends BaseAdapter {

    private final int listViewItemLayoutId;
    private final List<Note> notes;
    private final LayoutInflater inflater;

    public NoteAdapter(Context context, int listViewItemLayoutId, List<Note> games) {
        this.listViewItemLayoutId = listViewItemLayoutId;
        this.notes = games;

        inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Object getItem(int position) {
        return notes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = (convertView==null) ? inflater.inflate(this.listViewItemLayoutId,null) : convertView;

        TextView dateTime = view.findViewById(R.id.dateTime);
        dateTime.setText(notes.get(position).getDateTime());

        TextView text = view.findViewById(R.id.description);
        text.setText(notes.get(position).getNote());

        if(notes.get(position).getIsOver()){
            view.setBackgroundColor(Color.RED);
        }

        return view;
    }
}
