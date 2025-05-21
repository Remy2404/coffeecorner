package com.coffeecorner.app.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.coffeecorner.app.repositories.UserRepository;
import com.coffeecorner.app.viewmodels.UserViewModel;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final UserRepository userRepository;

    public ViewModelFactory(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(userRepository);
        } else if (modelClass.isAssignableFrom(UserViewModel.class)) {
            // TODO: Add UserViewModel creation if needed, passing userRepository
             throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        } // Add other ViewModels here
        else {
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }
    }
} 