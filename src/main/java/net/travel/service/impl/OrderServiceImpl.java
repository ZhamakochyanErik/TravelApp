package net.travel.service.impl;

import net.travel.model.UserOrder;
import net.travel.repository.HotelRoomRepository;
import net.travel.repository.UserOrderRepository;
import net.travel.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserOrderRepository orderRepository;
    @Autowired
    private HotelRoomRepository hotelRoomRepository;

    @Transactional
    public void add(UserOrder order) {
        orderRepository.save(order);
        hotelRoomRepository.save(order.getHotelRoom());
    }

    @Override
    public int orderCountByUserId(int userId) {
        return orderRepository.countByUser_id(userId);
    }

    @Override
    public Optional<UserOrder> getById(int id) {
        return orderRepository.findById(id);
    }

    @Override
    public void update(UserOrder userOrder) {
        orderRepository.save(userOrder);
    }
}
