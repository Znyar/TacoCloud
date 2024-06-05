package com.znyar.tacos.web;

import com.znyar.tacos.entity.TacoOrder;
import com.znyar.tacos.security.CustomOAuth2UserService;
import com.znyar.tacos.security.User;
import org.springframework.stereotype.Component;

@Component
public class UserOrdersSetter {

    public TacoOrder setOrderUser(TacoOrder order, Object principal) {
        User user = null;

        if (principal instanceof CustomOAuth2UserService.CustomOAuth2User oAuth2User) {
            user = oAuth2User.getUser();
        } else if (principal instanceof User) {
            user = (User) principal;
        }

        if (user != null) {
            order.setUsername(user.getUsername());
            order.setDeliveryCity(user.getCity());
            order.setDeliveryState(user.getState());
            order.setDeliveryZip(user.getZip());
            order.setDeliveryStreet(user.getStreet());
        }
        return order;
    }

}
