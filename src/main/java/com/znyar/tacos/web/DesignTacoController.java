package com.znyar.tacos.web;

import com.znyar.tacos.entity.Ingredient;
import com.znyar.tacos.entity.Taco;
import com.znyar.tacos.entity.TacoOrder;
import com.znyar.tacos.data.IngredientRepository;
import com.znyar.tacos.security.CustomOAuth2UserService;
import com.znyar.tacos.security.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.znyar.tacos.entity.Ingredient.*;

@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("tacoOrder")
@RequiredArgsConstructor
public class DesignTacoController {

    private final IngredientRepository ingredientRepository;
    private final UserOrdersSetter userOrdersSetter;

    @ModelAttribute
    public void addIngredientsToModel(Model model) {

        List<Ingredient> ingredients = (List<Ingredient>) ingredientRepository.findAll();

        Type[] types = Ingredient.Type.values();
        for (Type type : types) {
            model.addAttribute(type.toString().toLowerCase(), filterByType(ingredients, type));
        }

    }

    @ModelAttribute(name = "tacoOrder")
    public TacoOrder order(@AuthenticationPrincipal Object principal) {
        TacoOrder order  = new TacoOrder();
        return userOrdersSetter.setOrderUser(order, principal);
    }

    @ModelAttribute(name = "taco")
    public Taco taco() {
        return new Taco();
    }

    @GetMapping
    public String showDesignForm() {
        return "design";
    }

    @PostMapping
    public String processTaco(@Valid Taco taco, Errors errors,
                              @ModelAttribute("tacoOrder") TacoOrder tacoOrder) {
        if (errors.hasErrors()) {
            return "design";
        }
        tacoOrder.addTaco(taco);
        return "redirect:/orders/current";
    }

    private Iterable<Ingredient> filterByType(List<Ingredient> ingredients, Type type) {
        return ingredients.stream()
                .filter(ingredient -> ingredient.getType().equals(type))
                .collect(Collectors.toList());
    }

}
