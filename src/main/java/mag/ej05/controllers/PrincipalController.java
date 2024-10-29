package mag.ej05.controllers;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PrincipalController {

    @GetMapping("/")
    public String showHome(Model model) {
        model.addAttribute("year", LocalDate.now().getYear());

        return "indexView";
    }

    @GetMapping("/quienesSomos")
    public String showQuienesSomos(Model model) {
        model.addAttribute("year", LocalDate.now().getYear());

        return "quienesSomosView";
    }

}
