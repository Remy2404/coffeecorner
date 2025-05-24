package com.coffeecorner.app.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.coffeecorner.app.models.CartItem;
import com.coffeecorner.app.models.Order;
import com.coffeecorner.app.network.ApiCallback;
import com.coffeecorner.app.network.ApiResponse;
import com.coffeecorner.app.repositories.OrderRepository;
import com.coffeecorner.app.utils.PreferencesHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * OrderViewModel - Manages and provides order data to the UI
 * Handles order creation, tracking, and history
 */
public class OrderViewModel extends AndroidViewModel {

    private static final String TAG = "OrderViewModel";

    private final OrderRepository orderRepository;
    private final PreferencesHelper preferencesHelper;

    private final MutableLiveData<List<Order>> activeOrders = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Order>> completedOrders = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Order>> cancelledOrders = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Order> currentOrder = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    public OrderViewModel(@NonNull Application application) {
        super(application);
        orderRepository = OrderRepository.getInstance(application);
        preferencesHelper = new PreferencesHelper(application.getApplicationContext());
        loadOrders();
    }

    public LiveData<List<Order>> getActiveOrders() {
        return activeOrders;
    }

    public LiveData<List<Order>> getCompletedOrders() {
        return completedOrders;
    }

    public LiveData<List<Order>> getCancelledOrders() {
        return cancelledOrders;
    }

