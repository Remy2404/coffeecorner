package com.coffeecorner.app.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.Transformations;

import com.coffeecorner.app.models.MenuItem;
import com.coffeecorner.app.repositories.MenuRepository;
import com.coffeecorner.app.network.ApiService;
import com.coffeecorner.app.network.RetrofitClient;

import java.util.List;
import java.util.stream.Collectors;

public class MenuViewModel extends ViewModel {

    private MenuRepository menuRepository;
    private LiveData<List<MenuItem>> allMenuItems;
    private MutableLiveData<String> selectedCategory = new MutableLiveData<>();
    private LiveData<List<MenuItem>> filteredMenuItems;

    public MenuViewModel() {
        // TODO: Initialize ApiService properly (e.g., using RetrofitClient)
        ApiService apiService = RetrofitClient.getApiService(); // Assuming RetrofitClient.getApiService() exists
        menuRepository = new MenuRepository(apiService);

        allMenuItems = menuRepository.getMenuItems();

        // Filter menu items based on selected category
        filteredMenuItems = Transformations.switchMap(selectedCategory, category -> {
            if (category == null || category.isEmpty() || category.equals("All")) {
                return allMenuItems;
            } else {
                MutableLiveData<List<MenuItem>> filteredList = new MutableLiveData<>();
                // TODO: Implement actual filtering logic (maybe in repository or here)
                if (allMenuItems.getValue() != null) {
                    List<MenuItem> items = allMenuItems.getValue().stream()
                            .filter(item -> item.getCategory().equals(category))
                            .collect(Collectors.toList());
                    filteredList.setValue(items);
                }
                return filteredList;
            }
        });
    }

    public LiveData<List<MenuItem>> getFilteredMenuItems() {
        return filteredMenuItems;
    }

    public void selectCategory(String category) {
        selectedCategory.setValue(category);
    }

    public LiveData<List<MenuItem>> getMenuItems() {
        // TODO: Implement logic to fetch menu items (e.g., from a repository)
        // For now, return an empty list
        return allMenuItems; // Return all items by default
    }

    public LiveData<List<String>> getCategories() {
        return menuRepository.getCategories();
    }

    // TODO: Consider adding methods to expose loading state or errors from the repository
} 