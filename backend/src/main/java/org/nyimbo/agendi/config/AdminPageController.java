package org.nyimbo.agendi.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller @RequestMapping("/admin")
public class AdminPageController {
    @GetMapping public RedirectView admin() { return new RedirectView("/admin/index.html"); }
}
