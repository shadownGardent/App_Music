package com.example.app_music.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_music.R;
import com.example.app_music.model.adapter.SongAdapter;
import com.example.app_music.controller.SearchController;
import com.example.app_music.controller.SearchControllerImp;
import com.example.app_music.controller.SortController;
import com.example.app_music.controller.SortControllerImp;
import com.example.app_music.model.Song;
import com.example.app_music.viewmodel.ListSongViewModel;
import com.example.app_music.viewmodel.SongItemViewModel;

import java.util.List;

public class ListSongsFragment extends Fragment {
    private RecyclerView recyclerSongs;
    private SongItemViewModel viewModel;
    private SongAdapter adapter;
    private Toolbar toolbar;
    private final SortController sortController;
    private final SearchController searchController;
    private int sortNameCounter;

    public ListSongsFragment() {
        sortController = new SortControllerImp();
        searchController = new SearchControllerImp();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SongItemViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.fragment_list_song, container, false);
            addRecycler(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolBar(view);
    }

    private void setUpToolBar(View view) {
        toolbar = view.findViewById(R.id.toolbar_song);
        toolbar.inflateMenu(R.menu.song_menu);
        toolbar.setTitle(getString(R.string.fragment_name));
        setHasOptionsMenu(true);
        toolbar.setOnMenuItemClickListener(this::menuItemClick);
        searchByName();
    }

    @SuppressLint("NonConstantResourceId")
    private boolean menuItemClick(MenuItem menuItem) {
        List<Song> songs = ListSongViewModel.getInstance().getSongs().getValue();
        switch (menuItem.getItemId()) {
            case R.id.action_sort_name:
                sortByName(songs);
                return true;
            case R.id.action_more_vert:
                Toast.makeText(requireContext(),
                        "Chức năng này hiện chưa khả dụng", Toast.LENGTH_SHORT).show();
            default:
                return false;
        }
    }

    private void sortByName(List<Song> songs) {
        sortNameCounter++;
        if (sortNameCounter % 2 == 1) {
            sortController.sortByNameASC(songs);
        }else {
            sortController.sortByNameDESC(songs);
        }
        adapter.setSongs(songs);
    }

    private void searchByName() {
        List<Song> songs = ListSongViewModel.getInstance().getSongs().getValue();
        SearchView searchView = (SearchView) toolbar
                .getMenu().findItem(R.id.action_search).getActionView();
        searchView.setQueryHint(getString(R.string.text_search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Song> result = searchController.searchByName(songs, newText);
                adapter.setSongs(result);
                return false;
            }
        });
    }

    private void addRecycler(View view) {
        recyclerSongs = view.findViewById(R.id.recycler_view);
        adapter = new SongAdapter(this::setSelectedSong);
        ListSongViewModel model = ListSongViewModel.getInstance();
        model.getSongs().observe(getViewLifecycleOwner(), adapter::setSongs);
        model.loadData();
        recyclerSongs.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerSongs.setAdapter(adapter);
        recyclerSongs.setHasFixedSize(true);
    }

    private void setSelectedSong(Song song) {
        viewModel.setSelectedSong(song);
    }
}
