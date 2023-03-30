package com.example.callslow.ui.conversation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.callslow.databinding.FragmentConversationBinding;

public class ConversationFragment extends Fragment {
    private FragmentConversationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentConversationBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        return rootView;
    }
}
