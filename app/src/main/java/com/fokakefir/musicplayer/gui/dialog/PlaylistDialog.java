package com.fokakefir.musicplayer.gui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.fokakefir.musicplayer.R;

public class PlaylistDialog extends AppCompatDialogFragment implements AdapterView.OnItemSelectedListener {

    private EditText txtPlaylist;
    private Spinner spinner;

    private String strColor;

    private OnPlaylistDialogListener listener;

    public PlaylistDialog(OnPlaylistDialogListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_playlist, null);

        builder.setView(view)
                .setTitle("Add new playlist")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = txtPlaylist.getText().toString().trim();
                        listener.onPlaylistCreate(name, strColor);
                    }
                });

        this.txtPlaylist = view.findViewById(R.id.txt_new_playlist);
        this.spinner = view.findViewById(R.id.spinner_colors);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.colors, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner.setAdapter(adapter);
        this.spinner.setOnItemSelectedListener(this);

        return builder.create();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        this.strColor = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public interface OnPlaylistDialogListener {
        void onPlaylistCreate(String name, String color);
    }

}
