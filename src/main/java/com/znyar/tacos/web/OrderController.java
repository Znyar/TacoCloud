package com.znyar.tacos.web;

import com.znyar.tacos.data.TacoRepository;
import com.znyar.tacos.entity.Taco;
import com.znyar.tacos.entity.TacoOrder;
import com.znyar.tacos.data.OrderRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@RequestMapping("/orders")
@SessionAttributes("tacoOrder")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final TacoRepository tacoRepository;
    private final UserOrdersSetter userOrdersSetter;

    @GetMapping("/current")
    public String orderForm(Model model, HttpSession session,
                            @AuthenticationPrincipal Object principal) {
        TacoOrder order = (TacoOrder) session.getAttribute("tacoOrder");
        if (order == null) {
            order = new TacoOrder();
            session.setAttribute("tacoOrder", order);
        }

        order = userOrdersSetter.setOrderUser(order, principal);

        model.addAttribute("tacoOrder", order);
        return "orderForm";
    }

    @PostMapping
    public String processOrder(@Valid TacoOrder order, Errors errors,
                               SessionStatus sessionStatus) {
        if (errors.hasErrors()) {
            return "orderForm";
        }

        tacoRepository.saveAll(order.getTacos());
        order.getTacosIds().addAll(order.getTacos().stream()
                .map(Taco::getId)
                .toList());
        orderRepository.save(order);
        sessionStatus.setComplete();

        return "redirect:/";
    }
}
