package co.vinod.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    /**
     * Handles requests to the root path and redirects to the static index.html page
     * @return the name of the view to render
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/index.html";
    }
} 