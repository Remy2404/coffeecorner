package com.coffeecorner.app.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.coffeecorner.app.repositories.UserRepository;
import com.coffeecorner.app.repositories.ProductRepository;
import com.coffeecorner.app.repositories.CartRepository;
import com.coffeecorner.app.repositories.OrderRepository;
import com.coffeecorner.app.repositories.MenuRepository;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final UserRepository userRepository;
    private ProductRepository productRepository;
    private CartRepository cartRepository;
    private OrderRepository orderRepository;
    private MenuRepository menuRepository;

    // Constructor for user-related ViewModels
    public ViewModelFactory(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Constructor for all repositories
    public ViewModelFactory(UserRepository userRepository, ProductRepository productRepository,
            CartRepository cartRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
    }

    // Constructor for product-related ViewModels
    public ViewModelFactory(ProductRepository productRepository) {
        this.userRepository = null;
        this.productRepository = productRepository;
    }

    // Constructor for cart-related ViewModels
    public ViewModelFactory(CartRepository cartRepository) {
        this.userRepository = null;
        this.cartRepository = cartRepository;
    } // Constructor for order-related ViewModels

    public ViewModelFactory(OrderRepository orderRepository) {
        this.userRepository = null;
        this.orderRepository = orderRepository;
    }

    // Constructor for menu-related ViewModels
    public ViewModelFactory(MenuRepository menuRepository) {
        this.userRepository = null;
        this.menuRepository = menuRepository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(userRepository);
        } else if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(userRepository);
        } else if (modelClass.isAssignableFrom(ProductViewModel.class)) {
            if (productRepository == null) {
                throw new IllegalStateException("ProductRepository is required for ProductViewModel");
            }
            return (T) new ProductViewModel(productRepository);
        } else if (modelClass.isAssignableFrom(CartViewModel.class)) {
            if (cartRepository == null) {
                throw new IllegalStateException("CartRepository is required for CartViewModel");
            }
            return (T) new CartViewModel(cartRepository);
        } else if (modelClass.isAssignableFrom(OrderViewModel.class)) {
            if (orderRepository == null) {
                throw new IllegalStateException("OrderRepository is required for OrderViewModel");
            }
            return (T) new OrderViewModel(orderRepository);
        } else if (modelClass.isAssignableFrom(MenuViewModel.class)) {
            if (menuRepository == null) {
                throw new IllegalStateException("MenuRepository is required for MenuViewModel");
            }
            return (T) new MenuViewModel(menuRepository);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }
    }
}