    public LiveData<Order> getCurrentOrder() {
        return currentOrder;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public void loadOrders() {
        String userId = preferencesHelper.getUserId();
        if (userId == null || userId.isEmpty()) {
            errorMessage.setValue("User not logged in. Please login to view orders.");
            Log.w(TAG, "loadOrders: User not logged in.");
            return;
        }

        isLoading.setValue(true);
        Log.d(TAG, "loadOrders: Fetching orders for userId: " + userId);

        orderRepository.getUserOrders(userId, new OrderRepository.OrdersCallback() {
            @Override
            public void onOrdersLoaded(List<Order> orders) {
                isLoading.setValue(false);
                if (orders != null) {
                    Log.d(TAG, "loadOrders onOrdersLoaded: Received " + orders.size() + " orders.");
                    List<Order> active = new ArrayList<>();
                    List<Order> completed = new ArrayList<>();
                    List<Order> cancelled = new ArrayList<>();
                    for (Order order : orders) {
                        if (order.getStatus() == null) {
                            Log.w(TAG, "Order with ID " + order.getId() + " has null status. Skipping.");
                            continue;
                        }
                        switch (order.getStatus()) {
                            case Order.STATUS_PENDING:
                            case Order.STATUS_CONFIRMED:
                            case Order.STATUS_PREPARING:
                            case Order.STATUS_DELIVERING:
                                active.add(order);
                                break;
                            case Order.STATUS_DELIVERED:
                            case Order.STATUS_COMPLETED:
                                completed.add(order);
                                break;
                            case Order.STATUS_CANCELLED:
                            case Order.STATUS_REFUNDED:
                                cancelled.add(order);
                                break;
                            default:
                                Log.w(TAG, "Order with ID " + order.getId() + " has unhandled status: "
                                        + order.getStatus() + ". Adding to active list for now.");
                                active.add(order);
                                break;
                        }
                    }
                    activeOrders.setValue(active);
                    completedOrders.setValue(completed);
                    cancelledOrders.setValue(cancelled);
                    Log.d(TAG, "loadOrders: Filtered into Active=" + active.size() + ", Completed=" + completed.size()
                            + ", Cancelled=" + cancelled.size());
                } else {
                    Log.e(TAG, "loadOrders onOrdersLoaded: Received null orders list.");
                    activeOrders.setValue(new ArrayList<>());
                    completedOrders.setValue(new ArrayList<>());
                    cancelledOrders.setValue(new ArrayList<>());
                    errorMessage.setValue("Failed to load orders. Please try again.");
                }
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue("Error loading orders: " + error);
                Log.e(TAG, "loadOrders onError: " + error);
            }
        });
    }

    public void createOrder(List<CartItem> cartItems, double total, String deliveryAddress, String paymentMethod,
            String paymentId, String notes) {
        String userId = preferencesHelper.getUserId();
        if (userId == null || userId.isEmpty()) {
            errorMessage.setValue("User not logged in. Cannot create order.");
            Log.w(TAG, "createOrder: User not logged in.");
            return;
        }

        if (cartItems == null || cartItems.isEmpty()) {
            errorMessage.setValue("Cart is empty. Cannot create order.");
            Log.w(TAG, "createOrder: Cart is empty.");
            return;
        }

        isLoading.setValue(true);
        Log.d(TAG, "createOrder: Attempting to create order for userId: " + userId);

        // Build the necessary parameters for the repository method
        // The repository expects List<CartItem>, double, String, String, OrderCallback
        orderRepository.createOrder(cartItems, total, deliveryAddress, paymentMethod,
                new OrderRepository.OrderCallback() {
                    @Override
                    public void onOrderCreated(Order newOrder) {
                        isLoading.setValue(false);
                        if (newOrder != null) {
                            currentOrder.setValue(newOrder);
                            successMessage.setValue("Order created successfully! Order ID: " + newOrder.getId());
                            Log.d(TAG, "createOrder onOrderCreated: Order created with ID: " + newOrder.getId());
                            // Refresh order lists
                            loadOrders();
                        } else {
                            errorMessage.setValue("Failed to create order. Please try again.");
                            Log.e(TAG, "createOrder onOrderCreated: newOrder is null");
                        }
                    }

                    @Override
                    public void onOrderLoaded(Order order) {
                        // Not used for creation
                    }

                    @Override
                    public void onError(String error) {
                        isLoading.setValue(false);
                        errorMessage.setValue("Error creating order: " + error);
                        Log.e(TAG, "createOrder onError: " + error);
                    }
                });
    }

    public void trackOrder(String orderId) {
        if (orderId == null || orderId.isEmpty()) {
            errorMessage.setValue("Invalid Order ID for tracking.");
            Log.w(TAG, "trackOrder: Invalid Order ID.");
            return;
        }
        isLoading.setValue(true);
        Log.d(TAG, "trackOrder: Fetching details for orderId: " + orderId);

        orderRepository.getOrderById(orderId, new OrderRepository.OrderDetailCallback() {
            @Override
            public void onComplete() {
                isLoading.setValue(false);
                // Optionally update UI or LiveData here
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue("Error tracking order: " + error);
                Log.e(TAG, "trackOrder onError: " + error);
            }
        });
    }

    public void cancelOrder(String orderId) {
        if (orderId == null || orderId.isEmpty()) {
            errorMessage.setValue("Invalid Order ID for cancellation.");
            Log.w(TAG, "cancelOrder: Invalid Order ID.");
            return;
        }
        isLoading.setValue(true);
        Log.d(TAG, "cancelOrder: Attempting to cancel orderId: " + orderId);

        orderRepository.cancelOrder(orderId, new OrderRepository.OrderOperationCallback() {
            @Override
            public void onSuccess(String message) {
                isLoading.setValue(false);
                successMessage.setValue(message != null ? message : "Order cancelled successfully.");
                loadOrders();
                if (currentOrder.getValue() != null && currentOrder.getValue().getId().equals(orderId)) {
                    currentOrder.setValue(null);
                }
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue("Error cancelling order: " + error);
                Log.e(TAG, "cancelOrder onError: " + error);
            }
        });
    }

    // Helper method to find an order by ID from the existing lists (can be removed
    // if not needed)
    private Order findOrderById(String orderId) {
        if (orderId == null)
            return null;

        List<Order> allOrders = new ArrayList<>();
        if (activeOrders.getValue() != null)
            allOrders.addAll(activeOrders.getValue());
        if (completedOrders.getValue() != null)
            allOrders.addAll(completedOrders.getValue());
        if (cancelledOrders.getValue() != null)
            allOrders.addAll(cancelledOrders.getValue());

        for (Order order : allOrders) {
            if (orderId.equals(order.getId())) {
                return order;
            }
        }
        return null;
    }

    /**
     * Refresh orders from the repository
     * This method is called when the user returns to the order history screen
     */
    public void refreshOrders() {
        // Simply call loadOrders to refresh the data
        loadOrders();
    }

    // Call this method when the ViewModel is no longer used and will be destroyed.
    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "onCleared: OrderViewModel is cleared");
        // Release resources here if necessary, e.g., unregister listeners
    }
}
