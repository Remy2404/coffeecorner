package com.coffeecorner.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.coffeecorner.app.models.Order;
import com.coffeecorner.app.network.ApiResponse;
import com.coffeecorner.app.repositories.OrderRepository;

import java.util.List;
import java.util.Map;

/**
 * OrderViewModel - Manages order-related operations and data
 * Handles placing new orders, tracking orders, and viewing order history
 */
public class OrderViewModel extends ViewModel {

    private final OrderRepository orderRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<ApiResponse<Order>> orderPlaceResult = new MutableLiveData<>();

    public OrderViewModel(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
        isLoading.setValue(false);
    }

    /**
     * Place a new order
     * 
     * @param paymentMethod   Payment method used
     * @param deliveryAddress Delivery address
     * @param additionalInfo  Additional order information
     */
    public void placeOrder(String paymentMethod, String deliveryAddress, Map<String, Object> additionalInfo) {
        isLoading.setValue(true);
        orderRepository.placeOrder(paymentMethod, deliveryAddress, additionalInfo, new OrderRepository.OrderCallback() {
            @Override
            public void onOrderCreated(Order order) {
                isLoading.postValue(false);
                orderPlaceResult.postValue(new ApiResponse<>(true, "Order placed successfully", order));
            }

            @Override
            public void onOrderLoaded(Order order) {
                // Not used for placeOrder
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                orderPlaceResult.postValue(new ApiResponse<>(false, message, null));
            }
        });
    }

    /**
     * Get user's order history
     * 
     * @return LiveData containing list of user's orders
     */
    public LiveData<List<Order>> getOrderHistory() {
        isLoading.setValue(true);
        return orderRepository.getOrderHistory(new OrderRepository.OrderHistoryCallback() {
            @Override
            public void onComplete() {
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                // Error handling can be added here if needed
            }
        });
    }

    /**
     * Get order details by ID
     * 
     * @param orderId Order ID
     * @return LiveData containing order details
     */
    public LiveData<Order> getOrderById(String orderId) {
        isLoading.setValue(true);
        return orderRepository.getOrderById(orderId, new OrderRepository.OrderDetailCallback() {
            @Override
            public void onComplete() {
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                // Error handling can be added here if needed
            }
        });
    }

    /**
     * Track order status
     * 
     * @param orderId Order ID
     * @return LiveData containing current order status
     */
    public LiveData<String> trackOrderStatus(String orderId) {
        isLoading.setValue(true);
        return orderRepository.trackOrderStatus(orderId, new OrderRepository.OrderTrackingCallback() {
            @Override
            public void onComplete() {
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                // Error handling can be added here if needed
            }
        });
    }

    /**
     * Cancel an order
     * 
     * @param orderId Order ID
     */
    public void cancelOrder(String orderId) {
        isLoading.setValue(true);
        orderRepository.cancelOrder(orderId, new OrderRepository.OrderOperationCallback() {
            @Override
            public void onSuccess(String message) {
                isLoading.postValue(false);
                // Success handling can be added here if needed
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                // Error handling can be added here if needed
            }
        });
    }

    /**
     * Get loading state
     * 
     * @return LiveData indicating if an operation is in progress
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * Get order placement result
     * 
     * @return LiveData containing the result of order placement
     */
    public LiveData<ApiResponse<Order>> getOrderPlaceResult() {
        return orderPlaceResult;
    }
}
