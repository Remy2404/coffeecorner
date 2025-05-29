package com.coffeecorner.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.coffeecorner.app.repositories.UserRepository;
import com.coffeecorner.app.network.ApiResponse;
import com.coffeecorner.app.models.User;

public class LoginViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<ApiResponse<User>> _loginResult = new MutableLiveData<>();

    public LiveData<ApiResponse<User>> getLoginResult() {
        return _loginResult;
    }

    public LoginViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void login(String email, String password) {
        userRepository.login(email, password, new UserRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                _loginResult.postValue(new ApiResponse<>(true, "Login successful", user));
            }

            @Override
            public void onError(String errorMessage) {
                _loginResult.postValue(new ApiResponse<>(false, errorMessage, null));
            }
        });
    }

    public void authenticateWithFirebase(String firebaseToken) {
        userRepository.authenticateWithFirebase(firebaseToken, new UserRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                _loginResult.postValue(new ApiResponse<>(true, "Authentication successful", user));
            }

            @Override
            public void onError(String errorMessage) {
                _loginResult.postValue(new ApiResponse<>(false, errorMessage, null));
            }
        });
    }
}